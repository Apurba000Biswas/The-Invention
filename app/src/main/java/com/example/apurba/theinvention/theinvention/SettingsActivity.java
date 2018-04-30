package com.example.apurba.theinvention.theinvention;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract;

/*
 * Created by Apurba on 4/30/2018.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        TextView insert = findViewById(R.id.action_insert_sample_invention);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertSampleInvention();
                finish();
            }
        });
    }
    private void insertSampleInvention(){
        ContentValues values = getContentValues();
        Uri responeUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
        if (responeUri == null){
            //faild
            Toast.makeText(this, "Error with inserting Invention", Toast.LENGTH_SHORT).show();
        }else{
            // succesfull
            Toast.makeText(this, "Invention saved", Toast.LENGTH_SHORT).show();
        }
    }
    private ContentValues getContentValues(){
        //create content values to put in the database
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, "The Invention");
        values.put(InventoryContract.InventoryEntry.COLUMN_STATUS, InventoryContract.InventoryEntry.STATUS_RUNNING);
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_URL, "no url yet");
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, "This is all about new creation");
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PLATFORM, "Android app");
        values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_TYPE, "Computer Software");

        return values;
    }
}
