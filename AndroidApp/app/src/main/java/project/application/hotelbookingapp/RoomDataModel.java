package project.application.hotelbookingapp;

import org.json.JSONArray;

public class RoomDataModel {
    private String room_name,room_description,room_coverimage;
    private int hotel_id;
    private int room_id;
    private int room_pric;

    public String getRoom_name() {
        return room_name;
    }

    public String getRoom_description() {
        return room_description;
    }

    public String getRoom_coverimage() {
        return room_coverimage;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getRoom_pric() {
        return room_pric;
    }

    public int getRoom_discount() {
        return room_discount;
    }

    public JSONArray getRoom_galleyimages() {
        return room_galleyimages;
    }

    public RoomDataModel(String room_name, String room_description, String room_coverimage, int hotel_id, int room_id, int room_pric, int room_discount, JSONArray room_galleyimages) {
        this.room_name = room_name;
        this.room_description = room_description;
        this.room_coverimage = room_coverimage;
        this.hotel_id = hotel_id;
        this.room_id = room_id;
        this.room_pric = room_pric;
        this.room_discount = room_discount;
        this.room_galleyimages = room_galleyimages;
    }

    private int room_discount;
    JSONArray room_galleyimages;


}
