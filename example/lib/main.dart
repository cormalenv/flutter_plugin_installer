
import 'package:flutter/material.dart';
import 'package:installer/installer.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _appUrl = "com.cormal.installer_example";
  String _apkFilePath = "/storage/emulated/0/Download/Field_Manager_v2.0.0+3.apk";
  TextEditingController _tec1;
  TextEditingController _tec2;

  @override
  void initState() {
    super.initState();
    _tec1 = new TextEditingController(text: _apkFilePath);
    _tec2 = new TextEditingController(text: _appUrl);
  }
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
          ),
        body: new Column(
          children: <Widget>[
            TextField(
              controller: _tec1,
              decoration: InputDecoration(
                  hintText:
                  'apk file path to install. Like /storage/emulated/0/demo/update.apk'),
              onChanged: (path) => _apkFilePath = path,
              ),
            FlatButton(
                onPressed: () {
                  onClickInstallApk();
                },
                child: Text('install')),
            TextField(
              controller: _tec2,
              decoration:
              InputDecoration(hintText: 'URL for app store to launch'),
              onChanged: (url) => _appUrl = url,
              ),
            FlatButton(
                onPressed: () => onClickGotoAppStore(_appUrl),
                child: Text('gotoAppStore'))
          ],
          ),
        ),
      );
  }

  void onClickInstallApk() async {
    if (_apkFilePath.isEmpty) {
      print('make sure the apk file is set');
      return;
    }
    Map<PermissionGroup, PermissionStatus> permissions =
    await PermissionHandler().requestPermissions([PermissionGroup.storage]);
    if (permissions[PermissionGroup.storage] == PermissionStatus.granted) {
      Installer.installApk(_apkFilePath)
          .then((result) {
        print('install apk $result');
      }).catchError((error) {
        print('install apk error: $error');
      });
    } else {
      print('Permission request fail!');
    }
  }

  void onClickGotoAppStore(String url) {
    url = url.isEmpty
        ? 'https://itunes.apple.com/cn/app/'
        : url;
    Installer.gotoAppStore(url);
  }
}
