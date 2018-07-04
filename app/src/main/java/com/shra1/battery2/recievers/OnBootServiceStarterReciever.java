package com.shra1.battery2.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shra1.battery2.services.ScreenOnOffRecieverRegisterarService;
import com.shra1.battery2.utils.SharedPreferencesManager;
import com.shra1.battery2.utils.Utils;

public class OnBootServiceStarterReciever extends BroadcastReceiver {
    SharedPreferencesManager sharedPreferencesManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        if (sharedPreferencesManager.getSTART_SERVICE_AUTOMATICALLY()){
            if (Utils.isMyServiceRunning(context, ScreenOnOffRecieverRegisterarService.class)){ }else{
                Intent serviceStarterIntent = new Intent(context, ScreenOnOffRecieverRegisterarService.class);
                context.startService(serviceStarterIntent);
            }
        }
    }
}
