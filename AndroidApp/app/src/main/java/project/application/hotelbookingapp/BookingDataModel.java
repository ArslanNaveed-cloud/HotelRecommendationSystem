package project.application.hotelbookingapp;

public class BookingDataModel {
    private String roomname,hotelname,startdate,enddate,stay,coveriamge;
    private int room_price;

    public String getRoomname() {
        return roomname;
    }

    public String getHotelname() {
        return hotelname;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public String getStay() {
        return stay;
    }

    public String getCoveriamge() {
        return coveriamge;
    }


    public int getRoom_price() {
        return room_price;
    }

    public BookingDataModel(String roomname, String hotelname, String startdate, String enddate, String stay, String coveriamge, int room_price) {
        this.roomname = roomname;
        this.hotelname = hotelname;
        this.startdate = startdate;
        this.enddate = enddate;
        this.stay = stay;
        this.coveriamge = coveriamge;
        this.room_price = room_price;
    }
}
