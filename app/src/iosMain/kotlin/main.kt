
import androidx.compose.ui.window.ComposeUIViewController
import me.xx2bab.mediapiper.App
import me.xx2bab.mediapiper.Startup
import me.xx2bab.mediapiper.llm.LLMOperatorFactory
import me.xx2bab.mediapiper.objectdetection.home.camera.IOSCameraPreviewCreator
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

fun onStartup(iosCameraPreviewCreator: IOSCameraPreviewCreator) {
    Startup.run { koinApp ->
        koinApp.apply {
            modules(module {
                single { LLMOperatorFactory() }
                single<IOSCameraPreviewCreator> { iosCameraPreviewCreator }
            })
        }
    }
}
