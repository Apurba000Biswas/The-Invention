package com.example.apurba.theinvention.theinvention;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;
import com.example.apurba.theinvention.theinvention.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new InventoryDbHelper(this);
        setUpFabButton();
    }

    private void setUpFabButton(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "ok i am on" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_insert_sample_invention:
                insertSampleInvention();
            case R.id.action_delete_all_invention:
                // do something
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertSampleInvention(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //create content values to put in the database
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, "The Invention");
        values.put(InventoryEntry.COLUMN_STATUS, InventoryEntry.STATUS_RUNNING);
        values.put(InventoryEntry.COLUMN_INVENTORY_URL, "no url yet");
        values.put(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, "This is all about new creation");
        values.put(InventoryEntry.COLUMN_INVENTORY_PLATFORM, "Android app");
        values.put(InventoryEntry.COLUMN_INVENTORY_TYPE, "Computer Software");

        //insert(String table, String nullColumnHack, ContentValues values)
        //The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        long rowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (rowId > 0){
            Toast.makeText(this, "Ok inserted sample data in " + rowId, Toast.LENGTH_SHORT).show();
        }
    }
}
