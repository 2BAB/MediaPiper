@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package me.xx2bab.mediapiper

import platform.Foundation.NSUUID


actual fun randomUUID(): String = NSUUID().UUIDString()