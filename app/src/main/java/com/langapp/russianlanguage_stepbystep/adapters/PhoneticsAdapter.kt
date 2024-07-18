package com.langapp.russianlanguage_stepbystep.adapters

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.ImageAndTextModel
import com.langapp.russianlanguage_stepbystep.models.TextModel
import com.squareup.picasso.Picasso

class PhoneticsAdapter(private var data: ArrayList<TextModel>) : RecyclerView.Adapter<PhoneticsAdapter.ItemHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.itemTv)
        var card: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.phonetics_item, parent, false)
        return ItemHolder(itemHolder)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item: TextModel = data[position]

        holder.text.text = item.text

        holder.card.startAnimation(AnimationUtils.loadAnimation(holder.card.context, R.anim.scale_up))

        holder.card.setOnClickListener {

            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer.create(holder.itemView.context, Uri.parse(item.sound)).apply {
                setOnCompletionListener { it.release() }
                start()
            }
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

}