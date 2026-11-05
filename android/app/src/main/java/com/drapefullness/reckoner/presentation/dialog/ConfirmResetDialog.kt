package com.drapefullness.reckoner.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = stringResource(R.string.reset_defaults),
                color = colors.textPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_reset_body),
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.reset_defaults),
                    color = colors.error,
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
