// CameraView.swift

// Copyright 2023 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");

import UIKit
import AVFoundation
import MediaPipeTasksVision
import app

/**
 * The custom view is responsible for performing detection on incoming frames from the live camera and presenting the frames with the
 * bounding boxes of the detected objects to the user.
 */
class IOSCameraView: UIView {
    
    private struct Constants {
        static let edgeOffset: CGFloat = 2.0
    }
    
    private var previewView: UIView!
    private var cameraUnavailableLabel: UILabel!
    private var resumeButton: UIButton!
    private var overlayView: OverlayView!
    
    private var isSessionRunning = false
    private var isObserving = false
    private let backgroundQueue = DispatchQueue(label: "com.google.mediapipe.cameraController.backgroundQueue")
    private var lastInferenceTime = 0
    
    // MARK: Controllers that manage functionality
    // Handles all the camera related functionality
    private var cameraFeedService: CameraFeedService!
    
    private let objectDetectorServiceQueue = DispatchQueue(
        label: "com.google.mediapipe.cameraController.objectDetectorServiceQueue",
        attributes: .concurrent)
    
    // Queuing reads and writes to objectDetectorService using the Apple recommended way
    // as they can be read and written from multiple threads and can result in race conditions.
    private var _objectDetectorService: ObjectDetectorService?
    private var objectDetectorService: ObjectDetectorService? {
        get {
            objectDetectorServiceQueue.sync {
                return self._objectDetectorService
            }
        }
        set {
            objectDetectorServiceQueue.async(flags: .barrier) {
                self._objectDetectorService = newValue
            }
        }
    }
    
    private var model: Model = .efficientdetLite0
    private var maxResults: Int = 3
    private var scoreThreshold: Float = 0.2
    private var onInferenceTimeUpdate: (KotlinInt) -> KotlinUnit
    private var callback: (ObjectDetectionResult) -> KotlinUnit
    
    private weak var liveStreamDelegate: ObjectDetectorServiceLiveStreamDelegate?
    private var delegate: Delegate = .CPU
    
