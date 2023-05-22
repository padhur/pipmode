/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.pictureinpicture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.android.pictureinpicture.interfaces.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val timeProvider: TimeProvider): ViewModel() {

    private var job: Job? = null

    private var startUptimeMillis = timeProvider.uptimeMillis()
    private val timeMillis = MutableLiveData(0L)

    private val _started = MutableLiveData(false)

    val started: LiveData<Boolean> = _started
    val time = timeMillis.map { millis ->
        val minutes = millis / 1000 / 60
        val m = minutes.toString().padStart(2, '0')
        val seconds = (millis / 1000) % 60
        val s = seconds.toString().padStart(2, '0')
        val hundredths = (millis % 1000) / 10
        val h = hundredths.toString().padStart(2, '0')
        "$m:$s:$h"
    }

    /**
     * Starts the stopwatch if it is not yet started, or pauses it if it is already started.
     */
    fun startOrPause() {
        if (_started.value == true) {
            _started.value = false
            job?.cancel()
        } else {
            _started.value = true
            job = viewModelScope.launch(Dispatchers.Default) { start() }
        }
    }

    private suspend fun CoroutineScope.start() {
        startUptimeMillis = timeProvider.uptimeMillis() - (timeMillis.value ?: 0L)
        while (isActive) {
            val elapsedTime = timeProvider.uptimeMillis() - startUptimeMillis
            withContext(Dispatchers.Main) {
                timeMillis.value = elapsedTime
            }
            // Delay the computation to avoid excessive CPU usage
            delay(UPDATE_INTERVAL_MS)
        }
    }

    /**
     * Clears the stopwatch to 00:00:00.
     */
    fun clear() {
        startUptimeMillis = timeProvider.uptimeMillis()
        timeMillis.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        clear()
        job?.cancel()
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 10L
    }
}