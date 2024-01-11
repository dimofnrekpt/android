package com.x8bit.bitwarden.ui.platform.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.x8bit.bitwarden.R

/**
 * A custom composable representing a dialog that displays the time picker dialog.
 *
 * @param initialHour The initial hour to display.
 * @param initialMinute The initial minute to display.
 * @param onTimeSelect The callback to be invoked when a new time is selected.
 * @param onDismissRequest The callback to be invoked when a time has been selected.
 * @param is24Hour Indicates if the time selector should use a 24 hour format or a 12 hour format
 * with AM/PM.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun BitwardenTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelect: (hour: Int, minute: Int) -> Unit,
    onDismissRequest: () -> Unit,
    is24Hour: Boolean,
) {
    var showTimeInput by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )
    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onTimeSelect(timePickerState.hour, timePickerState.minute) },
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        inputToggleButton = {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { showTimeInput = !showTimeInput },
            ) {
                @Suppress("MaxLineLength")
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_keyboard),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(
                        // TODO: Get our own string for this (BIT-1405)
                        id = androidx.compose.material3.R.string.m3c_date_picker_switch_to_input_mode,
                    ),
                )
            }
        },
    ) {
        val modifier = Modifier.weight(1f)
        if (showTimeInput) {
            TimeInput(
                state = timePickerState,
                modifier = modifier,
            )
        } else {
            TimePicker(
                state = timePickerState,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    inputToggleButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    // TODO: This should be "Select time" but we don't have that string (BIT-1405)
                    text = stringResource(id = R.string.time),
                    style = MaterialTheme.typography.labelMedium,
                )

                content()

                Row(modifier = Modifier.fillMaxWidth()) {
                    inputToggleButton()
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton()
                    Spacer(modifier = Modifier.width(8.dp))
                    confirmButton()
                }
            }
        }
    }
}
