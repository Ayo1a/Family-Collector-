package com.liyah_barakb.familycollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class MyReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("mylog", ">>>>> action = " + action);

        if(Intent.ACTION_BATTERY_CHANGED.equals(action))
        {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Log.d("mylog", ">>>>> Battery Level = " + level);

            if(level < 20)
            {
                //showNotification();
                Log.d("mylog", ">>>>> Battery Level less then 20% show notification!");
            }
        }
    }
}

