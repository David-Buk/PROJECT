package com.example.project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry1 = "create table users(Username text, Password text, Age integer)";
        db.execSQL(qry1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void register(String username, String password, int age) {
        ContentValues cv = new ContentValues();
        cv.put("Username", username);
        cv.put("Password", password);
        cv.put("Age", age);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("users", null, cv);
        if (result == -1) {
            Log.d("DB_LOG", "Insert failed for user: " + username);
        } else {
            Log.d("DB_LOG", "User registered: " + username);
        }
        db.close();
    }


    public int login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE Username=? AND Password=?", new String[]{username, password});
        int result = 0;
        if (c.moveToFirst()) {
            result = 1;
        }
        c.close();
        db.close();
        return result;
    }
}