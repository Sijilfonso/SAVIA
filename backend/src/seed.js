const bcrypt = require('bcryptjs');
const { dbRun, dbAll } = require('./db');

const SALT_ROUNDS_VENDEDOR = 10;
const SALT_ROUNDS_ADMIN = 12;

async function seed() {
  console.log('Seeding database...');

  // Seed admins
  const admin1Hash = bcrypt.hashSync('C4m@gü3y#2026!A', SALT_ROUNDS_ADMIN);
  const admin2Hash = bcrypt.hashSync('S4v!@#C4m2026X', SALT_ROUNDS_ADMIN);

  await dbRun(
    'INSERT OR IGNORE INTO admin_accounts (id, username, passwordHash, nombre, telefonoRecuperacion) VALUES (?, ?, ?, ?, ?)',
    ['admin-1', 'savia.admin1', admin1Hash, 'Administrador SAVIA 1', '+5355551111']
  );
  await dbRun(
    'INSERT OR IGNORE INTO admin_accounts (id, username, passwordHash, nombre, telefonoRecuperacion) VALUES (?, ?, ?, ?, ?)',
    ['admin-2', 'savia.admin2', admin2Hash, 'Administrador SAVIA 2', '+5355552222']
  );

  // Seed stores (11 negocios aprobados)
  const stores = [
    { id: 'store-1', idInterno: 'CMP-00001', nombre: 'La Bodega de Pepe', tipo: 'TCP', cat: 'Alimentos', zona: 'La Caridad', lat: 21.3769, lng: -77.9172, pass: 'Bodega2024' },
    { id: 'store-2', idInterno: 'CMP-00002', nombre: 'El Mercadito de María', tipo: 'MIPYME', cat: 'Alimentos', zona: 'La Caridad', lat: 21.3775, lng: -77.9165, pass: 'Mercado2024' },
    { id: 'store-3', idInterno: 'CMP-00003', nombre: 'Ferretería El Clavo', tipo: 'TCP', cat: 'Ferretería', zona: 'Centro Histórico', lat: 21.3834, lng: -77.9181, pass: 'Ferre2024' },
    { id: 'store-4', idInterno: 'CMP-00004', nombre: 'La Casa del Aseo', tipo: 'MIPYME', cat: 'Aseo', zona: 'Centro Histórico', lat: 21.3840, lng: -77.9175, pass: 'Aseo2024!' },
    { id: 'store-5', idInterno: 'CMP-00005', nombre: 'Electrosur', tipo: 'MIPYME', cat: 'Electrónica', zona: 'Vista Hermosa', lat: 21.3720, lng: -77.9100, pass: 'Electro2024' },
    { id: 'store-6', idInterno: 'CMP-00006', nombre: 'Carnicería El Buen Corte', tipo: 'TCP', cat: 'Carnes', zona: 'Santa Rosa', lat: 21.3890, lng: -77.9050, pass: 'Carnes2024' },
    { id: 'store-7', idInterno: 'CMP-00007', nombre: 'Despensa Santa Rosa', tipo: 'MIPYME', cat: 'Alimentos', zona: 'Santa Rosa', lat: 21.3880, lng: -77.9060, pass: 'Despensa2024' },
    { id: 'store-8', idInterno: 'CMP-00008', nombre: 'Ferretería La Herradura', tipo: 'TCP', cat: 'Ferretería', zona: 'Centro Histórico', lat: 21.3825, lng: -77.9190, pass: 'Herradura2024' },
    { id: 'store-9', idInterno: 'CMP-00009', nombre: 'Belleza Tropical', tipo: 'MIPYME', cat: 'Belleza', zona: 'Vista Hermosa', lat: 21.3730, lng: -77.9110, pass: 'Belleza2024' },
    { id: 'store-10', idInterno: 'CMP-00010', nombre: 'Reparaciones Rápidas', tipo: 'TCP', cat: 'Reparaciones', zona: 'La Caridad', lat: 21.3780, lng: -77.9150, pass: 'Repara2024' },
    { id: 'store-11', idInterno: 'CMP-00011', nombre: 'Vivero Comunitario Santa Elena', tipo: 'PDL', cat: 'Vivero', zona: 'Santa Elena', lat: 21.3750, lng: -77.9200, pass: 'Vivero2024' }
  ];

  for (const s of stores) {
    const hash = bcrypt.hashSync(s.pass, SALT_ROUNDS_VENDEDOR);
    const planDestacado = ['store-2', 'store-4', 'store-5', 'store-9'].includes(s.id) ? 1 : 0;
    await dbRun(
      `INSERT OR IGNORE INTO stores (
        id, idInterno, nombrePublico, tipoEntidad, representanteNombre, representanteCI,
        representanteTelefono, licenciaEstatal, direccionCompleta, zona, latitud, longitud,
        categoriaPrincipal, telefonoWhatsApp, telefonoRecuperacion, webUrl, horario, entregaInfo,
        planDestacado, suscripcionActiva, verificado, estadoVerificacion, username, passwordHash
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        s.id, s.idInterno, s.nombre, s.tipo, s.nombre, '00000000000', '+5355550000',
        `${s.tipo}-${s.cat}-2024-0001`, `Dirección ${s.nombre}`, s.zona, s.lat, s.lng, s.cat,
        '+5355550000', '+5355550000', null, 'Lun-Sab 8:00-17:00', 'Recoge en tienda',
        planDestacado, 1, 1, 'aprobado', s.id.replace('-', ''), hash
      ]
    );
  }

  console.log('Seed completed');
  console.log('  - 2 admin accounts');
  console.log('  - 11 stores (MIPYME/TCP/PDL)');
  console.log('Admin 1: username=savia.admin1, password=C4m@gü3y#2026!A');
  console.log('Admin 2: username=savia.admin2, password=S4v!@#C4m2026X');
  console.log('Vendedor 1: username=store1, password=Bodega2024');
}

if (require.main === module) {
  seed().catch(err => {
    console.error('Seed failed:', err);
    process.exit(1);
  });
}

module.exports = { seed };
