var exec = require("cordova/exec");

/**
 * This is a global variable called exposed by cordova
 */    
var GoogleAnalytics = function(){};

GoogleAnalytics.prototype.setDefaultCategory = function(category, success, error) {
  exec(success, error, "GoogleAnalyticsPlugin", "setdefaultcategory", [category]);
};

GoogleAnalytics.prototype.setUseHeartbeat = function(useHeartbeat, success, error) {
  exec(success, error, "GoogleAnalyticsPlugin", "setuseheartbeat", [useHeartbeat]);
};

GoogleAnalytics.prototype.logevent = function(success, error, category, action, label, value) {
  exec(success, error, "GoogleAnalyticsPlugin", "logevent", [category, action, label, value]);
};

GoogleAnalytics.prototype.logscreenview = function(success, error, screen) {
  exec(success, error, "GoogleAnalyticsPlugin", "logscreenview", [screen]);
};

module.exports = new GoogleAnalytics();
