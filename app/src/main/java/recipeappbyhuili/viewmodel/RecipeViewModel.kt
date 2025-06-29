package recipeappbyhuili.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import recipeappbyhuili.data.RecipeDetail
import recipeappbyhuili.data.RecipeRepository

class RecipeViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel()
{

    private val _recipeTypeList = MutableLiveData<List<String>?>()
    val recipeTypeList: LiveData<List<String>?> = _recipeTypeList

    private val _recipeList = MutableLiveData<Pair<String?, List<RecipeDetail>?>>()
    val recipeList: LiveData<Pair<String?, List<RecipeDetail>?>> = _recipeList

    private var masterRecipeTypeData: List<String>? = listOf()
    var selectedRecipeType: String? = null

    fun loadRecipeData(context: Context)
    {
        viewModelScope.launch(Dispatchers.IO) {
            masterRecipeTypeData = recipeRepository.loadRecipeTypesFromAssets(context)

            if (selectedRecipeType == null)
            {
                selectedRecipeType = masterRecipeTypeData?.get(0)
            }

            _recipeTypeList.postValue(masterRecipeTypeData)
        }
    }

    fun onRecipeTypeSelected(position: Int)
    {
        if (position in -1..(recipeTypeList.value?.size ?: 0))
        {
            masterRecipeTypeData?.let {

                selectedRecipeType = it[position]

                viewModelScope.launch {
                    val filteredRecipeList = recipeRepository.filterRecipeByCategory(it[position])

                    _recipeList.value = Pair(selectedRecipeType, filteredRecipeList)
                }

            }
        }
    }

    fun refreshRecipeList() {
        viewModelScope.launch {
            val filteredRecipeList = recipeRepository.filterRecipeByCategory(selectedRecipeType ?: "")

            _recipeList.value = Pair(selectedRecipeType, filteredRecipeList)
        }
    }

    fun getSelectedRecipeId(position: Int) : Int
    {
        return _recipeList.value?.second?.get(position)?.id ?: -1
    }

    companion object {
        fun provideFactory(repository: RecipeRepository): ViewModelProvider.Factory
        {
            return object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                        return RecipeViewModel(repository) as T
                    }

                    throw IllegalArgumentException("Unknown ViewModel class")
                }

            }
        }
    }
}