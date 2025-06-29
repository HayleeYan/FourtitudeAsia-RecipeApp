package recipeappbyhuili

import android.app.Application
import recipeappbyhuili.data.RecipeRepository
import recipeappbyhuili.database.RecipeBookDatabase

class RecipeApplication : Application() {
    val recipeDatabase by lazy { RecipeBookDatabase.getInstance(this) }
    val recipeRepository by lazy { RecipeRepository(recipeDatabase.recipeDao()) }
}