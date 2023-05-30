package com.example.android.pictureinpicture.extensions

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import com.example.android.pictureinpicture.R
import com.google.android.material.snackbar.Snackbar

fun Snackbar.customConfig(context: Context) {
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(16, 16, 16, 16)
    this.view.layoutParams = params

    this.view.background = AppCompatResources.getDrawable(context, R.drawable.bg_snackbar)

    ViewCompat.setElevation(this.view, 8f)
}