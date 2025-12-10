# 🔐 Security Best Practices

## Overview

BakingApp implements enterprise-grade security measures:

- **Native API Key Storage** using NDK/C++ with XOR obfuscation
- **Encrypted storage** for sensitive data
- **Certificate pinning** for network security
- **Secure token management**
- **No sensitive data logging**

---

## 🔑 Native API Key Storage (NDK/C++)

### The Problem

Storing API keys in Android apps is challenging because:

1. **BuildConfig/Strings** - Keys are easily extracted from decompiled APKs
2. **Plain SharedPreferences** - Not encrypted, easily readable
3. **Even EncryptedSharedPreferences** - Keys still need to be stored somewhere initially

### The Solution Hierarchy

| Level | Approach | Security | Difficulty |
|:------|:---------|:---------|:-----------|
| 1 | `local.properties` + BuildConfig | 🔴 Low | Easy |
| 2 | **NDK + C++ (Implemented)** | 🟡 Medium-High | Medium |
| 3 | Backend Proxy / Token Exchange | 🟢 High | Complex |
| 4 | Provider-Side Restrictions | 🟢 Mandatory | Easy |

### Implementation: Level 2 - NDK/C++ Approach

BakingApp uses the **Native Development Kit (NDK)** to store API keys in C++ code with XOR obfuscation. This makes reverse engineering significantly harder because:

- Decompiling Java/Kotlin produces readable code
- Decompiling C++ produces ARM/x86 assembly, which is much harder to understand

#### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                     │
├─────────────────────────────────────────────────────────┤
│  @Inject lateinit var apiKeyProvider: ApiKeyProvider    │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              Hilt Dependency Injection                   │
├─────────────────────────────────────────────────────────┤
│  SecurityModule.provideApiKeyProvider()                 │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                  ApiKeyProvider                          │
│                   (Interface)                            │
├─────────────────────────────────────────────────────────┤
│  + getApiKey(): String                                  │
│  + getSecretKey(): String                               │
│  + getAppIdentifier(): String                           │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│               NativeKeyProvider                          │
│                 (Kotlin)                                 │
├─────────────────────────────────────────────────────────┤
│  System.loadLibrary("native-keys")                      │
│  external fun getApiKeyNative(context): String          │
└────────────────────────┬────────────────────────────────┘
                         │ JNI
┌────────────────────────▼────────────────────────────────┐
│               native-keys.cpp                            │
│                  (C++)                                   │
├─────────────────────────────────────────────────────────┤
│  XOR-encoded keys                                       │
│  Package name verification                              │
│  Runtime decoding                                       │
└─────────────────────────────────────────────────────────┘
```

#### Security Layers

1. **Native Code Storage** - Keys stored in ARM/x86 assembly after compilation
2. **XOR Obfuscation** - Keys not visible in hex editors or string dumps
3. **String Splitting** - No complete key appears in one location
4. **Package Verification** - Keys only work with the correct package name

#### Usage

```kotlin
@AndroidEntryPoint
class MyActivity : AppCompatActivity() {
    
    @Inject
    lateinit var apiKeyProvider: ApiKeyProvider
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get API keys securely from native code
        val apiKey = apiKeyProvider.getApiKey()
        val secretKey = apiKeyProvider.getSecretKey()
        
        // Initialize your SDK
        PaymentSDK.initialize(apiKey, secretKey)
    }
}
```

#### Safe Access Pattern

```kotlin
// Returns null if native library failed to load
val apiKey = apiKeyProvider.getApiKeyOrNull() ?: run {
    Log.e(TAG, "API key not available")
    showError("Configuration error")
    return
}

// Check availability before accessing
if (apiKeyProvider.isAvailable()) {
    initializeSDK(apiKeyProvider.getApiKey())
}
```

#### How XOR Obfuscation Works

```
Original Key:    "bk_fake_api"
XOR Key:         0x5A (90 in decimal)

Encoding:
'b' (0x62) XOR 0x5A = 0x38
'k' (0x6B) XOR 0x5A = 0x31
'_' (0x5F) XOR 0x5A = 0x05
...

