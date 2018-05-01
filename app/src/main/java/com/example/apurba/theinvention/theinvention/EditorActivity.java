package com.example.apurba.theinvention.theinvention;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.apurba.theinvention.theinvention.data.InventoryContract.InventoryEntry;

/*
 * Created by Apurba on 5/1/2018.
 */

public class EditorActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        mStatusSpinner = findViewById(R.id.status_spinner);
        nameEditText = findViewById(R.id.edit_invention_name);
        descriptionEditText = findViewById(R.id.edit_description);
        platformEditText = findViewById(R.id.edit_platform);
        typeEditText = findViewById(R.id.edit_type);
        urlEditText = findViewById(R.id.edit_url);
        saveButton = findViewById(R.id.save_button);

        setUpSpinner();
        setUpSaveButton();


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

        Uri responseUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        if (responseUri == null){
            Toast.makeText(this, "Eror with saving invention", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Invention saved", Toast.LENGTH_SHORT).show();
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
}

class IllegalValueException extends Exception {
    public IllegalValueException(String message) {
        super(message);
    }
}
