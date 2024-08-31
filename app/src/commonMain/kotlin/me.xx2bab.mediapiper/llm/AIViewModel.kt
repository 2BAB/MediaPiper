package me.xx2bab.mediapiper.llm

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// 7.
class AIViewModel : ScreenModel, KoinComponent {

    private val coroutineScope = MainScope()
    private val llmOperator: LLMOperator by inject()

    override fun onDispose() {
        super.onDispose()
        coroutineScope.cancel()
    }

    suspend fun initLlmModel() = llmOperator.initModel()

    suspend fun generateResponse(message: String) = llmOperator.generateResponse(message)

    suspend fun generateResponseInflow(message: String) = llmOperator.generateResponseAsync(message)

}