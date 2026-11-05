package com.application.hooksPlus.support

import android.view.View
import android.widget.TextView
import com.application.hooksPlus.board.HooksHandler

private const val INC_SCORE_VAL = 3
private const val DEC_SCORE_VAL = 1
private const val SCORE_OFFSET = 2000

class ScoreHandler constructor(val additionalHandlersBridge: AdditionalHandlersBridge) {
    var score: Int = 0
    private lateinit var scoreTile: View
    private lateinit var scoreView: TextView
    private var updateScoreEnabled = false

    fun incrementScore() {
        score += INC_SCORE_VAL
        updateScore()
    }

    fun decrementScore() {
        score -= DEC_SCORE_VAL
        updateScore()
    }

    private fun updateScore() {
        if (updateScoreEnabled)
            scoreView.text = score.toString()
    }

    fun resetScore() {
        score = 0
        updateScoreEnabled = true
        updateScore()
    }

    fun initialiseView(textView: TextView, view: View) {
        scoreTile = view
        scoreView = textView
        scoreView.text = ""
    }

    fun toggleScoreViewVisibility() {
        scoreView.visibility =
            if (additionalHandlersBridge.playHandler?.play == true) View.VISIBLE
            else View.GONE
    }

    fun clearVisibility() {
        scoreView.visibility = View.GONE
    }

    fun computeNinjaScore(hooksHandler: HooksHandler) {
        score = SCORE_OFFSET - 50 * hooksHandler.redTiles - hooksHandler.blueTiles * 100 + 300 * hooksHandler.greenTiles
        updateScore()
    }

    fun addTimerBonus(timeLeft: Int) {
        score += timeLeft * 50
        updateScore()
    }
}
