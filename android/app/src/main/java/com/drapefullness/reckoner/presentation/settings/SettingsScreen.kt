package com.drapefullness.reckoner.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.config.GameConfig
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.EditorialHeader
import com.drapefullness.reckoner.core.ui.MeasureField
import com.drapefullness.reckoner.core.ui.PrimaryButton
import com.drapefullness.reckoner.core.ui.SectionLabel
import com.drapefullness.reckoner.core.ui.rememberHapticTrigger
import com.drapefullness.reckoner.domain.model.UnitSystem
import com.drapefullness.reckoner.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        actions = SettingsActions(
            onUnits = settingsViewModel::setUnits,
            onSideHem = settingsViewModel::setSideHem,
            onBottomHem = settingsViewModel::setBottomHem,
            onHeaderAllowance = settingsViewModel::setHeaderAllowance,
            onSeamAllowance = settingsViewModel::setSeamAllowance,
            onLiningHem = settingsViewModel::setLiningHem,
            onLiningHeader = settingsViewModel::setLiningHeader,
            onHaptics = settingsViewModel::setHaptics,
            onEvening = settingsViewModel::setEveningTheme,
            onAskReset = settingsViewModel::askReset
        ),
        onBack = onBack,
        onMenu = onMenu
    )

    if (state.confirmReset) {
        ConfirmResetDialog(
            onConfirm = settingsViewModel::resetDefaults,
            onDismiss = settingsViewModel::dismissReset
        )
    }
}

private class SettingsActions(
    val onUnits: (UnitSystem) -> Unit,
    val onSideHem: (Int) -> Unit,
    val onBottomHem: (Int) -> Unit,
    val onHeaderAllowance: (Int) -> Unit,
    val onSeamAllowance: (Int) -> Unit,
    val onLiningHem: (Int) -> Unit,
    val onLiningHeader: (Int) -> Unit,
    val onHaptics: (Boolean) -> Unit,
    val onEvening: (Boolean) -> Unit,
    val onAskReset: () -> Unit
)

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    actions: SettingsActions,
    onBack: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            EditorialHeader(
                title = stringResource(R.string.settings_title),
                subtitle = stringResource(R.string.settings_subtitle),
                onBack = onBack
            )
            if (state.loading) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        SectionLabel(text = stringResource(R.string.settings_section_units))
                    }
                    item {
                        UnitSelector(
                            selected = state.settings.units,
                            onSelect = actions.onUnits
                        )
                    }
                    item {
                        SectionLabel(text = stringResource(R.string.settings_section_allowances))
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_side_hem),
                            display = state.sideHem,
                            value = state.settings.sideHemMm,
                            range = GameConfig.SIDE_HEM_MIN_MM..GameConfig.SIDE_HEM_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onSideHem
                        )
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_bottom_hem),
                            display = state.bottomHem,
                            value = state.settings.bottomHemMm,
                            range = GameConfig.BOTTOM_HEM_MIN_MM..GameConfig.BOTTOM_HEM_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onBottomHem
                        )
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_header_allowance),
                            display = state.headerAllowance,
                            value = state.settings.headerAllowanceMm,
                            range = GameConfig.HEADER_ALLOWANCE_MIN_MM..GameConfig.HEADER_ALLOWANCE_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onHeaderAllowance
                        )
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_seam_allowance),
                            display = state.seamAllowance,
                            value = state.settings.seamAllowanceMm,
                            range = GameConfig.SEAM_ALLOWANCE_MIN_MM..GameConfig.SEAM_ALLOWANCE_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onSeamAllowance
                        )
                    }
                    item {
                        SectionLabel(text = stringResource(R.string.settings_section_lining))
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_lining_hem),
                            display = state.liningHem,
                            value = state.settings.liningHemMm,
                            range = GameConfig.LINING_HEM_MIN_MM..GameConfig.LINING_HEM_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onLiningHem
                        )
                    }
                    item {
                        MeasureField(
                            label = stringResource(R.string.field_lining_header),
                            display = state.liningHeader,
                            value = state.settings.liningHeaderMm,
                            range = GameConfig.LINING_HEADER_MIN_MM..GameConfig.LINING_HEADER_MAX_MM,
                            step = GameConfig.STEP_SMALL_MM,
                            onChange = actions.onLiningHeader
                        )
                    }
                    item {
                        SectionLabel(text = stringResource(R.string.settings_section_app))
                    }
                    item {
                        SwitchRow(
                            label = stringResource(R.string.switch_haptics),
                            checked = state.settings.haptics,
                            onChange = actions.onHaptics
                        )
                    }
                    item {
                        SwitchRow(
                            label = stringResource(R.string.switch_evening),
                            checked = state.settings.eveningTheme,
                            onChange = actions.onEvening
                        )
                    }
                    item {
                        TextButton(
                            onClick = actions.onAskReset,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.reset_defaults),
                                color = colors.error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.settings_hint),
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UnitSelector(
    selected: UnitSystem,
    onSelect: (UnitSystem) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        UnitOption(
            label = stringResource(R.string.unit_cm),
            selected = selected == UnitSystem.CM,
            onSelect = { onSelect(UnitSystem.CM) },
            modifier = Modifier.weight(1f)
        )
        UnitOption(
            label = stringResource(R.string.unit_inches),
            selected = selected == UnitSystem.INCHES,
            onSelect = { onSelect(UnitSystem.INCHES) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UnitOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    val description = stringResource(
        if (selected) R.string.state_selected else R.string.state_not_selected
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colors.primary else colors.surface
        ),
        border = BorderStroke(1.dp, if (selected) colors.primary else colors.hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp),
        modifier = modifier
            .heightIn(min = 52.dp)
            .selectable(selected = selected) {
                haptic.tick()
                onSelect()
            }
            .semantics { stateDescription = description }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = label,
                color = if (selected) colors.onPrimary else colors.textSecondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    val haptic = rememberHapticTrigger()
    val description = stringResource(
        if (checked) R.string.state_selected else R.string.state_not_selected
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                color = colors.textPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
            Switch(
                checked = checked,
                onCheckedChange = {
                    haptic.tick()
                    onChange(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.onPrimary,
                    checkedTrackColor = colors.primary,
                    uncheckedThumbColor = colors.textTertiary,
                    uncheckedTrackColor = colors.surfaceVariant,
                    uncheckedBorderColor = colors.hairline
                ),
                modifier = Modifier
                    .size(width = 52.dp, height = 48.dp)
                    .semantics { stateDescription = description }
            )
        }
    }
}
