package project.application.hotelbookingapp;

public class UserInfoModel {
    static String firstname;
    static String lastname;
    static String username;
    static String email;

    public static String getProfilepic() {
        return profilepic;
    }

    static String profilepic;

    public static String getFirstname() {
        return firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static String getUsername() {
        return username;
    }


    public static String getEmail() {
        return email;
    }

    public UserInfoModel(String firstname, String lastname, String username, String email,String profilepic) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.profilepic = profilepic;
    }
}
