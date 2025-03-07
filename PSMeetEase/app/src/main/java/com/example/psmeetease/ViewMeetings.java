package com.example.psmeetease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewMeetings extends AppCompatActivity {
    private RecyclerView recyclerView; // Recycle view to display meetings
    private MeetingAdapter meetingAdapter; // Adapter to bind meetings with recycle view
    private Database database;
    private CalendarView calendar;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meetings);

        // Initializing views and db
        recyclerView = findViewById(R.id.recyclerView);
        calendar = findViewById(R.id.meetingCalendar);
        database = new Database(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(savedInstanceState == null) selectedDate = getCurrentDate(); // default date as current date if no date selected

        showMeetingsForDate(selectedDate = getCurrentDate()); // Show meetings for current date by default

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" +
                    String.format("%02d", month + 1) + "-" +
                    String.format("%02d", dayOfMonth);
            showMeetingsForDate(selectedDate);
        });

        // Delete button listener
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> showDeleteDialog());

        // Push button listener
        Button pushButton = findViewById(R.id.pushButton);
        pushButton.setOnClickListener(v -> pushMeetingsToNextDay());

        // Back button listener
        ImageButton backButton = findViewById(R.id.backButton2);
        backButton.setOnClickListener(v -> {finish();});
    }

    /*
     * Save Current state for rotation
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(selectedDate!=null) outState.putString("selectedDate", selectedDate);
    }

    /*
     * Restore saved instances after rotation
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            selectedDate = savedInstanceState.getString("selectedDate", getCurrentDate());
            showMeetingsForDate(selectedDate);
        }
    }

    /*
     * Method to show delete dialog that asks for final confirmation
     */
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Meetings")
                .setMessage("Do you want to delete meetings for the selected day or all meetings?")
                .setPositiveButton("Delete for this day", (dialog, id) -> { deleteMeetingsForSelectedDate(); })
                .setNegativeButton("Delete All Meetings", (dialog, id) -> { deleteAllMeetings(); })
                .setNeutralButton("Cancel", (dialog, id) -> { dialog.dismiss(); });
        builder.create().show();
    }

    /*
     * Method to delete all meetings from db
     */
    private void deleteAllMeetings() {
        boolean isDeleted = database.deleteAllMeetings();
        if (isDeleted) { Toast.makeText(this, "All meetings deleted.", Toast.LENGTH_SHORT).show(); }
        showMeetingsForDate(null);
    }

    /*
     * Method to delete all meetings from db for a specific date
     */
    private void deleteMeetingsForSelectedDate() {
        if(selectedDate != null) {
            boolean isDeleted = database.deleteMeetingsByDate(selectedDate);
            if(isDeleted) {
                Toast.makeText(this, "Meeting(s) for " + selectedDate + " deleted.", Toast.LENGTH_SHORT).show();
            }
        }
        showMeetingsForDate(selectedDate);
    }

    /*
     * Method to get current system date
     */
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /*
     * Method to show all meetings for selected date
     */
    private void showMeetingsForDate(String date) {
        List<Meeting> allMeetings = database.getAllMeetings();
        System.out.println(allMeetings.size());
        List<Meeting> filteredMeetings = new ArrayList<>();

        for (Meeting meeting : allMeetings) { if (meeting.getDate().equals(date)) filteredMeetings.add(meeting); }
        if (filteredMeetings.isEmpty()) { Toast.makeText(this, "No meetings scheduled for this date.", Toast.LENGTH_SHORT).show(); }
        meetingAdapter = new MeetingAdapter(filteredMeetings);
        recyclerView.setAdapter(meetingAdapter);
    }

    /*
     * Method to get next date when meeting has to be pushed.
     */
    private String getNextTargetDate(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(dateFormat.parse(currentDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.FRIDAY) {
            // Push Friday to next Monday
            calendar.add(Calendar.DAY_OF_YEAR, 3);
        } else if (dayOfWeek == Calendar.SATURDAY) {
            // Push Saturday to next Sunday
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            // Push Sunday to next Saturday
            calendar.add(Calendar.DAY_OF_YEAR, 6);
        } else {
            // Push weekdays (Monday to Thursday) to the next day
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateFormat.format(calendar.getTime());
    }

    /*
     * Method to push meetings to the other day
     */
    private void pushMeetingsToNextDay() {
        if (selectedDate == null) {
            Toast.makeText(this, "No date selected to push meetings.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Meeting> todayMeetings = database.getMeetingsByDate(selectedDate);

        if (todayMeetings.isEmpty()) {
            Toast.makeText(this, "No meetings to push for the selected date.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this) // Asks for final confirmation if meeting has to be pushed or no
                .setTitle("Push Meetings")
                .setMessage("Do you want to Push All Meetings for today?")
                .setPositiveButton("Yes", (dialog, id) -> {

                    String newDate = getNextTargetDate(selectedDate);
                    boolean allPushed = true;

                    for (Meeting meeting : todayMeetings) {
                        meeting.setDate(newDate);
                        boolean isUpdated = database.updateMeeting(meeting);
                        if (!isUpdated) allPushed = false;
                    }
                    if (allPushed) {
                        Toast.makeText(this, "All meetings pushed to " + newDate, Toast.LENGTH_SHORT).show();
                        showMeetingsForDate(selectedDate);
                    } else { Toast.makeText(this, "Failed to push some meetings.", Toast.LENGTH_SHORT).show(); }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

}
