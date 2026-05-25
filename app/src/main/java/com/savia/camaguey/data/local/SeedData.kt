package com.savia.camaguey.data.local

import com.savia.camaguey.data.model.*
import com.savia.camaguey.util.Constants

object SeedData {

    suspend fun populateDatabase(db: SaviaDatabase) {
        if (db.storeDao().count() > 0) return

        val stores = generateStores()
        db.storeDao().insertAll(stores)

        val products = generateProducts(stores)
        db.productDao().insertAll(products)

        val admins = generateAdmins()
        db.adminAccountDao().insertAll(admins)
    }

    private fun generateStores(): List<Store> {
        val now = System.currentTimeMillis()
        return listOf(
            Store(
                id = "store_001", idInterno = "CMP-00001", nombrePublico = "La Bodega de Pepe",
                tipoEntidad = Constants.TYPE_TCP, zona = "La Caridad",
                direccion = "Calle Maceo #45 e/ Libertad y Agramonte", latitud = 21.3769, longitud = -77.9172,
                telefono = "+53551234567", horario = "Lun-Dom 8:00-20:00",
                entregaDisponible = true, radioEntregaKm = 3, webUrl = "https://bodegapepe.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "bodegapepe", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53551234567", permiteReservas = false
            ),
            Store(
                id = "store_002", idInterno = "CMP-00002", nombrePublico = "El Mercadito de María",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "La Caridad",
                direccion = "Av. de los Mártires #112", latitud = 21.3775, longitud = -77.9165,
                telefono = "+53552345678", horario = "Lun-Sab 7:00-19:00",
                entregaDisponible = true, radioEntregaKm = 2,
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "mercaditomaria", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53552345678"
            ),
            Store(
                id = "store_003", idInterno = "CMP-00003", nombrePublico = "Ferretería El Clavo",
                tipoEntidad = Constants.TYPE_TCP, zona = "Centro Histórico",
                direccion = "Calle Ignacio Agramonte #78", latitud = 21.3834, longitud = -77.9181,
                telefono = "+53553456789", horario = "Lun-Sab 8:00-17:00",
                entregaDisponible = false, radioEntregaKm = 0,
                webUrl = "https://ferreclavo.ejemplo.cu",
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Ferretería",
                username = "ferreclavo", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53553456789"
            ),
            Store(
                id = "store_004", idInterno = "CMP-00004", nombrePublico = "La Casa del Aseo",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Centro Histórico",
                direccion = "Calle República #234", latitud = 21.3840, longitud = -77.9175,
                telefono = "+53554567890", horario = "Lun-Dom 9:00-18:00",
                entregaDisponible = true, radioEntregaKm = 4,
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Aseo",
                username = "casadelaseo", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53554567890"
            ),
            Store(
                id = "store_005", idInterno = "CMP-00005", nombrePublico = "Electrosur",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Vista Hermosa",
                direccion = "Calle 3ra #456 e/ 8 y 10", latitud = 21.3720, longitud = -77.9100,
                telefono = "+53555678901", horario = "Lun-Sab 9:00-18:00",
                entregaDisponible = true, radioEntregaKm = 5,
                webUrl = "https://electrosur.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Electrónica",
                username = "electrosur", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53555678901"
            ),
            Store(
                id = "store_006", idInterno = "CMP-00006", nombrePublico = "Carnicería El Buen Corte",
                tipoEntidad = Constants.TYPE_TCP, zona = "Santa Rosa",
                direccion = "Calle 5ta #89", latitud = 21.3890, longitud = -77.9050,
                telefono = "+53556789012", horario = "Lun-Sab 6:00-14:00",
                entregaDisponible = true, radioEntregaKm = 2,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Carnes",
                username = "buencorte", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53556789012"
            ),
            Store(
                id = "store_007", idInterno = "CMP-00007", nombrePublico = "Despensa Santa Rosa",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Santa Rosa",
                direccion = "Av. Santa Rosa #301", latitud = 21.3880, longitud = -77.9060,
                telefono = "+53557890123", horario = "Lun-Dom 7:00-21:00",
                entregaDisponible = true, radioEntregaKm = 3,
                webUrl = "https://despensasantarosa.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = true, categoriaPrincipal = "Alimentos",
                username = "despensasantarosa", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53557890123"
            ),
            Store(
                id = "store_008", idInterno = "CMP-00008", nombrePublico = "Ferretería La Herradura",
                tipoEntidad = Constants.TYPE_TCP, zona = "Centro Histórico",
                direccion = "Calle Cisneros #55", latitud = 21.3825, longitud = -77.9190,
                telefono = "+53558901234", horario = "Lun-Sab 8:00-17:00",
                entregaDisponible = false, radioEntregaKm = 0,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = now,
                verificado = true, destacado = false, categoriaPrincipal = "Ferretería",
                username = "herradura", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53558901234"
            ),
            Store(
                id = "store_009", idInterno = "CMP-00009", nombrePublico = "Belleza Tropical",
                tipoEntidad = Constants.TYPE_MIPYME, zona = "Vista Hermosa",
                direccion = "Calle 4ta #202", latitud = 21.3730, longitud = -77.9110,
                telefono = "+53559012345", horario = "Mar-Dom 10:00-19:00",
                entregaDisponible = true, radioEntregaKm = 4,
                webUrl = "https://bellezatropical.ejemplo.cu",
                planSuscripcion = Constants.PLAN_FEATURED, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = true, categoriaPrincipal = "Servicios",
                username = "bellezatropical", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53559012345"
            ),
            Store(
                id = "store_010", idInterno = "CMP-00010", nombrePublico = "Reparaciones Rápidas",
                tipoEntidad = Constants.TYPE_TCP, zona = "La Caridad",
                direccion = "Calle 10 de Octubre #12", latitud = 21.3780, longitud = -77.9150,
                telefono = "+53550123456", horario = "Lun-Sab 8:00-18:00",
                entregaDisponible = true, radioEntregaKm = 6,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = false, categoriaPrincipal = "Servicios",
                username = "reparaciones", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53550123456"
            ),
            Store(
                id = "store_011", idInterno = "CMP-00011", nombrePublico = "Vivero Comunitario Santa Elena",
                tipoEntidad = Constants.TYPE_PDL, zona = "Santa Elena",
                direccion = "Calle Santa Elena #77", latitud = 21.3750, longitud = -77.9200,
                telefono = "+53551234999", horario = "Lun-Sab 8:00-16:00",
                entregaDisponible = true, radioEntregaKm = 3,
                planSuscripcion = Constants.PLAN_BASIC, suscripcionActiva = true,
                fechaRegistro = now, ultimaConfirmacionStock = null,
                verificado = true, destacado = false, categoriaPrincipal = "Servicios",
                username = "viverosantaelena", passwordHash = "\$2a\$10\$placeholder",
                telefonoRecuperacion = "+53551234999"
            )
        )
    }