    // MARK: - Dedicated Initializer
    init(
        frame: CGRect,
        modelName: String,
        maxResults: Int,
        scoreThreshold: Float,
        onInferenceTimeUpdate: @escaping (KotlinInt) -> KotlinUnit,
        resultCallback: @escaping (ObjectDetectionResult) -> KotlinUnit
    ) {
        
        self.model = Model(name: modelName)!
        self.maxResults = maxResults
        self.scoreThreshold = scoreThreshold
        self.onInferenceTimeUpdate = onInferenceTimeUpdate
        self.callback = resultCallback
        
        super.init(frame: frame)
        
        self.setupView()
        self.initializeObjectDetectorServiceOnSessionResumption()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Setup View
    private func setupView() {
        // Initialize and add subviews
        previewView = UIView()
        previewView.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(previewView)
        
        cameraUnavailableLabel = UILabel()
        cameraUnavailableLabel.translatesAutoresizingMaskIntoConstraints = false
        cameraUnavailableLabel.text = "Camera Unavailable"
        cameraUnavailableLabel.textAlignment = .center
        cameraUnavailableLabel.isHidden = true
        self.addSubview(cameraUnavailableLabel)
        
        resumeButton = UIButton(type: .system)
        resumeButton.translatesAutoresizingMaskIntoConstraints = false
        resumeButton.setTitle("Resume", for: .normal)
        resumeButton.addTarget(self, action: #selector(onClickResume(_:)), for: .touchUpInside)
        resumeButton.isHidden = true
        self.addSubview(resumeButton)
        
        overlayView = OverlayView()
        overlayView.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(overlayView)
        
        // Set up constraints
        NSLayoutConstraint.activate([
            previewView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            previewView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            previewView.topAnchor.constraint(equalTo: self.topAnchor),
            previewView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            
            cameraUnavailableLabel.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            cameraUnavailableLabel.centerYAnchor.constraint(equalTo: self.centerYAnchor),
            
            resumeButton.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            resumeButton.topAnchor.constraint(equalTo: cameraUnavailableLabel.bottomAnchor, constant: 8),
            
            overlayView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            overlayView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            overlayView.topAnchor.constraint(equalTo: self.topAnchor),
            overlayView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        ])
        
        // Initialize cameraFeedService with previewView
        self.cameraFeedService = CameraFeedService(previewView: previewView)
        self.cameraFeedService.delegate = self
    }
    
    // MARK: - UIView Lifecycle Methods
    override func didMoveToWindow() {
        super.didMoveToWindow()
#if !targetEnvironment(simulator)
        if self.window != nil {
            // The view was added to a window, start the session
            initializeObjectDetectorServiceOnSessionResumption()
            cameraFeedService.startLiveCameraSession { [weak self] cameraConfiguration in
                DispatchQueue.main.async {
                    switch cameraConfiguration {
                    case .failed:
                        self?.presentVideoConfigurationErrorAlert()
                    case .permissionDenied:
                        self?.presentCameraPermissionsDeniedAlert()
                    default:
                        break
                    }
                }
            }
        } else {
            // The view was removed from a window, stop the session
            cameraFeedService.stopSession()
            clearObjectDetectorServiceOnSessionInterruption()
        }
#endif
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        cameraFeedService.updateVideoPreviewLayer(toFrame: previewView.bounds)
    }
    
    // Resume camera session when click button resume
    @objc func onClickResume(_ sender: Any) {
        cameraFeedService.resumeInterruptedSession { [weak self] isSessionRunning in
            if isSessionRunning {
                self?.resumeButton.isHidden = true
                self?.cameraUnavailableLabel.isHidden = true
                self?.initializeObjectDetectorServiceOnSessionResumption()
            }
        }
    }
    
    override func observeValue(
        forKeyPath keyPath: String?,
        of object: Any?, change: [NSKeyValueChangeKey : Any]?,
        context: UnsafeMutableRawPointer?) {
            clearAndInitializeObjectDetectorService()
        }
    
    private func presentCameraPermissionsDeniedAlert() {
        let alertController = UIAlertController(
            title: "Camera Permissions Denied",
            message:
                "Camera permissions have been denied for this app. You can change this by going to Settings",
            preferredStyle: .alert)
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: nil)
        let settingsAction = UIAlertAction(title: "Settings", style: .default) { (action) in
            UIApplication.shared.open(
                URL(string: UIApplication.openSettingsURLString)!, options: [:], completionHandler: nil)
        }
        alertController.addAction(cancelAction)
        alertController.addAction(settingsAction)
        
        if let topController = UIApplication.shared.topMostViewController() {
            topController.present(alertController, animated: true, completion: nil)
        }
    }
    
    private func presentVideoConfigurationErrorAlert() {
        let alert = UIAlertController(
            title: "Camera Configuration Failed",
            message: "There was an error while configuring camera.",
            preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        
        if let topController = UIApplication.shared.topMostViewController() {
            topController.present(alert, animated: true, completion: nil)
        }
    }
    
    private func initializeObjectDetectorServiceOnSessionResumption() {
        clearAndInitializeObjectDetectorService()
        //    startObserveConfigChanges()
    }
    
    @objc private func clearAndInitializeObjectDetectorService() {
        objectDetectorService = nil
        objectDetectorService = ObjectDetectorService
            .liveStreamDetectorService(
                model: self.model,
                maxResults: self.maxResults,
                scoreThreshold: self.scoreThreshold,
                liveStreamDelegate: self,
                delegate: self.delegate)
    }
    
    private func clearObjectDetectorServiceOnSessionInterruption() {
        //    stopObserveConfigChanges()
        objectDetectorService = nil
    }
    
}

// MARK: - CameraFeedServiceDelegate
extension IOSCameraView: CameraFeedServiceDelegate {
    
    func didOutput(sampleBuffer: CMSampleBuffer, orientation: UIImage.Orientation) {
        let currentTimeMs = Date().timeIntervalSince1970 * 1000
        // Pass the pixel buffer to MediaPipe
        backgroundQueue.async { [weak self] in
            self?.objectDetectorService?.detectAsync(
                sampleBuffer: sampleBuffer,
                orientation: orientation,
                timeStamps: Int(currentTimeMs))
        }
    }
    
    // MARK: Session Handling Alerts
    func sessionWasInterrupted(canResumeManually resumeManually: Bool) {
        // Updates the UI when session is interrupted.
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            if resumeManually {
                self.resumeButton.isHidden = false
            } else {
                self.cameraUnavailableLabel.isHidden = false
            }
            self.clearObjectDetectorServiceOnSessionInterruption()
        }
    }
    
    func sessionInterruptionEnded() {
        // Updates UI once session interruption has ended.
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.cameraUnavailableLabel.isHidden = true
            self.resumeButton.isHidden = true
            self.initializeObjectDetectorServiceOnSessionResumption()
        }
    }
    
