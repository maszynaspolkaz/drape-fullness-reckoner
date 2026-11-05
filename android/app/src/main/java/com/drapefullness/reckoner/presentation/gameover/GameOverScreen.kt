package com.drapefullness.reckoner.presentation.gameover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.theme.TabularNumber
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.PrimaryButton
import com.drapefullness.reckoner.core.ui.SecondaryButton
import com.drapefullness.reckoner.core.ui.SpecRow
import com.drapefullness.reckoner.core.ui.headingName

@Composable
fun GameOverScreen(
    windowId: String,
    onMeasureAgain: (String) -> Unit,
    onStackPreview: (String) -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by resultViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val addedMessage = stringResource(R.string.added_to_list)

    LaunchedEffect(windowId) {
        resultViewModel.load(windowId)
    }

    LaunchedEffect(Unit) {
        resultViewModel.events.collect {
            snackbarHostState.showSnackbar(addedMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameOverContent(
            state = state,
            onTab = resultViewModel::selectTab,
            onAdd = resultViewModel::addToShoppingList,
            onMeasureAgain = { onMeasureAgain(windowId) },
            onStackPreview = { onStackPreview(windowId) },
            onMenu = onMenu
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
        )
    }
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onTab: (ResultTab) -> Unit,
    onAdd: () -> Unit,
    onMeasureAgain: () -> Unit,
    onStackPreview: () -> Unit,
    onMenu: () -> Unit
) {
    val colors = LocalAppColors.current
    AppBackground(
        overlay = listOf(
            colors.gradientTop.copy(alpha = 0.94f),
            colors.gradientMid.copy(alpha = 0.96f),
            colors.gradientBottom.copy(alpha = 0.97f)
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.brass.copy(alpha = 0.07f),
                        colors.brass.copy(alpha = 0f)
                    ),
                    center = Offset(size.width * 0.70f, size.height * 0.18f),
                    radius = size.minDimension * 0.60f
                ),
                radius = size.minDimension * 0.60f,
                center = Offset(size.width * 0.70f, size.height * 0.18f)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.result_title),
                color = colors.textPrimary,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .width(52.dp)
                    .height(2.dp)
                    .background(colors.brass)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.windowLabel,
                color = colors.textSecondary,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                Text(
                    text = state.heroTotal,
                    color = colors.brass,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.displayMedium.merge(TabularNumber)
                )
                Text(
                    text = stringResource(R.string.result_of_fabric) + " " +
                        state.fabricWidthCaption,
                    color = colors.textTertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ResultTabs(selected = state.tab, onTab = onTab)
            AnimatedContent(
                targetState = state.tab,
                transitionSpec = {
                    fadeIn(tween(160)).togetherWith(fadeOut(tween(120)))
                },
                label = "tab",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    ResultTab.CUT_SHEET -> CutSheetTab(state)
                    ResultTab.LINING -> LiningTab(state)
                    ResultTab.PLEATS -> PleatsTab(state)
                }
            }
            Text(
                text = stringResource(R.string.result_hint),
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.measure_again),
                onClick = onMeasureAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.stack_preview),
                    onClick = onStackPreview,
                    brassOutline = true,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.add_to_shopping_list),
                    onClick = onAdd,
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(onClick = onMenu, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(
                    text = stringResource(R.string.menu),
                    color = colors.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ResultTabs(
    selected: ResultTab,
    onTab: (ResultTab) -> Unit
) {
    val colors = LocalAppColors.current
    Row(modifier = Modifier.fillMaxWidth()) {
        ResultTab.entries.forEach { tab ->
            val active = tab == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { onTab(tab) }
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(tabTitle(tab)),
                    color = if (active) colors.textPrimary else colors.textTertiary,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(10.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (active) colors.brass else Color.Transparent)
                )
            }
        }
    }
}

private fun tabTitle(tab: ResultTab): Int = when (tab) {
    ResultTab.CUT_SHEET -> R.string.tab_cut_sheet
    ResultTab.LINING -> R.string.tab_lining
    ResultTab.PLEATS -> R.string.tab_pleats
}

@Composable
private fun CutSheetTab(state: GameOverUiState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            SpecRow(
                label = stringResource(R.string.spec_finished_width),
                value = state.finishedWidth
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_widths), value = state.widths)
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_finished_drop),
                value = state.finishedDrop
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_cut_length), value = state.cutLength)
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_pattern_added),
                value = state.patternAdded
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_total_fabric),
                value = state.totalFabric,
                highlight = true
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_side_hems), value = state.sideHems)
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_header_allowance),
                value = state.headerAllowance
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_bottom_hem), value = state.bottomHem)
        }
    }
}

@Composable
private fun LiningTab(state: GameOverUiState) {
    val colors = LocalAppColors.current
    if (!state.lined) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.lining_unlined),
                color = colors.textSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            SpecRow(
                label = stringResource(R.string.spec_lining_widths),
                value = state.liningWidths
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_lining_cut_length),
                value = state.liningCutLength
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_lining_total),
                value = state.liningTotal,
                highlight = true
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_lining_header),
                value = state.liningHeader
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_lining_hem), value = state.liningHem)
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_lining_set_back),
                value = state.liningSetBack
            )
        }
    }
}

@Composable
private fun PleatsTab(state: GameOverUiState) {
    val colors = LocalAppColors.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                val baseY = size.height * 0.72f
                drawLine(
                    color = colors.primary,
                    start = Offset(0f, baseY),
                    end = Offset(size.width, baseY),
                    strokeWidth = 3f
                )
                val count = state.pleatCount.coerceIn(2, 40)
                val gap = size.width / (count + 1)
                for (i in 1..count) {
                    val x = gap * i
                    drawLine(
                        color = colors.brass,
                        start = Offset(x, baseY),
                        end = Offset(x, baseY - size.height * 0.46f),
                        strokeWidth = 4f
                    )
                }
            }
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_heading),
                value = stringResource(headingName(state.heading))
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_units_per_curtain),
                value = state.unitsPerCurtain
            )
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_fabric_per_unit),
                value = state.fabricPerUnit
            )
        }
        item {
            SpecRow(label = stringResource(R.string.spec_gap), value = state.gapBetween)
        }
        if (state.hasHooks) {
            item {
                SpecRow(
                    label = stringResource(R.string.spec_hooks_per_curtain),
                    value = state.hooksPerCurtain
                )
            }
            item {
                SpecRow(
                    label = stringResource(R.string.spec_hooks_total),
                    value = state.hooksTotal,
                    highlight = true
                )
            }
        }
        item {
            SpecRow(
                label = stringResource(R.string.spec_first_from_edge),
                value = state.firstFromEdge
            )
        }
    }
}
