package com.shra1.battery2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.shra1.battery2.utils.Constants.PERIODICALLY;

public class SharedPreferencesManager {
    static SharedPreferencesManager instance = null;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private SharedPreferencesManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(
                context.getPackageName() + "." + getClass().getName(),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public boolean getSTART_SERVICE_AUTOMATICALLY() {
        return sharedPreferences.getBoolean("START_SERVICE_AUTOMATICALLY", false);
    }

    public void setSTART_SERVICE_AUTOMATICALLY(boolean START_SERVICE_AUTOMATICALLY) {
        editor.putBoolean("START_SERVICE_AUTOMATICALLY", START_SERVICE_AUTOMATICALLY);
        editor.commit();
    }

    public int getBATTERY_DATA_SAVE_TYPE(){
        return sharedPreferences.getInt("BATTERY_DATA_SAVE_TYPE", PERIODICALLY);
    }

    public void setBATTERY_DATA_SAVE_TYPE(int BATTERY_DATA_SAVE_TYPE){
        editor.putInt("BATTERY_DATA_SAVE_TYPE", BATTERY_DATA_SAVE_TYPE);
        editor.commit();
    }
}
