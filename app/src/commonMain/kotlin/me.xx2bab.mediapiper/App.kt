package me.xx2bab.mediapiper

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import me.xx2bab.mediapiper.llm.AiRoute
import me.xx2bab.mediapiper.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    Navigator(AiRoute()) { navigator ->
        FadeTransition(navigator)
    }
}
