package com.drapefullness.reckoner.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brassOutline: Boolean = false
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "pressScaleSecondary"
    )
    val accent = if (brassOutline) colors.brass else colors.primary
    OutlinedButton(
        onClick = {
            haptic.tap()
            onClick()
        },
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        interactionSource = interaction,
        border = BorderStroke(1.dp, if (enabled) accent else colors.hairline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = accent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.textTertiary
        ),
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
