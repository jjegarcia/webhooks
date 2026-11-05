package com.application.hooksPlus.rooms

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.IntentArguments
import kotlinx.android.synthetic.main.room_row.view.closingTime
import kotlinx.android.synthetic.main.room_row.view.people
import kotlinx.android.synthetic.main.room_row.view.play
import kotlinx.android.synthetic.main.room_row.view.results
import kotlinx.android.synthetic.main.room_row.view.roomId
import kotlinx.android.synthetic.main.room_row.view.roomRow
import kotlinx.android.synthetic.main.room_row.view.rootSize

class RoomsAdapter(
    val roomsViewModel: RoomsViewModel,
    val roomsViewModelBridge: RoomsViewModelBridge
) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    var displayPlayerDetails: Boolean? = false
    var roomsList: MutableList<IntentArguments.RoomInfo> = mutableListOf()

    class ViewHolder(itemViewP: View) : RecyclerView.ViewHolder(itemViewP) {
        val row: LinearLayout = itemViewP.roomRow
        val id: TextView = itemViewP.roomId
        val people: TextView = itemViewP.people
        val closingTime: TextView = itemViewP.closingTime
        val results: TextView = itemViewP.results
        val playable: ImageView = itemViewP.play
        val rootSize: TextView = itemViewP.rootSize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.room_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        displayPlayerDetails = displayPlayerDetails(roomsList[position].closingTime)
        val hasUserPlayed = hasUserPlayed(position)
        val hasHostPlayed = roomsList[position].playerList[0].matchResultInfo.played
        val isUserHosting = roomsList[position].playerList[0].playerId == roomsViewModel.user
        setUiMembers(
            holder = holder,
            position = position,
            isClosingTimeNotOver = displayPlayerDetails,
            hasUserPlayed = hasUserPlayed,
            hasHostPlayed = hasHostPlayed,
            isUserHosting = isUserHosting
        )
        setPlayableListener(
            holder = holder,
            position = position,
            isClosingTimeNotOver = displayPlayerDetails,
            hasUserPlayed = hasUserPlayed,
            hasHostPlayed = hasHostPlayed,
            isUserHosting = isUserHosting
        )
        setRoomClickListener(holder, position)
    }

    private fun setUiMembers(
        holder: ViewHolder,
        position: Int,
        isClosingTimeNotOver: Boolean?,
        hasUserPlayed: Boolean,
        hasHostPlayed: Boolean,
        isUserHosting: Boolean
    ) {
        holder.id.text = roomsList[position].roomId
        holder.people.text = roomsList[position].people.toString()
        holder.closingTime.text =
            isClosingTimeNotOver?.let { getClosingTimeString(roomsList[position].closingTime) }
        holder.results.text = roomsList[position].results.toString()
        holder.playable.setImageResource(isClosingTimeNotOver?.let {
            getPlayIcon(
                isClosingTimeNotOver = it,
                hasUserPlayed = hasUserPlayed,
                hasHostPlayed = hasHostPlayed,
                isUserHosting = isUserHosting
            )
        }
            ?: R.drawable.ic_empty)
        holder.rootSize.text = roomsList[position].rootSelected
    }

    private fun setPlayableListener(
        holder: ViewHolder,
        position: Int,
        isClosingTimeNotOver: Boolean?,
        hasUserPlayed: Boolean,
        hasHostPlayed: Boolean,
        isUserHosting: Boolean
    ) {
        val test = hasHostPlayed.xor(isUserHosting)
        val isPlayEnabled = !hasUserPlayed && isClosingTimeNotOver == true && test
        holder.playable.setOnClickListener {
            Log.i("play button pressed=", "$position")
            if (isPlayEnabled) {
                roomsViewModelBridge.roomsDatabaseHandler?.subscribeToRoom(position)
            }
        }
    }

    private fun hasUserPlayed(position: Int): Boolean {
        val currentPlayerId = roomsViewModel.user
        val roomListWithCurrentPlayer = roomsList[position].playerList.filter {
            it.playerId == currentPlayerId
        }
        if (roomListWithCurrentPlayer.isNotEmpty()) {
            val hasPlayerPlayed = roomListWithCurrentPlayer[0].matchResultInfo.played
            return hasPlayerPlayed
        } else
            return false
    }


    private fun setRoomClickListener(holder: ViewHolder, position: Int) {
        holder.row.setOnClickListener { view ->
            displayPlayerDetails?.let { displayPlayerDetails ->
                roomsViewModel.callNextActivity(
                    view = view,
                    roomInfo = roomsList[position],
                    displayPlayerDetails = displayPlayerDetails
                )
            }
        }
    }

    private fun displayPlayerDetails(closingTime: Int): Boolean {
        val condition1 = closingTime > 0
        val condition2 =
            roomsViewModel.roomsViewModelBridge.serverDataHandler?.serverData?.forcePlayerDetailsEnabled
                ?: true
        return condition1 && condition2
    }

    private fun getPlayIcon(
        isClosingTimeNotOver: Boolean,
        hasUserPlayed: Boolean,
        hasHostPlayed: Boolean,
        isUserHosting: Boolean
    ): Int {
        val test = hasHostPlayed.xor(isUserHosting)
        val isPlayEnabled = !hasUserPlayed && isClosingTimeNotOver && test
        return if (isPlayEnabled) R.drawable.ic_play
        else R.drawable.ic_play_g
    }

    private fun getClosingTimeString(closingTime: Int): String {
        return if (roomsViewModel.roomsViewModelBridge.serverDataHandler?.serverData?.displayTimeEnabled == false) ""
        else
            if (closingTime < 0)
                "--"
            else getFormatedClosingTime(closingTime)
    }

    private fun getFormatedClosingTime(closingTime: Int): String =
        String.format(
            "%02d:%02d:%02d",
            closingTime / 3600,
            (closingTime / 60) % 60,
            closingTime % 60
        )

    override fun getItemCount(): Int = roomsList.size
}
