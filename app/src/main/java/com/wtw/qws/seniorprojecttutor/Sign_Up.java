package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.opengl.EGLDisplay;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.jar.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Sign_Up extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_PERMISSION_READ = 1001;

    private String email, password, firstName, lastName, isTutor, phoneNumber, ram_id, thirtyMinuteRate, sixtyMinuteRate, originalIsTutor, hasAppointments;
    private EditText txtFirstName, txtLastName, txtPhoneNumber, txtThirtyMinute, txtSixtyMinute, txtRamId;
    private TextView txtTutorRatePrompt, txtRamIdPrompt;
    private CheckBox chkTutor, chkStudent;
    private ImageView imgProfilePicture;
    private boolean noAppointments;
    private Button btnSubmit, btnPicture, btnPictureSubmit;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    private ArrayList<String> settingsArray;
    private static final String URL = "http://tutorapplication.a2hosted.com/tutorapplication/user_sign_up.php";
    private static final String URL_APPOINTMENTS = "http://tutorapplication.a2hosted.com/tutorapplication/get_user_appointments.php";
    private Handler mHandler;
    private Date myDate;

    //photo stuff
    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;
    private boolean permissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        myDate = new Date();
        settingsArray = new ArrayList<>();
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //The key argument here must match that used in the other activity
            email = extras.getString("email");
            password = extras.getString("password");
            firstName = extras.getString("first_name");
            lastName = extras.getString("last_name");
            originalIsTutor = extras.getString("is_tutor");
            isTutor = extras.getString("is_tutor");
            phoneNumber = extras.getString("phone_number");
            ram_id = extras.getString("ram_id");
            thirtyMinuteRate = extras.getString("thirty_minute");
            sixtyMinuteRate = extras.getString("sixty_minute");
            hasAppointments = extras.getString("has_appointments");
            // Log.i("HERE", ram_id);
        }
        noAppointments = false;

        txtFirstName = (EditText) findViewById(R.id.txtFirstName_SignUp);
        txtLastName = (EditText) findViewById(R.id.txtLastName_SignUp);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhone_SignUp);
        chkTutor = (CheckBox) findViewById(R.id.chkTutor_SignUp);
        chkStudent = (CheckBox) findViewById(R.id.chkStudent_SignUp);
        txtRamId = (EditText) findViewById(R.id.txtRamId_SignUp);
        txtThirtyMinute = (EditText) findViewById(R.id.txtTutorThirtyMin_SignUp);
        txtSixtyMinute = (EditText) findViewById(R.id.txtTutorSixtymin_SignUp);
        txtRamIdPrompt = (TextView) findViewById(R.id.txtRamIdPrompt_SignUp);
        txtTutorRatePrompt = (TextView) findViewById(R.id.txtTutorRatePrompt_SignUp);
        btnSubmit = (Button) findViewById(R.id.btnSubmit_SignUp);
        btnPicture = (Button) findViewById(R.id.btnProfilePicture_SignUp);
        imgProfilePicture = (ImageView) findViewById(R.id.imgProfilePicture_SignUp);
        btnPictureSubmit = (Button) findViewById(R.id.btnProfilePictureSubmit_SignUp);

        btnPictureSubmit.setEnabled(false);

        txtTutorRatePrompt.setVisibility(View.INVISIBLE);
        txtRamId.setVisibility(View.INVISIBLE);
        txtThirtyMinute.setVisibility(View.INVISIBLE);
        txtSixtyMinute.setVisibility(View.INVISIBLE);
        txtRamIdPrompt.setVisibility(View.INVISIBLE);

        if (firstName != null) {
            btnSubmit.setEnabled(false);
            getAppointments();
            txtFirstName.setText(firstName);
            txtLastName.setText(lastName);
            txtPhoneNumber.setText(phoneNumber);
            if (isTutor.equals("Y")) {
                chkTutor.setChecked(true);
                chkStudent.setChecked(false);
                txtRamId.setVisibility(View.INVISIBLE);
                txtRamIdPrompt.setVisibility(View.INVISIBLE);
                txtTutorRatePrompt.setVisibility(View.VISIBLE);
                txtThirtyMinute.setVisibility(View.VISIBLE);
                txtSixtyMinute.setVisibility(View.VISIBLE);
                txtSixtyMinute.setText(sixtyMinuteRate);
                txtThirtyMinute.setText(thirtyMinuteRate);
            } else {
                chkStudent.setChecked(true);
                chkTutor.setChecked(false);
                txtRamId.setVisibility(View.VISIBLE);
                txtRamIdPrompt.setVisibility(View.VISIBLE);
                txtTutorRatePrompt.setVisibility(View.INVISIBLE);
                txtThirtyMinute.setVisibility(View.INVISIBLE);
                txtSixtyMinute.setVisibility(View.INVISIBLE);
                txtRamId.setText(ram_id);
            }
        } else {
            noAppointments = true;
        }

        mHandler = new Handler(Looper.getMainLooper());
        /**
         * OnCheckedChanged for chkStudent checkbox
         */
        chkStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkTutor.setChecked(false);
                    isTutor = "N";
                    txtRamId.setVisibility(View.VISIBLE);
                    txtRamIdPrompt.setVisibility(View.VISIBLE);
                    txtTutorRatePrompt.setVisibility(View.INVISIBLE);
                    txtThirtyMinute.setVisibility(View.INVISIBLE);
                    txtSixtyMinute.setVisibility(View.INVISIBLE);
                } else {
                    isTutor = "Y";
                    chkTutor.setChecked(true);
                    chkStudent.setChecked(false);
                }
            }
        });

        /**
         * OnCheckedChanged for chkTutor checkbox
         */
        chkTutor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkStudent.setChecked(false);
                    isTutor = "Y";
                    txtRamId.setVisibility(View.INVISIBLE);
                    txtRamIdPrompt.setVisibility(View.INVISIBLE);
                    txtTutorRatePrompt.setVisibility(View.VISIBLE);
                    txtThirtyMinute.setVisibility(View.VISIBLE);
                    txtSixtyMinute.setVisibility(View.VISIBLE);
                } else {
                    isTutor = "N";
                    chkStudent.setChecked(true);
                    chkTutor.setChecked(false);
                }
            }
        });


        /**
         * OnClickListener for profile picture.
         */
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);*/

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


              /*if (getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA)) {
                    // Open default camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    // start the image capture Intent
                    startActivityForResult(intent, 100);

                } else {
                    Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                }
                Bitmap bm = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte[] ba = bao.toByteArray();
                uploadPicture();*/

            }
        });

        btnPictureSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //uploadImage();
                if(!permissionGranted) {
                    checkPermissions();
                    uploadFile();
                    return;
                }else{
                    uploadFile();
                }
                //uploadFile(getPath(filePath));



            }
        });


        /**PRE: 1.) Form is filled out in its entirety 2.) One and only one checkbox is checked
         * btnSubmit onClickListener
         * Updates the user's values in the user table
         */
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nameValidator(txtFirstName.getText().toString()) && nameValidator(txtLastName.getText().toString()) && phoneValidator(txtPhoneNumber.getText().toString())
                        && (chkStudent.isChecked() && ramValidator(txtRamId.getText().toString())) ||
                        (chkTutor.isChecked() && rateValidator(txtThirtyMinute.getText().toString()) && rateValidator(txtSixtyMinute.getText().toString()))){
                    if(!chkTutor.isChecked() && !chkStudent.isChecked()){
                        Toast.makeText(getApplicationContext(), "Please check either tutor or student", Toast.LENGTH_SHORT).show();
                    }else {
                        if (noAppointments == true || originalIsTutor == isTutor) {
                            //if(txtPhoneNumber.getText().toString().contains("'") || txtPhoneNumber.getText().toString().contains(","))

                            try {
                                URL url = new URL(URL);
                                OkHttpClient client = new OkHttpClient();

                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("email", email)
                                        .addFormDataPart("password", password)
                                        .addFormDataPart("first_name", txtFirstName.getText().toString())
                                        .addFormDataPart("last_name", txtLastName.getText().toString())
                                        .addFormDataPart("is_tutor", isTutor)
                                        .addFormDataPart("phone_number", txtPhoneNumber.getText().toString())
                                        .addFormDataPart("ram_id", txtRamId.getText().toString())
                                        .addFormDataPart("thirty_minute", txtThirtyMinute.getText().toString())
                                        .addFormDataPart("sixty_minute", txtSixtyMinute.getText().toString())
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
                                        GitUser user = gson.fromJson(response.body().charStream(), GitUser.class);
                                        //   Log.i("GitUser", user.success_);
                                        if (user.success_user_added != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Account created for: " + email, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
                                                }
                                            }); //end runnable
                                        } else if (user.error_user_added != null || user.student_info_error != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "That RAM ID is already in use", Toast.LENGTH_SHORT).show();
                                                }
                                            }); //end runnable
                                        } else if (user.student_info_update_error != null || user.error_update != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "No new data, exiting ", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
                                                }
                                            }); //end runnable
                                        } else if (user.student_info_success != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Successfully changed to student", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
                                                }
                                            }); //end runnable
                                        } else if (user.student_info_update_success != null || user.tutor_info_update_success != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Data successfully updated", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
                                                }
                                            }); //end runnable
                                        } else if (user.tutor_info_success != null) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Successfully changed to a tutor", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Main_Menu.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("password", password);
                                                    startActivity(intent);
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
                        } else {  //appointments check
                            Toast.makeText(getApplicationContext(), "You already have appointments, cancel them before switching roles", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }//END onClick
        });//END onClickListener


    }

    /**
     * This method queries the database for the users appointments, if any, to determine if they can switch roles
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAppointments() {
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

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Log.i("RESPONSE", response.body().charStream().toString());
                    GitUser[] user = gson.fromJson(response.body().charStream(), GitUser[].class);

                    if (user[0].Tutor_ID != null && user[0].Ram_ID != null) {
                        for(int x = 0; x < user.length; x++){
                            if(myDate.compareDates(user[x].Appointment_Date)){
                                Log.i("TEMP", user[x].Appointment_Date);
                                noAppointments = false;
                                break;
                            }else{
                                noAppointments = true;
                            }
                        }
                        mHandler.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {

                               // noAppointments = false;
                                btnSubmit.setEnabled(true);

                            }
                        });
                   } else if (user[0].error_get_appointments != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                noAppointments = true;
                                btnSubmit.setEnabled(true);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProfilePicture.setImageBitmap(bitmap);
                btnPictureSubmit.setEnabled(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method that gets the path of a file given its location
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    /**
     * This method is responsible for uploading the users profile picture
     */
    public void uploadFile() {
        try {
            Log.i("UPLOADFILE", "HERE");
            final URL urlPic = new URL("http://tutorapplication.a2hosted.com/tutorapplication/upload_picture.php");
            OkHttpClient client = new OkHttpClient();
            File file = new File(getPath(filePath));
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                    .addFormDataPart("email", email)
                    .addFormDataPart("name", "nameTest")
                    .build();

            Request request = new Request.Builder().url(urlPic)
                    .post(requestBody).build();

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

                    if (user.success_file_move != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("SUCESSFILE", "File moved");
                                Toast.makeText(getApplicationContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else if (user.error_file_move != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("ERRORFULE", "file not moved");
                                Toast.makeText(getApplicationContext(), "Error: Picture Not Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        }); //end runnable

                    } else if (user.error_match != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("ERRORFILE", "not enough params");



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
     * The following methods are needed for API level 22 and above.  They work, don't touch.
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageReadable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Handles validation for the first name and last name textbox
     * @param nameText
     * @return boolean
     */
    private boolean nameValidator(String nameText){
        if(nameText.length() > 0){
            if(!nameText.contains("'") || !nameText.contains(",")){
                if(nameText.matches("[a-zA-Z]+")){
                    if(!nameText.contains(" ")){
                        return true;
                    }else{
                        Toast.makeText(getApplicationContext(), "First or last name cannot contain spaces", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "First or last name can only contain letters", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "First of last name cannot contain a \"'\" or \",\"", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "First or last name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Handles validation for the phone number
     * @param phoneText
     * @return boolean
     */
    private boolean phoneValidator(String phoneText){
        if (phoneText.length() == 10) {
            if (!phoneText.contains(" ")) {
                if(phoneText.matches("[0-9]+")){
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Phone number can only contain digits", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Phone number cannot contain spaces", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean ramValidator(String ramText){
        if(ramText.length() == 9){
            if(txtRamId.getText().charAt(0) == 'R'){
                if(txtRamId.getText().toString().substring(1, 9).matches("[0-9]+")){
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Following the R, the Ram ID must contain 8 digits", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Ram must begin with an R", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Ram must begin with an R and be followed by 8 digits", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean rateValidator(String rateText){
        if(rateText.length() > 0) {
            if (rateText.length() < 4) {
                if (!rateText.contains(" ")) {
                    if (rateText.matches("[0-9]+")){
                        return true;
                    }else{
                        Toast.makeText(getApplicationContext(), "Rates must only contain digits", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Rates cannot contain spaces", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Rates cannot be higher than $999", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Rate(s) is/are empty", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}



