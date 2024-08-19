package me.xx2bab.mediapiper.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
internal actual fun SystemAppearance(isLight: Boolean) {
    LaunchedEffect(isLight) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (isLight) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        )
    }
}