package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DashboardSection
import com.example.model.DossierSection
import com.example.model.MissionCommandUiState
import com.example.model.MissionScenario
import com.example.ui.components.BiometricCardsGrid
import com.example.ui.components.CrewProfileSheet
import com.example.ui.components.CrewRosterHorizontalStrip
import com.example.ui.components.CrewRosterSidebar
import com.example.ui.components.EcgWaveformPanel
import com.example.ui.components.FlightSurgeonTriageLog
import com.example.ui.components.SpaceMedicineModulesPanel
import com.example.ui.components.TelemetryGridBackground
import com.example.ui.components.TopMissionCommandBar
import com.example.ui.theme.LocalAetherPalette

@Composable
fun MissionCommandDashboard(
    state: MissionCommandUiState,
    onInjectScenario: (MissionScenario) -> Unit,
    onSelectCrew: (String) -> Unit,
    onOpenDossier: (String) -> Unit,
    onCloseDossier: () -> Unit,
    onSelectDossierSection: (DossierSection) -> Unit,
    onTogglePrivacyMask: () -> Unit,
    onSelectSection: (DashboardSection) -> Unit,
    onAcknowledgeAlert: (String) -> Unit,
    onToggleLbnp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAetherPalette.current
    val selectedCrew = state.selectedCrew

    TelemetryGridBackground(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            val isWideScreen = maxWidth >= 880.dp
            val isMediumScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                // Desktop / Ruggedized Tablet Command Center Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TopMissionCommandBar(
                        metFormatted = state.metFormatted,
                        shieldingStatus = state.shieldingStatus,
                        activeScenario = state.activeScenario,
                        linkQualityPercent = state.linkQualityPercent,
                        isWideScreen = true,
                        onInjectScenario = onInjectScenario,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CrewRosterSidebar(
                            crewRoster = state.crewRoster,
                            selectedCrewId = state.selectedCrewId,
                            onSelectCrew = onSelectCrew,
                            onOpenDossier = onOpenDossier,
                            modifier = Modifier
                                .width(295.dp)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            EcgWaveformPanel(
                                crew = selectedCrew,
                                onOpenDossier = onOpenDossier,
                            )

                            BiometricCardsGrid(
                                crew = selectedCrew,
                                columns = 3,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SpaceMedicineModulesPanel(
                                    crew = selectedCrew,
                                    isWideLayout = true,
                                    onToggleLbnp = onToggleLbnp,
                                    modifier = Modifier.weight(1.35f),
                                )
                                FlightSurgeonTriageLog(
                                    triageLog = state.triageLog,
                                    onAcknowledgeAlert = onAcknowledgeAlert,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                // Handheld Mobile / Compact Vertical Tablet Layout
                val biometricCols = if (isMediumScreen) 3 else 2
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TopMissionCommandBar(
                        metFormatted = state.metFormatted,
                        shieldingStatus = state.shieldingStatus,
                        activeScenario = state.activeScenario,
                        linkQualityPercent = state.linkQualityPercent,
                        isWideScreen = false,
                        onInjectScenario = onInjectScenario,
                    )

                    CrewRosterHorizontalStrip(
                        crewRoster = state.crewRoster,
                        selectedCrewId = state.selectedCrewId,
                        onSelectCrew = onSelectCrew,
                        onOpenDossier = onOpenDossier,
                    )

                    // Subsystem View Switcher for quick mobile scanning
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(DashboardSection.entries) { section ->
                            val active = section == state.selectedSection
                            OutlinedButton(
                                onClick = { onSelectSection(section) },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("section_tab_${section.name.lowercase()}"),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    width = if (active) 1.5.dp else 1.dp,
                                    color = if (active) palette.telemetryCyan else palette.glassBorder,
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (active) palette.telemetryCyanSurface else palette.tacticalNavy.copy(alpha = 0.7f),
                                    contentColor = if (active) palette.telemetryCyan else palette.textSecondary,
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            ) {
                                Text(
                                    text = section.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                )
                            }
                        }
                    }

                    if (state.selectedSection == DashboardSection.ALL || state.selectedSection == DashboardSection.BIOMETRICS) {
                        EcgWaveformPanel(
                            crew = selectedCrew,
                            onOpenDossier = onOpenDossier,
                        )
                        BiometricCardsGrid(
                            crew = selectedCrew,
                            columns = biometricCols,
                        )
                    }

                    if (state.selectedSection == DashboardSection.ALL || state.selectedSection == DashboardSection.SPACE_MED) {
                        SpaceMedicineModulesPanel(
                            crew = selectedCrew,
                            isWideLayout = isMediumScreen,
                            onToggleLbnp = onToggleLbnp,
                        )
                    }

                    if (state.selectedSection == DashboardSection.ALL || state.selectedSection == DashboardSection.TRIAGE) {
                        FlightSurgeonTriageLog(
                            triageLog = state.triageLog,
                            onAcknowledgeAlert = onAcknowledgeAlert,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Crew Medical & Readiness Dossier Modal Sheet
        val dossierCrew = state.activeDossierCrew
        if (dossierCrew != null) {
            CrewProfileSheet(
                crew = dossierCrew,
                allCrew = state.crewRoster,
                activeSection = state.activeDossierSection,
                isPrivacyMasked = state.isPrivacyMasked,
                onSwitchCrew = onOpenDossier,
                onSelectSection = onSelectDossierSection,
                onTogglePrivacyMask = onTogglePrivacyMask,
                onDismiss = onCloseDossier,
            )
        }
    }
}
