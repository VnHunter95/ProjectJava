package com.example.hunter95.mynewtrackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hunter95 on 10/17/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Local_Loc.db";
    public static final String Table1 = "Location_Table";
    public static final String Table1_Col1 ="Id";
    public static final String Table1_Col2 ="name";
    public static final String Table1_Col3="latitude";
    public static final String Table1_Col4="longitude";

    private static DatabaseHelper mInstance = null;
    public static DatabaseHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + Table1 + " (Id INTEGER  PRIMARY KEY AUTOINCREMENT, name TEXT, latitude DOUBLE , longitude DOUBLE)");
        ContentValues contentValues = new ContentValues();
        contentValues.valueSet();
        contentValues.put(Table1_Col2, "Hutech");
        contentValues.put(Table1_Col3, 10.801885);
        contentValues.put(Table1_Col4, 106.714775);
        db.insert(Table1, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+ Table1);
        onCreate(db);
    }
//
//
//    public boolean insertCustomRSS(String chude, String link) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("SELECT * FROM " + Custom_RSS_Table + " WHERE " + CRSS_T_Col1 + " = '" + link.toLowerCase() + "'", null);
//        if(res.getCount()!=0)
//        {
//            return false;
//        }
//        ContentValues contentValues = new ContentValues();
//        contentValues.valueSet();
//        contentValues.put(CRSS_T_Col1, link.toLowerCase());
//        contentValues.put(CRSS_T_Col2, chude);
//        db.insert(Custom_RSS_Table, null, contentValues);
//
//        return true;
//    }
//    public boolean removeCustomRSS(String link) {
//        SQLiteDatabase db=this.getWritableDatabase();
//        int var = db.delete(Custom_RSS_Table, "Link like ?", new String[]{link});
//        if(var == 0)
//        {
//            return false;
//        }
//        return true;
//    }
    public void xoaHet(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + tableName + ";VACUUM;");
    }
    public Cursor getLocTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.rawQuery("SELECT * FROM " + Table1 , null);
    }
    public boolean insertLocToDatabase(String name, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.valueSet();
        contentValues.put(Table1_Col2, name);
        contentValues.put(Table1_Col3, latitude);
        contentValues.put(Table1_Col4, longitude);
        db.insert(Table1, null, contentValues);
        return true;
    }
        public boolean removeLocFromDatabase(int id) {
        SQLiteDatabase db=this.getWritableDatabase();
        int var = db.delete(Table1, "Id = ?", new String[]{String.valueOf(id)});
        if(var == 0)
        {
            return false;
        }
        return true;
    }
}
