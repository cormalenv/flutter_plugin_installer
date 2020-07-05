import 'dart:async';

import 'package:flutter/services.dart';

class Installer {

  static const MethodChannel _channel = const MethodChannel('installer');

  /// for Android : install apk by its file absolute path;
  static Future<String> installApk(String filePath) async {
    Map<String, String> params = {'filePath': filePath};
    return await _channel.invokeMethod('installApk', params);
  }

  /// for iOS: go to app store by the url
  static Future<String> gotoAppStore(String urlString) async {
    Map<String, String> params = {'urlString': urlString};
    return await _channel.invokeMethod('gotoAppStore', params);
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}

