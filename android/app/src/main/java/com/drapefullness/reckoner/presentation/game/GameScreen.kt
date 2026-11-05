package com.drapefullness.reckoner.presentation.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.theme.TabularNumber
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.PrimaryButton
import com.drapefullness.reckoner.core.ui.SecondaryButton
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.semantics.stateDescription
import com.drapefullness.reckoner.core.config.GameConfig
import com.drapefullness.reckoner.core.ui.HeadingStyleCard
import com.drapefullness.reckoner.core.ui.MeasureField
import com.drapefullness.reckoner.core.ui.WindowSketchCanvas
import com.drapefullness.reckoner.core.ui.headingBand
import com.drapefullness.reckoner.core.ui.headingName
import com.drapefullness.reckoner.core.ui.headingSprite
import com.drapefullness.reckoner.core.ui.rememberHapticTrigger
import com.drapefullness.reckoner.domain.model.HeadingStyle
import com.drapefullness.reckoner.domain.model.MeasureStep
import com.drapefullness.reckoner.presentation.dialog.SaveWindowDialog

@Composable
fun GameScreen(
    windowId: String?,
    onCalculated: (String) -> Unit,
    onParked: () -> Unit,
    onBack: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(windowId) {
        gameViewModel.start(windowId)
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.persistDraft() }
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Calculated -> onCalculated(event.windowId)
                GameEvent.Parked -> onParked()
            }
        }
    }

    val actions = remember(gameViewModel) {
        GameActions(
            onTrack = gameViewModel::setTrackWidth,
            onDrop = gameViewModel::setDrop,
            onClearance = gameViewModel::setClearance,
            onFabricWidth = gameViewModel::setFabricWidth,
            onRepeat = gameViewModel::setPatternRepeat,
            onPatternMatch = gameViewModel::setPatternMatch,
            onLined = gameViewModel::setLined,
            onHeading = gameViewModel::setHeading,
            onFullness = gameViewModel::setFullness,
            onPreset = gameViewModel::applyPreset,
            onNext = gameViewModel::next,
            onStepBack = gameViewModel::back,
            onCalculate = gameViewModel::calculate,
            onPark = gameViewModel::openDialog
        )
    }

    GameContent(
        state = state,
        actions = actions,
        onBack = onBack
    )

    if (state.dialogVisible) {
        SaveWindowDialog(
            rooms = state.rooms,
            initialRoom = state.window.room,
            initialLabel = state.window.label,
            onConfirm = { room, label -> gameViewModel.park(room, label) },
            onDismiss = { gameViewModel.dismissDialog() }
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    actions: GameActions,
    onBack: () -> Unit
) {
    val colors = LocalAppColors.current
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
        ) {
            GameHeader(step = state.step, onBack = onBack, onPark = actions.onPark)
            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    (slideInHorizontally(tween(280)) { it / 4 } + fadeIn(tween(220)))
                        .togetherWith(
                            slideOutHorizontally(tween(280)) { -it / 4 } + fadeOut(tween(180))
                        )
                },
                label = "step",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    MeasureStep.MEASURE -> MeasureStepContent(state = state, actions = actions)
                    MeasureStep.HEADING -> HeadingStepContent(state = state, actions = actions)
                    MeasureStep.FABRIC -> FabricStepContent(state = state, actions = actions)
                }
            }
            ResultStrip(state = state)
            GameControls(state = state, actions = actions)
        }
    }
}

