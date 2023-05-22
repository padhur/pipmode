package com.example.android.pictureinpicture

import android.os.Build
import android.os.Build.VERSION_CODES

object AndroidSystemUtils {

    fun isEnhancedPIPFeatureSupported() = Build.VERSION.SDK_INT >= VERSION_CODES.S

    fun isPIPModeSupported() = Build.VERSION.SDK_INT >= VERSION_CODES.O
}