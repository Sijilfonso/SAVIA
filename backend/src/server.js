const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const path = require('path');
const { initSchema } = require('./db');

const authRoutes = require('./routes/auth');
const storeRoutes = require('./routes/stores');
const productRoutes = require('./routes/products');
const logRoutes = require('./routes/logs');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(morgan('combined'));

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/stores', storeRoutes);
app.use('/api/products', productRoutes);
app.use('/api/logs', logRoutes);

// Static landing page
app.use(express.static(path.join(__dirname, '..', '..', 'landing')));
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, '..', '..', 'landing', 'index.html'));
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: 'Ruta no encontrada' });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('Server error:', err);
  res.status(500).json({ error: 'Error interno del servidor' });
});

// Initialize DB and start server
async function start() {
  try {
    await initSchema();
    console.log('Database schema initialized');
    
    app.listen(PORT, () => {
      console.log(`SAVIA API running on port ${PORT}`);
    });
  } catch (err) {
    console.error('Failed to start server:', err);
    process.exit(1);
  }
}

start();
