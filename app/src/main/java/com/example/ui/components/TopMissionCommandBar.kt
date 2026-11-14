package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MissionScenario
import com.example.model.RadiationShieldingStatus
import com.example.model.TelemetrySeverity
import com.example.ui.theme.LocalAetherPalette
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopMissionCommandBar(
    metFormatted: String,
    shieldingStatus: RadiationShieldingStatus,
    activeScenario: MissionScenario,
    linkQualityPercent: Double,
    isWideScreen: Boolean,
    onInjectScenario: (MissionScenario) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val shieldColor = severityColor(shieldingStatus.severity)
    val shieldBg = severitySurfaceColor(shieldingStatus.severity)

    AetherGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = if (shieldingStatus.severity == TelemetrySeverity.NOMINAL) {
            palette.telemetryCyan
        } else {
            shieldColor
        },
        cornerRadius = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top Branding & Live System Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_aether_emblem_1794649171183),
                        contentDescription = "AETHER Bio-Telemetry Emblem",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, palette.telemetryCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(id = R.string.brand_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = if (isWideScreen) 16.sp else 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(palette.telemetryCyanSurface)
                                    .border(
                                        1.dp,
                                        palette.telemetryCyan.copy(alpha = 0.5f),
                                        RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.brand_tag),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.telemetryCyan,
                                )
                            }
                            Text(
                                text = "EXPEDITION 74 // FLIGHT SURGEON CONSOLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    StatusPill(
                        label = if (isWideScreen) stringResource(id = R.string.sys_online) else "SYS ONLINE",
                        color = palette.nominalGreen,
                        backgroundColor = palette.nominalGreenSurface,
                        pulsing = true,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SatelliteAlt,
                            contentDescription = "Ku-Band Telemetry Link",
                            tint = palette.telemetryCyan,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = String.format(Locale.US, "Ku-BAND %.1f%%", linkQualityPercent),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                        )
                    }
                }
            }

            // MET Clock + Radiation Shielding Readout Row
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MetClockTile(
                        metFormatted = metFormatted,
                        modifier = Modifier.weight(1f),
                    )
                    RadiationShieldingTile(
                        shieldingStatus = shieldingStatus,
                        shieldColor = shieldColor,
                        shieldBg = shieldBg,
                        modifier = Modifier.weight(1.4f),
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetClockTile(
                        metFormatted = metFormatted,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    RadiationShieldingTile(
                        shieldingStatus = shieldingStatus,
                        shieldColor = shieldColor,
                        shieldBg = shieldBg,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Interactive Scenario Injection Control Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.scenario_injection_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "// ACTIVE: ${activeScenario.codeTag}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.telemetryCyan,
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MissionScenario.entries.forEach { scenario ->
                        val isSelected = scenario == activeScenario
                        val accent = scenarioAccentColor(scenario)
                        val icon = scenarioIcon(scenario)
                        OutlinedButton(
                            onClick = { onInjectScenario(scenario) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("scenario_button_${scenario.id}"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent else palette.glassBorder,
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) accent.copy(alpha = 0.16f) else palette.tacticalNavy.copy(alpha = 0.7f),
                                contentColor = if (isSelected) accent else palette.textSecondary,
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = scenario.fullLabel,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = scenario.fullLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetClockTile(
    metFormatted: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.obsidianVoid.copy(alpha = 0.72f))
            .border(1.dp, palette.glassBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = "Mission Elapsed Time",
                tint = palette.telemetryCyan,
                modifier = Modifier.size(18.dp),
            )
            Column {
                Text(
                    text = stringResource(id = R.string.met_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                )
                Text(
                    text = "DDD:HH:MM:SS",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                )
            }
        }
        Text(
            text = metFormatted,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
            fontWeight = FontWeight.Bold,
            color = palette.nominalGreen,
            modifier = Modifier.testTag("met_clock_text"),
        )
    }
}

@Composable
private fun RadiationShieldingTile(
    shieldingStatus: RadiationShieldingStatus,
    shieldColor: Color,
    shieldBg: Color,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.obsidianVoid.copy(alpha = 0.72f))
            .border(1.dp, shieldColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Radiation Shielding Status",
                tint = shieldColor,
                modifier = Modifier.size(18.dp),
            )
            Column {
                Text(
                    text = stringResource(id = R.string.shielding_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                )
                Text(
                    text = shieldingStatus.modeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format(Locale.US, "%.1f%% ATTEN", shieldingStatus.attenuationPercent),
                style = MaterialTheme.typography.titleSmall,
                color = shieldColor,
            )
            Text(
                text = String.format(Locale.US, "%.1f MeV/cm²·s", shieldingStatus.fluxMevCm2s),
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(shieldBg)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            )
        }
    }
}

@Composable
private fun scenarioAccentColor(scenario: MissionScenario): Color {
    val palette = LocalAetherPalette.current
    return when (scenario) {
        MissionScenario.NOMINAL_ORBIT -> palette.nominalGreen
        MissionScenario.EVA_SPACEWALK_STRESS -> palette.cautionAmber
        MissionScenario.SOLAR_PARTICLE_EVENT -> palette.criticalRed
        MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> palette.telemetryCyan
    }
}

private fun scenarioIcon(scenario: MissionScenario): ImageVector = when (scenario) {
    MissionScenario.NOMINAL_ORBIT -> Icons.Filled.Public
    MissionScenario.EVA_SPACEWALK_STRESS -> Icons.Filled.DirectionsWalk
    MissionScenario.SOLAR_PARTICLE_EVENT -> Icons.Filled.Bolt
    MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> Icons.Filled.WaterDrop
}
