package com.drapefullness.reckoner.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    val shape = RoundedCornerShape(16.dp)
    val fill = if (enabled) {
        Brush.horizontalGradient(listOf(colors.primary, colors.primaryDeep))
    } else {
        Brush.horizontalGradient(
            listOf(colors.primary.copy(alpha = 0.38f), colors.primaryDeep.copy(alpha = 0.38f))
        )
    }
    Button(
        onClick = {
            haptic.tap()
            onClick()
        },
        enabled = enabled,
        shape = shape,
        interactionSource = interaction,
        elevation = null,
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    colors.brass.copy(alpha = if (enabled) 0.55f else 0.18f),
                    Color.Transparent
                )
            )
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.onPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.textTertiary
        ),
        modifier = modifier
            .height(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = shape,
                ambientColor = colors.primary,
                spotColor = colors.primary
            )
            .background(brush = fill, shape = shape)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
