package me.xx2bab.mediapiper.llm

import cafe.adriel.voyager.core.model.ScreenModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// 7.
class LLMViewModel : ScreenModel, KoinComponent {

    private val llmOperator: LLMOperator by inject()

    suspend fun initLlmModel() = llmOperator.initModel()

    suspend fun generateResponse(message: String) = llmOperator.generateResponse(message)

    suspend fun generateResponseInflow(message: String) = llmOperator.generateResponseAsync(message)

}