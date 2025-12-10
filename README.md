# 🍰 BakingApp

<div align="center">

**Enterprise Android Clean Architecture Sample**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.09-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-2.52-34A853.svg?logo=android)](https://dagger.dev/hilt/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20+%20MVI-FF6F00.svg)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<br/>

*A production-ready, scalable Android application showcasing modern development practices, clean architecture, and best-in-class libraries.*

[Features](#-features) • [Architecture](#️-architecture) • [Tech Stack](#️-tech-stack) • [Getting Started](#-getting-started) • [Documentation](#-documentation)

</div>

---

## ✨ Highlights

| 🏗️ **Clean Architecture** | 📱 **Modern UI** | 🧪 **Testable** | 🔒 **Secure** |
|:---:|:---:|:---:|:---:|
| Multi-module setup with clear separation of concerns | Jetpack Compose with Material 3 Design | Comprehensive unit & UI tests | NDK/C++ API keys + Encrypted storage |

---

## 📸 Screenshots

| Login | Home | Recipe Details |
|:-----:|:----:|:--------------:|
| 🔐 Secure Authentication | 🏠 Recipe Discovery | 📖 Step-by-Step Guide |

---

## 🎯 Features

### 🔐 Authentication
- Email/password validation with real-time feedback
- Secure token storage using EncryptedSharedPreferences
- Loading states with smooth animations
- Comprehensive error handling

### 🏠 Recipe Discovery
- Beautiful recipe cards with images
- Pull-to-refresh functionality
- Category-based filtering
- Debounced search for performance
- Favorite toggle with local persistence

### 📖 Recipe Details
- Complete ingredients list with quantities
- Step-by-step cooking instructions
- Difficulty badges (Easy, Medium, Hard)
- Prep/cook time information
- Video support for cooking steps

---

## 🏗️ Architecture

BakingApp follows **Clean Architecture** principles combined with **MVI (Model-View-Intent)** pattern, ensuring:

- ✅ **Separation of Concerns** - Each layer has a single responsibility
- ✅ **Testability** - Business logic is isolated and easily testable
- ✅ **Scalability** - New features can be added without affecting existing code
- ✅ **Maintainability** - Clear boundaries make the codebase easy to understand

### Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                           PRESENTATION LAYER                                │
│  ┌────────────────┐    ┌────────────────┐    ┌─────────────────────────┐   │
│  │    Screens     │    │   ViewModels   │    │       UI State          │   │
│  │   (Compose)    │◄───│     (MVI)      │───►│    (Immutable)          │   │
│  │                │    │                │    │                         │   │
│  │  • LoginScreen │    │ • LoginVM      │    │ • LoginUiState          │   │
│  │  • HomeScreen  │    │ • HomeVM       │    │ • HomeUiState           │   │
│  │  • DetailScreen│    │ • DetailVM     │    │ • RecipeDetailUiState   │   │
│  └────────────────┘    └───────┬────────┘    └─────────────────────────┘   │
│                                │                                            │
└────────────────────────────────┼────────────────────────────────────────────┘
                                 │ invoke()
                                 ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                             DOMAIN LAYER                                    │
│  ┌────────────────┐    ┌────────────────┐    ┌─────────────────────────┐   │
│  │   Use Cases    │    │    Entities    │    │  Repository Interfaces  │   │
│  │                │    │                │    │                         │   │
│  │ • LoginUseCase │    │ • Recipe       │    │ • RecipeRepository      │   │
│  │ • GetRecipes   │    │ • Ingredient   │    │ • AuthRepository        │   │
│  │ • ToggleFav    │    │ • Step         │    │                         │   │
│  │ • SearchRecipes│    │ • LoginResult  │    │                         │   │
│  └────────────────┘    └────────────────┘    └────────────┬────────────┘   │
│                                                           │                 │
└───────────────────────────────────────────────────────────┼─────────────────┘
                                                            │ implements
                                                            ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                              DATA LAYER                                     │
│  ┌────────────────┐    ┌────────────────┐    ┌─────────────────────────┐   │
│  │  Repository    │    │  Data Sources  │    │        Mappers          │   │
│  │     Impl       │    │                │    │                         │   │
│  │                │◄───│ • RecipesApi   │    │ • RecipeEntity → Recipe │   │
│  │ • RecipeRepo   │    │ • RecipeDao    │    │ • RecipeDto → Recipe    │   │
│  │ • AuthRepo     │    │ • AuthApi      │    │                         │   │
│  └────────────────┘    └────────────────┘    └─────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow

```
┌──────────┐     ┌──────────────┐     ┌───────────┐     ┌────────────┐     ┌────────────┐
│   User   │────►│   Composable │────►│ ViewModel │────►│  Use Case  │────►│ Repository │
│  Action  │     │    Screen    │     │           │     │            │     │            │
└──────────┘     └──────────────┘     └─────┬─────┘     └────────────┘     └──────┬─────┘
                                            │                                      │
                                            │ StateFlow                            │
                                            │                                      ▼
                                      ┌─────▼─────┐                         ┌──────────────┐
                                      │ UI State  │◄────────────────────────│ Data Source  │
                                      │ (Updated) │       Result<T>         │  (API/Room)  │
                                      └───────────┘                         └──────────────┘
```

---

## 📦 Module Structure

The project follows a **feature-based modularization** strategy:

```
BakingApp/
│
├── 📱 app/                              # Application entry point
│   ├── BakingApplication.kt             # Hilt Application class
│   ├── MainActivity.kt                  # Single Activity
│   └── navigation/
│       └── BakingNavHost.kt             # Navigation graph
│
├── 🧱 core/                             # Shared core modules
│   ├── common/                          # Base classes & utilities
│   │   ├── base/
│   │   │   └── BaseUseCase.kt           # UseCase, FlowUseCase, NoParamUseCase
│   │   ├── result/
│   │   │   └── Result.kt                # Result<T> sealed class
│   │   ├── dispatcher/
│   │   │   └── DispatcherModule.kt      # Coroutine dispatchers
│   │   └── extensions/
│   │       └── FlowExtensions.kt        # Flow utility extensions
│   │
│   ├── network/                         # Networking layer
│   │   ├── api/
│   │   │   ├── AuthApi.kt               # Authentication endpoints
│   │   │   └── RecipesApi.kt            # Recipe endpoints
│   │   ├── interceptor/
│   │   │   ├── AuthInterceptor.kt       # Token injection
│   │   │   └── NetworkDelayInterceptor.kt
│   │   ├── model/
│   │   │   ├── RecipeDto.kt             # Data Transfer Objects
│   │   │   └── NetworkResponse.kt       # API response wrapper
│   │   └── di/
│   │       └── NetworkModule.kt         # Hilt network providers
│   │
│   ├── database/                        # Local persistence
│   │   ├── BakingDatabase.kt            # Room database
│   │   ├── dao/
│   │   │   └── RecipeDao.kt             # Recipe data access
│   │   ├── entity/
│   │   │   └── RecipeEntity.kt          # Room entities
│   │   └── di/
│   │       └── DatabaseModule.kt        # Hilt database providers
│   │
│   ├── security/                        # Security utilities
│   │   ├── cpp/                         # Native code (NDK)
│   │   │   ├── CMakeLists.txt           # CMake build config
│   │   │   └── native-keys.cpp          # XOR-obfuscated keys
│   │   ├── ApiKeyProvider.kt            # Key provider interface
│   │   ├── NativeKeyProvider.kt         # JNI bridge to native
│   │   ├── EncryptedPreferencesManager.kt
│   │   ├── SecureTokenManager.kt        # Token management
│   │   └── di/
│   │       └── SecurityModule.kt
│   │
│   └── ui/                              # Shared UI components
│       ├── components/
│       │   ├── BakingButton.kt
│       │   ├── BakingTextField.kt
│       │   ├── ErrorView.kt
│       │   ├── LoadingIndicator.kt
│       │   └── RecipeCard.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           └── Type.kt
│
├── 🎨 features/                         # Feature modules
│   ├── login/                           # Authentication feature
│   │   ├── data/
│   │   │   └── repository/
│   │   │       └── AuthRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── LoginResult.kt
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt
│   │   │   └── usecase/
│   │   │       ├── LoginUseCase.kt
│   │   │       ├── ValidateEmailUseCase.kt
│   │   │       └── ValidatePasswordUseCase.kt
│   │   ├── presentation/
│   │   │   ├── LoginScreen.kt
│   │   │   ├── LoginViewModel.kt
│   │   │   └── LoginUiState.kt
│   │   └── di/
│   │       └── LoginModule.kt
│   │
│   ├── home/                            # Recipe list feature
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   └── FakeRecipeDataSource.kt
│   │   │   ├── mapper/
│   │   │   │   └── RecipeMapper.kt
│   │   │   └── repository/
│   │   │       └── RecipeRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── Recipe.kt
│   │   │   ├── repository/
│   │   │   │   └── RecipeRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetRecipesUseCase.kt
│   │   │       ├── SearchRecipesUseCase.kt
│   │   │       └── ToggleFavoriteUseCase.kt
│   │   ├── presentation/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── HomeUiState.kt
│   │   └── di/
│   │       └── HomeModule.kt
│   │
│   └── recipe-details/                  # Recipe detail feature
│       └── presentation/
│           ├── RecipeDetailScreen.kt
│           ├── RecipeDetailViewModel.kt
│           └── RecipeDetailUiState.kt
│
└── 📚 docs/                             # Documentation
    ├── architecture.md
    ├── modules.md
    ├── networking.md
    ├── security.md
    ├── testing.md
    ├── performance.md
    └── compose_guidelines.md
```

### Module Dependencies

```
                    ┌─────────┐
                    │   app   │
                    └────┬────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
    ┌───────────┐  ┌───────────┐  ┌───────────────┐
    │   login   │  │   home    │  │ recipe-details │
    └─────┬─────┘  └─────┬─────┘  └───────┬───────┘
          │              │                │
          └──────────────┴────────────────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
    ┌──────────┐  ┌───────────┐  ┌──────────┐
    │ core:ui  │  │core:common│  │core:network│
    └──────────┘  └───────────┘  └──────────┘
                         │
                         ▼
                  ┌────────────┐
                  │core:security│
                  └────────────┘
```

---

## 🎭 MVI Pattern Implementation

### UI State

Immutable data class representing the entire screen state:

```kotlin
// LoginUiState.kt
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && 
                password.isNotBlank() && 
                emailError == null && 
                passwordError == null
}
```

### One-Time Events

Sealed class for navigation and side effects:

```kotlin
sealed class LoginEvent {
    data class NavigateToHome(val userName: String) : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}
```

### ViewModel

State holder with intent handling:

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    fun onEmailChange(email: String) {
        _uiState.update { state ->
            state.copy(email = email, emailError = null)
        }
    }
    
    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    _events.send(LoginEvent.NavigateToHome(result.data.name))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> { /* handled */ }
            }
        }
    }
}
```

---

## 🔧 Result Type

A generic sealed class for handling async operations:

```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : Result<Nothing>
    data object Loading : Result<Nothing>
}