@Composable
private fun GameHeader(
    step: MeasureStep,
    onBack: () -> Unit,
    onPark: () -> Unit
) {
    val colors = LocalAppColors.current
    Surface(
        color = colors.surface.copy(alpha = 0.72f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MeasureStep.entries.forEach { entry ->
                            val active = entry == step
                            val width by animateDpAsState(
                                targetValue = if (active) 24.dp else 8.dp,
                                animationSpec = tween(280),
                                label = "dot"
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(width)
                                    .height(8.dp)
                                    .background(
                                        if (active) colors.brass else colors.hairline,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(stepTitle(step)),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(onClick = onPark, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_park),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.hairline)
            )
        }
    }
}

private fun stepTitle(step: MeasureStep): Int = when (step) {
    MeasureStep.MEASURE -> R.string.step_measure
    MeasureStep.HEADING -> R.string.step_heading
    MeasureStep.FABRIC -> R.string.step_fabric
}

@Composable
private fun ResultStrip(state: GameUiState) {
    val colors = LocalAppColors.current
    val widths by animateIntAsState(
        targetValue = state.widths,
        animationSpec = tween(260),
        label = "widths"
    )
    Surface(
        color = colors.surface.copy(alpha = 0.86f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.hairline)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 18.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                StripValue(
                    label = stringResource(R.string.strip_widths),
                    value = widths.toString(),
                    accent = false
                )
                StripValue(
                    label = stringResource(R.string.strip_cut_length),
                    value = state.cutLengthLabel,
                    accent = false
                )
                StripValue(
                    label = stringResource(R.string.strip_total),
                    value = state.totalLabel,
                    accent = true
                )
            }
        }
    }
}

@Composable
private fun StripValue(
    label: String,
    value: String,
    accent: Boolean
) {
    val colors = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = colors.textTertiary,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = if (accent) colors.brass else colors.textPrimary,
            style = MaterialTheme.typography.bodyLarge.merge(TabularNumber)
        )
    }
}

