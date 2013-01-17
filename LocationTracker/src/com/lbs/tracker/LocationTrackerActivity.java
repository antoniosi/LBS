package com.lbs.tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationTrackerActivity extends Activity {
	private static final int DEFAULT_ZOOM_LEVEL = 16;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.US);
	private static final GregorianCalendar CALENDAR = new GregorianCalendar(TimeZone.getDefault());
	
//	private TrackerLocationListener locationListener;
//	private LBSDBAdapter dbAdapter;
	private Dialog dialog;
	private View.OnClickListener datePickerButtonClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_tracker);
//		dbAdapter = new LBSDBAdapter(this);
//		dbAdapter.open();
		initRetrievePastLocationsDatePickerDiaglog();
		initialize();
//		initLocation();
		
	}
	
	private void initRetrievePastLocationsDatePickerDiaglog() {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.lbs_picker);
		dialog.setTitle("Please pick the date:");
		
		final Button button = (Button) dialog.findViewById(R.id.datePick);
		if (datePickerButtonClickListener == null) {
			datePickerButtonClickListener = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.hide();
					DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.retrieveLocationDatePick);
					int day = datePicker.getDayOfMonth();
					int month = datePicker.getMonth() + 1;
					int year = datePicker.getYear();
					StringBuilder stDateStr = new StringBuilder().append(year)
							.append("-").append(month).append("-")
							.append(day);
					stDateStr.append(" 00:00:00,000");
					StringBuilder endDateStr = new StringBuilder().append(year)
							.append("-").append(month).append("-")
							.append(day);
					endDateStr.append(" 23:59:59,000");
					Date stDate, enDate;
					try {
						stDate = SDF.parse(stDateStr.toString());
						enDate = SDF.parse(endDateStr.toString());
						Collection<LocationData> locations = LocationTrackerService.getLocationTrackerService().getLocations(stDate.getTime(), enDate.getTime());
						
						for (LocationData location: locations) {
							long time = location.time;
							double latitude = location.latitude;
							double longitude = location.longitude;

							GoogleMap gMap = MapFragment.class.cast(
									getFragmentManager().findFragmentById(
											R.id.map)).getMap();
							gMap.addMarker(new MarkerOptions().position(
									new LatLng(latitude, longitude)).title(
									convertDate(time)));
							gMap.animateCamera(CameraUpdateFactory
									.newLatLngZoom(new LatLng(latitude,
											longitude), DEFAULT_ZOOM_LEVEL));

						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
		}
		button.setOnClickListener(datePickerButtonClickListener);
	}

	private String convertDate(long timeMs) {
        CALENDAR.setTimeInMillis(timeMs);
        return SDF.format(CALENDAR.getTime());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_location_tracker, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.show_tracked_locations:
			dialog.show();
//			final Button button = (Button) dialog.findViewById(R.id.datePick);
//			if (datePickerButtonClickListener == null) {
//				datePickerButtonClickListener = new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						DatePicker datePicker = (DatePicker) findViewById(R.id.retrieveLocationDatePick);
//						int day = datePicker.getDayOfMonth();
//						int month = datePicker.getMonth() + 1;
//						int year = datePicker.getYear();
//						StringBuilder stDateStr = new StringBuilder(year)
//								.append("-").append(month).append("-")
//								.append(day);
//						stDateStr.append(" 00:00:00");
//						StringBuilder endDateStr = new StringBuilder(year)
//								.append("-").append(month).append("-")
//								.append(day);
//						endDateStr.append(" 11:59:59");
//						Date stDate, enDate;
//						try {
//							stDate = SDF.parse(stDateStr.toString());
//							enDate = SDF.parse(endDateStr.toString());
//							Cursor cursor = dbAdapter.fetchLocations(
//									stDate.getTime(), enDate.getTime());
//							boolean hasData = cursor.moveToFirst();
//							while (hasData) {
//								int time = cursor.getInt(0);
//								double latitude = cursor.getDouble(1);
//								double longitude = cursor.getDouble(2);
//
//								GoogleMap gMap = MapFragment.class.cast(
//										getFragmentManager().findFragmentById(
//												R.id.map)).getMap();
//								gMap.addMarker(new MarkerOptions().position(
//										new LatLng(latitude, longitude)).title(
//										convertDate(time)));
//								gMap.animateCamera(CameraUpdateFactory
//										.newLatLngZoom(new LatLng(latitude,
//												longitude), DEFAULT_ZOOM_LEVEL));
//
//							}
//
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//				};
//			}
//			button.setOnClickListener(datePickerButtonClickListener);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
    private void initialize() {
    	LocationTrackerService.getLocationTrackerService().init(this, new LocationTrackerCallback() {
			
			@Override
			public void renderLocation(long time, double latitude, double longitude) {
				GoogleMap gMap = MapFragment.class.cast(getFragmentManager().findFragmentById(R.id.map)).getMap();
				gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(convertDate(time)));
				gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM_LEVEL));
			}
		});
//		LocationManager locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
//		this.locationListener = new TrackerLocationListener();
//
//		locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000L,
//				500.0f, this.locationListener);
	}
    
//    private class TrackerLocationListener implements LocationListener {
//		private double latitude;
//		private double longitude;
//		private long currentTime;
//		
//		public void onLocationChanged(Location location) {
//			latitude = location.getLatitude();
//			longitude = location.getLongitude();
//			currentTime = System.currentTimeMillis();
//			GoogleMap gMap = MapFragment.class.cast(getFragmentManager().findFragmentById(R.id.map)).getMap();
//			gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(convertDate(currentTime)));
//			gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM_LEVEL));
//			
//			dbAdapter.addLocation(currentTime, latitude, longitude);
//		}
//		
//		public void onProviderDisabled(String provider) {
//			// TODO Auto-generated method stub
//			
//		}
//		public void onProviderEnabled(String provider) {
//			updateLocation();
//		}
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		private void updateLocation() {
//			LocationManager locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			LocationProvider name = locationMgr.getProvider(LocationManager.GPS_PROVIDER);
//			if (name != null) {
//				Location lastGps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//				if (lastGps == null) {
//					latitude = 0.0;
//					longitude = 0.0;
//					currentTime = 0;
//				} else {
//					latitude = lastGps.getLatitude();
//					longitude = lastGps.getLongitude();
//					currentTime = System.currentTimeMillis();
//				}
//			}
//		}
//		
//		public double getLatitude() {
//			return latitude;
//		}
//
//		public double getLongitude() {
//			return longitude;
//		}
//		public long getCurrentTime() {
//			return currentTime;
//		}
//    }
    
//	private static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			final Calendar c = Calendar.getInstance();
//			int hour = c.get(Calendar.HOUR_OF_DAY);
//			int minute = c.get(Calendar.MINUTE);
//			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
//		}
//		
//		@Override
//		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			// TODO Auto-generated method stub
//			
//		}
//	}
	

}
