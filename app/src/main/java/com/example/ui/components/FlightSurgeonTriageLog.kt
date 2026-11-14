package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.TriageAlert
import com.example.ui.theme.LocalAetherPalette

@Composable
fun FlightSurgeonTriageLog(
    triageLog: List<TriageAlert>,
    onAcknowledgeAlert: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val activeCount = triageLog.count { !it.acknowledged }

    Column(
        modifier = modifier.fillMaxWidth(),
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
                    imageVector = Icons.Filled.MedicalInformation,
                    contentDescription = "Flight Surgeon Triage Log",
                    tint = palette.telemetryCyan,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(id = R.string.triage_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.telemetryCyan,
                    fontWeight = FontWeight.Bold,
                )
            }

            StatusPill(
                label = if (activeCount > 0) "$activeCount PENDING" else "ALL CLEAR",
                color = if (activeCount > 0) palette.cautionAmber else palette.nominalGreen,
                backgroundColor = if (activeCount > 0) palette.cautionAmberSurface else palette.nominalGreenSurface,
                pulsing = activeCount > 0,
            )
        }

        triageLog.forEach { alert ->
            TriageAlertCard(
                alert = alert,
                onAcknowledge = { onAcknowledgeAlert(alert.id) },
            )
        }
    }
}

@Composable
private fun TriageAlertCard(
    alert: TriageAlert,
    onAcknowledge: () -> Unit,
) {
    val palette = LocalAetherPalette.current
    val color = if (alert.acknowledged) palette.nominalGreen else severityColor(alert.severity)
    val surface = if (alert.acknowledged) palette.nominalGreenSurface else severitySurfaceColor(alert.severity)

    AetherGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("triage_card_${alert.id}"),
        accentColor = color,
        cornerRadius = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatusPill(
                        label = if (alert.acknowledged) "MITIGATED" else severityLabel(alert.severity),
                        color = color,
                        backgroundColor = surface,
                        pulsing = !alert.acknowledged,
                    )
                    Text(
                        text = alert.crewName,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = "MET ${alert.metTimestamp}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.obsidianVoid.copy(alpha = 0.72f))
                    .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "TRIGGER: ${alert.triggerMetric}",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = alert.clinicalSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.tacticalNavy.copy(alpha = 0.85f))
                    .border(1.dp, palette.glassBorderStrong, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "RECOMMENDED COUNTERMEASURE",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.telemetryCyan,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = alert.countermeasureTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (alert.acknowledged) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Countermeasure Acknowledged",
                            tint = palette.nominalGreen,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "ACK @ MET ${alert.acknowledgedAtMet ?: alert.metTimestamp} // SARAJTEAM40 CMO",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.nominalGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Action Required",
                            tint = color,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = "AWAITING CMO AUTHORIZATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAcknowledge,
                    enabled = !alert.acknowledged,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("ack_alert_${alert.id}"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color.copy(alpha = 0.2f),
                        contentColor = color,
                        disabledContainerColor = palette.nominalGreenSurface,
                        disabledContentColor = palette.nominalGreen,
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = if (alert.acknowledged) {
                            stringResource(id = R.string.countermeasure_active)
                        } else {
                            stringResource(id = R.string.ack_execute)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
