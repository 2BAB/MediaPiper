package me.xx2bab.mediapiper.objectdetection.home.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.UiComposable

@Composable
expect fun CameraPermissionControl(PermissionGrantedContent:  @Composable @UiComposable () -> Unit)