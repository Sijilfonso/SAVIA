# 🌿 SAVIA — Camagüey Compra Local

> **Eslogan:** *"Camagüey crece aquí"*

Directorio geolocalizado de negocios, emprendimientos y proyectos de desarrollo local para Camagüey, Cuba.

---

## 🚀 Generar FASE 1 desde GitHub (sin Android Studio local)

### Opción A: GitHub Actions (Automático)
1. Sube `generate_savia_fase1.py` a la raíz de tu repo
2. Ve a **Actions → SAVIA — Generar FASE 1 → Run workflow**
3. El workflow generará toda la estructura Android y hará commit automático

### Opción B: GitHub Codespaces (Interactivo)
1. Abre tu repo en **GitHub Codespaces** (botón verde `<> Code` → Codespaces)
2. En la terminal:
   ```bash
   python3 generate_savia_fase1.py .
   ```
3. La estructura completa aparece en el explorador de archivos

### Opción C: Local
```bash
python3 generate_savia_fase1.py /ruta/donde/quieras/savia-android
```

---

## 📁 Estructura generada (FASE 1)

```
savia-android/
├── build.gradle                          # Project level
├── settings.gradle
├── gradle/wrapper/gradle-wrapper.properties
├── app/
│   ├── build.gradle                      # App level (deps: Room, OSMDroid, Glide, Retrofit, Timber)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml           # Permisos + WelcomeActivity launcher
│       ├── java/com/savia/camaguey/
│       │   ├── SaviaApplication.kt       # Room init + OSMDroid + SeedData
│       │   ├── ui/welcome/
│       │   │   └── WelcomeActivity.kt    # Pantalla de bienvenida (compilable)
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── Store.kt          # 11 negocios semilla (incluye 1 PDL)
│       │   │   │   ├── Product.kt        # ~190 productos/servicios
│       │   │   │   ├── CartItem.kt
│       │   │   │   ├── UserLocation.kt
│       │   │   │   ├── InteractionLog.kt
│       │   │   │   ├── AdminAccount.kt   # 2 cuentas admin seed
│       │   │   │   └── VisitStats.kt     # Estadísticas de visitas
│       │   │   ├── local/
│       │   │   │   ├── SaviaDatabase.kt  # Room DB abstract class
│       │   │   │   ├── SeedData.kt       # Datos semilla completos
│       │   │   │   └── dao/
│       │   │   │       ├── StoreDao.kt
│       │   │   │       ├── ProductDao.kt
│       │   │   │       ├── CartDao.kt
│       │   │   │       ├── UserLocationDao.kt
│       │   │   │       ├── InteractionLogDao.kt
│       │   │   │       ├── AdminAccountDao.kt
│       │   │   │       └── VisitStatsDao.kt
│       │   │   └── repository/
│       │   │       ├── StoreRepository.kt
│       │   │       ├── ProductRepository.kt
│       │   │       └── CartRepository.kt
│       │   └── util/
│       │       ├── Constants.kt          # Constantes de negocio + traducción PDL/MIPYME/TCP
│       │       ├── Haversine.kt          # Distancia GPS + formato
│       │       ├── PriceFormatter.kt     # CUP/USD con locale es-CU
│       │       └── PasswordValidator.kt  # Validación contraseñas admin/vendedor
│       └── res/
│           ├── values/
│           │   ├── colors.xml            # Paleta exacta #0D3B2E, #C89F3C, #F7F6F2
│           │   ├── themes.xml            # Material Components 1.9.0
│           │   ├── strings.xml           # Todos los textos de la app
│           │   ├── styles.xml            # Cards, Chips, Badges
│           │   └── dimens.xml            # Padding, corners, tipografía
│           ├── drawable/
│           │   ├── bg_badge_green.xml    # Stock reciente
│           │   ├── bg_badge_gold.xml     # Destacado
│           │   ├── bg_badge_red.xml      # Stock viejo
│           │   └── bg_badge_blue.xml     # Verificado
│           └── layout/
│               └── activity_welcome.xml  # Launcher compilable
```

---

## 🛠 Stack técnico

| Capa | Tecnología |
|------|-----------|
| **Lenguaje** | Kotlin |
| **UI** | XML Views tradicionales (NO Jetpack Compose) |
| **minSdk** | 21 (Android 5.0) |
| **targetSdk** | 34 |
| **Mapas** | OSMDroid (OpenStreetMap, NO Google Maps) |
| **Base de datos** | Room (SQLite nativo) |
| **Imágenes** | Glide |
| **Networking** | Retrofit2 + OkHttp3 |
| **Async** | Kotlin Coroutines + ViewModel |
| **Material** | Material Components 1.9.0 |

---

## ✅ FASE 1 — Checklist completado

- [x] `build.gradle` (app y project) con todas las dependencias
- [x] `AndroidManifest.xml` con permisos (GPS, Internet, Storage)
- [x] Room Database: 7 Entities + 7 DAOs + Database abstract class
- [x] Data classes: Store, Product, CartItem, UserLocation, InteractionLog, AdminAccount, VisitStats
- [x] 3 Repositories (Store, Product, Cart)
- [x] Utilidades: Haversine, PriceFormatter, Constants, PasswordValidator
- [x] Seed data: 11 negocios (10 + 1 PDL) + ~190 productos/servicios + 2 admins
- [x] `SaviaApplication.kt` (inicialización Room + Timber + OSMDroid)
- [x] `colors.xml` + `themes.xml` con paleta definida
- [x] Soporte PDL en tipo_entidad
- [x] Tabla VisitStats para estadísticas de visitas
- [x] AdminAccount para login unificado

---

## 📌 Próximas fases

| Fase | Contenido |
|------|-----------|
| **FASE 2** | UI Core + Navegación comprador (Home, Adapters, Bottom Nav) |
| **FASE 3** | Mapa OSMDroid, Perfil tienda, Carrito, Ruta óptima |
| **FASE 4** | Login unificado, Panel vendedor, Panel admin, Registro wizard |
| **FASE 5** | Backend Node.js + Express, Landing web, CI/CD GitHub Actions |

---

## 🤝 Contribuir

Este proyecto es **código abierto** para el desarrollo local de Camagüey. 
Las PRs son bienvenidas siguiendo las reglas del prompt maestro.

---

*SAVIA — Camagüey crece aquí 🌿*
