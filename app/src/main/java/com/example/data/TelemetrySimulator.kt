package com.example.data

import com.example.model.AredCompliance
import com.example.model.BiometricTelemetry
import com.example.model.CircadianFluidMetrics
import com.example.model.CrewMemberTelemetry
import com.example.model.DosimetryMetrics
import com.example.model.MissionScenario
import com.example.model.RadiationShieldingStatus
import com.example.model.TelemetrySeverity
import com.example.model.TriageAlert
import kotlin.math.roundToInt
import kotlin.math.sin

object TelemetrySimulator {

    const val INITIAL_MET_SECONDS: Long =
        (142L * 86400L) + (8L * 3600L) + (41L * 60L) + 12L

    fun formatMet(totalSeconds: Long): String {
        val days = totalSeconds / 86400L
        val hours = (totalSeconds % 86400L) / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        return "%03d:%02d:%02d:%02d".format(days, hours, minutes, seconds)
    }

    fun initialRoster(): List<CrewMemberTelemetry> {
        return computeCrewRoster(
            scenario = MissionScenario.NOMINAL_ORBIT,
            tick = 0L,
            stabilizedCrewIds = emptySet(),
            lbnpActiveIds = emptySet(),
            previousRoster = emptyList(),
        )
    }

    fun computeShielding(
        scenario: MissionScenario,
        tick: Long,
        shelterAcknowledged: Boolean,
    ): RadiationShieldingStatus {
        val wave = sin(tick * 0.35)
        return when (scenario) {
            MissionScenario.NOMINAL_ORBIT -> RadiationShieldingStatus(
                modeLabel = "HULL + POLY-H2O // NOMINAL",
                attenuationPercent = 98.4 + wave * 0.2,
                fluxMevCm2s = 12.4 + wave * 1.1,
                stormShelterActive = false,
                severity = TelemetrySeverity.NOMINAL,
            )
            MissionScenario.EVA_SPACEWALK_STRESS -> RadiationShieldingStatus(
                modeLabel = "xEMU LAYERED // EVA EXPOSED",
                attenuationPercent = 91.2 + wave * 0.3,
                fluxMevCm2s = 28.6 + wave * 2.4,
                stormShelterActive = false,
                severity = TelemetrySeverity.CAUTION,
            )
            MissionScenario.SOLAR_PARTICLE_EVENT -> RadiationShieldingStatus(
                modeLabel = if (shelterAcknowledged) {
                    "STORM SHELTER // NODE-2 ACTIVE"
                } else {
                    "SPE FLUX SURGE // SHELTER REQ"
                },
                attenuationPercent = if (shelterAcknowledged) 95.8 + wave * 0.2 else 82.6 + wave * 0.6,
                fluxMevCm2s = (if (shelterAcknowledged) 148.0 else 412.5) + wave * 18.0,
                stormShelterActive = shelterAcknowledged,
                severity = if (shelterAcknowledged) TelemetrySeverity.CAUTION else TelemetrySeverity.CRITICAL,
            )
            MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> RadiationShieldingStatus(
                modeLabel = "HULL + POLY-H2O // NOMINAL",
                attenuationPercent = 98.1 + wave * 0.2,
                fluxMevCm2s = 14.1 + wave * 1.2,
                stormShelterActive = false,
                severity = TelemetrySeverity.NOMINAL,
            )
        }
    }

