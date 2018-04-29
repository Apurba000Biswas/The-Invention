package com.example.apurba.theinvention.theinvention.data;

/*
 * Created by Apurba on 4/29/2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider{

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    // Uri matcher code for inventory table
    private static final int INVENTORY = 100;
    // Uri matcher code for single row of inventory table
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // get readeble database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // declare a cursor
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                /**
                 * Cursor query (String table,
                 String[] columns,
                 String selection,
                 String[] selectionArgs,
                 String groupBy,
                 String having,
                 String orderBy)
                 */
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new  IllegalArgumentException("Unknown uri " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                // insert invention
                return insertInvention(uri, contentValues);
            default:
                throw new  IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInvention(Uri uri, ContentValues values){
        // check the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
        if (TextUtils.isEmpty(name)){
            throw new IllegalArgumentException("Invention requires a name");
        }
        // check status is valid
        Integer status = values.getAsInteger(InventoryEntry.COLUMN_STATUS);
        if (status == null || !InventoryEntry.isValidStatus(status)){
            throw new IllegalArgumentException("Invention requires a valid status - (Complete, Runing, In future)");
        }
        // check platform is not null
        String platform = values.getAsString(InventoryEntry.COLUMN_INVENTORY_PLATFORM);
        if (TextUtils.isEmpty(platform)){
            throw new IllegalArgumentException("Invention requires a platform");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // notifie all listeners that data has changed for the inventory content Uri
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
