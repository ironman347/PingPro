package com.example.pingpro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionsDialog(
    state: PingState,
    onEvent: (PingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(PingEvent.HideOptions)
        },
        confirmButton = {
            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        onEvent(PingEvent.HideOptions)
                    })
                {
                    Text(text = "Save")
                }
            }


        },
        title = { Text(text = "Options") },
        text = {
            Column (
                verticalArrangement = Arrangement.Center
            ) {

                DelaySlider(state = state, onEvent = onEvent)

            }
        },
    )
}


@Composable
fun DelaySlider(
    state: PingState,
    onEvent: (PingEvent) -> Unit,
) {
    var delaySliderPosition by remember { mutableFloatStateOf(1f) }
    var viewablePingsSliderPosition by remember { mutableIntStateOf(10) }
    Column {
        Text(text = "Delay")
        Slider(
            value = state.delayTime.toFloat()/1000,
            onValueChange = {
                delaySliderPosition = it
                onEvent(PingEvent.setDelay(delaySliderPosition.toLong()*1000))
                            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 5,
            valueRange = 1f..25f
        )
        Text(text = delaySliderPosition.toInt().toString() + "s")
        Text(text = "Viewable Pings", modifier = Modifier.padding(top = 16.dp))
        Slider(
            value = state.viewablePings.toFloat(),
            onValueChange = {
                viewablePingsSliderPosition = it.toInt()
                onEvent(PingEvent.setViewablePings(viewablePingsSliderPosition.toInt()))
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 5,
            valueRange = 10f..60f
        )
        Text(text = viewablePingsSliderPosition.toString() + " Pings")
    }
}
