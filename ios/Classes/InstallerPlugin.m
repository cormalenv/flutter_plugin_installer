#import "InstallerPlugin.h"
#if __has_include(<installer/installer-Swift.h>)
#import <installer/installer-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "installer-Swift.h"
#endif

@implementation InstallerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftInstallerPlugin registerWithRegistrar:registrar];
}
@end
