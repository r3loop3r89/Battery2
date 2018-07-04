package com.shra1.battery2.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.shra1.battery2.database.MDB;
import com.shra1.battery2.mymodels.BatteryEntry;
import com.shra1.battery2.utils.Utils;

public class ScreenOnOffReciever extends BroadcastReceiver {
    public static final String TAG = "ShraX";
    public boolean isRegistered;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "RECIEVED");
        int BatteryLevel = Utils.getBatteryPercentage(context);
        BatteryEntry entry = new BatteryEntry(System.currentTimeMillis(), BatteryLevel);
        BatteryEntry.DBCommands.addBatteryEntry(entry, MDB.getInstance(context).getWritableDatabase());
    }

    /**
     * register receiver
     *
     * @param context - Context
     * @param filter  - Intent Filter
     * @return see Context.registerReceiver(BroadcastReceiver,IntentFilter)
     */
    public Intent register(Context context, IntentFilter filter) {
        try {
            // here I propose to create
            // a isRegistered(Contex) method
            // as you can register receiver on different context
            // so you need to match against the same one :)
            // example  by storing a list of weak references
            // see LoadedApk.class - receiver dispatcher
            // its and ArrayMap there for example
            return !isRegistered
                    ? context.registerReceiver(this, filter)
                    : null;
        } finally {
            isRegistered = true;
        }
    }

    public boolean unregister(Context context) {
        // additional work match on context before unregister
        // eg store weak ref in register then compare in unregister
        // if match same instance
        return isRegistered
                && unregisterInternal(context);
    }

    private boolean unregisterInternal(Context context) {
        context.unregisterReceiver(this);
        isRegistered = false;
        return true;
    }
}
