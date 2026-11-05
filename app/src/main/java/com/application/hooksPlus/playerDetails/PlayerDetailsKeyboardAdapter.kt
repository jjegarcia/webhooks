package com.application.hooksPlus.playerDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.*
import kotlinx.android.synthetic.main.key_container.view.*
import kotlinx.android.synthetic.main.letter_tile.view.*

class PlayerDetailsKeyboardAdapter(
    val playerArguments: IntentArguments.PlayerInfo
) : RecyclerView.Adapter<PlayerDetailsKeyboardAdapter.ViewHolder>() {
    lateinit var letterHashMap: List<Pair<String, Int>>

    class ViewHolder(itemViewP: View) : RecyclerView.ViewHolder(itemViewP) {
        val item: ImageView = itemViewP.letterImageView
        val itemRightLayout: LinearLayout = itemViewP.right_marker
        val itemLeftLayout: LinearLayout = itemViewP.left_marker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        letterHashMap = playerArguments.matchResultInfo.letterHashMap.toList()
        val v = LayoutInflater.from(parent.context).inflate(R.layout.key_container, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = playerArguments.matchResultInfo.letterHashMap.size

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    holder.item.context,
                    letterHashMap[position].second
                )
            )
            playerArguments.matchResultInfo.keyboardColorListLeft[letterHashMap[position].first]?.let {
                holder.itemLeftLayout.setBackgroundColor(
                    it
                )
            }
            playerArguments.matchResultInfo.keyboardColorListRight[letterHashMap[position].first]?.let {
                holder.itemRightLayout.setBackgroundColor(
                    it
                )
            }
        }
    }
}