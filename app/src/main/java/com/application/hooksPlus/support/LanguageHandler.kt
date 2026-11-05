package com.application.hooksPlus.support

import android.widget.ImageView
import com.application.hooksPlus.R
import com.application.hooksPlus.main.BoardActivity

class LanguageHandler constructor(
    val masterArray: List<CurrentLists>,
    val rootSize: Int,
    val additionalHandlersBridge: AdditionalHandlersBridge,
    val boardActivity: BoardActivity
) {
    lateinit var flagButton: ImageView
    var language: Languages = Languages.ES
    fun toggleLanguage() {
        if (additionalHandlersBridge.modeHandler?.mode == Modes.GAME) {
            language =
                when (language) {
                    Languages.ES -> Languages.EN
                    else -> Languages.ES
                }
            setFlag(language)
        boardActivity.loadTables()
        }
    }

    private fun setFlag(language: Languages) {
        flagButton.setImageResource(
            when (language) {
                Languages.ES -> R.drawable.ic_es_flag
                else -> R.drawable.ic_en_flag
            }
        )
    }

    fun initialiseView(button: ImageView) {
        flagButton = button
        setFlag(language)
    }
}
