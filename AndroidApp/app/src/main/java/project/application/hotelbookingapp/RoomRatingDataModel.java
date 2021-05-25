package project.application.hotelbookingapp;

public class RoomRatingDataModel {
    int room_id;
    float room_rating;

    public int getRoom_id() {
        return room_id;
    }

    public float getRoom_rating() {
        return room_rating;
    }

    public RoomRatingDataModel(int room_id, float room_rating) {
        this.room_id = room_id;
        this.room_rating = room_rating;
    }
}
