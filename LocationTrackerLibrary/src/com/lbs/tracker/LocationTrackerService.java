package com.lbs.tracker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.lbs.tracker.db.adapter.LBSDBAdapter;

public class LocationTrackerService {
	private static LocationTrackerService locationTrackerService = new LocationTrackerService();
	private TrackerLocationListener locationListener;
	private LBSDBAdapter dbAdapter;
	private LocationTrackerCallback locationTrackerCallback;
	
	public static final LocationTrackerService getLocationTrackerService() {
		return locationTrackerService;
	}
	
	private LocationTrackerService() {
	}
	
	public Collection<LocationData> getLocations(long startTime, long endTime) {
		Cursor cursor = dbAdapter.fetchLocations(startTime, endTime);
		boolean hasData = cursor.moveToFirst();
		List<LocationData> locations = new LinkedList<LocationData>();
		while (hasData) {
			int time = cursor.getInt(1);
			double latitude = cursor.getDouble(2);
			double longitude = cursor.getDouble(3);
			
			LocationData location = new LocationData();
			location.time = time;
			location.latitude = latitude;
			location.longitude = longitude;
			locations.add(location);
			hasData = cursor.moveToNext();
		}
		return locations;
	}
	
	public double getCurrentLatitude() {
		return locationListener.getLatitude();
	}
	
	public double getCurrentLongitude() {
		return locationListener.getLongitude();
	}
	
	public double getCurrentTime() {
		return locationListener.getCurrentTime();
	}
	
	public void init(Context context, LocationTrackerCallback locationTrackerCallback) {
		this.locationTrackerCallback = locationTrackerCallback;
		dbAdapter = new LBSDBAdapter(context);
		dbAdapter.open();

		LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.locationListener = new TrackerLocationListener(context);

		locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000L,
				500.0f, this.locationListener);
		
	}
	
	private class TrackerLocationListener implements LocationListener {
		private double latitude;
		private double longitude;
		private long currentTime;
		private Context context;
		
		public TrackerLocationListener(Context context) {
			this.context = context;
		}
		
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			currentTime = System.currentTimeMillis();
			if (locationTrackerCallback != null) {
				locationTrackerCallback.renderLocation(currentTime, latitude, longitude);
			}
//			GoogleMap gMap = MapFragment.class.cast(context.getFragmentManager().findFragmentById(R.id.map)).getMap();
//			gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(convertDate(currentTime)));
//			gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM_LEVEL));
			
			dbAdapter.addLocation(currentTime, latitude, longitude);
		}
		
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		public void onProviderEnabled(String provider) {
			updateLocation();
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		private void updateLocation() {
			LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			LocationProvider name = locationMgr.getProvider(LocationManager.GPS_PROVIDER);
			if (name != null) {
				Location lastGps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (lastGps == null) {
					latitude = 0.0;
					longitude = 0.0;
					currentTime = 0;
				} else {
					latitude = lastGps.getLatitude();
					longitude = lastGps.getLongitude();
					currentTime = System.currentTimeMillis();
				}
			}
		}
		
		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}
		public long getCurrentTime() {
			return currentTime;
		}
    }
}
