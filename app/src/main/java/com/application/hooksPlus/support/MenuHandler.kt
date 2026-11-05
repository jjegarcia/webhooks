package com.application.hooksPlus.support

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.application.hooksPlus.R

class MenuHandler : ComponentActivity() {
    fun initialiseButton(button: ImageView) {
        setImage(button)
        button.visibility = View.GONE
    }

    private fun setImage(button: ImageView) {
        Log.i("test", "Menu Selected")
        button.setImageResource(R.drawable.ic_chrome_reader_mode_24px)
    }

    fun setVisibility(button: ImageView) {
        button.visibility = View.VISIBLE
    }
}