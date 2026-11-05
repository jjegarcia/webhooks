package com.application.hooksPlus.board

import com.application.hooksPlus.support.AdaptersBridge
import com.application.hooksPlus.support.CurrentLists

class LetterConfigurator constructor(
    private val adaptersBridge: AdaptersBridge
) {
    fun savePressedLetter(
        rootSize: Int,
        masterArray: List<CurrentLists>,
        testLetter: String,
        showLeftLetter: Boolean
    ) {
        if (showLeftLetter) masterArray[rootSize - 1].boardList[masterArray[rootSize - 1].pointersList.wordsCounter][0].nText =
            testLetter
        else masterArray[rootSize - 1].boardList[masterArray[rootSize - 1].pointersList.wordsCounter][5].nText =
            testLetter
        adaptersBridge.boardAdapter?.notifyDataSetChanged()
    }
}
