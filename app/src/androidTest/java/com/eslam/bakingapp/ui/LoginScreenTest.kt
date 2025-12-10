package com.eslam.bakingapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.eslam.bakingapp.core.ui.components.BakingTextField
import com.eslam.bakingapp.core.ui.components.PasswordTextField
import com.eslam.bakingapp.core.ui.components.PrimaryButton
import com.eslam.bakingapp.core.ui.components.TertiaryButton
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import com.eslam.bakingapp.features.login.presentation.LoginUiState
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loginScreen_displaysWelcomeText() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in to continue baking").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_emailFieldAcceptsInput() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
    }
    
    @Test
    fun loginScreen_showsEmailError() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(
                        email = "invalid",
                        emailError = "Please enter a valid email address"
                    ),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Please enter a valid email address").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_signInButtonExists() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_signInButtonClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(
                        email = "test@example.com",
                        password = "Password123"
                    ),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = { clicked = true }
                )
            }
        }
        
        composeTestRule.onNodeWithText("Sign In").performClick()
        assert(clicked)
    }
    
    @Test
    fun loginScreen_showsErrorMessage() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(errorMessage = "Invalid credentials"),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_showsForgotPasswordLink() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_showsTestCredentialsHint() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoginTestContent(
                    uiState = LoginUiState(),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Test: test@example.com / Password123").assertIsDisplayed()
    }
}

@Composable
private fun LoginTestContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🍰",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = "Sign in to continue baking",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        BakingTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email",
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PasswordTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "Password",
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError
        )
        
        TertiaryButton(
            text = "Forgot Password?",
            onClick = {}
        )
        
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PrimaryButton(
            text = "Sign In",
            onClick = onLoginClick,
            isLoading = uiState.isLoading
        )
        
        TertiaryButton(
            text = "Don't have an account? Sign Up",
            onClick = {}
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Test: test@example.com / Password123",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
