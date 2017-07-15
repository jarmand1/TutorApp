package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * Created by josianearmand on 3/24/17.
 */

public class TutorProfile extends Activity {

    private TextView txtName, txtCourse, txtTimeAvailable, txtRate, txtAdditional;
    private ImageView imgImageView;
    private Button btnContact, btnSetAppointment, btnMaps, btnReviews;
    private ProgressBar prgLoadingBar;
    private String isTutor, firstName, lastName, appDate, appBegin, appEnd, courseId, courseDesc, thirtyMinute, sixtyMinute,
            stuAppId, tutorAppId, appId, user_email, user_password, email, building, base64Str;
    private String[] timesArray, formattedTimesArray;
    private static final String URL_CREATE_APPOINTMENT = "http://tutorapplication.a2hosted.com/tutorapplication/create_appointment.php";
    private static final String URL_DELETE_APPOINTMENT = "http://tutorapplication.a2hosted.com/tutorapplication/remove_appointment.php";
    private static final String URL_CHECK_REVIEW = "http://tutorapplication.a2hosted.com/tutorapplication/check_review.php";
    private static final String URL_GET_REVIEWS = "http://tutorapplication.a2hosted.com/tutorapplication/get_user_reviews.php";
    private static final String URL_GET_IMAGE = "http://tutorapplication.a2hosted.com/tutorapplication/get_pictures.php";
    private ArrayList<String> reviews, ratings; //parallel arrays cuz lazy
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_profile);

        formattedTimesArray = getResources().getStringArray(R.array.times_array_visual);
        timesArray = getResources().getStringArray(R.array.times_array);

        final Bundle extras = getIntent().getExtras();
        if (null != extras) {
            appId = extras.getString("app_id");
            isTutor = extras.getString("is_tutor");
            firstName = extras.getString("first_name");
            lastName = extras.getString("last_name");
            appDate = extras.getString("app_date");
            appBegin = extras.getString("app_begin");
            appEnd = extras.getString("app_end");
            courseId = extras.getString("course_id");
            courseDesc = extras.getString("course_desc");
            thirtyMinute = extras.getString("thirty_minute");
            sixtyMinute = extras.getString("sixty_minute");
            stuAppId = extras.getString("stu_app_id");
            tutorAppId = extras.getString("tutor_app_id");
            user_email = extras.getString("user_email");
            user_password = extras.getString("user_password");
            email = extras.getString("email");
            building = extras.getString("building");
            getProfilePicture(email);
           // base64Str = extras.getString("base64");
           // Log.i("BASE64MATCHES", base64Str);

            /* if(isTutor.equals("Y")) {

                thirtyMinute = extras.getString("thirty_minute");
                sixtyMinute = extras.getString("sixty_minute");
            }*/
        }else{
            Log.e("TutorProfile", "extras are null");
        }




        txtName = (TextView) findViewById(R.id.txtName_TutorProfile);
        txtCourse = (TextView) findViewById(R.id.txtCourse_TutorProfile);
        txtTimeAvailable = (TextView) findViewById(R.id.txtTimeAvailable_TutorProfile);
        txtRate = (TextView) findViewById(R.id.txtRate_TutorProfile);
        txtAdditional = (TextView) findViewById(R.id.txtAdditionalInfo_TutorProfile);
        btnSetAppointment = (Button) findViewById(R.id.btnSetAppointment_TutorProfile);
        btnContact = (Button) findViewById(R.id.btnContact_TutorProfile);
        btnMaps = (Button) findViewById(R.id.btnMaps_TutorProfile);
        btnReviews = (Button) findViewById(R.id.btnReviews_TutorProfile);
        imgImageView = (ImageView) findViewById(R.id.imgImage_TutorProfile);
        prgLoadingBar = (ProgressBar) findViewById(R.id.prgLoadingBar_TutorProfile);

        txtName.setText(firstName + " " + lastName);
        if(isTutor.equals("Y")) {
            txtRate.setVisibility(View.INVISIBLE);
            btnReviews.setVisibility(View.INVISIBLE);
            btnContact.setText("CONTACT STUDENT");
            txtAdditional.setText("Study Location: " + building);
        }else if (isTutor.equals("N")){
            txtRate.setText("Thirty Minute Rate: $" + thirtyMinute + "\nSixty Minute Rate: $" + sixtyMinute);
            btnContact.setText("CONTACT TUTOR");
            if (extras.get("app_profile").equals("true")){
                txtRate.setText("Total cost: $" + extras.get("total"));
                btnMaps.setVisibility(View.VISIBLE);
                txtAdditional.setText("Study Location: " + building);
            }else if(extras.get("tutor_profile").equals("true")){
                btnMaps.setVisibility(View.INVISIBLE);
            }
        }

        txtTimeAvailable.setText("Appointment begins: " + formatTime(appBegin) + "\nAppointment ends: " + formatTime(appEnd) + "\nAppointment date: " + formatDate(appDate));
        txtCourse.setText("Course code: " + courseId + "\nCourse description: " + courseDesc);

        reviews = new ArrayList<>();
        ratings = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());


        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Email Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "My email content");
                startActivity(Intent.createChooser(emailIntent, "Send mail using..."));

            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Maps_Calender.class);
                i.putExtra("building", building);
                startActivity(i);
            }
        });

        btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getReviews();
            }
        });


        if(extras.get("tutor_profile").equals("true") && extras.get("app_profile").equals("false")) {

            btnSetAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Log.i("AAAAA", stuAppId);

                    AlertDialog.Builder builder = new AlertDialog.Builder(TutorProfile.this);
                    builder.setTitle("Create appointment?");
                    builder.setMessage("Are you sure you would like to create the appointment?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    try {
                                        final URL url = new URL(URL_CREATE_APPOINTMENT);
                                        OkHttpClient client = new OkHttpClient();

                                        RequestBody requestBody = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("stu_app_id", stuAppId)
                                                .addFormDataPart("tutor_app_id", tutorAppId)
                                                .build();

                                        final okhttp3.Request request = new okhttp3.Request.Builder()
                                                .url(url)
                                                .post(requestBody)
                                                .build();

                                        final Gson gson = new Gson();
                                        client.newCall(request).enqueue(new Callback() { //for async
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.e("onClick:TutorProfile", e.getMessage().toString());
                                            }

                                            @Override
                                            public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                                                Log.i("MADE_IT", "");
                                                if (!response.isSuccessful())
                                                    throw new IOException("Unexpected code " + response);
                                                Log.i("RESPONSE", response.body().charStream().toString());
                                                // GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                                GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                                // String s = user[0].Appointment_Begin;
                                                // Log.i("GitUser", s);

                                                //   Log.i("onResponse:Tutor,Stu", user.tutor_info + "," + user.student_info);

                                                if (user.appointment_info != null) { //might need to make this better
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(TutorProfile.this);
                                                            builder1.setTitle("Create calendar notification?");
                                                            builder1.setMessage("Would you like to create a calendar notification");
                                                            builder1.setCancelable(true);

                                                            builder1.setPositiveButton(
                                                                    "Yes",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {

                                                                            Toast.makeText(getApplicationContext(), "Success: appointment created", Toast.LENGTH_SHORT).show();
                                                                            Intent intent2 = new Intent(getApplicationContext(), Main_Menu.class);
                                                                            intent2.putExtra("email", user_email);
                                                                            intent2.putExtra("password", user_password);
                                                                            startActivity(intent2);

                                                                            Calendar beginTime = Calendar.getInstance();
                                                                            Calendar endTime = Calendar.getInstance();

                                                                            //FORMAT to set the beginning of an event
                                                                            //beginTime.set(YYYY, MM, DD, Hour, Mins);
                                                                            String year = ("20" + (appDate.substring(4, 6)));
                                                                            String month = appDate.substring(2, 4);
                                                                            String day = appDate.substring(0, 2);
                                                                            String hour = appBegin.substring(0, 1);
                                                                            String min = appBegin.substring(2, 4);
                                                                            beginTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
                                                                            endTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
                                                                            //format to set the end of an event
                                                                            //endTime.set(YYYY, MM, DD, Hour, Mins);
                                                                            hour = appEnd.substring(0, 1);
                                                                            min = appEnd.substring(2, 4);
                                                                            endTime.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));

                                                                            //TODO: email notifications


                                                                            Intent intent = new Intent(Intent.ACTION_EDIT);
                                                                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime);
                                                                            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
                                                                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "here");
                                                                            intent.setType("vnd.android.cursor.item/event");
                                                                            intent.putExtra("title", "Appointment With Tutor ");
                                                                            intent.putExtra("description", "Some description about the tutor appointment like course or ");
                                                                            startActivity(intent);


                                                                        }
                                                                    });

                                                            builder1.setNegativeButton(
                                                                    "No",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {

                                                                            Toast.makeText(getApplicationContext(), "Success: appointment created", Toast.LENGTH_SHORT).show();
                                                                            dialog.cancel();
                                                                            Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                                            intent.putExtra("email", user_email);
                                                                            intent.putExtra("password", user_password);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                            AlertDialog alert11 = builder1.create();
                                                            alert11.show();
                                                        }
                                                    });

                                                } else if (user.insert_error != null) {
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Failure: appointment already exists", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                            }
                                        });
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            });
        }else if(extras.get("tutor_profile").equals("false") && extras.get("app_profile").equals("true") && extras.get("present_appointments").equals("true")){

            btnSetAppointment.setText("CANCEL APPOINTMENT");
            btnSetAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final URL url = new URL(URL_DELETE_APPOINTMENT);
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("app_id", appId)
                                .build();

                        final okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();

                        final Gson gson = new Gson();
                        client.newCall(request).enqueue(new Callback() { //for async
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("onClick:TutorProfile", e.getMessage().toString());
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);

                                if (user.delete_appointment_success != null) { //might need to make this better
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {


                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(TutorProfile.this);
                                            builder1.setTitle("Cancel appointment?");
                                            builder1.setMessage("Are you sure you would like to cancel the appointment?");
                                            builder1.setCancelable(true);

                                            builder1.setPositiveButton(
                                                    "Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Toast.makeText(getApplicationContext(), "Success: appointment deleted", Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                            intent.putExtra("email", user_email);
                                                            intent.putExtra("password", user_password);
                                                            startActivity(intent);
                                                        }
                                                    });

                                            builder1.setNegativeButton(
                                                    "No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();

                                        }
                                    });
                                } else if (user.delete_appointment_error != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Fatal Error: doesn't exist", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else if(user.error_delete_app != null){
                                    //fatal error
                                }
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(extras.get("tutor_profile").equals("false") && extras.get("app_profile").equals("true") && extras.get("present_appointments").equals("false")){
            btnMaps.setVisibility(View.INVISIBLE);
            if(isTutor.equals("Y")){
                btnSetAppointment.setText("SEND PAYMENT"); //tutors cant leave reviews

                btnSetAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //TODO: PayPal API
                        Paypal beginPayment = new Paypal();
                        Intent paypalIntent = new Intent(getApplicationContext(), Paypal.class);
                        //insert payment amount here convert it to string...random amount is hard coded in for now
                        String paymentAmount = "40";
                        paypalIntent.putExtra("Payment Amount", paymentAmount);
                        startActivity(paypalIntent);
                       // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/us/home"));
                        //startActivity(browserIntent);

                    }
                });
            }else {
                btnSetAppointment.setText("LEAVE REVIEW");
                btnSetAppointment.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        try {
                            final URL url = new URL(URL_CHECK_REVIEW);
                            OkHttpClient client = new OkHttpClient();

                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("app_id", appId)
                                    .build();

                            final okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();

                            final Gson gson = new Gson();
                            client.newCall(request).enqueue(new Callback() { //for async
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("onClick:TutorProfile", e.getMessage().toString());
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);

                                    GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);

                                    if (user.success_review_check != null) { //might need to make this better
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(getApplicationContext(), Add_Review.class);
                                                i.putExtra("first_name", firstName);
                                                i.putExtra("last_name", lastName);
                                                i.putExtra("app_id", appId);
                                                i.putExtra("user_email", user_email);
                                                i.putExtra("user_password", user_password);
                                                i.putExtra("availability", "false");
                                                i.putExtra("possible_appointments", "false");
                                                i.putExtra("appointments", "false");
                                                i.putExtra("reviews", "true");
                                                startActivity(i);

                                            }
                                        });
                                    } else if (user.error_review_check != null) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "You already left a review for this tutor", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else if(user.error_review != null){
                                        //fatal error
                                        Toast.makeText(getApplicationContext(), "Fatal Error: appointment doesn't exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    private void getReviews(){
        Log.i("BLEEEH", tutorAppId);
        try {
            final URL url = new URL(URL_GET_REVIEWS);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tutor_app_id", tutorAppId)
                    .build();

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            final Gson gson = new Gson();
            client.newCall(request).enqueue(new Callback() { //for async
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("onClick:TutorProfile", e.getMessage().toString());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    GitUser user[] = gson.fromJson(response.body().charStream(), GitUser[].class);

                    if (user[0].Review != null) { //might need to make this better
                        for (int i = 0; i < user.length; i++) {
                            reviews.add(user[i].Review);
                            ratings.add(user[i].Rating);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent i = new Intent(getApplicationContext(), TutorMatches.class);
                                i.putExtra("user_email", user_email);
                                i.putExtra("user_password", user_password);
                                i.putExtra("first_name", firstName);
                                i.putExtra("last_name", lastName);
                                i.putExtra("reviews_arr", reviews);
                                i.putExtra("ratings_arr", ratings);
                                i.putExtra("reviews", "true");
                                i.putExtra("availability", "false");
                                i.putExtra("possible_appointments", "false");
                                i.putExtra("appointments", "false");
                                startActivity(i);
                            }
                        });
                    } else if (user[0].error_get_reviews != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No reviews found. ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Overrides the device's back key to end the activity
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume(){
        super.onResume();
        reviews.clear();
        ratings.clear();
       // reviews = new ArrayList<>();
       // reviews.clear();
       //imgImageView.setImageDrawable(null);
        //Log.i("resumed", "resu,ed");
    }

    @Override
    protected void onPause(){
        super.onPause();
        //imgImageView.setImageDrawable(null);
        //Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
       // Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_LONG).show();
    }

    /**
     *This method should be called during an onClick on the listview.  Based on the email, it retrieves the base64 string of the users profile picture
     * @param getEmail: email address of the user we want to load the image for
     */
    private void getProfilePicture(final String getEmail){
        try {
            final URL url = new URL(URL_GET_IMAGE);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", getEmail)
                    .build();

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            final Gson gson = new Gson();
            client.newCall(request).enqueue(new Callback() { //for async
                @Override
                public void onFailure(Call call, IOException e) {
                    //TODO: something
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);

                    if (user.success_get_image != null) {
                        base64Str = user.success_get_image;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Log.i("BASE64SUCCEESS", test+ " " + base64Str);
                                //base64Arr.add(base64Str);
                                byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgImageView.setImageBitmap(decodedByte);
                                prgLoadingBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else if (user.error_get_image != null) {
                        base64Str = "empty";
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // base64Arr.add(base64Str);
                                //Log.i("BASE64FAIL", test+ " " + base64Str);
                                prgLoadingBar.setVisibility(View.INVISIBLE);

                            }
                        }); //end runnable
                    }

                }
            }); //end callback

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(String time){
        for(int i = 0; i < timesArray.length; i++){
            if(time.equals(timesArray[i])){
                return formattedTimesArray[i];
            }
        }
        return null;
    }

    private String formatDate(String date){
        StringBuilder sb = new StringBuilder(date);
        // dd-mm-yy
        sb.insert(2, "-");
        sb.insert(5, "-");
        if(sb.charAt(0) == '0' && sb.charAt(3) == '0'){
            sb.delete(3, 4);
            sb.delete(0, 1);
        }else if (sb.charAt(0) == '0'){
            sb.delete(0, 1);
        }
        return sb.toString();
    }




}