// Extension functions
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R>
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T>
inline fun <T> Result<T>.onError(action: (Throwable, String?) -> Unit): Result<T>
fun <T> Result<T>.getOrNull(): T?
fun <T> Result<T>.getOrDefault(default: T): T
fun <T> Result<T>.getOrThrow(): T
```

---

## 📐 Base Use Cases

Reusable base classes for business logic:

```kotlin
// Single value use case
abstract class UseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                Result.Success(execute(parameters))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    protected abstract suspend fun execute(parameters: P): R
}

// Flow-based use case
abstract class FlowUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .catch { e -> emit(Result.Error(e as Exception)) }
            .flowOn(coroutineDispatcher)
    }
    
    protected abstract fun execute(parameters: P): Flow<Result<R>>
}

// No parameter variants
abstract class NoParamUseCase<R>(dispatcher: CoroutineDispatcher)
abstract class NoParamFlowUseCase<R>(dispatcher: CoroutineDispatcher)
```

---

## 🛠️ Tech Stack

### Core

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **Kotlin** | 2.0.20 | Programming language |
| **Jetpack Compose** | 2025.09.00 | Declarative UI toolkit |
| **Material 3** | Latest | Design system |
| **Hilt** | 2.52 | Dependency injection |
| **Coroutines** | 1.9.0 | Asynchronous programming |
| **Navigation Compose** | 2.9.1 | Screen navigation |

### Networking

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **Retrofit** | 2.11.0 | HTTP client |
| **OkHttp** | 4.12.0 | HTTP engine + interceptors |
| **Moshi** | 1.15.0 | JSON serialization |

### Database & Storage

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **Room** | 2.6.1 | Local SQLite database |
| **Paging 3** | 3.3.6 | Efficient data pagination |
| **EncryptedSharedPreferences** | 1.1.0-alpha06 | Secure key-value storage |

### Background Processing

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **WorkManager** | 2.10.4 | Background task scheduling |

### Image Loading

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **Coil** | 2.6.0 | Image loading for Compose |

### Testing

| Technology | Version | Purpose |
|:-----------|:-------:|:--------|
| **JUnit** | 4.13.2 | Unit test framework |
| **Turbine** | 1.2.1 | Flow testing |
| **MockWebServer** | 4.12.0 | API mocking |
| **Truth** | 1.4.5 | Fluent assertions |
| **Mockito** | 5.12.0 | Mocking framework |
| **Espresso** | 3.7.0 | UI testing |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK** 17
- **Android SDK** 35 (minimum SDK 24)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/your-repo/baking-app.git
cd baking-app

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or simply open in Android Studio and click Run ▶️
```

