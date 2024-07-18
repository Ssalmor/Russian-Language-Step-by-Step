package com.langapp.russianlanguage_stepbystep.adapters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.TextModel

class DialogAdapter(private var context: Context, private var data: List<TextModel>) : RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    private var itemsVisibleCount = 1

    class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val itemTv: TextView = itemView.findViewById(R.id.itemTv)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DialogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.phonetics_item, parent, false)
        return DialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        if (position >= data.size) {
            return
        }

        val text = data[position].text
        val sound = data[position].sound

        holder.itemTv.text = text
        holder.cardView.setOnClickListener {
            if (position < itemsVisibleCount) {
                MediaPlayer.create(context, Uri.parse(sound))?.start()

                if (position == itemsVisibleCount - 1 && itemsVisibleCount < data.size) {
                    itemsVisibleCount++
                    notifyItemInserted(itemsVisibleCount)
                }
            }
        }

        if (position == itemsVisibleCount - 1) {
            holder.cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_in_animation))
        }
    }

    override fun getItemCount(): Int = itemsVisibleCount
}