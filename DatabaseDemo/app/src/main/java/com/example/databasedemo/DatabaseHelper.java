package com.example.databasedemo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "PERSON_INFO.DB";
    static final int DATABASE_VERSION = 1;


    static final String DATABASE_TABLE_NAME = "USERS";
    static final String USER_ID = "id";
    static final String USER_NAME = "name";
    static final String USER_EMAIL = "email";
    static final String USER_CONTACT = "contact";
    static final String CURRENT_DATE = "date";
    static final String CURRENT_TIME = "time";


    private static final String CREATE_TABLE_QUERY = " CREATE TABLE " +
            DATABASE_TABLE_NAME + " ( " +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_NAME + " TEXT NOT NULL, " +
            USER_EMAIL + " TEXT NOT NULL, " +
            USER_CONTACT + " TEXT NOT NULL UNIQUE, " +
            CURRENT_DATE + " TEXT NOT NULL, " +
            CURRENT_TIME + " TEXT NOT NULL " + " );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
    }

    public boolean insertUserData(String name, String email, String contact, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(email.matches("")){ email = "null"; }

        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("contact", contact);
        contentValues.put("date", date);
        contentValues.put("time", time);

        if (name.matches("") || contact.matches("")) {
            return false;
        } else {
            long result = db.insert(DATABASE_TABLE_NAME, null, contentValues);
            return result != -1;
        }
    }


    public boolean updateUserData(String id, String name, String email, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("contact", contact);

        if (name.matches("") || email.matches("") || contact.matches("")) {
            return false;
        }
        else {
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE id=? ", new String[]{id});

            if (cursor.getCount() > 0) {
                long result = db.update(DATABASE_TABLE_NAME, contentValues, " id=? ", new String[]{id});
                return result != -1;
            } else {
                return false;
            }
        }
    }


    public boolean deleteUserData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE id=? ", new String[]{id});

        if (cursor.getCount() > 0) {
            long result = db.delete(DATABASE_TABLE_NAME, " id=? ", new String[]{id});
            return result != -1;
        } else {
            return false;
        }
    }

    public Cursor searchUserData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE id=? ", new String[]{id});
    }


    public Cursor getUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME, null);
    }
}
