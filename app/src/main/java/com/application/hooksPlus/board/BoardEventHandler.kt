package com.application.hooksPlus.board

import android.util.Log
import com.application.hooksPlus.support.CurrentLists
import com.application.hooksPlus.support.HandlersBridge
import com.application.hooksPlus.support.WordStructure
import java.util.*

class BoardEventHandler(
    val rootSize: Int,
    val masterArray: List<CurrentLists>,
    val handlersBridge: HandlersBridge
) {
    private fun foundHook(entry: WordStructure): Boolean =
        masterArray[rootSize - 1].hookList.contains(entry)

    @ExperimentalStdlibApi
    fun validateKey(
        key: String,
        rootIndex: Int,
        showLeftLetter: Boolean,
        letterConfigurator: LetterConfigurator,
        rootSize: Int,
        masterArray: List<CurrentLists>
    ): Boolean {
        val rootString = masterArray[rootSize - 1].rootList[rootIndex]
        val testLetter = key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val nTestLetter = if (showLeftLetter) testLetter + rootString
        else rootString + testLetter
        val nTestWordStructure = WordStructure(nTestLetter, rootString)
        val result = foundHook(nTestWordStructure)
        if (result) {
            val currentMasterHookIndex = masterArray[rootSize - 1].masterHookList.indexOf(
                WordStructure(
                    nTestLetter,
                    rootString
                )
            )
            if (currentMasterHookIndex == masterArray[rootSize - 1].masterHookList.size - 1)
                handlersBridge.keyboardHandler?.ignoreNext = true
            masterArray[rootSize - 1].hookList.remove(nTestWordStructure)
            Log.i("test", "match")
        }
        //JG: to check
//        else if (additionalHandlersBridge.playHandler?.play!!) {
//            testLetter = null.toString()
//        }
        letterConfigurator.savePressedLetter(
            rootSize = rootSize,
            masterArray = masterArray,
            testLetter = testLetter,
            showLeftLetter = showLeftLetter
        )
        return result
    }
}
