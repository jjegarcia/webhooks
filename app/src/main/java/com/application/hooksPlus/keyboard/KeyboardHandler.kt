package com.application.hooksPlus.keyboard

import android.view.View
import com.application.hooksPlus.board.LetterConfigurator
import com.application.hooksPlus.support.*

class KeyboardHandler constructor(
    private val providersBridge: ProvidersBridge,
    val rootSize: Int,
    val masterArray: List<CurrentLists>,
    private val handlersBridge: HandlersBridge,
    private val additionalHandlersBridge: AdditionalHandlersBridge
) {
    var ignoreNext: Boolean = false
    private var color: Int = 0

    @ExperimentalStdlibApi
    fun handleKeyClickListener(
        view: View,
        letterConfigurator: LetterConfigurator
    ) {
        if (additionalHandlersBridge.shitftHandler?.getButtonState() == true) {
            additionalHandlersBridge.shitftHandler?.toggleButton()
            handlersBridge.rootsHandler?.scrollToLocation(findPostion(view.tag.toString()))

        } else {
            if (!((additionalHandlersBridge.modeHandler?.mode == Modes.NINJA) && !additionalHandlersBridge.playHandler?.play!!) && additionalHandlersBridge.playHandler?.playable == true) {
                color = Colors.RED.color
                val text = view.tag.toString()
                if (checkValidKey(
                        key = text,
                        letterConfigurator = letterConfigurator,
                        rootSize = rootSize,
                        masterArray = masterArray
                    )
                ) {
                    additionalHandlersBridge.scoreHandler?.incrementScore()
                    color = Colors.GREEN.color
                    runSaveColor(text)
                    masterArray[rootSize - 1].pointersList.wordsCounter++
                    handlersBridge.hooksHandler?.addWord(text)
                } else {
                    runSaveColor(text)
                    additionalHandlersBridge.scoreHandler?.decrementScore()
                }
            }
        }
    }

    private fun findPostion(tag: String): Int {
       return masterArray[rootSize-1].rootList.indexOfFirst {
            it.startsWith(tag)
        }
    }

    private fun runSaveColor(text: String) {
        providersBridge.keyboardColorProvider?.saveKeyColor(
            keyText = text,
            color = color,
            colorStructure = ColorStructure(
                "",
                masterArray[rootSize - 1].wordParametersList[masterArray[rootSize - 1].pointersList.wordsCounter].showLeftLetter
            )
        )
    }

    @ExperimentalStdlibApi
    private fun checkValidKey(
        key: String,
        letterConfigurator: LetterConfigurator,
        rootSize: Int,
        masterArray: List<CurrentLists>
    ): Boolean {
        val rootIndex =
            masterArray[rootSize - 1].wordParametersList[masterArray[rootSize - 1].pointersList.wordsCounter].rootIndex
        val showLeftLetter =
            masterArray[rootSize - 1].wordParametersList[masterArray[rootSize - 1].pointersList.wordsCounter].showLeftLetter
        return rootIndex.let {
            handlersBridge.boardEventHandler!!.validateKey(
                key = key,
                rootIndex = it,
                showLeftLetter = showLeftLetter,
                letterConfigurator = letterConfigurator,
                rootSize = rootSize,
                masterArray = masterArray
            )
        }
    }
}