    fun computeCrewRoster(
        scenario: MissionScenario,
        tick: Long,
        stabilizedCrewIds: Set<String>,
        lbnpActiveIds: Set<String>,
        previousRoster: List<CrewMemberTelemetry>,
    ): List<CrewMemberTelemetry> {
        val prevMap = previousRoster.associateBy { it.id }

        val configs = listOf(
            CrewBaseConfig(
                id = "elena",
                callsign = "CDR-01",
                name = "CDR. Elena Rostova",
                roleBadge = "COMMANDER",
                suitAssignment = "IVA // SOKOL-KV3",
                isEvaActive = false,
                baseHr = 68,
                baseHrv = 62,
                baseSpo2 = 98.9,
                baseTemp = 36.8,
                baseSys = 118,
                baseDia = 74,
                baseIcp = 12.4,
                baseResp = 14,
                basePpCo2 = 2.1,
                baseGcr = 142.6,
                baseSpe = 18.4,
                squatProgress = 0.92f,
                squatKn = 1.48f,
                deadliftProgress = 0.88f,
                heelProgress = 0.95f,
                cardioMin = 38,
                cardioTarget = 45,
                bmdPercent = 98.6f,
                phaseShift = 0.3,
                alignment = 94,
                lux = 420,
                sleepEff = 89,
                fluidShiftMl = 1120,
            ),
            CrewBaseConfig(
                id = "marcus",
                callsign = "EVA-02",
                name = "FLT. Marcus Vance (EVA)",
                roleBadge = "EVA LEAD",
                suitAssignment = "EVA // xEMU-04",
                isEvaActive = true,
                baseHr = 76,
                baseHrv = 54,
                baseSpo2 = 98.4,
                baseTemp = 36.9,
                baseSys = 122,
                baseDia = 78,
                baseIcp = 13.1,
                baseResp = 16,
                basePpCo2 = 2.4,
                baseGcr = 158.2,
                baseSpe = 29.8,
                squatProgress = 0.84f,
                squatKn = 1.62f,
                deadliftProgress = 0.81f,
                heelProgress = 0.78f,
                cardioMin = 42,
                cardioTarget = 45,
                bmdPercent = 99.1f,
                phaseShift = -0.6,
                alignment = 88,
                lux = 580,
                sleepEff = 84,
                fluidShiftMl = 1190,
            ),
            CrewBaseConfig(
                id = "kenji",
                callsign = "SPC-03",
                name = "SPC. Kenji Takahashi",
                roleBadge = "PAYLOAD SPEC",
                suitAssignment = "IVA // FLIGHT-SUIT",
                isEvaActive = false,
                baseHr = 64,
                baseHrv = 68,
                baseSpo2 = 99.1,
                baseTemp = 36.7,
                baseSys = 114,
                baseDia = 72,
                baseIcp = 13.8,
                baseResp = 13,
                basePpCo2 = 1.9,
                baseGcr = 136.4,
                baseSpe = 16.2,
                squatProgress = 0.76f,
                squatKn = 1.32f,
                deadliftProgress = 0.72f,
                heelProgress = 0.85f,
                cardioMin = 31,
                cardioTarget = 45,
                bmdPercent = 97.4f,
                phaseShift = -1.2,
                alignment = 81,
                lux = 310,
                sleepEff = 79,
                fluidShiftMl = 1260,
            ),
            CrewBaseConfig(
                id = "amara",
                callsign = "CMO-04",
                name = "DR. Amara Okafor",
                roleBadge = "FLIGHT SURGEON",
                suitAssignment = "IVA // MED-BAY",
                isEvaActive = false,
                baseHr = 66,
                baseHrv = 65,
                baseSpo2 = 99.2,
                baseTemp = 36.8,
                baseSys = 116,
                baseDia = 73,
                baseIcp = 11.9,
                baseResp = 14,
                basePpCo2 = 2.0,
                baseGcr = 128.9,
                baseSpe = 15.1,
                squatProgress = 0.96f,
                squatKn = 1.40f,
                deadliftProgress = 0.94f,
                heelProgress = 0.92f,
                cardioMin = 45,
                cardioTarget = 45,
                bmdPercent = 99.3f,
                phaseShift = 0.1,
                alignment = 96,
                lux = 460,
                sleepEff = 91,
                fluidShiftMl = 1040,
            ),
        )

        return configs.mapIndexed { idx, cfg ->
            val phase = tick * 0.45 + idx * 1.3
            val wave1 = sin(phase)
            val wave2 = sin(phase * 0.6 + 0.8)
            val stabilized = cfg.id in stabilizedCrewIds
            val lbnpActive = cfg.id in lbnpActiveIds || stabilized
            val mitigationFactor = if (stabilized) 0.42 else 1.0

            var hrDelta = 0
            var hrvDelta = 0
            var spo2Delta = 0.0
            var tempDelta = 0.0
            var sysDelta = 0
            var diaDelta = 0
            var icpDelta = 0.0
            var respDelta = 0
            var ppCo2Delta = 0.0
            var scrubberDelta = 0
            var lcgFlow = if (cfg.isEvaActive) 55 else 30
            var fluidExtraMl = 0
            var doseRate = 0.042

            when (scenario) {
                MissionScenario.NOMINAL_ORBIT -> {
                    hrDelta = (wave1 * 3).roundToInt()
                    hrvDelta = (wave2 * 4).roundToInt()
                    spo2Delta = wave1 * 0.3
                    tempDelta = wave2 * 0.08
                    sysDelta = (wave1 * 2).roundToInt()
                    diaDelta = (wave2 * 2).roundToInt()
                    icpDelta = wave1 * 0.3
                    respDelta = (wave2 * 1).roundToInt()
                    ppCo2Delta = wave1 * 0.12
                    doseRate = 0.042 + idx * 0.003
                }

                MissionScenario.EVA_SPACEWALK_STRESS -> {
                    if (cfg.isEvaActive) {
                        hrDelta = (62 * mitigationFactor + wave1 * 5).roundToInt()
                        hrvDelta = (-35 * mitigationFactor + wave2 * 3).roundToInt()
                        spo2Delta = -3.6 * mitigationFactor + wave1 * 0.3
                        tempDelta = 1.45 * mitigationFactor + wave2 * 0.12
                        sysDelta = (30 * mitigationFactor + wave1 * 4).roundToInt()
                        diaDelta = (14 * mitigationFactor + wave2 * 3).roundToInt()
                        icpDelta = 2.6 * mitigationFactor + wave1 * 0.3
                        respDelta = (12 * mitigationFactor + wave2 * 2).roundToInt()
                        ppCo2Delta = 2.4 * mitigationFactor + wave1 * 0.18
                        scrubberDelta = (64 * mitigationFactor).roundToInt()
                        lcgFlow = if (stabilized) 95 else 68
                        doseRate = 0.095
                    } else if (cfg.id == "elena") {
                        hrDelta = (24 * mitigationFactor + wave1 * 3).roundToInt()
                        hrvDelta = (-16 * mitigationFactor + wave2 * 3).roundToInt()
                        tempDelta = 0.35 * mitigationFactor
                        sysDelta = (12 * mitigationFactor).roundToInt()
                        respDelta = (4 * mitigationFactor).roundToInt()
                        ppCo2Delta = 0.6 * mitigationFactor
                        scrubberDelta = (26 * mitigationFactor).roundToInt()
                        doseRate = 0.046
                    } else {
                        hrDelta = (10 * mitigationFactor + wave1 * 2).roundToInt()
                        hrvDelta = (-8 * mitigationFactor + wave2 * 2).roundToInt()
                        doseRate = 0.044
                    }
                }

                MissionScenario.SOLAR_PARTICLE_EVENT -> {
                    val speFactor = if (stabilized) 0.38 else 1.0
                    hrDelta = ((if (cfg.isEvaActive) 38 else 22) * speFactor + wave1 * 4).roundToInt()
                    hrvDelta = (-22 * speFactor + wave2 * 3).roundToInt()
                    spo2Delta = -0.8 * speFactor + wave1 * 0.2
                    tempDelta = 0.55 * speFactor + wave2 * 0.08
                    sysDelta = (16 * speFactor + wave1 * 3).roundToInt()
                    diaDelta = (8 * speFactor + wave2 * 2).roundToInt()
                    icpDelta = 1.6 * speFactor
                    respDelta = (5 * speFactor + wave1 * 1).roundToInt()
                    ppCo2Delta = 0.9 * speFactor
                    scrubberDelta = (28 * speFactor).roundToInt()
                    doseRate = if (cfg.isEvaActive) {
                        if (stabilized) 0.85 else 4.40
                    } else {
                        if (stabilized) 0.42 else 1.92
                    }
                }

                MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> {
                    val orthoFactor = if (lbnpActive) 0.45 else 1.0
                    hrDelta = (26 * orthoFactor + wave1 * 4).roundToInt()
                    hrvDelta = (-24 * orthoFactor + wave2 * 3).roundToInt()
                    spo2Delta = -1.1 * orthoFactor + wave1 * 0.2
                    tempDelta = 0.30 * orthoFactor
                    sysDelta = if (cfg.id == "kenji" || cfg.id == "elena") {
                        (-18 * orthoFactor + wave1 * 3).roundToInt()
                    } else {
                        (18 * orthoFactor + wave1 * 3).roundToInt()
                    }
                    diaDelta = (10 * orthoFactor + wave2 * 2).roundToInt()
                    icpDelta = (if (cfg.id == "kenji") 7.6 else 5.8) * orthoFactor + wave1 * 0.25
                    respDelta = (4 * orthoFactor).roundToInt()
                    ppCo2Delta = 0.7 * orthoFactor
                    fluidExtraMl = (640 * orthoFactor).roundToInt()
                    doseRate = 0.045
                }
            }

            val hr = (cfg.baseHr + hrDelta).coerceIn(48, 172)
            val hrv = (cfg.baseHrv + hrvDelta).coerceIn(12, 110)
            val spo2 = (cfg.baseSpo2 + spo2Delta).coerceIn(88.0, 100.0)
            val coreTemp = (cfg.baseTemp + tempDelta).coerceIn(35.8, 39.8)
            val thermalDelta = coreTemp - cfg.baseTemp
            val sys = (cfg.baseSys + sysDelta).coerceIn(85, 178)
            val dia = (cfg.baseDia + diaDelta).coerceIn(52, 110)
            val icp = (cfg.baseIcp + icpDelta).coerceIn(8.0, 26.0)
            val onsd = (4.7 + ((icp - 11.0).coerceAtLeast(0.0) * 0.16)).coerceIn(4.5, 6.9)
            val sansGrade = when {
                icp >= 18.5 -> 2
                icp >= 15.0 -> 1
                else -> 0
            }
            val resp = (cfg.baseResp + respDelta).coerceIn(10, 34)
            val ppCo2 = (cfg.basePpCo2 + ppCo2Delta).coerceIn(1.2, 6.8)
            val scrubberLoad = (22 + scrubberDelta + ((ppCo2 - 1.8) * 10).roundToInt()).coerceIn(15, 98)

            val prev = prevMap[cfg.id]
            val prevSpe = prev?.dosimetry?.speCumulativeMsv ?: cfg.baseSpe
            val prevGcr = prev?.dosimetry?.gcrCumulativeMsv ?: cfg.baseGcr
            val speIncrement = if (scenario == MissionScenario.SOLAR_PARTICLE_EVENT) {
                doseRate / 3600.0 * 12.0 // Accelerated telemetry visual delta for live dosimeter readout
            } else {
                0.0
            }
            val gcrIncrement = 0.042 / 3600.0 * 4.0

            val vitals = BiometricTelemetry(
                heartRateBpm = hr,
                hrvRmssdMs = hrv,
                hrvSdnnMs = (hrv * 1.24).roundToInt(),
                spo2Percent = (spo2 * 10.0).roundToInt() / 10.0,
                perfusionIndex = ((3.4 - (hr - 68) * 0.025).coerceIn(0.9, 4.8) * 10.0).roundToInt() / 10.0,
                coreTempCelsius = (coreTemp * 10.0).roundToInt() / 10.0,
                thermalDeltaCelsius = (thermalDelta * 100.0).roundToInt() / 100.0,
                lcgFlowPercent = lcgFlow,
                systolicMmHg = sys,
                diastolicMmHg = dia,
                icpMmHg = (icp * 10.0).roundToInt() / 10.0,
                onsdMm = (onsd * 10.0).roundToInt() / 10.0,
                sansGrade = sansGrade,
                respRateBpm = resp,
                ppCo2MmHg = (ppCo2 * 10.0).roundToInt() / 10.0,
                scrubberLoadPercent = scrubberLoad,
            )

            val severity = evaluateCrewSeverity(
                vitals = vitals,
                doseRate = doseRate,
                scenario = scenario,
                isEva = cfg.isEvaActive,
            )

            val prevHistory = prev?.ecgHistory ?: List(24) { hr }
            val nextHistory = (prevHistory.drop(1) + hr)

            val totalFluidMl = cfg.fluidShiftMl + fluidExtraMl
            val jugularCm2 = ((0.68 + (totalFluidMl - 1000) * 0.00085).coerceIn(0.62, 1.65) * 100.0).roundToInt() / 100.0

            CrewMemberTelemetry(
                id = cfg.id,
                callsign = cfg.callsign,
                name = cfg.name,
                roleBadge = cfg.roleBadge,
                suitAssignment = if (scenario == MissionScenario.EVA_SPACEWALK_STRESS && cfg.isEvaActive) {
                    "EVA // AIRLOCK-EXT"
                } else {
                    cfg.suitAssignment
                },
                isEvaActive = cfg.isEvaActive,
                vitals = vitals,
                ared = AredCompliance(
                    squatProgress = cfg.squatProgress,
                    squatLoadKn = cfg.squatKn,
                    deadliftProgress = cfg.deadliftProgress,
                    heelRaiseProgress = cfg.heelProgress,
                    cardioMinutes = cfg.cardioMin,
                    cardioTargetMinutes = cfg.cardioTarget,
                    bmdPreservationPercent = cfg.bmdPercent,
                ),
                circadianFluid = CircadianFluidMetrics(
                    phaseShiftHours = if (scenario == MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT) {
                        cfg.phaseShift - 0.8
                    } else {
                        cfg.phaseShift
                    },
                    circadianAlignmentPercent = if (scenario == MissionScenario.SOLAR_PARTICLE_EVENT) {
                        (cfg.alignment - 9).coerceIn(60, 100)
                    } else {
                        cfg.alignment
                    },
                    melanopsinLux = cfg.lux,
                    sleepEfficiencyPercent = cfg.sleepEff,
                    cephaladFluidShiftMl = totalFluidMl,
                    jugularCrossSectionCm2 = jugularCm2,
                    lbnpRecommendedMmHg = if (totalFluidMl >= 1500) -30 else -20,
                    lbnpActive = lbnpActive,
                ),
                dosimetry = DosimetryMetrics(
                    gcrCumulativeMsv = prevGcr + gcrIncrement,
                    speCumulativeMsv = prevSpe + speIncrement,
                    liveDoseRateMsvHr = doseRate,
                ),
                ecgHistory = nextHistory,
                overallSeverity = severity,
                profile = CrewDossierRepository.getProfile(cfg.id),
            )
        }
    }

