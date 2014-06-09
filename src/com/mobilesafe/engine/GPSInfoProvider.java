package com.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSInfoProvider {
	private static GPSInfoProvider gpsInfoProvider;
	private static Context context;
	private MyLocationListener locationListener;
	LocationManager manager;
	static SharedPreferences sp;

	private GPSInfoProvider() {
	}

	public static synchronized GPSInfoProvider getInstance(Context context) {
		if (gpsInfoProvider == null) {
			gpsInfoProvider = new GPSInfoProvider();
			GPSInfoProvider.context = context;

		}
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return gpsInfoProvider;
	}

	/**
	 * 将当前的位置存入
	 */
	public String getLocation() {
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = getProvider(manager);
		// 注册位置的监听器
		manager.requestLocationUpdates(provider, 60000, 50, getMyLocationListener());

		return sp.getString("location", "");

	}

	/**
	 * 停止当前的gps 监听
	 */
	public void stopLocationListener() {
		manager.removeUpdates(getMyLocationListener());
	}

	private MyLocationListener getMyLocationListener() {
		if (locationListener == null) {
			locationListener = new MyLocationListener();
		}
		return locationListener;
	}

	private class MyLocationListener implements LocationListener {

		/**
		 * 当手机位置发生改变
		 */
		@Override
		public void onLocationChanged(Location location) {
			// 维度
			String latitude = "latitude:" + location.getLatitude();
			// 经度
			String longitude = "longitude:" + location.getLongitude();
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("location", latitude + "-" + longitude);
			editor.commit();
		}

		/**
		 * 状态发生改变
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		/**
		 * 某个设备被打开
		 */
		@Override
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 某个设备被关闭
		 */
		@Override
		public void onProviderDisabled(String provider) {

		}
	}

	/**
	 * 位置管理服务 拿到当前系统中 最好的位置提供者
	 * 
	 * @param manager
	 * @return
	 */
	private String getProvider(LocationManager manager) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);
		return manager.getBestProvider(criteria, true);
	}
}
