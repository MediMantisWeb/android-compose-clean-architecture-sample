package com.eslam.bakingapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.eslam.bakingapp.core.ui.components.BakingTextField
import com.eslam.bakingapp.core.ui.components.ErrorView
import com.eslam.bakingapp.core.ui.components.FullScreenLoading
import com.eslam.bakingapp.core.ui.components.RecipeCard
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.presentation.HomeUiState
import com.eslam.bakingapp.features.home.presentation.RecipeCategories
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val testRecipes = listOf(
        Recipe(
            id = "1",
            name = "Chocolate Chip Cookies",
            description = "Delicious homemade cookies",
            imageUrl = null,
            servings = 24,
            prepTimeMinutes = 15,
            cookTimeMinutes = 12,
            difficulty = Difficulty.EASY,
            category = "Cookies"
        ),
        Recipe(
            id = "2",
            name = "Red Velvet Cupcakes",
            description = "Beautiful red velvet cupcakes",
            imageUrl = null,
            servings = 12,
            prepTimeMinutes = 20,
            cookTimeMinutes = 22,
            difficulty = Difficulty.MEDIUM,
            category = "Cupcakes"
        )
    )
    
    @Test
    fun homeScreen_displaysAppTitle() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = testRecipes),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("🍰 BakingApp").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_displaysSearchField() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = testRecipes),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Search recipes...").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_displaysRecipeCards() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = testRecipes),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Chocolate Chip Cookies").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_displaysCategoryChips() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = testRecipes),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_showsLoadingIndicator() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(isLoading = true, recipes = emptyList()),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Loading delicious recipes...").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_showsEmptyState() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = emptyList(), isLoading = false),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("No recipes yet").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_showsErrorState() {
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(
                        recipes = emptyList(),
                        errorMessage = "Network error",
                        isLoading = false
                    ),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Couldn't load recipes").assertIsDisplayed()
    }
    
    @Test
    fun homeScreen_recipeCardClickable() {
        var clickedRecipeId: String? = null
        
        composeTestRule.setContent {
            BakingAppTheme {
                HomeTestContent(
                    uiState = HomeUiState(recipes = testRecipes),
                    onSearchQueryChange = {},
                    onCategorySelected = {},
                    onRecipeClick = { clickedRecipeId = it },
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Chocolate Chip Cookies").performClick()
        assert(clickedRecipeId == "1")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTestContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🍰 BakingApp",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.recipes.isEmpty() -> {
                FullScreenLoading(
                    message = "Loading delicious recipes...",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            uiState.errorMessage != null && uiState.recipes.isEmpty() -> {
                ErrorView(
                    title = "Couldn't load recipes",
                    message = uiState.errorMessage ?: "",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        BakingTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = "Search recipes..."
                        )
                    }
                    
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(RecipeCategories.size) { index ->
                                val category = RecipeCategories[index]
                                FilterChip(
                                    selected = category == (uiState.selectedCategory ?: "All"),
                                    onClick = { onCategorySelected(if (category == "All") null else category) },
                                    label = { Text(category) }
                                )
                            }
                        }
                    }
                    
                    if (uiState.recipes.isEmpty() && !uiState.isLoading) {
                        item {
                            Text(
                                text = if (uiState.searchQuery.isNotBlank()) {
                                    "No recipes found for \"${uiState.searchQuery}\""
                                } else {
                                    "No recipes yet"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    items(uiState.recipes.size) { index ->
                        val recipe = uiState.recipes[index]
                        RecipeCard(
                            name = recipe.name,
                            description = recipe.description,
                            imageUrl = recipe.imageUrl,
                            prepTimeMinutes = recipe.prepTimeMinutes,
                            cookTimeMinutes = recipe.cookTimeMinutes,
                            servings = recipe.servings,
                            difficulty = recipe.difficulty.toDisplayString(),
                            isFavorite = recipe.isFavorite,
                            onCardClick = { onRecipeClick(recipe.id) },
                            onFavoriteClick = { onFavoriteClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}
