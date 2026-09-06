# AGENTS.md

## Documentation
Always consult the Android Knowledge Base (android docs)
before suggesting any Jetpack API.

## Network
- Use Retrofit for network calls

## Repository
- Wrap all api calls in a flow of Resource<> e.g. getCategories(): Flow<Resource<List<Category>>>

## Architecture
- Use Clean Architecture pattern for packages
- MVI + Hilt — do NOT suggest Koin or manual DI
- Do not inject repositories directly into ViewModels, use reusable usecase interfaces
- ViewModels use StateFlow — never LiveData for new code
- Repository pattern required for all data access

## UI
- All new screens use Jetpack Compose — no new XML layouts
- Reference HomeScreen.kt as the Compose pattern for this project

## Navigation
- Navigation 3 with type-safe routes — not navigation-compose 2.x
- All routes must be @Serializable — reference NavGraph.kt

## Build
- AGP 9 — use libs.versions.toml, no direct build.gradle.kts deps

## Testing
- JUnit 5 + MockK for unit tests — not Mockito or Espresso
- Coroutine tests use runTest from kotlinx-coroutines-test

## Never
- Thread.sleep() → use delay()
- GlobalScope → use viewModelScope
- Broad catch(Exception) → handle specific types
- !! operator → handle nullability explicitly