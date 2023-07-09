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

package com.simpleman.pomodoro.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simpleman.pomodoro.ui.theme.MyApplicationTheme
import kotlin.time.Duration.Companion.seconds

@Composable
fun TomatoScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TomatoScreen(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onTomatoClick = viewModel::onTomatoClick,
    )
}

@Composable
internal fun TomatoScreen(
    uiState: MainUiState,
    onTomatoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        val tomatoColor = when (uiState.tomatoState) {
            TomatoState.Idle -> Gray
            is TomatoState.RedPause -> Red.copy(alpha = 0.2F)
            is TomatoState.RedRunning -> Red
            is TomatoState.GreenRunning -> Green
            is TomatoState.GreenPause -> Green.copy(alpha = 0.2F)
        }
        val tomatoText = when (uiState.tomatoState) {
            TomatoState.Idle -> ""
            is TomatoState.RedPause -> uiState.tomatoState.timeLeftInSec.seconds
            is TomatoState.RedRunning -> uiState.tomatoState.timeLeftInSec.seconds
            is TomatoState.GreenRunning -> uiState.tomatoState.timeLeftInSec.seconds
            is TomatoState.GreenPause -> uiState.tomatoState.timeLeftInSec.seconds
        }.toString()
        val description = when (uiState.tomatoState) {
            TomatoState.Idle -> "開啟第一開番茄鐘吧～"
            is TomatoState.RedPause -> "停一下"
            is TomatoState.RedRunning -> "專注專注～"
            is TomatoState.GreenRunning -> "休息一下，進廣告～"
            is TomatoState.GreenPause -> "暫停休息，小專注～"
        }.toString()

        Column(
            modifier = modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(180.dp))
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onTomatoClick)
                    .drawBehind {
                        drawCircle(color = tomatoColor, radius = size.maxDimension / 2)
                    },
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = tomatoText,
                    color = White,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = description,
                color = DarkGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        TomatoScreen()
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        TomatoScreen()
    }
}
