# CMP Init Skill
Initializes project boilerplate (Base classes, Results, API, Navigation).

## 1. Result Classes (AppResult.kt)
```kotlin
sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    sealed class Error(open val message: String) : AppResult<Nothing>() {
        data class Generic(override val message: String) : Error(message)
        data object Timeout : Error("Network request timed out")
        data object Cancelled : Error("Network request cancelled")
        sealed class NetworkError : Error("Network Error") {
            data class HttpError(val code: Int, val errorBody: String?, val errorMessage: String?) : Error(errorMessage ?: "Unknown")
            data class SerializationError(val messageData: String?, val errorMessage: String) : Error(messageData ?: errorMessage)
            data class GenericError(val messageData: String?, val errorMessage: String) : Error(messageData ?: errorMessage)
        }
    }
}
suspend fun <T> resultOf(dispatcher: CoroutineDispatcher, block: suspend () -> T): AppResult<T> = 
    try { AppResult.success(block()) } catch (e: Exception) { AppResult.failure(e) }
```

## 2. Base Classes (BaseViewModel.kt)
```kotlin
abstract class BaseViewModel<S, A, E>(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState); val state = _state.asStateFlow()
    private val _events = Channel<E>(Channel.CONFLATED); val events = _events.receiveAsFlow()
    val queue = mutableStateOf<List<UiEvent>>(emptyList())
    abstract suspend fun handleActions(action: A)
    fun updateState(function: S.() -> S) { _state.update(function) }
    fun submitAction(action: A) { viewModelScope.launch { handleActions(action) } }
    fun submitEvent(event: E) { viewModelScope.launch { _events.send(event) } }
    suspend fun <T> onDataResult(dataState: DataState<T>, onData: suspend (T) -> Unit = { }) {
        dataState.data?.let { onData(it) }
    }
}
```

## 3. API & Navigation
- **Ktor**: `HttpClient.safeApiCall` (see `cmp-data-layer`).
- **Navigation**: `NavigationDispatcher` & `NavigationAction` (see `cmp-navigation`).

## Trigger: `init project`
- Scaffolds `AppResult`, `BaseViewModel`, `Navigation` wrapper, and `Ktor` extensions.
