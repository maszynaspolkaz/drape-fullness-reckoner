package com.drapefullness.reckoner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = colors.textTertiary,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.width(12.dp))
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.hairline)
        )
    }
}
