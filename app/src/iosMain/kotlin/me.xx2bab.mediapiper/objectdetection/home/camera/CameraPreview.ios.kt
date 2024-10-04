package me.xx2bab.mediapiper.objectdetection.home.camera

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import me.xx2bab.mediapiper.objectdetection.data.model.ObjectDetectionResult
import org.koin.compose.koinInject
import platform.UIKit.UIView


@Composable
actual fun CameraPreview(
    threshold: Float,
    maxResults: Int,
    delegate: Int,
    mlModel: Int,
    setInferenceTime: (newInferenceTime: Int) -> Unit,
    onDetectionResultUpdate: (result: ObjectDetectionResult) -> Unit,
) {
    val iOSCameraPreviewCreator = koinInject<IOSCameraPreviewCreator>()
    UIKitView(
        factory = {
            val iosCameraPreview: UIView = iOSCameraPreviewCreator(
                threshold,
                maxResults,
                delegate,
                mlModel,
                setInferenceTime,
                onDetectionResultUpdate)
            iosCameraPreview
        },
        modifier = Modifier.fillMaxSize(),
        update = { _ ->

        }
    )

}

