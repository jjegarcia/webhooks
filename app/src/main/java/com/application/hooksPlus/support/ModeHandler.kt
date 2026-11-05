package com.application.hooksPlus.support

import android.util.Log
import android.widget.ImageView

class ModeHandler constructor(val additionalHandlersBridge: AdditionalHandlersBridge) {
    var mode: Modes = Modes.GAME

    fun initialiseButton(button: ImageView) {
        mode = Modes.GAME
        setModeImage(button)
    }

    private fun setModeImage(button: ImageView) {
        Log.i("test", "Switched to ${mode.descrption} mode")
        mode.image.let { button.setImageResource(it) }
    }

    fun switchMode(button: ImageView) {
        mode = when (mode) {
            Modes.PRACTISE -> Modes.PRACTISE
            Modes.GAME -> Modes.NINJA
            Modes.NINJA -> Modes.NINJA
            Modes.BATTLE -> Modes.BATTLE
        }
        setModeImage(button)
        if (mode == Modes.NINJA) additionalHandlersBridge.playHandler?.setSolidPlay()
    }

    fun startGaming(button: ImageView?) {
        mode = Modes.GAME
        if (button != null) {
            setModeImage(button)
        }
    }
    fun setBattleMode(button: ImageView) {
        mode = Modes.BATTLE
        setModeImage(button)

    }

}
