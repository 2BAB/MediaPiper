/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.xx2bab.mediapiper.objectdetection.home.camera

import androidx.compose.runtime.Composable

// Here we have the camera view which is displayed in Home screen

// It's used to run object detection on live camera feed

// It takes as input the object detection options, and a function to update the inference time state

// You will notice we have a decorator that indicated we're using an experimental API for
// permissions, we're using it cause it's easy to check for permissions with it, and we need camera
// permission in this composable.
@Composable
fun CameraView(
    threshold: Float,
    maxResults: Int,
    delegate: Int,
    mlModel: Int,
    setInferenceTime: (newInferenceTime: Int) -> Unit,
) {
    CameraPermissionControl {
        CameraPreview(threshold, maxResults, delegate, mlModel, setInferenceTime) { result -> }
    }
}

