package com.example.apurba.theinvention.theinvention.data;

/*
 * Created by Apurba on 4/29/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper{
    public static String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "TheInvention.db";
    private static final int DATABASE_VERSION = 1; // database version

    public InventoryDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table sql statement to cretae datatbase

        /**CREATE TABLE theInventory (_id INTEGER PRIMARY KEY AUTOINCREMENT,
         name TEXT NOT NULL,
         status INTEGER NOT NULL DEFAULT 2,
         url TEXT, image INTEGER DEFAULT 0,
         description TEXT,
         paltform TEXT NOT NULL,
         type TEXT)
         */
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ( " +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 2, " +
                InventoryEntry.COLUMN_INVENTORY_URL + " TEXT, " +
                InventoryEntry.COLUMN_INVENTORY_DESCRIPTION + " TEXT, " +
                InventoryEntry.COLUMN_INVENTORY_PLATFORM + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_TYPE + " TEXT);";
        Log.v(LOG_TAG,SQL_CREATE_INVENTORY_TABLE);
        //String SQL_DROP_TABLE = "DROP TABLE " + InventoryEntry.TABLE_NAME + ";";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here
    }
}
