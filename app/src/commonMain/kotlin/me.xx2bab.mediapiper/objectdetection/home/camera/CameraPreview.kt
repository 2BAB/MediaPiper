package me.xx2bab.mediapiper.objectdetection.home.camera

import androidx.compose.runtime.Composable
import me.xx2bab.mediapiper.objectdetection.data.model.ObjectDetectionResult

@Composable
expect fun CameraPreview(
    threshold: Float,
    maxResults: Int,
    delegate: Int,
    mlModel: Int,
    setInferenceTime: (newInferenceTime: Int) -> Unit,
    onDetectionResultUpdate: (result: ObjectDetectionResult) -> Unit
)