# CMP Navigation Skill
Detailed guidelines and implementation for Medboard navigation.

## Navigation Wrapper Implementation
```kotlin
// NavigationCommand.kt
sealed class NavigationCommand {
    data class Navigate(val action: NavigationAction) : NavigationCommand()
    data object Back : NavigationCommand()
}

// NavigationDispatcher.kt
class NavigationDispatcher(private val navigationManager: NavigationManager) {
    val navigationCommands: Flow<SingleEvent<NavigationCommand>> = navigationManager.navActions
    suspend fun navigateTo(navAction: NavigationAction) = navigationManager.navigate(NavigationCommand.Navigate(navAction))
    suspend fun navigateBack() = navigationManager.navigate(NavigationCommand.Back)
}

// NavigationAction.kt
abstract class NavigationAction {
    abstract val route: String
    open val params: List<PathParams> = emptyList()
    val name: String = buildString { append(route); params.forEach { append("/{${it.key}}") } }
    open val navAnimation: NavAnimation? = NavAnimation.fadeInOut()
}
```

// NavigationManagerImpl.kt
class NavigationManagerImpl : NavigationManager {
    private val _navActions = MutableSharedFlow<SingleEvent<NavigationCommand>>(replay = 1)
    override val navActions = _navActions.asSharedFlow()
    override suspend fun navigate(command: NavigationCommand) = _navActions.emit(command.toSingleEvent())
}
```

## ViewModel Side
1. Inject `NavigationDispatcher`.
2. Call `navigationDispatcher.navigateTo(NavScreen.Target)`.
3. For back navigation: `navigationDispatcher.navigateBack()`.

## NavScreen Structure
Define new destinations in `NavScreen.kt`:
```kotlin
sealed class NavScreen : NavigationAction() {
    data object MyFeature : NavScreen() { override val route: String = "myFeature" }
}
```

## RootComposable Registration
Add destinations to the `NavHost` in `RootComposable.kt`:
```kotlin
composableHolder(NavScreen.MyFeature) { MyFeatureScreen() }
```

## Trigger: `add route <FeatureName>`
- Generates `NavScreen` entry and boilerplate for `RootComposable`.
