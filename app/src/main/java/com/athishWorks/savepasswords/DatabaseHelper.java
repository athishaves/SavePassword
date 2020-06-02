package com.athishWorks.savepasswords;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.athishWorks.savepasswords.pojoModels.PasswordsData;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "password.db";
    private static String TABLE_NAME = "password";

    private static String COL0 = "ID";
    private static String COL1 = "WEBSITE";
    private static String COL2 = "EMAIL";
    private static String COL3 = "PASSWORD";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT " + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
    }

    public boolean addData (PasswordsData item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item.getmWebsite());
        contentValues.put(COL2, item.getmEmail());
        contentValues.put(COL3, item.getmPassword());
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getData() {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateData(PasswordsData item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, item.getmID());
        Log.i("Pass", "ID "+item.getmID());
        contentValues.put(COL1, item.getmWebsite());
        contentValues.put(COL2, item.getmEmail());
        contentValues.put(COL3, item.getmPassword());
        return this.getWritableDatabase().update(TABLE_NAME, contentValues, COL0 + "=?", new String[]{item.getmID()})>0;
    }

    public boolean deleteRow (String id) {
        return this.getWritableDatabase().delete(TABLE_NAME, COL0 + "=?", new String[]{id})>0;
    }

}