Stored as: { 0x38, 0x31, 0x05, ... }

At runtime, XOR again to decode:
0x38 XOR 0x5A = 0x62 = 'b'
```

#### Encoding New Keys

Use the provided Python script:

```bash
cd core/security/scripts

# Encode an API key
python encode_keys.py "your_real_api_key" 0x5A --verify

# Example output:
# C++ vector initializer:
# const std::vector<char> YOUR_KEY_ENCODED = {
#     0x2f, 0x35, 0x31, 0x28, 0x1a, 0x3f, 0x3d, 0x28
#     ...
# };
```

Then update `native-keys.cpp` with the encoded values.

#### File Structure

```
core/security/
├── src/main/
│   ├── cpp/
│   │   ├── CMakeLists.txt         # CMake build configuration
│   │   └── native-keys.cpp        # Native key storage
│   └── java/.../security/
│       ├── ApiKeyProvider.kt      # Public interface
│       ├── NativeKeyProvider.kt   # JNI bridge
│       └── di/SecurityModule.kt   # Hilt bindings
├── scripts/
│   └── encode_keys.py             # Key encoding utility
├── proguard-rules.pro             # ProGuard rules
└── consumer-rules.pro             # Consumer ProGuard rules
```

#### Build Requirements

- **NDK**: Install via SDK Manager → SDK Tools → NDK
- **CMake**: Version 3.22.1+ (also via SDK Manager)

Supported ABIs:
- `arm64-v8a` (64-bit ARM, most modern devices)
- `armeabi-v7a` (32-bit ARM, older devices)
- `x86_64` (Emulators, Chromebooks)
- `x86` (Older emulators)

#### CI/CD Integration

For production, inject real keys during the build:

```yaml
# GitHub Actions example
- name: Inject API Keys
  run: |
    python core/security/scripts/encode_keys.py "${{ secrets.API_KEY }}" 0x5A > /tmp/api_key.txt
    # Update native-keys.cpp with the encoded values
```

### Level 3: Backend Proxy (Architect Level)

For maximum security, never put the real secret in the app:

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   App    │────►│ Your Backend │────►│ SDK Provider │────►│   Response   │
│          │     │ (has secret) │     │    API       │     │              │
└──────────┘     └──────────────┘     └──────────────┘     └──────────────┘
     │                  │
     │  temp token      │ real secret
     │◄─────────────────│
```

1. App authenticates user with your backend
2. Backend (holding the real secret) calls SDK provider
3. Backend generates a short-lived token
4. App uses temporary token (expires in 15 minutes)

### Level 4: Provider-Side Restrictions (Mandatory)

Always apply these restrictions in your API provider's console:

| Restriction | Description |
|:------------|:------------|
| **Package Name** | Restrict to `com.eslam.bakingapp` |
| **SHA-1 Fingerprint** | Restrict to your signing certificate |
| **API Scope** | Limit to only required APIs |
| **Quota Limits** | Set usage caps to limit damage |

```
Google Cloud Console → APIs & Services → Credentials
├── Application restrictions: Android apps
│   ├── Package name: com.eslam.bakingapp
│   └── SHA-1: XX:XX:XX:...
└── API restrictions: Restrict to Maps SDK only
```

---

## Encrypted Storage

### EncryptedSharedPreferences

```kotlin
@Singleton
class EncryptedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
```

### What to Store Encrypted

| Data Type | Store Encrypted? | Example |
|-----------|-----------------|---------|
| Auth Tokens | ✅ Yes | Access token, refresh token |
| User Credentials | ✅ Yes | Session data |
| API Keys (runtime) | ✅ Yes | Third-party service keys |
| User Preferences | ❌ No | Theme, language |
| Cached Data | ❌ No | Recipe data |

---

## Token Management

