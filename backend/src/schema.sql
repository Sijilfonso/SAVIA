-- SAVIA SQLite Schema
-- Mapeo de las entidades Room a tablas SQL

-- Stores (negocios / vendedores)
CREATE TABLE IF NOT EXISTS stores (
    id TEXT PRIMARY KEY,
    idInterno TEXT NOT NULL UNIQUE,
    nombrePublico TEXT NOT NULL,
    tipoEntidad TEXT NOT NULL CHECK(tipoEntidad IN ('MIPYME', 'TCP', 'PDL')),
    representanteNombre TEXT NOT NULL,
    representanteCI TEXT NOT NULL,
    representanteTelefono TEXT NOT NULL,
    licenciaEstatal TEXT NOT NULL,
    direccionCompleta TEXT NOT NULL,
    zona TEXT NOT NULL,
    latitud REAL NOT NULL,
    longitud REAL NOT NULL,
    categoriaPrincipal TEXT NOT NULL,
    telefonoWhatsApp TEXT NOT NULL,
    telefonoRecuperacion TEXT NOT NULL,
    webUrl TEXT,
    fotoLocalUrl TEXT,
    horario TEXT NOT NULL,
    entregaInfo TEXT NOT NULL,
    planDestacado INTEGER DEFAULT 0,
    planTrialHasta INTEGER,
    suscripcionActiva INTEGER DEFAULT 0,
    suscripcionVence INTEGER,
    verificado INTEGER DEFAULT 0,
    estadoVerificacion TEXT DEFAULT 'pendiente' CHECK(estadoVerificacion IN ('pendiente', 'aprobado', 'rechazado')),
    username TEXT NOT NULL UNIQUE,
    passwordHash TEXT NOT NULL,
    rol TEXT DEFAULT 'vendedor' CHECK(rol IN ('vendedor', 'admin')),
    creadoEn INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    ultimaActualizacion INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- Products (productos + servicios)
CREATE TABLE IF NOT EXISTS products (
    id TEXT PRIMARY KEY,
    tiendaId TEXT NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    categoria TEXT NOT NULL,
    tipoItem TEXT NOT NULL CHECK(tipoItem IN ('producto', 'servicio')),
    precioCUP REAL,
    precioUSD REAL,
    precioMLC REAL,
    precioOfertaCUP REAL,
    precioOfertaUSD REAL,
    monedaMostrar TEXT DEFAULT 'CUP' CHECK(monedaMostrar IN ('CUP', 'USD', 'MLC')),
    estadoStock TEXT DEFAULT 'disponible' CHECK(estadoStock IN ('disponible', 'agotado', 'por_encargo', 'no_disponible')),
    ofertaFlash INTEGER DEFAULT 0,
    stockCantidad INTEGER,
    unidadMedida TEXT,
    variante TEXT,
    marca TEXT,
    proveedor TEXT,
    tiempoEntrega TEXT,
    tags TEXT, -- JSON array como string
    creadoEn INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    ultimaActualizacion INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    FOREIGN KEY (tiendaId) REFERENCES stores(id) ON DELETE CASCADE
);

-- Admin accounts
CREATE TABLE IF NOT EXISTS admin_accounts (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    passwordHash TEXT NOT NULL,
    nombre TEXT NOT NULL,
    telefonoRecuperacion TEXT NOT NULL,
    rol TEXT DEFAULT 'admin',
    creadoEn INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- Password resets
CREATE TABLE IF NOT EXISTS password_resets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    codigo TEXT NOT NULL,
    telefonoDestino TEXT NOT NULL,
    creadoEn INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    expiraEn INTEGER DEFAULT ((strftime('%s', 'now') + 900) * 1000), -- 15 min
    usado INTEGER DEFAULT 0
);

-- Visit stats
CREATE TABLE IF NOT EXISTS visit_stats (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tiendaId TEXT NOT NULL,
    productoId TEXT,
    tipo TEXT NOT NULL CHECK(tipo IN ('perfil', 'producto', 'whatsapp_click')),
    timestampDia TEXT NOT NULL,
    timestampSemana TEXT NOT NULL,
    timestampMes TEXT NOT NULL,
    timestampAno TEXT NOT NULL,
    conteo INTEGER DEFAULT 1,
    ultimaActualizacion INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- Interaction logs
CREATE TABLE IF NOT EXISTS interaction_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo TEXT NOT NULL CHECK(tipo IN ('busqueda', 'clic_producto', 'clic_whatsapp', 'vista_perfil', 'agregado_carrito')),
    tiendaId TEXT,
    productoId TEXT,
    query TEXT,
    deviceId TEXT,
    latitud REAL,
    longitud REAL,
    timestamp INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- Search logs
CREATE TABLE IF NOT EXISTS search_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    query TEXT NOT NULL,
    filters TEXT, -- JSON string
    resultados INTEGER DEFAULT 0,
    deviceId TEXT,
    latitud REAL,
    longitud REAL,
    timestamp INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_stores_estado ON stores(estadoVerificacion);
CREATE INDEX IF NOT EXISTS idx_stores_zona ON stores(zona);
CREATE INDEX IF NOT EXISTS idx_stores_categoria ON stores(categoriaPrincipal);
CREATE INDEX IF NOT EXISTS idx_products_tienda ON products(tiendaId);
CREATE INDEX IF NOT EXISTS idx_products_tipo ON products(tipoItem);
CREATE INDEX IF NOT EXISTS idx_visit_stats_tienda ON visit_stats(tiendaId);
CREATE INDEX IF NOT EXISTS idx_visit_stats_tipo ON visit_stats(tipo);
CREATE INDEX IF NOT EXISTS idx_interaction_tienda ON interaction_logs(tiendaId);
CREATE INDEX IF NOT EXISTS idx_search_timestamp ON search_logs(timestamp);
