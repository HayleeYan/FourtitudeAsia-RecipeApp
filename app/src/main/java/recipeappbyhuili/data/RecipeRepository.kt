package recipeappbyhuili.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class RecipeRepository(private val context: Context) {

    fun loadRecipeTypesFromAssets() : List<RawRecipeDAO>
    {
        try {
            val originalJson = context.assets.open("recipetypes.json").bufferedReader().use { it.readText() }
            val rawRecipeDAO: List<RawRecipeDAO> = Gson().fromJson(originalJson, object: TypeToken<List<RawRecipeDAO>>() {}.type)
            return rawRecipeDAO
        }
        catch (ioException: IOException)
        {
            Log.e("", "EncounteredI IOException in RecipeRepository")
        }

        return emptyList()
    }


}