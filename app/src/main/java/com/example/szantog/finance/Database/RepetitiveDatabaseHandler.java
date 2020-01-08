package com.example.szantog.finance.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.RepetitiveItem;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by szantog on 2018.03.20..
 */

public class RepetitiveDatabaseHandler extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE_NAME = "Finance";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "repetitiveentries";

    private static final String TIME = "time";
    private static final String SUM = "sum";
    private static final String STARTTIME = "starttime";
    private static final String ENDTIME = "endtime";
    private static final String LATESTUPDATETIME = "latestupdatetime"; //last occurrence, not last checked time!!!
    private static final String TURNOVER_MONTH = "turnover_month";
    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY = "subcategory";
    private static final String COLLECTION = "collection"; //all the primary keys (time) of members of repetitive instances are put here as string
    public static final String DIVIDER = "__div__";

    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s " +
                    "(%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER, " +
                    "%s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)", TABLE_NAME,
            TIME, SUM, STARTTIME, ENDTIME,
            LATESTUPDATETIME, TURNOVER_MONTH, CATEGORY, SUBCATEGORY, COLLECTION);

    public RepetitiveDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(FinanceDatabaseHandler.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String query = String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER, " +
                        "%s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)", TABLE_NAME,
                TIME, SUM, STARTTIME, ENDTIME,
                LATESTUPDATETIME, TURNOVER_MONTH, CATEGORY, SUBCATEGORY, COLLECTION);
        sqLiteDatabase.execSQL(query);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE);
    }

    public String getAllCollections() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s ", COLLECTION, TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        String output = "";
        int count = cursor.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                output += cursor.getString(0);
            }
        }
        return output;
    }

    public ArrayList<RepetitiveItem> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s ", TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<RepetitiveItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            long startTime = cursor.getLong(cursor.getColumnIndex(STARTTIME));
            long endTime = cursor.getLong(cursor.getColumnIndex(ENDTIME));
            int turnover = cursor.getInt(cursor.getColumnIndex(TURNOVER_MONTH));
            String collection = cursor.getString(cursor.getColumnIndex(COLLECTION));
            String category = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCategory = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            output.add(new RepetitiveItem(time, sum, startTime, endTime, turnover, collection, category, subCategory));
        }
        cursor.close();
        db.close();
        return output;
    }

    public void insertData(RepetitiveItem item) {
        long time = item.getTime() + (long) (Math.random() * 100);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, System.currentTimeMillis());
        values.put(SUM, item.getSum());
        values.put(STARTTIME, item.getStartTime());
        values.put(ENDTIME, item.getEndTime());
        if (item.getStartTime() > System.currentTimeMillis()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(item.getStartTime());
            long latestUpdateTime = 0;
            if (cal.get(Calendar.MONTH) - item.getTurnoverMonth() >= 0) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) - item.getTurnoverMonth();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                cal.set(year, month, day, 0, 0);
                latestUpdateTime = cal.getTimeInMillis();
            } else {
                if (item.getTurnoverMonth() < 12) {
                    int year = cal.get(Calendar.YEAR) - 1;
                    int month = 12 - item.getTurnoverMonth();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    cal.set(year, month, day, 0, 0);
                    latestUpdateTime = cal.getTimeInMillis();
                }
                //ELSE STATEMENT MISSING HERE!


            }
            values.put(LATESTUPDATETIME, latestUpdateTime);
            values.put(COLLECTION, "");
        } else {
            values.put(LATESTUPDATETIME, item.getStartTime());
            values.put(COLLECTION, String.valueOf(time) + DIVIDER);
        }
        values.put(TURNOVER_MONTH, item.getTurnoverMonth());
        values.put(CATEGORY, item.getCategory());
        values.put(SUBCATEGORY, item.getSubCategory());
        db.insert(TABLE_NAME, null, values);
        values.clear();
        if (item.getStartTime() < System.currentTimeMillis()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getStartTime());
            values.put(TIME, time);
            values.put(SUM, item.getSum());
            values.put(FinanceDatabaseHandler.YEAR, calendar.get(Calendar.YEAR));
            values.put(FinanceDatabaseHandler.MONTH, calendar.get(Calendar.MONTH));
            values.put(FinanceDatabaseHandler.DAY, calendar.get(Calendar.DAY_OF_MONTH));
            values.put(CATEGORY, item.getCategory());
            values.put(SUBCATEGORY, item.getSubCategory());
            db.insert(FinanceDatabaseHandler.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void updateData(RepetitiveItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUM, item.getSum());
        values.put(ENDTIME, item.getEndTime());
        values.put(TURNOVER_MONTH, item.getTurnoverMonth());
        values.put(CATEGORY, item.getCategory());
        values.put(SUBCATEGORY, item.getSubCategory());
        db.update(TABLE_NAME, values, TIME + "=" + item.getTime(), null);
        db.close();
    }

    public void deleteData(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TIME + "=" + time, null);
        db.close();
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public ArrayList<EntryItem> checkData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s > ? OR %s = 0 ", TABLE_NAME, ENDTIME, ENDTIME);
        String[] selectionArgs = {String.valueOf(System.currentTimeMillis())};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return new ArrayList<>();
        } else {
            ArrayList<EntryItem> output = new ArrayList<>();
            ArrayList<Long> newItemPrimaryKeys = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                long endTime = cursor.getLong(cursor.getColumnIndex(ENDTIME));
                if (endTime < System.currentTimeMillis() && endTime != 0) {
                    continue;
                }
                long timePrimaryKey = cursor.getLong(cursor.getColumnIndex(TIME));
                long sum = cursor.getLong(cursor.getColumnIndex(SUM));
                String prevCollection = cursor.getString(cursor.getColumnIndex(COLLECTION));
                String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
                String subcat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
                long latestUpdateTime = cursor.getLong(cursor.getColumnIndex(LATESTUPDATETIME));
                calendar.setTimeInMillis(latestUpdateTime);
                int latestUpdateYear = calendar.get(Calendar.YEAR);
                int latestUpdateMonth = calendar.get(Calendar.MONTH);
                int latestUpdateDay = calendar.get(Calendar.DAY_OF_MONTH);
                int turnoverMonth = cursor.getInt(cursor.getColumnIndex(TURNOVER_MONTH));
                int nextUpdateYear = 0;
                int nextUpdateMonth = 0;
                if (latestUpdateMonth + turnoverMonth > 11) {
                    int extraYear = (latestUpdateMonth + turnoverMonth) / 12;
                    nextUpdateYear = latestUpdateYear + extraYear;
                    nextUpdateMonth = latestUpdateMonth + turnoverMonth - 12 * extraYear;
                } else {
                    nextUpdateYear = latestUpdateYear;
                    nextUpdateMonth = latestUpdateMonth + turnoverMonth;
                }
                calendar.set(nextUpdateYear, nextUpdateMonth, latestUpdateDay, 0, 0);
                if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                    continue;
                } else {
                    Boolean ready = false;
                    while (!ready) {
                        long newTime = System.currentTimeMillis() + (long) (Math.random() * 100000); //random values to avoid same values
                        newItemPrimaryKeys.add(newTime);
                        output.add(new EntryItem(newTime, sum, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH), cat, subcat));
                        latestUpdateYear = nextUpdateYear;
                        latestUpdateMonth = nextUpdateMonth;
                        if (latestUpdateMonth + turnoverMonth > 11) {
                            int extraYear = (latestUpdateMonth + turnoverMonth) / 12;
                            nextUpdateYear = latestUpdateYear + extraYear;
                            nextUpdateMonth = latestUpdateMonth + turnoverMonth - 12 * extraYear;
                        } else {
                            nextUpdateYear = latestUpdateYear;
                            nextUpdateMonth = latestUpdateMonth + turnoverMonth;
                        }
                        calendar.set(nextUpdateYear, nextUpdateMonth, latestUpdateDay);
                        if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                            ready = true;
                        }
                    }
                }
                SQLiteDatabase dbWrite = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                calendar.set(latestUpdateYear, latestUpdateMonth, latestUpdateDay);
                values.put(LATESTUPDATETIME, calendar.getTimeInMillis());
                String newCollection = "";
                for (int k = 0; k < newItemPrimaryKeys.size(); k++) {
                    newCollection += String.valueOf(newItemPrimaryKeys.get(k)) + DIVIDER;
                }
                values.put(COLLECTION, prevCollection + newCollection);
                dbWrite.update(TABLE_NAME, values, TIME + "=" + timePrimaryKey, null);
                dbWrite.close();
            }
            cursor.close();
            db.close();
            return output;
        }
    }

    public void updateCollection(String category, String subCategory, long sum, String collection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLLECTION, collection);
        String[] args = {category, subCategory, String.valueOf(sum)};
        db.update(TABLE_NAME, values, String.format("%s=? AND %s=? AND %s=?", CATEGORY, SUBCATEGORY, SUM), args);
    }
}
