package recipeappbyhuili.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import recipeappbyhuili.OnRecipeListClickListener
import recipeappbyhuili.R
import recipeappbyhuili.RecipeApplication
import recipeappbyhuili.RecipeTitleAdapter
import recipeappbyhuili.viewmodel.RecipeViewModel

class MainActivity : AppCompatActivity(), OnRecipeListClickListener {

    private lateinit var recipeViewModel: RecipeViewModel

    private lateinit var spinner: Spinner
    private lateinit var fabCreate: FloatingActionButton

    private val recipeDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            recipeViewModel.refreshRecipeList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top + 45, systemBars.right, systemBars.bottom)
            insets
        }

        linkUI()
        setupClickListeners()
        initViewModel()
        setupObservers()
    }

    private fun setupObservers()
    {
        recipeViewModel.recipeTypeList.observe(this) {

            it?.let {  recipeTypeList ->

                val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item,
                    recipeTypeList)

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        recipeViewModel.onRecipeTypeSelected(position)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            }

        }

        recipeViewModel.recipeList.observe(this) { (category, recipeList) ->

            val textView = findViewById<TextView>(R.id.main_tv_recipe_name)
            val recyclerView: RecyclerView = findViewById(R.id.main_rv_recipe_list)

            if (recipeList.isNullOrEmpty())
            {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
                textView.text = "No recipe for $category found!"
            }
            else
            {
                recyclerView.visibility = View.VISIBLE
                textView.visibility = View.GONE

                val titleList = recipeList.map { it.title }.toTypedArray()

                recyclerView.adapter = RecipeTitleAdapter(titleList, this)
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

        }
    }

    private fun linkUI()
    {
        spinner = findViewById(R.id.spinner_recipe_type)
        fabCreate = findViewById(R.id.fabCreate)
    }

    private fun initViewModel()
    {
        val app = application as RecipeApplication

        recipeViewModel = ViewModelProvider(
            this,
            RecipeViewModel.provideFactory(app.recipeRepository)
        )[RecipeViewModel::class.java]

        recipeViewModel.loadRecipeData(applicationContext)
    }

    private fun setupClickListeners()
    {
        fabCreate.setOnClickListener {
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipe_id", -1)
            intent.putExtra("recipe_type", recipeViewModel.selectedRecipeType)
            recipeDetailLauncher.launch(intent)
        }
    }

    // ===== OnRecipeListClickListener =====
    override fun onRecipeClick(position: Int) {
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra("recipe_id", recipeViewModel.getSelectedRecipeId(position))
        recipeDetailLauncher.launch(intent)
    }
}