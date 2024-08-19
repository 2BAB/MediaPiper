package me.xx2bab.mediapiper

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import me.xx2bab.mediapiper.llm.LLMOperatorFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module



class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        Startup.run { koinApp ->
            koinApp.apply {
                modules(androidModule)
                androidContext(this@AndroidApp)
            }
        }
        INSTANCE = this
    }
}

val androidModule = module {
    single { LLMOperatorFactory(androidContext()) }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalLifecycleOwner provides this
            ) {
                App()
            }
        }
    }
}
