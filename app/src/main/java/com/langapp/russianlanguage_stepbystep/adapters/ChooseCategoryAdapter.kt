package com.langapp.russianlanguage_stepbystep.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.ExpandableModel

class ChooseCategoryAdapter(private var data: ArrayList<String>, private val itemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<ChooseCategoryAdapter.CategoryItemViewHolder>() {

    class CategoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTv: TextView = itemView.findViewById(R.id.itemTv)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryItemViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.phonetics_item, parent, false)
        return CategoryItemViewHolder(viewHolder)
    }

    override fun onBindViewHolder(
        holder: CategoryItemViewHolder,
        position: Int
    ) {
        val item = data[position]
        holder.itemTv.text = item

        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }

    override fun getItemCount(): Int = data.size

    fun setData(newItems: ArrayList<String>) {
        data = newItems
        notifyDataSetChanged()
    }
}