    func didEncounterSessionRuntimeError() {
        // Handles session run time error by updating the UI and providing a button if session can be
        // manually resumed.
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.resumeButton.isHidden = false
            self.clearObjectDetectorServiceOnSessionInterruption()
        }
    }
}

// MARK: - ObjectDetectorServiceLiveStreamDelegate
extension IOSCameraView: ObjectDetectorServiceLiveStreamDelegate {
    
    func objectDetectorService(
        _ objectDetectorService: ObjectDetectorService,
        didFinishDetection result: ResultBundle?,
        error: Error?) {
            DispatchQueue.main.async { [weak self] in
                guard let self = self else {
                    return
                }
            
                guard let objectDetectorResult =
                        result?.objectDetectorResults.first as? ObjectDetectorResult else {
                    return
                }
                let imageSize = self.cameraFeedService.videoResolution
                
                let inferenceTime = objectDetectorResult.timestampInMilliseconds - lastInferenceTime
                lastInferenceTime = objectDetectorResult.timestampInMilliseconds
                self.onInferenceTimeUpdate(KotlinInt(integerLiteral: Int(inferenceTime > 10000 ? 0 : inferenceTime)))
                
                let commonDetections = objectDetectorResult.detections.map { de in
                    let commonCategories = de.categories.map { ca in
                        return Category(
                            score: ca.score,
                            index: Int32(ca.index),
                            categoryName: ca.categoryName ?? "",
                            displayName: ca.displayName ?? ""
                        )
                    }
                    let bb = de.boundingBox
                    let commonBoundingBox = RectF(bottom: Float(bb.maxY),
                                                  left: Float(bb.minX),
                                                  right: Float(bb.maxX),
                                                  top: Float(bb.minY),
                                                  width: Float(bb.width),
                                                  height: Float(bb.height))
                    return Detection(categories: commonCategories, boundingBox: commonBoundingBox)
                }
                
                let commonResult = ObjectDetectionResult(timestampMs: Int64(objectDetectorResult.timestampInMilliseconds),
                                                         detections: commonDetections)
                callback(commonResult)
                
                // Draw the overlay rectangles
                self.overlayView.draw(
                    objectOverlays: OverlayView.objectOverlays(
                        fromDetections: objectDetectorResult.detections,
                        inferredOnImageOfSize: imageSize,
                        andOrientation:  UIImage.Orientation.from(
                            deviceOrientation: UIDevice.current.orientation)),
                    inBoundsOfContentImageOfSize: imageSize,
                    edgeOffset: Constants.edgeOffset,
                    imageContentMode: self.cameraFeedService.videoGravity.contentMode)
            }
        }
}

// MARK: - UIImage Orientation Extension
extension UIImage.Orientation {
    static func from(deviceOrientation: UIDeviceOrientation) -> UIImage.Orientation {
        switch deviceOrientation {
        case .portrait:
            return .up
        case .landscapeLeft:
            return .left
        case .landscapeRight:
            return .right
        default:
            return .up
        }
    }
}

// MARK: - AVLayerVideoGravity Extension
extension AVLayerVideoGravity {
    var contentMode: UIView.ContentMode {
        switch self {
        case .resizeAspectFill:
            return .scaleAspectFill
        case .resizeAspect:
            return .scaleAspectFit
        case .resize:
            return .scaleToFill
        default:
            return .scaleAspectFill
        }
    }
}

// MARK: - UIApplication Extension to Get Top Most ViewController
extension UIApplication {
    func topMostViewController() -> UIViewController? {
        guard let window = self.connectedScenes
            .filter({$0.activationState == .foregroundActive})
            .compactMap({$0 as? UIWindowScene})
            .first?.windows
            .filter({$0.isKeyWindow}).first,
              var topController = window.rootViewController else {
            return nil
        }
        while let presentedViewController = topController.presentedViewController {
            topController = presentedViewController
        }
        return topController
    }
}
