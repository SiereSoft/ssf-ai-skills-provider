# CMP Architecture Skill
Enforces the 4-layer architecture and naming conventions for Medboard features.

## Project Structure Overview
```text
medboard-cmp/
├── androidApp/         # Android specific app code
├── iosApp/             # iOS specific app code
├── sharedUI/           # Core Multiplatform module
│   └── src/commonMain/kotlin/com/ssf/medboard/
│       ├── base/       # Core UI and utilities
│       │   ├── components/ # Common Compose elements
│       │   ├── navigation/ # NavigationDispatcher & logic
│       │   └── theme/      # Material3 theme & design tokens
│       ├── core/       # Business & Domain layer
│       │   ├── domain/model/ # Shared business models
│       │   └── interactors/  # Domain Use Cases
│       ├── datasource/ # Data layer implementations
│       │   ├── api/          # Ktor & network services
│       │   ├── cache/        # SQLDelight/Local DB
│       │   └── datastore/    # Settings & SharedPreferences
│       └── presentation/     # MVI features & ViewModels
│           └── <domain>/<feature>/
```

## 4-Layer Architecture
1. **DataSource**: Low-level data access (Network, DB).
   - `interface <F>DataSource`, `class <F>DataSourceImpl` in `datasource/<type>/<domain>/`
2. **UseCase/Interactor**: Single responsibility business logic.
   - `class <F>UseCase` in `core/interactors/<domain>/`
   - Uses `operator fun invoke(...)` and returns `AppResult`.
3. **ViewModel**: Manages UI state and handles actions.
   - `class <F>ViewModel` in `presentation/<domain>/<feature>/`
   - Extends `BaseViewModel<State, Action, Event>`.
4. **UI (Compose)**: Declarative screens and components.
   - `<F>Screen`: Root composable with ViewModel injection.
   - `<F>Content`: Stateless UI in `presentation/<domain>/<feature>/`.

## Naming & Packages
- **Features**: `com.ssf.medboard.presentation.<domain>.<feature>`
- **Interactors**: `com.ssf.medboard.core.interactors.<domain>`
- **Models**: `com.ssf.medboard.core.domain.model`
- **Theme/Base**: `com.ssf.medboard.base.theme`, `com.ssf.medboard.base.components`
