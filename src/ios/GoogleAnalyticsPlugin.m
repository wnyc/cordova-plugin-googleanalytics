//
//  GoogleAnalyticsPlugin.m
//

#import "GoogleAnalyticsPlugin.h"
#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

@implementation GoogleAnalyticsPlugin

#pragma mark Initialization

- (void)pluginInitialize
{
    // Optional: automatically send uncaught exceptions to Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    // Optional: set Google Analytics dispatch interval to e.g. 20 seconds.
    [GAI sharedInstance].dispatchInterval = 30;
    // Optional: set debug to YES for extra debugging information.
    //[GAI sharedInstance].debug = YES;
    
    NSString * gaKey = @"__GA_KEY__";
    if( gaKey!=nil && [gaKey isEqualToString:@""]==NO && [gaKey rangeOfString:@"GA_KEY"].location == NSNotFound ){
        // Create tracker instance. This tracker can later be retreived by calling defaultTracker
        [[GAI sharedInstance] trackerWithTrackingId:gaKey];
    }
    
    NSLog(@"Google Analytics Plugin initialized");
}

#pragma mark Plugin methods

- (void)logscreenview:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* screen = [command.arguments objectAtIndex:0];
    
    NSLog (@"Google Analytics logging screen: (%@)", screen );
    
    [self _logScreenView:screen];
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) logevent:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    if( [command.arguments count] < 3 ) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"GA events require at least a Category string, an Action string, and a Label string."];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    NSString * category = [command.arguments objectAtIndex:0];
    NSString * action = [command.arguments objectAtIndex:1];
    NSString * label = [command.arguments objectAtIndex:2];
    NSNumber * value = [NSNumber numberWithInt:0];
    
    NSLog (@"Google Analytics logging event: (%@)", action );
    
    if( [command.arguments count] > 3 ) {
        NSString * valueString = [command.arguments objectAtIndex:3];
        if ( valueString != (id)[NSNull null] ) {
            value = [NSNumber numberWithInt:[valueString intValue]];
        }
    }
    [self _logGAEvent:category
                           action:action
                            label:label
                            value:value];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)_logGAEvent:(NSString *)category action:(NSString *)action label:(NSString *)label value:(NSNumber *)value {
    id<GAITracker> tracker = [[GAI sharedInstance] defaultTracker];
    [tracker send:[[GAIDictionaryBuilder createEventWithCategory:category action:action label:label value:value] build]];
}

- (void)_logScreenView:(NSString*) screen {
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName value:screen];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
    [tracker set:kGAIScreenName value:nil];
}

@end
