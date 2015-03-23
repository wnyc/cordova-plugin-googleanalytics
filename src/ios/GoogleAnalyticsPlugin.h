//
//  GoogleAnalyticsPlugin.h
//

#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>

@interface GoogleAnalyticsPlugin : CDVPlugin

- (void)setuseheartbeat:(CDVInvokedUrlCommand *)command;
- (void)setdefaultcategory:(CDVInvokedUrlCommand *)command;
- (void)logevent:(CDVInvokedUrlCommand *)command;
- (void)logscreenview:(CDVInvokedUrlCommand *)command;

@end
