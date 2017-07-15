package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class TutorMatches extends AppCompatActivity {
    ArrayList<HashMap<String, String>> possibleAppointments, pastAppointments, presentAppointments;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> titles, reviewsArr, ratingsArr;
    private String isTutor, availability, possible_appointments, appointments, email, password, isPresentAppointments, reviews, base64Str, reviewsFName, reviewsLName;
    private int clickLoc;
    private String[] formattedTimesArray, timesArray;
    private TextView titlePrompt;
    private ListView tutorListView;
    private Date myDate;
    private Toolbar myToolbar;
    private RatingBar ratRating;
    private ActionBar actionBar;
    MenuItem itmAppointments;
    private static final String URL = "http://tutorapplication.a2hosted.com/tutorapplication/get_matched_person_information.php";
    private static final String URL_DELETE_AVAILABILITY = "http://tutorapplication.a2hosted.com/tutorapplication/remove_availability.php";
    //private static final String URL_GET_IMAGE = "http://tutorapplication.a2hosted.com/tutorapplication/get_pictures.php";
    private static final String TAG = "TutorMatches";
    private Handler mHandler;
    private ArrayList<String> base64Arr;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_match_results);
        Log.i("ONCREATE", "in Oncreate");


        titles = new ArrayList<>();
        titlePrompt = (TextView) findViewById(R.id.txtTitlePrompt_MatchResults);
        tutorListView = (ListView) findViewById(R.id.lstResultsListView_MatchResults);
        ratRating = (RatingBar) findViewById(R.id.ratRatingBar_TutorMatches);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        ratRating.setVisibility(View.INVISIBLE);

        //itmAppointments = actionBar.findItem(R.id.itmAppointments);

        setSupportActionBar(myToolbar);
        //myToolbar.setVisibility(View.INVISIBLE);
        actionBar = getSupportActionBar();
        //actionBar.hide();
        actionBar.show();

       // myToolbar.setVisibility(View.INVISIBLE);


        //replaced with populate method
        /*Bundle extras = getIntent().getExtras();
        if(extras != null){
            possibleAppointments = (ArrayList<HashMap<String, String>>) extras.getSerializable("app_array");
            isTutor = extras.getString("is_tutor");
            availability = extras.getString("availability");
            pssible_appointments = extras.getString("possible_appointments");
            appointments = extras.getString("appointments");
        }*/


        myDate = new Date();
        populate();
        mHandler = new Handler(Looper.getMainLooper());
        base64Arr = new ArrayList<>();
        formattedTimesArray = getResources().getStringArray(R.array.times_array_visual);
        timesArray = getResources().getStringArray(R.array.times_array);
        /*if(possible_appointments.equals("true") || appointments.equals("true") || reviews.equals("true")) {
            for (int i = 0; i < possibleAppointments.size(); i++) {
                HashMap temp = possibleAppointments.get(i);
                getProfilePicture(String.valueOf(temp.get("email")));
            }
        }*/


        /**
         * Because the next blocks of code are a mess, were using (incorrectly) JavaDoc comments to help distinguish them
         */

        /**
         * User is looking for matches/ possible appointments
         */
        if(possible_appointments.equals("true")) {
           // actionBar.setTitle("Possible Appointments");

            for (int i = 0; i < possibleAppointments.size(); i++) {
                HashMap temp = possibleAppointments.get(i);
                titles.add(temp.get("first_name") + " " + temp.get("last_name"));

            }
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.results_list_item, titles);
            tutorListView.setAdapter(arrayAdapter);
            tutorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = titles.get(i);
                    Log.i("ADAPTERITEM", String.valueOf(i));
                    Intent intent = new Intent(getApplicationContext(), TutorProfile.class);
                    HashMap<String, String> temp = new HashMap<>();
                    temp = possibleAppointments.get(i);
                    //base64Str = getProfilePicture(temp.get("email"));
                    //base64Str = base64Arr.get(i);
                    //intent.putExtra("base64",base64Str);
                    intent.putExtra("is_tutor", isTutor);
                    intent.putExtra("user_email", email); //user email
                    intent.putExtra("user_password", password); //user password
                    intent.putExtra("email", temp.get("email")); //tutor/student email
                    intent.putExtra("first_name", temp.get("first_name"));
                    intent.putExtra("last_name", temp.get("last_name"));
                    intent.putExtra("app_date", temp.get("app_date"));
                    intent.putExtra("app_begin", temp.get("app_begin"));
                    intent.putExtra("app_end", temp.get("app_end"));
                    intent.putExtra("course_id", temp.get("course_id"));
                    intent.putExtra("course_desc", temp.get("course_desc"));
                    intent.putExtra("stu_app_id", temp.get("stu_app_id"));
                    intent.putExtra("tutor_app_id", temp.get("tutor_app_id"));
                    intent.putExtra("tutor_profile", "true");
                    intent.putExtra("app_profile", "false");
                    if (isTutor.equals("N")) {
                        intent.putExtra("thirty_minute", temp.get("thirty_minute"));
                        intent.putExtra("sixty_minute", temp.get("sixty_minute"));
                        Log.i("TESTY", temp.get("thirty_minute"));
                    }else{
                        intent.putExtra("building", temp.get("building"));
                    }
                    Log.i("TESTY", email + " " + temp.get("email"));
                    startActivity(intent);

                }
            });
            //availability button
            /**
             * User presses Availability button
             */
        }else if(availability.equals("true")) {

            Toast.makeText(getApplicationContext(), "Tap an item to delete it", Toast.LENGTH_SHORT).show();
            actionBar.setTitle("Availability");
            titlePrompt.setVisibility(View.INVISIBLE);
            // titlePrompt.setText("My Availability");
            for (int i = 0; i < possibleAppointments.size(); i++) {
                HashMap temp = possibleAppointments.get(i);

                titles.add(formatDate(temp.get("app_date").toString()) + ", " + formatTime(temp.get("app_begin").toString()) + "-" + formatTime(temp.get("app_end").toString()) + ", " + temp.get("course_id"));


            arrayAdapter = new ArrayAdapter<String>(this, R.layout.results_list_item, titles);
            tutorListView.setAdapter(arrayAdapter);
            tutorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = titles.get(i);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(TutorMatches.this);
                    builder1.setTitle("Delete item?");
                    builder1.setMessage("Are you sure you would like to delete the availability for: " + item + "?");
                    builder1.setCancelable(true);
                     final int temp = i;
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    clickLoc = temp;
                                    deleteAvailability(temp);
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
        }

            /**
             * User is looking for their appointments.  This block runs each result from the appointments table through a date checker to determine if they are past appointments or future ones
             */
        }else if(appointments.equals("true")){
            //titlePrompt.setText("My Appointments");
            if(isPresentAppointments.equals("true")) {
                actionBar.setTitle("Appointments");
            }else{
                actionBar.setTitle("Past Appointments");
            }
            for (int i = 0; i < possibleAppointments.size(); i++) {
           // for (int i = 0; i < presentAppointments.size(); i++) {
                HashMap temp = possibleAppointments.get(i);

                //HashMap temp = presentAppointments.get(i);
                titles.add(temp.get("course_id") + " on " + formatDate(temp.get("app_date").toString()) + " with " + temp.get("first_name") + " " + temp.get("last_name"));

                arrayAdapter = new ArrayAdapter<String>(this, R.layout.results_list_item, titles);
                tutorListView.setAdapter(arrayAdapter);
                tutorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getApplicationContext(), TutorProfile.class);
                        HashMap<String, String> temp = possibleAppointments.get(i);
                        //getProfilePicture(i, temp.get("email"));
                        //intent.putExtra("base64",getProfilePicture(temp.get("email")));
                        //base64Str = base64Arr.get(test);
                        //intent.putExtra("base64",base64Str);
                        intent.putExtra("is_tutor", isTutor);
                        intent.putExtra("user_email", email); //USER email
                        intent.putExtra("user_password", password);
                        intent.putExtra("email", temp.get("email"));//student/user email -NOT USER
                        intent.putExtra("app_id", temp.get("app_id"));
                        intent.putExtra("first_name", temp.get("first_name"));
                        intent.putExtra("last_name", temp.get("last_name"));
                        intent.putExtra("app_date", temp.get("app_date"));
                        intent.putExtra("app_begin", temp.get("app_begin"));
                        intent.putExtra("app_end", temp.get("app_end"));
                        intent.putExtra("course_id", temp.get("course_id"));
                        intent.putExtra("course_desc", temp.get("course_desc"));
                        intent.putExtra("stu_app_id", temp.get("stu_app_id"));
                        intent.putExtra("tutor_app_id", temp.get("tutor_app_id"));
                        intent.putExtra("tutor_profile", "true");
                        intent.putExtra("app_profile", "false");
                        intent.putExtra("building", temp.get("building"));
                        // Log.i("TESTS", "user email" + email + "nonuser email " + temp.get("email"));
                        if (isTutor.equals("Y")) {
                            intent.putExtra("thirty_minute", temp.get("thirty_minute"));
                            intent.putExtra("sixty_minute", temp.get("sixty_minute"));
                        }
                        else if(isTutor.equals("N")){
                            intent.putExtra("total", temp.get("total"));
                            //Log.i("TESSST", temp.get("total"));
                        }
                        intent.putExtra("tutor_profile", "false");
                        intent.putExtra("app_profile", "true");
                        if(isPresentAppointments.equals("true")){
                            intent.putExtra("present_appointments", "true");
                        }else{
                            intent.putExtra("present_appointments", "false");
                        }
                        startActivity(intent);

                    }

                });
            }
            /**
             * Reviews: User comes here from a tutor profile
             */
        }else if(reviews.equals("true")){
            ratRating.setVisibility(View.VISIBLE);
            ratRating.setNumStars(roundRating(ratingsArr));
            Log.i("REVIEWSARR", String.valueOf(reviewsArr.size()));

            for (int i = 0; i < reviewsArr.size(); i++) {
                titles.add(reviewsArr.get(i).substring(0, (reviewsArr.get(i).length() / 5)) + "...");
            }
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.results_list_item, titles);
            tutorListView.setAdapter(arrayAdapter);
            tutorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), ReviewDetailView.class);
                    intent.putExtra("review", reviewsArr.get(i));
                    intent.putExtra("rating", ratingsArr.get(i));
                    intent.putExtra("first_name", reviewsFName);
                    intent.putExtra("last_name", reviewsLName);
                    startActivity(intent);

                }

            });



        }

    }

    protected void onStart(){
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume(){
        super.onResume();
        myDate = new Date();
        populate();
        mHandler = new Handler(Looper.getMainLooper());
        base64Arr = new ArrayList<>();

    }


/*
    //old menu stuff

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tutor_match_menu, menu);
        //getActionBar().show();
        itmAppointments = menu.getItem(0);
        if(appointments.equals("true")){
            itmAppointments.setVisible(true);
        }else{
            itmAppointments.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //tutorial: https://developer.android.com/training/appbar/actions.html
        switch (item.getItemId()) {
            case R.id.itmAppointments:

                if(itmAppointments.getTitle().equals("Past Appointments")) {
                    itmAppointments.setTitle("Future Appointments");
                }else if(itmAppointments.getTitle().equals("Future Appointments")){
                    itmAppointments.setTitle("Past Appointments");
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    */


    /**
     * This method contains the HTTP request code for deleting an avilability item.  Only accessbile
     * when the user is viewing availability
     * @param clickLoc
     */
    private void deleteAvailability( int clickLoc){
        final String appId;
        final int test = clickLoc;
        HashMap<String, String> temp = possibleAppointments.get(clickLoc);
        if(isTutor.equals("Y")){
            appId = temp.get("tutor_app_id");
       }else{
            appId = temp.get("stu_app_id");
        }

        try {
            final URL url = new URL(URL_DELETE_AVAILABILITY);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("is_tutor", isTutor)
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
                    //TODO: something
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);

                    if (user.delete_availability_success != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Availability successfully deleted", Toast.LENGTH_SHORT).show();
                                titles.remove(appId);
                                arrayAdapter.remove(arrayAdapter.getItem(test));
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    } else if (user.delete_availability_error != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Fatal error: Availability deletion failed", Toast.LENGTH_SHORT).show();
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

  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent i = new Intent(getApplicationContext(), Main_Menu.class);
            HashMap<String, String> temp = new HashMap<>();
            temp = possibleAppointments.get(0);
            Log.i("EMAIL", temp.get("email"));
            i.putExtra("email", temp.get("email"));
            i.putExtra("password", temp.get("password"));
            startActivity(i);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }*/
 /* @Override
  protected void onResume() {
      // TODO Auto-generated method stub
      super.onResume();
      results = new ArrayList<>();
      getUserInformation();
  }*/

    /**
     * This method is used when an activity that is called from this activity (TutorProfile) finishes.  It is designed to
     * catch the result code and extras passed from the called activity upon its finish.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case RESULT_OK:
                Bundle extras = data.getExtras();
                if(extras != null){
                    if(extras.get("app_deleted").equals("true")){
                        Toast.makeText(getApplicationContext(), "HEREEEE  ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.e(TAG, "onActivityResult: Extras empty, line:" + Thread.currentThread().getStackTrace()[2].getLineNumber());
                }
                break;
            default:
                Log.e(TAG,"onActivityResult: Fatal Error, line:" + Thread.currentThread().getStackTrace()[2].getLineNumber() );
        }
    }

    /**
     * General initialization method used to populate the data passed from the previous Activity and split up past and future appointments.  Called in the onCreate method
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populate(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            possibleAppointments =  (ArrayList<HashMap<String, String>>) extras.getSerializable("app_array");
            reviewsArr = (ArrayList<String>) extras.getSerializable("reviews_arr");
            ratingsArr = (ArrayList<String>) extras.getSerializable("ratings_arr");
            //presentAppointments =  new ArrayList<HashMap<String, String>>();
            //pastAppointments =  new ArrayList<HashMap<String, String>>();
            isTutor = extras.getString("is_tutor");
            availability = extras.getString("availability");
            possible_appointments = extras.getString("possible_appointments");
            appointments = extras.getString("appointments");
            email = extras.getString("user_email");
            password = extras.getString("user_password");
            isPresentAppointments = extras.getString("present_appointments");
            reviews = extras.getString("reviews");
            reviewsFName = extras.getString("first_name");
            reviewsLName = extras.getString("last_name");
           // Log.i("populate()", user_email);
            //split up past and future appointments
            //HashMap<String, String> temp;
            /*for(int i = 0; i<possibleAppointments.size(); i++){
                temp = possibleAppointments.get(i);
                if(myDate.compareDates(temp.get("app_date"))){
                    presentAppointments.add(temp);
                }else{
                    pastAppointments.add(temp);
                }
            }*/
        }else{
            Log.e("EXTRAS_NULL", "populate(): extras are null");
        }
    }

    /**
     * This method is used to convert the String arraylist of ratings into a single, averaged, integer value
     * @param arr
     * @return
     */
    private int roundRating(ArrayList<String> arr){
        double total = 0;
        for(int i = 0; i < arr.size(); i++){
            total += Double.parseDouble(arr.get(i));
        }
        return (int) Math.rint(total);
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
