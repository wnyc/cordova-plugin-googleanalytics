package org.nypr.cordova.googleanalyticsplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.nypr.android.R;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.util.Log;

// Google Analytics Key set in analytics.xml

public class GoogleAnalyticsPlugin extends CordovaPlugin {

	protected static final String LOG_TAG = "GoogleAnalyticsPlugin";
	
	protected Tracker mTracker; 
	
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
			mTracker = myInstance.newTracker(R.xml.analytics);
		}
	}
	
	protected void _logScreenView(String screen) {
		Log.d(LOG_TAG, "Google Analytics logging screen view (" + screen + ")");
		if ( mTracker != null && screen != null ) {
			mTracker.setScreenName(screen);
			mTracker.send(new HitBuilders.AppViewBuilder().build());
		} else {
			Log.d(LOG_TAG, "GA Tracker not configured");
		}
	}
		
	protected void _trackEvent(String category, String action, String label, Long value){
		Log.d(LOG_TAG, "Google Analytics logging event (" + action + ")");
		if ( mTracker != null ) {
			
			mTracker.send(new HitBuilders.EventBuilder()
					.setCategory(category)
					.setAction(action)
					.setLabel(label)
					.setValue(value)
					.build());
			
		} else {
			Log.d(LOG_TAG, "GA Tracker not configured");
		}
	}
}
