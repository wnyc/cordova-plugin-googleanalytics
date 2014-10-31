# Google Analytics PhoneGap/Cordova Plugin

### Platform Support

This plugin supports PhoneGap/Cordova apps running on both iOS and Android.

### Version Requirements

This plugin is meant to work with Cordova 3.5.0+ and the latest version of the GoogleAnalytics library.

Google Analytics documentation and integration guides for IOS and Android:  
https://developers.google.com/analytics/devguides/collection/ios/v2/  
https://developers.google.com/analytics/devguides/collection/android/v2/  

TODO - update plugin to latest SDK versions  

## Installation

#### Automatic Installation using PhoneGap/Cordova CLI (iOS and Android)
1. Make sure you update your projects to Cordova iOS version 3.5.0+ before installing this plugin.

        cordova platform update ios
        cordova platform update android

2. Install this plugin using PhoneGap/Cordova cli:

        cordova local plugin add https://github.com/wnyc/cordova-plugin-googleanalytics.git

3. For iOS, modify GoogleAnalyticsPlugin.m, replacing with your configuration setting:
     
        NSString * gaKey = @"__GA_KEY__";

   For Android, modify analytics.xml, replacing with your configuration setting:
     
        <string name="ga_trackingId">__GA_KEY__</string> 

   Todo: pull GA key from configuration setting

## Usage

    // log an event  
    window.googleanalytics.logevent(  successCallback, failureCallback, category, action, label, value );

    // log a screen view  
    window.googleanalytics.logscreenview(  successCallback, failureCallback, screen );

