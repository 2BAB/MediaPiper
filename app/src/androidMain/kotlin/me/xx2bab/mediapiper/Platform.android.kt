@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.xx2bab.mediapiper

import android.content.Intent
import android.net.Uri
import java.util.UUID

internal actual object Platform {
    actual fun openUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AndroidApp.INSTANCE.startActivity(intent)
    }
}

actual fun randomUUID(): String = UUID.randomUUID().toString()