package com.wtw.qws.seniorprojecttutor;

import android.content.Intent;
import android.media.Rating;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.Connections;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Add_Review extends AppCompatActivity {

    private static final String URL_ADD_REVIEW = "http://tutorapplication.a2hosted.com/tutorapplication/add_review.php";
    private static final String TAG = "AddReview";
    private String firstName, lastName, userEmail, userPassword, appId;
    private EditText txtReview;
    private TextView txtTitle, txtCharCount;
    private Button btnSubmit;
    private RatingBar ratRating;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__review);

        final Bundle extras = getIntent().getExtras();
        if(extras != null){
            appId = extras.getString("app_id");
            firstName = extras.getString("first_name");
            lastName = extras.getString("last_name");
            userEmail = extras.getString("user_email");
            userPassword = extras.getString("user_password");
        }else{
            Log.e(TAG, "Error: extras are empty");

        }

        mHandler = new Handler(Looper.getMainLooper());

        txtReview = (EditText) findViewById(R.id.txtReviewText_AddReview);
        txtCharCount = (TextView) findViewById(R.id.txtReviewCharCount_AddReview);
        txtTitle = (TextView) findViewById(R.id.txtTitle_AddReview);
        btnSubmit = (Button) findViewById(R.id.btnSubmit_AddReview);
        ratRating = (RatingBar) findViewById(R.id.ratRatingBar_AddReview);

        txtTitle.setText(firstName + " " + lastName);



         final TextWatcher reviewCharacterCount = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtCharCount.setText(String.valueOf(charSequence.length()));
                if(charSequence.length() >= 1000){
                    txtCharCount.setText("You are at the maximum amount of allowed characters");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        txtReview.addTextChangedListener(reviewCharacterCount);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Send data to addReview script. Either add review in a textview in a fragment or something else.  Because we are here, the date is good, just check
                //to see if they left a review already in php (ex. if android side app_id exists in table then we probably cant leave one)
               if (reviewValidation(txtReview.getText().toString())) {
                       try {
                           final URL url = new URL(URL_ADD_REVIEW);
                           OkHttpClient client = new OkHttpClient();

                           RequestBody requestBody = new MultipartBody.Builder()
                                   .setType(MultipartBody.FORM)
                                   .addFormDataPart("app_id", appId)
                                   .addFormDataPart("review", txtReview.getText().toString())
                                   .addFormDataPart("rating", String.valueOf(ratRating.getRating()))
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

                                   if (user.success_review_added != null) { //might need to make this better
                                       mHandler.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               Toast.makeText(getApplicationContext(), "Review added", Toast.LENGTH_SHORT).show();
                                               Intent i = new Intent(getApplicationContext(), Main_Menu.class);
                                               i.putExtra("email", userEmail);
                                               i.putExtra("password", userPassword);
                                               startActivity(i);
                                           }
                                       });
                                   } else if (user.error_add_review != null) {
                                       mHandler.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               //fatal error
                                               Toast.makeText(getApplicationContext(), "Fatal Error: doesn't exist", Toast.LENGTH_SHORT).show();
                                           }
                                       });

                                   } else if (user.error_review != null) {
                                       mHandler.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               //shouldn't be able to get here
                                               Toast.makeText(getApplicationContext(), "Error: review exists already", Toast.LENGTH_SHORT).show();
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
                   }//END IF RATING CHECK


            }
        });

    }

    private boolean reviewValidation(String reviewText){
        if(reviewText.length() > 25){
            if(reviewText.length() < 1000){
                if(ratRating.getRating() > 1.0){
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Minimum star rating is 1.0", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Reviews are limited to 1000 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(getApplicationContext(), "Reviews need to be at least 25 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
