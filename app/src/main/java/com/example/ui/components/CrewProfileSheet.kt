package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CrewMemberTelemetry
import com.example.model.DossierSection
import com.example.model.EmergencyContact
import com.example.model.MedicalHistoryProfile
import com.example.model.PsychReadinessProfile
import com.example.model.TelemetrySeverity
import com.example.model.TrainingCredential
import com.example.ui.theme.LocalAetherPalette
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrewProfileSheet(
    crew: CrewMemberTelemetry,
    allCrew: List<CrewMemberTelemetry>,
    activeSection: DossierSection,
    isPrivacyMasked: Boolean,
    onSwitchCrew: (String) -> Unit,
    onSelectSection: (DossierSection) -> Unit,
    onTogglePrivacyMask: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalAetherPalette.current
    val profile = crew.profile
    val statusColor = severityColor(crew.overallSeverity)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        BackHandler(onBack = onDismiss)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.obsidianVoid.copy(alpha = 0.84f))
                .systemBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 880.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .testTag("crew_dossier_sheet"),
                shape = RoundedCornerShape(14.dp),
                color = palette.tacticalNavy,
                border = BorderStroke(1.dp, palette.telemetryCyan.copy(alpha = 0.55f)),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.obsidianVoid.copy(alpha = 0.8f))
                            .border(1.dp, palette.glassBorder)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CREW MEDICAL & READINESS DOSSIER // ${crew.callsign}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.telemetryCyan,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = crew.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = palette.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${crew.roleBadge}  •  ${profile.nationality}  •  ${profile.totalDaysInSpace} DAYS IN ORBIT  •  ${String.format(Locale.US, "%.1f", profile.evaHoursLogged)} EVA HRS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedButton(
                                    onClick = onTogglePrivacyMask,
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("privacy_mask_toggle"),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isPrivacyMasked) palette.cautionAmber else palette.glassBorderStrong,
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isPrivacyMasked) palette.cautionAmberSurface else palette.glassSurface,
                                        contentColor = if (isPrivacyMasked) palette.cautionAmber else palette.textSecondary,
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                ) {
                                    Icon(
                                        imageVector = if (isPrivacyMasked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                        contentDescription = "Toggle Privacy Mask",
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPrivacyMasked) "MASKED" else "CMO VIEW",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.glassSurface)
                                        .border(1.dp, palette.glassBorder, RoundedCornerShape(8.dp))
                                        .testTag("close_dossier_button"),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close Crew Dossier",
                                        tint = palette.textPrimary,
                                    )
                                }
                            }
                        }

                        // Quick Crew Switcher inside Dossier
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            allCrew.forEach { member ->
                                val active = member.id == crew.id
                                Box(
                                    modifier = Modifier
                                        .heightIn(min = 36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (active) palette.telemetryCyanSurface else palette.glassSurface
                                        )
                                        .border(
                                            1.dp,
                                            if (active) palette.telemetryCyan else palette.glassBorder,
                                            RoundedCornerShape(6.dp),
                                        )
                                        .clickable { onSwitchCrew(member.id) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("dossier_switch_${member.id}"),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${member.callsign} • ${member.name.substringAfter(". ").substringBefore(" (")}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (active) palette.telemetryCyan else palette.textSecondary,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                    )
                                }
                            }
                        }

                        // Dossier Section Filter Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            DossierSection.entries.forEach { sec ->
                                val selected = sec == activeSection
                                Box(
                                    modifier = Modifier
                                        .heightIn(min = 36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (selected) statusColor.copy(alpha = 0.16f) else palette.obsidianVoid.copy(alpha = 0.6f)
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) statusColor else palette.glassBorder,
                                            RoundedCornerShape(6.dp),
                                        )
                                        .clickable { onSelectSection(sec) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("dossier_tab_${sec.name.lowercase()}"),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = sec.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) statusColor else palette.textSecondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }

                    // Scrollable Dossier Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (activeSection == DossierSection.ALL || activeSection == DossierSection.TRAINING) {
                            TrainingSection(
                                division = profile.agencyDivision,
                                credentials = profile.specializedTraining,
                            )
                        }

                        if (activeSection == DossierSection.ALL || activeSection == DossierSection.MEDICAL) {
                            MedicalHistorySection(
                                medical = profile.medicalHistory,
                                masked = isPrivacyMasked,
                            )
                        }

                        if (activeSection == DossierSection.ALL || activeSection == DossierSection.PSYCH) {
                            PsychologicalReadinessSection(
                                psych = profile.psychologicalReadiness,
                            )
                        }

                        if (activeSection == DossierSection.ALL || activeSection == DossierSection.EMERGENCY) {
                            EmergencyContactsSection(
                                contacts = profile.emergencyContacts,
                                masked = isPrivacyMasked,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierSectionHeader(
    icon: ImageVector,
    title: String,
    accent: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accent,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = accent,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TrainingSection(
    division: String,
    credentials: List<TrainingCredential>,
) {
    val palette = LocalAetherPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DossierSectionHeader(
            icon = Icons.Filled.School,
            title = "SPECIALIZED TRAINING & CERTIFICATIONS // $division",
            accent = palette.telemetryCyan,
        )
        credentials.forEach { cred ->
            AetherGlassCard(cornerRadius = 10.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = cred.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.telemetryCyan,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "•  ${cred.authority}",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted,
                            )
                        }
                        Text(
                            text = cred.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        StatusPill(
                            label = cred.status,
                            color = palette.nominalGreen,
                            backgroundColor = palette.nominalGreenSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cred.loggedHoursOrCycles,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicalHistorySection(
    medical: MedicalHistoryProfile,
    masked: Boolean,
) {
    val palette = LocalAetherPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DossierSectionHeader(
            icon = Icons.Filled.LocalHospital,
            title = "CLINICAL MEDICAL HISTORY & ALLERGIES",
            accent = palette.cautionAmber,
        )

        // Anthropometric & Blood Type Strip
        AetherGlassCard(cornerRadius = 10.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DossierKeyValue("BLOOD TYPE", if (masked) "REDACTED" else medical.bloodType, palette.criticalRed)
                DossierKeyValue("HEIGHT", "${medical.heightCm} cm", palette.textPrimary)
                DossierKeyValue("MASS", String.format(Locale.US, "%.1f kg", medical.weightKg), palette.textPrimary)
                DossierKeyValue("WAIVER", if (masked) "MASKED" else "ACTIVE", palette.telemetryCyan)
            }
        }

        // Allergies
        AetherGlassCard(
            accentColor = if (medical.allergies.any { it.severity == TelemetrySeverity.CRITICAL }) {
                palette.criticalRed
            } else {
                palette.cautionAmber
            },
            cornerRadius = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "KNOWN ALLERGIES & CONTRAINDICATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Bold,
                )
                medical.allergies.forEach { allergy ->
                    val col = severityColor(allergy.severity)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.obsidianVoid.copy(alpha = 0.7f))
                            .border(1.dp, col.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (masked) "ALLERGY RECORD REDACTED" else allergy.substance,
                                style = MaterialTheme.typography.titleSmall,
                                color = col,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (masked) "Toggle CMO View to inspect clinical reaction details" else allergy.reaction,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusPill(
                            label = severityLabel(allergy.severity),
                            color = col,
                            backgroundColor = severitySurfaceColor(allergy.severity),
                        )
                    }
                }
            }
        }

        // Past Injuries & Surgeries
        AetherGlassCard(cornerRadius = 10.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "PAST INJURIES, SURGERIES & FLIGHT WAIVERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Bold,
                )
                medical.pastInjuriesAndSurgeries.forEach { rec ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.obsidianVoid.copy(alpha = 0.65f))
                            .border(1.dp, palette.glassBorder, RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (masked) "${rec.year} // CLINICAL RECORD REDACTED" else "${rec.year} // ${rec.title}",
                                style = MaterialTheme.typography.titleSmall,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = rec.clearanceStatus,
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.nominalGreen,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = if (masked) "Protected medical narrative — CMO authorization active" else rec.clinicalNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textSecondary,
                        )
                    }
                }

                Text(
                    text = "ACTIVE FORMULARY & WAIVER: ${if (masked) "REDACTED" else "${medical.activeMedications.joinToString(" • ")} | ${medical.flightWaivers}"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.telemetryCyan,
                )
            }
        }
    }
}

