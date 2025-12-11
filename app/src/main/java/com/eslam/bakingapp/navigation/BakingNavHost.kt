package com.eslam.bakingapp.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eslam.bakingapp.features.cookingtimer.presentation.activity.CookingTimerActivity
import com.eslam.bakingapp.features.home.presentation.HomeScreen
import com.eslam.bakingapp.features.login.presentation.LoginScreen
import com.eslam.bakingapp.features.recipe_details.presentation.RecipeDetailScreen

/**
 * Navigation routes for the app.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val RECIPE_DETAIL = "recipe/{recipeId}"
    
    fun recipeDetail(recipeId: String) = "recipe/$recipeId"
}

/**
 * Helper function to launch the Cooking Timer Activity.
 */
private fun launchCookingTimer(context: Context) {
    context.startActivity(CookingTimerActivity.createIntent(context))
}

/**
 * Main navigation host for the BakingApp.
 * Manages all navigation between screens.
 */
@Composable
fun BakingNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        // Login Screen
        composable(route = Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // TODO: Navigate to register screen
                }
            )
        }
        
        // Home Screen
        composable(route = Routes.HOME) {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                onTimerClick = {
                    launchCookingTimer(context)
                }
            )
        }
        
        // Recipe Detail Screen
        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) {
            RecipeDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartTimer = { recipeName, cookingTime ->
                    // Launch the cooking timer activity
                    // The timer will be created with the recipe name and cooking time
                    launchCookingTimer(context)
                }
            )
        }
    }
}