```kotlin
@Singleton
class SecureTokenManager @Inject constructor(
    private val encryptedPrefs: EncryptedPreferencesManager
) : TokenProvider {
    
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        encryptedPrefs.putString(KEY_ACCESS_TOKEN, accessToken)
        encryptedPrefs.putString(KEY_REFRESH_TOKEN, refreshToken)
        encryptedPrefs.putLong(KEY_TOKEN_EXPIRY, expiryTime)
    }
    
    fun isTokenExpired(): Boolean {
        val expiryTime = encryptedPrefs.getLong(KEY_TOKEN_EXPIRY, 0L)
        return System.currentTimeMillis() >= expiryTime
    }
    
    fun clearAll() {
        encryptedPrefs.remove(KEY_ACCESS_TOKEN)
        encryptedPrefs.remove(KEY_REFRESH_TOKEN)
        encryptedPrefs.remove(KEY_TOKEN_EXPIRY)
    }
}
```

---

## Network Security

### Certificate Pinning

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.bakingapp.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### Network Security Config

```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.bakingapp.com</domain>
        <pin-set expiration="2025-12-31">
            <pin digest="SHA-256">base64-encoded-pin</pin>
            <pin digest="SHA-256">backup-pin</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

### AndroidManifest Configuration

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="false">
```

---

## Logging Security

### Debug-Only Logging

```kotlin
@Provides
fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}
```

### Redacting Sensitive Headers

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    redactHeader("Authorization")
    redactHeader("Cookie")
}
```

---

## Security Checklist

### API Key Security
- [x] Use NDK/C++ for API key storage
- [x] Apply XOR obfuscation to keys
- [x] Implement package name verification
- [x] Add ProGuard rules for native methods
- [ ] Configure provider-side restrictions

### Token Security
- [x] Use EncryptedSharedPreferences for tokens
- [x] Implement token expiry checking
- [x] Clear tokens on logout
- [x] Implement token refresh

### Network Security
- [x] Disable cleartext traffic
- [ ] Implement certificate pinning
- [x] Redact sensitive headers in logs
- [x] No sensitive data in logs

### Build Security
- [x] Use ProGuard/R8 for release builds
- [x] Strip debug symbols from native code
- [ ] Implement root detection (optional)
- [ ] Implement tamper detection (optional)

---

## Anti-Patterns to Avoid

```kotlin
// ❌ DON'T: Hardcode API keys in Kotlin/Java
const val API_KEY = "abc123secret"

// ✅ DO: Use Native Key Provider
val apiKey = apiKeyProvider.getApiKey()

// ❌ DON'T: Log sensitive data
Log.d("Auth", "Token: $accessToken")

// ✅ DO: Log only non-sensitive info
Log.d("Auth", "Token received, length: ${accessToken.length}")

// ❌ DON'T: Store tokens in regular SharedPreferences
val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
prefs.edit().putString("token", token).apply()

// ✅ DO: Use encrypted storage
encryptedPrefs.putString("token", token)

// ❌ DON'T: Store keys in strings.xml or BuildConfig
<string name="api_key">secret123</string>

// ✅ DO: Use native storage with obfuscation
apiKeyProvider.getApiKey()
```

---

## Testing Security

### Mock Implementation for Tests

```kotlin
class FakeApiKeyProvider : ApiKeyProvider {
    override fun isAvailable() = true
    override fun getApiKey() = "test_api_key"
    override fun getSecretKey() = "test_secret_key"
    override fun getAppIdentifier() = "test_app_v1"
    override fun validateKeyFormat(key: String) = true
    override fun getApiKeyOrNull() = getApiKey()
    override fun getSecretKeyOrNull() = getSecretKey()
}

// In Hilt test module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SecurityModule::class]
)
@Module
class TestSecurityModule {
    @Provides
    @Singleton
    fun provideApiKeyProvider(): ApiKeyProvider = FakeApiKeyProvider()
}
```

---

## Further Reading

- [Android NDK Documentation](https://developer.android.com/ndk)
- [JNI Tips](https://developer.android.com/training/articles/perf-jni)
- [Network Security Config](https://developer.android.com/training/articles/security-config)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-top-10/)
