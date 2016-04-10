package com.yoavgibri.cash.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yoavgibri.cash.Expense;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Yoav on 21/02/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    public static final int VERSION = 2;
    public static final String DB_NAME = "cash";
    public static final String TABLE_NAME = "expenses";
    public static final String COL_UID = "_id";
    public static final String COL_TIME = "time";
    public static final String COL_NAME = "name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_PLACE = "place";
    public static final String COL_COMMENT = "comment";
    public static final String COL_TYPE = "type";
    public static final String COL_MONTH = "month";

    private SQLiteDatabase mDb;
    private ArrayList<Expense> mTempArray;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mTempArray = new ArrayList<>();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Version 1:
//        String sqlCreate = String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s DOUBLE, %4$s TEXT, %5$s DOUBLE, %6$s TEXT, %7$s TEXT)",
//                TABLE_NAME, COL_UID, COL_TIME, COL_NAME, COL_AMOUNT, COL_PLACE, COL_COMMENT);

        //Version 2:
        String sqlCreate = String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s DOUBLE, %4$s TEXT, %5$s DOUBLE, %6$s TEXT, %7$s TEXT, %8$s DOUBLE)",
                TABLE_NAME, COL_UID, COL_TIME, COL_TYPE, COL_AMOUNT, COL_PLACE, COL_COMMENT, COL_MONTH);
        db.execSQL(sqlCreate);

        Log.d(TAG, sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;

        while (upgradeTo <= newVersion){
            switch (upgradeTo){
                case 2:
                    db.execSQL("BEGIN TRANSACTION");
                    db.execSQL("ALTER TABLE "+TABLE_NAME+" RENAME TO tempTable");
                    db.execSQL(String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s DOUBLE, %4$s TEXT, %5$s DOUBLE, %6$s TEXT, %7$s TEXT, %8$s DOUBLE)",
                            TABLE_NAME, COL_UID, COL_TIME, COL_TYPE, COL_AMOUNT, COL_PLACE, COL_COMMENT, COL_MONTH));
                    db.execSQL("INSERT INTO "+TABLE_NAME+"("+COL_UID+", "+COL_TIME+", "+COL_TYPE+", "+COL_AMOUNT+", "+COL_PLACE+", "+COL_COMMENT+") " +
                            "SELECT "+COL_UID+", "+COL_TIME+", "+COL_NAME+", "+COL_AMOUNT+", "+COL_PLACE+", "+COL_COMMENT+" " +
                            "FROM tempTable");
                    db.execSQL("DROP TABLE tempTable");
                    db.execSQL("COMMIT");
                    break;
                case 3:
                    // do something
                    break;
                case 4:
                    // do something
                    break;
            }
            upgradeTo++;
        }

//        String sqlUpgrade = "DROP TABLE IF EXIST " + TABLE_NAME;
//        db.execSQL(sqlUpgrade);
//        onCreate(db);

        Log.d(TAG, "DB version changing from " + oldVersion + " to " + newVersion);
    }


    public void insertExpense(Expense newExpense) {
        mDb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, newExpense.getName());
        values.put(COL_AMOUNT, newExpense.getAmount());
        values.put(COL_PLACE, newExpense.getPlace());
        values.put(COL_TIME, newExpense.getTime());
        values.put(COL_COMMENT, newExpense.getComment());
        values.put(COL_MONTH, newExpense.getMonth());

        mDb.insertOrThrow(TABLE_NAME, null, values);
        mDb.close();

        Log.d(TAG, "A new expense has been added!\n" + newExpense.toString());
    }

    public int getTotalExpenses() {
        mDb = getReadableDatabase();
        int totalExpenses = 0;
        long monthStart = getThisMonthStart();
        String selection = COL_TIME+">"+monthStart;
        Cursor c = mDb.query(TABLE_NAME, new String[]{COL_AMOUNT}, selection, null, null, null, null);
        while (c.moveToNext()) {
            totalExpenses += c.getInt(c.getColumnIndex(COL_AMOUNT));
        }
        c.close();

        return totalExpenses;
    }



