package com.ex2m75e.rahul.bmiapp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BMIData.db";
    public static final String TABLE_NAME = "BMIData_Table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Height";
    public static final String COL_3 = "Weight";
    public static final String COL_4 = "BMI";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Height text,weight text,bmi float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("Drop table IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String Height, String Weight, String BMI){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, Height);
        contentValues.put(COL_3, Weight);
        contentValues.put(COL_4, BMI);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME, null);
        return res;

    }
}
