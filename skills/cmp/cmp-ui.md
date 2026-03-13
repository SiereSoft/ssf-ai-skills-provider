# CMP UI & Screen Skill
Guidelines for Medboard UI using Compose Multiplatform.

## Screen Level
- Use `<F>Screen()` as the entry point.
- Inject ViewModel: `val vm = koinViewModel<<F>ViewModel>()`.
- Collect State: `val state by vm.state.collectAsStateWithLifecycle()`.

## RootScreen Wrapper
Wrap content in `RootScreen` for event handling and common UI components:
```kotlin
RootScreen(
    viewModel = vm,
    eventProcessor = { event -> 
        when (event) {
            is <F>Event.Navigate -> { /* Navigation logic if not handled by dispatcher */ }
        }
    }
) {
    <F>Content(state) { vm.submitAction(it) }
}
```

## Stateless Content
- Pure Compose UI without ViewModel references.
- Signature: `fun <F>Content(state: <F>State, action: (<F>Action) -> Unit)`.

## Guidelines
- Follow `cmp-theming` for colors and spacing.
- Reuse base components from `com.ssf.medboard.base.components` (buttons, textfields, etc.).
- Keep `Content` stateless for easier previews and testing.
- **Strictly use Material Design 3** for all available components.

## UI Performance Rules
- **Minimize Recomposition**: Read state as late as possible. Use lambda-based modifiers: `Modifier.offset { IntOffset(0, scrollOffset) }`.
- **Heavy Computations**: Always wrap non-trivial calculations in `remember { ... }`.
- **Filtered State**: Use `derivedStateOf` for values derived from other states that change frequently (e.g., scroll position).
- **Lazy List Keys**: Always provide a unique `key` in `items()` of `LazyColumn`/`LazyRow`.
- **Animations**: Use `Modifier.graphicsLayer` for transforms (alpha, translation, scale) to avoid triggering recomposition of the entire content.

## UI Styling Rules
- **Theme Consistency**: Strictly use `MedboardTheme` accessors: `Colors.<name>`, `Dimens.<name>`, `Typographs.<name>`.
- **No Hardcoding**: Avoid hardcoded colors (e.g., `Color.White`) or sizes (e.g., `16.dp`). Use semantic theme values.
- **Adaptive Content**: Use `LocalWindowSizeClass` and apply `.adaptiveMediumWidth(widthSizeClass)` for multi-screen support.
- **Component Naming**: Suffix stateless UI functions with `Content` (e.g., `SignInContent`).

## Adaptive Layouts
- Use `LocalWindowSizeClass.current.widthSizeClass`.
- Apply modifiers like `.adaptiveMediumWidth(widthSizeClass)`.
- Use `Dimens` (e.g., `Dimens.medium`, `Dimens.large`) for spacing.
- Use `Colors` and `Typographs` for styling.

## Components
- Prefer `sharedUI` components (e.g., `SocialLoginButton`, `TopAppBarWithBack`).
- Use `Box` or `Column` as root containers in `Content`.

## Trigger: `generate ui <FeatureName>`
- Generates Screen (with injection) and Content (stateless) templates.
- Includes `RootScreen` setup.
