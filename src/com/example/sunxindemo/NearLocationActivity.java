package com.example.sunxindemo;

import java.util.LinkedList;
import java.util.List;

import com.example.sunxindemo.debug.DebugUtil;
import com.example.sunxindemo.util.NetUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearLocationActivity extends FragmentActivity implements
		LocationListener, OnItemClickListener
{
	private static final String TAG = "NearLocationActivity";

	private static final int LOCATION_TIMEOUT = 1000 * 60;

	private ListView mListView;
	private NearLocationAdapter mAdapter;

	private TextView mTipTxtView;

	private LocationManager mLocationManager;

	private Location mCurLocation;

	private NearLocationAsyncTask mAsyncTask;

	public static void actionStartNearLocation(Context context)
	{
		Intent intent = new Intent(context, NearLocationActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearlocation_activity_main);

		mListView = (ListView) findViewById(R.id.lsitView);
		mListView.setEmptyView(findViewById(R.id.emptyView));
		mListView.setOnItemClickListener(this);

		mTipTxtView = (TextView) findViewById(R.id.tipInfo);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			if (mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{
				mLocationManager.requestSingleUpdate(
						LocationManager.GPS_PROVIDER, this, null);
			}

			if (NetUtil.isNetworkAvailable(this)
					&& mLocationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			{
				mLocationManager.requestSingleUpdate(
						LocationManager.NETWORK_PROVIDER, this, null);
			}

			final Runnable runnable = new Runnable()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					mLocationManager.removeUpdates(NearLocationActivity.this);

					if (mCurLocation == null
							&& (mLocationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null || mLocationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null))
					{
						mCurLocation = mLocationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (mCurLocation == null)
						{
							mCurLocation = mLocationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						}

						mAsyncTask = new NearLocationAsyncTask();
						mAsyncTask.execute(mCurLocation);
					}
				}
			};

			new Handler().postDelayed(runnable, LOCATION_TIMEOUT);
		}
		else
		{
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setMessage(R.string.nearlocation_openlocationtip)
					.setNegativeButton(R.string.nearlocation_cancel,
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									// TODO Auto-generated method stub

								}
							})
					.setPositiveButton(R.string.nearlocation_setting,
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(intent);
								}
							}).create();

			dialog.show();
		}
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "onLocationChanged");

		mLocationManager.removeUpdates(this);

		mTipTxtView.setText(R.string.nearlocation_findnearpeople);

		mCurLocation = new Location(location);

		mAsyncTask = new NearLocationAsyncTask();
		mAsyncTask.execute(mCurLocation);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "onStatusChanged");
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "onProviderEnabled");
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "onProviderDisabled");
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		// TODO Auto-generated method stub
		NearLocationItem item = (NearLocationItem) mListView.getItemAtPosition(position);
		UserLoationActivity.actionStartUserLocation(this, item.latitude, item.longitude);
	}
	
	private class NearLocationAsyncTask extends
			AsyncTask<Location, Void, List<NearLocationItem>>
	{
		private String mErrorMessage;

		@Override
		protected void onPostExecute(List<NearLocationItem> result)
		{
			// TODO Auto-generated method stub
			if (result != null)
			{
				mAdapter = new NearLocationAdapter(result);
				mListView.setAdapter(mAdapter);
			}
			else if (mErrorMessage != null)
			{

			}
		}

		@Override
		protected List<NearLocationItem> doInBackground(Location... params)
		{
			// TODO Auto-generated method stub
			if (params != null && params.length > 0)
			{
				Location curLocation = params[0];

				List<NearLocationItem> items = new LinkedList<NearLocationItem>();
				Location tmpLocation = new Location(
						LocationManager.GPS_PROVIDER);
				// 进行网络请求，得到附近联系人的地理信息
				try
				{
					NearLocationItem tmpItem = new NearLocationItem();
					tmpItem.name = "yourself";
					tmpItem.latitude = curLocation.getLatitude();
					tmpItem.longitude = curLocation.getLongitude();
					tmpItem.distance = 0;
					items.add(tmpItem);

					for (int index = 0; index < 50; index++)
					{
						NearLocationItem item = new NearLocationItem();
						item.name = "username_" + index;
						item.latitude = curLocation.getLatitude() + 0.01
								* (index + 1);
						item.longitude = curLocation.getLongitude() + 0.02
								* (index + 1);

						tmpLocation.setLatitude(item.latitude);
						tmpLocation.setLongitude(item.longitude);

						item.distance = curLocation.distanceTo(tmpLocation);
						items.add(item);
					}
				}
				catch (Exception ex)
				{
					DebugUtil.LOGV(TAG, "ex : " + ex);
				}

				return items;
			}

			return null;
		}
	}

	private class NearLocationAdapter extends BaseAdapter
	{

		private List<NearLocationItem> mItems = new LinkedList<NearLocationActivity.NearLocationItem>();

		public NearLocationAdapter(List<NearLocationItem> items)
		{
			// TODO Auto-generated constructor stub
			mItems = items;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mItems.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			if (convertView == null)
			{
				convertView = getLayoutInflater().inflate(
						R.layout.nearlocation_listitem, null);
				NearLocationHolder holder = new NearLocationHolder();
				holder.mNameView = (TextView) convertView
						.findViewById(R.id.name);
				holder.mDistanceView = (TextView) convertView
						.findViewById(R.id.distance);
				convertView.setTag(holder);
			}

			NearLocationHolder holder = (NearLocationHolder) convertView
					.getTag();
			NearLocationItem item = (NearLocationItem) getItem(position);
			holder.mNameView.setText(item.name);
			holder.mDistanceView.setText(String.valueOf(item.distance));

			return convertView;
		}

	}

	private static class NearLocationHolder
	{
		public TextView mNameView;
		public TextView mDistanceView;
	}

	private static class NearLocationItem
	{
		public String name;
		public double longitude;
		public double latitude;

		public double distance;

	}
}
