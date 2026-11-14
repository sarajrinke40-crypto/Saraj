package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TelemetrySimulator
import com.example.model.DashboardSection
import com.example.model.DossierSection
import com.example.model.MissionCommandUiState
import com.example.model.MissionScenario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MissionCommandViewModel : ViewModel() {

    private val stabilizedCrewIds = mutableSetOf<String>()
    private val lbnpActiveIds = mutableSetOf<String>()
    private var stormShelterAcknowledged = false

    private val _uiState = MutableStateFlow(
        MissionCommandUiState(
            metSeconds = TelemetrySimulator.INITIAL_MET_SECONDS,
            metFormatted = TelemetrySimulator.formatMet(TelemetrySimulator.INITIAL_MET_SECONDS),
            activeScenario = MissionScenario.NOMINAL_ORBIT,
            shieldingStatus = TelemetrySimulator.computeShielding(
                scenario = MissionScenario.NOMINAL_ORBIT,
                tick = 0L,
                shelterAcknowledged = false,
            ),
            crewRoster = TelemetrySimulator.initialRoster(),
            selectedCrewId = "marcus",
            triageLog = TelemetrySimulator.buildScenarioTriageAlerts(
                scenario = MissionScenario.NOMINAL_ORBIT,
                metFormatted = TelemetrySimulator.formatMet(TelemetrySimulator.INITIAL_MET_SECONDS),
            ),
        )
    )
    val uiState: StateFlow<MissionCommandUiState> = _uiState.asStateFlow()

    init {
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { current ->
                    val nextMet = current.metSeconds + 1L
                    val nextTick = current.telemetryTickCount + 1L
                    val updatedRoster = TelemetrySimulator.computeCrewRoster(
                        scenario = current.activeScenario,
                        tick = nextTick,
                        stabilizedCrewIds = stabilizedCrewIds,
                        lbnpActiveIds = lbnpActiveIds,
                        previousRoster = current.crewRoster,
                    )
                    val updatedShielding = TelemetrySimulator.computeShielding(
                        scenario = current.activeScenario,
                        tick = nextTick,
                        shelterAcknowledged = stormShelterAcknowledged,
                    )
                    current.copy(
                        metSeconds = nextMet,
                        metFormatted = TelemetrySimulator.formatMet(nextMet),
                        shieldingStatus = updatedShielding,
                        crewRoster = updatedRoster,
                        linkQualityPercent = 99.3 + ((nextTick % 5) * 0.1),
                        telemetryTickCount = nextTick,
                    )
                }
            }
        }
    }

    fun selectCrew(crewId: String) {
        _uiState.update { it.copy(selectedCrewId = crewId) }
    }

    fun openDossier(crewId: String) {
        _uiState.update {
            it.copy(
                selectedCrewId = crewId,
                activeDossierCrewId = crewId,
                activeDossierSection = DossierSection.ALL,
            )
        }
    }

    fun closeDossier() {
        _uiState.update { it.copy(activeDossierCrewId = null) }
    }

    fun selectDossierSection(section: DossierSection) {
        _uiState.update { it.copy(activeDossierSection = section) }
    }

    fun togglePrivacyMask() {
        _uiState.update { it.copy(isPrivacyMasked = !it.isPrivacyMasked) }
    }

    fun selectSection(section: DashboardSection) {
        _uiState.update { it.copy(selectedSection = section) }
    }

    fun injectScenario(scenario: MissionScenario) {
        stabilizedCrewIds.clear()
        lbnpActiveIds.clear()
        stormShelterAcknowledged = false

        _uiState.update { current ->
            val nextTick = current.telemetryTickCount + 1L
            val newAlerts = TelemetrySimulator.buildScenarioTriageAlerts(
                scenario = scenario,
                metFormatted = current.metFormatted,
            )
            val retainedHistory = current.triageLog
                .filter { it.acknowledged }
                .take(4)

            val updatedRoster = TelemetrySimulator.computeCrewRoster(
                scenario = scenario,
                tick = nextTick,
                stabilizedCrewIds = stabilizedCrewIds,
                lbnpActiveIds = lbnpActiveIds,
                previousRoster = current.crewRoster,
            )
            val updatedShielding = TelemetrySimulator.computeShielding(
                scenario = scenario,
                tick = nextTick,
                shelterAcknowledged = false,
            )

            val autoFocusCrew = when (scenario) {
                MissionScenario.EVA_SPACEWALK_STRESS -> "marcus"
                MissionScenario.SOLAR_PARTICLE_EVENT -> "marcus"
                MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> "kenji"
                MissionScenario.NOMINAL_ORBIT -> current.selectedCrewId
            }

            current.copy(
                activeScenario = scenario,
                shieldingStatus = updatedShielding,
                crewRoster = updatedRoster,
                selectedCrewId = autoFocusCrew,
                triageLog = newAlerts + retainedHistory,
                telemetryTickCount = nextTick,
            )
        }
    }

    fun acknowledgeAlert(alertId: String) {
        _uiState.update { current ->
            val targetAlert = current.triageLog.firstOrNull { it.id == alertId }
            if (targetAlert != null && !targetAlert.acknowledged) {
                stabilizedCrewIds.add(targetAlert.crewId)
                if (current.activeScenario == MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT) {
                    lbnpActiveIds.add(targetAlert.crewId)
                }
                if (current.activeScenario == MissionScenario.SOLAR_PARTICLE_EVENT) {
                    stormShelterAcknowledged = true
                }
            }

            val updatedLog = current.triageLog.map { alert ->
                if (alert.id == alertId && !alert.acknowledged) {
                    alert.copy(
                        acknowledged = true,
                        acknowledgedAtMet = current.metFormatted,
                    )
                } else {
                    alert
                }
            }

            val updatedRoster = TelemetrySimulator.computeCrewRoster(
                scenario = current.activeScenario,
                tick = current.telemetryTickCount,
                stabilizedCrewIds = stabilizedCrewIds,
                lbnpActiveIds = lbnpActiveIds,
                previousRoster = current.crewRoster,
            )
            val updatedShielding = TelemetrySimulator.computeShielding(
                scenario = current.activeScenario,
                tick = current.telemetryTickCount,
                shelterAcknowledged = stormShelterAcknowledged,
            )

            current.copy(
                triageLog = updatedLog,
                crewRoster = updatedRoster,
                shieldingStatus = updatedShielding,
            )
        }
    }

    fun toggleLbnpCountermeasure(crewId: String) {
        if (crewId in lbnpActiveIds) {
            lbnpActiveIds.remove(crewId)
        } else {
            lbnpActiveIds.add(crewId)
        }
        _uiState.update { current ->
            val updatedRoster = TelemetrySimulator.computeCrewRoster(
                scenario = current.activeScenario,
                tick = current.telemetryTickCount,
                stabilizedCrewIds = stabilizedCrewIds,
                lbnpActiveIds = lbnpActiveIds,
                previousRoster = current.crewRoster,
            )
            current.copy(crewRoster = updatedRoster)
        }
    }
}
