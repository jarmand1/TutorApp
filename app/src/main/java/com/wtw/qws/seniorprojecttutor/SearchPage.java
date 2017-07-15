package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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

/**
 * Created by josianearmand on 3/23/17.
 */

public class SearchPage extends Activity {

    private String email, password, ramId, isTutor, endTime, startTime, currentDate, thirtyMinuteRate, sixtyMinuteRate, building;
    private static String formattedDate, rawDate, rawEndTime;
    private String[] coursesCodeArr, timeArray, formattedTimesArray;
    private Spinner spnAvailableFrom, spnCourseSearch, spnBuildingsSearch;
    private int start, end;
    private Button btnSearch, btnPickDate;
    private CheckBox chkThrityMinute, chkSixtyMinute;
    private TextView txtEndTime;
    private static final String URL_REQUEST_APPT = "http://tutorapplication.a2hosted.com/tutorapplication/add_requested_appointment.php";
    private static final String URL_MATCH = "http://tutorapplication.a2hosted.com/tutorapplication/match_appointment.php";
    private static final String TAG = "SEARCH_PAGE";
    private Handler mHandler;
    private ArrayList<HashMap<String, String>> results;
    private Date myDate;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        mHandler = new Handler(Looper.getMainLooper());
        myDate = new Date();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            email = extras.getString("email");
            password = extras.getString("password");
            ramId = extras.getString("ram_id"); //shouldn't need, script should handle this
            isTutor = extras.getString("is_tutor");
            thirtyMinuteRate = extras.getString("thirty_minute");
            sixtyMinuteRate = extras.getString("sixty_minute");
        }


        formattedDate = "";

        spnCourseSearch = (Spinner) findViewById(R.id.spnCourseSearchAuto_SearchPage);
        spnAvailableFrom = (Spinner) findViewById(R.id.spnAvailableFrom_SearchPage);
        //spnAvalableTo = (Spinner) findViewById(R.id.spnAvailableTo_SearchPage);
        btnSearch = (Button) findViewById(R.id.btnSearch_SearchPage);
        chkThrityMinute = (CheckBox) findViewById(R.id.chkThirtyMinute_SearchPage);
        chkSixtyMinute = (CheckBox) findViewById(R.id.chkSixtyMinute_SearchPage);
        txtEndTime = (TextView) findViewById(R.id.txtEndTime_SearchPage);
        btnPickDate = (Button) findViewById(R.id.btnPickDate_SearchPage);
        spnBuildingsSearch = (Spinner) findViewById(R.id.spnBuildingSearchAuto_SearchPage);

        if(isTutor.equals("Y")){
            spnBuildingsSearch.setVisibility(View.INVISIBLE);
        }

        results = new ArrayList<>();
        coursesCodeArr = getResources().getStringArray(R.array.courses_code_array);
        timeArray = getResources().getStringArray(R.array.times_array);
        formattedTimesArray = getResources().getStringArray(R.array.times_array_visual);

        spnAvailableFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                start = Integer.valueOf(timeArray[spnAvailableFrom.getSelectedItemPosition()]);
                calculateEndTime();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                calculateEndTime();

            }
        });
        chkThrityMinute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateEndTime();
            }
        });


        chkSixtyMinute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateEndTime();
            }
        });



        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
                calculateEndTime();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spnCourseSearch.getSelectedItem().toString().equals("") && !spnAvailableFrom.getSelectedItem().toString().equals("") && !txtEndTime.getText().equals("")) {   //probably make better
                    if (isTutor.equals("Y") || isTutor.equals("N")) {
                        if((!chkThrityMinute.isChecked() && chkSixtyMinute.isChecked()) || (chkThrityMinute.isChecked() && !chkSixtyMinute.isChecked())){
                            if (rawDate != null) {
                                if(!rawDate.equals("")) {
                                    if(myDate.compareDates(rawDate)) {
                                        //First script, this adds the requested appointment, assuming there are no errors, it will run the match script
                                        Log.i("sel", Integer.toString(spnCourseSearch.getSelectedItemPosition()));
                                        try {
                                            final URL url = new URL(URL_REQUEST_APPT);
                                            OkHttpClient client = new OkHttpClient();

                                            if(isTutor.equals("N")) {
                                                building = String.valueOf(spnBuildingsSearch.getSelectedItem());
                                            }else{
                                                building = "empty";
                                            }
                                                RequestBody requestBody = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("email", email)
                                                        .addFormDataPart("course_id", coursesCodeArr[spnCourseSearch.getSelectedItemPosition()])
                                                        .addFormDataPart("app_date", rawDate)  //TODO: fix
                                                        .addFormDataPart("app_begin", timeArray[spnAvailableFrom.getSelectedItemPosition()])
                                                        .addFormDataPart("app_end", rawEndTime)
                                                        .addFormDataPart("is_tutor", isTutor)
                                                        .addFormDataPart("building", building)
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
                                                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE

                                                    if (!response.isSuccessful())
                                                        throw new IOException("Unexpected code " + response);
                                                    Log.i("RESPONSE", response.body().charStream().toString());
                                                    // GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                                    GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                                    //   Log.i("onResponse:Tutor,Stu", user.tutor_info + "," + user.student_info);
                                                    if (user.success_information_added_requested_appt != null) { //might need to make this better
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Success, request made", Toast.LENGTH_SHORT).show();
                                                                //match();
                                                                //instead of match, bring them back to the main menu
                                                                Intent i = new Intent(getApplicationContext(), Main_Menu.class);
                                                                i.putExtra("email", email);
                                                                i.putExtra("password", password);
                                                                startActivity(i);
                                                            }
                                                        });
                                                    } else if (user.insert_error_availability != null) {
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Error: You already have a request with that information", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }); //end runnable
                                                    } else if (user.error_requested_appt != null) {
                                                        Log.e(TAG, user.error_requested_appt.toString());
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(getApplicationContext(), "Error: Fatal Error, check log", Toast.LENGTH_SHORT).show();
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
                                    }else{ //compare dates check
                                        Toast.makeText(getApplicationContext(), "Appointment must be at least 1 day in advance.", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "You must pick a date", Toast.LENGTH_SHORT).show();
                                }
                        }//END IF date check
                             else{
                                Toast.makeText(getApplicationContext(), "You must pick a date", Toast.LENGTH_SHORT).show();
                        }

                        }//END IF checkbox check
                        else{
                            Toast.makeText(getApplicationContext(), "You must choose an end time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void calculateEndTime(){
        if(chkSixtyMinute.isChecked())
        {
            chkThrityMinute.setEnabled(false);
            startTime = timeArray[spnAvailableFrom.getSelectedItemPosition()];
            if (start < 930) {
                end = start + 100; //use this a formatted time?
                endTime = formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()+2];
                rawEndTime = timeArray[spnAvailableFrom.getSelectedItemPosition()+2];
                txtEndTime.setText(formattedDate + " from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + endTime);
                end = 0;
            } else if ((start >= 930) && (start < 2300)) {
                end = start + 100;
                endTime = formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()+2];
                rawEndTime = timeArray[spnAvailableFrom.getSelectedItemPosition()+2];
                txtEndTime.setText(formattedDate + " from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + endTime);
                end = 0;
            }else if(start == 2300){
                endTime = "0000";
                rawEndTime = timeArray[0];
                txtEndTime.setText(formattedDate + "from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + formattedTimesArray[0]);
            } else if (start == 2330) {
                endTime = "0030";
                rawEndTime = timeArray[1];
                txtEndTime.setText(formattedDate + "from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + formattedTimesArray[1]);
            }
        }
        else {
            chkThrityMinute.setEnabled(true);
        }
        if(chkThrityMinute.isChecked())    {
            chkSixtyMinute.setEnabled(false);
            if (start < 930){
                end = start + 30;
                endTime = formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()+1];
                rawEndTime = timeArray[spnAvailableFrom.getSelectedItemPosition()+1];
                txtEndTime.setText(formattedDate + "from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + endTime);
                end = 0;
            }
            else if ((start >= 930) && (start < 2330)) {
                end = start + 30;
                endTime = formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()+1];
                rawEndTime = timeArray[spnAvailableFrom.getSelectedItemPosition()+1];
                txtEndTime.setText(formattedDate + "from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + endTime);
                end = 0;
            } else if (start == 2330)  {
                endTime = "0000";
                rawEndTime = timeArray[0];
                txtEndTime.setText(formattedDate + "from " + formattedTimesArray[spnAvailableFrom.getSelectedItemPosition()] + " to " + formattedTimesArray[0]);
            }
        }
        else {
            chkSixtyMinute.setEnabled(true);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it

            return new DatePickerDialog(getActivity(), this, year, month, day);

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = sdf.format(c.getTime());
            rawDate = formattedDate.substring(5,7)+formattedDate.substring(8,10)+formattedDate.substring(2,4);
            Log.i("NEWDATE", rawDate);


        }
    }


}
/*

    private void match(){
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
                    Log.e(TAG,e.getMessage().toString());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException { //START RESPONSE
                    Log.i("MADE_IT","");
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    // GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                    GitUser[] user = gson.fromJson(response.body().charStream(), GitUser[].class);
                   // String s = user[0].Appointment_Begin;
                   // Log.i("GitUser", s);

                    //   Log.i("onResponse:Tutor,Stu", user.tutor_info + "," + user.student_info);
                    if (user[0].Tutor_ID != null || user[0].Ram_ID != null) { //might need to make this better
                        HashMap<String, String> temp = new HashMap<>();
                        for(int i = 0; i<user.length; i++){
                              if(isTutor.equals("Y")){
                                  temp.put("ram_id", user[i].Ram_ID);
                                  temp.put("student_email", user[i].Student_Email);
                              }else if(isTutor.equals("N")){
                                  temp.put("tutor_id", user[i].Tutor_ID);
                                  temp.put("tutor_email", user[i].Student_Email);
                                  temp.put("thirty_minute", user[i].Thirty_Minute_Rate);
                                  temp.put("sixty_minute", user[i].Sixty_Minute_Rate);
                              }
                              temp.put("tutor_app_id", user[i].Tutor_Appointment_ID);
                              temp.put("stu_app_id", user[i].Student_Appointment_ID);
                              temp.put("course_id", user[i].Course_ID);
                              temp.put("course_description", user[i].Course_Description);
                              temp.put("app_date", user[i].Appointment_Date);
                              temp.put("app_begin", user[i].Appointment_Begin);
                              temp.put("app_end", user[i].Appointment_End);
                              temp.put("first_name", user[i].First_Name);
                              temp.put("last_name", user[i].Last_Name);

                              results.add(temp);
                          }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Success: match(es) found.", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, results.toString());
                                //TODO: populate tutorMatches page
                                Intent i = new Intent(getApplicationContext(), TutorMatches.class);
                                i.putExtra("app_array", results);
                                i.putExtra("is_tutor", isTutor);
                                i.putExtra("availability", "false");
                                i.putExtra("possible_appointments", "true");
                                i.putExtra("appointments", "false");
                                startActivity(i);
                            }
                        });

                    } else if (user[0].failure_match_not_found != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO: Probably handle this on the appointment view screen.
                                Log.i(TAG, "Match not found");
                                Toast.makeText(getApplicationContext(), "Error: Fatal Error, check log", Toast.LENGTH_SHORT).show();
                            }
                        }); //end runnable
                    } else if(user[0].error_match != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error: Missing data", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Missing data");
                            }
                        }); //end runnable
                    }else if(user[0].failure_match != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Fatal Error: User doesn't exist", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "User doesn't exist");
                            }
                        }); //end runnable
                    } else if(user[0].error != null){
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
*/

