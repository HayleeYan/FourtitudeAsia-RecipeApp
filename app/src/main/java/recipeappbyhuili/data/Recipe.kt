package recipeappbyhuili.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "recipes")
data class RecipeDetail(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    var title: String,
    val ingredients: String,
    val steps: String,
    val imagePath: String? = null
)

@Dao
interface RecipeDao {
    @Insert suspend fun insert(recipe: RecipeDetail)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(recipe: RecipeDetail)

    @Delete suspend fun delete(recipe: RecipeDetail)

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeDetail>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeDetailFor(recipeId: Int): RecipeDetail

    @Query("SELECT * FROM recipes WHERE type = :type")
    fun getAllRecipesOfCategory(type: String): Flow<List<RecipeDetail>>

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)
}