### Test Credentials

| Field | Value |
|:------|:------|
| **Email** | `test@example.com` |
| **Password** | `Password123` |

---

## 🧪 Testing

The project includes comprehensive tests across all layers:

### Unit Tests

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific module tests
./gradlew :features:login:testDebugUnitTest
./gradlew :features:home:testDebugUnitTest
```

### Integration Tests

```bash
# Run instrumented tests
./gradlew connectedAndroidTest
```

### Test Coverage

| Layer | Test Types |
|:------|:-----------|
| **Domain** | Use case tests with fake repositories |
| **Data** | Repository tests with MockWebServer |
| **Presentation** | ViewModel tests with Turbine |
| **UI** | Compose UI tests with Espresso |

### Example Test

```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val fakeRepository = FakeAuthRepository()
    val loginUseCase = LoginUseCase(fakeRepository)
    
    // When
    val result = loginUseCase("test@example.com", "Password123")
    
    // Then
    assertThat(result).isInstanceOf(Result.Success::class.java)
}
```

---

## 🔒 Security Features

| Feature | Implementation |
|:--------|:---------------|
| **Native API Key Storage** | NDK/C++ with XOR obfuscation for API keys |
| **Encrypted Storage** | EncryptedSharedPreferences for tokens |
| **No Sensitive Logs** | ProGuard rules remove logging in release |
| **Certificate Pinning** | Ready for production configuration |
| **Clear-text Disabled** | Network security config enforces HTTPS |
| **Code Obfuscation** | R8 minification for release builds |

### 🔑 Native Key Provider

API keys are stored securely in native C++ code with multiple protection layers:

```kotlin
@Inject
lateinit var apiKeyProvider: ApiKeyProvider

