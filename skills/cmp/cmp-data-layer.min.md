# CMP Data Layer Skill
Detailed implementation of DataSources, UseCases, Ktor, and Storage.

## Ktor Implementation (safeApiCall & Factory)
```kotlin
val client = HttpClient {
    defaultRequest { url("your-base-url-here"); contentType(ContentType.Application.Json) }
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
}

suspend inline fun <reified T> HttpClient.safeApiCall(
    dispatcher: CoroutineDispatcher = DefaultDispatcherProvider().default(),
    crossinline block: HttpRequestBuilder.() -> Unit,
): AppResult<T> = withContext(dispatcher) {
    try {
        val response = request { block() }
        AppResult.Success(response.body())
    } catch (throwable: Throwable) {
        when (throwable) {
            is ClientRequestException -> Error.NetworkError.HttpError(throwable.response.status.value, throwable.response.body(), "API Key Missing")
            is SerializationException -> Error.NetworkError.SerializationError(throwable.message, "Serialization failure.")
            else -> Error.NetworkError.GenericError(throwable.message, "Something went wrong")
        }
    }
}
```

## Storage Implementation (Settings)
```kotlin
class UserStorageImpl(private val settings: Settings) : UserStorage {
    override fun getToken() = settings.getStringOrNull("token") ?: ""
    override fun setToken(value: String) { settings["token"] = value }
    override fun clear() { settings.remove("token") }
}
```

## DataSource Pattern
```kotlin
class AuthDataSourceImpl(private val client: HttpClient) : AuthDataSource {
    override suspend fun signIn(id: String, pw: String) = client.safeApiCall<UserDataDto> {
        url { path("/auth/local") }; method = HttpMethod.Post; setBody(SignInRequest(id, pw))
    }
}
```

## UseCase & Error Handling
```kotlin
class SignInUseCase(private val ds: AuthDataSource, private val storage: UserStorage) {
    suspend operator fun invoke(id: String, pw: String): AppResult<Unit> =
        ds.signIn(id, pw).onSuccess { storage.setToken(it.jwt) }.mapResult { }
}
```

## Trigger: `generate data <FeatureName>`
- Generates `DataSource`, `UseCase`, and Ktor/Storage boilerplate.