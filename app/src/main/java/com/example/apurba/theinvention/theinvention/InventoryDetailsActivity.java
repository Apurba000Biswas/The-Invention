package com.example.apurba.theinvention.theinvention;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;


/*
 * Created by Apurba on 4/30/2018.
 */

public class InventoryDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri selectedInventoryUri = null;
    private CollapsingToolbarLayout mCollapsingToolBar;

    private TextView statusTexView;
    //private TextView urlTextView;
    private TextView descTextView;
    private TextView platformTextView;
    private TextView typeTextView;
    private Button gotoWebsite;

    private String mName;
    private String url = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invention_details);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolBar = findViewById(R.id.collapse_toolbar);

        statusTexView = findViewById(R.id.status);
        descTextView = findViewById(R.id.description);
        platformTextView = findViewById(R.id.platform);
        typeTextView = findViewById(R.id.type);
        gotoWebsite = findViewById(R.id.goto_website);

        setUpGotoWebsiteButton();

        setUpEditInvention();

        Intent intent = getIntent();
        selectedInventoryUri = intent.getData();

        if (selectedInventoryUri != null ){
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
    }

    private void setUpGotoWebsiteButton(){
        gotoWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(url)){
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }catch (ActivityNotFoundException ex){
                        Toast.makeText(InventoryDetailsActivity.this, "Not valid url", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(InventoryDetailsActivity.this, "No url found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpEditInvention(){
        FloatingActionButton editButton = findViewById(R.id.edit_invention);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorInent = new Intent(InventoryDetailsActivity.this, EditorActivity.class);
                editorInent.setData(selectedInventoryUri);
                startActivity(editorInent);
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
            case R.id.action_delete_invention:
                showDeleteConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_invention_dialog_msg);
        // for positive button
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInvention();
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

    private void deleteInvention(){
        int rowsDeleted = 0;
        rowsDeleted = getContentResolver().delete(
                selectedInventoryUri,  // uri
                null,           // selection
                null);    // selectionArgs
        if (rowsDeleted != 0){
            Toast.makeText(this, "" + mName + " deleted successfully", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Error with deleting " + mName, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {InventoryEntry._ID,
        InventoryEntry.COLUMN_INVENTORY_NAME,
        InventoryEntry.COLUMN_STATUS,
        InventoryEntry.COLUMN_INVENTORY_URL,
        InventoryEntry.COLUMN_INVENTORY_DESCRIPTION,
        InventoryEntry.COLUMN_INVENTORY_PLATFORM,
        InventoryEntry.COLUMN_INVENTORY_TYPE};

        return new CursorLoader(this,
                selectedInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            int nameColIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int statusColIndex = data.getColumnIndex(InventoryEntry.COLUMN_STATUS);
            int urlColIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_URL);
            int descColIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION);
            int platformColIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PLATFORM);
            int typeColIndex = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_TYPE);

            mName = data.getString(nameColIndex);
            int status = data.getInt(statusColIndex);
            url = data.getString(urlColIndex);
            String description = data.getString(descColIndex);
            String platform = data.getString(platformColIndex);
            String type = data.getString(typeColIndex);

            mCollapsingToolBar.setTitle(mName);
            setAllViews(status, description, platform, type);
        }
    }

    private void setAllViews(int status, String description, String platform, String type){
        statusTexView.setText(InventoryEntry.getValidStatus(status));
        descTextView.setText(description);
        platformTextView.setText(platform);
        typeTextView.setText(type);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        statusTexView.setText("");
        url = "";
        descTextView.setText("");
        platformTextView.setText("");
        typeTextView.setText("");
    }
}
