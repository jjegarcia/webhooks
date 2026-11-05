package com.application.hooksPlus.support

import android.graphics.Color
import android.view.View
import com.application.hooksPlus.support.Roots.*
import com.application.hooksPlus.tables.*

class CurrentListProvider constructor(
    val additionalHandlersBridge: AdditionalHandlersBridge,
    val hashTables: HashTables
) {
    private val hooks1: MutableList<WordStructure> =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN) hooks1_en.toMutableList()
        else hooks1_es.toMutableList()
    private val hooks2: MutableList<WordStructure> =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN) hooks2_en.toMutableList()
        else hooks2_es.toMutableList()
    private val hooks3: MutableList<WordStructure> =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN) getHooks3En()
        else hooks3_es.toMutableList()
    private val hooks4: MutableList<WordStructure> =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN) getHooks4En()
        else getHooks4Es()

    private val roots1: List<String> = hooks1.map { it.root }.toMutableList().distinct()
    private val roots2: List<String> = hooks2.map { it.root }.toMutableList().distinct()
    private val roots3: List<String> = hooks3.map { it.root }.toMutableList().distinct()
    private val roots4: List<String> = hooks4.map { it.root }.toMutableList().distinct()

    private fun getHooks4En(): MutableList<WordStructure> {
        val hooks4: MutableList<WordStructure> = hooks4_en.toMutableList()
        hooks4.addAll(hooks4b_en)
        hooks4.addAll(hooks4c_en)
        return hooks4
    }

    private fun getHooks4Es(): MutableList<WordStructure> {
        val hooks4: MutableList<WordStructure> = hooks4_es.toMutableList()
        hooks4.addAll(hooks4b_es)
        hooks4.addAll(hooks4c_es)
        return hooks4
    }

    private fun getHooks3En(): MutableList<WordStructure> {
        val hooks3: MutableList<WordStructure> = hooks3_en.toMutableList()
        hooks3.addAll(hooks3b_en)
        return hooks3
    }

    private fun fetchRoot(rootSize: Int): Roots {
        return when (rootSize) {
            1 -> ROOT1
            2 -> ROOT2
            3 -> ROOT3
            4 -> ROOT4
            else -> ROOT1
        }
    }

    private fun getCurrentRootList(roots: Roots): List<String> {
        return when (roots) {
            ROOT1 -> roots1
            ROOT2 -> roots2
            ROOT3 -> roots3
            ROOT4 -> roots4
        }
    }

    private fun getCurrentHookList(roots: Roots): MutableList<WordStructure> {
        return when (roots) {
            ROOT1 -> hooks1.toMutableList()
            ROOT2 -> hooks2.toMutableList()
            ROOT3 -> hooks3.toMutableList()
            ROOT4 -> hooks4.toMutableList()
        }
    }

    private fun getCurrentMasterHookList(roots: Roots): List<WordStructure> {
        return when (roots) {
            ROOT1 -> hooks1
            ROOT2 -> hooks2
            ROOT3 -> hooks3
            ROOT4 -> hooks4
        }
    }

    private fun getBlankCurrentLists(rootSize: Int): CurrentLists {
        val roots = getCurrentRootList(fetchRoot(rootSize))
        return CurrentLists(
            rootList = roots,
            hookList = getCurrentHookList(fetchRoot(rootSize)),
            masterHookList = getCurrentMasterHookList(fetchRoot(rootSize)),
            keyboardColorList = mutableMapOf(),
            rootColorTableLeft = getBlankColorTable(),
            rootColorTableRight = getBlankColorTable(),
            rootParametersList = getRootParametersList(roots),//getBlankRootParameters(),
            wordParametersList = getBlankWordParametersList(rootSize),
            boardList = getBlankBoardList(),
            pointersList = getBlankPointers()
        )
    }

    fun getBlankArrayCurrentList(): MutableList<CurrentLists> {
        return listOf(
            getBlankCurrentLists(rootSize = 1),
            getBlankCurrentLists(rootSize = 2),
            getBlankCurrentLists(rootSize = 3),
            getBlankCurrentLists(rootSize = 4)
        ).toMutableList()
    }

    private fun getBlankColorTable(): HashMap<String, Int> {
        val itemTable = mutableMapOf<String, Int>()
        repeat(hashTables.letters.size) {
            itemTable[hashTables.letters[it]] = 0xdce4ed
        }
        return itemTable as HashMap<String, Int>
    }

    private fun getBlankRootParameters(): MutableList<RootParameters> {
        return mutableListOf(
            RootParameters(
                nColor = Color.CYAN,
                nEditable = true,
                nVisible = View.VISIBLE
            )
        )
    }

    private fun getBlankWordParametersList(rootSize: Int): MutableList<WordParameters> {
        return mutableListOf(
            WordParameters(
                rootIndex = 0,
                showLeftLetter = if (!(additionalHandlersBridge.modeHandler?.mode == Modes.NINJA)) rootSize != 4 //to review if roots4 for english
                else true,
                rootSize = rootSize
            )
        )
    }

    private fun getBlankBoardList(): MutableList<List<TileContent>> = mutableListOf()

    private fun getBlankPointers(): DataPointers {
        return DataPointers(
            wordsCounter = 0,
            currentRoot = 0,
            rootIndexCount = 0,
            rootCount = 0
        )
    }

    private fun getRootParametersList(roots: List<String>): MutableList<RootParameters> {
        val list: MutableList<RootParameters> = getBlankRootParameters()
        roots.forEach { _ ->
            list.add(
                RootParameters(
                    0xdce4ed,
                    true,
                    View.VISIBLE
                )
            )
        }
        return list
    }
}