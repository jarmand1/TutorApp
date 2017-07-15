package com.wtw.qws.seniorprojecttutor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {



    private EditText txtEmail, txtPassword;
    private Button btnSignIn, btnSignUp;
    private RequestQueue requestQueue;
   // private static final String URL = "http://10.0.2.2:80/tutorapplication/user_control.php";
   // private static final String URL = "http://192.168.1.11:80/tutorapplication/user_control.php";
    //private static final String URL = "http://localhost/tutorial2/user_control.php";
    private static final String URL = "http://tutorapplication.a2hosted.com/tutorapplication/user_control.php";
    private StringRequest request;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = (EditText) findViewById(R.id.txtEmail_MainActivity);
        txtPassword = (EditText) findViewById(R.id.txtPassword_MainActivity);
        btnSignIn = (Button) findViewById(R.id.btnLogIn_MainActivity);
    btnSignUp = (Button) findViewById(R.id.btnSignUp_MainActivity);

        //requestQueue = Volley.newRequestQueue(this);
        mHandler = new Handler(Looper.getMainLooper());

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("MainActivity","onClick");
                if(emailValidator(txtEmail.getText().toString()) && passwordValidator(txtPassword.getText().toString())){
                        try {
                            URL url = new URL(URL);
                            OkHttpClient client = new OkHttpClient();

                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("email", txtEmail.getText().toString())
                                    .addFormDataPart("password", txtPassword.getText().toString())
                                    .build();

                            final okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();

                            //okhttp3.Response response = client.newCall(request).execute(); //for non async

                            final Gson gson = new Gson();
                            client.newCall(request).enqueue(new Callback() { //for async
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    //TODO: something
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                    if (!response.isSuccessful())
                                        throw new IOException("Unexpected code " + response);
                                    GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                    //   Log.i("GitUser", user.success_);
                                    if (user.success_log_in != null) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Welcome, " + txtEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                intent.putExtra("email", txtEmail.getText().toString());
                                                intent.putExtra("password", txtPassword.getText().toString());
                                                startActivity(intent);
                                            }
                                        }); //end runnable
                                    } else if (user.error_log_in_password != null){
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Incorrect password ", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else if (user.error_log_in != null) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.i("HERE", "here");
                                                Toast.makeText(getApplicationContext(), "Account doesn't exist ", Toast.LENGTH_SHORT).show();

                                            }
                                        }); //end runnable
                                    } else if (user.error != null) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
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

            }//END onClick
        });//END onClickListener

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailValidator(txtEmail.getText().toString()) && passwordValidator(txtPassword.getText().toString())) {
                    try {
                        URL url = new URL(URL);
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("email", txtEmail.getText().toString())
                                .addFormDataPart("password", txtPassword.getText().toString())
                                .build();

                        final okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();

                        //okhttp3.Response response = client.newCall(request).execute(); //for non async

                        final Gson gson = new Gson();
                        client.newCall(request).enqueue(new Callback() { //for async
                            @Override
                            public void onFailure(Call call, IOException e) {
                                //TODO: something
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                //   Log.i("GitUser", user.success_);
                                if (user.error_log_in != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                            builder1.setTitle("Create Account?");
                                            builder1.setMessage("Are you sure you would like to create an account?");
                                            builder1.setCancelable(true);

                                            builder1.setPositiveButton(
                                                    "Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Toast.makeText(getApplicationContext(), "Please enter all necessary details", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), Sign_Up.class);
                                                            intent.putExtra("email", txtEmail.getText().toString());
                                                            intent.putExtra("password", txtPassword.getText().toString());
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
                                    }); //end runnable
                                }
                                if (user.success_log_in != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Account already exists", Toast.LENGTH_SHORT).show();

                                        }
                                    }); //end runnable
                                } else if (user.error != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Error, see log", Toast.LENGTH_SHORT).show();
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
            }
        });


    }//END onCreate

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("Exit Application?");
            builder1.setMessage("Are you sure you would like to exit the application?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);
                            //android.os.Process.killProcess(android.os.Process.myPid());
                            //System.exit(1);
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

    /**
     * Handles validation for the email textbox
     * @param emailText
     * @return
     */
    private boolean emailValidator(String emailText){
        if(emailText.length() == 22 || emailText.length() == 23){
            if(!emailText.contains("'") || !emailText.contains(",")){
                if(emailText.substring(0, 6).matches("[a-zA-Z]+") || emailText.substring(0, 7).matches("[a-zA-Z]+")){
                    if(emailText.charAt(6) != '@' || emailText.charAt(7) != '@'){
                        if(emailText.charAt(18) != '.' || emailText.charAt(19) != '.'){
                            return true;
                        }else{
                            Toast.makeText(getApplicationContext(), "Email is not in the correct format", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Email is not in the correct format", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Email is not in the correct format", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Email/Password cannot contain a \"'\" or \",\"", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Email must be 22 characters for students or 23 for professors", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean passwordValidator(String passwordText){
        if(passwordText.length() >= 4){
            if(!passwordText.contains(",") || !passwordText.contains("'")) {
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "Password cannot contain \",\" or \"'\"", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Password needs to be at least 4 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}//END MainActivity

  /* JSONObject post_dict = new JSONObject();

                    try{
                        post_dict.put("email", txtEmail.getText().toString());
                        post_dict.put("password", txtPassword.getText().toString());
                    }catch (JSONException e){
                        Log.e("JSON Error: " ,e.getMessage().toString());
                        e.printStackTrace();
                    }*/


//  new MakeConnection().execute(URL, String.valueOf(post_dict));