package com.example.sunxindemo.debug;

import android.util.Log;

public class DebugUtil
{
	public static final boolean DEBUG = true;
	
	public static void LOGV(String tag, String message)
	{
		if(DEBUG)
			Log.v(tag, message);
	}
}
