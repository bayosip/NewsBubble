# NewsBubble

An Android news reader built with Jetpack Compose. Headlines from [NewsAPI.org](https://newsapi.org)
are grouped by category on an animated "bubble" home screen; tapping a category opens a
paginated list of its headlines, and tapping an article opens it in an in-app WebView.

## Features

- **Home screen** — one floating, draggable-looking bubble per `NewsCategory` (business,
  entertainment, general, health, science, sports, technology); tap a bubble to open that
  category.
- **Category screen** — paginated headline list (Paging 3) with pull-to-refresh, shimmer
  loading skeletons, and a dedicated empty/error state.
- **In-app article viewer** — tapping a headline opens its URL in a composable `WebView`
  screen (`ArticleWebView`) pushed onto the back stack, instead of leaving the app.

## Architecture

Clean Architecture, split into `domain` / `data` / `presentation` packages, wired with Hilt:

```
domain/
  model/            NewsCategory, Article, HeadlineData, Source
  repository/        NewsRepository (interface)
  usecase/            GetHeadlinesByCategoryUseCase (interface)
  util/               Resource<T>, BaseDataSource, Util
data/
  remote/             NewsApiService (Retrofit)
  repository/         NewsRepositoryImpl, GenericPagingSource
  mapper/             Article -> UIArticle
  ui_model/           UIArticle
presentation/
  home/               HomeScreen (category picker)
  category/           CategoryScreen (MVI: Contract/ViewModel)
  webview/            ArticleWebView (in-app browser)
  navigation/         Navigation 3 routes + graph
```

- **MVI** — `CategoryScreen` follows Intent → ViewModel → State/Effect
  (`CategoryContract.kt`, `CategoryViewModel.kt`) with `StateFlow` for state and
  `SharedFlow` for one-shot effects (e.g. opening an article).
- **Repository** — every network call is wrapped in `Flow<Resource<T>>>`
  (`Resource.STATUS`: `LOADING` / `SUCCESS` / `ERROR`), decoupling the UI from Retrofit's
  `Response<T>` type.
- **Navigation** — [Navigation 3](https://developer.android.com/guide/navigation/navigation-3)
  with type-safe, `@Serializable` routes (`Home`, `CategoryDetail`, `ArticleWebView`) — see
  `NewsNavGraph.kt`.
- **Networking** — Retrofit over OkHttp, with the NewsAPI key attached via an
  `Interceptor`, and Gson for response deserialization.
- **Pagination** — `NewsRepositoryImpl` backs a Paging 3 `Pager`/`PagingSource`
  (`GenericPagingSource`) so category lists load one page at a time.

## Setup

1. Get a free API key from [newsapi.org](https://newsapi.org).
2. Add it to `local.properties` at the project root (not committed to version control):

   ```properties
   newsApiKey=YOUR_KEY_HERE
   ```

3. Open the project in Android Studio and sync Gradle, or build from the command line:

   ```sh
   ./gradlew :app:assembleDebug
   ```

Minimum SDK 28, target/compile SDK 37.

## Testing

Unit tests use **JUnit 5 + MockK**, with `kotlinx-coroutines-test` for coroutine/Flow
assertions:

```sh
./gradlew :app:testDebugUnitTest
```

Covered: `ArticleMapper` (title/url/description edge cases), `GetHeadlinesByCategoryUseCaseImpl`,
`NewsRepositoryImpl` (success/error/exception paths against a mocked `NewsApiService`),
`GenericPagingSource` (page keys, empty pages, error propagation), and `CategoryViewModel`
(load/dedupe/switch category, article-click effect).

Instrumented Compose UI tests run on a device or emulator:

```sh
./gradlew :app:connectedDebugAndroidTest
```

Covered: `ArticleRowItem` renders an article's title/description/source and invokes its
`onClick` callback when tapped.

> Note: on some physical devices, Espresso's idling-resource check fails with
> `NoSuchMethodException: android.hardware.input.InputManager.getInstance` — a known
> Espresso/AOSP compatibility issue unrelated to this app. Run on an emulator, or target one
> device with `ANDROID_SERIAL=<serial> ./gradlew :app:connectedDebugAndroidTest`, if you hit it.

## Tech stack

Kotlin, Jetpack Compose, Navigation 3, Hilt, Retrofit + OkHttp + Gson, Coil, Paging 3,
kotlinx.coroutines, JUnit 5, MockK.
