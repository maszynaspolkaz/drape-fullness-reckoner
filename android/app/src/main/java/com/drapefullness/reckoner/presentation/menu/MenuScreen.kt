package com.drapefullness.reckoner.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.drapefullness.reckoner.core.ui.SectionLabel
import com.drapefullness.reckoner.core.ui.StatCard

@Composable
fun MenuScreen(
    onStart: () -> Unit,
    onOpenWindow: (String) -> Unit,
    onShoppingList: () -> Unit,
    onGuide: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onStart = onStart,
        onOpenWindow = onOpenWindow,
        onShoppingList = onShoppingList,
        onGuide = onGuide,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStart: () -> Unit,
    onOpenWindow: (String) -> Unit,
    onShoppingList: () -> Unit,
    onGuide: () -> Unit,
    onSettings: () -> Unit
) {
    val colors = LocalAppColors.current
    val entry = remember { MutableTransitionState(false).apply { targetState = true } }

    AppBackground(
        overlay = listOf(
            colors.gradientTop.copy(alpha = 0.93f),
            colors.gradientMid.copy(alpha = 0.95f),
            colors.gradientBottom.copy(alpha = 0.97f)
        )
    ) {
        MenuDecor()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = stringResource(R.string.cd_home),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onGuide, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = stringResource(R.string.cd_guide),
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            AnimatedVisibility(
                visibleState = entry,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 6 }
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.menu_hero_top),
                        color = colors.textPrimary,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = stringResource(R.string.menu_hero_bottom),
                        color = colors.primary,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Spacer(
                        modifier = Modifier
                            .width(52.dp)
                            .height(2.dp)
                            .background(colors.brass)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.menu_tagline),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }
            if (state.showStats) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = stringResource(R.string.stat_windows),
                        value = state.windowCount,
                        accent = colors.brass,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_fabric),
                        value = state.fabricTotal,
                        accent = colors.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            SectionLabel(text = stringResource(R.string.menu_section_rooms))
            RoomArea(
                state = state,
                onOpenWindow = onOpenWindow,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.shopping_list),
                onClick = onShoppingList,
                brassOutline = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.menu_hint),
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.start_measuring),
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MenuDecor() {
    val colors = LocalAppColors.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.primary.copy(alpha = 0.06f), colors.primary.copy(alpha = 0f)),
                center = Offset(size.width * 0.86f, size.height * 0.12f),
                radius = size.minDimension * 0.55f
            ),
            radius = size.minDimension * 0.55f,
            center = Offset(size.width * 0.86f, size.height * 0.12f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.brass.copy(alpha = 0.05f), colors.brass.copy(alpha = 0f)),
                center = Offset(size.width * 0.10f, size.height * 0.88f),
                radius = size.minDimension * 0.45f
            ),
            radius = size.minDimension * 0.45f,
            center = Offset(size.width * 0.10f, size.height * 0.88f)
        )
        val inset = 24.dp.toPx()
        drawLine(
            color = colors.hairline.copy(alpha = 0.35f),
            start = Offset(inset, 0f),
            end = Offset(inset, size.height),
            strokeWidth = 1f
        )
        drawLine(
            color = colors.hairline.copy(alpha = 0.35f),
            start = Offset(size.width - inset, 0f),
            end = Offset(size.width - inset, size.height),
            strokeWidth = 1f
        )
    }
}

@Composable
private fun RoomArea(
    state: MenuUiState,
    onOpenWindow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    if (state.isEmpty) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            Spacer(
                modifier = Modifier
                    .size(56.dp)
                    .background(colors.hairline, RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.menu_empty_title),
                color = colors.textSecondary,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.menu_empty_hint),
                color = colors.textTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
        return
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(state.groups, key = { it.room }) { group ->
            RoomGroupCard(group = group, onOpenWindow = onOpenWindow)
        }
    }
}

@Composable
private fun RoomGroupCard(
    group: RoomGroup,
    onOpenWindow: (String) -> Unit
) {
    val colors = LocalAppColors.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = group.room,
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(
                            colors.brass.copy(alpha = 0.12f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = group.rows.size.toString(),
                        color = colors.brass,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            group.rows.forEach { row ->
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.hairline)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenWindow(row.id) }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = row.label,
                        color = colors.textPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = row.size,
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.labelSmall.merge(TabularNumber)
                        )
                        Text(
                            text = row.fabric,
                            color = colors.brass,
                            style = MaterialTheme.typography.labelMedium.merge(TabularNumber)
                        )
                    }
                }
            }
        }
    }
}
