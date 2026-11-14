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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
fun SpaceMedicineModulesPanel(
    crew: CrewMemberTelemetry,
    isWideLayout: Boolean,
    onToggleLbnp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(id = R.string.space_med_section_title),
            style = MaterialTheme.typography.titleSmall,
            color = palette.telemetryCyan,
            fontWeight = FontWeight.Bold,
        )

        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AredComplianceCard(
                    crew = crew,
                    modifier = Modifier.weight(1f),
                )
                CircadianFluidCard(
                    crew = crew,
                    onToggleLbnp = { onToggleLbnp(crew.id) },
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            AredComplianceCard(
                crew = crew,
                modifier = Modifier.fillMaxWidth(),
            )
            CircadianFluidCard(
                crew = crew,
                onToggleLbnp = { onToggleLbnp(crew.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DosimetryTrackerCard(
            crew = crew,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AredComplianceCard(
    crew: CrewMemberTelemetry,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val ared = crew.ared
    val cardioFraction = (ared.cardioMinutes / ared.cardioTargetMinutes.toFloat()).coerceIn(0f, 1f)

    AetherGlassCard(
        modifier = modifier.testTag("ared_compliance_card"),
        cornerRadius = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = "ARED Resistive Exercise",
                        tint = palette.nominalGreen,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.ared_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = String.format(Locale.US, "BMD %.1f%%", ared.bmdPreservationPercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.nominalGreen,
                    fontWeight = FontWeight.Bold,
                )
            }

            AredProgressRow(
                label = String.format(Locale.US, "ARED SQUAT (%.2f kN)", ared.squatLoadKn),
                progress = ared.squatProgress,
                valueLabel = "${(ared.squatProgress * 100).toInt()}%",
                color = if (ared.squatProgress >= 0.85f) palette.nominalGreen else palette.cautionAmber,
            )
            AredProgressRow(
                label = "ARED ROMANIAN DEADLIFT",
                progress = ared.deadliftProgress,
                valueLabel = "${(ared.deadliftProgress * 100).toInt()}%",
                color = if (ared.deadliftProgress >= 0.80f) palette.nominalGreen else palette.cautionAmber,
            )
            AredProgressRow(
                label = "ARED HEEL RAISES (SOLEUS)",
                progress = ared.heelRaiseProgress,
                valueLabel = "${(ared.heelRaiseProgress * 100).toInt()}%",
                color = if (ared.heelRaiseProgress >= 0.85f) palette.telemetryCyan else palette.cautionAmber,
            )
            AredProgressRow(
                label = "CEVIS / T2 CARDIO LOAD",
                progress = cardioFraction,
                valueLabel = "${ared.cardioMinutes}/${ared.cardioTargetMinutes} MIN",
                color = if (cardioFraction >= 0.85f) palette.nominalGreen else palette.telemetryCyan,
            )
        }
    }
}

@Composable
private fun AredProgressRow(
    label: String,
    progress: Float,
    valueLabel: String,
    color: Color,
) {
    val palette = LocalAetherPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(palette.obsidianVoid)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun CircadianFluidCard(
    crew: CrewMemberTelemetry,
    onToggleLbnp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val cf = crew.circadianFluid
    val fluidSeverity = when {
        cf.cephaladFluidShiftMl >= 1650 -> TelemetrySeverity.CRITICAL
        cf.cephaladFluidShiftMl >= 1350 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val fluidColor = severityColor(fluidSeverity)

    AetherGlassCard(
        modifier = modifier.testTag("circadian_fluid_card"),
        accentColor = if (fluidSeverity == TelemetrySeverity.NOMINAL) null else fluidColor,
        cornerRadius = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bedtime,
                        contentDescription = "Circadian and Fluid Shift",
                        tint = palette.telemetryCyan,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.circadian_fluid_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatSubBox(
                    title = "CIRCADIAN ALIGN",
                    value = "${cf.circadianAlignmentPercent}%",
                    detail = String.format(Locale.US, "PHASE Δ %+.1fh", cf.phaseShiftHours),
                    accent = if (cf.circadianAlignmentPercent < 82) palette.cautionAmber else palette.nominalGreen,
                    modifier = Modifier.weight(1f),
                )
                StatSubBox(
                    title = "ZEITGEBER / SLEEP",
                    value = "${cf.melanopsinLux} lux",
                    detail = "SLEEP EFF ${cf.sleepEfficiencyPercent}%",
                    accent = palette.telemetryCyan,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatSubBox(
                    title = "CEPHALAD SHIFT",
                    value = "+${cf.cephaladFluidShiftMl} mL",
                    detail = "THORACIC POOL",
                    accent = fluidColor,
                    modifier = Modifier.weight(1f),
                )
                StatSubBox(
                    title = "JUGULAR AREA",
                    value = String.format(Locale.US, "%.2f cm²", cf.jugularCrossSectionCm2),
                    detail = "LBNP REC ${cf.lbnpRecommendedMmHg} mmHg",
                    accent = fluidColor,
                    modifier = Modifier.weight(1f),
                )
            }

            OutlinedButton(
                onClick = onToggleLbnp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("lbnp_toggle_button"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    1.dp,
                    if (cf.lbnpActive) palette.nominalGreen else palette.telemetryCyan.copy(alpha = 0.5f),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (cf.lbnpActive) palette.nominalGreenSurface else palette.telemetryCyanSurface,
                    contentColor = if (cf.lbnpActive) palette.nominalGreen else palette.telemetryCyan,
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = "Chibis LBNP Countermeasure",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (cf.lbnpActive) {
                        "CHIBIS LBNP ACTIVE (${cf.lbnpRecommendedMmHg} mmHg) — TAP TO STANDBY"
                    } else {
                        "ENGAGE CHIBIS LBNP (${cf.lbnpRecommendedMmHg} mmHg)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun StatSubBox(
    title: String,
    value: String,
    detail: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.obsidianVoid.copy(alpha = 0.72f))
            .border(1.dp, palette.glassBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun DosimetryTrackerCard(
    crew: CrewMemberTelemetry,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val d = crew.dosimetry
    val rateSeverity = when {
        d.liveDoseRateMsvHr >= 1.5 -> TelemetrySeverity.CRITICAL
        d.liveDoseRateMsvHr >= 0.08 -> TelemetrySeverity.CAUTION
        else -> TelemetrySeverity.NOMINAL
    }
    val rateColor = severityColor(rateSeverity)

    AetherGlassCard(
        modifier = modifier.testTag("dosimetry_tracker_card"),
        accentColor = if (rateSeverity == TelemetrySeverity.NOMINAL) palette.radiationViolet else rateColor,
        cornerRadius = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Flare,
                        contentDescription = "GCR vs SPE Cumulative Dosimetry",
                        tint = palette.radiationViolet,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.dosimetry_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                StatusPill(
                    label = String.format(Locale.US, "%.3f mSv/hr", d.liveDoseRateMsvHr),
                    color = rateColor,
                    backgroundColor = severitySurfaceColor(rateSeverity),
                    pulsing = rateSeverity != TelemetrySeverity.NOMINAL,
                )
            }

            // Stacked GCR + SPE Career Limit Bar (Canvas)
            val gcrFrac = (d.gcrCumulativeMsv / d.careerLimitMsv).toFloat().coerceIn(0f, 1f)
            val speFrac = (d.speCumulativeMsv / d.careerLimitMsv).toFloat().coerceIn(0f, (1f - gcrFrac))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.obsidianVoid)
                    .border(1.dp, palette.glassBorder, RoundedCornerShape(6.dp))
            ) {
                val gcrW = size.width * gcrFrac
                val speW = size.width * speFrac
                drawRect(
                    color = palette.telemetryCyan,
                    topLeft = Offset.Zero,
                    size = Size(gcrW, size.height),
                )
                drawRect(
                    color = palette.radiationViolet,
                    topLeft = Offset(gcrW, 0f),
                    size = Size(speW, size.height),
                )
                // 50% and 80% caution/alert markers
                val warnX = size.width * 0.8f
                drawLine(
                    color = palette.cautionAmber,
                    start = Offset(warnX, 0f),
                    end = Offset(warnX, size.height),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DosimetryLegendItem(
                    label = "GCR (GALACTIC)",
                    value = String.format(Locale.US, "%.2f mSv", d.gcrCumulativeMsv),
                    color = palette.telemetryCyan,
                )
                DosimetryLegendItem(
                    label = "SPE (SOLAR PARTICLE)",
                    value = String.format(Locale.US, "%.2f mSv", d.speCumulativeMsv),
                    color = palette.radiationViolet,
                )
                DosimetryLegendItem(
                    label = "TOTAL / REMAINING",
                    value = String.format(Locale.US, "%.1f / %.1f mSv", d.totalCumulativeMsv, d.remainingCareerMsv),
                    color = palette.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun DosimetryLegendItem(
    label: String,
    value: String,
    color: Color,
) {
    val palette = LocalAetherPalette.current
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}
