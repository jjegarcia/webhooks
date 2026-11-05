package com.application.hooksPlus.support

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView

private const val TIMER_TIMEOUT: Long = 40000
private const val TIMER_TICK: Long = 1000
private const val COUNT_DOWN_INITIAL_VALUE = (TIMER_TIMEOUT / TIMER_TICK).toInt()

class TimerHandler constructor(val additionalHandlersBridge: AdditionalHandlersBridge, val handlersBridge: HandlersBridge) {
    var playingTime: Int = 0
    private lateinit var timerView: TextView
    private lateinit var timerTile: View
    lateinit var timer: CountDownTimer
    var countDownCounter: Int = COUNT_DOWN_INITIAL_VALUE

    fun initialiseView(textView: TextView, view: View) {
        timerView = textView
        timerView.text = ""
        timerTile = view
        timerTile.visibility = View.GONE
    }

    fun stop() = timer.cancel()

    fun setUp() {
        countDownCounter = COUNT_DOWN_INITIAL_VALUE
        timerTile.visibility = View.VISIBLE
        timer = object : CountDownTimer(TIMER_TIMEOUT, TIMER_TICK) {
            override fun onTick(millisUntilFinished: Long) {
                countDownCounter--
                Log.i("Test", "msToFinish=$millisUntilFinished")
                timerView.text = countDownCounter.toString()
            }

            override fun onFinish() {
                Log.i("Test", "Timer Finished")
                additionalHandlersBridge.playHandler?.endBattle()
                additionalHandlersBridge.playHandler?.updatePlayStatus()
                handlersBridge.hooksHandler?.fetchUnsolved()
            }
        }
    }
}
