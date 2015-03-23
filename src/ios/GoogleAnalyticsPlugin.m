//
//  GoogleAnalyticsPlugin.m
//

#import "GoogleAnalyticsPlugin.h"
#import "GAI.h"
#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"

static int const kGoogleAnalyticsPluginHeartbeatInterval= 60 * 5; // 5 minutes
static NSString *const kGoogleAnalyticsPluginDefaultCategory = @"Unknown";
static NSString *const kGoogleAnalyticsPluginHeartbeatAction = @"SessionHeartbeat";

@interface GoogleAnalyticsPlugin ()

@property BOOL useHeartbeat;
@property NSString *defaultCategory;
@property NSTimer *heartbeatTimer;

@end;

@implementation GoogleAnalyticsPlugin

#pragma mark Initialization

- (void)pluginInitialize {
    self.useHeartbeat = NO;
    self.defaultCategory = kGoogleAnalyticsPluginDefaultCategory;
    
    // Optional: automatically send uncaught exceptions to Google Analytics.
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    // Optional: set Google Analytics dispatch interval to e.g. 20 seconds.
    [GAI sharedInstance].dispatchInterval = 30;
    // Optional: uncomment for extra debugging information.
    //[[GAI sharedInstance].logger setLogLevel:kGAILogLevelVerbose];
    
    NSString *gaKey = @"__GA_KEY__";
    if( gaKey!=nil && [gaKey isEqualToString:@""]==NO && [gaKey rangeOfString:@"GA_KEY"].location == NSNotFound ){
        // Create tracker instance. This tracker can later be retreived by calling defaultTracker
        [[GAI sharedInstance] trackerWithTrackingId:gaKey];
        
        // manually start new GA session
        [self _startNewSession];
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_onResignActive) name:UIApplicationWillResignActiveNotification object:nil];
}

#pragma mark Cleanup

- (void)dispose {
    if (self.useHeartbeat) {
        [self _stopHeartbeat];
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillResignActiveNotification object:nil];
}

#pragma mark Plugin methods

- (void)setuseheartbeat:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    BOOL useHeartbeat = [[command.arguments objectAtIndex:0] boolValue];
    
    if (self.useHeartbeat != useHeartbeat) {
        self.useHeartbeat = useHeartbeat;
    
        if (self.useHeartbeat) {
            [self _startHeartbeat];
        }
        else {
            [self _stopHeartbeat];
        }
    }
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setdefaultcategory:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    NSString *defaultCategory = [command.arguments objectAtIndex:0];
    
    self.defaultCategory = defaultCategory;
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)logscreenview:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    NSString* screen = [command.arguments objectAtIndex:0];
    
    [self _logScreenView:screen];
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) logevent:(CDVInvokedUrlCommand *)command {
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

#pragma mark Heartbeat timer methods

- (void) _startHeartbeat {
    if (self.heartbeatTimer) {
        [self.heartbeatTimer invalidate];
    }

    // start the timer
    self.heartbeatTimer = [NSTimer scheduledTimerWithTimeInterval: kGoogleAnalyticsPluginHeartbeatInterval
                                                           target: self
                                                         selector: @selector(_heartbeat)
                                                         userInfo: nil
                                                          repeats: YES];
    // send initial heartbeat
    [self _heartbeat];
}

- (void) _stopHeartbeat {
    if (self.heartbeatTimer) {
        if ([self.heartbeatTimer isValid]) {
            // send final heartbeat
            [self _heartbeat];
        }

        [self.heartbeatTimer invalidate];
        self.heartbeatTimer = nil;
    }
}

- (void) _heartbeat {
    [self _logGAEvent:self.defaultCategory action:kGoogleAnalyticsPluginHeartbeatAction label:nil value:0];
}

#pragma App state change handlers

- (void)onAppTerminate {
    [[GAI sharedInstance] dispatch];
}

- (void)_onResignActive {
    [[GAI sharedInstance] dispatch];
}

#pragma mark Session methods

- (void)_startNewSession {
    id<GAITracker> tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAISessionControl value:@"start"];
    [tracker set:kGAISessionControl value:nil];
}


@end
