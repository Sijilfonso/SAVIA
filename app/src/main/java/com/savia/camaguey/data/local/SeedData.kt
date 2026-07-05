package com.savia.camaguey.data.local

import com.savia.camaguey.data.model.AdminAccount
import com.savia.camaguey.data.model.Product
import com.savia.camaguey.data.model.Store
import com.savia.camaguey.data.model.UserLocation
import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Datos semilla (seed data) para SAVIA.
 * 11 negocios (10 + 1 PDL) + 178 productos/servicios + 2 admins.
 * Se ejecuta UNA SOLA VEZ al crear la base de datos.
 */
object SeedData {

    const val CURRENT_SEED_VERSION = 1

    // BCrypt hashes (cost 10 para vendedores, cost 12 para admins)
    private val bcryptHasher10 = BCrypt.withDefaults()
    private val bcryptHasher12 = BCrypt.with(BCrypt.Version.VERSION_2Y, LongArray(12))

    val adminAccounts: List<AdminAccount> = listOf(
        AdminAccount(
            id = "admin-1",
            username = "savia.admin1",
            passwordHash = bcryptHasher12.hashToString(12, "C4m@gü3y#2026!A".toCharArray()),
            nombre = "Administrador SAVIA 1",
            telefonoRecuperacion = "+5355551111"
        ),
        AdminAccount(
            id = "admin-2",
            username = "savia.admin2",
            passwordHash = bcryptHasher12.hashToString(12, "S4v!@#C4m2026X".toCharArray()),
            nombre = "Administrador SAVIA 2",
            telefonoRecuperacion = "+5355552222"
        )
    )

