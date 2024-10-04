package me.xx2bab.mediapiper.llm

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

actual class LLMOperatorFactory() {
    actual fun create(): LLMOperator = object : LLMOperator {
        override suspend fun initModel(): String? {
            return null
        }

        override fun sizeInTokens(text: String): Int {
            return 0
        }

        override suspend fun generateResponse(inputText: String): String {
            return ""
        }

        override suspend fun generateResponseAsync(inputText: String): Flow<Pair<String, Boolean>> {
            return flow {  }
        }
    }
}

//actual class LLMOperatorFactory(private val llmInferenceDelegate: LLMOperatorSwift) {
//    actual fun create(): LLMOperator = LLMOperatorIOSImpl(llmInferenceDelegate)
//}

//// FIXME: MPPLLMInference throws NPE during initialization stage without detailed stacktrace
//@OptIn(ExperimentalForeignApi::class)
//class LLMOperatorIOSImpl: LLMOperator {
//
//    private val inference: MPPLLMInference
//
//    init {
//        val modelPath = NSBundle.mainBundle.pathForResource("gemma-2b-it-gpu-int4", "bin")
//
//        val options = MPPLLMInferenceOptions(modelPath!!)
//        options.setModelPath(modelPath!!)
//        options.setMaxTokens(2048)
//        options.setTopk(40)
//        options.setTemperature(0.8f)
//        options.setRandomSeed(102)
//
//        inference = MPPLLMInference(options, null) // NPE throws here!!
//    }
//
//    override fun sizeInTokens(text: String): Int {
//        return 0
//    }
//
//    override fun generateResponse(inputText: String): String {
//        return inference.generateResponseWithInputText(inputText, null)!!
//    }
//
//    override fun generateResponseAsync(inputText: String, ...) {
//        // inference.generateResponseAsyncWithInputText(...)
//    }
//
//}


class LLMOperatorIOSImpl(private val delegate: LLMOperatorSwift) : LLMOperator {

    private val initialized = atomic<Boolean>(false)

    override suspend fun initModel(): String? {
        if (initialized.value) {
            return null
        }
        return try {
            delegate.loadModel(MODEL_NAME)
            initialized.value = true
            null
        } catch (e: Exception) {
            e.message
        }
    }

    override fun sizeInTokens(text: String): Int = -1 // TODO

    override suspend fun generateResponse(inputText: String): String {
        if (initialized.value.not()) {
            throw IllegalStateException("LLMInference is not initialized properly")
        }
        return delegate.generateResponse(inputText)
    }

    override suspend fun generateResponseAsync(inputText: String): Flow<Pair<String, Boolean>> {
        if (initialized.value.not()) {
            throw IllegalStateException("LLMInference is not initialized properly")
        }
        val partialResultsFlow = MutableSharedFlow<Pair<String, Boolean>>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        delegate.generateResponseAsync(inputText, { partialResponse ->
            partialResultsFlow.tryEmit(partialResponse to false)
        }, { completion ->
            partialResultsFlow.tryEmit(completion to true)
        })
        return partialResultsFlow.asSharedFlow()
    }

}

interface LLMOperatorSwift {
    suspend fun loadModel(modelName: String)
    fun sizeInTokens(text: String): Int
    suspend fun generateResponse(inputText: String): String
    suspend fun generateResponseAsync(
        inputText: String,
        progress: (partialResponse: String) -> Unit,
        completion: (completeResponse: String) -> Unit
    )
}