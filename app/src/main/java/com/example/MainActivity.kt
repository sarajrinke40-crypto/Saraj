package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MissionCommandDashboard
import com.example.ui.theme.AetherTheme
import com.example.viewmodel.MissionCommandViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            AetherTheme {
                val viewModel: MissionCommandViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                MissionCommandDashboard(
                    state = uiState,
                    onInjectScenario = viewModel::injectScenario,
                    onSelectCrew = viewModel::selectCrew,
                    onOpenDossier = viewModel::openDossier,
                    onCloseDossier = viewModel::closeDossier,
                    onSelectDossierSection = viewModel::selectDossierSection,
                    onTogglePrivacyMask = viewModel::togglePrivacyMask,
                    onSelectSection = viewModel::selectSection,
                    onAcknowledgeAlert = viewModel::acknowledgeAlert,
                    onToggleLbnp = viewModel::toggleLbnpCountermeasure,
                )
            }
        }
    }
}
