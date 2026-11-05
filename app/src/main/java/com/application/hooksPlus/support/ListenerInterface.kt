package com.application.hooksPlus.support

import android.os.Parcelable
import android.view.View

interface ListenerInterface {
    fun back(view: View)
    fun callNextActivity(view: View, intentArguments: Parcelable)
    fun callNextActivity(view: View, intentArguments: Parcelable, displayPlayerDetails: Boolean)
}
