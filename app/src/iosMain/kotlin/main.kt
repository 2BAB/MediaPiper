import androidx.compose.ui.window.ComposeUIViewController
import me.xx2bab.mediapiper.App
import me.xx2bab.mediapiper.Startup
import me.xx2bab.mediapiper.llm.LLMOperatorFactory
import me.xx2bab.mediapiper.llm.LLMOperatorSwift
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

fun onStartup(llmInferenceDelegate: LLMOperatorSwift) {
    Startup.run { koinApp ->
        koinApp.apply {
            modules(module {
                single { LLMOperatorFactory(llmInferenceDelegate) }
            })
        }
    }
}
