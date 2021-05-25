package project.application.hotelbookingapp;

public class HotelRatingDataModel {
    int hotel_id;
    float hotel_rating;

    public int getHotel_id() {
        return hotel_id;
    }

    public float getHotel_rating() {
        return hotel_rating;
    }

    public HotelRatingDataModel(int hotel_id, float hotel_rating) {
        this.hotel_id = hotel_id;
        this.hotel_rating = hotel_rating;
    }
}
