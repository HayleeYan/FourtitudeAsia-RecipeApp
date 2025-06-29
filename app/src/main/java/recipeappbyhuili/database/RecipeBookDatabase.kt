package recipeappbyhuili.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import recipeappbyhuili.data.RecipeDao
import recipeappbyhuili.data.RecipeDetail

@Database(entities = [RecipeDetail::class], version = 1)
abstract class RecipeBookDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var INSTANCE: RecipeBookDatabase? = null

        fun getInstance(context: Context): RecipeBookDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RecipeBookDatabase::class.java,
                    "recipebook_db"
                ).addCallback(DatabaseCallback(context))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Default data
            CoroutineScope(Dispatchers.IO).launch {
                getInstance(context).recipeDao().apply {
                    insert(RecipeDetail(
                        type = "appetizer",
                        title = "Roti Jala",
                        ingredients = "2 cups all-purpose flour\n1 ½ cups coconut milk\n1 cup water\n1 egg\n½ tsp turmeric powder\n½ tsp salt\nCooking oil (for greasing)",
                        steps = "Mix flour, coconut milk, water, egg, turmeric, and salt into a smooth batter.\n" +
                                "Pour into a squeeze bottle or roti jala mould.\n" +
                                "Heat a non-stick pan, lightly oil it.\n" +
                                "Squeeze batter in a criss-cross pattern.\n" +
                                "Cook 1–2 minutes, no flipping needed.\n" +
                                "Fold into triangles and serve.",
                        imagePath = "drawable/sample_roti_jala"))

                    insert(RecipeDetail(
                        type = "appetizer",
                        title = "Keropok Lekor",
                        ingredients = "250g fish fillet (mackerel or sardine)\n" +
                                "150g sago flour (or tapioca flour)\n" +
                                "1 tsp salt\n" +
                                "Water as needed\n" +
                                "Oil (for frying)",
                        steps = "Blend fish into a paste.\n" +
                                "Mix with flour and salt to form a dough.\n" +
                                "Shape into sausage-like rolls.\n" +
                                "Boil until they float, then cool.\n" +
                                "Slice and deep fry until golden and crispy.",
                        imagePath = "drawable/sample_keropok_lekor"))

                    insert(RecipeDetail(
                        type = "mains",
                        title = "Nasi Lemak",
                        ingredients = "1 cup rice\n" +
                                "1 cup coconut milk\n" +
                                "1 cup water\n" +
                                "1 pandan leaf (optional)\n" +
                                "Salt (a pinch)\n" +
                                "Boiled egg, cucumber slices, roasted peanuts, fried anchovies (for sides)\n" +
                                "Sambal (can be store-bought or homemade)",
                        steps = "Rinse rice, then cook with coconut milk, water, salt, and pandan leaf.\n" +
                                "Serve rice with boiled egg, cucumber, peanuts, anchovies, and sambal.",
                        imagePath = "drawable/sample_nasi_lemak"))

                    insert(RecipeDetail(
                        type = "mains",
                        title = "Mee Goreng Mamak",
                        ingredients = "Yellow noodles (1 pack)\n" +
                                "Garlic (2 cloves, chopped)\n" +
                                "Onion (1, sliced)\n" +
                                "Egg (1)\n" +
                                "Tofu (optional)\n" +
                                "Soy sauce (2 tbsp)\n" +
                                "Chili sauce (1 tbsp)\n" +
                                "Tomato ketchup (1 tbsp)\n" +
                                "Lime (optional)\n" +
                                "Veggies (like cabbage or bean sprouts)",
                        steps = "Heat oil, fry garlic and onion.\n" +
                                "Add tofu, veggies, and stir.\n" +
                                "Push to side, scramble egg, then mix in noodles.\n" +
                                "Add sauces, stir-fry all together.\n" +
                                "Squeeze lime if desired before serving.",
                        imagePath = "drawable/sample_mee_goreng_mamak"))

                    insert(RecipeDetail(
                        type = "beverage",
                        title = "Teh Tarik",
                        ingredients = "Black tea (1 bag or 1 tsp loose)\n" +
                                "Hot water (1 cup)\n" +
                                "Condensed milk (2 tbsp)\n" +
                                "Evaporated milk (2 tbsp)",
                        steps = "Brew tea in hot water for 3–5 mins.\n" +
                                "Add condensed and evaporated milk.\n" +
                                "“Pull” the tea by pouring it back and forth between two cups until frothy.\n" +
                                "Serve hot.",
                        imagePath = "drawable/sample_teh_tarik"))
                }
            }
        }
    }
}