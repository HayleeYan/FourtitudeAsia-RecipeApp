package recipeappbyhuili.data

data class RecipeType(val type: String,
                      val label: String)

data class RecipeDetail(val name: String,
                        val ingredients: List<String>,
                        val instructions: List<String>)

data class RawRecipeDAO(
    val type: String,
    val display_en: String?,
    val list: List<RecipeDetail>?
)

fun RawRecipeDAO.toRecipeType(currentLanguage: String = "en"): RecipeType
{
    val fallback = "failed_to_read"

    val display = when (currentLanguage.lowercase())
    {
        else -> display_en ?: fallback
    }

    return RecipeType(type, display)
}