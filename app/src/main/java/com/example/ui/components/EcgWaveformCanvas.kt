package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CrewMemberTelemetry
import com.example.model.TelemetrySeverity
import com.example.ui.theme.LocalAetherPalette
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun EcgWaveformPanel(
    crew: CrewMemberTelemetry,
    onOpenDossier: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val hr = crew.vitals.heartRateBpm
    val hrSeverity = when {
        hr >= 128 -> TelemetrySeverity.CRITICAL
        hr >= 100 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val traceColor = severityColor(hrSeverity)
    val rrIntervalMs = (60_000f / hr.coerceAtLeast(30)).toInt()
    val rhythmLabel = when {
        hr >= 128 -> "SINUS TACHY // STRESS"
        hr >= 100 -> "ELEVATED SINUS"
        else -> "LEAD II // NORMAL SINUS"
    }

    var elapsedSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    elapsedSeconds += dt
                }
                lastNanos = frameNanos
            }
        }
    }

    AetherGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = traceColor,
        cornerRadius = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Selected Crew & ECG Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Heart Rate Lead II ECG",
                            tint = traceColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = stringResource(id = R.string.ecg_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = "${crew.name}  •  ${crew.suitAssignment}  •  $rhythmLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.telemetryCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { onOpenDossier(crew.id) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("header_open_dossier_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, palette.telemetryCyan.copy(alpha = 0.55f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = palette.telemetryCyanSurface,
                        contentColor = palette.telemetryCyan,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Badge,
                        contentDescription = "View Crew Profile Dossier",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CREW PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Real-time Oscilloscope ECG Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.obsidianVoid.copy(alpha = 0.92f))
                    .border(1.dp, traceColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .testTag("ecg_waveform_canvas"),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerY = h * 0.56f
                    val amplitude = h * 0.36f

                    // Minor & Major ECG calibration grid (5mm / 1mm style)
                    val minorGrid = 12.dp.toPx()
                    var gx = 0f
                    var ix = 0
                    while (gx <= w) {
                        val isMajor = ix % 5 == 0
                        drawLine(
                            color = if (isMajor) palette.ecgGridMajor else palette.ecgGridMinor,
                            start = Offset(gx, 0f),
                            end = Offset(gx, h),
                            strokeWidth = if (isMajor) 1.dp.toPx() else 0.5.dp.toPx(),
                        )
                        gx += minorGrid
                        ix++
                    }
                    var gy = 0f
                    var iy = 0
                    while (gy <= h) {
                        val isMajor = iy % 5 == 0
                        drawLine(
                            color = if (isMajor) palette.ecgGridMajor else palette.ecgGridMinor,
                            start = Offset(0f, gy),
                            end = Offset(w, gy),
                            strokeWidth = if (isMajor) 1.dp.toPx() else 0.5.dp.toPx(),
                        )
                        gy += minorGrid
                        iy++
                    }

                    // Scrolling Lead II waveform synced to live BPM
                    val windowSeconds = 3.2f
                    val beatsPerSecond = hr / 60f
                    val sampleCount = 240
                    val path = Path()
                    val glowPath = Path()

                    for (i in 0..sampleCount) {
                        val frac = i / sampleCount.toFloat()
                        val x = frac * w
                        val timeAtX = elapsedSeconds - ((1f - frac) * windowSeconds)
                        val cyclePos = ((timeAtX * beatsPerSecond) % 1f + 1f) % 1f
                        val ecgValue = evaluateLeadIiPQRST(
                            phase = cyclePos,
                            stressFactor = ((hr - 70) / 70f).coerceIn(0f, 1f),
                            timeSec = timeAtX,
                        )
                        val y = centerY - (ecgValue * amplitude)
                        if (i == 0) {
                            path.moveTo(x, y)
                            glowPath.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            glowPath.lineTo(x, y)
                        }
                    }

                    // Phosphor bloom underlay
                    drawPath(
                        path = glowPath,
                        color = traceColor.copy(alpha = 0.24f),
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )

                    // Crisp primary Lead II trace with left-to-right phosphor fade
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                traceColor.copy(alpha = 0.25f),
                                traceColor.copy(alpha = 0.85f),
                                traceColor,
                            )
                        ),
                        style = Stroke(
                            width = 2.2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )

                    // Leading beam phosphor dot at right edge
                    val headPhase = ((elapsedSeconds * beatsPerSecond) % 1f + 1f) % 1f
                    val headVal = evaluateLeadIiPQRST(
                        phase = headPhase,
                        stressFactor = ((hr - 70) / 70f).coerceIn(0f, 1f),
                        timeSec = elapsedSeconds,
                    )
                    val headPoint = Offset(w - 2.dp.toPx(), centerY - headVal * amplitude)
                    drawCircle(
                        color = traceColor.copy(alpha = 0.35f),
                        radius = 8.dp.toPx(),
                        center = headPoint,
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = headPoint,
                    )
                }

                // Live BPM & R-R overlay badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.obsidianVoid.copy(alpha = 0.82f))
                        .border(1.dp, traceColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "$hr",
                        style = MaterialTheme.typography.displayLarge,
                        color = traceColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ecg_bpm_value"),
                    )
                    Column {
                        Text(
                            text = "BPM",
                            style = MaterialTheme.typography.labelSmall,
                            color = traceColor,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "R-R ${rrIntervalMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                        )
                    }
                }

                Text(
                    text = stringResource(id = R.string.ecg_calibration),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }
}

private fun evaluateLeadIiPQRST(
    phase: Float,
    stressFactor: Float,
    timeSec: Float,
): Float {
    // Gaussian-synthesized Lead II P-QRS-T morphology
    val pWave = 0.14f * gaussian(phase, center = 0.12f, width = 0.025f)
    val qDip = -0.16f * gaussian(phase, center = 0.215f, width = 0.009f)
    val rPeak = 1.05f * gaussian(phase, center = 0.242f, width = 0.011f)
    val sDip = -0.28f * gaussian(phase, center = 0.272f, width = 0.011f)
    val tWave = (0.28f + stressFactor * 0.05f) * gaussian(phase, center = 0.46f, width = 0.042f)
    val baselineWander = 0.018f * sin(2f * PI.toFloat() * 0.25f * timeSec)
    val emgArtifact = stressFactor * 0.022f * sin(2f * PI.toFloat() * 23f * timeSec)

    return pWave + qDip + rPeak + sDip + tWave + baselineWander + emgArtifact
}

private fun gaussian(x: Float, center: Float, width: Float): Float {
    val d = (x - center) / width
    return exp((-0.5f * d.pow(2)).toDouble()).toFloat()
}
