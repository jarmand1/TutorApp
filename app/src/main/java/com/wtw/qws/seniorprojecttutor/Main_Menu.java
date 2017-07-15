package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class Main_Menu extends Activity {

    private String email, password, firstName, lastName, phoneNumber, isTutor, ramId, sixtyMinuteRate, thirtyMinuteRate;
    private static final String TAG = "Main_Menu";
    private Button btnFindAppointments, btnViewAppointments, btnViewPossibleAppointments, btnViewAvailability, btnSettings, btnViewPastAppointments;
    private TextView txtWelcome;
    private static final String URL = "http://tutorapplication.a2hosted.com/tutorapplication/get_user_information.php";
    private static final String URL_MATCH = "http://tutorapplication.a2hosted.com/tutorapplication/match_appointment.php";
    private static final String URL_AVAILABILITY = "http://tutorapplication.a2hosted.com/tutorapplication/get_user_availability.php";
    private static final String URL_APPOINTMENTS = "http://tutorapplication.a2hosted.com/tutorapplication/get_user_appointments.php";
    private Handler mHandler;
    private ArrayList<HashMap<String, String>> results;
    private ArrayList<String> settingsDateArray;
    private Date myDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            email = extras.getString("email");
            password = extras.getString("password");
        }

        results = new ArrayList<>();
        settingsDateArray = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());
        myDate = new Date();
        //Log.i("EMAIL", email);
        getUserInformation();

        txtWelcome = (TextView) findViewById(R.id.txtWelcome_MainMenu);
        btnFindAppointments = (Button) findViewById(R.id.btnFindAppointments_MainMenu);
        btnViewAppointments = (Button) findViewById(R.id.btnViewAppointments_MainMenu);
        btnViewPossibleAppointments = (Button) findViewById(R.id.btnPossibleAppointments_MainMenu);
        btnViewPastAppointments = (Button) findViewById(R.id.btnViewPastAppointments_MainMenu);
        btnViewAvailability = (Button) findViewById(R.id.btnViewAvailability_MainMenu);
        btnSettings = (Button) findViewById(R.id.btnSettings_MainMenu);


        btnViewAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAvailability();
            }
       });

        btnViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppointments(true);
            }
        });

        btnViewPastAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAppointments(false);
            }
        });

        btnViewPossibleAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                match();

            }
        });

        btnFindAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add better validation
                if (isTutor.equals("Y") || isTutor.equals("N")) {
                    if ((thirtyMinuteRate.equals("") && sixtyMinuteRate.equals(""))) {
                       // Toast.makeText(getApplicationContext(), "Please head to settings and add your rates", Toast.LENGTH_SHORT).show();
                    } else if (ramId.equals("")) {
                       // Toast.makeText(getApplicationContext(), "Please head to settings and add your RAM ID", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(getApplicationContext(), SearchPage.class);
                    i.putExtra("email", email);
                    i.putExtra("password", password);
                    i.putExtra("ram_id", ramId);
                    i.putExtra("is_tutor", isTutor);
                    i.putExtra("thirty_minute", thirtyMinuteRate);
                    i.putExtra("sixty_minute", sixtyMinuteRate);
                    startActivity(i);
                } else {
                    //should never get here
                    Toast.makeText(getApplicationContext(), "You are not registered as a student or tutor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getAppointmentsSettings();
                Intent i = new Intent(getApplicationContext(), Sign_Up.class);
                i.putExtra("email", email);
                i.putExtra("password", password);
                i.putExtra("first_name", firstName);
                i.putExtra("last_name", lastName);
                i.putExtra("is_tutor", isTutor);
                i.putExtra("phone_number", phoneNumber);
                i.putExtra("ram_id", ramId);
                i.putExtra("thirty_minute", thirtyMinuteRate);
                i.putExtra("sixty_minute", sixtyMinuteRate);
                startActivity(i);
            }
        });



    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        results = new ArrayList<>();
        getUserInformation();
    }

    /**
     * This method is responsible for obtaining the list of appointments for the user.  Linked to the btnPossibleAppointments button
     * @param: if true: get future appointments, if false: get past appointments
     */
    private void getAppointments(final boolean isPresentAppointments) {
        try {
            final URL url = new URL(URL_APPOINTMENTS);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
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

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call call, final okhttp3.Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    GitUser[] user = gson.fromJson(response.body().charStream(), GitUser[].class);

                    if (user[0].Tutor_ID != null && user[0].Ram_ID != null) {
                        for (int i = 0; i < user.length; i++) {
                            HashMap<String, String> temp = new HashMap<>();
                            if(isTutor.equals("Y")) {
                                temp.put("email", user[i].Student_Email);
                            }else if (isTutor.equals("N")){
                                temp.put("email", user[i].Tutor_Email);
                                temp.put("total", user[i].Total);
                            }
                            temp.put("app_id", user[i].Appointment_ID);
                            temp.put("ram_id", user[i].Ram_ID);
                            temp.put("tutor_id", user[i].Tutor_ID);
                            temp.put("course_id", user[i].Course_ID);
                            temp.put("course_desc", user[i].Course_Description);
                            temp.put("app_date", user[i].Appointment_Date);
                            temp.put("app_begin", user[i].Appointment_Begin);
                            temp.put("app_end", user[i].Appointment_End);
                            temp.put("tutor_app_id", user[i].Tutor_Appointment_ID);
                            temp.put("stu_app_id", user[i].Student_Appointment_ID);
                            temp.put("first_name", user[i].First_Name);
                            temp.put("last_name", user[i].Last_Name);
                            temp.put("building", user[i].Building);
                            if(isPresentAppointments){
                                if(myDate.compareDates(user[i].Appointment_Date)){
                                    results.add(temp);
                                }
                            }else{
                                if(!myDate.compareDates(user[i].Appointment_Date)){
                                    results.add(temp);
                                }
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                if(results.size()>0) {
                                    Intent i = new Intent(getApplicationContext(), TutorMatches.class);
                                    Log.i(TAG, results.toString());
                                    i.putExtra("user_email", email); //users email
                                    i.putExtra("user_password", password);
                                    i.putExtra("app_array", results);
                                    i.putExtra("is_tutor", isTutor);
                                    i.putExtra("availability", "false");
                                    i.putExtra("possible_appointments", "false");
                                    i.putExtra("appointments", "true");
                                    i.putExtra("reviews", "false");
                                    if (isPresentAppointments) {
                                        i.putExtra("present_appointments", "true");
                                    } else {
                                        i.putExtra("present_appointments", "false");
                                    }
                                    startActivity(i);
                                }else if(results.size()==0 && isPresentAppointments){
                                    Toast.makeText(getApplicationContext(), "No appointments exist.", Toast.LENGTH_SHORT).show();
                                }else if(results.size()==0 && !isPresentAppointments){
                                    Toast.makeText(getApplicationContext(), "No past appointments exist.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if (user[0].error_get_appointments != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No appointments exist.", Toast.LENGTH_SHORT).show();
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




    /**
     * Responsible for retrieving relevant user information from the database.
     */
    private void getUserInformation() {
        try {
            final URL url = new URL(URL);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
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
                    //Log.i("GitUser", user.success_get_info);
                    Log.i("onResponse:Tutor,Stu", user.tutor_info + "," + user.student_info);
                    if (user.success_get_info != null) { //might need to make this better
                        firstName = user.first_name;
                        lastName = user.last_name;
                        isTutor = user.is_tutor;
                        phoneNumber = user.phone_number;
                        ramId = user.ram_id;
                        thirtyMinuteRate = user.thirty_minute;
                        sixtyMinuteRate = user.sixty_minute;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                txtWelcome.setText("Hello " + firstName + "!");
                                /*if (isTutor.equals("Y")) {
                                    btnFindAppointments.setText(R.string.appointmenttutor);
                                } else if (isTutor.equals("N")) {
                                    btnFindAppointments.setText(R.string.appointmentstudent);
                                }*/

                            }
                        });

                    } else if (user.error_get_info != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "getUserInformation: Something went wrong");
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

    private void match() {
        Log.i(TAG, "in match()");
        try {
            final URL url = new URL(URL_MATCH);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
                    .addFormDataPart("is_tutor", isTutor)
                    .build();

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            final Gson gson = new Gson();
            client.newCall(request).enqueue(new Callback() { //for async
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.getMessage().toString());
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    // GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                    GitUser[] user = gson.fromJson(response.body().charStream(), GitUser[].class);
                    // String s = user[0].Appointment_Begin;
                    // Log.i("GitUser", s);

                    //   Log.i("onResponse:Tutor,Stu", user.tutor_info + "," + user.student_info);
                    if (user[0].Tutor_ID != null || user[0].Ram_ID != null) { //might need to make this better
                        for (int i = 0; i < user.length; i++) {
                            HashMap<String, String> temp = new HashMap<>();
                            if (isTutor.equals("Y")) {
                                temp.put("ram_id", user[i].Ram_ID);
                                temp.put("building", user[i].Building);
                            } else if (isTutor.equals("N")) {
                                temp.put("tutor_id", user[i].Tutor_ID);
                                temp.put("thirty_minute",user[i].Thirty_Minute_Rate);
                                temp.put("sixty_minute",user[i].Sixty_Minute_Rate);
                            }
                            temp.put("user_email", email); //this is the users email
                            temp.put("user_password", password);
                            temp.put("email", user[i].Student_Email); //this is the student or tutor email: poorly named
                            temp.put("course_id", user[i].Course_ID);
                            temp.put("app_date", user[i].Appointment_Date);
                            temp.put("app_begin", user[i].Appointment_Begin);
                            temp.put("app_end", user[i].Appointment_End);
                            temp.put("first_name", user[i].First_Name);
                            temp.put("last_name", user[i].Last_Name);
                            temp.put("stu_app_id", user[i].Student_Appointment_ID);
                            temp.put("tutor_app_id", user[i].Tutor_Appointment_ID);
                            temp.put("course_desc", user[i].Course_Description);
                            if(myDate.compareDates(user[i].Appointment_Date)) { //Should prevent old availability from appearing
                                results.add(temp);
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, results.toString());
                                //TODO: populate tutorMatches page
                               if(results.size() >0) {
                                   Toast.makeText(getApplicationContext(), "Success: match(es) found.", Toast.LENGTH_SHORT).show();
                                   Intent i = new Intent(getApplicationContext(), TutorMatches.class);
                                   i.putExtra("user_email", email);
                                   i.putExtra("user_password", password);
                                   i.putExtra("app_array", results);
                                   i.putExtra("is_tutor", isTutor);
                                   i.putExtra("availability", "false");
                                   i.putExtra("possible_appointments", "true");
                                   i.putExtra("appointments", "false");
                                   i.putExtra("reviews", "false");
                                   startActivity(i);
                               }else{
                                   Toast.makeText(getApplicationContext(), "No matches found, try adding more availability.", Toast.LENGTH_SHORT).show();
                               }
                            }
                        });

                    } else if (user[0].failure_match_not_found != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO: Probably handle this on the appointment view screen.
                                Log.i(TAG, "Match not found");
                                Toast.makeText(getApplicationContext(), "No matches found, try adding more availability.", Toast.LENGTH_SHORT).show();
                            }
                        }); //end runnable
                    } else if (user[0].error_match != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No matches found, try adding more availability.", Toast.LENGTH_SHORT).show();
                               // Toast.makeText(getApplicationContext(), "Error: Missing data", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Missing data");
                            }
                        }); //end runnable
                    } else if (user[0].failure_match != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Fatal Error: User doesn't exist", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "User doesn't exist");
                            }
                        }); //end runnable
                    } else if (user[0].error != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Fatal Error: check log", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "match(): Query failed");
                            }
                        }); //end runnable
                    }
                }//END RESPONSE
            }); //end callback

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAvailability() {
        Log.i(TAG, "in getAvailability()");
        try {
            final URL url = new URL(URL_AVAILABILITY);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
                    .addFormDataPart("is_tutor", isTutor)
                    .build();

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            final Gson gson = new Gson();
            client.newCall(request).enqueue(new Callback() { //for async
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.getMessage().toString());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());

                    GitUser[] user = gson.fromJson(response.body().charStream(), GitUser[].class);

                        if (user[0].availability.equals("not_null")) {
                            for (int i = 1; i < user.length; i++) {
                                HashMap<String, String> temp = new HashMap<>();
                                if (isTutor.equals("Y")) {
                                    temp.put("tutor_id", user[i].Tutor_ID);
                                    temp.put("tutor_app_id", user[i].Tutor_Appointment_ID);
                                } else if (isTutor.equals("N")) {
                                    temp.put("ram_id", user[i].Ram_ID);
                                    temp.put("stu_app_id", user[i].Student_Appointment_ID);
                                }
                                temp.put("user_email", email);  //users email
                                temp.put("user_password", password);
                                temp.put("email", user[i].Student_Email);   //student/ tutor email
                                temp.put("course_id", user[i].Course_ID);
                                temp.put("app_date", user[i].Appointment_Date);
                                temp.put("app_begin", user[i].Appointment_Begin);
                                temp.put("app_end", user[i].Appointment_End);
                                results.add(temp);
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, results.toString());
                                    //TODO: populate tutorMatches page
                                    Intent i = new Intent(getApplicationContext(), TutorMatches.class);
                                    i.putExtra("app_array", results);
                                    i.putExtra("is_tutor", isTutor);
                                    i.putExtra("availability", "true");
                                    i.putExtra("possible_appointments", "false");
                                    i.putExtra("appointments", "false");
                                    i.putExtra("reviews", "false");
                                    startActivity(i);
                                }
                            });

                        }else if (user[0].availability.equals("null")){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No availability found, try adding some", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Main_Menu.this);
            builder1.setTitle("Log out?");
            builder1.setMessage("Are you sure you would like to log out?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
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

        return super.onKeyDown(keyCode, event);
    }



}