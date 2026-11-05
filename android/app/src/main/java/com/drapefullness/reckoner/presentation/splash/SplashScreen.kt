package com.drapefullness.reckoner.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.theme.Brass
import com.drapefullness.reckoner.core.theme.BrassLight
import com.drapefullness.reckoner.core.theme.SplashBottom
import com.drapefullness.reckoner.core.theme.SplashMid
import com.drapefullness.reckoner.core.theme.SplashTop
import com.drapefullness.reckoner.core.ui.AppBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.instance)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "drape")
    val breath by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    AppBackground(
        overlay = listOf(
            SplashTop.copy(alpha = 0.94f),
            SplashMid.copy(alpha = 0.90f),
            SplashBottom.copy(alpha = 0.96f)
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height * 0.40f
            for (i in 0 until 3) {
                val radius = size.minDimension * (0.22f + i * 0.09f)
                val sweep = 70f + breath * 40f
                drawArc(
                    color = Brass.copy(alpha = 0.18f),
                    startAngle = 180f + (90f - sweep / 2f),
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(132.dp)
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = stringResource(R.string.splash_title_top),
                color = Color(0xFFF4F8FA),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.splash_title_bottom),
                color = BrassLight,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Spacer(
                modifier = Modifier
                    .width(44.dp)
                    .height(2.dp)
                    .background(Brass)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                color = Color(0xFFCFDAE0).copy(alpha = 0.80f),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .width(148.dp)
                    .height(3.dp)
            ) {
                LinearProgressIndicator(
                    color = Brass,
                    trackColor = Color(0xFFFFFFFF).copy(alpha = 0.14f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Transparent,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.splash_caption),
                color = Color(0xFF9FB3BD),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