//    public ArrayList<Integer> getMonths(){
//        mDb = getReadableDatabase();
//        Cursor c = mDb.query(TABLE_NAME, new String[]{COL_MONTH}, null, null, null, null, null);
//        ArrayList<Integer> months = new ArrayList<>();
//
//        while (c.moveToNext()){
//            if (!months.contains(c.getInt(c.getColumnIndex(COL_MONTH))))
//            months.add(c.getInt(c.getColumnIndex(COL_MONTH)));
//        }
//        c.close();
//        return months;
//    }

    public Cursor getTotalsByMonth() {
        mDb = getReadableDatabase();
        return mDb.query(TABLE_NAME, new String[]{COL_MONTH, "SUM("+COL_AMOUNT+")"}, null, null, COL_MONTH, null, COL_MONTH);
    }

    public Cursor getMonthTotalsByType(String month){
        mDb = getReadableDatabase();
        return mDb.query(TABLE_NAME, new String[]{COL_TYPE, "SUM("+COL_AMOUNT+")"}, COL_MONTH+"="+month, null, COL_TYPE, null, COL_TYPE);
    }

    public ArrayList<Expense> getExpensesByMonth(String month){
        mDb = getReadableDatabase();
        ArrayList<Expense> expenses = new ArrayList<>();

        Cursor c = mDb.query(TABLE_NAME, null, COL_MONTH+"="+month, null, null, null, null, null);
        while (c.moveToNext()){
            Expense expense = expenseFromCursor(c);
            expenses.add(expense);
        }
        c.close();
        return expenses;
    }

    private long getThisMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis();
    }

    public ArrayList<Expense> getExpensesArray() {
        mDb = getReadableDatabase();
        ArrayList<Expense> expenses = new ArrayList<Expense>();
        Cursor c = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToPosition(c.getCount());
        while (c.moveToPrevious()) {
            Expense expense = expenseFromCursor(c);
            expenses.add(expense);
        }
//        expenses.add(new Expense());
        c.close();
        mTempArray = expenses;
        return expenses;
    }

    public Expense getExpense(long id) {
        mDb = getReadableDatabase();
        Cursor c = mDb.query(TABLE_NAME, null, COL_UID+"="+id, null, null, null, null);
        c.moveToFirst();
        Expense expense = expenseFromCursor(c);
        c.close();
        return expense;
    }

    public void updateExpense(Expense editExpense) {
        mDb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, editExpense.getName());
        values.put(COL_AMOUNT, editExpense.getAmount());
        values.put(COL_PLACE, editExpense.getPlace());
        values.put(COL_TIME, editExpense.getTime());
        values.put(COL_COMMENT, editExpense.getComment());

        mDb.update(TABLE_NAME, values, COL_UID+"="+editExpense.getId(), null);
        Log.d(TAG, "expense "+editExpense.toString()+" has updated");
        mDb.close();
    }

    @NonNull
    private Expense expenseFromCursor(Cursor c) {
        String name = c.getString(c.getColumnIndex(COL_TYPE));
        String place = c.getString(c.getColumnIndex(COL_PLACE));
        String comment = c.getString(c.getColumnIndex(COL_COMMENT));
        long time = c.getLong(c.getColumnIndex(COL_TIME));
        int amount = c.getInt(c.getColumnIndex(COL_AMOUNT));

        Expense expense = new Expense(name, place, comment, time, amount);
        expense.setId(c.getLong(c.getColumnIndex(COL_UID)));

        return expense;
    }

    public void clearAllExpenses() {
        mDb = getWritableDatabase();
        mDb.delete(TABLE_NAME, null, null);
        mDb.close();
    }

    public void removeExpense(long position) {
        mDb = getWritableDatabase();
        mDb.delete(TABLE_NAME, COL_UID+"="+position, null);
        mDb.close();
    }

    public ArrayList<Expense> getTempArray() {
        return mTempArray;
    }


}
