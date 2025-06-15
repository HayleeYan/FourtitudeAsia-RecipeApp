package recipeappbyhuili

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import recipeappbyhuili.dialog.RecipeTypeDialog
import recipeappbyhuili.viewmodel.RecipeViewModel

class MainActivity : AppCompatActivity() {

    private val RECIPE_DIALOG_TAG = "recipe_type_list_dialog"

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeTypeDialog: RecipeTypeDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top + 45, systemBars.right, systemBars.bottom)
            insets
        }

        recipeViewModel = ViewModelProvider(this, RecipeViewModel.provideFactory(applicationContext))[RecipeViewModel::class.java]
        recipeViewModel.loadRecipeData()
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers()
    {
        recipeViewModel.recipeTypeList.observe(this) { recipeTypeListResult ->

            recipeTypeListResult?.let {
                recipeTypeDialog = RecipeTypeDialog()
                recipeTypeDialog.show(supportFragmentManager, RECIPE_DIALOG_TAG)
            }
        }
    }

    private fun setupClickListeners()
    {
        findViewById<Button>(R.id.main_btn_filter).setOnClickListener {
            recipeTypeDialog.show(supportFragmentManager, RECIPE_DIALOG_TAG)
        }
    }
}