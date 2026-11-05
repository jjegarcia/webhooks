package com.application.hooksPlus.board

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.*
import kotlinx.android.synthetic.main.letter_tile.view.*
import kotlinx.android.synthetic.main.word_tile.view.*

private const val RIGHT_MARGIN_PIXELS = 6

class BoardBinder constructor(
    private val handlersBridge: HandlersBridge,
    private val additionalHandlersBridge: AdditionalHandlersBridge,
    val masterArray: List<CurrentLists>,
    private var rootSize: Int,
    private val hashTables: HashTables

) {

    fun setBoardBinder(
        holder: BoardAdapter.ViewHolder,
        position: Int
    ) {
        setWordBinder(
            view = holder.item,
            position = position
        )
        holder.bottomPadding.visibility =
            if (position == masterArray[rootSize - 1].boardList.size - 1) View.VISIBLE
            else View.GONE
        holder.tileBox.visibility =
            if (!(!(position == masterArray[rootSize - 1].boardList.size - 1) && (additionalHandlersBridge.playHandler!!.play || additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE))) View.VISIBLE
            else View.GONE
    }

    private fun setWordListener(view: View) {
        view.setOnClickListener {
            Log.i("test", "word clicked")
            if (additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE) {
                if (additionalHandlersBridge.modeHandler?.mode == Modes.NINJA && !additionalHandlersBridge.playHandler?.play!!) {
                    Log.i("test", "toggling ? letter")
                    handlersBridge.hooksHandler?.toggleBlankLetter()
                }
            }
        }
    }

    private fun setWordBinder(
        view: View, position: Int
    ) {
        if (!additionalHandlersBridge.playHandler!!.play) {
            setLetterBinder(
                letter = view.letter1,
                item = masterArray[rootSize - 1].boardList[position][0],
            )
        } else {
            if (position == masterArray[rootSize - 1].boardList.size - 1) {
                val testItem = masterArray[rootSize - 1].boardList[position][0]
                testItem.nText = null
                setLetterBinder(
                    letter = view.letter1,
                    item = testItem
                )
            }
        }
        setLetterBinder(
            letter = view.letter2,
            item = masterArray[rootSize - 1].boardList[position][1]
        )
        setLetterBinder(
            letter = view.letter3,
            item = masterArray[rootSize - 1].boardList[position][2]
        )
        setLetterBinder(
            letter = view.letter4,
            item = masterArray[rootSize - 1].boardList[position][3]
        )
        setLetterBinder(
            letter = view.letter5,
            item = masterArray[rootSize - 1].boardList[position][4]
        )
        if (!additionalHandlersBridge.playHandler!!.play) {// || additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE) {
            setLetterBinder(
                letter = view.letter6,
                item = masterArray[rootSize - 1].boardList[position][5]
            )
        } else {
            if (position == masterArray[rootSize - 1].boardList.size - 1) {
                val testItem = masterArray[rootSize - 1].boardList[position][5]
                testItem.nText = null
                setLetterBinder(
                    letter = view.letter6,
                    item = testItem
                )
            }
        }
        setWordListener(
            view = view
        )
    }

    private fun setLetterBinder(letter: View, item: TileContent) {
        letter.letterImageView.apply {
            if (item.nText == null) {
                setImageResource(R.drawable.ic_empty)
            } else {
                item.nText?.let { text ->
                    getView(text)?.let {
                        setImageResource(it)
                    }
                }
            }
            letter.visibility = if (item.nVisible) View.VISIBLE
            else View.GONE
            this.layoutParams = getMarginRight(this)
        }
    }

    private fun getView(text: String): Int? = hashTables.viewMap[text]

    private fun getMarginRight(imageView: ImageView): ViewGroup.LayoutParams {
        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.rightMargin = RIGHT_MARGIN_PIXELS
        return param
    }
}
