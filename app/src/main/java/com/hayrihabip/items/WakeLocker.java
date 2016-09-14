package com.hayrihabip.items;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

	// Waking up mobile if it is sleeping
    public static void acquire(Context context) {
        if (wakeLock != null) 
        	wakeLock.release();

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        wakeLock.acquire();
    }

	// Releasing wake lock
    public static void release() {
        if (wakeLock != null) 
        	wakeLock.release();
        
        wakeLock = null;
    }
}
