package me.xx2bab.mediapiper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.FadeTransition
import me.xx2bab.mediapiper.llm.AiRoute
import me.xx2bab.mediapiper.objectdetection.ObjectDetectionRoute
import me.xx2bab.mediapiper.theme.AppTheme
import mediapiper.app.generated.resources.Res
import mediapiper.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import mediapiper.app.generated.resources.llm_screen_name
import mediapiper.app.generated.resources.object_detection_name

@Composable
internal fun App() = AppTheme {
    Navigator(HomeRoute()) { navigator ->
        FadeTransition(navigator)
    }
}

class HomeRoute : Screen {

    @Composable
    override fun Content() {
        HomeScreen()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HomeScreen() {
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = {
                Text(stringResource(Res.string.app_name))
            })
        }) {
            Column(modifier = Modifier.fillMaxSize().padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    navigator.push(AiRoute())
                }) {
                    Text(stringResource(Res.string.llm_screen_name))
                }

                Button(onClick = {
                    navigator.push(ObjectDetectionRoute())
                }) {
                    Text(stringResource(Res.string.object_detection_name))
                }
            }

        }
    }

}