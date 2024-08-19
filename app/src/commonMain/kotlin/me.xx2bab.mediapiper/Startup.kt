package me.xx2bab.mediapiper

import me.xx2bab.mediapiper.llm.LLMOperator
import me.xx2bab.mediapiper.llm.LLMOperatorFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

// 3.
object Startup {
	fun run(platformSpecifiedKoinInitBlock: (koin: KoinApplication) -> Unit) {
		Napier.base(DebugAntilog())
		startKoin {
			modules(sharedModule)
			platformSpecifiedKoinInitBlock(this)
		}
	}
}

val sharedModule = module {
	single<LLMOperator> { get<LLMOperatorFactory>().create() }
}