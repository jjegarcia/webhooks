package com.application.hooksPlus.board


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.support.AdditionalHandlersBridge
import com.application.hooksPlus.support.CurrentLists
import kotlinx.android.synthetic.main.board_tile.view.*
import kotlinx.android.synthetic.main.row_tile.view.*

class BoardAdapter(
    val boardBinder: BoardBinder,
    var masterArray: List<CurrentLists>,
    private var rootSize: Int,
    val additionalHandlersBridge: AdditionalHandlersBridge

) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.board_tile, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        boardBinder.setBoardBinder(
            holder = holder,
            position = position
        )
    }

    override fun getItemCount() = masterArray[rootSize - 1].boardList.size//items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item = itemView.row_tile_box.word
        val bottomPadding=itemView.bottom_padding
        val tileBox=itemView.row_tile_box
        val view=itemView
    }
}