    private fun generateProducts(stores: List<Store>): List<Product> {
        val now = System.currentTimeMillis()
        val products = mutableListOf<Product>()
        var productId = 1

        val bodega = stores[0]
        val bodegaProducts = listOf(
            "Arroz" to 180.0, "Frijoles" to 250.0, "Aceite" to 450.0, "Azúcar" to 120.0,
            "Harina" to 90.0, "Leche" to 65.0, "Huevos (30u)" to 450.0, "Pollo (kg)" to 380.0,
            "Pasta" to 85.0, "Sal" to 25.0, "Café" to 320.0, "Spaghetti" to 95.0,
            "Sardinas" to 180.0, "Galletas" to 120.0, "Mantequilla" to 280.0
        )
        bodegaProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = bodega.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val mercadito = stores[1]
        val mercaditoProducts = listOf(
            "Arroz premium" to 220.0, "Frijoles negros" to 280.0, "Aceite de oliva" to 650.0,
            "Azúcar blanca" to 140.0, "Harina de trigo" to 100.0, "Leche condensada" to 120.0,
            "Huevos (12u)" to 200.0, "Pavo (kg)" to 520.0, "Pasta corta" to 95.0,
            "Sal marina" to 40.0, "Café molido" to 380.0, "Spaghetti integral" to 110.0,
            "Atún" to 250.0, "Galletas saladas" to 140.0, "Mantequilla sin sal" to 300.0,
            "Queso blanco" to 350.0, "Yogurt" to 85.0
        )
        mercaditoProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = mercadito.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val ferre = stores[2]
        val ferreProducts = listOf(
            "Martillo" to 350.0, "Clavos (kg)" to 180.0, "Destornillador" to 120.0,
            "Cinta métrica" to 95.0, "Alicate" to 220.0, "Broca" to 85.0,
            "Silicona" to 150.0, "Pintura blanca" to 450.0, "Brocha" to 80.0,
            "Cable eléctrico (m)" to 65.0, "Enchufe" to 45.0, "Bombillo LED" to 120.0,
            "Cerradura" to 380.0, "Bisagras (par)" to 95.0, "Lija" to 35.0,
            "Tornillos (caja)" to 75.0, "Guantes trabajo" to 140.0
        )
        ferreProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = ferre.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Ferretería"
            ))
        }

        val aseo = stores[3]
        val aseoProducts = listOf(
            "Detergente" to 250.0, "Jabón de baño" to 65.0, "Shampoo" to 180.0,
            "Acondicionador" to 190.0, "Pasta dental" to 120.0, "Cepillo dental" to 85.0,
            "Papel higiénico" to 150.0, "Servilletas" to 95.0, "Desinfectante" to 220.0,
            "Limpiador multiuso" to 180.0, "Esponja" to 35.0, "Cloro" to 140.0,
            "Suavizante" to 200.0, "Jabón líquido" to 160.0, "Toallas húmedas" to 110.0,
            "Ambientador" to 130.0, "Bolsa basura" to 75.0
        )
        aseoProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = aseo.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Aseo"
            ))
        }

        val electro = stores[4]
        val electroProducts = listOf(
            "Cable HDMI" to 450.0, "Mouse USB" to 350.0, "Teclado" to 650.0,
            "Pendrive 32GB" to 380.0, "Batería AA (par)" to 120.0, "Cargador universal" to 550.0,
            "Auriculares" to 280.0, "Hub USB" to 220.0, "Adaptador corriente" to 180.0,
            "Batería 9V" to 95.0, "Cable red (m)" to 85.0, "Interruptor wifi" to 750.0,
            "Regleta" to 320.0, "Linterna LED" to 180.0, "Pila recargable" to 250.0,
            "Cable USB-C" to 150.0, "Adaptador OTG" to 120.0
        )
        electroProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = electro.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Electrónica"
            ))
        }

        val carne = stores[5]
        val carneProducts = listOf(
            "Bistec (kg)" to 850.0, "Carne molida (kg)" to 780.0, "Pollo entero" to 520.0,
            "Chuleta (kg)" to 920.0, "Costilla (kg)" to 680.0, "Hígado (kg)" to 450.0,
            "Pechuga (kg)" to 950.0, "Alitas (kg)" to 380.0, "Pierna (kg)" to 720.0,
            "Filete pescado (kg)" to 650.0, "Salchichas (paq)" to 280.0, "Jamón (kg)" to 1200.0,
            "Tocino (kg)" to 580.0, "Morcilla (kg)" to 420.0, "Chorizo (kg)" to 750.0,
            "Lomo (kg)" to 1100.0, "Paletilla (kg)" to 680.0
        )
        carneProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = carne.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Carnes"
            ))
        }

        val despensa = stores[6]
        val despensaProducts = listOf(
            "Arroz" to 190.0, "Frijoles" to 260.0, "Aceite" to 460.0, "Azúcar" to 125.0,
            "Harina" to 95.0, "Leche" to 70.0, "Huevos (30u)" to 460.0, "Pollo (kg)" to 390.0,
            "Pasta" to 90.0, "Sal" to 28.0, "Café" to 330.0, "Spaghetti" to 100.0,
            "Sardinas" to 185.0, "Galletas" to 125.0, "Mantequilla" to 290.0,
            "Queso crema" to 320.0, "Mermelada" to 180.0
        )
        despensaProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = despensa.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Alimentos"
            ))
        }

        val herradura = stores[7]
        val herraduraProducts = listOf(
            "Martillo carpintero" to 380.0, "Clavos galvanizados (kg)" to 200.0, "Destornillador plano" to 130.0,
            "Cinta métrica 5m" to 110.0, "Alicate universal" to 240.0, "Broca metal" to 95.0,
            "Silicona caliente" to 160.0, "Pintura exterior" to 480.0, "Brocha 4"" to 90.0,
            "Cable coaxial (m)" to 75.0, "Tomacorriente" to 55.0, "Bombillo ahorrador" to 130.0,
            "Cerradura digital" to 420.0, "Bisagras grandes" to 110.0, "Lija agua" to 40.0,
            "Tornillos inox (caja)" to 85.0, "Guantes cuero" to 160.0
        )
        herraduraProducts.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = herradura.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_PRODUCT,
                enStock = true, ultimaEdicion = now, categoria = "Ferretería"
            ))
        }

        val belleza = stores[8]
        val bellezaServices = listOf(
            "Corte dama" to 350.0, "Corte caballero" to 250.0, "Tinte" to 650.0,
            "Manicure" to 280.0, "Pedicure" to 320.0, "Facial" to 450.0,
            "Maquillaje social" to 550.0, "Peinado evento" to 480.0, "Depilación cejas" to 120.0,
            "Tratamiento keratina" to 1200.0, "Uñas acrílicas" to 450.0, "Masaje relajante" to 500.0,
            "Limpieza facial profunda" to 380.0, "Alisado permanente" to 850.0, "Mechas" to 750.0,
            "Maquillaje novia" to 950.0, "Peinado trenzas" to 350.0
        )
        bellezaServices.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = belleza.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP, tipoItem = Constants.ITEM_SERVICE,
                enStock = true, porEncargo = true, ultimaEdicion = now, categoria = "Belleza"
            ))
        }

        val repar = stores[9]
        val reparServices = listOf(
            "Reparación celular pantalla" to 2500.0, "Reparación celular batería" to 1800.0,
            "Reparación celular puerto carga" to 1200.0, "Reparación laptop" to 3500.0,
            "Formateo PC" to 800.0, "Instalación antivirus" to 450.0,
            "Reparación nevera" to 2800.0, "Reparación lavadora" to 2200.0,
            "Reparación ventilador" to 650.0, "Reparación TV" to 3200.0,
            "Cambio módulo celular" to 4500.0, "Reparación tablet" to 2000.0,
            "Mantenimiento PC" to 600.0, "Recuperación datos" to 1500.0,
            "Reparación consola" to 3800.0, "Soldadura electrónica" to 850.0,
            "Diagnóstico gratuito" to 0.0
        )
        reparServices.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = repar.id, nombre = name, precio = price,
                moneda = if (price > 0) Constants.CURRENCY_CUP else Constants.CURRENCY_CUP,
                tipoItem = Constants.ITEM_SERVICE,
                enStock = true, porEncargo = true, ultimaEdicion = now, categoria = "Reparación"
            ))
        }

        val vivero = stores[10]
        val viveroItems = listOf(
            "Planta ornamental" to 250.0, "Semillas variadas" to 120.0,
            "Maceta mediana" to 180.0, "Tierra abonada" to 150.0,
            "Asesoría jardinería" to 500.0, "Compostaje casero" to 300.0,
            "Taller ambiental" to 200.0, "Planta medicinal" to 180.0,
            "Fertilizante orgánico" to 220.0, "Árbol frutal" to 450.0
        )
        viveroItems.forEach { (name, price) ->
            products.add(Product(
                id = "prod_${productId++.toString().padStart(5, '0')}",
                tiendaId = vivero.id, nombre = name, precio = price,
                moneda = Constants.CURRENCY_CUP,
                tipoItem = if (name.contains("Asesoría") || name.contains("Taller") || name.contains("Compostaje"))
                    Constants.ITEM_SERVICE else Constants.ITEM_PRODUCT,
                enStock = true, porEncargo = false, ultimaEdicion = now, categoria = "Jardinería"
            ))
        }

        return products
    }

    private fun generateAdmins(): List<AdminAccount> {
        return listOf(
            AdminAccount(
                username = "savia.admin1",
                passwordHash = "\$2a\$12\$placeholder_hash_admin1",
                rol = "admin",
                telefonoRecuperacion = "+53559998877"
            ),
            AdminAccount(
                username = "savia.admin2",
                passwordHash = "\$2a\$12\$placeholder_hash_admin2",
                rol = "admin",
                telefonoRecuperacion = "+53557776655"
            )
        )
    }
}
