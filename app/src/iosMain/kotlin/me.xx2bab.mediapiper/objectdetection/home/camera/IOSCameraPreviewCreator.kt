package me.xx2bab.mediapiper.objectdetection.home.camera

import me.xx2bab.mediapiper.objectdetection.data.model.ObjectDetectionResult
import platform.UIKit.UIView

typealias IOSCameraPreviewCreator = ( threshold: Float,
                                      maxResults: Int,
                                      delegate: Int,
                                      mlModel: Int,
                                      setInferenceTime: (newInferenceTime: Int) -> Unit,
                                      callback: IOSCameraPreviewCallback) -> UIView
typealias IOSCameraPreviewCallback = (result: ObjectDetectionResult) -> Unit