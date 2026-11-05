package com.drapefullness.reckoner.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.drapefullness.reckoner.core.theme.LocalAppColors

@Composable
fun WindowSketchCanvas(
    trackWidthMm: Int,
    dropMm: Int,
    clearanceMm: Int,
    openFraction: Float,
    foldCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val glass = colors.gradientBottom
    val frame = colors.primary
    val brass = colors.brass
    val panelNear = colors.primary.copy(alpha = 0.82f)
    val panelFar = colors.primaryDeep.copy(alpha = 0.70f)
    val shadow = colors.textPrimary.copy(alpha = 0.10f)

    Canvas(modifier = modifier) {
        val pad = size.minDimension * 0.10f
        val poleY = pad
        val innerW = size.width - pad * 2f
        val innerH = size.height - pad * 2f

        val ratio = (dropMm.toFloat() / trackWidthMm.toFloat()).coerceIn(0.35f, 2.2f)
        var frameW = innerW * 0.78f
        var frameH = frameW * ratio
        if (frameH > innerH * 0.80f) {
            frameH = innerH * 0.80f
            frameW = frameH / ratio
        }
        val frameLeft = (size.width - frameW) / 2f
        val frameTop = poleY + innerH * 0.10f

        drawRect(
            color = glass,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameW, frameH)
        )
        drawRect(
            color = frame,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameW, frameH),
            style = Stroke(width = 2f)
        )
        drawLine(
            color = frame.copy(alpha = 0.55f),
            start = Offset(frameLeft + frameW / 2f, frameTop),
            end = Offset(frameLeft + frameW / 2f, frameTop + frameH),
            strokeWidth = 1.5f
        )

        val poleLeft = frameLeft - innerW * 0.08f
        val poleRight = frameLeft + frameW + innerW * 0.08f
        drawLine(
            color = brass,
            start = Offset(poleLeft, poleY),
            end = Offset(poleRight, poleY),
            strokeWidth = 6f
        )
        drawCircle(color = brass, radius = 7f, center = Offset(poleLeft, poleY))
        drawCircle(color = brass, radius = 7f, center = Offset(poleRight, poleY))

        val panelFullW = (frameLeft + frameW / 2f - poleLeft)
        val open = openFraction.coerceIn(0f, 1f)
        val panelW = panelFullW * (1f - open * 0.62f)
        val panelTop = poleY + 6f
        val panelBottom = frameTop + frameH * 0.98f
        val bands = foldCount.coerceIn(3, 26)

        drawRect(
            color = shadow,
            topLeft = Offset(poleLeft, panelBottom),
            size = Size(panelW, 8f)
        )
        drawRect(
            color = shadow,
            topLeft = Offset(poleRight - panelW, panelBottom),
            size = Size(panelW, 8f)
        )

        val bandW = panelW / bands
        for (i in 0 until bands) {
            val tone = if (i % 2 == 0) panelNear else panelFar
            drawRect(
                color = tone,
                topLeft = Offset(poleLeft + i * bandW, panelTop),
                size = Size(bandW * 0.94f, panelBottom - panelTop)
            )
            drawRect(
                color = tone,
                topLeft = Offset(poleRight - panelW + i * bandW, panelTop),
                size = Size(bandW * 0.94f, panelBottom - panelTop)
            )
        }

        val floorY = frameTop + frameH + (innerH * 0.06f)
        drawLine(
            color = colors.textTertiary,
            start = Offset(pad, floorY),
            end = Offset(size.width - pad, floorY),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
        )
        if (clearanceMm > 0) {
            val markX = frameLeft + frameW + innerW * 0.03f
            drawLine(
                color = brass,
                start = Offset(markX, frameTop + frameH),
                end = Offset(markX, floorY),
                strokeWidth = 2f
            )
            drawLine(
                color = brass,
                start = Offset(markX - 6f, floorY),
                end = Offset(markX + 6f, floorY),
                strokeWidth = 2f
            )
        }
    }
}
