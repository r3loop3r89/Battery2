package com.shra1.battery2.mymodels;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class BatteryEntry {
    public static final String TABLE_NAME = "BatteryEntry";
    public static final String sBatteryEntryID = "BatteryEntryID";
    public static final String sBatteryEntryOn = "BatteryEntryOn";
    public static final String sBatteryLevel = "BatteryLevel";

    int BatteryEntryID;
    long BatteryEntryOn;
    int BatteryLevel;

    public BatteryEntry(long BatteryEntryOn, int BatteryLevel) {
        this.BatteryEntryOn = BatteryEntryOn;
        this.BatteryLevel = BatteryLevel;
    }

    public BatteryEntry() {

    }

    public BatteryEntry(int batteryEntryID, long batteryEntryOn, int batteryLevel) {
        BatteryEntryID = batteryEntryID;
        BatteryEntryOn = batteryEntryOn;
        BatteryLevel = batteryLevel;
    }

    public int getBatteryEntryID() {
        return BatteryEntryID;
    }

    public void setBatteryEntryID(int batteryEntryID) {
        BatteryEntryID = batteryEntryID;
    }

    public long getBatteryEntryOn() {
        return BatteryEntryOn;
    }

    public void setBatteryEntryOn(long batteryEntryOn) {
        BatteryEntryOn = batteryEntryOn;
    }

    public int getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        BatteryLevel = batteryLevel;
    }

    public static class DBCommands {
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        " ( " +
                        sBatteryEntryID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        sBatteryEntryOn + " NUMERIC, " +
                        sBatteryLevel + " INTEGER " +
                        " ) ";

        public static void addBatteryEntryAsync(BatteryEntry b, SQLiteDatabase db) {
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    BatteryEntry b = (BatteryEntry) objects[0];
                    SQLiteDatabase db = (SQLiteDatabase) objects[1];
                    ContentValues values = new ContentValues();
                    values.put(sBatteryEntryOn, b.getBatteryEntryOn());
                    values.put(sBatteryLevel, b.getBatteryLevel());
                    db.insert(TABLE_NAME, null, values);
                    return true;
                }
            };
            asyncTask.execute(b, db);

        }
        public static void addBatteryEntry(BatteryEntry b, SQLiteDatabase db) {
            ContentValues values = new ContentValues();
            values.put(sBatteryEntryOn, b.getBatteryEntryOn());
            values.put(sBatteryLevel, b.getBatteryLevel());
            db.insert(TABLE_NAME, null, values);
        }

        public static void getAllBatteryEntries(SQLiteDatabase db, final GetAllBatteryEntriesCallback c) {
            AsyncTask asyncTask = new AsyncTask<Object, Void, List<BatteryEntry>>() {
                @Override
                protected List<BatteryEntry> doInBackground(Object... voids) {
                    SQLiteDatabase db = (SQLiteDatabase) voids[0];
                    List<BatteryEntry> l = new ArrayList<>();
                    Cursor sB = db.query(TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

                    if (sB.getCount() > 0) {
                        sB.moveToFirst();
                        do {
                            BatteryEntry b = new BatteryEntry(
                                    sB.getInt(sB.getColumnIndex(sBatteryEntryID)),
                                    sB.getLong(sB.getColumnIndex(sBatteryEntryOn)),
                                    sB.getInt(sB.getColumnIndex(sBatteryLevel)));
                            l.add(b);
                        } while (sB.moveToNext());
                    }

                    return l;
                }

                @Override
                protected void onPostExecute(List<BatteryEntry> batteryEntries) {
                    super.onPostExecute(batteryEntries);
                    c.onCompleted(batteryEntries);
                }
            };
            asyncTask.execute(db);
        }

        public static void getBatteryEntriesFor(SQLiteDatabase db, DateTime dt, final GetAllBatteryEntriesCallback c) {
            AsyncTask asyncTask = new AsyncTask<Object, Void, List<BatteryEntry>>() {
                @Override
                protected List<BatteryEntry> doInBackground(Object... voids) {
                    SQLiteDatabase db = (SQLiteDatabase) voids[0];
                    DateTime dt = (DateTime) voids[1];

                    DateTime fromDate = dt;
                    fromDate = fromDate.withHourOfDay(00);
                    fromDate = fromDate.withMinuteOfHour(00);
                    fromDate = fromDate.withSecondOfMinute(00);
                    long fromEpoch = fromDate.getMillis();

                    DateTime toDate = dt;
                    toDate = toDate.withHourOfDay(23);
                    toDate = toDate.withMinuteOfHour(59);
                    toDate = toDate.withSecondOfMinute(59);
                    long toEpoch = toDate.getMillis();

                    List<BatteryEntry> l = new ArrayList<>();
                    Cursor sB = db.query(TABLE_NAME,
                            null,
                            sBatteryEntryOn+" >= ? AND "+sBatteryEntryOn+" <= ?",
                            new String[]{""+fromEpoch, ""+toEpoch},
                            null,
                            null,
                            null);

                    if (sB.getCount() > 0) {
                        sB.moveToFirst();
                        do {
                            BatteryEntry b = new BatteryEntry(
                                    sB.getInt(sB.getColumnIndex(sBatteryEntryID)),
                                    sB.getLong(sB.getColumnIndex(sBatteryEntryOn)),
                                    sB.getInt(sB.getColumnIndex(sBatteryLevel)));
                            l.add(b);
                        } while (sB.moveToNext());
                    }

                    return l;
                }

                @Override
                protected void onPostExecute(List<BatteryEntry> batteryEntries) {
                    super.onPostExecute(batteryEntries);
                    c.onCompleted(batteryEntries);
                }
            };
            asyncTask.execute(db, dt);
        }

    }

    public interface GetAllBatteryEntriesCallback{
        public void onCompleted(List<BatteryEntry> batteryEntries);
    }
}
