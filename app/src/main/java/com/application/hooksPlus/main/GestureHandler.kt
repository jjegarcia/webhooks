package com.application.hooksPlus.main

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

class GestureHandler constructor(private val boardActivity: BoardActivity) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i("test", "slided")
        if (event2.x -event1.x >0) boardActivity.loadNextRoot()
        return true
    }
}


