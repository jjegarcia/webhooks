package com.application.hooksPlus.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.application.hooksPlus.R
import kotlinx.android.synthetic.main.dot_container.view.*

class DotsAdapter constructor(
    val rootSize: Int,
    val numberOfItems: Int
) : Adapter<DotsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val onItem: ImageView = itemView.onDot
        val offItem: ImageView = itemView.offDot
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dot_container, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = numberOfItems

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.offItem.visibility = if (!(position == rootSize - 1)) View.VISIBLE
        else View.GONE
        holder.onItem.visibility = if (position == rootSize - 1) View.VISIBLE
        else View.GONE
    }
}