package project.application.hotelbookingapp;

public class Urls {
    public static String IP = "192.168.100.28";
    public static String PORT = "3000";
    public static String DOMAIN1 = IP.trim() + ":" + PORT.trim();
    public static String DOMAIN = "http://"+IP.trim() + ":" + PORT.trim();

    public static String LOGIN_ACCOUNT = "http://" + DOMAIN1 + "/authentication/userlogin";
    public static String SIGNUP_ACCOUNT = "http://" + DOMAIN1 + "/authentication/registeruser";
    public static String FORGOT_PASSWORD = "http://" + DOMAIN1 + "/authentication/userforgotpassword";
    public static String SEARCH_USERS = "http://" + DOMAIN1 + "/users/searchuser/";
    //Update User Detail
    public static String UPDATE_PROFILE_PIC = "http://" + DOMAIN1 + "/users/updateprofilepic/";
    public static String UPDATE_FIRSTNAME = "http://" + DOMAIN1 + "/users/updatefirstname/";
    public static String UPDATE_LASTNAME = "http://" + DOMAIN1 + "/users/updatelastname/";
    public static String UPDATE_USERNAME = "http://" + DOMAIN1 + "/users/updateusername/";
    public static String UPDATE_PASSWORD = "http://" + DOMAIN1 + "/users/updatepassword/";
    public static String UPDATE_EMAIL = "http://" + DOMAIN1 + "/users/updateemail/";

    //HOTELS
    public static String GET_HOTEL = "http://" + DOMAIN1 + "/api/gethotels/";
    public static String GET_HIGHESTRATEDHOTEL = "http://" + DOMAIN1 + "/api/gethighestratedhotels/";

    //ROOMS
    public static String GET_ROOM = "http://" + DOMAIN1 + "/api/getrooms/";

    //Payments
    public static String PAYEMNT = "http://" + DOMAIN1 + "/payment/onlinepayment";

    //Check dates
    public static String CHECK_STARTDATE = "http://" + DOMAIN1 + "/checkdate/startdate";
    public static String CHECK_ENDDATE = "http://" + DOMAIN1 + "/checkdate/enddate";

    //Insert Bookings
    public static String INSERT_BOOKINGS = "http://" + DOMAIN1 + "/bookings/insertbooking";
    //Get Booking Details
    public static String GET_BOOKINGLIST = "http://" + DOMAIN1 + "/bookings/getbookingdetails";

    //Check Booking
    public static String CHECK_BOOKING = "http://" + DOMAIN1 + "/bookings/checkbookingdetails";
    public static String CHECK_ROOMBOOKING = "http://" + DOMAIN1 + "/bookings/checkroombookingdetails";

    //Insert Reviews
    public static String INSERT_REVIEWS = "http://" + DOMAIN1 + "/reviews/inserthotelreviews";
    public static String INSERT_RoomREVIEWS = "http://" + DOMAIN1 + "/reviews/insertroomreviews";

    //Get Reviews
    public static String GET_REVIEWS = "http://" + DOMAIN1 + "/api/gethotelreviews";
    public static String GET_ROOMREVIEWS = "http://" + DOMAIN1 + "/api/getroomreviews";

    //getRating
    public static String GET_HOTELRATING = "http://" + DOMAIN1 + "/api/gethotelrating";
    public static String GET_ROOMRATING = "http://" + DOMAIN1 + "/api/getroomrating";

    //getpopularhotels
    public static String GET_POPULARHOTEL = "http://" + DOMAIN1 + "/api/getpopularhotels";

    //Add To Favourites
    public static String ADDTOFAVOURITES = "http://" + DOMAIN1 + "/api/insertfavourite";
    //Get Favourties
    public static String GETFAVOURITES = "http://" + DOMAIN1 + "/api/getfavourites";
    //Get Recommendations
    public static String GETRECOMMENDATIONS = "http://" + DOMAIN1 + "/api/gethotelrecommendations";

}
