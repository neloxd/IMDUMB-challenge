# Checklist del reto técnico

| Requisito | Implementación |
|---|---|
| App Android en Kotlin | `app/src/main/java/` y plugin `org.jetbrains.kotlin.android` |
| UI solo XML / ConstraintLayout | `app/src/main/res/layout/`; no se aplica el plugin de Compose |
| MVP | Contratos y presenters en `presentation/splash`, `presentation/home` y `presentation/detail` |
| Clean Architecture | `presentation -> domain <- data`; interfaces de repositorio en `domain/repository` |
| Retrofit + Gson | `NetworkModule`, `TvMazeApi` y DTOs de `data/remote/dto` |
| RxJava 2 / RxKotlin | Repositorios, casos de uso y presenters usan `Single`, schedulers y `CompositeDisposable` |
| Firebase | Analytics + Remote Config, defaults XML y `google-services.json` mock versionado |
| Splash + persistencia local | `SplashPresenter`, `FirebaseConfigurationDataSource`, `AppConfigurationStorage` |
| Product Flavors | `dev` y `prod` con `BASE_URL`, página, intervalos, timeout, suffix y recursos propios |
| RecyclerViews anidados | `CategoryAdapter` mantiene un adapter interno por ViewHolder y comparte `RecycledViewPool` |
| Reciclado y rendimiento | `ListAdapter`, `DiffUtil`, IDs estables, prefetch, cache, restauración de scroll y cancelación Glide |
| Detalle | `DetailActivity`: ViewPager2, paginación, HTML, rating y actores |
| Botón inferior fijo | `recommendButton` está fuera del `NestedScrollView` y anclado al borde inferior |
| Bottom sheet dinámico | `RecommendationBottomSheet` con `NestedScrollView`, comentario, validación y confirmación |
| Feedback de éxito | `DetailPresenter.recommend` y `Snackbar` mediante `DetailView` |
| Compilación tras clonar | Wrapper, checksum, flavors, Firebase mock y CI incluidos; comandos en `README.md` |
| SOLID | Responsabilidades pequeñas, contratos segregados e inversión de dependencias con Dagger 2 |
