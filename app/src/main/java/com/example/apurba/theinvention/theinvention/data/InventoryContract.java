package com.example.apurba.theinvention.theinvention.data;

/*
 * Created by Apurba on 4/28/2018.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract(){
    }

    public static final String CONTENT_AUTHORITY = "com.example.apurba.theinvention.theinvention";
    public static final Uri BASE_CONYENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";


    // define inventory table schema
    public static final class InventoryEntry implements BaseColumns{

        // define inventory table name
        public static final String TABLE_NAME = "inventory";
        // define uri for the table (for all row)
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONYENT_URI, PATH_INVENTORY);

        //define each collumn
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INVENTORY_NAME = "name";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_INVENTORY_URL = "url";
        public static final String COLUMN_INVENTORY_DESCRIPTION = "description";
        public static final String COLUMN_INVENTORY_PLATFORM = "platform";
        public static final String COLUMN_INVENTORY_TYPE = "type";

        public static final int STATUS_COMPLETE = 0;
        public static final int STATUS_RUNNING = 1;
        public static final int STATUS_IN_FUTURE = 2;

        /**The MIME type for a list of inventory
         * vnd.android.cursor.dir/com.example.apurba.theinvention.theinvention/inventory
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**The MIME type of a single inventory
         * vnd.android.cursor.item/com.example.apurba.theinvention.theinvention/inventory
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**Whether or not the given status is valid or not
         */
        public static boolean isValidStatus(int status){
            if (status == STATUS_COMPLETE || status == STATUS_RUNNING || status == STATUS_IN_FUTURE){
                return true;
            }
            return false;
        }
    }
}