@Composable
private fun GameControls(
    state: GameUiState,
    actions: GameActions
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface.copy(alpha = 0.86f))
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        if (state.error != null) {
            Text(
                text = stringResource(errorMessage(state.error)),
                color = colors.error,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.step != MeasureStep.MEASURE) {
                SecondaryButton(
                    text = stringResource(R.string.back),
                    onClick = actions.onStepBack
                )
            }
            PrimaryButton(
                text = stringResource(
                    if (state.step == MeasureStep.FABRIC) R.string.calculate else R.string.next
                ),
                onClick = {
                    if (state.step == MeasureStep.FABRIC) {
                        actions.onCalculate()
                    } else {
                        actions.onNext()
                    }
                },
                enabled = state.error == null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun errorMessage(error: GameError): Int = when (error) {
    GameError.TRACK_WIDTH -> R.string.error_track_width
    GameError.DROP -> R.string.error_drop
    GameError.FABRIC_WIDTH -> R.string.error_fabric_width
}

@Composable
private fun MeasureStepContent(
    state: GameUiState,
    actions: GameActions
) {
    val colors = LocalAppColors.current
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            WindowSketchCanvas(
                trackWidthMm = state.window.trackWidthMm,
                dropMm = state.window.dropMm,
                clearanceMm = state.window.clearanceMm,
                openFraction = 0f,
                foldCount = state.foldCount,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
        }
        item {
            MeasureField(
                label = stringResource(R.string.field_track_width),
                display = state.trackLabel,
                value = state.window.trackWidthMm,
                range = GameConfig.TRACK_WIDTH_MIN_MM..GameConfig.TRACK_WIDTH_MAX_MM,
                step = GameConfig.STEP_SMALL_MM,
                onChange = actions.onTrack
            )
        }
        item {
            MeasureField(
                label = stringResource(R.string.field_drop),
                display = state.dropLabel,
                value = state.window.dropMm,
                range = GameConfig.DROP_MIN_MM..GameConfig.DROP_MAX_MM,
                step = GameConfig.STEP_SMALL_MM,
                onChange = actions.onDrop
            )
        }
        item {
            MeasureField(
                label = stringResource(R.string.field_clearance),
                display = state.clearanceLabel,
                value = state.window.clearanceMm,
                range = GameConfig.CLEARANCE_MIN_MM..GameConfig.CLEARANCE_MAX_MM,
                step = GameConfig.STEP_SMALL_MM,
                onChange = actions.onClearance
            )
        }
        item {
            Text(
                text = stringResource(R.string.quick_presets),
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PresetChip(
                    text = stringResource(R.string.preset_sill),
                    onClick = { actions.onPreset(1400, 1200) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_floor),
                    onClick = { actions.onPreset(1800, 2400) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_bay),
                    onClick = { actions.onPreset(3000, 2400) }
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    text: String,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(40.dp)
            .border(1.dp, colors.hairline, RoundedCornerShape(20.dp))
            .clickable {
                haptic.tick()
                onClick()
            }
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = text,
            color = colors.textSecondary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun HeadingStepContent(
    state: GameUiState,
    actions: GameActions
) {
    val colors = LocalAppColors.current
    val heading = state.window.heading
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(HeadingStyle.entries, key = { it.name }) { style ->
            HeadingStyleCard(
                spriteRes = headingSprite(style),
                name = stringResource(headingName(style)),
                band = stringResource(headingBand(style)),
                selected = style == heading,
                onSelect = { actions.onHeading(style) }
            )
        }
        item(span = { GridItemSpan(2) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.fullnessLabel + " " + stringResource(R.string.fullness_suffix),
                    color = colors.brass,
                    style = MaterialTheme.typography.headlineSmall
                )
                Slider(
                    value = state.window.fullness,
                    onValueChange = actions.onFullness,
                    valueRange = heading.minFullness..heading.maxFullness,
                    steps = heading.sliderSteps,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.brass,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.hairline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { stateDescription = state.fullnessLabel }
                )
                Text(
                    text = stringResource(foldsLabel(state.foldsBand)),
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

private fun foldsLabel(band: FoldsBand): Int = when (band) {
    FoldsBand.GENEROUS -> R.string.folds_generous
    FoldsBand.TAILORED -> R.string.folds_tailored
    FoldsBand.FLAT -> R.string.folds_flat
}

@Composable
private fun FabricStepContent(
    state: GameUiState,
    actions: GameActions
) {
    val colors = LocalAppColors.current
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            MeasureField(
                label = stringResource(R.string.field_fabric_width),
                display = state.fabricWidthLabel,
                value = state.window.fabricWidthMm,
                range = GameConfig.FABRIC_WIDTH_MIN_MM..GameConfig.FABRIC_WIDTH_MAX_MM,
                step = GameConfig.STEP_SMALL_MM,
                onChange = actions.onFabricWidth
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PresetChip(
                    text = stringResource(R.string.preset_width_137),
                    onClick = { actions.onFabricWidth(1370) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_width_140),
                    onClick = { actions.onFabricWidth(1400) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_width_280),
                    onClick = { actions.onFabricWidth(2800) }
                )
            }
        }
        item {
            MeasureField(
                label = stringResource(R.string.field_pattern_repeat),
                display = state.repeatLabel,
                value = state.window.patternRepeatMm,
                range = GameConfig.PATTERN_REPEAT_MIN_MM..GameConfig.PATTERN_REPEAT_MAX_MM,
                step = GameConfig.STEP_SMALL_MM,
                onChange = actions.onRepeat
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PresetChip(
                    text = stringResource(R.string.preset_repeat_0),
                    onClick = { actions.onRepeat(0) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_repeat_32),
                    onClick = { actions.onRepeat(320) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_repeat_64),
                    onClick = { actions.onRepeat(640) }
                )
            }
        }
        item {
            SwitchRow(
                label = stringResource(R.string.switch_pattern_match),
                checked = state.window.patternMatch,
                enabled = state.window.patternRepeatMm > 0,
                onCheckedChange = actions.onPatternMatch
            )
        }
        item {
            SwitchRow(
                label = stringResource(R.string.switch_lined),
                checked = state.window.lined,
                enabled = true,
                onCheckedChange = actions.onLined
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(16.dp))
                    .border(1.dp, colors.hairline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(headingSprite(state.window.heading)),
                    contentDescription = stringResource(headingName(state.window.heading)),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(headingName(state.window.heading)),
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = state.fullnessLabel,
                    color = colors.brass,
                    style = MaterialTheme.typography.labelMedium.merge(TabularNumber)
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, colors.hairline, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .height(48.dp)
    ) {
        Text(
            text = label,
            color = if (enabled) colors.textSecondary else colors.textTertiary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                uncheckedTrackColor = colors.hairline
            )
        )
    }
}
