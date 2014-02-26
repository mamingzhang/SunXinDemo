package com.example.sunxindemo;

import com.example.sunxindemo.debug.DebugUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{
	private static final String TAG = "ServerService";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		DebugUtil.LOGV(TAG, "BootReceiver onReceive");
		final String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action))
		{
			ServerService.actionStartServerService(context);
		}
	}

}
