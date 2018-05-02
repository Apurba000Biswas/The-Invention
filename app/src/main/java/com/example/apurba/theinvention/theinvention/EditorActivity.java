package com.example.apurba.theinvention.theinvention;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

/*
 * Created by Apurba on 5/1/2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText platformEditText;
    private EditText typeEditText;
    private EditText urlEditText;
    private Spinner mStatusSpinner;
    private ImageView saveButton;

    private int mStatus;
    private String name;
    private String description;
    private String platform;
    private String type;
    private String url;

    private Uri selectedInventUri = null;
    private static final int EXISTING_INVENTORY_LOADER = 0;

    private boolean mInventionHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventionHasChanged = true;
            return false;
        }
    };

    private ActionMode mActionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        Intent intent = getIntent();
        selectedInventUri  = intent.getData();

        mStatusSpinner = findViewById(R.id.status_spinner);
        nameEditText = findViewById(R.id.edit_invention_name);
        descriptionEditText = findViewById(R.id.edit_description);
        platformEditText = findViewById(R.id.edit_platform);
        typeEditText = findViewById(R.id.edit_type);
        urlEditText = findViewById(R.id.edit_url);

        saveButton = findViewById(R.id.save_button);

        setUpSpinner();

        if (selectedInventUri != null ){
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }else {
            TextView header = findViewById(R.id.editor_header);
            header.setText(R.string.header_name);
        }

        nameEditText.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        platformEditText.setOnTouchListener(mTouchListener);
        typeEditText.setOnTouchListener(mTouchListener);
        urlEditText.setOnTouchListener(mTouchListener);
        mStatusSpinner.setOnTouchListener(mTouchListener);

        urlEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mActionMode = EditorActivity.this.startActionMode(new ActionBarCallBack());
                return true;
            }
        });

        setUpSaveButton();
    }

    @Override
    public void onBackPressed() {
        // if not changed
        if (!mInventionHasChanged){
            super.onBackPressed();
            return;
        }
        // if changed
        DialogInterface.OnClickListener discardButtonClickeListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickeListener);

    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        // positive button
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        // negative button
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setUpSaveButton(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllEditTextValue();
                try {
                    checkAllValues();
                    saveInvention();
                    finish();
                }catch (IllegalValueException ex){
                    Toast.makeText(EditorActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveInvention(){
        ContentValues values = getContentValues();
        if (selectedInventUri == null){
            // perform insertion
            Uri responseUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (responseUri == null){
                // failed
                Toast.makeText(this, "Eror with saving invention", Toast.LENGTH_SHORT).show();
            }else{
                // successful
                Toast.makeText(this, "Invention saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            // perform update
            int rowsUpdated = getContentResolver().update(selectedInventUri, values, null, null);
            if (rowsUpdated == 0){
                // failed
                Toast.makeText(this, "Eror with updating invention", Toast.LENGTH_SHORT).show();
            }else {
                // successful
                Toast.makeText(this, "Invention updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, name);
        values.put(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, description);
        values.put(InventoryEntry.COLUMN_INVENTORY_PLATFORM, platform);
        values.put(InventoryEntry.COLUMN_INVENTORY_TYPE, type);
        if (TextUtils.isEmpty(url)){
            values.put(InventoryEntry.COLUMN_INVENTORY_URL, "no url");
        }else{
            values.put(InventoryEntry.COLUMN_INVENTORY_URL, url);
        }
        values.put(InventoryEntry.COLUMN_STATUS, mStatus);
        return values;
    }

    private void checkAllValues() throws IllegalValueException {
        if (TextUtils.isEmpty(name)){
            throw new IllegalValueException("Invention requires a name");
        }else if (TextUtils.isEmpty(description)){
            throw new IllegalValueException("Description should have some words");
        }else if (TextUtils.isEmpty(platform)){
            throw new IllegalValueException("Invention requires a platform (Ex:- NOT SELECTED YET)");
        }else if (TextUtils.isEmpty(type)){
            throw new IllegalValueException("Invention requires a type (Ex:-ABSTRACT IDEA)");
        }
    }

    private void getAllEditTextValue(){
        name = nameEditText.getText().toString().trim();
        description = descriptionEditText.getText().toString().trim();
        platform = platformEditText.getText().toString().trim();
        type = typeEditText.getText().toString().trim();
        url = urlEditText.getText().toString().trim();
    }

    private void setUpSpinner(){
        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_option, android.R.layout.simple_spinner_item);
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mStatusSpinner.setAdapter(statusSpinnerAdapter);

        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.action_complete))) {
                        mStatus = InventoryEntry.STATUS_COMPLETE; // complete
                    } else if (selection.equals(getString(R.string.action_running))) {
                        mStatus = InventoryEntry.STATUS_RUNNING; // running
                    } else {
                        mStatus = InventoryEntry.STATUS_IN_FUTURE; // in future
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mStatus = InventoryEntry.STATUS_IN_FUTURE;
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
                selectedInventUri,
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

            setAllViews(name, status, url, description, platform, type);
        }
    }
    private void setAllViews(String name, int status, String url, String description, String platform, String type){
        nameEditText.setText(name);
        descriptionEditText.setText(description);
        platformEditText.setText(platform);
        typeEditText.setText(type);
        urlEditText.setText(url);

        switch (status){
            case InventoryEntry.STATUS_COMPLETE:
                mStatusSpinner.setSelection(InventoryEntry.STATUS_COMPLETE);
                break;
            case InventoryEntry.STATUS_RUNNING:
                mStatusSpinner.setSelection(InventoryEntry.STATUS_RUNNING);
                break;
            case InventoryEntry.STATUS_IN_FUTURE:
                mStatusSpinner.setSelection(InventoryEntry.STATUS_IN_FUTURE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        descriptionEditText.setText("");
        platformEditText.setText("");
        typeEditText.setText("");
        urlEditText.setText("");
        mStatusSpinner.setSelection(InventoryEntry.STATUS_IN_FUTURE);
    }








    class ActionBarCallBack implements ActionMode.Callback{
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int id = menuItem.getItemId();
            if(id == R.id.item_paste)
            {
                //Toast.makeText(EditorActivity.this,"paste selected",Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if(clipboard.hasPrimaryClip()== true) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    String pasteText = item.getText().toString();
                    urlEditText.setText(pasteText);
                    Toast.makeText(getApplicationContext(), " Paste Complete ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    }
}

class IllegalValueException extends Exception {
     IllegalValueException(String message) {
        super(message);
    }
}


