# CMP Kotlin & Anti-Patterns Skill
Best practices for Kotlin and Compose Multiplatform development in Medboard.

## Kotlin Anti-Patterns (Avoid)
- ❌ **Blocking Coroutines**: Never use `runBlocking` on the main thread.
- ❌ **GlobalScope**: Avoid `GlobalScope`. Use `viewModelScope` or structured concurrency.
- ❌ **State Exposure**: Do not expose `MutableStateFlow`. Expose as `StateFlow`.
- ❌ **Flow Exceptions**: Always use `.catch { ... }` operator on flows.
- ❌ **Hardcoded Dispatchers**: Inject `CoroutineDispatcher` for testability.
- ❌ **SideEffects**: Avoid state mutations inside Composables. Use `LaunchedEffect`.

## Correct vs Incorrect Examples
### State Exposure
```kotlin
// ❌ Incorrect
val state = MutableStateFlow(MyState())
// ✅ Correct
private val _state = MutableStateFlow(MyState())
val state: StateFlow<MyState> = _state.asStateFlow()
```

### Flow Collection in UI
```kotlin
// ❌ Incorrect: collecting in init {}
// ✅ Correct: use collectAsStateWithLifecycle() in Screen
val state by vm.state.collectAsStateWithLifecycle()
```

### Side Effects
```kotlin
// ❌ Incorrect: side effect directly in body
if (isError) { showSnackbar() }
// ✅ Correct: use LaunchedEffect
LaunchedEffect(isError) { if (isError) showSnackbar() }
```

## Best Practices
1. **Sealed Classes**: Use for finite sets (MVI Actions, Events, AppResult).
2. **Lazy Initialization**: Prefer `by lazy` over `lateinit` where possible.
3. **Stability**: Annotate UI models with `@Stable` or `@Immutable` if needed.