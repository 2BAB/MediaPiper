//
//  DefaultConstants.swift
//  iosApp
//
//  Created by 2BAB on 17/9/24.
//

import Foundation
import UIKit

// MARK: Define default constants
struct DefaultConstants {
  static let labelColors = [
    UIColor.red,
    UIColor(displayP3Red: 90.0/255.0, green: 200.0/255.0, blue: 250.0/255.0, alpha: 1.0),
    UIColor.green,
    UIColor.orange,
    UIColor.blue,
    UIColor.purple,
    UIColor.magenta,
    UIColor.yellow,
    UIColor.cyan,
    UIColor.brown
  ]
  static let displayFont = UIFont.systemFont(ofSize: 14.0, weight: .medium)
}

enum Model: Int, CaseIterable {
  case efficientdetLite0
  case efficientdetLite2
  
  var name: String {
    switch self {
    case .efficientdetLite0:
      return "EfficientDet-Lite0"
    case .efficientdetLite2:
      return "EfficientDet-Lite2"
    }
  }
  
  var modelPath: String? {
    switch self {
    case .efficientdetLite0:
      return Bundle.main.path(
        forResource: "efficientdet_lite0", ofType: "tflite")
    case .efficientdetLite2:
      return Bundle.main.path(
        forResource: "efficientdet_lite2", ofType: "tflite")
    }
  }
  
  init?(name: String) {
    switch name {
    case "EfficientDet-Lite0":
      self.init(rawValue: 0)
    case "EfficientDet-Lite2":
      self.init(rawValue: 1)
    default:
      return nil
    }
  }
  
}