@Composable
private fun PsychologicalReadinessSection(
    psych: PsychReadinessProfile,
) {
    val palette = LocalAetherPalette.current
    val scoreColor = when {
        psych.compositeScore >= 90 -> palette.nominalGreen
        psych.compositeScore >= 80 -> palette.cautionAmber
        else -> palette.criticalRed
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DossierSectionHeader(
            icon = Icons.Filled.Psychology,
            title = "PSYCHOLOGICAL READINESS ASSESSMENT",
            accent = scoreColor,
        )

        AetherGlassCard(
            accentColor = scoreColor,
            cornerRadius = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Radial Readiness Score Gauge
                    Box(
                        modifier = Modifier.size(86.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = 8.dp.toPx()
                            val inset = stroke / 2f
                            val arcSize = Size(size.width - stroke, size.height - stroke)
                            drawArc(
                                color = palette.obsidianVoid,
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                            )
                            drawArc(
                                color = scoreColor,
                                startAngle = 135f,
                                sweepAngle = 270f * (psych.compositeScore / 100f),
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Round),
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${psych.compositeScore}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                                color = scoreColor,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "INDEX",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted,
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = psych.clearanceRating,
                            style = MaterialTheme.typography.titleSmall,
                            color = scoreColor,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "EVALUATED MET ${psych.lastEvaluatedMet}  •  ${psych.evaluator}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                        )
                        Text(
                            text = psych.clinicalSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textPrimary,
                        )
                    }
                }

                // 4 Domain Breakdown Bars
                psych.domains.forEach { domain ->
                    val domColor = when {
                        domain.scorePercent >= 90 -> palette.nominalGreen
                        domain.scorePercent >= 78 -> palette.telemetryCyan
                        else -> palette.cautionAmber
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${domain.label} — ${domain.statusNote}",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary,
                            )
                            Text(
                                text = "${domain.scorePercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = domColor,
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
                                    .fillMaxWidth((domain.scorePercent / 100f).coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(domColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyContactsSection(
    contacts: List<EmergencyContact>,
    masked: Boolean,
) {
    val palette = LocalAetherPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DossierSectionHeader(
            icon = Icons.Filled.ContactPhone,
            title = "EMERGENCY CONTACT & NEXT-OF-KIN DIRECTORY",
            accent = palette.radiationViolet,
        )

        contacts.forEach { contact ->
            AetherGlassCard(
                accentColor = palette.radiationViolet,
                cornerRadius = 10.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "${contact.priorityLabel}  •  ${contact.relationship.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.radiationViolet,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (masked) "NOK IDENTITY MASKED" else contact.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (masked) "LOCATION REDACTED" else contact.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (masked) "•••-•••-••••" else contact.primaryChannel,
                            style = MaterialTheme.typography.titleSmall,
                            color = palette.nominalGreen,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (masked) "ENCRYPTED" else "ALT: ${contact.secondaryChannel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierKeyValue(
    label: String,
    value: String,
    color: Color,
) {
    val palette = LocalAetherPalette.current
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}
