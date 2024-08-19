import UIKit
import MediaPipeTasksGenAI
import app

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        
        do {
            let delegate = try LLMOperatorSwiftImpl()
            MainKt.onStartup(llmInferenceDelegate: delegate)
        } catch let error {
            print("Mediapipe GenAI Task SDK failed to init: \(error)")
        }
        
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }
        return true
    }
}
