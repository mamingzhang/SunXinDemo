package com.example.sunxindemo;

import java.util.concurrent.atomic.AtomicBoolean;

import com.example.sunxindemo.debug.DebugUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ServerService extends Service
{
	private static final String TAG = "ServerService";
	
	public static final String ACTION_START_SERVERSERVICE = "com.example.sunxindemo.startservice";
	public static final String ACTION_STOP_SERVERSERVICE = "com.example.sunxindemo.stopservice";
	public static final String ACTION_RESET_SERVERSERVICE = "com.example.sunxindemo.resetservice";
	
	public static final int SYNC_INTERVAL = 5 * 60 * 1000;
	
	private SyncThread mSyncThread;
	
	public static void actionStartServerService(Context context)
	{
		Intent intent = new Intent(context, ServerService.class);
		intent.setAction(ACTION_START_SERVERSERVICE);
		
		context.startService(intent);
	}
	
	public static void actionStopServerService(Context context)
	{
		Intent intent = new Intent(context, ServerService.class);
		intent.setAction(ACTION_STOP_SERVERSERVICE);
		
		context.startService(intent);
	}
	
	public static void actionResetServerService(Context context)
	{
		Intent intent = new Intent(context, ServerService.class);
		intent.setAction(ACTION_RESET_SERVERSERVICE);
		
		context.startService(intent);
	}
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		DebugUtil.LOGV(TAG, "onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "onStartCommand");
		
		boolean bNeedStart = false;
		
		if(ACTION_START_SERVERSERVICE.equals(intent.getAction()))
		{
			if(mSyncThread == null || mSyncThread.bSyncCanceld())
				bNeedStart = true;
		}
		else if(ACTION_STOP_SERVERSERVICE.equals(intent.getAction()))
		{
			if(mSyncThread != null)
			{
				mSyncThread.cancelSync();
				mSyncThread = null;
			}
			
			stopSelf(startId);
		}
		else if(ACTION_RESET_SERVERSERVICE.equals(intent.getAction()))
		{
			if(mSyncThread != null)
			{
				mSyncThread.cancelSync();
				mSyncThread = null;
			}
			
			bNeedStart = true;
		}
		
		if(bNeedStart)
		{
			mSyncThread = new SyncThread();
			mSyncThread.start();
		}
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		DebugUtil.LOGV(TAG, "onDestroy");
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static class SyncThread extends Thread
	{
		private AtomicBoolean bCanceled = new AtomicBoolean(false);
		
		public void cancelSync()
		{
			bCanceled.set(true);
			interrupt();
		}
		
		public boolean bSyncCanceld()
		{
			return bCanceled.get();
		}
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			DebugUtil.LOGV(TAG, "SyncThread run");
			
			while(!bSyncCanceld())
			{
				try
				{
					DebugUtil.LOGV(TAG, "SyncThread run once");
					Thread.sleep(SYNC_INTERVAL);
				}
				catch(InterruptedException ex)
				{
					
				}
				catch(Exception ex)
				{
					
				}
			}
		}
	}
}
