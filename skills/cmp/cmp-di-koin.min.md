# CMP DI Koin Skill
Registration and organization of dependencies using Koin.

## Koin Modules
- **DataSource/Cache**: Registered in `cacheModule` as `single`.
- **Interactors (UseCases)**: Registered in `coreModule` or `AppModule` as `factory`.
- **ViewModels**: Registered in `AppModule` or feature-specific modules as `factory`.

## Registration Patterns
```kotlin
// DataSource (Single instance)
single<FDataSource> { FDataSourceImpl(get(), get()) }

// UseCase (New instance per call)
factory { FUseCase(get()) }

// ViewModel (Scoped to screen)
factory { FViewModel(get(), get(), get()) }
```

## Injection in UI
- **ViewModel**: `val vm = koinViewModel<FViewModel>()`.
- **Properties**: `val ds = koinInject<FDataSource>()`.

## Guidelines
- Avoid manual singleton instantiation; use Koin `single`.
- Use `factory` for stateful components like ViewModels to ensure a clean state.
- Keep `AppModule` clean by delegating to sub-modules (`cacheModule`, `coreModule`).

## Trigger: `register <FeatureName>`
- Adds `factory` or `single` entries to appropriate Koin modules.