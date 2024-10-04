package me.xx2bab.mediapiper.objectdetection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import me.xx2bab.mediapiper.objectdetection.data.ObjectDetectorHelper
import me.xx2bab.mediapiper.objectdetection.home.HomeScreen
import me.xx2bab.mediapiper.objectdetection.options.OptionsScreen

class ObjectDetectionRoute : Screen {

    @Composable
    override fun Content() {
        ObjectDetectionWrapperScreen()
    }

    @Composable
    private fun ObjectDetectionWrapperScreen() {
        // Here we're first defining the object detector parameters states

        // We're defining them at the top of the components tree so that they
        // are accessible to all the app components, and any change of these
        // states will be reflected across the entire app, ensuring consistency

        // We're using "rememberSaveable" rather than "remember" so that the state
        // is preserved when the app change its orientation.

        // Since using a data class with "rememberSaveable" requires additional
        // configuration, we'll just define each option state individually as
        // "rememberSaveable" works with primitive values out of the box

        var threshold by rememberSaveable {
            mutableStateOf(0.4f)
        }
        var maxResults by rememberSaveable {
            mutableStateOf(5)
        }
        var delegate by rememberSaveable {
            mutableStateOf(ObjectDetectorHelper.DELEGATE_CPU)
        }
        var mlModel by rememberSaveable {
            mutableStateOf(ObjectDetectorHelper.MODEL_EFFICIENTDETV0)
        }
        var showHomeScreen by rememberSaveable {
            mutableStateOf(true)
        }
        Scaffold { paddingValues ->
            Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (showHomeScreen) {
                    HomeScreen(
                        onOptionsButtonClick = {
                            showHomeScreen = false
                        },
                        threshold = threshold,
                        maxResults = maxResults,
                        delegate = delegate,
                        mlModel = mlModel
                    )
                } else {
                    OptionsScreen(onBackButtonClick = {
                        showHomeScreen = true
                    },
                        threshold = threshold,
                        setThreshold = { threshold = it },
                        maxResults = maxResults,
                        setMaxResults = { maxResults = it },
                        delegate = delegate,
                        setDelegate = { delegate = it },
                        mlModel = mlModel,
                        setMlModel = { mlModel = it })
                }
            }
        }
    }

}