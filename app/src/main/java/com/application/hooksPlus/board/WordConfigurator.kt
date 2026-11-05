package com.application.hooksPlus.board

import android.graphics.Color
import com.application.hooksPlus.support.CurrentLists
import com.application.hooksPlus.support.TileContent
import com.application.hooksPlus.support.WordParameters

private const val LIGHT_BLUE = Color.CYAN

class WordConfigurator constructor(
    var rootSize: Int,
    val masterArray: List<CurrentLists>
) {
    private lateinit var wordList: List<TileContent>
    private fun setWordList(wordParameters: WordParameters) {
        wordParameters.apply {
            wordList = listOf(
                //hook left
                TileContent(
                    null,
                    nEditable = true,
                    nVisible = (showLeftLetter),
                    nColor = LIGHT_BLUE
                ),
                //root 1
                TileContent(
                    if (rootSize in 1..4)
                        masterArray[rootSize - 1].rootList[rootIndex][0].toString()
                    else
                        null,
                    nEditable = false,
                    nVisible = (rootSize in 1..4),
                    nColor = LIGHT_BLUE
                ),
                //root 2
                TileContent(
                    if (rootSize in 2..4)
                        masterArray[rootSize - 1].rootList[rootIndex][1].toString()
                    else
                        null,
                    nEditable = false,
                    nVisible = (rootSize in 2..4),
                    nColor = LIGHT_BLUE
                ),
                //root 3
                TileContent(
                    if (rootSize in 3..4) masterArray[rootSize - 1].rootList[rootIndex][2].toString()
                    else null,
                    nEditable = false,
                    nVisible = (rootSize in 3..4),
                    nColor = LIGHT_BLUE
                ),
                //root 4
                TileContent(
                    if (rootSize == 4)
                        masterArray[rootSize - 1].rootList[rootIndex][3].toString()
                    else null,
                    nEditable = false,
                    nVisible = (rootSize == 4),
                    nColor = LIGHT_BLUE
                ),
                //hook right
                TileContent(
                    nText = null,
                    nEditable = true,
                    nVisible = (!showLeftLetter),
                    nColor = LIGHT_BLUE
                )
            )
        }
    }

    private fun getWordListContent() = wordList

    fun configureWord(wordParameters: WordParameters): List<TileContent> {
        setWordList(wordParameters)
        return getWordListContent()
    }
}
