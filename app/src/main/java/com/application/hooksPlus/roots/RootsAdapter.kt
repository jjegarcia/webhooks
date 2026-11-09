package com.application.hooksPlus.roots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.application.hooksPlus.R
import com.application.hooksPlus.support.*
import kotlinx.android.synthetic.main.letter_tile.view.*
import kotlinx.android.synthetic.main.row_tile.view.*
import kotlinx.android.synthetic.main.word_tile.view.*

private const val RIGHT_MARGIN_PIXELS = 6

class RootsAdapter constructor(
    val rootSize: Int,
    val masterArray: List<CurrentLists>,
    val handlersBridge: HandlersBridge,
    val additionalHandlersBridge: AdditionalHandlersBridge,
    val hashTables: HashTables,
    private val onRootSelected: (() -> Unit)? = null
) : Adapter<RootsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: View = itemView.word
        val layoutItem: LinearLayout = itemView.rowLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_tile, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = masterArray[rootSize - 1].rootList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        buildWord(holder, position)
        setBackgroundColor(position, holder)
        setRootClickListener(holder, position)
    }

    private fun setRootClickListener(
        holder: ViewHolder,
        position: Int
    ) {
        holder.item.setOnClickListener {
            if (!additionalHandlersBridge.playHandler?.play!!) { // || additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE) {
                handlersBridge.rootsHandler?.clickHandler(
                    position,
                    masterArray[rootSize - 1].rootParametersList
                )
                notifyDataSetChanged()
                onRootSelected?.invoke()
            }
        }
    }

    private fun setBackgroundColor(
        position: Int,
        holder: ViewHolder
    ) {
        masterArray[rootSize - 1].rootParametersList[position].nColor?.let {
            holder.layoutItem.setBackgroundColor(
                it
            )
        }
    }

    private fun buildWord(holder: ViewHolder, position: Int) {
        holder.item.letter1.visibility = View.GONE
        setLetter2(holder, position)
        setLetter3(holder, position)
        setLetter4(holder, position)
        setLetter5(holder, position)
        setLetter6(holder, position)
    }

    private fun setLetter6(
        holder: ViewHolder,
        position: Int
    ) {
        if (rootSize < 5) holder.item.letter6.visibility = View.GONE
        else
            holder.item.letter6.letterImageView.apply {
                getView(masterArray[rootSize - 1].rootList[position][4].toString())?.let {
                    setImageResource(
                        it
                    )
                }
            }
    }

    private fun setLetter5(
        holder: ViewHolder,
        position: Int
    ) {
        if (rootSize < 4) holder.item.letter5.visibility = View.GONE
        else
            holder.item.letter5.letterImageView.apply {
                getView(masterArray[rootSize - 1].rootList[position][3].toString())?.let {
                    setImageResource(
                        it
                    )
                }
                if (rootSize > 4) layoutParams = getMarginRight(this)
            }
    }

    private fun setLetter4(
        holder: ViewHolder,
        position: Int
    ) {
        if (rootSize < 3) holder.item.letter4.visibility = View.GONE
        else
            holder.item.letter4.letterImageView.apply {
                getView(masterArray[rootSize - 1].rootList[position][2].toString())?.let {
                    setImageResource(
                        it
                    )
                }
                if (rootSize > 3) layoutParams = getMarginRight(this)
            }
    }

    private fun setLetter3(
        holder: ViewHolder,
        position: Int
    ) {
        if (rootSize < 2) holder.item.letter3.visibility = View.GONE
        else
            holder.item.letter3.letterImageView.apply {
                getView(masterArray[rootSize - 1].rootList[position][1].toString())?.let {
                    setImageResource(
                        it
                    )
                }
                if (rootSize > 2) layoutParams = getMarginRight(this)
            }
    }

    private fun setLetter2(
        holder: ViewHolder,
        position: Int
    ) {
        holder.item.letter2.letterImageView.apply {
            getView(masterArray[rootSize - 1].rootList[position][0].toString())?.let {
                setImageResource(
                    it
                )
            }
            if (rootSize > 1) layoutParams = getMarginRight(this)
        }
    }

    private fun getMarginRight(imageView: ImageView): ViewGroup.LayoutParams {
        val param = imageView.layoutParams as ViewGroup.MarginLayoutParams
        param.rightMargin = RIGHT_MARGIN_PIXELS
        return param
    }

    private fun getView(text: String): Int? = hashTables.viewMap[text]
}