package recipeappbyhuili

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnRecipeListClickListener {
    fun onRecipeClick(position: Int)
}

class RecipeTitleAdapter(
    private val dataSet: Array<String>,
    private val listener: OnRecipeListClickListener
): RecyclerView.Adapter<RecipeTitleAdapter.ViewHolder>()
{
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.viewholder_tv_title)

            view.setOnClickListener {
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION)
                {
                    listener.onRecipeClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeTitleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_recipe_title, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeTitleAdapter.ViewHolder, position: Int) {

        holder.textView.text = dataSet[position]
    }

    override fun getItemCount(): Int = dataSet.size

}