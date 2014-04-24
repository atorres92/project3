package edu.msu.sparty.project3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LocationService extends Service {

    private LocationManager locationManager = null;
    private ActiveListener activeListener = new ActiveListener();
    
    private Location breslinLocation;
    private Location spartyLocation;
    private Location beaumontLocation;
    
    private double latitude = 0;
    private double longitude = 0;
        
    private boolean firstRun;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {        
    	firstRun = false;
    	
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	breslinLocation = new Location(LocationManager.GPS_PROVIDER);
    	breslinLocation.setLatitude(42.728173);
    	breslinLocation.setLongitude(-84.492248);
    	spartyLocation = new Location(LocationManager.GPS_PROVIDER);
    	spartyLocation.setLatitude(42.731138);
    	spartyLocation.setLongitude(-84.487508);
    	beaumontLocation = new Location(LocationManager.GPS_PROVIDER);
    	beaumontLocation.setLatitude(42.731951);
    	beaumontLocation.setLongitude(-84.482165);

    	Log.i("onCreate","onCreate");
        registerListeners(500);

    }

    @Override
    public void onDestroy() {
        unregisterListeners();
    }   
    
    private void registerListeners(int interval) {
    	
        unregisterListeners();
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        
        String bestAvailable = locationManager.getBestProvider(criteria, true);
        
        if(bestAvailable != null) {
            locationManager.requestLocationUpdates(bestAvailable, interval, 1, activeListener);
            /*
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            if( !firstRun ) {
                firstRun = true;
                onLocation(location);
            }
            */
        }
    }
    
    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }
    
    private void onLocation(Location location) {
        if(location == null) {
            return;
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i("LocationService.java: ", "current latitude: " + String.valueOf(latitude));
        Log.i("LocationService.java: ", "current longitude: " + String.valueOf(longitude));

        
        float [] breslinDistance = new float[1];
        float [] spartyDistance = new float[1];
        float [] beaumontDistance = new float[1];
        
        Location.distanceBetween(latitude, longitude, breslinLocation.getLatitude(), breslinLocation.getLongitude(), breslinDistance);
        Location.distanceBetween(latitude, longitude, spartyLocation.getLatitude(), spartyLocation.getLongitude(), spartyDistance);
        Location.distanceBetween(latitude, longitude, beaumontLocation.getLatitude(), beaumontLocation.getLongitude(), beaumontDistance);

        Log.i("Sparty: ", String.valueOf(spartyDistance[0]));
        Log.i("Breslin: ", String.valueOf(breslinDistance[0]));
        Log.i("Beaumont: ", String.valueOf(beaumontDistance[0]));
        
        float chosenDistance = Math.min(breslinDistance[0], Math.min(spartyDistance[0], beaumontDistance[0]));
        String dest = "";

        
        Log.i("dest; ", dest);
        
        Log.i("Logger:", "Chosen distance: " + String.valueOf(chosenDistance));
        if (chosenDistance < 100 ) {
        	unregisterListeners();
        	Log.i("LocationService.java: ", "Near landmark");
        	Intent intent = null;
            if ( chosenDistance == breslinDistance[0] ) {
                intent = new Intent(this, BreslinActivity.class);
            } else if (chosenDistance == spartyDistance[0]) {
                intent = new Intent(this, SpartyActivity.class);
            } else if (chosenDistance == beaumontDistance[0]) {
                intent = new Intent(this, BeaumontActivity.class);
            }
            intent.putExtra(MainActivity.LANDMARK, dest);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Notification notification = new Notification.Builder(this)
	            .setContentTitle("Arrived at " + dest)
	            .setContentText(dest).setSmallIcon(R.drawable.ic_launcher)
	            .setContentIntent(pIntent)
	            .setAutoCancel(true)
	            .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

        } else if (chosenDistance < 500) {
        	Log.i("LocationService.java: ", "Within 500 meters of landmark");
        	registerListeners(10000);
        } else if (chosenDistance < 1000) {
        	Log.i("LocationService.java: ", "Within 1000 meters of landmark");
        	registerListeners(30000);
        } else {
        	Log.i("LocationSErvice.java: ", "Far away");
        	registerListeners(60000);
        }
    }
    
    private class ActiveListener implements LocationListener {
    	    	
		@Override
		public void onLocationChanged(Location loc) {
			onLocation(loc);
		}

		@Override
		public void onProviderDisabled(String arg0) {
            registerListeners(500);
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

        
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}; 
}
