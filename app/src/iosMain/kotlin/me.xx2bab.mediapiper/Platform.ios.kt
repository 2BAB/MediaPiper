@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.xx2bab.mediapiper

import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UIKit.UIApplication

internal actual object Platform {
    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: throw IllegalArgumentException("Illegal url: $url")
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}

actual fun randomUUID(): String = NSUUID().UUIDString()