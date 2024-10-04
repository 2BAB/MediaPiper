package me.xx2bab.mediapiper.objectdetection.home.camera

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.UiComposable
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
actual fun CameraPermissionControl(PermissionGrantedContent:  @Composable @UiComposable () -> Unit) {
    var hasCameraPermission by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        hasCameraPermission = requestCameraAccess()
    }

    when (hasCameraPermission) {
        true -> {
            PermissionGrantedContent()
        }
        false -> {
            Text("Camera permission denied. Please grant access from settings.")
        }
        null -> {
            Text("Requesting camera permission...")
        }
    }
}


private suspend fun requestCameraAccess(): Boolean = suspendCoroutine { continuation ->
    val authorizationStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

    when (authorizationStatus) {
        AVAuthorizationStatusNotDetermined -> {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                continuation.resume(granted)
            }
        }
        AVAuthorizationStatusRestricted, AVAuthorizationStatusDenied -> {
            continuation.resume(false)
        }
        AVAuthorizationStatusAuthorized -> {
            continuation.resume(true)
        }
        else -> {
            continuation.resume(false)
        }
    }
}