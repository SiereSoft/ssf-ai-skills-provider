# CMP Scaffolding Skill
Automates creation of Medboard features across all 4 layers.

## Trigger: `init <FeatureName> <Domain>`

## Generation Rules
1. **MVI**: Create `<F>State`, `<F>Action`, `<F>Event` in `mvi/` package.
2. **ViewModel**: Inherit from `BaseViewModel` with created MVI classes.
3. **UI**: Create `<F>Screen` (uses `RootScreen`) and stateless `<F>Content`.
4. **Data**: Create `interface <F>DataSource` and `class <F>UseCase`.
5. **Koin**: Generate registration code snippets for `AppModule` and `coreModule`.

## MVI Template
```kotlin
data class {{F}}State(val isLoading: Boolean = false)
sealed interface {{F}}Action { data object OnInit : {{F}}Action }
sealed interface {{F}}Event
```

## ViewModel Template
```kotlin
class {{F}}ViewModel(
    private val useCase: {{F}}UseCase,
    private val navigationDispatcher: NavigationDispatcher
) : BaseViewModel<{{F}}State, {{F}}Action, {{F}}Event>({{F}}State()) {
    override suspend fun handleActions(action: {{F}}Action) { /* ... */ }
}
```

## Screen Template
```kotlin
@Composable fun {{F}}Screen() {
    val vm = koinViewModel<{{F}}ViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    RootScreen(vm, { /* events */ }) { {{F}}Content(state) { vm.submitAction(it) } }
}
```

## Guidelines
- Follow naming conventions in `cmp-architecture`.
- Use stateless `Content` pattern in `cmp-ui`.
- Register in Koin via `cmp-di-koin`.