    fun evaluateCrewSeverity(
        vitals: BiometricTelemetry,
        doseRate: Double,
        scenario: MissionScenario,
        isEva: Boolean,
    ): TelemetrySeverity {
        if (
            vitals.heartRateBpm >= 128 ||
            vitals.spo2Percent <= 95.0 ||
            vitals.coreTempCelsius >= 38.0 ||
            vitals.icpMmHg >= 18.5 ||
            vitals.ppCo2MmHg >= 4.2 ||
            (scenario == MissionScenario.SOLAR_PARTICLE_EVENT && doseRate >= 1.5)
        ) {
            return TelemetrySeverity.CRITICAL
        }
        if (
            vitals.heartRateBpm >= 100 ||
            vitals.spo2Percent <= 96.8 ||
            vitals.coreTempCelsius >= 37.4 ||
            vitals.icpMmHg >= 15.0 ||
            vitals.ppCo2MmHg >= 3.0 ||
            vitals.systolicMmHg >= 140 ||
            vitals.systolicMmHg <= 98 ||
            (isEva && scenario == MissionScenario.EVA_SPACEWALK_STRESS)
        ) {
            return TelemetrySeverity.CAUTION
        }
        return TelemetrySeverity.NOMINAL
    }

    fun buildScenarioTriageAlerts(
        scenario: MissionScenario,
        metFormatted: String,
    ): List<TriageAlert> {
        return when (scenario) {
            MissionScenario.NOMINAL_ORBIT -> listOf(
                TriageAlert(
                    id = "nom_1",
                    crewId = "kenji",
                    crewName = "SPC. Kenji Takahashi",
                    severity = TelemetrySeverity.CAUTION,
                    metTimestamp = metFormatted,
                    triggerMetric = "CIRCADIAN Δ -1.2h // ARED 76%",
                    clinicalSummary = "Mild circadian phase lag detected ahead of payload rack shift; resistive volume trailing daily bone-loading target.",
                    countermeasureTitle = "Schedule 480nm Blue-Enriched Light Therapy (20 min) & Complete Final ARED Heel-Raise Set",
                ),
                TriageAlert(
                    id = "nom_2",
                    crewId = "amara",
                    crewName = "DR. Amara Okafor",
                    severity = TelemetrySeverity.NOMINAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "ALL BIOMETRICS NOMINAL",
                    clinicalSummary = "Flight medical telemetry lock verified across 4/4 crew nodes. Ocular ultrasound & CDRA CO2 scrubbing nominal.",
                    countermeasureTitle = "Log Routine Expedition 74 Medical Readiness Sign-Off",
                ),
            )

            MissionScenario.EVA_SPACEWALK_STRESS -> listOf(
                TriageAlert(
                    id = "eva_1",
                    crewId = "marcus",
                    crewName = "FLT. Marcus Vance (EVA)",
                    severity = TelemetrySeverity.CRITICAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "HR 138 BPM // ΔT +1.45°C // ppCO2 4.8 mmHg",
                    clinicalSummary = "High metabolic workload during external truss torque ops. xEMU LiOH scrubber saturation at 86% with hypercapnic drive and thermal drift.",
                    countermeasureTitle = "Boost xEMU LCG Cooling Flow to 95%, Pause EVA Translation 4 Min & Sip 350 mL Electrolyte",
                ),
                TriageAlert(
                    id = "eva_2",
                    crewId = "elena",
                    crewName = "CDR. Elena Rostova",
                    severity = TelemetrySeverity.CAUTION,
                    metTimestamp = metFormatted,
                    triggerMetric = "HR 92 BPM // AIRLOCK OVERWATCH",
                    clinicalSummary = "Elevated sympathetic load during EVA umbilical monitoring and airlock pressure verification.",
                    countermeasureTitle = "Engage Controlled Paced Breathing Protocol & Verify Secondary O2 Umbilical Reserve",
                ),
            )

            MissionScenario.SOLAR_PARTICLE_EVENT -> listOf(
                TriageAlert(
                    id = "spe_1",
                    crewId = "marcus",
                    crewName = "FLT. Marcus Vance (EVA)",
                    severity = TelemetrySeverity.CRITICAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "SPE FLUX 412 MeV/cm²·s // 4.40 mSv/hr",
                    clinicalSummary = "NOAA SWPC S3 Solar Radiation Storm arrival. Unshielded EVA suit attenuation insufficient against >250 MeV proton flux.",
                    countermeasureTitle = "ABORT EVA TRANSLATION — Emergency Airlock Ingress & Relocate to Node-2 Water-Wall Storm Shelter",
                ),
                TriageAlert(
                    id = "spe_2",
                    crewId = "elena",
                    crewName = "CDR. Elena Rostova",
                    severity = TelemetrySeverity.CRITICAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "SPE DOSE 1.92 mSv/hr // HULL FLUX",
                    clinicalSummary = "Station bulkhead dose rate exceeding nominal 0.05 mSv/hr threshold. Acute cumulative career-limit acceleration risk.",
                    countermeasureTitle = "Seal Polyethylene Storm Shelter Bulkhead & Administer Radioprotective Antioxidant Kit",
                ),
                TriageAlert(
                    id = "spe_3",
                    crewId = "kenji",
                    crewName = "SPC. Kenji Takahashi",
                    severity = TelemetrySeverity.CAUTION,
                    metTimestamp = metFormatted,
                    triggerMetric = "HRV 46 ms // SLEEP DISRUPTION",
                    clinicalSummary = "Autonomic stress response following SPE klaxon and shelter relocation.",
                    countermeasureTitle = "Verify Personal Dosimeter Sync & Initiate Shelter Hydration Check",
                ),
            )

            MissionScenario.MICROGRAVITY_ORTHOSTATIC_SHIFT -> listOf(
                TriageAlert(
                    id = "ortho_1",
                    crewId = "kenji",
                    crewName = "SPC. Kenji Takahashi",
                    severity = TelemetrySeverity.CRITICAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "ICP 21.4 mmHg // ONSD 6.4 mm // SANS G2",
                    clinicalSummary = "Pronounced cephalad fluid shift (+1,900 mL) causing elevated intracranial pressure, optic nerve sheath distension, and jugular venous stasis.",
                    countermeasureTitle = "Deploy Chibis LBNP Suit (-30 mmHg for 35 min) & Initiate SANS Ophthalmic Venous Decongestion",
                ),
                TriageAlert(
                    id = "ortho_2",
                    crewId = "elena",
                    crewName = "CDR. Elena Rostova",
                    severity = TelemetrySeverity.CRITICAL,
                    metTimestamp = metFormatted,
                    triggerMetric = "BP 100/84 mmHg // ICP 18.2 mmHg",
                    clinicalSummary = "Orthostatic pulse pressure narrowing with concurrent cephalad venous congestion and baroreceptor desensitization.",
                    countermeasureTitle = "Activate LBNP -25 mmHg Countermeasure & Administer Oral Isotonic Fluid Loading (600 mL)",
                ),
            )
        }
    }

    private data class CrewBaseConfig(
        val id: String,
        val callsign: String,
        val name: String,
        val roleBadge: String,
        val suitAssignment: String,
        val isEvaActive: Boolean,
        val baseHr: Int,
        val baseHrv: Int,
        val baseSpo2: Double,
        val baseTemp: Double,
        val baseSys: Int,
        val baseDia: Int,
        val baseIcp: Double,
        val baseResp: Int,
        val basePpCo2: Double,
        val baseGcr: Double,
        val baseSpe: Double,
        val squatProgress: Float,
        val squatKn: Float,
        val deadliftProgress: Float,
        val heelProgress: Float,
        val cardioMin: Int,
        val cardioTarget: Int,
        val bmdPercent: Float,
        val phaseShift: Double,
        val alignment: Int,
        val lux: Int,
        val sleepEff: Int,
        val fluidShiftMl: Int,
    )
}
