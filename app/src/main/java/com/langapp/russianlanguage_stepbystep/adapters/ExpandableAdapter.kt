package com.langapp.russianlanguage_stepbystep.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.Lessons
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.CollectionItem
import com.langapp.russianlanguage_stepbystep.models.ExpandableModel

class ExpandableAdapter(
    private var context: Context,
    private var mList: List<ExpandableModel>,
    private val itemClickListener: (CollectionItem) -> Unit,
    private val sectionToggleListener: ((Int, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<ExpandableAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linear_layout)
        val expandableLayout: RelativeLayout = itemView.findViewById(R.id.expandable_layout)
        val mTextView: TextView = itemView.findViewById(R.id.itemTv)
        val mArrowImage: ImageView = itemView.findViewById(R.id.arrow_imageview)
        val nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.child_rv)
        val cardView: CardView = itemView.findViewById(R.id.eachItemCardView)
        val question: ImageButton = itemView.findViewById(R.id.question_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val model: ExpandableModel = mList[position]
        holder.mTextView.text = model.getItemText()

        val isExpandable: Boolean = model.isExpandable()
        holder.expandableLayout.visibility = if(isExpandable) View.VISIBLE else View.GONE

        if(isExpandable) holder.mArrowImage.setImageResource(R.drawable.baseline_arrow_drop_up_24)
        else holder.mArrowImage.setImageResource(R.drawable.baseline_arrow_drop_down_24)

        if(!model.isAnimated) {
            holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.cardView.context, R.anim.recycler_in_animation))
            model.isAnimated = true
        }

        if(model.explanationList.isNotEmpty() && model.explanationList[position] != 0) {
            holder.question.visibility = View.VISIBLE
            holder.question.setOnClickListener { showDialog(model.explanationList[position]) }
        }

        val nestedAdapter = NestedAdapter(context, model.getNestedList(), itemClickListener)
        holder.nestedRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.nestedRecyclerView.setHasFixedSize(true)
        holder.nestedRecyclerView.adapter = nestedAdapter
        holder.linearLayout.setOnClickListener {

            model.setExpandable(!model.isExpandable())
            notifyItemChanged(holder.adapterPosition)

            sectionToggleListener?.invoke(holder.adapterPosition, model.isExpandable())
        }
    }

    private fun showDialog(layout: Int) {

        val dialogInflater = LayoutInflater.from(context).inflate(layout, null)

        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogInflater)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            show()
        }

        dialogInflater.findViewById<Button>(R.id.ok_button).setOnClickListener {
            dialog.dismiss()
        }
    }

    fun setData(newItems: List<ExpandableModel>) {
        mList = newItems
        notifyDataSetChanged()
    }

    fun updateOpenedSections(openedSections: Set<Int>) {
        mList.forEachIndexed { index, expandableModel ->
            expandableModel.setExpandable(openedSections.contains(index))
        }
        notifyDataSetChanged()
    }

}