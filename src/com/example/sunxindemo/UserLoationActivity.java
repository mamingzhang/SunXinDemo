package com.example.sunxindemo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.sunxindemo.debug.DebugUtil;
import com.example.sunxindemo.util.BaiduMapUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class UserLoationActivity extends Activity implements MKGeneralListener
{
	private static final String TAG = "NearLocationActivity";

	private BMapManager mBMapMan = null;
	private MapView mMapView = null;

	private static final String LATITUDE_PARAM = "latitude_param";
	private static final String LONGITUDE_PARAM = "longitude_param";

	private double mInitLatitude;
	private double mInitLongitude;

	private LocationClient mLocationClient;
	private BDLocationListenerImpl mBDLocationListener = new BDLocationListenerImpl();

	public static void actionStartUserLocation(Context context,
			double latitude, double longitude)
	{
		Intent intent = new Intent(context, UserLoationActivity.class);
		intent.putExtra(LATITUDE_PARAM, latitude);
		intent.putExtra(LONGITUDE_PARAM, longitude);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInitLatitude = getIntent().getDoubleExtra(LATITUDE_PARAM, -1);
		mInitLongitude = getIntent().getDoubleExtra(LONGITUDE_PARAM, -1);

		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(BaiduMapUtil.BAIDUMAP_KEY, this);

		setContentView(R.layout.userlocation_activity_main);

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		MapController mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (mInitLatitude * 1E6),
				(int) (mInitLongitude * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(12);

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mBDLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setAddrType("all");
		option.setScanSpan(5000);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	protected void onDestroy()
	{
		mMapView.destroy();
		if (mBMapMan != null)
		{
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		mMapView.onPause();
		if (mBMapMan != null)
		{
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		mMapView.onResume();
		if (mBMapMan != null)
		{
			mBMapMan.start();
		}
		super.onResume();
	}

	@Override
	public void onGetNetworkState(int arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPermissionState(int arg0)
	{
		// TODO Auto-generated method stub

	}

	public class BDLocationListenerImpl implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// TODO Auto-generated method stub
			if (location == null)
			{
				return;
			}

			if (DebugUtil.DEBUG)
			{
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation)
				{
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
				}
				else if (location.getLocType() == BDLocation.TypeNetWorkLocation)
				{
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
				}

				DebugUtil.LOGV(TAG, sb.toString());
			}

			LocationData mLocData = new LocationData();
			mLocData.latitude = location.getLatitude();
			mLocData.longitude = location.getLongitude();
			mLocData.accuracy = location.getRadius();
			mLocData.direction = location.getDerect();

			MyLocationOverlay myLocationOverlay = new MyLocationOverlay(
					mMapView);
			myLocationOverlay.setData(mLocData);
			mMapView.getOverlays().add(myLocationOverlay);
			mMapView.refresh();

			mLocationClient.unRegisterLocationListener(mBDLocationListener);
			mLocationClient.stop();
		}

		@Override
		public void onReceivePoi(BDLocation arg0)
		{
			// TODO Auto-generated method stub

		}

	}
}
