package com.example.apurba.theinvention.theinvention;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract;
import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int INVENTION_LOADER = 0;
    private InventoryCursorAdapter mCursorAdapter;

    private int mSize = -10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpFabButton();
        setUpSampleInventory();
        setUpRemoveAllInventory();

        ListView inventionListView = findViewById(R.id.invention_list);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventionListView.setAdapter(mCursorAdapter);

        setListViewToClickResponse(inventionListView);

        getSupportLoaderManager().initLoader(INVENTION_LOADER, null, this);
    }

    private void setUpRemoveAllInventory(){
        ImageView removeAll = findViewById(R.id.remove_all);
        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSize == 0){
                    Toast.makeText(MainActivity.this, "Nothing to delete here", Toast.LENGTH_SHORT).show();
                }else {
                    showDeleteConfirmationDialog();
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_invention_dialog_message);
        // for positive button
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllInventions();
            }
        });
        // for negative button
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllInventions(){
        int rowsDeleted = 0;
        rowsDeleted = getContentResolver().delete(
                InventoryEntry.CONTENT_URI,  // uri
                null,           // selection
                null);    // selectionArgs
        if (rowsDeleted != 0){
            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Error with deleting All inventions", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpSampleInventory(){
        ImageView addSampleInventory = findViewById(R.id.add_sample);
        addSampleInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSampleInsertConfirmationDialog();
            }
        });
    }
    private void showSampleInsertConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_sample_invention_dialog_msg);
        // for positive button
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                insertSampleInvention();
            }
        });
        // for negative button
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertSampleInvention(){
        ContentValues values = getContentValues();
        Uri responeUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
        if (responeUri == null){
            //faild
            Toast.makeText(this, "Error with inserting Invention", Toast.LENGTH_SHORT).show();
        }else{
            // succesfull
            Toast.makeText(this, "Sample Invention saved", Toast.LENGTH_SHORT).show();
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

    private void setListViewToClickResponse(ListView list){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "id : " + id, Toast.LENGTH_SHORT ).show();
                Intent detailsInent = new Intent(MainActivity.this, InventoryDetailsActivity.class);
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                detailsInent.setData(uri);
                startActivity(detailsInent);
            }
        });
    }

    private void setUpFabButton(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorInent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editorInent);
            }
        });
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
        mSize = data.getCount();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
