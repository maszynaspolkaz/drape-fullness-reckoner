package com.drapefullness.reckoner.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.theme.TabularNumber
import kotlin.math.roundToInt

@Composable
fun MeasureField(
    label: String,
    display: String,
    value: Int,
    range: IntRange,
    step: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    var editing by rememberSaveable(label) { mutableStateOf(false) }
    var draft by rememberSaveable(label) { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        haptic.tick()
                        onChange((value - step).coerceIn(range.first, range.last))
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.step_minus),
                        color = colors.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = display,
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.bodyLarge.merge(TabularNumber),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .semantics { stateDescription = display }
                        .clickable {
                            draft = (value / 10f).roundToInt().toString()
                            editing = !editing
                        }
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                )
                IconButton(
                    onClick = {
                        haptic.tick()
                        onChange((value + step).coerceIn(range.first, range.last))
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.step_plus),
                        color = colors.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (editing) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = draft,
                    onValueChange = { entry ->
                        draft = entry.filter { it.isDigit() }.take(4)
                        val parsed = draft.toIntOrNull()
                        if (parsed != null) {
                            onChange((parsed * 10).coerceIn(range.first, range.last))
                        }
                    },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.enter_centimetres)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
