const express = require('express');
const bcrypt = require('bcryptjs');
const { dbGet, dbRun } = require('../db');
const { generateToken } = require('../middleware/auth');
const { validate, loginSchema, passwordResetRequestSchema, passwordResetSchema } = require('../middleware/validate');

const router = express.Router();

const SALT_ROUNDS_VENDEDOR = 10;
const SALT_ROUNDS_ADMIN = 12;

/**
 * POST /api/auth/login
 * Login unificado: username + password → JWT + rol
 */
router.post('/login', validate(loginSchema), async (req, res) => {
  try {
    const { username, password } = req.body;

    // 1. Buscar en stores (vendedores)
    const store = await dbGet(
      'SELECT * FROM stores WHERE username = ? AND estadoVerificacion = "aprobado" LIMIT 1',
      [username]
    );
    if (store) {
      const valid = bcrypt.compareSync(password, store.passwordHash);
      if (valid) {
        const token = generateToken({
          userId: store.id,
          username: store.username,
          rol: 'vendedor',
          nombre: store.nombrePublico
        });
        return res.json({
          success: true,
          token,
          rol: 'vendedor',
          userId: store.id,
          nombre: store.nombrePublico
        });
      }
    }

    // 2. Buscar en admin_accounts
    const admin = await dbGet(
      'SELECT * FROM admin_accounts WHERE username = ? LIMIT 1',
      [username]
    );
    if (admin) {
      const valid = bcrypt.compareSync(password, admin.passwordHash);
      if (valid) {
        const token = generateToken({
          userId: admin.id,
          username: admin.username,
          rol: 'admin',
          nombre: admin.nombre
        });
        return res.json({
          success: true,
          token,
          rol: 'admin',
          userId: admin.id,
          nombre: admin.nombre
        });
      }
    }

    res.status(401).json({ error: 'Usuario o contraseña incorrectos' });
  } catch (err) {
    console.error('Login error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/auth/recovery/request
 * Genera código de 6 dígitos para recuperación de contraseña
 */
router.post('/recovery/request', validate(passwordResetRequestSchema), async (req, res) => {
  try {
    const { username } = req.body;

    // Buscar teléfono de recuperación
    const store = await dbGet(
      'SELECT telefonoRecuperacion FROM stores WHERE username = ? LIMIT 1',
      [username]
    );
    const admin = !store ? await dbGet(
      'SELECT telefonoRecuperacion FROM admin_accounts WHERE username = ? LIMIT 1',
      [username]
    ) : null;

    const telefono = store?.telefonoRecuperacion || admin?.telefonoRecuperacion;
    if (!telefono) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    const codigo = Math.floor(100000 + Math.random() * 900000).toString();

    await dbRun(
      `INSERT INTO password_resets (username, codigo, telefonoDestino, expiraEn)
       VALUES (?, ?, ?, (strftime('%s', 'now') + 900) * 1000)`,
      [username, codigo, telefono]
    );

    // En producción: enviar por WhatsApp Business API
    res.json({
      success: true,
      message: 'Código generado',
      // En desarrollo incluimos el código para testing
      codigo: process.env.NODE_ENV !== 'production' ? codigo : undefined
    });
  } catch (err) {
    console.error('Recovery request error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/auth/recovery/reset
 * Restablece contraseña con código válido
 */
router.post('/recovery/reset', validate(passwordResetSchema), async (req, res) => {
  try {
    const { username, codigo, nuevaPassword } = req.body;

    const reset = await dbGet(
      `SELECT * FROM password_resets
       WHERE username = ? AND codigo = ? AND usado = 0 AND expiraEn > (strftime('%s', 'now') * 1000)
       ORDER BY creadoEn DESC LIMIT 1`,
      [username, codigo]
    );

    if (!reset) {
      return res.status(400).json({ error: 'Código inválido o expirado' });
    }

    const isAdmin = await dbGet('SELECT id FROM admin_accounts WHERE username = ? LIMIT 1', [username]);
    const hash = bcrypt.hashSync(nuevaPassword, isAdmin ? SALT_ROUNDS_ADMIN : SALT_ROUNDS_VENDEDOR);

    if (isAdmin) {
      await dbRun('UPDATE admin_accounts SET passwordHash = ? WHERE username = ?', [hash, username]);
    } else {
      await dbRun('UPDATE stores SET passwordHash = ? WHERE username = ?', [hash, username]);
    }

    await dbRun('UPDATE password_resets SET usado = 1 WHERE id = ?', [reset.id]);

    res.json({ success: true, message: 'Contraseña actualizada' });
  } catch (err) {
    console.error('Recovery reset error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;
