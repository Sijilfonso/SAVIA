const express = require('express');
const bcrypt = require('bcryptjs');
const { dbAll, dbGet, dbRun } = require('../db');
const { verifyToken, requireRole } = require('../middleware/auth');
const { validate, storeRegistrationSchema } = require('../middleware/validate');

const router = express.Router();

const SALT_ROUNDS = 10;

/**
 * GET /api/stores
 * Lista negocios aprobados (público)
 */
router.get('/', async (req, res) => {
  try {
    const { zona, categoria, query } = req.query;
    let sql = 'SELECT * FROM stores WHERE estadoVerificacion = "aprobado"';
    const params = [];

    if (zona) {
      sql += ' AND zona = ?';
      params.push(zona);
    }
    if (categoria) {
      sql += ' AND categoriaPrincipal = ?';
      params.push(categoria);
    }
    if (query) {
      sql += ' AND (nombrePublico LIKE ? OR direccionCompleta LIKE ?)';
      params.push(`%${query}%`, `%${query}%`);
    }

    sql += ' ORDER BY planDestacado DESC, nombrePublico ASC';

    const stores = await dbAll(sql, params);
    res.json({ stores });
  } catch (err) {
    console.error('Stores list error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * GET /api/stores/:id
 * Perfil público de un negocio
 */
router.get('/:id', async (req, res) => {
  try {
    const store = await dbGet(
      'SELECT id, nombrePublico, tipoEntidad, direccionCompleta, zona, latitud, longitud, categoriaPrincipal, telefonoWhatsApp, horario, entregaInfo, webUrl, verificado, planDestacado FROM stores WHERE id = ? AND estadoVerificacion = "aprobado" LIMIT 1',
      [req.params.id]
    );

    if (!store) {
      return res.status(404).json({ error: 'Negocio no encontrado' });
    }

    // Registrar visita
    const now = new Date();
    const dia = now.toISOString().split('T')[0];
    const semana = `${now.getFullYear()}-W${Math.ceil((now.getDate() + 6 - now.getDay()) / 7)}`;
    const mes = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    const ano = String(now.getFullYear());

    await dbRun(
      'INSERT INTO visit_stats (tiendaId, tipo, timestampDia, timestampSemana, timestampMes, timestampAno) VALUES (?, ?, ?, ?, ?, ?)',
      [req.params.id, 'perfil', dia, semana, mes, ano]
    );

    res.json({ store });
  } catch (err) {
    console.error('Store profile error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/stores/register
 * Registro de negocio (pendiente de verificación)
 */
router.post('/register', validate(storeRegistrationSchema), async (req, res) => {
  try {
    const body = req.body;
    const idInterno = `CMP-${Math.floor(10000 + Math.random() * 90000)}`;
    const passwordHash = bcrypt.hashSync(body.password, SALT_ROUNDS);
    const id = require('crypto').randomUUID();

    await dbRun(
      `INSERT INTO stores (
        id, idInterno, nombrePublico, tipoEntidad, representanteNombre, representanteCI,
        representanteTelefono, licenciaEstatal, direccionCompleta, zona, latitud, longitud,
        categoriaPrincipal, telefonoWhatsApp, telefonoRecuperacion, webUrl, horario, entregaInfo,
        username, passwordHash, estadoVerificacion, verificado
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        id, idInterno, body.nombrePublico, body.tipoEntidad, body.representanteNombre,
        body.representanteCI, body.representanteTelefono, body.licenciaEstatal, body.direccionCompleta,
        body.zona, body.latitud, body.longitud, body.categoriaPrincipal, body.telefonoWhatsApp,
        body.telefonoRecuperacion, body.webUrl || null, body.horario, body.entregaInfo,
        body.username, passwordHash, 'pendiente', 0
      ]
    );

    res.status(201).json({
      success: true,
      message: 'Solicitud registrada. Verificación pendiente 24-48h.',
      idInterno
    });
  } catch (err) {
    console.error('Store registration error:', err);
    if (err.message && err.message.includes('UNIQUE constraint failed')) {
      return res.status(409).json({ error: 'El username o ID ya existe' });
    }
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * GET /api/stores/pending
 * Lista negocios pendientes (solo admin)
 */
router.get('/pending', verifyToken, requireRole('admin'), async (req, res) => {
  try {
    const stores = await dbAll('SELECT * FROM stores WHERE estadoVerificacion = "pendiente" ORDER BY creadoEn DESC');
    res.json({ stores });
  } catch (err) {
    console.error('Pending stores error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/stores/:id/verify
 * Aprobar o rechazar negocio (solo admin)
 */
router.post('/:id/verify', verifyToken, requireRole('admin'), async (req, res) => {
  try {
    const { action } = req.body; // 'approve' or 'reject'
    const estado = action === 'approve' ? 'aprobado' : 'rechazado';
    const verificado = action === 'approve' ? 1 : 0;

    await dbRun(
      'UPDATE stores SET estadoVerificacion = ?, verificado = ? WHERE id = ?',
      [estado, verificado, req.params.id]
    );

    res.json({ success: true, message: `Negocio ${estado}` });
  } catch (err) {
    console.error('Verify store error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;
