package com.application.hooksPlus.roomDetails

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.support.IntentArguments
import com.application.hooksPlus.support.ListenerInterface

class RoomDetailsViewModel constructor(
    val roomDetailsArguments: IntentArguments.RoomInfo,
    val displayPlayerDetails:Boolean
) : ViewModel() {
    var playersAdapter: PlayersAdapter? = null
    var roomIdText: String = ""
    var timerText: String? = ""

    private lateinit var roomDetailsRecycler: RecyclerView

    fun backPrevious(view: View) = (view.context as ListenerInterface).back(view)

    fun callNextActivity(view: View, playerInfo: IntentArguments.PlayerInfo) =
        (view.context as ListenerInterface).callNextActivity(view, playerInfo)

    fun initialiseRoomDetails(
        context: Context,
        roomDetailsRecyclerView: RecyclerView
    ) {
        playersAdapter = PlayersAdapter(roomDetailsArguments = roomDetailsArguments, roomDetailsViewModel = this, displayPlayerDetails = displayPlayerDetails)
        roomDetailsRecycler = roomDetailsRecyclerView
        roomDetailsRecycler.apply {
            layoutManager = LinearLayoutManager(
                context, RecyclerView.VERTICAL, false
            )
            adapter = playersAdapter
        }
    }

    init {
        roomIdText = roomDetailsArguments.roomId
        timerText=roomDetailsArguments.closingTime.toString()
    }
}