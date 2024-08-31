@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.xx2bab.mediapiper

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()