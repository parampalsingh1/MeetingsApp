package com.example.psmeetease;
/*
 * Name:        Parampal Singh
 * Student no.: 7003114
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class scheduleMeeting extends AppCompatActivity {
    private EditText meetingTitle, meetingDate, meetingTime, phoneNumber;
    private Database database; // Database instance
    private static final int REQUEST_CONTACT_PICKER = 1; // constant for contact picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);

        meetingTitle = findViewById(R.id.enterTitle);
        meetingDate = findViewById(R.id.enterDate);
        meetingTime = findViewById(R.id.enterTime);
        phoneNumber = findViewById(R.id.enterContactPhone);

        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }

        database = new Database(this); // Initialize database

        // Setting click listeners for date, time and contact selection
        meetingDate.setOnClickListener(v -> showDatePicker());
        meetingTime.setOnClickListener(v -> showTimePicker());
        phoneNumber.setOnClickListener(v -> openContacts());

        findViewById(R.id.saveButton).setOnClickListener(v -> saveMeeting()); // Click listener for save button

        ImageButton backButton = findViewById(R.id.backButton); // Click listener for back button
        backButton.setOnClickListener(v -> {finish();});
    }

    /*
     * Save Current state for rotation
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("meetingTitle", meetingTitle.getText().toString());
        outState.putString("meetingDate", meetingDate.getText().toString());
        outState.putString("meetingTime", meetingTime.getText().toString());
        outState.putString("phoneNumber", phoneNumber.getText().toString());
    }

    /*
     * Restore saved instances after rotation
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            meetingTitle.setText(savedInstanceState.getString("meetingTitle",""));
            meetingDate.setText(savedInstanceState.getString("meetingDate",""));
            meetingTime.setText(savedInstanceState.getString("meetingTime",""));
            phoneNumber.setText(savedInstanceState.getString("phoneNumber",""));
        }
    }

    /*
     * Method to save meeting details in database
     */
    private void saveMeeting() {
        String title = meetingTitle.getText().toString();
        String date = meetingDate.getText().toString();
        String time = meetingTime.getText().toString();
        String phone = phoneNumber.getText().toString();

        long result = database.insertMeeting(title, date, time, phone);

        if (result != -1) {
            Toast.makeText(this, "Meeting saved successfully!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(scheduleMeeting.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        }
        else { Toast.makeText(this, "Failed to save meeting.", Toast.LENGTH_SHORT).show(); }
    }

    /*
     * Method to show datePicker to select a date
     */
    @SuppressLint("NewApi")
    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" +
                            String.format("%02d", selectedMonth + 1) + "-" +
                            String.format("%02d", selectedDay);
                    meetingDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    /*
     * Method to show timePicker to pick a time
     */
    @SuppressLint("NewApi")
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    meetingTime.setText(formattedTime);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    /*
     * Method to handle contact picker results to get phone numbers from device
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONTACT_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contactUri = data.getData();
                String[] projection = { android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String contactName = cursor.getString(nameIndex);
                    phoneNumber.setText(contactName);
                    cursor.close();
                }
            }
        }
    }

    /*
     * Method to open contact picker and allow user to select a contact
     */
    @SuppressWarnings("deprecation")
    private void openContacts() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, REQUEST_CONTACT_PICKER);
    }
}
