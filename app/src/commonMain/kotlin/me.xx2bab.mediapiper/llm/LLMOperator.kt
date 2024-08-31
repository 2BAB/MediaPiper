package me.xx2bab.mediapiper.llm

import kotlinx.coroutines.flow.Flow

// We suppose the extension is always .bin ,
// so we don't pass it to the program.
const val MODEL_NAME = "gemma-2b-it-gpu-int4"
//const val MODEL_NAME = "gemma-2b-it-cpu-int8"

const val MODEL_EXTENSION = "bin"

expect class LLMOperatorFactory {
    fun create(): LLMOperator
}

// 8.
interface LLMOperator {

    /**
     * To load the model into current context.
     * @return 1. null if it went well 2. an error message in string
     */
    suspend fun initModel(): String?

    /**
     * To calculate the token size of a string.
     */
    fun sizeInTokens(text: String): Int

    /**
     * To generate response for give inputText in synchronous way.
     */
    suspend fun generateResponse(inputText: String): String

    /**
     * To generate response for give inputText in asynchronous way.
     * @return A flow with partial response in String and completion flag in Boolean.
     */
    suspend fun generateResponseAsync(inputText: String): Flow<Pair<String, Boolean>>

}
