package com.example.psmeetease;

/*
 * Name:        Parampal Singh
 * Student no.: 7003114
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MeetingsDB";
    private static final int DATABASE_VERSION = 1; // Version constant
    private static final String TABLE_MEETINGS = "meetings";
    private static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_PHONE = "phone";

    // Query to create the database
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_MEETINGS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE + " TEXT, " +
            COL_DATE + " TEXT, " +
            COL_TIME + " TEXT, " +
            COL_PHONE + " TEXT" + ")";

    // Constructor
    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Called when db is created for the first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    /*
     * Called when db version is upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        onCreate(db);
    }

    /*
     * Method to insert meeting into db
     */
    public long insertMeeting(String title, String date, String time, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_PHONE, phone);
        long result = db.insert(TABLE_MEETINGS, null, values);
        db.close();
        return result;
    }

    /*
     * Method to delete a meeting for a specific Date
     */
    public boolean deleteMeetingsByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("meetings", "date=?", new String[]{date});
        db.close();
        return rowsDeleted > 0;
    }

    /*
     * Method to get list of all existing meetings
     */
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEETINGS, null);

        if (cursor != null) {
            int colIdIndex = cursor.getColumnIndex(COL_ID);
            int colTitleIndex = cursor.getColumnIndex(COL_TITLE);
            int colDateIndex = cursor.getColumnIndex(COL_DATE);
            int colTimeIndex = cursor.getColumnIndex(COL_TIME);
            int colPhoneIndex = cursor.getColumnIndex(COL_PHONE);

            if (colIdIndex == -1 || colTitleIndex == -1 || colDateIndex == -1 || colTimeIndex == -1 || colPhoneIndex == -1) {
                throw new IllegalStateException("One or more columns missing in the db table.");
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(colIdIndex);
                    String title = cursor.getString(colTitleIndex);
                    String date = cursor.getString(colDateIndex);
                    String time = cursor.getString(colTimeIndex);
                    String phone = cursor.getString(colPhoneIndex);

                    meetings.add(new Meeting(id, title, date, time, phone));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return meetings;
    }

    /*
     * Method to update changes in existing meetings
     */
    public boolean updateMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TITLE, meeting.getTitle());
        values.put(COL_DATE, meeting.getDate());
        values.put(COL_TIME, meeting.getTime());
        values.put(COL_PHONE, meeting.getPhone());

        int rows = db.update(TABLE_MEETINGS, values, COL_ID + " = ?", new String[]{String.valueOf(meeting.getId())});
        db.close();
        return rows > 0;
    }

    /*
     * Method to get meetings for a specific date
     */
    public List<Meeting> getMeetingsByDate(String date) {
        List<Meeting> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEETINGS + " WHERE " + COL_DATE + " = ?", new String[]{date});

        if (cursor != null) {
            int colIdIndex = cursor.getColumnIndex(COL_ID);
            int colTitleIndex = cursor.getColumnIndex(COL_TITLE);
            int colDateIndex = cursor.getColumnIndex(COL_DATE);
            int colTimeIndex = cursor.getColumnIndex(COL_TIME);
            int colPhoneIndex = cursor.getColumnIndex(COL_PHONE);

            if (colIdIndex == -1 || colTitleIndex == -1 || colDateIndex == -1 || colTimeIndex == -1 || colPhoneIndex == -1) {
                throw new IllegalStateException("One or more columns missing in the db table.");
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(colIdIndex);
                    String title = cursor.getString(colTitleIndex);
                    String meetingDate = cursor.getString(colDateIndex);
                    String time = cursor.getString(colTimeIndex);
                    String phone = cursor.getString(colPhoneIndex);

                    meetings.add(new Meeting(id, title, meetingDate, time, phone));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return meetings;
    }

    /*
     * Method to delete All meetings
     */
    public boolean deleteAllMeetings() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEETINGS, null, null); // This deletes all rows in the table
        db.close();
        return rowsDeleted > 0;
    }
}
