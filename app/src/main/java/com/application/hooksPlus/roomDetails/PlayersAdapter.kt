package com.application.hooksPlus.roomDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.IntentArguments
import kotlinx.android.synthetic.main.player_row.view.*

class PlayersAdapter constructor(
    val roomDetailsArguments: IntentArguments.RoomInfo,
    val roomDetailsViewModel: RoomDetailsViewModel,
    val displayPlayerDetails: Boolean
) : RecyclerView.Adapter<PlayersAdapter.ViewHolder>() {
    class ViewHolder(itemViewP: View) : RecyclerView.ViewHolder(itemViewP) {
        val row: LinearLayout = itemViewP.playerRow
        val playerId: TextView = itemViewP.playerId
        val name: TextView = itemViewP.name
        val email: TextView = itemViewP.email
        val ranking: TextView = itemViewP.ranking
        val score: TextView = itemViewP.score
        val place: TextView = itemViewP.place
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.player_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playerId.text = roomDetailsArguments.playerList[position].playerId
        holder.name.text = roomDetailsArguments.playerList[position].name
        holder.email.text = roomDetailsArguments.playerList[position].email
        holder.ranking.text = roomDetailsArguments.playerList[position].ranking.toString()
        holder.score.text = roomDetailsArguments.playerList[position].score.toString()
        holder.score.visibility = determineScoreVisibility(roomDetailsArguments.closingTime)
        holder.place.text = roomDetailsArguments.playerList[position].place.toString()
        setPlayerClickListener(holder = holder, position = position)
    }

    private fun determineScoreVisibility(closingTime: Int): Int = if (closingTime > 0) View.GONE else View.VISIBLE

    private fun setPlayerClickListener(holder: ViewHolder, position: Int) {
        holder.row.setOnClickListener {
            if (roomDetailsArguments.closingTime <= 0) {
                roomDetailsViewModel.callNextActivity(
                    view = it,
                    roomDetailsArguments.playerList[position]
                )
            }
        }
    }

    override fun getItemCount(): Int = roomDetailsArguments.playerList.size


}