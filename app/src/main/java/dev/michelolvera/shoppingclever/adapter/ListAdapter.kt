package dev.michelolvera.shoppingclever.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.michelolvera.shoppingclever.R
import dev.michelolvera.shoppingclever.model.ListModel
import kotlinx.android.synthetic.main.list_item.view.*

class ListAdapter (private val lists: Array<ListModel> = emptyArray()): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    class ListViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.itemView.button_text.text = lists[position].title
    }

    override fun getItemCount(): Int = lists.size
}