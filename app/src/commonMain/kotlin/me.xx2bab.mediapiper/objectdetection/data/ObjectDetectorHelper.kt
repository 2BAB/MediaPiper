package me.xx2bab.mediapiper.objectdetection.data

import me.xx2bab.mediapiper.objectdetection.data.model.ObjectDetectionResult

object ObjectDetectorHelper {

    const val DELEGATE_CPU = 0
    const val DELEGATE_GPU = 1
    const val MODEL_EFFICIENTDETV0 = 0
    const val MODEL_EFFICIENTDETV2 = 1
    const val MAX_RESULTS_DEFAULT = 3
    const val THRESHOLD_DEFAULT = 0.5F
    const val OTHER_ERROR = 0
    const val GPU_ERROR = 1

    const val TAG = "ObjectDetectorHelper"

    data class ResultBundle(
        val results: List<ObjectDetectionResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    // Used to pass results or errors back to the calling class
    interface DetectorListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }

}