package com.drapefullness.reckoner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.theme.TabularNumber

@Composable
fun SpecRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val colors = LocalAppColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = label,
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                color = if (highlight) colors.brass else colors.textPrimary,
                style = MaterialTheme.typography.bodyLarge.merge(TabularNumber),
                fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
                textAlign = TextAlign.End
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.hairline)
        )
    }
}
