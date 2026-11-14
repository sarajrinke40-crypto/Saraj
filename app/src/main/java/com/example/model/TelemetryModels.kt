package com.example.model

enum class TelemetrySeverity {
    NOMINAL,
    CAUTION,
    CRITICAL,
}

enum class MissionScenario(
    val id: String,
    val shortLabel: String,
    val fullLabel: String,
    val codeTag: String,
) {
    NOMINAL_ORBIT(
        id = "nominal_orbit",
        shortLabel = "Nominal Orbit",
        fullLabel = "Nominal Orbit",
        codeTag = "LEO // ISS-7",
    ),
    EVA_SPACEWALK_STRESS(
        id = "eva_stress",
        shortLabel = "EVA Stress",
        fullLabel = "EVA Spacewalk Stress",
        codeTag = "AIRLOCK // xEMU",
    ),
    SOLAR_PARTICLE_EVENT(
        id = "spe_radiation",
        shortLabel = "Solar Particle Event",
        fullLabel = "Solar Particle Event (Radiation)",
        codeTag = "SPE // >250 MeV",
    ),
    MICROGRAVITY_ORTHOSTATIC_SHIFT(
        id = "orthostatic_shift",
        shortLabel = "Orthostatic Shift",
        fullLabel = "Microgravity Orthostatic Shift",
        codeTag = "FLUID // CEPHALAD",
    ),
}

enum class DashboardSection(val label: String) {
    ALL("ALL SYSTEMS"),
    BIOMETRICS("ECG & VITALS"),
    SPACE_MED("SPACE MED"),
    TRIAGE("TRIAGE LOG"),
}

enum class DossierSection(val label: String) {
    ALL("FULL DOSSIER"),
    TRAINING("TRAINING"),
    MEDICAL("MEDICAL HX"),
    PSYCH("PSYCH READINESS"),
    EMERGENCY("CONTACTS"),
}

data class TrainingCredential(
    val code: String,
    val title: String,
    val authority: String,
    val loggedHoursOrCycles: String,
    val status: String,
)

data class MedicalAllergy(
    val substance: String,
    val reaction: String,
    val severity: TelemetrySeverity,
)

data class MedicalEventRecord(
    val year: String,
    val title: String,
    val clinicalNote: String,
    val clearanceStatus: String,
)

data class MedicalHistoryProfile(
    val bloodType: String,
    val heightCm: Int,
    val weightKg: Double,
    val allergies: List<MedicalAllergy>,
    val pastInjuriesAndSurgeries: List<MedicalEventRecord>,
    val activeMedications: List<String>,
    val flightWaivers: String,
)

data class PsychDomainScore(
    val label: String,
    val scorePercent: Int,
    val statusNote: String,
)

data class PsychReadinessProfile(
    val compositeScore: Int,
    val clearanceRating: String,
    val lastEvaluatedMet: String,
    val evaluator: String,
    val clinicalSummary: String,
    val domains: List<PsychDomainScore>,
)

data class EmergencyContact(
    val priorityLabel: String,
    val name: String,
    val relationship: String,
    val location: String,
    val primaryChannel: String,
    val secondaryChannel: String,
)

data class CrewDossierProfile(
    val crewId: String,
    val nationality: String,
    val ageYears: Int,
    val totalDaysInSpace: Int,
    val evaHoursLogged: Double,
    val agencyDivision: String,
    val specializedTraining: List<TrainingCredential>,
    val medicalHistory: MedicalHistoryProfile,
    val psychologicalReadiness: PsychReadinessProfile,
    val emergencyContacts: List<EmergencyContact>,
)

data class BiometricTelemetry(
    val heartRateBpm: Int,
    val hrvRmssdMs: Int,
    val hrvSdnnMs: Int,
    val spo2Percent: Double,
    val perfusionIndex: Double,
    val coreTempCelsius: Double,
    val thermalDeltaCelsius: Double,
    val lcgFlowPercent: Int,
    val systolicMmHg: Int,
    val diastolicMmHg: Int,
    val icpMmHg: Double,
    val onsdMm: Double,
    val sansGrade: Int,
    val respRateBpm: Int,
    val ppCo2MmHg: Double,
    val scrubberLoadPercent: Int,
) {
    val mapMmHg: Int
        get() = ((systolicMmHg + (2 * diastolicMmHg)) / 3.0).toInt()
}

data class AredCompliance(
    val squatProgress: Float,
    val squatLoadKn: Float,
    val deadliftProgress: Float,
    val heelRaiseProgress: Float,
    val cardioMinutes: Int,
    val cardioTargetMinutes: Int,
    val bmdPreservationPercent: Float,
)

data class CircadianFluidMetrics(
    val phaseShiftHours: Double,
    val circadianAlignmentPercent: Int,
    val melanopsinLux: Int,
    val sleepEfficiencyPercent: Int,
    val cephaladFluidShiftMl: Int,
    val jugularCrossSectionCm2: Double,
    val lbnpRecommendedMmHg: Int,
    val lbnpActive: Boolean,
)

data class DosimetryMetrics(
    val gcrCumulativeMsv: Double,
    val speCumulativeMsv: Double,
    val liveDoseRateMsvHr: Double,
    val careerLimitMsv: Double = 600.0,
) {
    val totalCumulativeMsv: Double
        get() = gcrCumulativeMsv + speCumulativeMsv

    val remainingCareerMsv: Double
        get() = (careerLimitMsv - totalCumulativeMsv).coerceAtLeast(0.0)

    val careerLimitFraction: Float
        get() = (totalCumulativeMsv / careerLimitMsv).toFloat().coerceIn(0f, 1f)
}

data class CrewMemberTelemetry(
    val id: String,
    val callsign: String,
    val name: String,
    val roleBadge: String,
    val suitAssignment: String,
    val isEvaActive: Boolean,
    val vitals: BiometricTelemetry,
    val ared: AredCompliance,
    val circadianFluid: CircadianFluidMetrics,
    val dosimetry: DosimetryMetrics,
    val ecgHistory: List<Int>,
    val overallSeverity: TelemetrySeverity,
    val profile: CrewDossierProfile,
)

data class TriageAlert(
    val id: String,
    val crewId: String,
    val crewName: String,
    val severity: TelemetrySeverity,
    val metTimestamp: String,
    val triggerMetric: String,
    val clinicalSummary: String,
    val countermeasureTitle: String,
    val acknowledged: Boolean = false,
    val acknowledgedAtMet: String? = null,
)

data class RadiationShieldingStatus(
    val modeLabel: String,
    val attenuationPercent: Double,
    val fluxMevCm2s: Double,
    val stormShelterActive: Boolean,
    val severity: TelemetrySeverity,
)

data class MissionCommandUiState(
    val metSeconds: Long,
    val metFormatted: String,
    val activeScenario: MissionScenario,
    val shieldingStatus: RadiationShieldingStatus,
    val crewRoster: List<CrewMemberTelemetry>,
    val selectedCrewId: String,
    val triageLog: List<TriageAlert>,
    val selectedSection: DashboardSection = DashboardSection.ALL,
    val activeDossierCrewId: String? = null,
    val activeDossierSection: DossierSection = DossierSection.ALL,
    val isPrivacyMasked: Boolean = false,
    val linkQualityPercent: Double = 99.6,
    val telemetryTickCount: Long = 0L,
) {
    val selectedCrew: CrewMemberTelemetry
        get() = crewRoster.firstOrNull { it.id == selectedCrewId } ?: crewRoster.first()

    val activeDossierCrew: CrewMemberTelemetry?
        get() = activeDossierCrewId?.let { id -> crewRoster.firstOrNull { it.id == id } }
}
