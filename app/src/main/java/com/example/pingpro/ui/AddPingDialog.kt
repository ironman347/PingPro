package com.example.pingpro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddPingDialog(
    state: PingState,
    onEvent: (PingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(PingEvent.HideDialog)
        },
        confirmButton = {
            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        onEvent(PingEvent.SavePing)
                    })
                {
                    Text(text = "Save")
                }
            }


        },
        title = { Text(text = state.addingTargetText) },
        text = {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                TextField(value = state.target,
                    onValueChange = {
                        onEvent(PingEvent.setTarget(it))
                    },
                    placeholder = {
                        Text(text = "New Target")
                    })
            }
        },
    )
}