//
//  MediapipeLLMInferenceDelegate.swift
//  iosApp
//
//  Created by 2BAB on 20/6/24.
//

import Foundation
import MediaPipeTasksGenAI
import app


class LLMOperatorSwiftImpl: LLMOperatorSwift {
    
    let errorTag = "Mediapipe-LLM-Tasks-iOS: "
    var llmInference: LlmInference?
    
    
    func loadModel(modelName: String) async throws {
        let path = Bundle.main.path(forResource: modelName, ofType: "bin")!
        let llmOptions =  LlmInference.Options(modelPath: path)
        llmOptions.maxTokens = 4096
        llmOptions.temperature = 0.9
        
        llmInference = try LlmInference(options: llmOptions)
    }
    
    func generateResponse(inputText: String) async throws -> String {
        return try llmInference!.generateResponse(inputText: inputText)
    }
    
    func generateResponseAsync(inputText: String, progress: @escaping (String) -> Void, completion: @escaping (String) -> Void) async throws {
        try llmInference!.generateResponseAsync(inputText: inputText) { partialResponse, error in
            // progress
            if let e = error {
                print("\(self.errorTag) \(e)")
                completion(e.localizedDescription)
                return
            }
            if let partial = partialResponse {
                progress(partial)
            }
        } completion: {
            completion("")
        }
    }
    
    func sizeInTokens(text: String) -> Int32 {
        return 0
    }
    
}
