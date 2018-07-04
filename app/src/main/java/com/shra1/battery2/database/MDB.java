package com.shra1.battery2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shra1.battery2.mymodels.BatteryEntry;

public class MDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "battery.db";
    public static final int VERSION = 1;

    static MDB instance = null;

    public MDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static MDB getInstance(Context context) {
        if (instance == null) {
            instance = new MDB(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BatteryEntry.DBCommands.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
