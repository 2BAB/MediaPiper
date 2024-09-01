package me.xx2bab.mediapiper.llm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

// 6.
class AiRoute : Screen {

    @Composable
    override fun Content() {
        AiScreen()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AiScreen(llmOperator:LLMOperator = koinInject()) {
        val chatViewModel = rememberScreenModel { ChatViewModel(llmOperator) }
        var showLoading by rememberSaveable { mutableStateOf(true) }
        Column {
            TopAppBar(
                title = {
                    Text("AI Samples")
                },
                navigationIcon = {
                    val navigator = LocalNavigator.currentOrThrow
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White)
            ) {
                if (showLoading) {
                    LoadingScreen(llmOperator, onModelLoaded = {
                        showLoading = false
                    })
                } else {
                    ChatRoute(chatViewModel)
                }
            }
        }
    }
}


@Preview
@Composable
fun SimpleComposablePreview() {
    AiRoute()
}

