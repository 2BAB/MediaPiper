@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.xx2bab.mediapiper

internal expect object Platform {
    fun openUrl(url: String)
}

expect fun randomUUID(): String