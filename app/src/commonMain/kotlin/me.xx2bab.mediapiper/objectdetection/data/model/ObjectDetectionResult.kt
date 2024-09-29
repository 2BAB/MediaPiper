package me.xx2bab.mediapiper.objectdetection.data.model

data class ObjectDetectionResult(
    val timestampMs: Long = 0L,
    val detections: List<Detection>?
)

data class Detection(
    val categories: List<Category>,
    val boundingBox: RectF
)

data class Category(
    val score: Float = 0f,
    val index: Int = -1,
    val categoryName: String = "",
    val displayName: String = ""
)

data class RectF(
    val bottom: Float = 0f,
    val left: Float = 0f,
    val right: Float = 0f,
    val top: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f
)