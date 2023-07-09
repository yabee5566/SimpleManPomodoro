package com.simple_man.pomodoro.ui.main

import android.graphics.Color

data class MainUiState(
    val tomatoState: TomatoState
)

sealed interface ClockTomato {
    val timeLeftInSec: Int
}

sealed interface TomatoState {
    object Idle : TomatoState
    data class RedRunning(override val timeLeftInSec: Int) : TomatoState, ClockTomato
    data class GreenRunning(override val timeLeftInSec: Int) : TomatoState, ClockTomato
    data class RedPause(override val timeLeftInSec: Int) : TomatoState, ClockTomato
    data class GreenPause(override val timeLeftInSec: Int) : TomatoState, ClockTomato
}