package recipeappbyhuili.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import recipeappbyhuili.R
import recipeappbyhuili.RecipeApplication
import recipeappbyhuili.viewmodel.RecipeDetailViewModel
import recipeappbyhuili.viewmodel.RecipeViewModel

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var layoutRecipeTitle : TextInputLayout
    private lateinit var editTextRecipeTitle : TextInputEditText
    private lateinit var layoutRecipeIngredients : TextInputLayout
    private lateinit var editTextRecipeIngredients : TextInputEditText
    private lateinit var layoutRecipeSteps : TextInputLayout
    private lateinit var editTextRecipeSteps : TextInputEditText
    private lateinit var btnSave : Button
    private lateinit var fabAction : FloatingActionButton

    private lateinit var viewmodel : RecipeDetailViewModel

    private var isEdited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        linkUI()
        initListeners()
        initViewModel()
        setupObservers()
    }

    private fun setEditMode(enabled: Boolean)
    {
        editTextRecipeTitle.isEnabled = enabled
        editTextRecipeIngredients.isEnabled = enabled
        editTextRecipeSteps.isEnabled = enabled

        if (enabled)
        {
            layoutRecipeTitle.boxStrokeColor = ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_primary_dark)
            layoutRecipeIngredients.boxStrokeColor = ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_primary_dark)
            layoutRecipeSteps.boxStrokeColor = ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_primary_dark)

            btnSave.visibility = View.VISIBLE
            fabAction.visibility = View.GONE
        }
        else
        {
            layoutRecipeTitle.boxStrokeColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)
            layoutRecipeIngredients.boxStrokeColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)
            layoutRecipeSteps.boxStrokeColor = ContextCompat.getColor(applicationContext, android.R.color.transparent)

            btnSave.visibility = View.GONE
            fabAction.visibility = View.VISIBLE
        }

    }

    private fun linkUI()
    {
        imageView = findViewById(R.id.img_image)
        layoutRecipeTitle = findViewById(R.id.til_title)
        editTextRecipeTitle = findViewById(R.id.et_title)
        layoutRecipeIngredients = findViewById(R.id.til_ingredients)
        editTextRecipeIngredients = findViewById(R.id.et_ingredients)
        layoutRecipeSteps = findViewById(R.id.til_steps)
        editTextRecipeSteps = findViewById(R.id.et_steps)
        btnSave = findViewById(R.id.btn_save)
        fabAction = findViewById(R.id.editFab)
    }

    private fun initListeners()
    {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEdited)
                {
                    setResult(Activity.RESULT_OK)
                }
                else
                {
                    setResult(Activity.RESULT_CANCELED)
                }

                finish()
            }
        })

        btnSave.setOnClickListener {
            val title = editTextRecipeTitle.text?.toString()?.trim()
            val ingredients = editTextRecipeIngredients.text?.toString()?.trim()
            val steps = editTextRecipeIngredients.text?.toString()?.trim()

            var isAllValid = true

            if (title.isNullOrEmpty())
            {
                layoutRecipeTitle.error = "Title is required"
                isAllValid = false
            }
            else
            {
                layoutRecipeTitle.error = null
            }

            if (ingredients.isNullOrEmpty())
            {
                layoutRecipeIngredients.error = "Ingredients is required"
                isAllValid = false
            }
            else
            {
                layoutRecipeIngredients.error = null
            }


            if (steps.isNullOrEmpty())
            {
                layoutRecipeSteps.error = "Steps is required"
                isAllValid = false
            }
            else
            {
                layoutRecipeSteps.error = null
            }

            if (isAllValid)
            {
                viewmodel.updateRecipe(title = title!!, ingredients = ingredients!!, steps = steps!!)
                setEditMode(false)
                isEdited = true
            }
        }

        fabAction.setOnClickListener {
            val popup = PopupMenu(this, fabAction)
            popup.menuInflater.inflate(R.menu.recipe_details_action_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_edit -> {
                        setEditMode(true)
                        true
                    }

                    R.id.action_delete -> {
                        viewmodel.deleteRecipe()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    private fun initViewModel()
    {
        val app = application as RecipeApplication

        viewmodel = ViewModelProvider(
            this,
            RecipeDetailViewModel.provideFactory(app.recipeRepository)
        )[RecipeDetailViewModel::class.java]

        viewmodel.loadRecipeDetailById(intent.getIntExtra("recipe_id", -1))
        viewmodel.newRecipeType = intent.getStringExtra("recipe_type")
    }

    private fun setupObservers()
    {
        viewmodel.selectedRecipeDetail.observe(this) { recipeDetail ->

            setEditMode(recipeDetail == null)

            recipeDetail?.let {
                editTextRecipeTitle.setText(it.title)
                editTextRecipeIngredients.setText(it.ingredients)
                editTextRecipeSteps.setText(it.steps)

                when
                {
                    it.imagePath == null ->
                    {
                        imageView.setImageResource(R.drawable.sample_default)
                    }

                    it.imagePath.startsWith("drawable/") ->
                    {
                        val resourceName = it.imagePath.removePrefix("drawable/")
                        val resId = resources.getIdentifier(resourceName, "drawable", packageName)
                        if (resId != 0) {
                            imageView.setImageResource(resId)
                        } else {
                            imageView.setImageResource(R.drawable.sample_default)
                        }
                    }
                }

            } ?: run {
                editTextRecipeTitle.setText("")
                editTextRecipeIngredients.setText("")
                editTextRecipeSteps.setText("")
            }
        }

        viewmodel.deleteSuccess.observe(this) { deleted ->
            if (deleted)
            {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}