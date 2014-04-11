package org.nypr.cordova.googleanalyticsplugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.util.Log;

// Google Analytics Key set in analytics.xml

public class GoogleAnalyticsPlugin extends CordovaPlugin {

	protected static final String LOG_TAG = "GoogleAnalyticsPlugin";
	
	protected Tracker mTracker; 
	
	public GoogleAnalyticsPlugin() {
		Log.d(LOG_TAG, "Google Analytics Plugin constructed");
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    	EasyTracker.getInstance().activityStart(cordova.getActivity());
		super.initialize(cordova, webView);
		Log.d(LOG_TAG, "Google Analytics Plugin initialized");
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Google Analytics Plugin ending session");
		EasyTracker.getInstance().activityStop(cordova.getActivity());
		super.onDestroy();
	}

	@Override
	public void onReset() {
		Log.d(LOG_TAG, "Google Analytics Plugin onReset--WebView has navigated to new page or refreshed.");
		super.onReset();
	}
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		boolean ret=true;
		try {
			_setTracker();
			
			if(action.equalsIgnoreCase("logevent")){
        		String c = args.getString(0);
        		String a = args.getString(1);
        		String l = args.getString(2);
        		Long v = Long.valueOf(0);
        		if ( args.length() > 3) {
        			if ( args.get(3)!=null ) {
        				try {
							v = args.getLong(3);
						} catch (JSONException e) {
							v = Long.valueOf(0);
						}
        			}
        		}
				_trackEvent(c, a, l, v);	
				callbackContext.success();
			}else if (action.equalsIgnoreCase("logscreenview")){
				_logScreenView(args.getString(0));
				callbackContext.success();
			}else{
				callbackContext.error(LOG_TAG + " error: invalid action (" + action + ")");
				ret=false;
			}
		} catch (JSONException e) {
			callbackContext.error(LOG_TAG + " error: invalid json");
			ret = false;
		} catch (Exception e) {
			callbackContext.error(LOG_TAG + " error: " + e.getMessage());
			ret = false;
		}
		return ret;
	}
	
	protected void _setTracker() {
		if(mTracker==null && cordova.getActivity()!=null) {
			GoogleAnalytics myInstance = GoogleAnalytics.getInstance(cordova.getActivity());
			mTracker = myInstance.getDefaultTracker();
		}
	}
	
	protected void _logScreenView(String screen) throws JSONException {
		Log.d(LOG_TAG, "Google Analytics logging screen view (" + screen + ")");
		if ( mTracker != null && screen != null ) {
			mTracker.sendView(screen);
		} else {
			Log.d(LOG_TAG, "GA Tracker not configured");
		}
	}
		
	protected void _trackEvent(String category, String action, String label, Long value){
		Log.d(LOG_TAG, "Google Analytics logging event (" + action + ")");
		if ( mTracker != null ) {
			mTracker.sendEvent(category, action, label, value);
		} else {
			Log.d(LOG_TAG, "GA Tracker not configured");
		}
	}
}
