package com.example.psmeetease;

/*
 * Name:        Parampal Singh
 * Student no.: 7003114
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {
    private List<Meeting> meetings; // List to store meeting data

    // Constructor
    public MeetingAdapter(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    /*
     * This method is called when a new view holder is created. It sets the layout for each item in the RecyclerView.
     */
    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(view);
    }

    /*
     * This method is called to bind data to the view holder.
     */
    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        Meeting meeting = meetings.get(position);
        holder.title.setText(meeting.getTitle());
        holder.date.setText(meeting.getDate());
        holder.time.setText(meeting.getTime());
        holder.phone.setText(meeting.getPhone());
    }

    /*
     * This method returns the total number of meetings in the list
     */
    @Override
    public int getItemCount() { return meetings.size(); }

    /*
     * ViewHolder class that holds references to the views for each meeting item
     */
    public static class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, phone;

        // Constructor
        public MeetingViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.meetingTitle);
            date = itemView.findViewById(R.id.meetingDate);
            time = itemView.findViewById(R.id.meetingTime);
            phone = itemView.findViewById(R.id.meetingPhone);
        }
    }
}
