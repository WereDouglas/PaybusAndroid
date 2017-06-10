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
public class ExpenseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_TOTAL = "bus";
    // expenses table name
    private static final String TABLE_EXPENSE= "expense";
    private static final String KEY_SESSION_ID = "sessionID";
    private static final String KEY_PARTICULAR = "particular";
    private static final String KEY_QTY = "qty";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_SYNC = "sync";

    public ExpenseHandler(Context context) {
        super(context, DATABASE_TOTAL, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSE + "("
                + KEY_SESSION_ID + " TEXT,"+ KEY_PARTICULAR + " TEXT," + KEY_QTY+ " TEXT," + KEY_UNIT + " TEXT,"+ KEY_TOTAL + " TEXT,"+ KEY_SYNC + " TEXT"+ ")";
        db.execSQL(CREATE_EXPENSE_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        // Create tables again
        onCreate(db);
    }
    void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, expense.getSessionID()); // expense Name
        values.put(KEY_PARTICULAR, expense.getParticular());
        values.put(KEY_QTY, expense.getQty());
        values.put(KEY_UNIT, expense.getUnit());
        values.put(KEY_TOTAL, expense.getTotal());
        values.put(KEY_SYNC, expense.getSync());

        // Inserting Row
        db.insert(TABLE_EXPENSE, null, values);
        db.close(); // Closing database connection
    }
    // Getting single expense
    Expense getExpense(String expenseID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXPENSE, new String[] { KEY_SESSION_ID, KEY_SYNC }, KEY_PARTICULAR + "=?",
                new String[] { String.valueOf(expenseID) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Expense expense = new Expense(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return expense
        return expense;
    }

    // Getting All expenses
    public ArrayList<Expense> getAllExpenses() {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXPENSE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense Expense = new Expense();
                Expense.setSessionID((cursor.getString(0)));
                Expense.setParticular(cursor.getString(1));
                Expense.setQty(cursor.getString(2));
                Expense.setUnit(cursor.getString(3));
                Expense.setTotal(cursor.getString(4));
                Expense.setSync(cursor.getString(5));
                expenseList.add(Expense);
            } while (cursor.moveToNext());
        }

        // return expense list
        return expenseList;
    }
    public ArrayList<Expense> getWhere() {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXPENSE + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense Expense = new Expense();
                Expense.setSessionID((cursor.getString(0)));
                Expense.setParticular(cursor.getString(1));
                Expense.setQty(cursor.getString(2));
                Expense.setUnit(cursor.getString(3));
                Expense.setTotal(cursor.getString(4));
                Expense.setSync(cursor.getString(5));
                expenseList.add(Expense);
            } while (cursor.moveToNext());
        }
        // return payment list
        return expenseList;
    }
    public String updateSync(String particular) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE expense SET sync='t' WHERE particular='"+particular+"' ");

        return "Updated";
    }
    public ArrayList<Expense> Sync() {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXPENSE + " WHERE "+ KEY_SYNC +" = 'f'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense Expense = new Expense();
                Expense.setSessionID((cursor.getString(0)));
                Expense.setParticular(cursor.getString(1));
                Expense.setQty(cursor.getString(2));
                Expense.setUnit(cursor.getString(3));
                Expense.setTotal(cursor.getString(4));
                Expense.setSync(cursor.getString(5));
                expenseList.add(Expense);
            } while (cursor.moveToNext());
        }

        // return expense list
        return expenseList;
    }

    // Updating single expense
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, expense.getSessionID());
        values.put(KEY_PARTICULAR, expense.getParticular());

        // updating row
        return db.update(TABLE_EXPENSE, values, KEY_SESSION_ID + " = ?",
                new String[] { String.valueOf(expense.getSessionID()) });
    }
    public void deleteSession(String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE, KEY_SESSION_ID + " = ?",
                new String[]{String.valueOf(sessionID)});
        db.close();
    }

    // Deleting single expense
    public void deleteExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSE, KEY_SESSION_ID+ " = ?",
                new String[] { String.valueOf(expense.getSessionID()) });
        db.close();
    }
    public void delete() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_EXPENSE);

    }
    // Getting expenses Count
    public int getexpensesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXPENSE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

}