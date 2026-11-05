package com.application.hooksPlus.playerDetails

import androidx.lifecycle.MutableLiveData
import com.application.hooksPlus.R
import com.application.hooksPlus.support.IntentArguments.*

class PlayerDetailsFactory(val playerArguments: PlayerInfo) {
    val rootList = mutableListOf<LetterParameters>()

    fun getMatchRootInfo(): List<LetterParameters> {
        buildRootSources()
        rootList.add(getEmptyTilePosition(), LetterParameters(R.drawable.ic_empty))
        return rootList.toList()
    }

    private fun getEmptyTilePosition() = if (playerArguments.showLeftLetter) 0
    else playerArguments.root.length

    private fun buildRootSources() {
        playerArguments.root.forEachIndexed { index, root ->
            rootList.add(LetterParameters(playerArguments.matchResultInfo.letterHashMap.getValue(root.toString())))
        }
    }

    fun postMatchSummaryInfo(_matchSummaryInfo: MutableLiveData<MatchSummaryInfo>) {
        _matchSummaryInfo.postValue(
            MatchSummaryInfo(
                scoreText = playerArguments.matchResultInfo.score.toString(),
                playedTimeText = playerArguments.matchResultInfo.playedTime.toString(),
                placeText = playerArguments.matchResultInfo.place.toString()
            )
        )
    }
}