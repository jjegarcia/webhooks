package com.application.hooksPlus.keyboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.board.LetterConfigurator
import com.application.hooksPlus.support.AdaptersBridge
import com.application.hooksPlus.support.AdditionalHandlersBridge
import com.application.hooksPlus.support.HandlersBridge
import com.application.hooksPlus.support.ProvidersBridge
import kotlinx.android.synthetic.main.key_container.view.*
import kotlinx.android.synthetic.main.letter_tile.view.*

class KeyboardAdapter(
    val letterHashMap: HashMap<String, Int>,
    private val handlersBridge: HandlersBridge,
    val additionalHandlersBridge: AdditionalHandlersBridge,
    private val adaptersBridge: AdaptersBridge,
    val letterConfigurator: LetterConfigurator,
    private val providersBridge: ProvidersBridge
) :
    RecyclerView.Adapter<KeyboardAdapter.ViewHolder>() {
    lateinit var letterList:List<Pair<String,Int>>

    class ViewHolder(itemViewP: View) : RecyclerView.ViewHolder(itemViewP) {
        val item: ImageView =itemViewP.letterImageView
        val itemRightLayout: LinearLayout = itemViewP.right_marker
        val itemLeftLayout: LinearLayout = itemViewP.left_marker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        letterList=letterHashMap.toList()
        val v = LayoutInflater.from(parent.context).inflate(R.layout.key_container, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = letterHashMap.size

    @OptIn(ExperimentalStdlibApi::class)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    holder.item.context,
                    letterList[position].second
                )
            )
            tag = letterList[position].first

            setOnClickListener {
                handlersBridge.keyboardHandler?.handleKeyClickListener(
                    view = it,
                    letterConfigurator = letterConfigurator
                )
            }
        }
        providersBridge.keyboardColorProvider?.getColorFromCurrentTableLeft(letterList[position].first)?.let {
            holder.itemLeftLayout.setBackgroundColor(it)
        }
        providersBridge.keyboardColorProvider?.getColorFromCurrentTableRight(letterList[position].first)?.let {
            holder.itemRightLayout.setBackgroundColor(it)
        }
    }
}
