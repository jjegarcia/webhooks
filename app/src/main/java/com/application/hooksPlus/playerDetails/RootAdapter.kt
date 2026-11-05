package com.application.hooksPlus.playerDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.IntentArguments.LetterParameters
import kotlinx.android.synthetic.main.letter_tile.view.*

class RootAdapter constructor(
    val rootList: List<LetterParameters>
) : RecyclerView.Adapter<RootAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val letterImageView: ImageView = itemView.letterImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.key_container, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = rootList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.letterImageView.setImageResource(rootList[position].viewSource)
        holder.letterImageView.visibility = rootList[position].visibilityValue
    }
}