# IMDUMB

Aplicación Android en Kotlin para explorar categorías de películas y consultar el detalle de cada título. La entrega usa **XML + ConstraintLayout**, **MVP + Clean Architecture**, **RxJava 2/RxKotlin**, **Retrofit + Gson**, **Firebase Remote Config/Analytics**, **Dagger 2**, **Glide**, RecyclerViews anidados y Product Flavors `dev`/`prod`.

> No usa Jetpack Compose. Dagger se limita a inyección de dependencias; la arquitectura y las reglas de dependencia están separadas explícitamente.

La correspondencia requisito por requisito está en [`docs/REQUIREMENTS_CHECKLIST.md`](docs/REQUIREMENTS_CHECKLIST.md).

## Flujo funcional

1. `SplashActivity` solicita configuración a Firebase Remote Config.
2. Los valores activos o los defaults XML se persisten en `SharedPreferences` y se reutilizan como fallback local.
3. `HomeActivity` consume TVMaze con Retrofit y construye una lista vertical de categorías.
4. Cada categoría contiene su propio RecyclerView horizontal de películas.
5. Al tocar una película se abre `DetailActivity`, que carga imágenes y reparto en paralelo con RxJava.
6. El detalle muestra carrusel `ViewPager2`, título, calificación, resumen HTML con `Html.fromHtml`, actores y el botón inferior fijo **Recomendar**.
7. **Recomendar** abre un `BottomSheetDialogFragment` de altura adaptable, valida un comentario y confirma con `Snackbar`.

## Arquitectura

```text
presentation (Activities, XML, Adapters, MVP Contracts/Presenters)
        │
        ▼
domain (Models, Repository interfaces, Use Cases)
        ▲
        │
data (Retrofit/Firebase/SharedPreferences, DTOs, Mapper, Repository impls)
```

La dirección de dependencias es `presentation -> domain <- data`. El dominio no conoce Android, Retrofit, Firebase ni las vistas. Dagger ensambla las implementaciones en el composition root (`di/`).

### Estructura principal

```text
app/src/main/java/com/imdumb/app/
├── data/
│   ├── local/                 # SharedPreferences
│   ├── mapper/                # DTO -> dominio
│   ├── remote/api/            # Retrofit
│   ├── remote/firebase/       # Remote Config
│   └── repository/            # Implementaciones
├── di/                        # Dagger 2
├── domain/
│   ├── model/
│   ├── repository/            # Contratos
│   └── usecase/
└── presentation/
    ├── base/                  # MVP base
    ├── splash/
    ├── home/                  # RecyclerView externo/interno
    └── detail/                # ViewPager2 + BottomSheet
```

## Stack y versiones

| Componente | Versión |
|---|---:|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.7.3 |
| Gradle Wrapper | 8.9 |
| Java/JDK | 17 |
| compileSdk / targetSdk | 35 |
| minSdk | 23 |
| Retrofit / Gson adapter | 2.11.0 |
| RxJava 2 / RxAndroid / RxKotlin | 2.2.21 / 2.1.1 / 2.4.0 |
| Dagger | 2.52 |
| Glide | 4.16.0 |
| Firebase BoM | 33.7.0 |
| RecyclerView | 1.4.0 |
| ConstraintLayout | 2.2.0 |
| ViewPager2 | 1.1.0 |

### Herramientas recomendadas

- Android Studio **Ladybug 2024.2.1 o posterior** con soporte para AGP 8.7.
- JDK 17.
- Android SDK Platform 35.
- No es necesario instalar Gradle globalmente: el repositorio incluye Wrapper 8.9 y su checksum.

## Compilar y ejecutar

Desde la raíz:

```bash
./gradlew :app:assembleDevDebug
./gradlew :app:assembleProdDebug
```

Instalación directa del flavor de desarrollo:

```bash
./gradlew :app:installDevDebug
```

Pruebas y lint:

```bash
./gradlew :app:testDevDebugUnitTest :app:lintDevDebug
```

En Android Studio, seleccionar una variante desde **Build Variants**:

- `devDebug`
- `prodDebug`
- `devRelease`
- `prodRelease`

## Product Flavors / Build Variants

| Flavor | Application ID | BASE_URL | Otras diferencias |
|---|---|---|---|
| `dev` | `com.imdumb.app.dev` | `https://api.tvmaze.com/` | `applicationIdSuffix`, página 1, Remote Config sin caché, timeout de 4 s, nombre “IMDUMB Dev” |
| `prod` | `com.imdumb.app` | `https://api.tvmaze.com/` | página 0, intervalo de Remote Config de 3600 s, timeout de 6 s, nombre “IMDUMB” |

Cada flavor declara su propio `BuildConfig.BASE_URL`. TVMaze tiene un solo host público, por eso la diferencia funcional de entorno se demuestra además con `SHOWS_PAGE`, frecuencia de Remote Config, application ID y recursos de strings.

## API REST

No requiere API key.

- `GET /shows?page={page}`: catálogo base.
- `GET /shows/{id}/cast`: actores.
- `GET /shows/{id}/images`: carrusel.

Las categorías se generan por género en `TvMazeMapper`; también se agrega “Más populares” ordenada por rating.

## Firebase

Se integran:

- Firebase Analytics para eventos de arranque, carga de categorías y selección de contenido.
- Firebase Remote Config para:
  - `welcome_message`
  - `home_title`
  - `recommendations_enabled`

El repositorio incluye `app/google-services.json` **mock, sin credenciales reales**, exclusivamente para que el plugin de Google Services pueda generar recursos para `dev` y `prod` sin pasos manuales. La aplicación intenta ejecutar `fetchAndActivate` con un timeout corto por flavor; si ese proyecto mock no responde, utiliza `remote_config_defaults.xml`, persiste el resultado en `SharedPreferences` y continúa normalmente.

Para conectar un proyecto Firebase real, reemplazar `app/google-services.json`, registrar ambos package names (`com.imdumb.app` y `com.imdumb.app.dev`) y crear las tres claves anteriores. En un producto real no se deben versionar credenciales sensibles.

## RecyclerView avanzado

- `ListAdapter` + `DiffUtil` para categorías, películas, actores e imágenes.
- IDs estables.
- `RecyclerView.RecycledViewPool` compartido entre listas horizontales.
- `initialPrefetchItemCount` y cache de ViewHolders internos.
- Restauración del estado horizontal por categoría.
- Cancelación de cargas Glide al reciclar ViewHolders.
- Animaciones de cambio desactivadas en la lista exterior para evitar parpadeos.

## SOLID aplicado

- **S — Single Responsibility:** API, mapper, storage, repositories, use cases, presenters y views tienen responsabilidades separadas.
- **O — Open/Closed:** los contratos de repositorio permiten reemplazar TVMaze/Firebase sin modificar casos de uso.
- **L — Liskov:** las implementaciones respetan los contratos del dominio y siempre devuelven modelos válidos/fallbacks.
- **I — Interface Segregation:** `MovieRepository` y `AppConfigurationRepository` exponen únicamente operaciones de su contexto.
- **D — Dependency Inversion:** presentation depende de casos de uso y data implementa interfaces del dominio; Dagger enlaza ambas capas.

## Decisiones de resiliencia

- Remote Config cae a valores activos/defaults y luego a configuración local.
- Cast e imágenes fallan de forma independiente: el detalle básico sigue visible.
- Pull-to-refresh cancela la petición anterior para evitar carreras.
- Los errores de red preservan contenido ya cargado y se comunican con Snackbar.
- El proyecto contiene Wrapper, checksum, configuración Firebase mock, flavors y CI; no requiere archivos locales versionados.

### Author
Jesús Villa
