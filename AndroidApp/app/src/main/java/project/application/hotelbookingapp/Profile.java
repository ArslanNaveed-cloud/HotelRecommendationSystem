package project.application.hotelbookingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {
    private String username;
    private ProgressDialog dialog;
    private LinearLayout mylayout1;
    JSONObject jsonObject;
    ImageView useriamge,changeprofilepic;
    TextView uname,fname,lname,useremail,pass;
     String profilepic,password;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    int size = 0;
    private String filePath;
    private LinearLayout user_name,firstname,lastname,email,password1;
    Intent intent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        useriamge = view.findViewById(R.id.userimage);
        changeprofilepic  = view.findViewById(R.id.editprofilepic);
        uname = view.findViewById(R.id.uname);
        fname = view.findViewById(R.id.fname);
        lname = view.findViewById(R.id.lname);
        useremail = view.findViewById(R.id.uemail);
        pass = view.findViewById(R.id.pass);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        user_name = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password1 = view.findViewById(R.id.password);



        firstname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),ChangeFirstName.class);
                startActivity(intent);
            }
        });
        lastname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),ChangeLastName.class);
                startActivity(intent);
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),ChangeUsername.class);
                startActivity(intent);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),ChangeEmail.class);
                startActivity(intent);
            }
        });
        password1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),ChangePassword.class);
                startActivity(intent);
            }
        });

        changeprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermisiion();
            }
        });

        fname.setText(UserInfoModel.getFirstname());
        lname.setText(UserInfoModel.getLastname());
        uname.setText(UserInfoModel.getUsername());
        useremail.setText(UserInfoModel.getEmail());
        profilepic = UserInfoModel.getProfilepic();
        pass.setText("**************");
        Glide.with(getActivity()).load(Urls.DOMAIN+"/assets/profilepictures/"+profilepic).
                circleCrop().
                into(useriamge);

        return view;
    }
    public void checkpermisiion(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            }
            else{
                UploadPics();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(requestCode == 100 && (grantResults[i] == PackageManager.PERMISSION_GRANTED)){

                UploadPics();
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

                }
            }
        }

    }
    public void UploadPics(){
        Intent intent = new Intent(getActivity(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(false)
                .setShowImages(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .setIgnoreNoMedia(true)
                .build());
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1
                && resultCode == RESULT_OK
                && data != null){
            mediaFiles.clear();
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
            size = mediaFiles.size();
            for(int i = 0;i<mediaFiles.size();i++){
                MediaFile file = mediaFiles.get(i);
                filePath = file.getPath();
                buildDialog3(getActivity()).show();

            }
        }

    }
    private void uploadNow() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading file");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final File file = new File(filePath);
        Ion.with(getActivity())
                .load("POST", Urls.UPDATE_PROFILE_PIC)
                .setLogging("1312wWOD", Log.INFO)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        double progress = (100.0 * uploaded) / total;
                        progressDialog.setMessage("Uploading profile picture.. " + ((int) progress) + " %");
                    }
                })
                .setMultipartFile("file", "application/pdf", file)
                .setMultipartParameter("username",username)

                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        Log.i("312333", result + "  " + e + "");
                        if (result != null) {
                            try {
                                JSONObject mainObject = new JSONObject(result);
                                String status = mainObject.getString("status");
                                if (status.equals("500")) {
                                    buildDialog(getActivity(), "500", "Internal Server Error","500").show();

                                }

                                else if (status.equals("200")) {
                                    buildDialog(getActivity(),"Congratulations","profile pic updated","200").show();
                                    waitforsometime();
                                }
                                else if(status.equals("409")){

                                    buildDialog(getActivity(), "Account not created", "User with same name already exists","409").show();

                                }
                            } catch (Exception ex) {
                                Log.i("312333", ex + "");
                            }
                        } else {
                            buildDialog(getActivity(), "404", "Record Not Saved","404").show();
                        }
                    }
                });

    }

    public AlertDialog.Builder buildDialog(Context c, String header, String message, final String status) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               Intent intent = new Intent(getActivity(),DashBoardActivity.class);
               startActivity(intent);
               getActivity().finish();
            }
        }, 1000);
    }
    public AlertDialog.Builder buildDialog3(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Are you sure.?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadNow();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }

}