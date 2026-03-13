# CMP Gradle Skill
Guidelines for building and managing dependencies in Medboard.

## Core Commands
- **Assemble**: `./gradlew assembleDebug` (Android).
- **Test**: `./gradlew test` (Shared/Desktop), `./gradlew connectedDebugAndroidTest` (Device).
- **Run Android**: `./gradlew installDebug`.
- **Run Desktop**: `./gradlew run`.

## Dependency Management
- **Version Catalog**: Managed in `gradle/libs.versions.toml`.
- **Shared Dependencies**: Add to `commonMain` in `sharedUI/build.gradle.kts`.
- **Platform Dependencies**: `androidMain` or `iosMain`.

## Multiplatform Config
Ensure proper `kotlin { ... }` blocks for targeting:
- `jvm()` (Desktop)
- `androidTarget()`
- `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`

## Module Structure
1. `sharedUI`: UI, Theme, Base classes, Presenters.
2. `androidApp`: Android-specific application wrapper.
3. `iosApp`: Swift-based iOS application wrapper.

## Code Style Enforcement
- Use `./gradlew detekt` (if configured) for code style checks.
- Adhere to the `gradle.properties` for memory and JVM settings.

## Trigger: `build info`
- Summarizes common build tasks for Medboard.
