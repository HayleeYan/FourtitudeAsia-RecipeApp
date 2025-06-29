package recipeappbyhuili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import recipeappbyhuili.data.RecipeDetail
import recipeappbyhuili.data.RecipeRepository

class RecipeDetailViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _selectedRecipeDetail = MutableLiveData<RecipeDetail?>()
    val selectedRecipeDetail: LiveData<RecipeDetail?> = _selectedRecipeDetail

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    private var currentRecipeId : Int = -1
    var newRecipeType: String? = null // For Create use

    fun loadRecipeDetailById(id: Int)
    {
        // Search for possible
        if (id > -1)
        {
            viewModelScope.launch {
                val result = repository.filterRecipeById(id)

                if (result != null)
                {
                    currentRecipeId = id
                }

                _selectedRecipeDetail.postValue(result)
            }
        }
        else
        {
            // Create NEW
            _selectedRecipeDetail.value = null
        }
    }

    fun deleteRecipe(){

        if (currentRecipeId > -1)
        {
            viewModelScope.launch {
                repository.deleteRecipeById(currentRecipeId)
                _deleteSuccess.value = true
            }
        }
    }

    fun updateRecipe(title: String, ingredients: String, steps: String){

        if (currentRecipeId > -1)
        {
            _selectedRecipeDetail.value?.let {
                val updatedRecipe = RecipeDetail(
                    type = it.type,
                    id = it.id,
                    title = title,
                    ingredients = ingredients,
                    steps = steps,
                )

                viewModelScope.launch {
                    repository.update(updatedRecipe)
                }
            }
        }
        else
        {
            val newRecipe = RecipeDetail(
                type = newRecipeType ?: "",
                title = title,
                ingredients = ingredients,
                steps = steps,
            )

            viewModelScope.launch {
                repository.insert(newRecipe)
            }
        }
    }

    companion object {
        fun provideFactory(repository: RecipeRepository): ViewModelProvider.Factory
        {
            return object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
                        return RecipeDetailViewModel(repository) as T
                    }

                    throw IllegalArgumentException("Unknown ViewModel class")
                }

            }
        }
    }
}