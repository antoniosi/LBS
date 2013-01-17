package com.lbs.tracker;


public interface LocationTrackerCallback {
	public void renderLocation(long time, double latitude, double longitude);

}
