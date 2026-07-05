const express = require('express');
const { dbRun } = require('../db');

const router = express.Router();

/**
 * POST /api/logs/interaction
 * Log de interacción del comprador (anónimo)
 */
router.post('/interaction', async (req, res) => {
  try {
    const { tipo, tiendaId, productoId, query, deviceId, latitud, longitud } = req.body;
    const now = Date.now();

    await dbRun(
      'INSERT INTO interaction_logs (tipo, tiendaId, productoId, query, deviceId, latitud, longitud, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
      [tipo, tiendaId || null, productoId || null, query || null, deviceId || null, latitud || null, longitud || null, now]
    );

    res.status(201).json({ success: true });
  } catch (err) {
    console.error('Interaction log error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * POST /api/logs/search
 * Log de búsqueda
 */
router.post('/search', async (req, res) => {
  try {
    const { query, filters, resultados, deviceId, latitud, longitud } = req.body;
    const now = Date.now();

    await dbRun(
      'INSERT INTO search_logs (query, filters, resultados, deviceId, latitud, longitud, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [query, filters ? JSON.stringify(filters) : null, resultados || 0, deviceId || null, latitud || null, longitud || null, now]
    );

    res.status(201).json({ success: true });
  } catch (err) {
    console.error('Search log error:', err);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;
