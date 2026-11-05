package com.drapefullness.reckoner.presentation.stackpreview

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.EditorialHeader
import com.drapefullness.reckoner.core.ui.SecondaryButton
import com.drapefullness.reckoner.core.ui.SpecRow
import com.drapefullness.reckoner.core.ui.WindowSketchCanvas
import com.drapefullness.reckoner.core.ui.rememberHapticTrigger

@Composable
fun StackPreviewScreen(
    windowId: String,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val stackViewModel: StackPreviewViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by stackViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(windowId) {
        stackViewModel.load(windowId)
    }

    StackPreviewContent(
        state = state,
        onDrag = stackViewModel::drag,
        onRelease = stackViewModel::release,
        onBack = onBack,
        onMenu = onMenu
    )
}

@Composable
private fun StackPreviewContent(
    state: StackPreviewUiState,
    onDrag: (Float) -> Unit,
    onRelease: () -> Unit,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    val glide by animateFloatAsState(
        targetValue = state.openFraction,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "glide"
    )
    val stateLabel = stringResource(stackStateLabel(state.stackState))
    val canvasLabel = stringResource(R.string.cd_stack_preview)

    AppBackground(
        overlay = listOf(
            colors.gradientTop.copy(alpha = 0.94f),
            colors.gradientMid.copy(alpha = 0.96f),
            colors.gradientBottom.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            EditorialHeader(
                title = stringResource(R.string.stack_preview),
                subtitle = state.windowLabel,
                onBack = onBack
            )
            WindowSketchCanvas(
                trackWidthMm = state.trackWidthMm,
                dropMm = state.dropMm,
                clearanceMm = state.clearanceMm,
                openFraction = glide,
                foldCount = state.foldCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .semantics {
                        contentDescription = canvasLabel
                        stateDescription = stateLabel
                    }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ) {
                Surface(
                    color = colors.brass,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .draggable(
                            state = rememberDraggableState { delta -> onDrag(delta / 600f) },
                            orientation = Orientation.Horizontal,
                            onDragStopped = {
                                haptic.tick()
                                onRelease()
                            }
                        )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_drag_handle),
                            contentDescription = stringResource(R.string.cd_drag_handle),
                            tint = colors.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                SpecRow(
                    label = stringResource(R.string.spec_stack_back),
                    value = state.stackBackLabel,
                    highlight = true
                )
                SpecRow(
                    label = stringResource(R.string.spec_glass_covered),
                    value = state.coveredLabel
                )
                SpecRow(
                    label = stringResource(R.string.spec_track_width),
                    value = state.trackLabel
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.back_to_cut_sheet),
                    onClick = onBack,
                    brassOutline = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = onMenu, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text(
                        text = stringResource(R.string.menu),
                        color = colors.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun stackStateLabel(state: StackState): Int = when (state) {
    StackState.OPEN -> R.string.stack_open
    StackState.HALF_OPEN -> R.string.stack_half_open
    StackState.CLOSED -> R.string.stack_closed
}