// Get API key from native storage
val apiKey = apiKeyProvider.getApiKey()
```

**Security Layers:**
- 🛡️ **Native Code** - Compiled to ARM/x86 assembly (hard to decompile)
- 🔐 **XOR Obfuscation** - Keys not visible in hex editors
- 📦 **Package Verification** - Keys only work with correct package name
- ✂️ **String Splitting** - No complete key in one location

See [security.md](docs/security.md) for detailed implementation guide.

---

## ⚡ Performance Optimizations

| Optimization | Benefit |
|:-------------|:--------|
| **Immutable UI State** | Prevents unintended state mutations |
| **Stable Composables** | Efficient recomposition |
| **Proper Coroutine Scoping** | No memory leaks |
| **Database Indices** | Fast query performance |
| **Image Caching** | Reduced network calls with Coil |
| **Offline-First** | Instant data from local cache |

---

## 📱 Navigation

```
App Navigation Graph
│
├── /login         →  Authentication screen (Start destination)
│                     ├── Email input
│                     ├── Password input
│                     └── Login button
│
├── /home          →  Recipes list screen
│                     ├── Search bar
│                     ├── Category chips
│                     └── Recipe grid
│
└── /recipe/{id}   →  Recipe detail screen
                      ├── Hero image
                      ├── Ingredients
                      └── Steps
```

---

## 🎨 Design Principles

### SOLID Principles Applied

| Principle | Application |
|:----------|:------------|
| **Single Responsibility** | Each class has one job: ViewModels manage UI state, UseCases contain business logic |
| **Open/Closed** | Repository interfaces allow extension without modification |
| **Liskov Substitution** | FakeRepository seamlessly replaces real implementation in tests |
| **Interface Segregation** | Small, focused interfaces (TokenProvider vs AuthManager) |
| **Dependency Inversion** | High-level modules depend on abstractions, not implementations |

### Domain Models

```kotlin
data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val servings: Int,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val difficulty: Difficulty,
    val category: String,
    val isFavorite: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList()
) {
    val totalTimeMinutes: Int
        get() = prepTimeMinutes + cookTimeMinutes
}

enum class Difficulty {
    EASY, MEDIUM, HARD;
    
    fun toDisplayString(): String = when (this) {
        EASY -> "Easy"
        MEDIUM -> "Medium"
        HARD -> "Hard"
    }
}
```

---

## 📚 Documentation

Detailed documentation is available in the `/docs` folder:

| Document | Description |
|:---------|:------------|
| [architecture.md](docs/architecture.md) | Clean Architecture deep dive |
| [modules.md](docs/modules.md) | Module structure and dependencies |
| [networking.md](docs/networking.md) | Network layer implementation |
| [security.md](docs/security.md) | Security: NDK keys, encryption, network |
| [testing.md](docs/testing.md) | Testing strategy and examples |
| [performance.md](docs/performance.md) | Performance optimization guide |
| [compose_guidelines.md](docs/compose_guidelines.md) | Jetpack Compose best practices |
| [interview_questions.md](docs/interview_questions.md) | Senior Android interview Q&A |

---

## 🧑‍💻 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push** to the branch
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open** a Pull Request

### Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful commit messages
- Add tests for new features
- Update documentation as needed

---

## 📄 License

```
MIT License

Copyright (c) 2024 BakingApp

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgments

- [Google Android Team](https://developer.android.com/) for Jetpack libraries
- [Unsplash](https://unsplash.com/) for sample images
- [Material Design](https://material.io/) for design guidelines
- The Android community for inspiration and best practices

---

<div align="center">

**Made with ❤️ using Clean Architecture**

⭐ Star this repo if you find it helpful!

</div>
