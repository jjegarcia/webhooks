package com.application.hooksPlus.support

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R

class PlayHandler(
    private val adaptersBridge: AdaptersBridge,
    var rootSize: Int,
    var masterArray: List<CurrentLists>,
    val additionalHandlersBridge: AdditionalHandlersBridge,
    val boardRecycler: RecyclerView?,
    val handlersBridge: HandlersBridge
) {
    var play: Boolean = false
    private lateinit var playButton: ImageView
    var playable: Boolean = true

    fun handlePlayButton() {
        if ((additionalHandlersBridge.modeHandler?.mode == Modes.NINJA || additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE) && playable) {
            if (play) {
                if (additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE) {
                    playButton.setImageResource(R.drawable.ic_play)
                } else {
                    endBattle()
                }
                handlersBridge.hooksHandler?.fetchUnsolved()
                handlersBridge.hooksHandler?.setRedTiles()
                handlersBridge.hooksHandler?.setGreenTiles()
            } else {
                playButton.setImageResource(R.drawable.ic_stop)
            }
            updatePlayStatus()
        }
    }

    fun updatePlayStatus() {
        adaptersBridge.keyboardAdapter?.notifyDataSetChanged()
        updateScore()
        play = !play
        adaptersBridge.boardAdapter?.notifyDataSetChanged()
        boardRecycler?.smoothScrollToPosition(masterArray[rootSize - 1].pointersList.rootCount)
    }

    private fun updateScore() {
        handlersBridge.hooksHandler?.let { additionalHandlersBridge.scoreHandler?.computeNinjaScore(it) }
        if (additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE) {
            additionalHandlersBridge.scoreHandler?.addTimerBonus(additionalHandlersBridge.timerHandler?.countDownCounter ?: 0)
        }
        additionalHandlersBridge.scoreHandler?.toggleScoreViewVisibility()
    }

    fun endBattle() {
        playButton.setImageResource(R.drawable.ic_play_g)
        playable = false
        additionalHandlersBridge.timerHandler?.stop()
        additionalHandlersBridge.timerHandler?.playingTime = additionalHandlersBridge.timerHandler?.countDownCounter ?: 0
    }

    fun initialiseButton(button: ImageView) {
        playButton = button
        playButton.setImageResource(R.drawable.ic_play_g)
    }

    fun setSolidPlay() = playButton.setImageResource(R.drawable.ic_play)

    fun setSolidStop() {
        playButton.setImageResource(R.drawable.ic_stop)
        play = true
    }
}
