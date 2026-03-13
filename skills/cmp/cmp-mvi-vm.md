# CMP MVI & ViewModel Skill
Guides on `BaseViewModel` and strict MVI implementation with construction details.

## MVI Components Construction
```kotlin
// mvi/SignInState.kt
data class SignInState(
    val isLoading: Boolean = false,
    val email: InputWrapper = InputWrapper(),
    val isRememberMeChecked: Boolean = false
)

// mvi/SignInAction.kt
sealed interface SignInAction {
    data class OnEmailChange(val email: String) : SignInAction
    data object OnLoginClick : SignInAction
    data object OnSignUpClick : SignInAction
}

// mvi/SignInEvent.kt
sealed interface SignInEvent {
    data object ShowErrorDialog : SignInEvent
}
```

## ViewModel Implementation
```kotlin
class SignInViewModel(
    private val signIn: SignIn,
    private val navigationDispatcher: NavigationDispatcher,
    private val userStorage: UserStorage
) : BaseViewModel<SignInState, SignInAction, SignInEvent>(SignInState()) {

    override suspend fun handleActions(action: SignInAction) {
        when (action) {
            is SignInAction.OnEmailChange -> updateState { copy(email = email.copy(value = action.email)) }
            SignInAction.OnLoginClick -> performSignIn()
            SignInAction.OnSignUpClick -> navigationDispatcher.navigateTo(NavScreen.SignUp)
        }
    }

    private suspend fun performSignIn() {
        updateState { copy(isLoading = true) }
        signIn(state.value.email.value, "pw")
            .onSuccess { 
                userStorage.setToken(it.token)
                navigationDispatcher.navigateTo(NavScreen.Home) 
            }
            .onFailure { /* Handle Error */ }
            .also { updateState { copy(isLoading = false) } }
    }
}
```

## Trigger: `generate vm <FeatureName>`
- Generates `ViewModel`, `State`, `Action`, and `Event` classes.
- Includes `updateState` patterns and `onSuccess`/`onFailure` result handling.
