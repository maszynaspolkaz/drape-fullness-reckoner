package com.drapefullness.reckoner.presentation.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.LocalAppColors
import com.drapefullness.reckoner.core.ui.AppBackground
import com.drapefullness.reckoner.core.ui.EditorialHeader

private val guideTitles = listOf(
    R.string.guide_track_title,
    R.string.guide_pole_title,
    R.string.guide_drop_title,
    R.string.guide_fullness_title
)

private val guideBodies = listOf(
    R.string.guide_track_body,
    R.string.guide_pole_body,
    R.string.guide_drop_body,
    R.string.guide_fullness_body
)

@Composable
fun GuideScreen(
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val guideViewModel: GuideViewModel = viewModel(factory = AppViewModelFactory.instance)
    val state by guideViewModel.state.collectAsStateWithLifecycle()
    GuideContent(
        state = state,
        onToggle = guideViewModel::toggle,
        onBack = onBack,
        onMenu = onMenu
    )
}

@Composable
private fun GuideContent(
    state: GuideUiState,
    onToggle: (Int) -> Unit,
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
                title = stringResource(R.string.guide_title),
                subtitle = stringResource(R.string.guide_subtitle),
                onBack = onBack
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                itemsIndexed(guideTitles) { index, title ->
                    GuideCard(
                        title = stringResource(title),
                        body = stringResource(guideBodies[index]),
                        expanded = state.expanded == index,
                        onToggle = { onToggle(index) }
                    )
                }
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
private fun GuideCard(
    title: String,
    body: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalAppColors.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(220),
        label = "chevron"
    )
    Card(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.hairline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = body,
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
