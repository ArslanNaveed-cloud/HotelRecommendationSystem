package project.application.hotelbookingapp;

public class ReviewsDataModel {
    int myhotelid;
    String myexperience;
    double myrating ;
    String mynames;

    public int getMyhotelid() {
        return myhotelid;
    }

    public String getMyexperience() {
        return myexperience;
    }

    public double getMyrating() {
        return myrating;
    }

    public String getMynames() {
        return mynames;
    }

    public ReviewsDataModel(int myhotelid, String myexperience, double myrating, String mynames) {
        this.myhotelid = myhotelid;
        this.myexperience = myexperience;
        this.myrating = myrating;
        this.mynames = mynames;
    }
}
