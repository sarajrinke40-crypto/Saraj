package com.example.data

import com.example.model.CrewDossierProfile
import com.example.model.EmergencyContact
import com.example.model.MedicalAllergy
import com.example.model.MedicalEventRecord
import com.example.model.MedicalHistoryProfile
import com.example.model.PsychDomainScore
import com.example.model.PsychReadinessProfile
import com.example.model.TelemetrySeverity
import com.example.model.TrainingCredential

object CrewDossierRepository {

    private val profiles: Map<String, CrewDossierProfile> = mapOf(
        "elena" to CrewDossierProfile(
            crewId = "elena",
            nationality = "INTL / ROS-ESA",
            ageYears = 42,
            totalDaysInSpace = 386,
            evaHoursLogged = 41.5,
            agencyDivision = "Orbital Command & Flight Operations",
            specializedTraining = listOf(
                TrainingCredential(
                    code = "CMD-LEO-IV",
                    title = "ISS / Orbital Station Commander Qualification",
                    authority = "Multilateral Crew Operations Panel",
                    loggedHoursOrCycles = "3,240 SIM HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "EVA-ORL-02",
                    title = "Orlan-MKS & xEMU Contingency Airlock Operations",
                    authority = "NBL / Star City Hydro-Lab",
                    loggedHoursOrCycles = "41.5 EVA HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "RNDZ-DOCK",
                    title = "Manual Spacecraft Rendezvous & Proximity Docking",
                    authority = "Flight Operations Directorate",
                    loggedHoursOrCycles = "18 DOCK CYCLES",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "CMO-ALT",
                    title = "Designated Crew Medical Officer (Secondary)",
                    authority = "Aerospace Med Branch",
                    loggedHoursOrCycles = "120 CLIN HRS",
                    status = "VERIFIED",
                ),
            ),
            medicalHistory = MedicalHistoryProfile(
                bloodType = "O NEGATIVE",
                heightCm = 171,
                weightKg = 63.4,
                allergies = listOf(
                    MedicalAllergy(
                        substance = "Penicillin-class Beta-Lactams",
                        reaction = "Systemic urticaria & bronchospasm (Avoid — use Azithromycin / Levofloxacin)",
                        severity = TelemetrySeverity.CRITICAL,
                    ),
                    MedicalAllergy(
                        substance = "Latex Adhesives",
                        reaction = "Localized contact dermatitis under ECG electrode pads (Use hydrogel pads)",
                        severity = TelemetrySeverity.CAUTION,
                    ),
                ),
                pastInjuriesAndSurgeries = listOf(
                    MedicalEventRecord(
                        year = "2021",
                        title = "Right Clavicle Hairline Fracture (Parachute Landing)",
                        clinicalNote = "Non-operative union; full range of motion restored under load testing.",
                        clearanceStatus = "UNRESTRICTED",
                    ),
                    MedicalEventRecord(
                        year = "2024",
                        title = "Mild High-G Vestibular Adaptation Episode",
                        clinicalNote = "Resolved within 48h post-centrifuge; baseline caloric testing normal.",
                        clearanceStatus = "CLEARED",
                    ),
                ),
                activeMedications = listOf(
                    "Cholecalciferol (Vitamin D3) 1,000 IU QD",
                    "Potassium Citrate 20 mEq PRN Renal Stone Prophylaxis",
                ),
                flightWaivers = "Class I Flight Status — Hydrogel ECG Lead Mandate",
            ),
            psychologicalReadiness = PsychReadinessProfile(
                compositeScore = 94,
                clearanceRating = "GREEN // OPTIMAL COMMAND POSTURE",
                lastEvaluatedMet = "138:14:00:00",
                evaluator = "Dr. H. Lindqvist, Behavioral Health Lead",
                clinicalSummary = "Sustains high executive composure under multi-system anomalies. Sleep architecture stable; strong leadership rapport across all four crew quarters.",
                domains = listOf(
                    PsychDomainScore("Cognitive Throughput", 96, "Zero latency degradation"),
                    PsychDomainScore("Stress & Anomaly Resilience", 95, "High vagal recovery"),
                    PsychDomainScore("Sleep & Circadian Stability", 90, "6.9h avg consolidated"),
                    PsychDomainScore("Interpersonal Crew Cohesion", 94, "Strong command alignment"),
                ),
            ),
            emergencyContacts = listOf(
                EmergencyContact(
                    priorityLabel = "PRIMARY NOK",
                    name = "Viktor Rostov",
                    relationship = "Spouse",
                    location = "Cologne, DE // ESA EAC",
                    primaryChannel = "+49-2203-555-0148",
                    secondaryChannel = "MOC-EAC-NOK-011",
                ),
                EmergencyContact(
                    priorityLabel = "AGENCY LIAISON",
                    name = "Cmdr. M. Petrov",
                    relationship = "Flight Crew Operations Chief",
                    location = "Houston, TX // JSC Bldg 4",
                    primaryChannel = "+1-281-555-0192",
                    secondaryChannel = "DSN-CAPCOM-SURGEON",
                ),
            ),
        ),

        "marcus" to CrewDossierProfile(
            crewId = "marcus",
            nationality = "USA / NASA",
            ageYears = 37,
            totalDaysInSpace = 194,
            evaHoursLogged = 78.2,
            agencyDivision = "Extravehicular Activity & Robotics Branch",
            specializedTraining = listOf(
                TrainingCredential(
                    code = "EVA-xEMU-LEAD",
                    title = "Lead Spacewalk Specialist (xEMU / EMU Systems)",
                    authority = "JSC Neutral Buoyancy Lab",
                    loggedHoursOrCycles = "78.2 EVA HRS",
                    status = "ACTIVE EVA",
                ),
                TrainingCredential(
                    code = "SSRMS-OPS",
                    title = "Canadarm2 Mobile Servicing & Truss Manipulation",
                    authority = "Canadian Space Agency / JSC",
                    loggedHoursOrCycles = "640 SIM HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "HYPO-DECOMP",
                    title = "Decompression Sickness & Suit Rupture Recovery",
                    authority = "Altitude Chamber Ops",
                    loggedHoursOrCycles = "14 RUNS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "THERM-REG",
                    title = "High-Workload LCG Thermal Load Management",
                    authority = "EVA Physiology Lab",
                    loggedHoursOrCycles = "28 TESTS",
                    status = "VERIFIED",
                ),
            ),
            medicalHistory = MedicalHistoryProfile(
                bloodType = "A POSITIVE",
                heightCm = 182,
                weightKg = 79.8,
                allergies = listOf(
                    MedicalAllergy(
                        substance = "Sulfa Antimicrobials (TMP-SMX)",
                        reaction = "Generalized maculopapular rash & mild pyrexia",
                        severity = TelemetrySeverity.CAUTION,
                    ),
                ),
                pastInjuriesAndSurgeries = listOf(
                    MedicalEventRecord(
                        year = "2020",
                        title = "Left Shoulder Arthroscopic Labral Repair (SLAP)",
                        clinicalNote = "Full torque recovery verified via NBL 8-hour suit endurance trials; monitor rotator fatigue post-EVA.",
                        clearanceStatus = "EVA CLEARED",
                    ),
                    MedicalEventRecord(
                        year = "2023",
                        title = "Grade I Right Patellar Tendinopathy",
                        clinicalNote = "Resolved with eccentric ARED loading protocol; asymptomatic.",
                        clearanceStatus = "UNRESTRICTED",
                    ),
                ),
                activeMedications = listOf(
                    "Electrolyte Oral Rehydration Buffer Pre-EVA",
                    "Ibuprofen 400 mg PRN Post-EVA Joint Soreness",
                ),
                flightWaivers = "Waiver W-204: Left Glenohumeral Anchor — Annual MRI Nominal",
            ),
            psychologicalReadiness = PsychReadinessProfile(
                compositeScore = 89,
                clearanceRating = "GREEN // HIGH EVA AUTONOMY",
                lastEvaluatedMet = "140:09:30:00",
                evaluator = "Dr. H. Lindqvist, Behavioral Health Lead",
                clinicalSummary = "Thrives during complex extravehicular tasks. Mild post-EVA sympathetic elevation expected; watch for cumulative sleep truncation during back-to-back spacewalks.",
                domains = listOf(
                    PsychDomainScore("Spatial & Task Focus", 95, "Superior EVA precision"),
                    PsychDomainScore("Stress & Anomaly Resilience", 91, "Steady under suit warnings"),
                    PsychDomainScore("Sleep & Circadian Stability", 82, "Mild pre-EVA anticipation"),
                    PsychDomainScore("Interpersonal Crew Cohesion", 88, "Responsive to IVA overwatch"),
                ),
            ),
            emergencyContacts = listOf(
                EmergencyContact(
                    priorityLabel = "PRIMARY NOK",
                    name = "Claire Vance",
                    relationship = "Spouse",
                    location = "League City, TX",
                    primaryChannel = "+1-281-555-0137",
                    secondaryChannel = "JSC-FAM-SUP-044",
                ),
                EmergencyContact(
                    priorityLabel = "SECONDARY NOK",
                    name = "Col. David Vance (Ret.)",
                    relationship = "Father",
                    location = "Colorado Springs, CO",
                    primaryChannel = "+1-719-555-0184",
                    secondaryChannel = "N/A",
                ),
            ),
        ),

        "kenji" to CrewDossierProfile(
            crewId = "kenji",
            nationality = "JPN / JAXA",
            ageYears = 35,
            totalDaysInSpace = 142,
            evaHoursLogged = 12.4,
            agencyDivision = "Microgravity Science & Life Support Systems",
            specializedTraining = listOf(
                TrainingCredential(
                    code = "KIBO-PAY-09",
                    title = "Kibo Exposed Facility & Fluid Physics Payload Specialist",
                    authority = "JAXA Tsukuba Space Center",
                    loggedHoursOrCycles = "1,480 LAB HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "ECLSS-CDRA",
                    title = "Environmental Control & CO2 Scrubber Maintenance",
                    authority = "Orbital Systems Engineering",
                    loggedHoursOrCycles = "320 LAB HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "SANS-OCT-OP",
                    title = "Spectralis OCT2 & Ocular Ultrasound Operator",
                    authority = "Space Medicine Directorate",
                    loggedHoursOrCycles = "45 SCANS",
                    status = "CURRENT",
                ),
            ),
            medicalHistory = MedicalHistoryProfile(
                bloodType = "B POSITIVE",
                heightCm = 174,
                weightKg = 66.2,
                allergies = listOf(
                    MedicalAllergy(
                        substance = "Iodinated Contrast Media",
                        reaction = "Delayed pruritus & flushing",
                        severity = TelemetrySeverity.CAUTION,
                    ),
                    MedicalAllergy(
                        substance = "No Known Drug Allergies (Oral/IV)",
                        reaction = "Standard pharmacopeia cleared",
                        severity = TelemetrySeverity.NOMINAL,
                    ),
                ),
                pastInjuriesAndSurgeries = listOf(
                    MedicalEventRecord(
                        year = "2019",
                        title = "PRK Refractive Corneal Surgery (Bilateral)",
                        clinicalNote = "20/15 uncorrected acuity; mild hyperopic shift (+0.50 D) noted on Mission Day 110 — SANS surveillance active.",
                        clearanceStatus = "MONITOR SANS",
                    ),
                    MedicalEventRecord(
                        year = "2022",
                        title = "Appendectomy (Laparoscopic, Uncomplicated)",
                        clinicalNote = "Complete healing; zero intra-abdominal adhesions on baseline ultrasound.",
                        clearanceStatus = "CLEARED",
                    ),
                ),
                activeMedications = listOf(
                    "Melatonin 0.5 mg PRN Circadian Phase Alignment",
                    "Artificial Tears (Preservative-Free) QID",
                ),
                flightWaivers = "SANS Surveillance Protocol — Bi-weekly ONSD & OCT Checks",
            ),
            psychologicalReadiness = PsychReadinessProfile(
                compositeScore = 83,
                clearanceRating = "AMBER // MONITOR SLEEP & FATIGUE",
                lastEvaluatedMet = "141:18:10:00",
                evaluator = "Dr. H. Lindqvist, Behavioral Health Lead",
                clinicalSummary = "Detail-oriented and analytical, but showing -1.2h circadian phase drift from late-shift payload troubleshooting. Recommend protected sleep block and morning 480nm light exposure.",
                domains = listOf(
                    PsychDomainScore("Cognitive Throughput", 91, "Accurate payload execution"),
                    PsychDomainScore("Stress & Anomaly Resilience", 84, "Elevated workload fatigue"),
                    PsychDomainScore("Sleep & Circadian Stability", 71, "Fragmented REM cycles"),
                    PsychDomainScore("Interpersonal Crew Cohesion", 87, "Collaborative & supportive"),
                ),
            ),
            emergencyContacts = listOf(
                EmergencyContact(
                    priorityLabel = "PRIMARY NOK",
                    name = "Yuki Takahashi",
                    relationship = "Spouse",
                    location = "Tsukuba, Ibaraki, JP",
                    primaryChannel = "+81-29-555-0163",
                    secondaryChannel = "JAXA-TKSC-NOK-08",
                ),
                EmergencyContact(
                    priorityLabel = "AGENCY LIAISON",
                    name = "Dr. T. Nakamura",
                    relationship = "JAXA Flight Surgeon",
                    location = "Tsukuba, JP // TKSC",
                    primaryChannel = "+81-29-555-0119",
                    secondaryChannel = "MCC-TSUKUBA-MED",
                ),
            ),
        ),

        "amara" to CrewDossierProfile(
            crewId = "amara",
            nationality = "GBR / ESA",
            ageYears = 39,
            totalDaysInSpace = 218,
            evaHoursLogged = 19.0,
            agencyDivision = "Aerospace Medicine & Crew Health Command",
            specializedTraining = listOf(
                TrainingCredential(
                    code = "CMO-PRIME-I",
                    title = "Chief Medical Officer — Orbital Trauma & Tele-Surgery",
                    authority = "ESA / NASA Space Medicine Board",
                    loggedHoursOrCycles = "2,100 CLIN HRS",
                    status = "ACTIVE CMO",
                ),
                TrainingCredential(
                    code = "RAD-MED-SPE",
                    title = "Acute Radiation Syndrome & SPE Dosimetry Triage",
                    authority = "European Radiation Health Lab",
                    loggedHoursOrCycles = "90 SIM HRS",
                    status = "CURRENT",
                ),
                TrainingCredential(
                    code = "LBNP-SANS",
                    title = "Chibis LBNP & Intracranial Pressure Countermeasures",
                    authority = "EAC Aerospace Physiology",
                    loggedHoursOrCycles = "62 PROTOCOLS",
                    status = "CURRENT",
                ),
            ),
            medicalHistory = MedicalHistoryProfile(
                bloodType = "O POSITIVE",
                heightCm = 169,
                weightKg = 61.5,
                allergies = listOf(
                    MedicalAllergy(
                        substance = "No Known Drug Allergies (NKDA)",
                        reaction = "Cleared for full orbital formulary",
                        severity = TelemetrySeverity.NOMINAL,
                    ),
                ),
                pastInjuriesAndSurgeries = listOf(
                    MedicalEventRecord(
                        year = "2018",
                        title = "Left Ankle Lateral Ligament Sprain (Grade II)",
                        clinicalNote = "Conservative physiotherapy; full proprioception and treadmill impact tolerance.",
                        clearanceStatus = "UNRESTRICTED",
                    ),
                ),
                activeMedications = listOf(
                    "Cholecalciferol (Vitamin D3) 1,000 IU QD",
                ),
                flightWaivers = "None — Unconditional Class I Medical Clearance",
            ),
            psychologicalReadiness = PsychReadinessProfile(
                compositeScore = 96,
                clearanceRating = "GREEN // EXEMPLARY CLINICAL CALM",
                lastEvaluatedMet = "141:06:00:00",
                evaluator = "Dr. H. Lindqvist, Behavioral Health Lead",
                clinicalSummary = "Anchors crew emotional stability and behavioral health monitoring. Exceptional sleep efficiency and rapid autonomic recovery after triage drills.",
                domains = listOf(
                    PsychDomainScore("Cognitive Throughput", 97, "Peak diagnostic precision"),
                    PsychDomainScore("Stress & Anomaly Resilience", 97, "Model clinical composure"),
                    PsychDomainScore("Sleep & Circadian Stability", 94, "91% sleep efficiency"),
                    PsychDomainScore("Interpersonal Crew Cohesion", 96, "High peer trust index"),
                ),
            ),
            emergencyContacts = listOf(
                EmergencyContact(
                    priorityLabel = "PRIMARY NOK",
                    name = "Dr. Chidi Okafor",
                    relationship = "Brother",
                    location = "London, UK",
                    primaryChannel = "+44-20-7946-0192",
                    secondaryChannel = "ESA-EAC-NOK-204",
                ),
                EmergencyContact(
                    priorityLabel = "SECONDARY NOK",
                    name = "Ngozi Okafor",
                    relationship = "Mother",
                    location = "Manchester, UK",
                    primaryChannel = "+44-161-496-0145",
                    secondaryChannel = "N/A",
                ),
            ),
        ),
    )

    fun getProfile(crewId: String): CrewDossierProfile {
        return profiles[crewId] ?: profiles.values.first()
    }
}
