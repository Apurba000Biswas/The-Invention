package com.example.apurba.theinvention.theinvention;

/*
 * Created by Apurba on 4/30/2018.
 */


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.apurba.theinvention.theinvention.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter{

    // sutable constrauctor
    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.invention_name);
        TextView status = view.findViewById(R.id.invention_status);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        int statusColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_STATUS);

        String nameStr = cursor.getString(nameColumnIndex);
        int statusInt = cursor.getInt(statusColumnIndex);
        String statusStr = getStatus(statusInt);

        name.setText(nameStr);
        status.setText(statusStr);
    }

    private String getStatus(int status){
        switch (status){
            case InventoryContract.InventoryEntry.STATUS_COMPLETE:
                return "Project Complete";
            case InventoryContract.InventoryEntry.STATUS_RUNNING:
                return "Currently Running";
            case InventoryContract.InventoryEntry.STATUS_IN_FUTURE:
                return "Stacked - For future";
            default:
                return "Unknown";
        }
    }
}
