//
//  GoogleAnalyticsPlugin.h
//  NYPRNative
//
//  Created by Brad Kammin on 4/1/14.
//
//

#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>

@interface GoogleAnalyticsPlugin : CDVPlugin

- (void)logevent:(CDVInvokedUrlCommand*)command;
- (void)logscreenview:(CDVInvokedUrlCommand*)command;

@end
