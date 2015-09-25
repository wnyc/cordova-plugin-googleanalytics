package org.nypr.cordova.googleanalyticsplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.nypr.android.R;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.Logger.LogLevel;

import android.util.Log;
import java.util.HashMap;

// Google Analytics Key set in analytics.xml

public class GoogleAnalyticsPlugin extends CordovaPlugin {

	protected static final String LOG_TAG = "GoogleAnalyticsPlugin";
	protected HashMap<String, Tracker> mTrackers;
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		boolean ret=true;
		try {
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

				Tracker tracker = getTrackerFromArguments(args, 4);

				_trackEvent(tracker, c, a, l, v);
				callbackContext.success();
			}else if (action.equalsIgnoreCase("logscreenview")){

				Tracker tracker = getTrackerFromArguments(args, 1);

				_logScreenView(tracker, args.getString(0));
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

    protected Tracker getTrackerFromArguments(JSONArray args, int index) throws JSONException {
        String trackerId = null;
        if ( args.length() > index) {
            if ( args.get(index)!=null && !args.getString(index).equals("null")) {
                try {
                    trackerId = args.getString(index);
                } catch (JSONException e) {
                    trackerId = null;
                }
            }
        }

        return getTrackerById(trackerId);
    }

    protected Tracker getTrackerById(String trackerId) {

        if (mTrackers == null) {
            mTrackers = new HashMap<String, Tracker>();

            GoogleAnalytics.getInstance(cordova.getActivity()).getLogger().setLogLevel(LogLevel.VERBOSE);
        }

        if (trackerId == null) {
            trackerId = "default";
        }

        if (!mTrackers.containsKey(trackerId)) {
            Tracker tracker;
            GoogleAnalytics googleAnalyticsInstance = GoogleAnalytics.getInstance(cordova.getActivity());
            if (trackerId.equals("default")) {
                tracker = googleAnalyticsInstance.newTracker(R.xml.analytics);
            } else {
                tracker = googleAnalyticsInstance.newTracker(trackerId);
            }
            mTrackers.put(trackerId, tracker);
        }

        return mTrackers.get(trackerId);
    }

	protected void _logScreenView(Tracker tracker, String screen) {
		Log.d(LOG_TAG, "Google Analytics logging screen view (" + screen + ")");
		if ( tracker != null && screen != null ) {
            tracker.setScreenName(screen);
            tracker.send(new HitBuilders.AppViewBuilder().build());
		} else {
			Log.d(LOG_TAG, "GA Tracker not configured");
		}
	}
		
	protected void _trackEvent(Tracker tracker, String category, String action, String label, Long value){
		Log.d(LOG_TAG, "Google Analytics logging event (" + action + ")");
		if ( tracker != null ) {

            tracker.send(new HitBuilders.EventBuilder()
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
