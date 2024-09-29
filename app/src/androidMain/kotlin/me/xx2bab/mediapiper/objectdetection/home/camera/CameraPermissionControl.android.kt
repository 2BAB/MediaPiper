package me.xx2bab.mediapiper.objectdetection.home.camera

import android.Manifest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.UiComposable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraPermissionControl(PermissionGrantedContent:  @Composable @UiComposable () -> Unit) {
    // We first have to deal with the camera permission, so we declare a state for it
    val storagePermissionState: PermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)

    // When using this composable, we wanna check the camera permission state, and ask for the
    // permission to use the phone camera in case we don't already have it
    LaunchedEffect(key1 = Unit) {
        if (!storagePermissionState.hasPermission) {
            storagePermissionState.launchPermissionRequest()
        }
    }


    // In case we don't have the permission to use a camera, we'll just display a text to let the
    // user know that that's the case, and we won't show anything else
    if (!storagePermissionState.hasPermission) {
        Text(text = "No Storage Permission!")
    } else {
        PermissionGrantedContent()
    }
}