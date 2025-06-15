package recipeappbyhuili.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import recipeappbyhuili.MainActivity
import recipeappbyhuili.data.RecipeType
import recipeappbyhuili.viewmodel.RecipeViewModel

class RecipeTypeDialog : DialogFragment() {

    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private var recipeTypeList: List<RecipeType>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        recipeTypeList = recipeViewModel.recipeTypeList.value

        isCancelable = false

        if (recipeTypeList.isNullOrEmpty())
        {
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Oops!")
                .setMessage("The application has failed to retrieve something important! Please try again later?")
                .setPositiveButton("OK", errorScenarioButtonAction)
                .create()
        }

        val recipeTypeDisplayList: Array<CharSequence> = recipeTypeList!!.map { it.label }.toTypedArray()

        var dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Recipe Types")
            .setSingleChoiceItems(recipeTypeDisplayList, recipeViewModel.selectedRecipeTypeIndex(), null)
            .setPositiveButton("Confirm", positiveButtonAction)

        if (recipeViewModel.selectedRecipeTypeIndex() > -1)
        {
            dialog.setNegativeButton("Cancel", negativeButtonAction)
        }

        return dialog.create()
    }

    private val positiveButtonAction: ((DialogInterface, Int) -> Unit) = { dialog, which ->
        recipeViewModel.onRecipeTypeSelected()
    }

    private val negativeButtonAction: ((DialogInterface, Int) -> Unit) = { dialog, which ->
        if (recipeViewModel.selectedRecipeTypeIndex() == -1)
        {
            Toast.makeText(context, "Please choose 1 to proceed!", Toast.LENGTH_SHORT).show()
        }
        else
        {
            dialog.dismiss()
        }
    }

    private val errorScenarioButtonAction: ((DialogInterface, Int) -> Unit) = { dialog, which ->
        requireActivity().finishAffinity()
    }

}