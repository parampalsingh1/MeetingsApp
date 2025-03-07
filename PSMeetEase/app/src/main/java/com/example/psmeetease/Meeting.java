package com.example.psmeetease;

public class Meeting {
    private int id;
    private String title;
    private String date;
    private String time;
    private String phone;

    // Constructor
    public Meeting(int id, String title, String date, String time, String phone) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.phone = phone;
    }

    // Getter and setters

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public String getPhone() { return phone; }

}
