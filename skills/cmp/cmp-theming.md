# CMP Theming Skill
Handles Compose Multiplatform theming using Material3 and custom properties.

## MedboardTheme
The root theme wrapper located in `com.ssf.medboard.base.theme`.
It provides:
- `Colors`: Access to `ColorScheme`
- `Dimens`: Access to custom `Dimensions`
- `Typographs`: Access to Material3 `Typography`
- `IsDarkTheme`: Boolean flag for dark mode

## Usage Examples
```kotlin
@Composable
fun MyContent() {
    Column(modifier = Modifier.background(Colors.background)) {
        Text("Title", style = Typographs.titleLarge, color = Colors.primary)
        Spacer(Modifier.height(Dimens.paddingMedium))
    }
}
```

## Adaptive Layouts
- Use `LocalWindowSizeClass.current` for responsive UI.
- `Dimens` automatically adapts to screen size (Compact/Medium/Expanded).

## Best Practices
- Avoid hardcoding colors; use `Colors.<semantic_name>`.
- Use `Dimens` for paddings, margins, and sizes to ensure consistency.
- Support both Light and Dark themes via semantic color naming.
