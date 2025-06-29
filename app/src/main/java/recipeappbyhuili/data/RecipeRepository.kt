package recipeappbyhuili.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.io.IOException

class RecipeRepository(private val dao: RecipeDao) {

    val recipeList = dao.getAllRecipes()

    suspend fun insert(recipe: RecipeDetail) = dao.insert(recipe)
    suspend fun delete(recipe: RecipeDetail) = dao.delete(recipe)

    suspend fun filterRecipeByCategory(category: String): List<RecipeDetail>
    {
        return dao.getAllRecipesOfCategory(category).first()
    }

    suspend fun filterRecipeById(id: Int) : RecipeDetail?
    {
        return dao.getRecipeDetailFor(id)
    }

    suspend fun update(recipe: RecipeDetail) = dao.update(recipe)

    suspend fun deleteRecipeById(id: Int)
    {
        dao.deleteRecipeById(id)
    }

    fun loadRecipeTypesFromAssets(context: Context) : List<String>
    {
        try {
            val originalJson = context.assets.open("recipetypes.json").bufferedReader().use { it.readText() }
            val rawRecipeDAO: List<String> = Gson().fromJson(originalJson, object: TypeToken<List<String>>() {}.type)
            return rawRecipeDAO
        }
        catch (ioException: IOException)
        {
            Log.e("", "Encountered IOException in RecipeRepository")
        }

        return emptyList()
    }
}