package com.application.hooksPlus.support

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.application.hooksPlus.R

class ShiftHandler {
    var pushed: Boolean = false
    var buttonImageView: ImageView? = null
    fun initialiseButton(button: ImageView) {
        setImage(button)
        button.visibility = View.VISIBLE
        buttonImageView = button
    }

    private fun setImage(button: ImageView) {
        Log.i("test", "Menu Selected")
        button.setImageResource(R.drawable.ic_shift)
    }

    fun setVisibility(button: ImageView) {
        button.visibility = View.VISIBLE
    }

    fun clearVisibility(button: ImageView) {
        button.visibility = View.GONE
    }

    fun setPushedColor(button: ImageView) {
        button.setBackgroundColor(Colors.BLUE.color)
    }

    fun resetPushedColor(button: ImageView) {
        button.setBackgroundColor(0xfdce4ed)
    }

    fun toggleButton(button: ImageView) {
        if (!pushed)
            setPushedColor(button)
        else
            resetPushedColor(button)
        pushed = !pushed
    }

    fun toggleButton() {
        if (!pushed)
            buttonImageView?.let { setPushedColor(it) }
        else
            buttonImageView?.let { resetPushedColor(it) }
        pushed = !pushed
    }

    fun getButtonState() = pushed

    fun clearVisibility() {
        buttonImageView?.visibility = View.GONE
    }
}