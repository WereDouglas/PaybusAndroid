package com.vuga.paybus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class PaymentHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bus";
    // Payments table name
    private static final String TABLE_PAYMENTS = "payments";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_DATE = "date";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_COST = "cost";
    private static final String KEY_CREATED = "created";
    private static final String KEY_SEAT = "seat";
    private static final String KEY_NAME = "name";
    private static final String KEY_SYNC = "sync";
    private static final String KEY_LUG = "luggage";
    private static final String KEY_SESSION_ID = "sessionID";
    // Payments Table Columns names

    public PaymentHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PAYMENTS_TABLE = "CREATE TABLE " + TABLE_PAYMENTS + "("
                + KEY_BARCODE + " TEXT," + KEY_DATE+ " TEXT,"+ KEY_CONTACT + " TEXT," + KEY_COST+ " TEXT," + KEY_CREATED + " TEXT,"+ KEY_SEAT + " TEXT," + KEY_NAME + " TEXT,"+KEY_SYNC+" TEXT,"+ KEY_SESSION_ID +" TEXT,"+ KEY_LUG +" TEXT"+ ")";
        db.execSQL(CREATE_PAYMENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        // Create tables again
        onCreate(db);
    }
    void addPayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, payment.getBarcode()); // Payment Name
        values.put(KEY_DATE, payment.getDate()); // Payment Phone
        values.put(KEY_CONTACT, payment.getContact());
        values.put(KEY_COST, payment.getCost());
        values.put(KEY_CREATED, payment.getCreated());
        values.put(KEY_SEAT, payment.getSeat());
        values.put(KEY_CONTACT, payment.getContact());
        values.put(KEY_NAME, payment.getName());
        values.put(KEY_SYNC, payment.getSync());
        values.put(KEY_SESSION_ID, payment.getSessionID());
        values.put(KEY_LUG, payment.getLuggage());
        // Inserting Row
        db.insert(TABLE_PAYMENTS, null, values);
        db.close(); // Closing database connection
    }
    // Getting single payment
    Payment getPayment(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PAYMENTS, new String[] { KEY_BARCODE,
                        KEY_NAME, KEY_SEAT }, KEY_CONTACT + "=?",
                new String[] { String.valueOf(barcode) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Payment payment = new Payment(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
        // return payment
        return payment;
    }
    public void deleteSession(String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAYMENTS, KEY_SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionID)});
        db.close();
    }

    // Getting All Payments
    public ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> paymentList = new ArrayList<Payment>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment();
                payment.setBarcode((cursor.getString(0)));
                payment.setDate(cursor.getString(1));
                payment.setContact(cursor.getString(2));
                payment.setCost(cursor.getString(3));
                payment.setCreated(cursor.getString(4));
                payment.setSeat(cursor.getString(5));
                payment.setEmail(cursor.getString(6));
                payment.setName(cursor.getString(7));
                payment.setSessionID(cursor.getString(8));
                payment.setLuggage(cursor.getString(9));
             //   payment.setSync(cursor.getString(8));
                // Adding payment to list
                paymentList.add(payment);
            } while (cursor.moveToNext());
        }
        // return payment list
        return paymentList;
    }
    ////get all payments where

    public ArrayList<Payment> getWhere(String sessionID) {
        ArrayList<Payment> paymentList = new ArrayList<Payment>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENTS + " WHERE "+ KEY_SESSION_ID +" = '"+sessionID+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment();
                payment.setBarcode((cursor.getString(0)));
                payment.setDate(cursor.getString(1));
                payment.setContact(cursor.getString(2));
                payment.setCost(cursor.getString(3));
                payment.setCreated(cursor.getString(4));
                payment.setSeat(cursor.getString(5));
                payment.setEmail(cursor.getString(6));
                payment.setName(cursor.getString(7));
                payment.setSessionID(cursor.getString(8));
                payment.setLuggage(cursor.getString(9));
                //   payment.setSync(cursor.getString(8));
                // Adding payment to list
                paymentList.add(payment);
            } while (cursor.moveToNext());
        }

        // return payment list
        return paymentList;
    }
    public ArrayList<Payment> Sync(String sessionID) {
        ArrayList<Payment> paymentList = new ArrayList<Payment>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENTS + " WHERE "+ KEY_SYNC +" = 'f' AND sessionID ='"+sessionID+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Payment payment = new Payment();
                payment.setBarcode((cursor.getString(0)));
                payment.setDate(cursor.getString(1));
                payment.setContact(cursor.getString(2));
                payment.setCost(cursor.getString(3));
                payment.setCreated(cursor.getString(4));
                payment.setSeat(cursor.getString(5));
                payment.setEmail(cursor.getString(6));
                payment.setName(cursor.getString(7));
                payment.setSessionID(cursor.getString(8));
                payment.setLuggage(cursor.getString(9));
                //   payment.setSync(cursor.getString(8));
                // Adding payment to list
                paymentList.add(payment);
            } while (cursor.moveToNext());
        }

        // return payment list
        return paymentList;
    }

    // Updating single payment
    public int updatePayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, payment.getName());
        values.put(KEY_CONTACT, payment.getContact());

        // updating row
        return db.update(TABLE_PAYMENTS, values, KEY_BARCODE + " = ?",
                new String[] { String.valueOf(payment.getBarcode()) });
    }
    public String updateSync(String barcode ) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE payments SET sync='t' WHERE barcode='"+barcode+"' ");

        return "Updated";
    }

    // Deleting single payment
    public void deletePayment(Payment payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAYMENTS, KEY_BARCODE+ " = ?",
                new String[] { String.valueOf(payment.getBarcode()) });
        db.close();
    }


    // Getting payments Count
    public int getPaymentsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PAYMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
