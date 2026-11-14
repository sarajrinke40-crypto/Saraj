package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.MissionScenario
import com.example.model.TelemetrySeverity
import com.example.viewmodel.MissionCommandViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AETHER", appName)
    }

    @Test
    fun `scenario injection and triage acknowledgment update telemetry state`() {
        val vm = MissionCommandViewModel()
        assertEquals(MissionScenario.NOMINAL_ORBIT, vm.uiState.value.activeScenario)

        vm.injectScenario(MissionScenario.SOLAR_PARTICLE_EVENT)
        val speState = vm.uiState.value
        assertEquals(MissionScenario.SOLAR_PARTICLE_EVENT, speState.activeScenario)
        assertEquals(TelemetrySeverity.CRITICAL, speState.shieldingStatus.severity)

        val firstAlert = speState.triageLog.first()
        vm.acknowledgeAlert(firstAlert.id)
        val afterAck = vm.uiState.value
        assertTrue(afterAck.triageLog.first { it.id == firstAlert.id }.acknowledged)
        assertTrue(afterAck.shieldingStatus.stormShelterActive)
    }

    @Test
    fun `crew dossier opens with training medical psych and emergency contacts`() {
        val vm = MissionCommandViewModel()
        vm.openDossier("elena")

        val dossierCrew = vm.uiState.value.activeDossierCrew
        assertNotNull(dossierCrew)
        assertEquals("CDR. Elena Rostova", dossierCrew!!.name)
        assertTrue(dossierCrew.profile.specializedTraining.isNotEmpty())
        assertTrue(dossierCrew.profile.medicalHistory.allergies.isNotEmpty())
        assertTrue(dossierCrew.profile.psychologicalReadiness.compositeScore > 0)
        assertTrue(dossierCrew.profile.emergencyContacts.isNotEmpty())

        vm.togglePrivacyMask()
        assertTrue(vm.uiState.value.isPrivacyMasked)

        vm.closeDossier()
        assertNull(vm.uiState.value.activeDossierCrewId)
    }
}