    val stores: List<Store> = listOf(
        // Tienda 1: La Bodega de Pepe (TCP - Alimentos)
        Store(
            id = "store-1", idInterno = "CMP-00001", nombrePublico = "La Bodega de Pepe",
            tipoEntidad = "TCP", representanteNombre = "José Luis Pepe García",
            representanteCI = "85061212345", representanteTelefono = "+5355550001",
            licenciaEstatal = "TCP-ALM-2024-0001", direccionCompleta = "Calle Maceo #123 entre San José y San Martín",
            zona = "La Caridad", latitud = 21.3769, longitud = -77.9172,
            categoriaPrincipal = "Alimentos", telefonoWhatsApp = "+5355550001",
            telefonoRecuperacion = "+5355550001", webUrl = "https://bodegapepe.ejemplo.cu",
            fotoLocalUrl = null, horario = "Lun-Sab 8:00-17:00", entregaInfo = "Recoge en tienda",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "bodegapepe", passwordHash = bcryptHasher10.hashToString(10, "Bodega2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 2: El Mercadito de María (MIPYME - Alimentos)
        Store(
            id = "store-2", idInterno = "CMP-00002", nombrePublico = "El Mercadito de María",
            tipoEntidad = "MIPYME", representanteNombre = "María Elena Sánchez",
            representanteCI = "78031298765", representanteTelefono = "+5355550002",
            licenciaEstatal = "MIPYME-ALM-2024-0002", direccionCompleta = "Avenida de los Mártires #45 entre Cisneros y Ignacio Agramonte",
            zona = "La Caridad", latitud = 21.3775, longitud = -77.9165,
            categoriaPrincipal = "Alimentos", telefonoWhatsApp = "+5355550002",
            telefonoRecuperacion = "+5355550002", webUrl = null,
            fotoLocalUrl = null, horario = "Lun-Dom 7:00-19:00", entregaInfo = "Entrega a domicilio en La Caridad",
            planDestacado = true, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "mercaditomaria", passwordHash = bcryptHasher10.hashToString(10, "Mercado2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 3: Ferretería El Clavo (TCP - Ferretería)
        Store(
            id = "store-3", idInterno = "CMP-00003", nombrePublico = "Ferretería El Clavo",
            tipoEntidad = "TCP", representanteNombre = "Carlos Manuel Herrera",
            representanteCI = "81050545678", representanteTelefono = "+5355550003",
            licenciaEstatal = "TCP-FER-2024-0003", direccionCompleta = "Calle Padre Valencia #88 esquina a San Esteban",
            zona = "Centro Histórico", latitud = 21.3834, longitud = -77.9181,
            categoriaPrincipal = "Ferretería", telefonoWhatsApp = "+5355550003",
            telefonoRecuperacion = "+5355550003", webUrl = "https://ferreclavo.ejemplo.cu",
            fotoLocalUrl = null, horario = "Lun-Sab 8:30-17:30", entregaInfo = "Recoge en tienda, envío por acuerdo",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "ferreclavo", passwordHash = bcryptHasher10.hashToString(10, "Ferre2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 4: La Casa del Aseo (MIPYME - Aseo)
        Store(
            id = "store-4", idInterno = "CMP-00004", nombrePublico = "La Casa del Aseo",
            tipoEntidad = "MIPYME", representanteNombre = "Ana Lucía Fernández",
            representanteCI = "92011023456", representanteTelefono = "+5355550004",
            licenciaEstatal = "MIPYME-ASE-2024-0004", direccionCompleta = "Calle República #210 altos entre Lugareño y Callejón de la Paila",
            zona = "Centro Histórico", latitud = 21.3840, longitud = -77.9175,
            categoriaPrincipal = "Aseo", telefonoWhatsApp = "+5355550004",
            telefonoRecuperacion = "+5355550004", webUrl = null,
            fotoLocalUrl = null, horario = "Lun-Sab 9:00-18:00", entregaInfo = "Entrega a domicilio en Centro Histórico y La Caridad",
            planDestacado = true, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "casadelseo", passwordHash = bcryptHasher10.hashToString(10, "Aseo2024!".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 5: Electrosur (MIPYME - Electrónica)
        Store(
            id = "store-5", idInterno = "CMP-00005", nombrePublico = "Electrosur",
            tipoEntidad = "MIPYME", representanteNombre = "Roberto Miguel Suárez",
            representanteCI = "75081567890", representanteTelefono = "+5355550005",
            licenciaEstatal = "MIPYME-ELE-2024-0005", direccionCompleta = "Avenida de la Libertad #56 entre 3ra y 4ta, Vista Hermosa",
            zona = "Vista Hermosa", latitud = 21.3720, longitud = -77.9100,
            categoriaPrincipal = "Electrónica", telefonoWhatsApp = "+5355550005",
            telefonoRecuperacion = "+5355550005", webUrl = "https://electrosur.ejemplo.cu",
            fotoLocalUrl = null, horario = "Lun-Sab 9:00-17:00", entregaInfo = "Recoge en tienda, asesoría técnica incluida",
            planDestacado = true, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "electrosur", passwordHash = bcryptHasher10.hashToString(10, "Electro2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 6: Carnicería El Buen Corte (TCP - Carnes)
        Store(
            id = "store-6", idInterno = "CMP-00006", nombrePublico = "Carnicería El Buen Corte",
            tipoEntidad = "TCP", representanteNombre = "Luis Antonio Domínguez",
            representanteCI = "86042234567", representanteTelefono = "+5355550006",
            licenciaEstatal = "TCP-CAR-2024-0006", direccionCompleta = "Calle Santa Rosa #78 entre Colón y Bolívar",
            zona = "Santa Rosa", latitud = 21.3890, longitud = -77.9050,
            categoriaPrincipal = "Carnes", telefonoWhatsApp = "+5355550006",
            telefonoRecuperacion = "+5355550006", webUrl = null,
            fotoLocalUrl = null, horario = "Mar-Dom 6:00-14:00", entregaInfo = "Pedidos con 24h de anticipación",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "buencorte", passwordHash = bcryptHasher10.hashToString(10, "Carnes2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 7: Despensa Santa Rosa (MIPYME - Alimentos)
        Store(
            id = "store-7", idInterno = "CMP-00007", nombrePublico = "Despensa Santa Rosa",
            tipoEntidad = "MIPYME", representanteNombre = "Diana Patricia Vega",
            representanteCI = "91061112345", representanteTelefono = "+5355550007",
            licenciaEstatal = "MIPYME-ALM-2024-0007", direccionCompleta = "Calle General Gómez #112 esquina a Santa Rosa",
            zona = "Santa Rosa", latitud = 21.3880, longitud = -77.9060,
            categoriaPrincipal = "Alimentos", telefonoWhatsApp = "+5355550007",
            telefonoRecuperacion = "+5355550007", webUrl = "https://despensasantarosa.ejemplo.cu",
            fotoLocalUrl = null, horario = "Lun-Sab 7:30-18:00", entregaInfo = "Entrega a domicilio en Santa Rosa y alrededores",
            planDestacado = true, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "despensasr", passwordHash = bcryptHasher10.hashToString(10, "Despensa2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 8: Ferretería La Herradura (TCP - Ferretería)
        Store(
            id = "store-8", idInterno = "CMP-00008", nombrePublico = "Ferretería La Herradura",
            tipoEntidad = "TCP", representanteNombre = "Pedro Alberto Castillo",
            representanteCI = "79083056789", representanteTelefono = "+5355550008",
            licenciaEstatal = "TCP-FER-2024-0008", direccionCompleta = "Calle San Esteban #45 entre Luz y Caballería",
            zona = "Centro Histórico", latitud = 21.3825, longitud = -77.9190,
            categoriaPrincipal = "Ferretería", telefonoWhatsApp = "+5355550008",
            telefonoRecuperacion = "+5355550008", webUrl = null,
            fotoLocalUrl = null, horario = "Lun-Vie 8:00-17:00, Sáb 8:00-12:00", entregaInfo = "Recoge en tienda",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "herradura", passwordHash = bcryptHasher10.hashToString(10, "Herradura24".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 9: Belleza Tropical (MIPYME - Servicios)
        Store(
            id = "store-9", idInterno = "CMP-00009", nombrePublico = "Belleza Tropical",
            tipoEntidad = "MIPYME", representanteNombre = "Laura Isabel Morales",
            representanteCI = "88071567891", representanteTelefono = "+5355550009",
            licenciaEstatal = "MIPYME-SER-2024-0009", direccionCompleta = "Calle 4ta #23 entre Avenida Libertad y 5ta, Vista Hermosa",
            zona = "Vista Hermosa", latitud = 21.3730, longitud = -77.9110,
            categoriaPrincipal = "Belleza", telefonoWhatsApp = "+5355550009",
            telefonoRecuperacion = "+5355550009", webUrl = "https://bellezatropical.ejemplo.cu",
            fotoLocalUrl = null, horario = "Lun-Sab 9:00-19:00", entregaInfo = "Cita previa por WhatsApp",
            planDestacado = true, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "bellezatropical", passwordHash = bcryptHasher10.hashToString(10, "Belleza2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 10: Reparaciones Rápidas (TCP - Servicios)
        Store(
            id = "store-10", idInterno = "CMP-00010", nombrePublico = "Reparaciones Rápidas",
            tipoEntidad = "TCP", representanteNombre = "Francisco Javier Méndez",
            representanteCI = "83020478901", representanteTelefono = "+5355550010",
            licenciaEstatal = "TCP-SER-2024-0010", direccionCompleta = "Calle San José #156 entre Maceo y Agramonte",
            zona = "La Caridad", latitud = 21.3780, longitud = -77.9150,
            categoriaPrincipal = "Reparaciones", telefonoWhatsApp = "+5355550010",
            telefonoRecuperacion = "+5355550010", webUrl = null,
            fotoLocalUrl = null, horario = "Lun-Sab 8:00-18:00", entregaInfo = "Servicio a domicilio, diagnóstico gratuito",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "reparaciones", passwordHash = bcryptHasher10.hashToString(10, "Repara2024".toCharArray()),
            rol = "vendedor"
        ),
        // Tienda 11: Vivero Comunitario Santa Elena (PDL - Servicios/Productos)
        Store(
            id = "store-11", idInterno = "CMP-00011", nombrePublico = "Vivero Comunitario Santa Elena",
            tipoEntidad = "PDL", representanteNombre = "Yamila Cruz Hernández",
            representanteCI = "95010323456", representanteTelefono = "+5355550011",
            licenciaEstatal = "PDL-ECO-2024-0011", direccionCompleta = "Calle Santa Elena #5, área verde comunitaria",
            zona = "Santa Elena", latitud = 21.3750, longitud = -77.9200,
            categoriaPrincipal = "Vivero", telefonoWhatsApp = "+5355550011",
            telefonoRecuperacion = "+5355550011", webUrl = null,
            fotoLocalUrl = null, horario = "Lun-Vie 8:00-16:00, Sáb 8:00-12:00", entregaInfo = "Talleres comunitarios gratuitos, plantas a precio solidario",
            planDestacado = false, planTrialHasta = null, suscripcionActiva = true, suscripcionVence = null,
            verificado = true, estadoVerificacion = "aprobado",
            username = "viverosantaelena", passwordHash = bcryptHasher10.hashToString(10, "Vivero2024".toCharArray()),
            rol = "vendedor"
        )
    )

    val products: List<Product> = buildList {
        // === TIENDA 1: La Bodega de Pepe (15 productos Alimentos) ===
        add(Product("p1-1", "store-1", "Arroz blanco premium", "Arroz de grano largo, 1kg", "Alimentos", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 50, "kg", null, null, null, tags = listOf("básico", "granos")))
        add(Product("p1-2", "store-1", "Frijoles negros", "Frijoles selectos, 1kg", "Alimentos", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 30, "kg", null, null, null, tags = listOf("básico", "legumbres")))
        add(Product("p1-3", "store-1", "Aceite comestible", "Aceite de soya, 1L", "Alimentos", "producto", 450.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 40, "litro", null, null, null, tags = listOf("básico", "aceite")))
        add(Product("p1-4", "store-1", "Azúcar blanca", "Azúcar refinada, 1kg", "Alimentos", "producto", 200.0, 0.8, null, "CUP", "disponible", false, null, null, null, null, null, 60, "kg", null, null, null, tags = listOf("básico", "dulce")))
        add(Product("p1-5", "store-1", "Harina de trigo", "Harina todo uso, 1kg", "Alimentos", "producto", 300.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 45, "kg", null, null, null, tags = listOf("básico", "panadería")))
        add(Product("p1-6", "store-1", "Leche en polvo", "Leche entera en polvo, 400g", "Alimentos", "producto", 600.0, 2.5, null, "CUP", "disponible", false, null, null, null, null, null, 25, "unidad", null, null, null, tags = listOf("lácteo", "niños")))
        add(Product("p1-7", "store-1", "Café molido", "Café 100% arábigo, 250g", "Alimentos", "producto", 800.0, 3.0, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("bebida", "desayuno")))
        add(Product("p1-8", "store-1", "Pasta de tomate", "Pasta concentrada, 280g", "Alimentos", "producto", 180.0, 0.7, null, "CUP", "disponible", false, null, null, null, null, null, 35, "unidad", null, null, null, tags = listOf("salsa", "cocina")))
        add(Product("p1-9", "store-1", "Sal iodada", "Sal refinada iodada, 1kg", "Alimentos", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 80, "kg", null, null, null, tags = listOf("básico", "condimento")))
        add(Product("p1-10", "store-1", "Mantequilla", "Mantequilla sin sal, 225g", "Alimentos", "producto", 550.0, 2.2, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("lácteo", "repostería")))
        add(Product("p1-11", "store-1", "Huevos (cartón)", "Cartón de 30 huevos frescos", "Alimentos", "producto", 900.0, 3.5, null, "CUP", "disponible", false, null, null, null, null, null, 20, "cartón", null, null, null, tags = listOf("proteína", "fresco")))
        add(Product("p1-12", "store-1", "Pollo entero", "Pollo fresco por kg", "Alimentos", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 40, "kg", null, null, null, tags = listOf("carnes", "proteína")))
        add(Product("p1-13", "store-1", "Papas", "Papas frescas, 1kg", "Alimentos", "producto", 150.0, 0.6, null, "CUP", "disponible", false, null, null, null, null, null, 70, "kg", null, null, null, tags = listOf("verdura", "básico")))
        add(Product("p1-14", "store-1", "Cebolla", "Cebolla blanca, 1kg", "Alimentos", "producto", 100.0, 0.4, null, "CUP", "disponible", false, null, null, null, null, null, 55, "kg", null, null, null, tags = listOf("verdura", "condimento")))
        add(Product("p1-15", "store-1", "Ajo", "Ajo fresco, 100g", "Alimentos", "producto", 80.0, 0.3, null, "CUP", "disponible", false, null, null, null, null, null, 40, "100g", null, null, null, tags = listOf("condimento", "verdura")))

        // === TIENDA 2: El Mercadito de María (17 productos Alimentos) ===
        add(Product("p2-1", "store-2", "Arroz integral", "Arroz integral orgánico, 1kg", "Alimentos", "producto", 280.0, 1.1, null, "CUP", "disponible", false, null, null, null, null, null, 35, "kg", null, null, null, tags = listOf("orgánico", "granos")))
        add(Product("p2-2", "store-2", "Frijoles rojos", "Frijoles rojos selectos, 1kg", "Alimentos", "producto", 360.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 28, "kg", null, null, null, tags = listOf("legumbres", "básico")))
        add(Product("p2-3", "store-2", "Lentejas", "Lentejas canadienses, 1kg", "Alimentos", "producto", 400.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 22, "kg", null, null, null, tags = listOf("legumbres", "proteína")))
        add(Product("p2-4", "store-2", "Garbanzos", "Garbanzos secos, 1kg", "Alimentos", "producto", 420.0, 1.7, null, "CUP", "disponible", false, null, null, null, null, null, 20, "kg", null, null, null, tags = listOf("legumbres", "básico")))
        add(Product("p2-5", "store-2", "Aceite de oliva", "Aceite de oliva extra virgen, 500ml", "Alimentos", "producto", 950.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "botella", null, null, null, tags = listOf("premium", "aceite")))
        add(Product("p2-6", "store-2", "Miel pura", "Miel de abeja natural, 500g", "Alimentos", "producto", 700.0, 3.0, null, "CUP", "disponible", false, null, null, null, null, null, 18, "botella", null, null, null, tags = listOf("natural", "dulce")))
        add(Product("p2-7", "store-2", "Mermelada de mango", "Mermelada artesanal, 280g", "Alimentos", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 25, "frasco", null, null, null, tags = listOf("artesanal", "desayuno")))
        add(Product("p2-8", "store-2", "Yogurt natural", "Yogurt casero, 500ml", "Alimentos", "producto", 300.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 30, "envase", null, null, null, tags = listOf("lácteo", "probiotico")))
        add(Product("p2-9", "store-2", "Queso blanco", "Queso fresco campesino, 500g", "Alimentos", "producto", 650.0, 2.8, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("lácteo", "queso")))
        add(Product("p2-10", "store-2", "Pan de casa", "Pan artesanal de harina integral, 400g", "Alimentos", "producto", 200.0, 0.8, null, "CUP", "disponible", false, null, null, null, null, null, 40, "unidad", null, null, null, tags = listOf("panadería", "artesanal")))
        add(Product("p2-11", "store-2", "Malta", "Bebida de malta, 330ml", "Alimentos", "producto", 180.0, 0.7, null, "CUP", "disponible", false, null, null, null, null, null, 60, "lata", null, null, null, tags = listOf("bebida", "refresco")))
        add(Product("p2-12", "store-2", "Jugo de guayaba", "Jugo natural 100%, 1L", "Alimentos", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 45, "botella", null, null, null, tags = listOf("jugo", "natural")))
        add(Product("p2-13", "store-2", "Plátano macho", "Plátano macho maduro, 1kg", "Alimentos", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 50, "kg", null, null, null, tags = listOf("fruta", "frito")))
        add(Product("p2-14", "store-2", "Calabaza", "Calabaza criolla, 1kg", "Alimentos", "producto", 90.0, 0.35, null, "CUP", "disponible", false, null, null, null, null, null, 40, "kg", null, null, null, tags = listOf("verdura", "cocina")))
        add(Product("p2-15", "store-2", "Boniato", "Boniato dulce, 1kg", "Alimentos", "producto", 110.0, 0.45, null, "CUP", "disponible", false, null, null, null, null, null, 45, "kg", null, null, null, tags = listOf("tubérculo", "dulce")))
        add(Product("p2-16", "store-2", "Malanga", "Malanga fresca, 1kg", "Alimentos", "producto", 130.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 35, "kg", null, null, null, tags = listOf("tubérculo", "básico")))
        add(Product("p2-17", "store-2", "Yuca", "Yuca fresca, 1kg", "Alimentos", "producto", 100.0, 0.4, null, "CUP", "disponible", false, null, null, null, null, null, 42, "kg", null, null, null, tags = listOf("tubérculo", "frito")))

        // === TIENDA 3: Ferretería El Clavo (17 productos Ferretería) ===
        add(Product("p3-1", "store-3", "Clavos 2\"", "Clavos para madera, 1kg", "Ferretería", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 25, "kg", null, null, null, tags = listOf("clavos", "madera")))
        add(Product("p3-2", "store-3", "Tornillos 1/4\"", "Tornillos autorroscantes, caja 100u", "Ferretería", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 30, "caja", null, null, null, tags = listOf("tornillos", "fijación")))
        add(Product("p3-3", "store-3", "Martillo carpintero", "Martillo 16oz mango de madera", "Ferretería", "producto", 850.0, 3.5, null, "CUP", "disponible", false, null, null, null, null, null, 12, "unidad", null, null, null, tags = listOf("herramienta", "martillo")))
        add(Product("p3-4", "store-3", "Alicate universal", "Alicate 8\" acero al carbono", "Ferretería", "producto", 650.0, 2.8, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("herramienta", "alicate")))
        add(Product("p3-5", "store-3", "Destornillador plano", "Destornillador 6mm x 150mm", "Ferretería", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("herramienta", "destornillador")))
        add(Product("p3-6", "store-3", "Destornillador Phillips", "Destornillador PH2 x 150mm", "Ferretería", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("herramienta", "destornillador")))
        add(Product("p3-7", "store-3", "Sierra para metal", "Sierra de arco para metal 12\"", "Ferretería", "producto", 950.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, 8, "unidad", null, null, null, tags = listOf("herramienta", "sierra")))
        add(Product("p3-8", "store-3", "Cinta métrica 5m", "Cinta métrica de acero 5m x 19mm", "Ferretería", "producto", 480.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 18, "unidad", null, null, null, tags = listOf("medición", "cinta")))
        add(Product("p3-9", "store-3", "Nivel de burbuja", "Nivel magnético 12\"", "Ferretería", "producto", 720.0, 3.0, null, "CUP", "disponible", false, null, null, null, null, null, 10, "unidad", null, null, null, tags = listOf("medición", "nivel")))
        add(Product("p3-10", "store-3", "Broca para concreto 1/4\"", "Broca widia 1/4\" x 4\"", "Ferretería", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 25, "unidad", null, null, null, tags = listOf("broca", "concreto")))
        add(Product("p3-11", "store-3", "Broca para madera 3/8\"", "Broca espiral 3/8\"", "Ferretería", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 22, "unidad", null, null, null, tags = listOf("broca", "madera")))
        add(Product("p3-12", "store-3", "Lija 80", "Lija de agua grano 80, 10 hojas", "Ferretería", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 35, "paquete", null, null, null, tags = listOf("lija", "acabado")))
        add(Product("p3-13", "store-3", "Lija 120", "Lija de agua grano 120, 10 hojas", "Ferretería", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 35, "paquete", null, null, null, tags = listOf("lija", "acabado")))
        add(Product("p3-14", "store-3", "Pintura blanca latex", "Pintura latex interior, 1L", "Ferretería", "producto", 1200.0, 5.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "litro", null, null, null, tags = listOf("pintura", "interior")))
        add(Product("p3-15", "store-3", "Pintura azul latex", "Pintura latex interior azul, 1L", "Ferretería", "producto", 1250.0, 5.2, null, "CUP", "disponible", false, null, null, null, null, null, 12, "litro", null, null, null, tags = listOf("pintura", "interior")))
        add(Product("p3-16", "store-3", "Brocha 3\"", "Brocha pelo natural 3\"", "Ferretería", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("pintura", "brocha")))
        add(Product("p3-17", "store-3", "Rodillo 9\"", "Rodillo para pintar 9\"", "Ferretería", "producto", 550.0, 2.3, null, "CUP", "disponible", false, null, null, null, null, null, 16, "unidad", null, null, null, tags = listOf("pintura", "rodillo")))

        // === TIENDA 4: La Casa del Aseo (17 productos Aseo) ===
        add(Product("p4-1", "store-4", "Jabón de lavar", "Jabón en barra, 250g", "Aseo", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 80, "unidad", null, null, null, tags = listOf("jabón", "ropa")))
        add(Product("p4-2", "store-4", "Detergente líquido", "Detergente líquido, 1L", "Aseo", "producto", 450.0, 1.8, null, "CUP", "disponible", false, null, null, null, null, null, 40, "litro", null, null, null, tags = listOf("detergente", "ropa")))
        add(Product("p4-3", "store-4", "Suavizante", "Suavizante de ropa, 1L", "Aseo", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 35, "litro", null, null, null, tags = listOf("suavizante", "ropa")))
        add(Product("p4-4", "store-4", "Cloro", "Cloro doméstico, 1L", "Aseo", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 50, "litro", null, null, null, tags = listOf("desinfectante", "limpieza")))
        add(Product("p4-5", "store-4", "Desinfectante piso", "Desinfectante líquido, 1L", "Aseo", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 42, "litro", null, null, null, tags = listOf("desinfectante", "piso")))
        add(Product("p4-6", "store-4", "Jabón líquido manos", "Jabón líquido, 500ml", "Aseo", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 45, "envase", null, null, null, tags = listOf("jabón", "manos")))
        add(Product("p4-7", "store-4", "Shampoo", "Shampoo multivitamínico, 400ml", "Aseo", "producto", 550.0, 2.3, null, "CUP", "disponible", false, null, null, null, null, null, 30, "envase", null, null, null, tags = listOf("cabello", "shampoo")))
        add(Product("p4-8", "store-4", "Acondicionador", "Acondicionador hidratante, 400ml", "Aseo", "producto", 520.0, 2.2, null, "CUP", "disponible", false, null, null, null, null, null, 28, "envase", null, null, null, tags = listOf("cabello", "acondicionador")))
        add(Product("p4-9", "store-4", "Pasta dental", "Pasta dental blanqueadora, 75ml", "Aseo", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 60, "tubo", null, null, null, tags = listOf("dental", "higiene")))
        add(Product("p4-10", "store-4", "Cepillo dental", "Cepillo dental suave", "Aseo", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 55, "unidad", null, null, null, tags = listOf("dental", "cepillo")))
        add(Product("p4-11", "store-4", "Desodorante roll-on", "Desodorante antitranspirante, 50ml", "Aseo", "producto", 420.0, 1.8, null, "CUP", "disponible", false, null, null, null, null, null, 40, "envase", null, null, null, tags = listOf("desodorante", "higiene")))
        add(Product("p4-12", "store-4", "Jabón de tocador", "Jabón perfumado, 125g", "Aseo", "producto", 150.0, 0.6, null, "CUP", "disponible", false, null, null, null, null, null, 70, "unidad", null, null, null, tags = listOf("jabón", "baño")))
        add(Product("p4-13", "store-4", "Crema hidratante", "Crema corporal, 200ml", "Aseo", "producto", 480.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 25, "envase", null, null, null, tags = listOf("piel", "crema")))
        add(Product("p4-14", "store-4", "Papel higiénico", "Papel higiénico 4 rollos", "Aseo", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 100, "paquete", null, null, null, tags = listOf("papel", "baño")))
        add(Product("p4-15", "store-4", "Servilletas", "Servilletas de papel, 100u", "Aseo", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 80, "paquete", null, null, null, tags = listOf("papel", "cocina")))
        add(Product("p4-16", "store-4", "Esponja cocina", "Esponja abrasiva, 3u", "Aseo", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 65, "paquete", null, null, null, tags = listOf("cocina", "limpieza")))
        add(Product("p4-17", "store-4", "Bolsa basura", "Bolsas para basura, 20u", "Aseo", "producto", 200.0, 0.85, null, "CUP", "disponible", false, null, null, null, null, null, 55, "paquete", null, null, null, tags = listOf("basura", "bolsa")))

        // === TIENDA 5: Electrosur (17 productos Electrónica) ===
        add(Product("p5-1", "store-5", "Cable eléctrico 14 AWG", "Cable THW 14 AWG, metro", "Electrónica", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 200, "metro", null, null, null, tags = listOf("cable", "eléctrico")))
        add(Product("p5-2", "store-5", "Cable eléctrico 12 AWG", "Cable THW 12 AWG, metro", "Electrónica", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 150, "metro", null, null, null, tags = listOf("cable", "eléctrico")))
        add(Product("p5-3", "store-5", "Toma corriente doble", "Toma 110V doble con placa", "Electrónica", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 40, "unidad", null, null, null, tags = listOf("toma", "eléctrico")))
        add(Product("p5-4", "store-5", "Interruptor sencillo", "Interruptor de pared sencillo", "Electrónica", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 45, "unidad", null, null, null, tags = listOf("interruptor", "eléctrico")))
        add(Product("p5-5", "store-5", "Bombillo LED 9W", "Bombillo LED 9W luz cálida, E27", "Electrónica", "producto", 220.0, 0.9, null, "CUP", "disponible", false, null, null, null, null, null, 60, "unidad", null, null, null, tags = listOf("bombillo", "LED")))
        add(Product("p5-6", "store-5", "Bombillo LED 15W", "Bombillo LED 15W luz blanca, E27", "Electrónica", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 50, "unidad", null, null, null, tags = listOf("bombillo", "LED")))
        add(Product("p5-7", "store-5", "Lámpara LED 20W", "Lámpara LED tipo panel 20W", "Electrónica", "producto", 850.0, 3.5, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("lámpara", "LED")))
        add(Product("p5-8", "store-5", "Cargador USB 2A", "Cargador pared USB 2A + cable", "Electrónica", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 35, "unidad", null, null, null, tags = listOf("cargador", "USB")))
        add(Product("p5-9", "store-5", "Cable USB tipo C", "Cable USB-C a USB-A, 1m", "Electrónica", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 40, "unidad", null, null, null, tags = listOf("cable", "USB-C")))
        add(Product("p5-10", "store-5", "Cable Lightning", "Cable Lightning, 1m certificado", "Electrónica", "producto", 420.0, 1.8, null, "CUP", "disponible", false, null, null, null, null, null, 30, "unidad", null, null, null, tags = listOf("cable", "iPhone")))
        add(Product("p5-11", "store-5", "Pila AA", "Pila alcalina AA, 2u", "Electrónica", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 50, "blister", null, null, null, tags = listOf("pila", "AA")))
        add(Product("p5-12", "store-5", "Pila AAA", "Pila alcalina AAA, 2u", "Electrónica", "producto", 220.0, 0.9, null, "CUP", "disponible", false, null, null, null, null, null, 50, "blister", null, null, null, tags = listOf("pila", "AAA")))
        add(Product("p5-13", "store-5", "Batería 9V", "Batería 9V cuadrada", "Electrónica", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 25, "unidad", null, null, null, tags = listOf("pila", "9V")))
        add(Product("p5-14", "store-5", "Extensión 3 tomas", "Extensión 3 tomas 110V, 5m", "Electrónica", "producto", 550.0, 2.3, null, "CUP", "disponible", false, null, null, null, null, null, 22, "unidad", null, null, null, tags = listOf("extensión", "eléctrico")))
        add(Product("p5-15", "store-5", "Regulador voltaje", "Regulador 110V 1000W", "Electrónica", "producto", 1800.0, 7.5, null, "CUP", "disponible", false, null, null, null, null, null, 10, "unidad", null, null, null, tags = listOf("regulador", "protección")))
        add(Product("p5-16", "store-5", "Auriculares alámbricos", "Auriculares con micrófono, 3.5mm", "Electrónica", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 30, "unidad", null, null, null, tags = listOf("audio", "auriculares")))
        add(Product("p5-17", "store-5", "Auriculares Bluetooth", "Auriculares inalámbricos TWS", "Electrónica", "producto", 1200.0, 5.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("audio", "bluetooth")))

        // === TIENDA 6: Carnicería El Buen Corte (17 productos Carnes) ===
        add(Product("p6-1", "store-6", "Bistec de res", "Bistec de primera, 1kg", "Carnes", "producto", 650.0, 2.7, null, "CUP", "disponible", false, null, null, null, null, null, 25, "kg", null, null, null, tags = listOf("res", "bistec")))
        add(Product("p6-2", "store-6", "Carne molida", "Carne molida de res, 1kg", "Carnes", "producto", 550.0, 2.3, null, "CUP", "disponible", false, null, null, null, null, null, 30, "kg", null, null, null, tags = listOf("res", "molida")))
        add(Product("p6-3", "store-6", "Costilla de cerdo", "Costilla de cerdo fresca, 1kg", "Carnes", "producto", 480.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 35, "kg", null, null, null, tags = listOf("cerdo", "costilla")))
        add(Product("p6-4", "store-6", "Lomo de cerdo", "Lomo entero de cerdo, 1kg", "Carnes", "producto", 520.0, 2.2, null, "CUP", "disponible", false, null, null, null, null, null, 28, "kg", null, null, null, tags = listOf("cerdo", "lomo")))
        add(Product("p6-5", "store-6", "Pechuga de pollo", "Pechuga deshuesada, 1kg", "Carnes", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 40, "kg", null, null, null, tags = listOf("pollo", "pechuga")))
        add(Product("p6-6", "store-6", "Muslo de pollo", "Muslo con piel, 1kg", "Carnes", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 45, "kg", null, null, null, tags = listOf("pollo", "muslo")))
        add(Product("p6-7", "store-6", "Ala de pollo", "Ala completa, 1kg", "Carnes", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 50, "kg", null, null, null, tags = listOf("pollo", "ala")))
        add(Product("p6-8", "store-6", "Hígado de res", "Hígado fresco, 1kg", "Carnes", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 20, "kg", null, null, null, tags = listOf("res", "víscera")))
        add(Product("p6-9", "store-6", "Lengua de res", "Lengua de res, 1kg", "Carnes", "producto", 500.0, 2.1, null, "CUP", "disponible", false, null, null, null, null, null, 15, "kg", null, null, null, tags = listOf("res", "especial")))
        add(Product("p6-10", "store-6", "Chuleta de cerdo", "Chuleta de cerdo, 1kg", "Carnes", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 32, "kg", null, null, null, tags = listOf("cerdo", "chuleta")))
        add(Product("p6-11", "store-6", "Panceta", "Panceta ahumada, 1kg", "Carnes", "producto", 580.0, 2.4, null, "CUP", "disponible", false, null, null, null, null, null, 18, "kg", null, null, null, tags = listOf("cerdo", "ahumado")))
        add(Product("p6-12", "store-6", "Salchicha fresca", "Salchicha artesanal, 1kg", "Carnes", "producto", 620.0, 2.6, null, "CUP", "disponible", false, null, null, null, null, null, 22, "kg", null, null, null, tags = listOf("embutido", "artesanal")))
        add(Product("p6-13", "store-6", "Jamón vela", "Jamón vela ahumado, 1kg", "Carnes", "producto", 750.0, 3.1, null, "CUP", "disponible", false, null, null, null, null, null, 16, "kg", null, null, null, tags = listOf("embutido", "ahumado")))
        add(Product("p6-14", "store-6", "Pavo entero", "Pavo fresco, 1kg", "Carnes", "producto", 550.0, 2.3, null, "CUP", "disponible", false, null, null, null, null, null, 20, "kg", null, null, null, tags = listOf("pavo", "especial")))
        add(Product("p6-15", "store-6", "Conejo", "Conejo fresco, 1kg", "Carnes", "producto", 480.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "kg", null, null, null, tags = listOf("conejo", "especial")))
        add(Product("p6-16", "store-6", "Chorizo", "Chorizo criollo, 1kg", "Carnes", "producto", 680.0, 2.8, null, "CUP", "disponible", false, null, null, null, null, null, 20, "kg", null, null, null, tags = listOf("embutido", "criollo")))
        add(Product("p6-17", "store-6", "Morcilla", "Morcilla artesanal, 1kg", "Carnes", "producto", 520.0, 2.2, null, "CUP", "disponible", false, null, null, null, null, null, 18, "kg", null, null, null, tags = listOf("embutido", "artesanal")))

        // === TIENDA 7: Despensa Santa Rosa (17 productos Alimentos) ===
        add(Product("p7-1", "store-7", "Spaghetti", "Spaghetti importado, 500g", "Alimentos", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 40, "paquete", null, null, null, tags = listOf("pasta", "importado")))
        add(Product("p7-2", "store-7", "Macarrones", "Macarrones, 500g", "Alimentos", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 35, "paquete", null, null, null, tags = listOf("pasta", "básico")))
        add(Product("p7-3", "store-7", "Salsa de tomate", "Salsa lista para pasta, 500g", "Alimentos", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 45, "frasco", null, null, null, tags = listOf("salsa", "pasta")))
        add(Product("p7-4", "store-7", "Mayonesa", "Mayonesa, 250g", "Alimentos", "producto", 300.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 38, "frasco", null, null, null, tags = listOf("salsa", "ensalada")))
        add(Product("p7-5", "store-7", "Ketchup", "Ketchup, 400g", "Alimentos", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 42, "frasco", null, null, null, tags = listOf("salsa", "aderezo")))
        add(Product("p7-6", "store-7", "Mostaza", "Mostaza americana, 200g", "Alimentos", "producto", 220.0, 0.9, null, "CUP", "disponible", false, null, null, null, null, null, 35, "frasco", null, null, null, tags = listOf("salsa", "aderezo")))
        add(Product("p7-7", "store-7", "Vinagre", "Vinagre blanco, 1L", "Alimentos", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 50, "litro", null, null, null, tags = listOf("condimento", "conserva")))
        add(Product("p7-8", "store-7", "Salsa soya", "Salsa soya, 200ml", "Alimentos", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 30, "frasco", null, null, null, tags = listOf("salsa", "asiática")))
        add(Product("p7-9", "store-7", "Chorizo español", "Chorizo español, 250g", "Alimentos", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 22, "paquete", null, null, null, tags = listOf("embutido", "español")))
        add(Product("p7-10", "store-7", "Atún enlatado", "Atún en aceite, 170g", "Alimentos", "producto", 520.0, 2.2, null, "CUP", "disponible", false, null, null, null, null, null, 28, "lata", null, null, null, tags = listOf("pescado", "enlatado")))
        add(Product("p7-11", "store-7", "Sardinas", "Sardinas en salsa de tomate, 170g", "Alimentos", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 32, "lata", null, null, null, tags = listOf("pescado", "enlatado")))
        add(Product("p7-12", "store-7", "Galletas saladas", "Galletas crackers, 200g", "Alimentos", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 55, "paquete", null, null, null, tags = listOf("galleta", "snack")))
        add(Product("p7-13", "store-7", "Galletas dulces", "Galletas de vainilla, 300g", "Alimentos", "producto", 200.0, 0.85, null, "CUP", "disponible", false, null, null, null, null, null, 50, "paquete", null, null, null, tags = listOf("galleta", "dulce")))
        add(Product("p7-14", "store-7", "Chocolate", "Chocolate en barra, 100g", "Alimentos", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 30, "unidad", null, null, null, tags = listOf("dulce", "chocolate")))
        add(Product("p7-15", "store-7", "Refresco cola", "Refresco cola, 2L", "Alimentos", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 40, "botella", null, null, null, tags = listOf("refresco", "bebida")))
        add(Product("p7-16", "store-7", "Refresco naranja", "Refresco naranja, 2L", "Alimentos", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 40, "botella", null, null, null, tags = listOf("refresco", "bebida")))
        add(Product("p7-17", "store-7", "Agua mineral", "Agua mineral natural, 1.5L", "Alimentos", "producto", 150.0, 0.6, null, "CUP", "disponible", false, null, null, null, null, null, 60, "botella", null, null, null, tags = listOf("agua", "bebida")))

        // === TIENDA 8: Ferretería La Herradura (17 productos Ferretería) ===
        add(Product("p8-1", "store-8", "Cerradura puerta", "Cerradura sobreponer, 2 vueltas", "Ferretería", "producto", 850.0, 3.5, null, "CUP", "disponible", false, null, null, null, null, null, 12, "unidad", null, null, null, tags = listOf("cerradura", "seguridad")))
        add(Product("p8-2", "store-8", "Bisagra 4\"", "Bisagra piana 4\" acero, 2u", "Ferretería", "producto", 280.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 25, "paquete", null, null, null, tags = listOf("bisagra", "puerta")))
        add(Product("p8-3", "store-8", "Bisagra 3\"", "Bisagra piana 3\" acero, 2u", "Ferretería", "producto", 220.0, 0.9, null, "CUP", "disponible", false, null, null, null, null, null, 28, "paquete", null, null, null, tags = listOf("bisagra", "puerta")))
        add(Product("p8-4", "store-8", "Tirador", "Tirador de puerta cromado", "Ferretería", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("tirador", "puerta")))
        add(Product("p8-5", "store-8", "Cadena puerta", "Cadena de seguridad puerta", "Ferretería", "producto", 180.0, 0.75, null, "CUP", "disponible", false, null, null, null, null, null, 30, "unidad", null, null, null, tags = listOf("cadena", "seguridad")))
        add(Product("p8-6", "store-8", "Pomo puerta", "Pomo redondo cromado", "Ferretería", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 22, "unidad", null, null, null, tags = listOf("pomo", "puerta")))
        add(Product("p8-7", "store-8", "Escuadra 12\"", "Escuadra carpintero 12\"", "Ferretería", "producto", 450.0, 1.9, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("escuadra", "medición")))
        add(Product("p8-8", "store-8", "Serrucho", "Serrucho carpintero 20\"", "Ferretería", "producto", 750.0, 3.1, null, "CUP", "disponible", false, null, null, null, null, null, 10, "unidad", null, null, null, tags = listOf("serrucho", "carpintería")))
        add(Product("p8-9", "store-8", "Formón 1\"", "Formón mango madera 1\"", "Ferretería", "producto", 380.0, 1.6, null, "CUP", "disponible", false, null, null, null, null, null, 18, "unidad", null, null, null, tags = listOf("formón", "carpintería")))
        add(Product("p8-10", "store-8", "Cincel", "Cincel frío 1/2\" x 6\"", "Ferretería", "producto", 420.0, 1.8, null, "CUP", "disponible", false, null, null, null, null, null, 16, "unidad", null, null, null, tags = listOf("cincel", "herramienta")))
        add(Product("p8-11", "store-8", "Pinza punta", "Pinza punta larga 6\"", "Ferretería", "producto", 320.0, 1.3, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("pinza", "herramienta")))
        add(Product("p8-12", "store-8", "Pinza corte", "Pinza corte diagonal 6\"", "Ferretería", "producto", 300.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 22, "unidad", null, null, null, tags = listOf("pinza", "herramienta")))
        add(Product("p8-13", "store-8", "Llave ajustable", "Llave ajustable 10\"", "Ferretería", "producto", 580.0, 2.4, null, "CUP", "disponible", false, null, null, null, null, null, 14, "unidad", null, null, null, tags = listOf("llave", "herramienta")))
        add(Product("p8-14", "store-8", "Llave fija 14-15", "Llave fija combinada 14-15mm", "Ferretería", "producto", 350.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, 18, "unidad", null, null, null, tags = listOf("llave", "herramienta")))
        add(Product("p8-15", "store-8", "Llave tubo", "Llave de tubo 12\"", "Ferretería", "producto", 620.0, 2.6, null, "CUP", "disponible", false, null, null, null, null, null, 12, "unidad", null, null, null, tags = listOf("llave", "herramienta")))
        add(Product("p8-16", "store-8", "Manguera jardín", "Manguera PVC 1/2\" x 10m", "Ferretería", "producto", 950.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, 10, "rollo", null, null, null, tags = listOf("manguera", "jardín")))
        add(Product("p8-17", "store-8", "Pistola riego", "Pistola riego 7 funciones", "Ferretería", "producto", 480.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("riego", "jardín")))

        // === TIENDA 9: Belleza Tropical (17 servicios Belleza) ===
        add(Product("p9-1", "store-9", "Corte de cabello mujer", "Corte, lavado y peinado", "Belleza", "servicio", 800.0, 3.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("cabello", "corte")))
        add(Product("p9-2", "store-9", "Corte de cabello hombre", "Corte clásico o moderno", "Belleza", "servicio", 500.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "20-30 min", tags = listOf("cabello", "corte")))
        add(Product("p9-3", "store-9", "Tinte completo", "Tinte permanente + lavado + peinado", "Belleza", "servicio", 1500.0, 6.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "90-120 min", tags = listOf("cabello", "tinte")))
        add(Product("p9-4", "store-9", "Mechas", "Mechas parciales o completas", "Belleza", "servicio", 1200.0, 5.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-90 min", tags = listOf("cabello", "mechas")))
        add(Product("p9-5", "store-9", "Alisado keratina", "Tratamiento alisado con keratina", "Belleza", "servicio", 2500.0, 10.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "120-180 min", tags = listOf("cabello", "alisado")))
        add(Product("p9-6", "store-9", "Manicure básico", "Limpieza, corte, esmaltado", "Belleza", "servicio", 400.0, 1.5, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("uñas", "manicure")))
        add(Product("p9-7", "store-9", "Manicure gel", "Manicure con esmalte semipermanente", "Belleza", "servicio", 700.0, 2.8, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("uñas", "gel")))
        add(Product("p9-8", "store-9", "Pedicure básico", "Limpieza, corte, esmaltado pies", "Belleza", "servicio", 500.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("uñas", "pedicure")))
        add(Product("p9-9", "store-9", "Pedicure spa", "Pedicure + exfoliación + masaje", "Belleza", "servicio", 900.0, 3.5, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-75 min", tags = listOf("uñas", "spa")))
        add(Product("p9-10", "store-9", "Facial limpieza profunda", "Limpieza facial + exfoliación + mascarilla", "Belleza", "servicio", 1000.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-90 min", tags = listOf("rostro", "facial")))
        add(Product("p9-11", "store-9", "Facial hidratación", "Hidratación intensiva facial", "Belleza", "servicio", 800.0, 3.2, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("rostro", "hidratación")))
        add(Product("p9-12", "store-9", "Depilación ceja", "Diseño y depilación de cejas", "Belleza", "servicio", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "15-20 min", tags = listOf("rostro", "cejas")))
        add(Product("p9-13", "store-9", "Maquillaje social", "Maquillaje para eventos sociales", "Belleza", "servicio", 1200.0, 5.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("rostro", "maquillaje")))
        add(Product("p9-14", "store-9", "Maquillaje novia", "Maquillaje bridal + prueba", "Belleza", "servicio", 2500.0, 10.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Previa cita", "90-120 min", tags = listOf("rostro", "novia")))
        add(Product("p9-15", "store-9", "Masaje relajante", "Masaje corporal relajante 60min", "Belleza", "servicio", 1500.0, 6.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60 min", tags = listOf("cuerpo", "masaje")))
        add(Product("p9-16", "store-9", "Masaje descontracturante", "Masaje terapéutico 60min", "Belleza", "servicio", 1800.0, 7.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60 min", tags = listOf("cuerpo", "terapéutico")))
        add(Product("p9-17", "store-9", "Tratamiento capilar", "Hidratación + keratina capilar", "Belleza", "servicio", 1000.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("cabello", "tratamiento")))

        // === TIENDA 10: Reparaciones Rápidas (17 servicios Reparaciones) ===
        add(Product("p10-1", "store-10", "Reparación celular pantalla", "Cambio pantalla LCD/OLED", "Reparaciones", "servicio", 3500.0, 15.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-60 min", tags = listOf("celular", "pantalla")))
        add(Product("p10-2", "store-10", "Reparación celular batería", "Cambio batería original", "Reparaciones", "servicio", 2500.0, 10.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "20-30 min", tags = listOf("celular", "batería")))
        add(Product("p10-3", "store-10", "Reparación celular puerto", "Cambio puerto de carga", "Reparaciones", "servicio", 1800.0, 7.5, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("celular", "carga")))
        add(Product("p10-4", "store-10", "Reparación celular software", "Flasheo, liberación, backup", "Reparaciones", "servicio", 1000.0, 4.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-60 min", tags = listOf("celular", "software")))
        add(Product("p10-5", "store-10", "Reparación laptop pantalla", "Cambio pantalla laptop", "Reparaciones", "servicio", 4500.0, 18.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-90 min", tags = listOf("laptop", "pantalla")))
        add(Product("p10-6", "store-10", "Reparación laptop teclado", "Cambio teclado laptop", "Reparaciones", "servicio", 2800.0, 11.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("laptop", "teclado")))
        add(Product("p10-7", "store-10", "Reparación laptop batería", "Cambio batería laptop", "Reparaciones", "servicio", 3200.0, 13.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "20-30 min", tags = listOf("laptop", "batería")))
        add(Product("p10-8", "store-10", "Mantenimiento laptop", "Limpieza interna + pasta térmica", "Reparaciones", "servicio", 1500.0, 6.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("laptop", "mantenimiento")))
        add(Product("p10-9", "store-10", "Reparación TV LED", "Diagnóstico + reparación TV LED", "Reparaciones", "servicio", 2000.0, 8.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-120 min", tags = listOf("TV", "electrónica")))
        add(Product("p10-10", "store-10", "Reparación nevera", "Reparación compresor + gas", "Reparaciones", "servicio", 3000.0, 12.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-90 min", tags = listOf("electrodoméstico", "nevera")))
        add(Product("p10-11", "store-10", "Reparación lavadora", "Reparación mecánica + eléctrica", "Reparaciones", "servicio", 2500.0, 10.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-90 min", tags = listOf("electrodoméstico", "lavadora")))
        add(Product("p10-12", "store-10", "Reparación ventilador", "Cambio capacitor + rodamientos", "Reparaciones", "servicio", 800.0, 3.2, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("electrodoméstico", "ventilador")))
        add(Product("p10-13", "store-10", "Instalación eléctrica básica", "Instalación tomas + interruptores", "Reparaciones", "servicio", 1500.0, 6.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-120 min", tags = listOf("eléctrico", "instalación")))
        add(Product("p10-14", "store-10", "Reparación cerradura", "Cambio mecanismo cerradura", "Reparaciones", "servicio", 600.0, 2.5, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("cerrajería", "seguridad")))
        add(Product("p10-15", "store-10", "Instalación ventana", "Instalación o reparación ventana", "Reparaciones", "servicio", 2000.0, 8.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "60-120 min", tags = listOf("carpintería", "ventana")))
        add(Product("p10-16", "store-10", "Reparación bicicleta", "Ajuste frenos + cambios + lubricación", "Reparaciones", "servicio", 500.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "30-45 min", tags = listOf("bici", "mecánica")))
        add(Product("p10-17", "store-10", "Reparación moto eléctrica", "Diagnóstico batería + motor", "Reparaciones", "servicio", 1200.0, 5.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Sab", "45-60 min", tags = listOf("moto", "eléctrica")))

        // === TIENDA 11: Vivero Comunitario Santa Elena (10 items Vivero) ===
        add(Product("p11-1", "store-11", "Planta ornamental pequeña", "Suculentas, cactus, pequeñas", "Vivero", "producto", 150.0, 0.6, null, "CUP", "disponible", false, null, null, null, null, null, 30, "unidad", null, null, null, tags = listOf("planta", "ornamental")))
        add(Product("p11-2", "store-11", "Planta ornamental mediana", "Palmas, helechos, ficus", "Vivero", "producto", 350.0, 1.4, null, "CUP", "disponible", false, null, null, null, null, null, 20, "unidad", null, null, null, tags = listOf("planta", "interior")))
        add(Product("p11-3", "store-11", "Planta frutal", "Mango, limón, naranja, aguacate (pequeña)", "Vivero", "producto", 250.0, 1.0, null, "CUP", "disponible", false, null, null, null, null, null, 15, "unidad", null, null, null, tags = listOf("frutal", "árbol")))
        add(Product("p11-4", "store-11", "Semillas variadas", "Semillas de hortalizas, 10g", "Vivero", "producto", 80.0, 0.3, null, "CUP", "disponible", false, null, null, null, null, null, 50, "sobre", null, null, null, tags = listOf("semillas", "huerto")))
        add(Product("p11-5", "store-11", "Sustrato orgánico", "Tierra compostada, 5kg", "Vivero", "producto", 200.0, 0.8, null, "CUP", "disponible", false, null, null, null, null, null, 40, "saco", null, null, null, tags = listOf("sustrato", "compost")))
        add(Product("p11-6", "store-11", "Compost casero", "Abono orgánico, 2kg", "Vivero", "producto", 120.0, 0.5, null, "CUP", "disponible", false, null, null, null, null, null, 35, "saco", null, null, null, tags = listOf("compost", "abono")))
        add(Product("p11-7", "store-11", "Asesoría jardinería", "Visita a domicilio + plan de jardín", "Vivero", "servicio", 500.0, 2.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Lun-Vie", "60 min", tags = listOf("asesoría", "jardín")))
        add(Product("p11-8", "store-11", "Taller compostaje", "Taller comunitario mensual de compostaje", "Vivero", "servicio", 0.0, 0.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Sábados", "120 min", tags = listOf("taller", "gratuito")))
        add(Product("p11-9", "store-11", "Taller huerto urbano", "Aprende a cultivar en espacios pequeños", "Vivero", "servicio", 0.0, 0.0, null, "CUP", "disponible", false, null, null, null, null, null, null, null, "Sábados", "120 min", tags = listOf("taller", "gratuito")))
        add(Product("p11-10", "store-11", "Maceta artesanal", "Maceta de barro hecha a mano, mediana", "Vivero", "producto", 300.0, 1.2, null, "CUP", "disponible", false, null, null, null, null, null, 25, "unidad", null, null, null, tags = listOf("maceta", "artesanal")))
    }

    val defaultLocation: UserLocation = UserLocation(
        id = 1,
        latitud = 21.3833,      // Centro aproximado de Camagüey
        longitud = -77.9167,
        zona = "Centro Histórico",
        direccionTexto = "Camagüey, Cuba",
        modoObtencion = "default"
    )
}
