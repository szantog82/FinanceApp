package com.example.szantog.finance.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.SubCategoryListItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by szantog on 2018.03.16..
 */

public class FinanceDatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Finance";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "incomeexpenditure";

    private static final String TIME = "time";
    private static final String SUM = "sum";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    private static final String CATEGORY = "category";
    private static final String SUBCATEGORY = "subcategory";

    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s " +
            "(%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, " +
            "%s TEXT, %s TEXT)", TABLE_NAME, TIME, SUM, YEAR, MONTH, DAY, CATEGORY, SUBCATEGORY);

    public FinanceDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        // sqLiteDatabase.execSQL(RepetitiveDatabaseHandler.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String query = String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, " +
                "%s TEXT, %s TEXT)", TABLE_NAME, TIME, SUM, YEAR, MONTH, DAY, CATEGORY, SUBCATEGORY);
        sqLiteDatabase.execSQL(query);
    }

    public void insertEntry(EntryItem item) {
        //kell külön év, hónap, nap is, mert mi van, ha nem aznapra kerül bejegyzés?!
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, item.getTime());
        values.put(SUM, item.getSum());
        values.put(YEAR, item.getYear());
        values.put(MONTH, item.getMonth());
        values.put(DAY, item.getDay());
        values.put(CATEGORY, item.getCategory());
        values.put(SUBCATEGORY, item.getSubCategory());
        try {
            db.insert(TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            values.put(TIME, item.getTime() + (long) (Math.random() * 100000));  //to make sure, every item will be unique
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public ArrayList<EntryItem> getCertainDailyData(int year, int month, int day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? ", TABLE_NAME, YEAR, MONTH, DAY);
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month), String.valueOf(day)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public ArrayList<EntryItem> getCertainMonthlyData(int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ", TABLE_NAME, YEAR, MONTH);
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public ArrayList<EntryItem> getCertainYearlyData(int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? ", TABLE_NAME, YEAR);
        String[] selectionArgs = {String.valueOf(year)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            int month = cursor.getInt(cursor.getColumnIndex(MONTH));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public ArrayList<EntryItem> getCertainMonthlyDataCategoryDefined(int year, int month, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ? ", TABLE_NAME, YEAR, MONTH, CATEGORY);
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month), category};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            if (subCat == null) {
                subCat = "";
            }
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public ArrayList<EntryItem> getCertainYearlyDataCategoryDefined(int year, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ", TABLE_NAME, YEAR, CATEGORY);
        String[] selectionArgs = {String.valueOf(year), category};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            int month = cursor.getInt(cursor.getColumnIndex(MONTH));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            if (subCat == null) {
                subCat = "";
            }
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public long getMonthlyBalance(int year, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT SUM(%s) FROM %s WHERE %s = ? AND %s = ? ", SUM, TABLE_NAME, YEAR, MONTH);
        String[] selectionArgs = {String.valueOf(year), String.valueOf(month)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        long sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    public long getSumByTime(long time) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s WHERE %s = ? ", SUM, TABLE_NAME, TIME);
        String[] selectionArgs = {String.valueOf(time)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        long sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    public long getTotalBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT SUM(%s) FROM %s ", SUM, TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        long sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    public ArrayList<EntryItem> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<EntryItem> output = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long time = cursor.getLong(cursor.getColumnIndex(TIME));
            long sum = cursor.getLong(cursor.getColumnIndex(SUM));
            int year = cursor.getInt(cursor.getColumnIndex(YEAR));
            int month = cursor.getInt(cursor.getColumnIndex(MONTH));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            output.add(new EntryItem(time, sum, year, month, day, cat, subCat));
        }
        cursor.close();
        db.close();
        return output;
    }

    public void updateEntry(EntryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUM, item.getSum());
        values.put(YEAR, item.getYear());
        values.put(MONTH, item.getMonth());
        values.put(DAY, item.getDay());
        values.put(CATEGORY, item.getCategory());
        values.put(SUBCATEGORY, item.getSubCategory());
        db.update(TABLE_NAME, values, TIME + "=" + item.getTime(), null);
        db.close();
    }

    public void deleteEntry(EntryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TIME + "=" + item.getTime(), null);
        db.close();
    }

    public void deleteEntryByTime(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TIME + "=" + time, null);
        db.close();
    }

    public ArrayList<String> getDistinctCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT DISTINCT %s FROM %s ", CATEGORY, TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> output = new ArrayList<>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                output.add(cursor.getString(0));
            }
        }
        return output;
    }

    public ArrayList<SubCategoryListItem> getSubCategoriesforCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s WHERE %s = ? ", SUBCATEGORY, TABLE_NAME, CATEGORY);
        String[] selectionArgs = {category};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        ArrayList<SubCategoryListItem> sortItems = new ArrayList<>();
        if (cursor.moveToFirst()) {
            ArrayList<String> subCategories = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                if (cursor.getString(0) != null && cursor.getString(0).length() > 0) {
                    subCategories.add(cursor.getString(0));
                }
            }

            for (int i = 0; i < subCategories.size(); i++) {
                Boolean added = false;
                for (int j = 0; j < sortItems.size(); j++) {
                    if (sortItems.get(j).getName().equals(subCategories.get(i))) {
                        sortItems.get(j).incrCount();
                        added = true;
                    }
                }
                if (!added) {
                    sortItems.add(new SubCategoryListItem(subCategories.get(i), 1));
                }
            }

            Collections.sort(sortItems);

        }
        cursor.close();
        db.close();
        return sortItems;
    }

    public ArrayList<EntryItem> customQuery(String sum, char sumOperator, long timeFrom, long timeTo, String category, String categoryOperator,
                                            String subCategory, String subCategoryOperator) {
        ArrayList<EntryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sumQuery = "";
        String categoryQuery = "";
        String subCategoryQuery = "";
        if (sum.length() > 0) {
            sum = sum.replace("'", "");
            sum = sum.replace("\"", "");
            sum = sum.replace(";", "");
            sumQuery = SUM + sumOperator + sum;
        }
        if (category.length() > 0) {
            category = category.replace("'", "");
            category = category.replace("\"", "");
            category = category.replace(";", "");
            if (categoryOperator.equals("=")) {
                categoryQuery = CATEGORY + categoryOperator + "'" + category + "'";
            } else {
                categoryQuery = CATEGORY + " " + categoryOperator + " " + "'%" + category + "%'";
            }
        }
        if (subCategory.length() > 0) {
            subCategory = subCategory.replace("'", "");
            subCategory = subCategory.replace("\"", "");
            subCategory = subCategory.replace(";", "");
            if (subCategoryOperator.equals("=")) {
                subCategoryQuery = SUBCATEGORY + subCategoryOperator + "'" + subCategory + "'";
            } else {
                subCategoryQuery = SUBCATEGORY + " " + subCategoryOperator + " " + "'%" + subCategory + "%'";
            }
        }
        if (sumQuery.length() == 0 && categoryQuery.length() == 0 && subCategoryQuery.length() == 0) {
            sumQuery = "1=1";
        }

        if (sumQuery.length() > 0 && (categoryQuery.length() > 0 || subCategoryQuery.length() > 0)) {
            sumQuery += " AND ";
        }
        if (categoryQuery.length() > 0 && subCategoryQuery.length() > 0) {
            categoryQuery += " AND ";
        }
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_NAME + " WHERE " + sumQuery + " " + categoryQuery + " " +
                subCategoryQuery, null);
        Calendar calendar = Calendar.getInstance();
        int timeFromInDays = (int) (timeFrom / 1000 / 3600 / 24);
        int timeToInDays = (int) (timeTo / 1000 / 3600 / 24);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            long itemTime = cursor.getLong(cursor.getColumnIndex(TIME));
            long itemSum = cursor.getLong(cursor.getColumnIndex(SUM));
            int year = cursor.getInt(cursor.getColumnIndex(YEAR));
            int month = cursor.getInt(cursor.getColumnIndex(MONTH));
            int day = cursor.getInt(cursor.getColumnIndex(DAY));
            String cat = cursor.getString(cursor.getColumnIndex(CATEGORY));
            String subCat = cursor.getString(cursor.getColumnIndex(SUBCATEGORY));
            if (timeTo == 0) {
                items.add(new EntryItem(itemTime, itemSum, year, month, day, cat, subCat));
            } else {
                calendar.set(year, month, day);
                int currentTimeInDays = (int) (calendar.getTimeInMillis() / 1000 / 3600 / 24);
                if (currentTimeInDays >= timeFromInDays && currentTimeInDays <= timeToInDays) {
                    items.add(new EntryItem(itemTime, itemSum, year, month, day, cat, subCat));
                }
            }
        }
        cursor.close();
        db.close();
        return items;
    }

    public void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public String getPreviousInstances(long sum, String category, String subCategory) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT %s,%s,%s FROM %s WHERE %s = ? AND %s = ? AND %s = ?", TIME, CATEGORY, SUBCATEGORY, TABLE_NAME, CATEGORY, SUBCATEGORY, SUM);
        String[] selectionArgs = {category, subCategory, String.valueOf(sum)};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        int count = cursor.getCount();
        String wh = category + "/" + subCategory + ":" + String.valueOf(sum) + ": ";
        String coll = "";
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            coll += String.valueOf(cursor.getLong(0)) + RepetitiveDatabaseHandler.DIVIDER;
        }
        return coll;
    }
}