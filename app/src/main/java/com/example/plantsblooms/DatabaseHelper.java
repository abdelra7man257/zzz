package com.example.plantsblooms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COLUMN_NAME = "name";
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns for user information
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Table and columns for blogs
    public static final String TABLE_BLOGS = "blogs";
    public static final String COLUMN_BLOG_ID = "_id";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_BLOG_TEXT = "blog_text";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT);";

    private static final String CREATE_TABLE_BLOGS =
            "CREATE TABLE " + TABLE_BLOGS + " (" +
                    COLUMN_BLOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_EMAIL + " TEXT, " +
                    COLUMN_BLOG_TEXT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate: Creating tables");
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BLOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOGS);
        onCreate(db);
    }

    // Method to check user credentials
    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + " = ? AND " +
                COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Method to insert a new user into the database
    public boolean insertUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, user.getName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(COLUMN_PASSWORD, user.getPassword());
            long result = db.insert(TABLE_USERS, null, values);
            db.close();
            return result != -1; // Return true if insertion was successful
        } catch (Exception e) {
            e.printStackTrace();
            // Log the exception message
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // Method to insert a new blog
    public boolean insertBlog(String userEmail, String blogText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_BLOG_TEXT, blogText);
        long result = db.insert(TABLE_BLOGS, null, values);
        return result != -1;
    }

    // Method to retrieve blogs for a specific user
    public List<BlogsList> getBlogsForUser(String userEmail) {
        List<BlogsList> blogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BLOGS +
                " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});


        Date currentDate = new Date();


        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(COLUMN_BLOG_ID);
            int id = (idIndex != -1) ? cursor.getInt(idIndex) : -1;

            int blogTextIndex = cursor.getColumnIndex(COLUMN_BLOG_TEXT);
            String blogText = (blogTextIndex != -1) ? cursor.getString(blogTextIndex) : "";

            if (id != -1) {
                blogs.add(new BlogsList(id, userEmail, blogText, currentDate));

            }
        }

        cursor.close();
        return blogs;
    }
}
