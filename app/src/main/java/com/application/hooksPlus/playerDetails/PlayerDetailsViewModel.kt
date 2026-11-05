package com.application.hooksPlus.playerDetails

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.support.IntentArguments.*
import com.application.hooksPlus.support.ListenerInterface

class PlayerDetailsViewModel constructor(
    val playerArguments: PlayerInfo
) : ViewModel() {
    val playerDetailsFactory = PlayerDetailsFactory(playerArguments)
    private val _matchSummaryInfo = MutableLiveData<MatchSummaryInfo>()
    val matchSummaryInfo: LiveData<MatchSummaryInfo> get() = _matchSummaryInfo

    var playerDetailsKeyboardAdapter: PlayerDetailsKeyboardAdapter? = null
    var rootAdapter: RootAdapter? = null
    var playerNameText: String? = ""

    @SuppressLint("StaticFieldLeak")
    private lateinit var playerDetailsRecycler: RecyclerView

    fun backPrevious(view: View) = (view.context as ListenerInterface).back(view)

    fun initialisePlayer(
        context: Context,
        playerDetailsRecyclerView: RecyclerView,
        rootRecyclerView: RecyclerView,
        playerName: String?
    ) {
        setPlayerDetailsAdapter(playerDetailsRecyclerView, context)
        setRootAdapter(rootRecyclerView, context)
        playerNameText = playerName
    }

    private fun setRootAdapter(rootRecyclerView: RecyclerView, context: Context) {
        rootAdapter = RootAdapter(playerDetailsFactory.getMatchRootInfo())
        rootRecyclerView.apply {
            layoutManager= LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            adapter=rootAdapter
        }
    }

    private fun setPlayerDetailsAdapter(playerDetailsRecyclerView: RecyclerView, context: Context) {
        playerDetailsKeyboardAdapter = PlayerDetailsKeyboardAdapter(playerArguments)
        playerDetailsRecycler = playerDetailsRecyclerView
        playerDetailsRecycler.apply {
            layoutManager = GridLayoutManager(
                context, 7
            )
            adapter = playerDetailsKeyboardAdapter
        }
    }

    init {
        playerDetailsFactory.postMatchSummaryInfo(_matchSummaryInfo)
        playerNameText = playerArguments.name
    }

}