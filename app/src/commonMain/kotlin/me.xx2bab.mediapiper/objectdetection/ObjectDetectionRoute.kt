package me.xx2bab.mediapiper.objectdetection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.xx2bab.mediapiper.llm.ChatRoute
import me.xx2bab.mediapiper.llm.LoadingScreen
import me.xx2bab.mediapiper.llm.MODEL_NAME
import mediapiper.app.generated.resources.Res
import mediapiper.app.generated.resources.llm_screen_name
import mediapiper.app.generated.resources.object_detection_name
import org.jetbrains.compose.resources.stringResource

class ObjectDetectionRoute : Screen {

    @Composable
    override fun Content() {
        ObjectDetectionWrapperScreen()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ObjectDetectionWrapperScreen() {
        Column {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.object_detection_name))
                },
                navigationIcon = {
                    val navigator = LocalNavigator.currentOrThrow
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White)
            ) {

            }
        }
    }

}