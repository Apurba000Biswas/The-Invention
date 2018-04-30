package com.example.apurba.theinvention.theinvention;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int INVENTION_LOADER = 0;
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpFabButton();

        ListView inventionListView = findViewById(R.id.invention_list);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventionListView.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(INVENTION_LOADER, null, this);
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
        ContentValues values = getContentValues();
        Uri responeUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
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
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, "The Invention");
        values.put(InventoryEntry.COLUMN_STATUS, InventoryEntry.STATUS_RUNNING);
        values.put(InventoryEntry.COLUMN_INVENTORY_URL, "no url yet");
        values.put(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, "This is all about new creation");
        values.put(InventoryEntry.COLUMN_INVENTORY_PLATFORM, "Android app");
        values.put(InventoryEntry.COLUMN_INVENTORY_TYPE, "Computer Software");

        return values;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // make projection how many column we are interested now
        String[]projection = {InventoryEntry._ID, InventoryEntry.COLUMN_INVENTORY_NAME, InventoryEntry.COLUMN_STATUS};
        //CursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
