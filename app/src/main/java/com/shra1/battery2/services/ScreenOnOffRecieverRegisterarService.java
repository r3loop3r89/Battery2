package com.shra1.battery2.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.shra1.battery2.database.MDB;
import com.shra1.battery2.mymodels.BatteryEntry;
import com.shra1.battery2.recievers.ScreenOnOffReciever;
import com.shra1.battery2.utils.Constants;
import com.shra1.battery2.utils.SharedPreferencesManager;
import com.shra1.battery2.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.shra1.battery2.utils.Constants.PERIODICALLY;

public class ScreenOnOffRecieverRegisterarService extends Service {


    public static final String TAG = "ShraX";
    private static ScreenOnOffRecieverRegisterarService s = null;
    IntentFilter intentFilter;
    ScreenOnOffReciever screenOnOffReciever;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    SharedPreferencesManager sharedPreferencesManager;

    public static ScreenOnOffRecieverRegisterarService getServiceObject() {
        return s;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        s = this;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());
        if (sharedPreferencesManager.getBATTERY_DATA_SAVE_TYPE() == PERIODICALLY) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Shrawake");
            wakeLock.acquire();

            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            int BatteryLevel = Utils.getBatteryPercentage(getApplicationContext());
                            BatteryEntry entry = new BatteryEntry(System.currentTimeMillis(), BatteryLevel);
                            BatteryEntry.DBCommands.addBatteryEntryAsync(entry, MDB.getInstance(getApplicationContext()).getWritableDatabase());
                        }
                    }, 0, Constants.BATTERY_ENTRY_INTERVAL, TimeUnit.SECONDS);
        } else {
            screenOnOffReciever = new ScreenOnOffReciever();
            intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            screenOnOffReciever.register(this, intentFilter);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void shutdownWorker() {
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());
        if (sharedPreferencesManager.getBATTERY_DATA_SAVE_TYPE() == PERIODICALLY) {
            wakeLock.release();
            scheduler.shutdown();
        } else {
            screenOnOffReciever.unregister(this);
        }
    }
}
