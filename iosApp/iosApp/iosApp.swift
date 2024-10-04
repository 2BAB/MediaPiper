import UIKit
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
//            let delegate = try LLMOperatorSwiftImpl()
            MainKt.onStartup(iosCameraPreviewCreator: { threshold, maxResults, delegate, mlModel, onInferenceTimeUpdate, resultCallback in
                return IOSCameraView.init(frame: CGRectMake(0, 0, 0, 0),
                                          modelName: Int(truncating: mlModel) == 0 ? "EfficientDet-Lite0" : "EfficientDet-Lite2",
                                          maxResults: Int(truncating: maxResults),
                                          scoreThreshold: Float(truncating: threshold),
                                          onInferenceTimeUpdate: onInferenceTimeUpdate,
                                          resultCallback: resultCallback
                                         )
            })
        } catch let error {
            print("Mediapipe GenAI Task SDK failed to init: \(error)")
        }
        
        if let window = window {
            let controller = MainKt.MainViewController()
            window.rootViewController = controller
            window.makeKeyAndVisible()
        }
        return true
    }
}
