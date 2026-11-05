package com.application.hooksPlus.support

import android.util.Log
import android.widget.ImageView
import com.application.hooksPlus.R
import com.application.hooksPlus.main.BoardActivity

class CommandsHandler constructor(
    val handlersBridge: HandlersBridge,
    val masterArray: List<CurrentLists>,
    val rootSize: Int,
    val boardActivity: BoardActivity
) {
    private lateinit var launchButton: ImageView

     fun executeUpdateRootCommand(root: String) {
        val position = masterArray[root.length - 1].rootList.indexOf(root)
        handlersBridge.hooksHandler?.jumpToRoot(position)
        handlersBridge.rootsHandler?.highlightSelectedCommand(
            position = position,
            color = 0xdce4ed
        )
    }

    fun initialiseButton(button: ImageView) {
        launchButton = button
        launchButton.setImageResource(R.drawable.ic_book)
    }

    fun executeFindRoot(root: String, showLeftLetter: Boolean) {
        val size = root.length
        if (masterArray[size - 1].rootList.contains(root)) {
            Log.i("Test:", "command switch to root $root feasible")
            boardActivity.moveToRootList(root)

            handlersBridge.hooksHandler?.setLeftLetter(showLeftLetter)
        }
    }
}
