package com.example.apurba.theinvention.theinvention;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;


/*
 * Created by Apurba on 4/30/2018.
 */

public class InventoryDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri selectedInventoryUri = null;
    private CollapsingToolbarLayout mCollapsingToolBar;

    private TextView statusTexView;
    private TextView urlTextView;
    private TextView descTextView;
    private TextView plarformTextView;
    private TextView typeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invention_details);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolBar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);

        statusTexView = findViewById(R.id.status);
        urlTextView = findViewById(R.id.url);
        descTextView = findViewById(R.id.description);
        plarformTextView = findViewById(R.id.platform);
        typeTextView = findViewById(R.id.type);

        setUpEditInvention();

        Intent intent = getIntent();
        selectedInventoryUri = intent.getData();

        if (selectedInventoryUri != null ){
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
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

            String name = data.getString(nameColIndex);
            int status = data.getInt(statusColIndex);
            String url = data.getString(urlColIndex);
            String description = data.getString(descColIndex);
            String platform = data.getString(platformColIndex);
            String type = data.getString(typeColIndex);

            mCollapsingToolBar.setTitle(name);
            setAllViews(status, url, description, platform, type);
        }
    }

    private void setAllViews(int status, String url, String description, String platform, String type){
        statusTexView.setText(InventoryEntry.getValidStatus(status));
        urlTextView.setText(url);
        descTextView.setText(description);
        plarformTextView.setText(platform);
        typeTextView.setText(type);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        statusTexView.setText("");
        urlTextView.setText("");
        descTextView.setText("");
        plarformTextView.setText("");
        typeTextView.setText("");
    }
}
