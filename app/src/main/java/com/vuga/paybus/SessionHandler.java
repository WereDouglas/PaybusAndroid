package com.vuga.paybus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class SessionHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bus";
    // sessions table name
    private static final String TABLE_SESSIONS = "sessions";
    private static final String KEY_SESSION_ID = "sessionID";
    private static final String KEY_DATE = "date";
    private static final String KEY_BUS = "bus";
    private static final String KEY_ROUTE = "route";
    private static final String KEY_SEATS = "seats";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SYNC = "sync";
    // sessions Table Columns names
    /////*******/
    private static final String TABLE_PAYMENTS = "payments";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_COST = "cost";
    private static final String KEY_CREATED = "created";
    private static final String KEY_SEAT = "seat";
    private static final String KEY_NAME = "name";
    private static final String KEY_LUG = "luggage";

    // sessions Expense
    /////*******/
    private static final String TABLE_EXPENSE= "expense";
    private static final String KEY_PARTICULAR = "particular";
    private static final String KEY_QTY = "qty";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_TOTAL = "total";


    public SessionHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SESSIONS_TABLE = "CREATE TABLE " + TABLE_SESSIONS + "("
                + KEY_SESSION_ID + " TEXT," + KEY_DATE+ " TEXT,"+ KEY_BUS + " TEXT," + KEY_ROUTE+ " TEXT," + KEY_SEATS + " TEXT,"+ KEY_STATUS + " TEXT,"+ KEY_SYNC + " TEXT,"+ KEY_COST + " TEXT"+ ")";
        db.execSQL(CREATE_SESSIONS_TABLE);

        String CREATE_PAYMENTS_TABLE = "CREATE TABLE " + TABLE_PAYMENTS + "("
                + KEY_BARCODE + " TEXT," + KEY_DATE+ " TEXT,"+ KEY_CONTACT + " TEXT," + KEY_COST+ " TEXT," + KEY_CREATED + " TEXT,"+ KEY_SEAT + " TEXT," + KEY_NAME + " TEXT,"+KEY_SYNC+" TEXT,"+ KEY_SESSION_ID +" TEXT,"+ KEY_LUG +" TEXT"+ ")";
        db.execSQL(CREATE_PAYMENTS_TABLE);

        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE + "("
                + KEY_SESSION_ID + " TEXT,"+ KEY_PARTICULAR + " TEXT," + KEY_QTY+ " TEXT," + KEY_UNIT + " TEXT,"+ KEY_TOTAL + " TEXT,"+ KEY_SYNC + " TEXT"+ ")";
        db.execSQL(CREATE_EXPENSE_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        // Create tables again
        onCreate(db);
    }
    void addSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, session.getSessionID()); // session Name
        values.put(KEY_DATE, session.getDate()); // session Phone
        values.put(KEY_ROUTE, session.getRoute());
        values.put(KEY_SEATS, session.getSeat());
        values.put(KEY_STATUS, session.getStatus());
        values.put(KEY_SYNC, session.getSync());
        values.put(KEY_BUS, session.getBus());
        values.put(KEY_COST, session.getCost());
        // Inserting Row
        db.insert(TABLE_SESSIONS, null, values);
        db.close(); // Closing database connection
    }
    // Getting single session
    Session getSession(String sessionID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SESSIONS, new String[] { KEY_SESSION_ID, KEY_STATUS }, KEY_BUS + "=?",
                new String[] { String.valueOf(sessionID) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Session session = new Session(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        // return session
        return session;
    }

    // Getting All sessions
    public ArrayList<Session> getAllSessions() {
        ArrayList<Session> sessionList = new ArrayList<Session>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SESSIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Session Session = new Session();
                Session.setSessionID((cursor.getString(0)));
                Session.setDate(cursor.getString(1));
                Session.setRoute(cursor.getString(3));
                Session.setSeat(cursor.getString(4));
                Session.setStatus(cursor.getString(5));
                Session.setSync(cursor.getString(6));
                Session.setBus(cursor.getString(2));
                Session.setCost(cursor.getString(7));
                sessionList.add(Session);
            } while (cursor.moveToNext());
        }

        // return session list
        return sessionList;
    }
    public ArrayList<Session> Sync() {
        ArrayList<Session> sessionList = new ArrayList<Session>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SESSIONS + " WHERE "+ KEY_SYNC +" <> 't'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Session Session = new Session();
                Session.setSessionID((cursor.getString(0)));
                Session.setDate(cursor.getString(1));
                Session.setRoute(cursor.getString(3));
                Session.setSeat(cursor.getString(4));
                Session.setStatus(cursor.getString(5));
                Session.setSync(cursor.getString(6));
                Session.setBus(cursor.getString(2));
                Session.setCost(cursor.getString(7));
                sessionList.add(Session);
            } while (cursor.moveToNext());
        }

        // return session list
        return sessionList;
    }
    public String updateSync(String sessionID ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE sessions SET sync='t' WHERE sessionID='"+sessionID+"' ");

        return "Updated";
    }


    // Updating single session
    public int updateSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, session.getSessionID());
        values.put(KEY_BUS, session.getBus());

        // updating row
        return db.update(TABLE_SESSIONS, values, KEY_SESSION_ID + " = ?",
                new String[] { String.valueOf(session.getSessionID()) });
    }

    // Deleting single session
    public void deleteSession(String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSIONS, KEY_SESSION_ID+ " = ?",
                new String[] { String.valueOf(sessionID) });
        db.close();
    }


    // Getting sessions Count
    public int getsessionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SESSIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

}