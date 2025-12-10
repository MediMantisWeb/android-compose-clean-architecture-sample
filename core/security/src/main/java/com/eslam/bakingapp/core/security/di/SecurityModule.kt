package com.eslam.bakingapp.core.security.di

import com.eslam.bakingapp.core.network.interceptor.TokenProvider
import com.eslam.bakingapp.core.security.ApiKeyProvider
import com.eslam.bakingapp.core.security.DefaultApiKeyProvider
import com.eslam.bakingapp.core.security.NativeKeyProvider
import com.eslam.bakingapp.core.security.SecureTokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing security-related dependencies.
 *
 * Provides:
 * - [TokenProvider] for authentication token management
 * - [ApiKeyProvider] for secure API key access via native code
 * - [NativeKeyProvider] for direct native library access
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        secureTokenManager: SecureTokenManager
    ): TokenProvider

    companion object {
        /**
         * Provides the ApiKeyProvider implementation.
         *
         * Uses [DefaultApiKeyProvider] which delegates to [NativeKeyProvider]
         * for secure API key storage in native code.
         */
        @Provides
        @Singleton
        fun provideApiKeyProvider(
            nativeKeyProvider: NativeKeyProvider
        ): ApiKeyProvider {
            return DefaultApiKeyProvider(nativeKeyProvider)
        }
    }
}
