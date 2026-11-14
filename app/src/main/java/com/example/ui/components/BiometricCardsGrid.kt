package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CrewMemberTelemetry
import com.example.model.TelemetrySeverity
import com.example.ui.theme.LocalAetherPalette
import java.util.Locale

@Composable
fun BiometricCardsGrid(
    crew: CrewMemberTelemetry,
    columns: Int,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val v = crew.vitals

    val hrvSeverity = when {
        v.hrvRmssdMs <= 25 -> TelemetrySeverity.CRITICAL
        v.hrvRmssdMs <= 40 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val spo2Severity = when {
        v.spo2Percent <= 95.0 -> TelemetrySeverity.CRITICAL
        v.spo2Percent <= 96.8 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val tempSeverity = when {
        v.coreTempCelsius >= 38.0 -> TelemetrySeverity.CRITICAL
        v.coreTempCelsius >= 37.4 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val bpSeverity = when {
        v.systolicMmHg >= 150 || v.systolicMmHg <= 94 -> TelemetrySeverity.CRITICAL
        v.systolicMmHg >= 136 || v.systolicMmHg <= 102 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val icpSeverity = when {
        v.icpMmHg >= 18.5 -> TelemetrySeverity.CRITICAL
        v.icpMmHg >= 15.0 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val respSeverity = when {
        v.ppCo2MmHg >= 4.2 || v.scrubberLoadPercent >= 85 -> TelemetrySeverity.CRITICAL
        v.ppCo2MmHg >= 3.0 || v.scrubberLoadPercent >= 65 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }

    val cards = listOf(
        MetricCardData(
            tag = "metric_hrv",
            title = "HEART RATE VARIABILITY",
            primaryValue = "${v.hrvRmssdMs} ms",
            subtitle = "RMSSD  •  SDNN ${v.hrvSdnnMs} ms",
            footnote = if (v.hrvRmssdMs <= 30) "SYMPATHETIC STRAIN" else "AUTONOMIC RECOVERY OK",
            progress = (v.hrvRmssdMs / 90f).coerceIn(0.08f, 1f),
            severity = hrvSeverity,
            icon = Icons.Filled.Waves,
        ),
        MetricCardData(
            tag = "metric_spo2",
            title = "SpO2 SATURATION",
            primaryValue = String.format(Locale.US, "%.1f%%", v.spo2Percent),
            subtitle = String.format(Locale.US, "PERFUSION INDEX %.1f%%", v.perfusionIndex),
            footnote = if (v.spo2Percent <= 95.0) "HYPOXEMIC DRIFT ALERT" else "O2 TRANSPORT NOMINAL",
            progress = ((v.spo2Percent - 85.0) / 15.0).toFloat().coerceIn(0.05f, 1f),
            severity = spo2Severity,
            icon = Icons.Filled.Bloodtype,
        ),
        MetricCardData(
            tag = "metric_temp",
            title = "CORE TEMP & THERMAL Δ",
            primaryValue = String.format(Locale.US, "%.1f °C", v.coreTempCelsius),
            subtitle = String.format(Locale.US, "ΔT %+.2f °C  •  LCG %d%%", v.thermalDeltaCelsius, v.lcgFlowPercent),
            footnote = if (v.coreTempCelsius >= 37.8) "HEAT LOAD ELEVATED" else "THERMOREGULATION STABLE",
            progress = ((v.coreTempCelsius - 35.5) / 4.0).toFloat().coerceIn(0.1f, 1f),
            severity = tempSeverity,
            icon = Icons.Filled.Thermostat,
        ),
        MetricCardData(
            tag = "metric_bp",
            title = "ARTERIAL BLOOD PRESSURE",
            primaryValue = "${v.systolicMmHg}/${v.diastolicMmHg}",
            subtitle = "mmHg  •  MAP ${v.mapMmHg} mmHg",
            footnote = if (v.systolicMmHg <= 102) "ORTHOSTATIC LABILITY" else "PERFUSION PRESSURE OK",
            progress = ((v.mapMmHg - 50) / 75f).coerceIn(0.1f, 1f),
            severity = bpSeverity,
            icon = Icons.Filled.MonitorHeart,
        ),
        MetricCardData(
            tag = "metric_icp",
            title = "ICP // SANS MONITOR",
            primaryValue = String.format(Locale.US, "%.1f mmHg", v.icpMmHg),
            subtitle = String.format(Locale.US, "ONSD %.1f mm  •  SANS G%d", v.onsdMm, v.sansGrade),
            footnote = when (v.sansGrade) {
                2 -> "OPTIC SHEATH DISTENSION"
                1 -> "CEPHALAD PRESSURE WATCH"
                else -> "NEURO-OCULAR BASELINE"
            },
            progress = ((v.icpMmHg - 6.0) / 20.0).toFloat().coerceIn(0.1f, 1f),
            severity = icpSeverity,
            icon = Icons.Filled.RemoveRedEye,
        ),
        MetricCardData(
            tag = "metric_resp",
            title = "RESP & ppCO2 SCRUBBER",
            primaryValue = "${v.respRateBpm} br/m",
            subtitle = String.format(Locale.US, "ppCO2 %.1f mmHg  •  SCRUB %d%%", v.ppCo2MmHg, v.scrubberLoadPercent),
            footnote = if (v.scrubberLoadPercent >= 80) "LiOH / CDRA HIGH LOAD" else "GAS EXCHANGE NOMINAL",
            progress = (v.scrubberLoadPercent / 100f).coerceIn(0.08f, 1f),
            severity = respSeverity,
            icon = Icons.Filled.Air,
        ),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(id = R.string.biometrics_section_title),
            style = MaterialTheme.typography.titleSmall,
            color = palette.telemetryCyan,
            fontWeight = FontWeight.Bold,
        )

        val rows = cards.chunked(columns.coerceAtLeast(1))
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { item ->
                    BiometricCard(
                        data = item,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class MetricCardData(
    val tag: String,
    val title: String,
    val primaryValue: String,
    val subtitle: String,
    val footnote: String,
    val progress: Float,
    val severity: TelemetrySeverity,
    val icon: ImageVector,
)

@Composable
private fun BiometricCard(
    data: MetricCardData,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val accent = severityColor(data.severity)
    val accentBg = severitySurfaceColor(data.severity)

    AetherGlassCard(
        modifier = modifier.testTag(data.tag),
        accentColor = if (data.severity == TelemetrySeverity.NOMINAL) null else accent,
        cornerRadius = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.title,
                        tint = accent,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentBg)
                        .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = severityLabel(data.severity),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Text(
                text = data.primaryValue,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                color = accent,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textPrimary,
            )

            // Progress / threshold bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.obsidianVoid)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(data.progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(accent)
                )
            }

            Text(
                text = data.footnote,
                style = MaterialTheme.typography.labelSmall,
                color = if (data.severity == TelemetrySeverity.NOMINAL) palette.textMuted else accent,
            )
        }
    }
}
