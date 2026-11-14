package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.TelemetrySeverity
import com.example.ui.theme.LocalAetherPalette

@Composable
fun severityColor(severity: TelemetrySeverity): Color {
    val palette = LocalAetherPalette.current
    return when (severity) {
        TelemetrySeverity.NOMINAL -> palette.nominalGreen
        TelemetrySeverity.CAUTION -> palette.cautionAmber
        TelemetrySeverity.CRITICAL -> palette.criticalRed
    }
}

@Composable
fun severitySurfaceColor(severity: TelemetrySeverity): Color {
    val palette = LocalAetherPalette.current
    return when (severity) {
        TelemetrySeverity.NOMINAL -> palette.nominalGreenSurface
        TelemetrySeverity.CAUTION -> palette.cautionAmberSurface
        TelemetrySeverity.CRITICAL -> palette.criticalRedSurface
    }
}

fun severityLabel(severity: TelemetrySeverity): String = when (severity) {
    TelemetrySeverity.NOMINAL -> "NOMINAL"
    TelemetrySeverity.CAUTION -> "CAUTION"
    TelemetrySeverity.CRITICAL -> "CRITICAL"
}

@Composable
fun TelemetryGridBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val palette = LocalAetherPalette.current
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.obsidianVoid,
                        palette.tacticalNavy,
                        palette.obsidianVoid,
                    )
                )
            )
            .drawBehind {
                val minorStep = 28.dp.toPx()
                val majorStep = minorStep * 4f
                var x = 0f
                var idx = 0
                while (x <= size.width) {
                    val isMajor = idx % 4 == 0
                    drawLine(
                        color = if (isMajor) palette.gridLineAccent else palette.gridLineSubtle,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if (isMajor) 1.dp.toPx() else 0.5.dp.toPx(),
                    )
                    x += minorStep
                    idx++
                }
                var y = 0f
                var idy = 0
                while (y <= size.height) {
                    val isMajor = idy % 4 == 0
                    drawLine(
                        color = if (isMajor) palette.gridLineAccent else palette.gridLineSubtle,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = if (isMajor) 1.dp.toPx() else 0.5.dp.toPx(),
                    )
                    y += minorStep
                    idy++
                }
            },
        content = content,
    )
}

@Composable
fun AetherGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    cornerRadius: Dp = 10.dp,
    content: @Composable () -> Unit,
) {
    val palette = LocalAetherPalette.current
    val targetBorder = accentColor?.copy(alpha = 0.55f) ?: palette.glassBorder
    val borderColor by animateColorAsState(targetValue = targetBorder, animationSpec = tween(260), label = "cardBorder")
    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            palette.glassSurfaceElevated,
                            palette.glassSurface,
                        )
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    pulsing: Boolean = false,
) {
    val pulseAlpha = if (pulsing) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val a by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(750),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulseAlpha",
        )
        a
    } else {
        1f
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = pulseAlpha))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
        )
    }
}
