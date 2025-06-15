package recipeappbyhuili.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import recipeappbyhuili.data.RawRecipeDAO
import recipeappbyhuili.data.RecipeDetail
import recipeappbyhuili.data.RecipeRepository
import recipeappbyhuili.data.RecipeType
import recipeappbyhuili.data.toRecipeType

class RecipeViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel()
{

    private val _recipeTypeList = MutableLiveData<List<RecipeType>?>()
    private val _recipeList = MutableLiveData<List<RecipeDetail>?>()

    val recipeTypeList: LiveData<List<RecipeType>?> = _recipeTypeList
    val recipeList: LiveData<List<RecipeDetail>?> = _recipeList

    private var masterRecipeData: ArrayList<RawRecipeDAO>? = null
    private var selectedRecipeType: RecipeType? = null

    fun loadRecipeData()
    {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedRecipeTypeList = recipeRepository.loadRecipeTypesFromAssets()

            masterRecipeData = if (loadedRecipeTypeList.isNotEmpty())
            {
                ArrayList(loadedRecipeTypeList)
            }
            else
            {
                null
            }

            val recipeTypeList = masterRecipeData?.map { it.toRecipeType() }

            _recipeTypeList.postValue(recipeTypeList)
        }
    }

    fun onRecipeTypeSelected(position: Int)
    {
        if (position in -1..(recipeTypeList.value?.size ?: 0))
        {
            selectedRecipeType = recipeTypeList.value?.get(position)

            val recipeList =  masterRecipeData?.firstOrNull { it.type == selectedRecipeType?.type }

            if (recipeList != null)
            {
                _recipeList.value = recipeList.list
            }
        }
    }

    fun selectedRecipeTypeIndex(): Int
    {
        return if (selectedRecipeType == null)
        {
            -1
        }
        else
        {
            selectedRecipeType?.let { recipeTypeList.value?.indexOf(it) } ?: -1
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory
        {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = RecipeRepository(context.applicationContext)

                    if (modelClass.isAssignableFrom(RecipeViewModel::class.java))
                    {
                        @Suppress("UNCHECKED_CAST")
                        return RecipeViewModel(repository) as T
                    }

                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}