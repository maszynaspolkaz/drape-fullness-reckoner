package com.drapefullness.reckoner.presentation.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.theme.TabularNumber
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.EditorialHeader
import com.drapefullness.reckoner.core.ui.SecondaryButton
import com.drapefullness.reckoner.core.ui.SectionLabel
import com.drapefullness.reckoner.core.ui.SpecRow
import com.drapefullness.reckoner.domain.model.ShoppingKind

@Composable
fun ShoppingListScreen(
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val listViewModel: ShoppingListViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by listViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        listViewModel.refresh()
    }

    ShoppingListContent(
        state = state,
        onToggle = listViewModel::toggle,
        onClear = listViewModel::clearTicked,
        onBack = onBack,
        onMenu = onMenu
    )
}

@Composable
private fun ShoppingListContent(
    state: ShoppingListUiState,
    onToggle: (String, Boolean) -> Unit,
    onClear: () -> Unit,
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
                .padding(horizontal = 20.dp)
        ) {
            EditorialHeader(
                title = stringResource(R.string.shopping_list),
                subtitle = stringResource(R.string.shopping_subtitle),
                onBack = onBack
            )
            if (state.isEmpty) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.shopping_empty_title),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.shopping_empty_hint),
                        color = colors.textTertiary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    state.rooms.forEach { room ->
                        item(key = room.room) {
                            SectionLabel(text = room.room)
                        }
                        items(room.rows, key = { it.key }) { row ->
                            ShoppingRowItem(row = row, onToggle = onToggle)
                        }
                    }
                }
                TotalsCard(state = state)
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.clear_ticked),
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            )
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
private fun ShoppingRowItem(
    row: ShoppingRow,
    onToggle: (String, Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = row.ticked,
            onCheckedChange = { value -> onToggle(row.key, value) },
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.hairline
            ),
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(kindLabel(row.kind)),
                color = if (row.ticked) colors.textTertiary else colors.textPrimary,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (row.ticked) TextDecoration.LineThrough else null
            )
            Text(
                text = row.windowLabel,
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            text = row.amount,
            color = if (row.ticked) colors.textTertiary else colors.brass,
            style = MaterialTheme.typography.bodyLarge.merge(TabularNumber),
            textDecoration = if (row.ticked) TextDecoration.LineThrough else null
        )
    }
}

@Composable
private fun TotalsCard(state: ShoppingListUiState) {
    val colors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.brass),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            SpecRow(
                label = stringResource(R.string.total_fabric),
                value = state.fabricTotal,
                highlight = true
            )
            if (state.showLining) {
                SpecRow(
                    label = stringResource(R.string.total_lining),
                    value = state.liningTotal
                )
            }
            if (state.showHooks) {
                SpecRow(
                    label = stringResource(R.string.total_hooks),
                    value = state.hooksTotal
                )
            }
            SpecRow(
                label = stringResource(R.string.total_tape),
                value = state.tapeTotal
            )
        }
    }
}

private fun kindLabel(kind: ShoppingKind): Int = when (kind) {
    ShoppingKind.FABRIC -> R.string.kind_fabric
    ShoppingKind.LINING -> R.string.kind_lining
    ShoppingKind.HOOKS -> R.string.kind_hooks
    ShoppingKind.TAPE -> R.string.kind_tape
}
