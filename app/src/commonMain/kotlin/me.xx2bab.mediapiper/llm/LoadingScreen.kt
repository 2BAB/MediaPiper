package me.xx2bab.mediapiper.llm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import mediapiper.app.generated.resources.Res
import mediapiper.app.generated.resources.loading_model

@Composable
internal fun LoadingScreen(
    llmOperator: LLMOperator,
    onModelLoaded: () -> Unit = { }
) {
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        if (errorMessage != "") {
            ErrorMessage(errorMessage)
        } else {
            LoadingIndicator()
        }
    }

    LaunchedEffect(Unit) {
        // Create the LlmInference in a separate thread
        withContext(Dispatchers.IO) {
            val res = llmOperator.initModel()
            if (res == null) {
                withContext(Dispatchers.Main) {
                    onModelLoaded()
                }
                Napier.i("model is loaded")
            } else {
                errorMessage = "Model loaded Error: $res"
                Napier.e("model failed to load: $res")
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.loading_model),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}
