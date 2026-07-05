const express = require('express');
const { dbAll, dbGet, dbRun } = require('../db');
const { verifyToken, requireRole } = require('../middleware/auth');
const { validate, productSchema } = require('../middleware/validate');

const router = express.Router();

/**
 * GET /api/products
 * Lista productos públicos (aprobados + disponibles)
 */
router.get('/', async (req, res) => {
  try {
    const { tiendaId, categoria, tipo, query } = req.query;
    let sql = `
      SELECT p.*, s.nombrePublico as tiendaNombre, s.verificado, s.planDestacado
      FROM products p
      JOIN stores s ON p.tiendaId = s.id
      WHERE s.estadoVerificacion = "aprobado"
        AND p.estadoStock != "agotado"
        AND p.estadoStock != "no_disponible"
    `;
    const params = [];

    if (tiendaId) {
      sql += ' AND p.tiendaId = ?';
      params.push(tiendaId);
    }
    if (categoria) {
      sql += ' AND p.categoria = ?';
      params.push(categoria);
    }
    if (tipo) {
      sql += ' AND p.tipoItem = ?';
      params.push(tipo);
    }
    if (query) {
      sql += ' AND (p.nombre LIKE ? OR p.descripcion LIKE ?)';
      params.push(`%${query}%`, `%${query}%`);
    }

    sql += ' ORDER BY s.planDestacado DESC, p.creadoEn DESC';

    const products = await dbAll(sql, params);
    res.json({ products });
  } catch (err) {
    console.error('Products list error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * GET /api/products/:id
 * Detalle de un producto
 */
router.get('/:id', async (req, res) => {
  try {
    const product = await dbGet(
      `SELECT p.*, s.nombrePublico as tiendaNombre
       FROM products p
       JOIN stores s ON p.tiendaId = s.id
       WHERE p.id = ? AND s.estadoVerificacion = "aprobado" LIMIT 1`,
      [req.params.id]
    );

    if (!product) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    // Registrar visita al producto
    const now = new Date();
    const dia = now.toISOString().split('T')[0];
    const semana = `${now.getFullYear()}-W${Math.ceil((now.getDate() + 6 - now.getDay()) / 7)}`;
    const mes = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    const ano = String(now.getFullYear());

    await dbRun(
      'INSERT INTO visit_stats (tiendaId, productoId, tipo, timestampDia, timestampSemana, timestampMes, timestampAno) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [product.tiendaId, product.id, 'producto', dia, semana, mes, ano]
    );

    res.json({ product });
  } catch (err) {
    console.error('Product detail error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/products
 * Crear producto (solo vendedor propietario o admin)
 */
router.post('/', verifyToken, validate(productSchema), async (req, res) => {
  try {
    const { tiendaId, nombre, descripcion, categoria, tipoItem, precioCUP, precioUSD, precioMLC,
            monedaMostrar, estadoStock, ofertaFlash, precioOfertaCUP, precioOfertaUSD } = req.body;

    // Verificar propiedad (solo vendedor de su propia tienda o admin)
    if (req.user.rol !== 'admin') {
      const store = await dbGet('SELECT id FROM stores WHERE id = ? AND username = ? LIMIT 1', [tiendaId, req.user.username]);
      if (!store) {
        return res.status(403).json({ error: 'No tienes permiso para esta tienda' });
      }
    }

    const id = require('crypto').randomUUID();
    const now = Date.now();

    await dbRun(
      `INSERT INTO products (id, tiendaId, nombre, descripcion, categoria, tipoItem, precioCUP, precioUSD, precioMLC,
        precioOfertaCUP, precioOfertaUSD, monedaMostrar, estadoStock, ofertaFlash, creadoEn, ultimaActualizacion)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [id, tiendaId, nombre, descripcion || null, categoria, tipoItem, precioCUP || null, precioUSD || null, precioMLC || null,
        precioOfertaCUP || null, precioOfertaUSD || null, monedaMostrar, estadoStock, ofertaFlash ? 1 : 0, now, now]
    );

    res.status(201).json({ success: true, id });
  } catch (err) {
    console.error('Create product error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * PUT /api/products/:id
 * Actualizar producto (solo vendedor propietario o admin)
 */
router.put('/:id', verifyToken, async (req, res) => {
  try {
    const product = await dbGet('SELECT tiendaId FROM products WHERE id = ? LIMIT 1', [req.params.id]);
    if (!product) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    if (req.user.rol !== 'admin') {
      const store = await dbGet('SELECT id FROM stores WHERE id = ? AND username = ? LIMIT 1', [product.tiendaId, req.user.username]);
      if (!store) {
        return res.status(403).json({ error: 'No tienes permiso para este producto' });
      }
    }

    const fields = [];
    const params = [];
    const allowed = ['nombre', 'descripcion', 'categoria', 'tipoItem', 'precioCUP', 'precioUSD', 'precioMLC',
                     'precioOfertaCUP', 'precioOfertaUSD', 'monedaMostrar', 'estadoStock', 'ofertaFlash'];

    allowed.forEach(key => {
      if (req.body[key] !== undefined) {
        fields.push(`${key} = ?`);
        params.push(key === 'ofertaFlash' ? (req.body[key] ? 1 : 0) : req.body[key]);
      }
    });

    if (fields.length === 0) {
      return res.status(400).json({ error: 'No hay campos para actualizar' });
    }

    fields.push('ultimaActualizacion = ?');
    params.push(Date.now());
    params.push(req.params.id);

    await dbRun(
      `UPDATE products SET ${fields.join(', ')} WHERE id = ?`,
      params
    );

    res.json({ success: true, message: 'Producto actualizado' });
  } catch (err) {
    console.error('Update product error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * DELETE /api/products/:id
 * Eliminar producto (solo vendedor propietario o admin)
 */
router.delete('/:id', verifyToken, async (req, res) => {
  try {
    const product = await dbGet('SELECT tiendaId FROM products WHERE id = ? LIMIT 1', [req.params.id]);
    if (!product) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    if (req.user.rol !== 'admin') {
      const store = await dbGet('SELECT id FROM stores WHERE id = ? AND username = ? LIMIT 1', [product.tiendaId, req.user.username]);
      if (!store) {
        return res.status(403).json({ error: 'No tienes permiso para este producto' });
      }
    }

    await dbRun('DELETE FROM products WHERE id = ?', [req.params.id]);
    res.json({ success: true, message: 'Producto eliminado' });
  } catch (err) {
    console.error('Delete product error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;
