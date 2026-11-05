package com.application.hooksPlus.board

import com.application.hooksPlus.support.AdaptersBridge
import com.application.hooksPlus.support.CurrentLists
import com.application.hooksPlus.support.WordParameters

class BoardConfigurator constructor(
    private var rootSize: Int,
    var masterArray: List<CurrentLists>,
    val adaptersBridge: AdaptersBridge
) {
    private val wordConfigurator: WordConfigurator =
        WordConfigurator(rootSize, masterArray)

    fun setBoardList() {
        if (masterArray[rootSize - 1].boardList.isNullOrEmpty()) {
            masterArray[rootSize - 1].boardList =
                masterArray[rootSize - 1].wordParametersList.map(wordConfigurator::configureWord)
                    .toMutableList()
        }
    }

    fun addWord(wordParameters: WordParameters) {
        masterArray[rootSize - 1].wordParametersList.add(wordParameters)
        masterArray[rootSize - 1].boardList.add(wordConfigurator.configureWord(wordParameters))
        adaptersBridge.boardAdapter?.notifyDataSetChanged()
    }

    fun replaceWord(wordParameters: WordParameters) {
        masterArray[rootSize - 1].wordParametersList.removeAt(masterArray[rootSize - 1].wordParametersList.size - 1)
        masterArray[rootSize - 1].wordParametersList.add(wordParameters)
        masterArray[rootSize - 1].boardList.removeAt(masterArray[rootSize - 1].boardList.size - 1)
        masterArray[rootSize - 1].boardList.add(wordConfigurator.configureWord(wordParameters))
        adaptersBridge.boardAdapter?.notifyDataSetChanged()
    }
}
