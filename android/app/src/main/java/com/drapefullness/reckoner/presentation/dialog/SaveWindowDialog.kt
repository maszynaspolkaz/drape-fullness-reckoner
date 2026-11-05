package com.drapefullness.reckoner.presentation.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun SaveWindowDialog(
    rooms: List<String>,
    initialRoom: String,
    initialLabel: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    var room by rememberSaveable { mutableStateOf(initialRoom) }
    var label by rememberSaveable { mutableStateOf(initialLabel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = stringResource(R.string.dialog_park_title),
                color = colors.textPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dialog_room),
                    color = colors.textTertiary,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rooms.forEach { entry ->
                        val selected = entry == room
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(40.dp)
                                .background(
                                    if (selected) {
                                        colors.brass.copy(alpha = 0.14f)
                                    } else {
                                        colors.surfaceVariant
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    1.dp,
                                    if (selected) colors.brass else colors.hairline,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { room = entry }
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = entry,
                                color = colors.textSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it.take(28) },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.dialog_new_room)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it.take(28) },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.dialog_window_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(room, label) }) {
                Text(
                    text = stringResource(R.string.save),
                    color = colors.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = colors.textTertiary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    )
}
