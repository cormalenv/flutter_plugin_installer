package io.flutter.plugins.installer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** InstallerPlugin */
public class InstallerPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;
  private int installRequestCode = 1234;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "installer");
    channel.setMethodCallHandler(this);
  }
    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    if (registrar.activity() == null) {
      return;
    }
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "installer");
    final InstallerPlugin _plugin =  new InstallerPlugin();
    _plugin.setup(registrar.activity());
    channel.setMethodCallHandler(_plugin);
  }

  public InstallerPlugin() {}

  private void setup(Activity _activity){
    this.activity = _activity;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("installApk")) {
      String filePath = call.argument("filePath");
      String appId = call.argument("appId");
      try {
        installApk(activity, filePath);
        result.success("Success");
      } catch (Exception e) {
        result.error(e.getClass().getSimpleName(), e.getMessage(), null);
      }
    }
    else if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public static void installApk(Activity activity, String filePath) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(uriFromFile(activity, new File(filePath)), "application/vnd.android.package-archive");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
      Log.e("TAG", "Error in opening the file!");
    }
  }

  private static Uri uriFromFile(Activity activity, File file) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return FileProvider.getUriForFile(activity, activity.getPackageName() + ".installer_provider", file);
    } else {
      return Uri.fromFile(file);
    }
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}
