package com.application.hooksPlus.keyboard

import com.application.hooksPlus.support.*


class KeyboardColorProvider(
    val rootSize: Int,
    private val masterArray: List<CurrentLists>,
    val adaptersBridge: AdaptersBridge,
    val additionalHandlersBridge: AdditionalHandlersBridge,
    private val hashTables: HashTables
) {

    fun addColorTableListItems(colorStructure: ColorStructure) {
        masterArray[rootSize - 1].keyboardColorList.getOrPut(
            key = ColorStructure(
                colorStructure.root,
                true
            ), defaultValue = { masterArray[rootSize - 1].rootColorTableLeft })
        masterArray[rootSize - 1].keyboardColorList.getOrPut(
            key = ColorStructure(
                colorStructure.root,
                false
            ), defaultValue = { masterArray[rootSize - 1].rootColorTableRight })
    }

    fun saveKeyColor(
        keyText: String,
        color: Int,
        colorStructure: ColorStructure
    ) {
        if (colorStructure.left)
            masterArray[rootSize - 1].rootColorTableLeft.set(key = keyText, value = color)
        else
            masterArray[rootSize - 1].rootColorTableRight.set(key = keyText, value = color)
        adaptersBridge.keyboardAdapter?.notifyDataSetChanged()
    }

    fun getColorFromCurrentTableLeft(keyText: String): Int? {
        val color = masterArray[rootSize - 1].rootColorTableLeft[keyText]
        val answer = maskAnswerColor(color)
        return answer
    }

    private fun maskAnswerColor(color: Int?): Int? {
        return if (additionalHandlersBridge.playHandler?.play!!) {// || additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE) {
            if (color == Colors.RED.color || color == Colors.GREEN.color) {
                Colors.MAGENTA.color
            } else {
                0xdce4ed
            }
        } else {
            color
        }
    }

    fun getColorFromCurrentTableRight(keyText: String): Int? =
        maskAnswerColor(masterArray[rootSize - 1].rootColorTableRight[keyText])

    fun fetchCurrentRootColorTables(colorStructure: ColorStructure) {
        if (masterArray[rootSize - 1].keyboardColorList.containsKey(colorStructure)) {
            masterArray[rootSize - 1].rootColorTableLeft =
                masterArray[rootSize - 1].keyboardColorList[ColorStructure(
                    colorStructure.root,
                    left = true
                )]!!
            masterArray[rootSize - 1].rootColorTableRight =
                masterArray[rootSize - 1].keyboardColorList[ColorStructure(
                    colorStructure.root,
                    left = false
                )]!!
        } else {
            setBlankCurrentTables()
        }
        adaptersBridge.keyboardAdapter?.notifyDataSetChanged()
    }

    private fun setBlankCurrentTables() {
        masterArray[rootSize - 1].rootColorTableLeft = getBlankTable()
        masterArray[rootSize - 1].rootColorTableRight = getBlankTable()
    }

    private fun getBlankTable(): HashMap<String, Int> {
        val itemTable = mutableMapOf<String, Int>()
        repeat(hashTables.letters.size) {
            itemTable[hashTables.letters[it]] = 0xdce4ed
        }
        return itemTable as HashMap<String, Int>
    }

    fun getColorSize(rootLeft: Boolean, color: Int): Int {
        val rootColorTable: HashMap<String, Int> =
            if (rootLeft) {
                getList(masterArray[rootSize - 1].rootColorTableLeft, color)
            } else {
                getList(masterArray[rootSize - 1].rootColorTableRight, color)
            }
        return rootColorTable.size
    }

    private fun getList(rootColorTable: HashMap<String, Int>, color: Int): HashMap<String, Int> {
        val filtered = rootColorTable.filter { entry ->
            entry.value == color
        }
        return filtered as HashMap<String, Int>
    }
}