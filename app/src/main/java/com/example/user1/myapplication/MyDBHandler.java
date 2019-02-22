package com.example.user1.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "productDb.db";
    public static final String TABLE_NAME = "purchases";
    private static final String COLUMN_PURCHASE_ID = "item_id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DATE = "purchase_date";
    private static final String COLUMN_PRICE = "price";




    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_PURCHASE_ID + " INTEGER" + " PRIMARY KEY,"
                + COLUMN_CATEGORY + " TEXT," + COLUMN_PRICE + " REAL," + COLUMN_DATE + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /* Getters for column names


     */
    public static String getColumnCategory() {
        return COLUMN_CATEGORY;
    }

    public static String getColumnDate() {
        return COLUMN_DATE;
    }

    public static String getColumnPrice() {
        return COLUMN_PRICE;
    }

    public static String getColumnPurchaseId() {
        return COLUMN_PURCHASE_ID;
    }



    // Adds data objects to database.
    public void addHandler(Product product) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_PURCHASE_ID, product.getProductID());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_DATE, product.getPurchaseDate());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    // Runs the query to database and returns the data gotten from the query.
    public List<String> loadHandler(String query) {
        int index = 0;
        double balance = 0;
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(query, null);
        if (cur != null) {
            while (cur.moveToNext()) {
                result.add(index, "ID: " + cur.getString(cur.getColumnIndex(COLUMN_PURCHASE_ID)) + System.getProperty("line.separator") + "Category: "
                        + cur.getString(cur.getColumnIndex(COLUMN_CATEGORY)) + System.getProperty("line.separator") + "Price: "
                        + cur.getString(cur.getColumnIndex(COLUMN_PRICE)) + "€ " + System.getProperty("line.separator") + "Date: "
                        + cur.getString(cur.getColumnIndex(COLUMN_DATE)));
                index++;
                balance += cur.getDouble(cur.getColumnIndex(COLUMN_PRICE));
            }
        }
        result.add(String.valueOf(balance));
        cur.close();
        db.close();
        return result;
    }
    // Deletes all data from table.
    public boolean deleteAllHandler(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_NAME);
        } catch(Exception ex) {
            Log.e(ex.toString(),"Error in delete all function!");
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    // Drops item by ID from curr table.
    public boolean dropSelectedIDHandler(int productID) {
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_PURCHASE_ID + "='"
                + String.valueOf(productID) + "'";

        boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.rawQuery(query, null);
        if (cur.moveToFirst()) {
            db.delete(TABLE_NAME, COLUMN_PURCHASE_ID + "=?",
                    new String[]{String.valueOf(productID)});
            result = true;
            cur.close();
        }
        db.close();
        return result;
    }
    // Loads the info of how much monmey has been used in total.
    public Double loadBugdetHandler(String query) {
        Double result = 0.0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(query, null);

        try{
            if (cur != null) {
                while (cur.moveToNext()) {
                    result += Double.parseDouble(cur.getString(cur.getColumnIndex(COLUMN_PRICE)));
                }
            }
        } catch(NumberFormatException ex){
            throw new NumberFormatException("PRICE FORMAT ERROR!");
        }
        cur.close();
        db.close();
        return result;
    }
}
