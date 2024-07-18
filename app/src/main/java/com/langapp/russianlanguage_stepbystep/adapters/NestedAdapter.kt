package com.langapp.russianlanguage_stepbystep.adapters

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.CollectionItem
import com.langapp.russianlanguage_stepbystep.utils.FillInBlanksEditText
import com.langapp.russianlanguage_stepbystep.utils.RecyclerItemClickListener
import com.squareup.picasso.Picasso

class NestedAdapter(private var context: Context, private var mList: List<CollectionItem>, private val itemClickListener: (CollectionItem) -> Unit) : RecyclerView.Adapter<NestedAdapter.NestedViewHolder>() {

    class NestedViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTv: TextView = itemView.findViewById(R.id.nestedItemTv)
        var mIv: ImageView = itemView.findViewById(R.id.nestedItemIv)
        var mEt: FillInBlanksEditText = itemView.findViewById(R.id.nestedItemEt)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.nested_item, parent, false)
        return NestedViewHolder(viewHolder)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val item = mList[position]
        holder.mTv.text = item.getDisplayName()

        if(item.sound.contains(".png")) {
            holder.mIv.visibility = View.VISIBLE
            Picasso.get().load(item.sound.toUri()).into(holder.mIv)
        }

        if(item.getDisplayName().contains("_____")) {
            holder.mEt.visibility = View.VISIBLE
            holder.mTv.visibility = View.GONE

            holder.mEt.imeOptions = EditorInfo.IME_ACTION_DONE
            holder.mEt.setRawInputType(InputType.TYPE_CLASS_TEXT)

            holder.mEt.setText(item.userInput ?: item.getDisplayName())
            if(holder.mEt.text.toString().lowercase() == item.answer?.lowercase())
                holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            else
                holder.mEt.setText(item.getDisplayName())

            holder.mEt.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(holder.mEt.text.toString().lowercase() == item.answer?.lowercase()) {
                        holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        item.userInput = holder.mEt.text.toString()
                        Log.d("editText", "${holder.mEt.text.toString().lowercase()}, ${item.answer?.lowercase()}")
                    }
                    else {
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
                        item.userInput = holder.mEt.text.toString()
                        Log.d("editText", "${holder.mEt.text.toString().lowercase()}, ${item.answer?.lowercase()}")
                    }
                    true
                } else {
                    false
                }
            }

        }

        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }
}