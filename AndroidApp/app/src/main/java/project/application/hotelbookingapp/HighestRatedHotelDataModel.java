package project.application.hotelbookingapp;

import org.json.JSONArray;

public class HighestRatedHotelDataModel {

    private String hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage;
    private int hotel_id;
    JSONArray hotl_galleyimages;

    public String getHotel_name() {
        return hotel_name;
    }

    public String getHotel_address() {
        return hotel_address;
    }

    public String getHotel_city() {
        return hotel_city;
    }

    public String getHotel_description() {
        return hotel_description;
    }

    public String getHotel_coverimage() {
        return hotel_coverimage;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public JSONArray getHotl_galleyimages() {
        return hotl_galleyimages;
    }

    public HighestRatedHotelDataModel(String hotel_name, String hotel_address, String hotel_city, String hotel_description, String hotel_coverimage, int hotel_id, JSONArray hotl_galleyimages) {
        this.hotel_name = hotel_name;
        this.hotel_address = hotel_address;
        this.hotel_city = hotel_city;
        this.hotel_description = hotel_description;
        this.hotel_coverimage = hotel_coverimage;
        this.hotel_id = hotel_id;
        this.hotl_galleyimages = hotl_galleyimages;
    }
}
