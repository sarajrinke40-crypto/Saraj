package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CrewMemberTelemetry
import com.example.ui.theme.LocalAetherPalette
import java.util.Locale

@Composable
fun CrewRosterSidebar(
    crewRoster: List<CrewMemberTelemetry>,
    selectedCrewId: String,
    onSelectCrew: (String) -> Unit,
    onOpenDossier: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current

    AetherGlassCard(
        modifier = modifier,
        cornerRadius = 12.dp,
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
                Text(
                    text = stringResource(id = R.string.crew_roster_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.telemetryCyan,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "4/4 LOCK",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.nominalGreen,
                )
            }

            crewRoster.forEach { member ->
                CrewRosterCard(
                    member = member,
                    selected = member.id == selectedCrewId,
                    onClick = { onSelectCrew(member.id) },
                    onOpenDossier = { onOpenDossier(member.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun CrewRosterHorizontalStrip(
    crewRoster: List<CrewMemberTelemetry>,
    selectedCrewId: String,
    onSelectCrew: (String) -> Unit,
    onOpenDossier: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.crew_roster_title),
                style = MaterialTheme.typography.titleSmall,
                color = palette.telemetryCyan,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "TAP TO SELECT // OPEN DOSSIER",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(end = 4.dp),
        ) {
            items(crewRoster, key = { it.id }) { member ->
                CrewRosterCard(
                    member = member,
                    selected = member.id == selectedCrewId,
                    onClick = { onSelectCrew(member.id) },
                    onOpenDossier = { onOpenDossier(member.id) },
                    modifier = Modifier.width(264.dp),
                )
            }
        }
    }
}

@Composable
private fun CrewRosterCard(
    member: CrewMemberTelemetry,
    selected: Boolean,
    onClick: () -> Unit,
    onOpenDossier: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val statusColor = severityColor(member.overallSeverity)
    val statusBg = severitySurfaceColor(member.overallSeverity)

    val borderColor by animateColorAsState(
        targetValue = if (selected) palette.telemetryCyan else palette.glassBorder,
        animationSpec = tween(220),
        label = "crewBorder",
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) palette.glassSurfaceBright else palette.obsidianVoid.copy(alpha = 0.65f),
        animationSpec = tween(220),
        label = "crewBg",
    )

    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("crew_card_${member.id}"),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(statusBg)
                            .border(1.dp, statusColor.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (member.id == "amara") {
                                Icons.Filled.MedicalServices
                            } else {
                                Icons.Filled.Person
                            },
                            contentDescription = member.name,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = member.roleBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.telemetryCyan,
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted,
                            )
                            Text(
                                text = member.callsign,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            // Suit Assignment Badge + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val suitColor = if (member.isEvaActive) palette.cautionAmber else palette.telemetryCyan
                val suitBg = if (member.isEvaActive) palette.cautionAmberSurface else palette.telemetryCyanSurface
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(suitBg)
                        .border(1.dp, suitColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = member.suitAssignment,
                        style = MaterialTheme.typography.labelSmall,
                        color = suitColor,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = severityLabel(member.overallSeverity),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Live Mini-Vitals Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.obsidianVoid.copy(alpha = 0.7f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MiniVitalItem(
                    label = "HR",
                    value = "${member.vitals.heartRateBpm}",
                    unit = "bpm",
                    color = statusColor,
                )
                MiniVitalItem(
                    label = "SpO2",
                    value = String.format(Locale.US, "%.1f", member.vitals.spo2Percent),
                    unit = "%",
                    color = if (member.vitals.spo2Percent <= 95.0) palette.criticalRed else palette.nominalGreen,
                )
                MiniVitalItem(
                    label = "TEMP",
                    value = String.format(Locale.US, "%.1f", member.vitals.coreTempCelsius),
                    unit = "°C",
                    color = if (member.vitals.coreTempCelsius >= 38.0) palette.criticalRed else palette.textPrimary,
                )
            }

            // Crew Medical Dossier Quick Trigger
            OutlinedButton(
                onClick = onOpenDossier,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("open_dossier_${member.id}"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, palette.telemetryCyan.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = palette.telemetryCyanSurface.copy(alpha = 0.6f),
                    contentColor = palette.telemetryCyan,
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Badge,
                    contentDescription = "Open ${member.name} Dossier",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MED DOSSIER // PSYCH ${member.profile.psychologicalReadiness.compositeScore}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun MiniVitalItem(
    label: String,
    value: String,
    unit: String,
    color: Color,
) {
    val palette = LocalAetherPalette.current
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
            )
        }
    }
}
