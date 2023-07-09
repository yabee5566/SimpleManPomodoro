/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.simple_man.pomodoro.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.simple_man.pomodoro.data.TomatoRepository
import com.simple_man.pomodoro.extension.ConflatedJob
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tomatoRepository: TomatoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(tomatoState = TomatoState.Idle))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("haha", "ahhhh!", throwable)

    }

    private val countDownJob = ConflatedJob()

    init {


    }

    private fun startCounting() {
        Log.d("haha", "startCounting")
        countDownJob += viewModelScope.launch(exceptionHandler) {
            while (true) {
                val tomatoState = _uiState.value.tomatoState
                delay((1).seconds)
                when (tomatoState) {
                    is TomatoState.GreenRunning -> {
                        if (tomatoState.timeLeftInSec - 1 <= 0) {
                            _uiState.update {
                                it.copy(
                                    tomatoState = TomatoState.RedRunning(
                                        RED_DURATION_IN_SEC
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    tomatoState = tomatoState
                                        .copy(timeLeftInSec = tomatoState.timeLeftInSec - 1)
                                )
                            }
                        }
                    }
                    is TomatoState.RedRunning -> {
                        if (tomatoState.timeLeftInSec - 1 <= 0) {
                            _uiState.update {
                                it.copy(
                                    tomatoState = TomatoState.GreenRunning(
                                        GREEN_DURATION_IN_SEC
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    tomatoState = tomatoState
                                        .copy(timeLeftInSec = tomatoState.timeLeftInSec - 1)
                                )
                            }
                        }
                    }
                    TomatoState.Idle, is TomatoState.RedPause, is TomatoState.GreenPause -> {
                        Log.d("haha", "do nothing")
                    }
                }
            }
        }

    }

    private fun stopCounting() {
        Log.d("haha", "stopCounting")
        countDownJob.cancel()
    }

    fun onTomatoClick() {
        viewModelScope.launch(exceptionHandler) {
            val tomatoState = when (val prevTomatoState = _uiState.value.tomatoState) {
                TomatoState.Idle -> {
                    startCounting()
                    TomatoState.RedRunning(RED_DURATION_IN_SEC)
                }
                is TomatoState.RedRunning -> {
                    stopCounting()
                    TomatoState.RedPause(prevTomatoState.timeLeftInSec)
                }
                is TomatoState.RedPause -> {
                    startCounting()
                    TomatoState.RedRunning(prevTomatoState.timeLeftInSec)
                }
                is TomatoState.GreenRunning -> {
                    stopCounting()
                    TomatoState.GreenPause(prevTomatoState.timeLeftInSec)
                }
                is TomatoState.GreenPause -> {
                    startCounting()
                    TomatoState.GreenRunning(prevTomatoState.timeLeftInSec)
                }
            }
            _uiState.update { it.copy(tomatoState = tomatoState) }
        }
    }

    companion object {
        const val RED_DURATION_IN_SEC = 25 * 60
        const val GREEN_DURATION_IN_SEC = 15 * 60
    }
}
