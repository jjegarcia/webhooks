package com.application.hooksPlus.roots

import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.support.*

class RootsHandler constructor(
    private val handlersBridge: HandlersBridge,
    val rootsRecycler: RecyclerView,
    var rootSize: Int,
    val masterArray: List<CurrentLists>,
    val providersBridge: ProvidersBridge,
    val additionalHandlersBridge: AdditionalHandlersBridge
) {

    fun clickHandler(position: Int, rootParametersList: MutableList<RootParameters>) {
        Log.i("test", " root clicked: $position")
        providersBridge.keyboardColorProvider?.addColorTableListItems(
            ColorStructure(
                masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.currentRoot],
                true
            )
        )
        highlightSelected(
            position,
            if (additionalHandlersBridge.modeHandler?.mode != Modes.NINJA) Colors.YELLOW.color
            else 0xdce4ed,
            rootParametersList
        )
        providersBridge.keyboardColorProvider?.fetchCurrentRootColorTables(
            colorStructure = ColorStructure(
                masterArray[rootSize - 1].rootList[masterArray[rootSize - 1].pointersList.currentRoot],
                true
            )
        )
        if (handlersBridge.keyboardHandler!!.ignoreNext) {
            handle_glitch(position)
        } else handlersBridge.hooksHandler!!.jumpToRoot(position)
    }

    private fun handle_glitch(position: Int) {
        handlersBridge.hooksHandler?.loadRoot(position)

        if (position < masterArray[rootSize - 1].rootList.size - 1)
            handlersBridge.keyboardHandler!!.ignoreNext = false
    }

    fun highlightSelected(
        position: Int,
        color: Int,
        rootParametersList: MutableList<RootParameters>
    ) {
        if (rootParametersList[masterArray[rootSize - 1].pointersList.currentRoot].nColor != Color.GREEN)
            rootParametersList[masterArray[rootSize - 1].pointersList.currentRoot].nColor = color
        if (rootParametersList[position].nColor != Color.GREEN)
            rootParametersList[position].nColor = Color.CYAN
        masterArray[rootSize - 1].pointersList.currentRoot = position
    }

    fun highlightSelectedCommand(position: Int, color: Int) {
        masterArray[rootSize - 1].rootParametersList[masterArray[rootSize - 1].pointersList.currentRoot].nColor =
            color
        masterArray[rootSize - 1].rootParametersList[position].nColor = Color.CYAN
        rootsRecycler.smoothScrollToPosition(position)
    }
    fun scrollToLocation(position: Int)= rootsRecycler.smoothScrollToPosition(position)
}