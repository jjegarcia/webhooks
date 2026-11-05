package com.application.hooksPlus.board

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.support.*

class HooksHandler(
    val boardConfigurator: BoardConfigurator?,
    val boardRecycler: RecyclerView?,
    var rootSize: Int,
    val masterArray: List<CurrentLists>,
    private val providersBridge: ProvidersBridge,
    val handlersBridge: HandlersBridge,
    val adaptersBridge: AdaptersBridge,
    val additionalHandlersBridge: AdditionalHandlersBridge
) {
    private lateinit var hookList: List<String>
    private lateinit var hookLeft: MutableList<String>
    private lateinit var hookRight: MutableList<String>
    private var rootLeft: Boolean = true
    var blueTiles: Int = 0
    var redTiles: Int = 0
    var greenTiles: Int = 0

    fun initialiseHook() =
        hookListBuild(masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount])

    private fun hookListBuild(root: String) {
        val hookStructure = masterArray[rootSize - 1].hookList.filter {
            it.root.contains(root)
        }
        hookList = hookStructure.map {
            it.word
        }
        hookLeft = hookList.filter {
            it.endsWith(root)
        }.toMutableList()
        hookRight = hookList.filter {
            it.startsWith(root) && !hookLeft.contains(it)
        }.toMutableList()
        rootLeft = if (additionalHandlersBridge.modeHandler?.mode != Modes.NINJA)
            hookLeft.isNotEmpty()
        else true
    }

    fun addWord(test: String) {
        updateHookList(test)
        boardConfigurator?.addWord(
            WordParameters(
                rootIndex = masterArray[rootSize - 1].pointersList.rootIndexCount,
                showLeftLetter = rootLeft,
                rootSize = rootSize
            )
        )
        boardRecycler?.smoothScrollToPosition(masterArray[rootSize - 1].pointersList.rootCount)
    }

    private fun updateHookList(test: String) {
        masterArray[rootSize - 1].pointersList.rootCount++
        if (rootLeft) {
            if (hookLeft.isNotEmpty()) hookLeft.remove(test + masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount])
            if (hookLeft.isEmpty()) {
                if (!additionalHandlersBridge.playHandler?.play!!)// || additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE)
                    rootLeft = false
                if (hookRight.isEmpty())
                    if (!additionalHandlersBridge.playHandler?.play!!)// || additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE)
                        loadNextRoot()
            }
        } else {
            if (hookRight.isNotEmpty()) hookRight.remove(masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount] + test)
            if (hookRight.isEmpty())
                if (additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE)
                    if (!(additionalHandlersBridge.modeHandler?.mode == Modes.NINJA && additionalHandlersBridge.playHandler?.play!!)) loadNextRoot()
        }
    }

    private fun loadNextRoot() {
        rootLeft = true
        masterArray[rootSize - 1].rootParametersList[masterArray[rootSize - 1].pointersList.rootIndexCount].nEditable =
            false
        providersBridge.keyboardColorProvider?.addColorTableListItems(
            ColorStructure(
                root = masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount],
                left = rootLeft
            )
        )
        highlightSelectedfromLoadNext()
        if (!handlersBridge.keyboardHandler?.ignoreNext!!)
            masterArray[rootSize - 1].pointersList.rootIndexCount++
        hookListBuild(masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount])
        providersBridge.keyboardColorProvider?.fetchCurrentRootColorTables(
            ColorStructure(
                root = masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount],
                left = rootLeft
            )
        )
        masterArray[rootSize - 1].pointersList.currentRoot = masterArray[rootSize - 1].pointersList.rootIndexCount
    }

    fun jumpToRoot(index: Int) {
        masterArray[rootSize - 1].pointersList.rootCount++
        rootLeft = true
        masterArray[rootSize - 1].pointersList.rootIndexCount = index
        hookListBuild(masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount])
        replaceWord()
    }

    private fun replaceWord() {
        boardConfigurator?.replaceWord(
            WordParameters(
                rootIndex = masterArray[rootSize - 1].pointersList.rootIndexCount,
                showLeftLetter = rootLeft,
                rootSize = rootSize
            )
        )
    }

    private fun highlightSelectedfromLoadNext() {
        masterArray[rootSize - 1].rootParametersList[masterArray[rootSize - 1].pointersList.rootIndexCount].nColor =
            Color.GREEN
        if (masterArray[rootSize - 1].pointersList.rootIndexCount < masterArray[rootSize - 1].rootList.size - 1)
            if (masterArray[rootSize - 1].rootParametersList[masterArray[rootSize - 1].pointersList.rootIndexCount + 1].nColor != Color.GREEN)
                masterArray[rootSize - 1].rootParametersList[masterArray[rootSize - 1].pointersList.rootIndexCount + 1].nColor =
                    Color.CYAN
        adaptersBridge.rootsAdapter?.notifyDataSetChanged()
    }

    fun loadRoot(position: Int) {
        rootLeft = true
        masterArray[rootSize - 1].pointersList.rootIndexCount = position
        hookListBuild(masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount])
        replaceWord()
        providersBridge.keyboardColorProvider?.fetchCurrentRootColorTables(
            ColorStructure(
                root = masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.rootIndexCount],
                left = rootLeft
            )
        )
    }

    fun toggleBlankLetter() {
        rootLeft = !rootLeft
        replaceWord()
        adaptersBridge.rootsAdapter?.notifyDataSetChanged()
    }

    fun setLeftLetter(showLeftLetter: Boolean) {
        rootLeft = showLeftLetter
        replaceWord()
        adaptersBridge.rootsAdapter?.notifyDataSetChanged()
    }

    fun fetchUnsolved() {
        val unsolvedList = if (rootLeft) fetchUnsolvedLeft()
        else fetchUnsolvedRight()
        blueTiles = unsolvedList.size

        updateKeyboardUnsolvedColors(
            unsolvedList
        )
    }

    fun updateKeyboardUnsolvedColors(unsolved: List<String>) {
        unsolved.forEach { item -> runSaveColor(item) }
    }

    fun runSaveColor(text: String) {
        providersBridge.keyboardColorProvider?.saveKeyColor(
            keyText = text,
            color = Colors.BLUE.color,
            colorStructure = ColorStructure(
                "",
                masterArray[rootSize - 1].wordParametersList[masterArray[rootSize - 1].pointersList.wordsCounter].showLeftLetter
            )
        )
    }

    private fun fetchUnsolvedLeft(): List<String> = hookLeft.map { item -> item.substring(0, 1) }

    private fun fetchUnsolvedRight(): List<String> = hookRight.map { item -> item.substring(rootSize) }

    fun setRedTiles() {
        redTiles = providersBridge.keyboardColorProvider?.getColorSize(rootLeft, Colors.RED.color) ?: 0
    }

    fun setGreenTiles() {
        greenTiles = providersBridge.keyboardColorProvider?.getColorSize(rootLeft, Colors.GREEN.color) ?: 0
    }

}
