package com.example.android.pictureinpicture.interfaces

import android.os.SystemClock

class SystemTimeProvider : TimeProvider {
    override fun uptimeMillis(): Long {
        return SystemClock.uptimeMillis()
    }
}
