package com.wtw.qws.seniorprojecttutor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Created by josianearmand on 4/23/17.
 */

public class Maps_Calender extends AppCompatActivity implements OnMapReadyCallback{

   // GoogleMap mgoogleMap;
   // String meeting_location; //stores which building the user chose from the markers
    String isTutor = "N"; //variable to check if user is a student or tutor
    GoogleMap mgoogleMap;
    String meeting_location = "Alumni Hall"; //stores which building the user chose
    MarkerOptions alumni_hall = new MarkerOptions().position(new LatLng(40.7511748,-73.4289978)).title("Alumni Hall");
    MarkerOptions campus_center = new MarkerOptions().position(new LatLng(40.7541418,-73.4305168)).title("Campus Center");
    MarkerOptions conklin_hall = new MarkerOptions().position(new LatLng(40.7534965,-73.4311346)).title("Conklin Hall");
    MarkerOptions dewey_hall = new MarkerOptions().position(new LatLng(40.7516012,-73.4281755)).title("Dewey Hall");
    MarkerOptions gleeson_hall = new MarkerOptions().position(new LatLng(40.7530083,-73.4307615)).title("Gleeson Hall");
    MarkerOptions greenley_library = new MarkerOptions().position(new LatLng(40.7515185,-73.4316896)).title("Greenley Library");
    MarkerOptions hale_hall = new MarkerOptions().position(new LatLng(40.7520564,-73.4324267)).title("Hale Hall");
    MarkerOptions health_wellness_center = new MarkerOptions().position(new LatLng(40.7507326,-73.4290692)).title("Health and Wellness Center");
    MarkerOptions hooper_hall = new MarkerOptions().position(new LatLng(40.7510118,-73.4294148)).title("Hooper Hall");
    MarkerOptions horton_hall = new MarkerOptions().position(new LatLng(40.7520564,-73.4324267)).title("Horton Hall");
    MarkerOptions knapp_hall = new MarkerOptions().position(new LatLng(40.7514944,-73.4305936)).title("Knapp Hall");
    MarkerOptions laffin_hall = new MarkerOptions().position(new LatLng(40.7507638,-73.43076)).title("Laffin Hall");
    MarkerOptions lupton_hall = new MarkerOptions().position(new LatLng(40.7507493,-73.4334162)).title("Lupton Hall");
    MarkerOptions memorial_hall = new MarkerOptions().position(new LatLng(40.7506944,-73.4296519)).title("Memorial Hall");
    MarkerOptions nold_hall = new MarkerOptions().position(new LatLng(40.748911,-73.4337001)).title("Nold Hall");
    MarkerOptions orchard_hall = new MarkerOptions().position(new LatLng(40.7509432,-73.4265504)).title("Orchard Hall");
    MarkerOptions roosevelt_hall = new MarkerOptions().position(new LatLng(40.7505813,-73.4319413)).title("Roosevelt Hall");
    MarkerOptions sinclair_hall = new MarkerOptions().position(new LatLng(40.7521708,-73.4279054)).title("Sinclair Hall");
    MarkerOptions thompson_hall = new MarkerOptions().position(new LatLng(40.7535122,-73.4296106)).title("Thompson Hall");
    MarkerOptions ward_hall = new MarkerOptions().position(new LatLng(40.7524003,-73.4311857)).title("Ward Hall");
    MarkerOptions whitman_hall = new MarkerOptions().position(new LatLng(40.7524537,-73.4300858)).title("Whitman Hall");
    Spinner choose_a_location_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        final Bundle extras = getIntent().getExtras();
        if (null != extras) {
            meeting_location = extras.getString("building");
        }
        final CheckBox show_all_locations = (CheckBox) findViewById(R.id.show_all_checkBox);
        initMap();

        choose_a_location_spinner = (Spinner) findViewById(R.id.choose_a_location_spinner);


        // Get the string array
        String[] campus_buildings = getResources().getStringArray(R.array.campus_buildings);
        //set textview and the option to pick a building to be invisible if they're a tutor
        if(isTutor.equals("Y"))
        {
            choose_a_location_spinner.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.textView);
            TextView textView2 = (TextView) findViewById(R.id.textView2);

            textView.setText("Meeting Location: " + meeting_location);
            textView2.setVisibility(View.GONE);
            show_all_locations.setVisibility(View.GONE);
        }

        show_all_locations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (show_all_locations.isChecked())
                {
                    mgoogleMap.addMarker(alumni_hall);
                    mgoogleMap.addMarker(campus_center);
                    mgoogleMap.addMarker(conklin_hall);
                    mgoogleMap.addMarker(dewey_hall);
                    mgoogleMap.addMarker(gleeson_hall);
                    mgoogleMap.addMarker(greenley_library);
                    mgoogleMap.addMarker(hale_hall);
                    mgoogleMap.addMarker(health_wellness_center);
                    mgoogleMap.addMarker(hooper_hall);
                    mgoogleMap.addMarker(horton_hall);
                    mgoogleMap.addMarker(knapp_hall);
                    mgoogleMap.addMarker(laffin_hall);
                    mgoogleMap.addMarker(lupton_hall);
                    mgoogleMap.addMarker(memorial_hall);
                    mgoogleMap.addMarker(nold_hall);
                    mgoogleMap.addMarker(orchard_hall);
                    mgoogleMap.addMarker(roosevelt_hall);
                    mgoogleMap.addMarker(sinclair_hall);
                    mgoogleMap.addMarker(thompson_hall);
                    mgoogleMap.addMarker(ward_hall);
                    mgoogleMap.addMarker(whitman_hall);

                }
                else if (!show_all_locations.isChecked())
                {
                    //clear all the markers and add the one they selected previously
                    mgoogleMap.clear();

                    addAMarker();

                }
            }
        });
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, campus_buildings);
        choose_a_location_spinner.setAdapter(adapter);

        //Setting the starting location = to the meeting place wanted by the student
        for(int i = 0; i < campus_buildings.length; i++) {
            if(campus_buildings[i].equals(meeting_location)) {
                choose_a_location_spinner.setSelection(i);
            }
        }

        //return the building the user picked from spinner
        choose_a_location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String building = choose_a_location_spinner.getSelectedItem().toString();
                meeting_location = building;
                Toast.makeText(getApplicationContext(), building, Toast.LENGTH_SHORT);
                if (building.equals("School of Business"))
                {
                    Context context = getApplicationContext();
                    CharSequence text = meeting_location + "google maps location not available.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = meeting_location;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    mgoogleMap.clear();
                    addAMarker();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void addAMarker()
    {
        if (meeting_location.equals(alumni_hall.getTitle()) )
        {
            mgoogleMap.addMarker(alumni_hall);
        }
        else if (meeting_location.equals(campus_center.getTitle()))
        {
            mgoogleMap.addMarker(campus_center);
        }
        else if (meeting_location.equals(conklin_hall.getTitle()))
        {
            mgoogleMap.addMarker(conklin_hall);
        }
        else if (meeting_location.equals(dewey_hall.getTitle()))
        {
            mgoogleMap.addMarker(dewey_hall);
        }
        else if (meeting_location.equals(gleeson_hall.getTitle()))
        {
            mgoogleMap.addMarker(gleeson_hall);
        }
        else if (meeting_location.equals(greenley_library.getTitle()) )
        {
            mgoogleMap.addMarker(greenley_library);
        }
        else if (meeting_location.equals(hale_hall.getTitle()) )
        {
            mgoogleMap.addMarker(hale_hall);
        }
        else if (meeting_location.equals(health_wellness_center.getTitle()) )
        {
            mgoogleMap.addMarker(health_wellness_center);
        }
        else if (meeting_location.equals(hooper_hall.getTitle()))
        {
            mgoogleMap.addMarker(hooper_hall);
        }

        else if (meeting_location.equals(horton_hall.getTitle()) )
        {
            mgoogleMap.addMarker(horton_hall);
        }
        else if (meeting_location.equals(knapp_hall.getTitle()) )
        {
            mgoogleMap.addMarker(knapp_hall);
        }

        else if (meeting_location.equals(laffin_hall.getTitle()))
        {
            mgoogleMap.addMarker(laffin_hall);
        }
        else if (meeting_location.equals(lupton_hall.getTitle()))
        {
            mgoogleMap.addMarker(lupton_hall);
        }
        else if (meeting_location.equals(memorial_hall.getTitle()))
        {
            mgoogleMap.addMarker(memorial_hall);
        }
        else if (meeting_location.equals(nold_hall.getTitle()) )
        {
            mgoogleMap.addMarker(nold_hall);
        }
        else if (meeting_location.equals(orchard_hall.getTitle()))
        {
            mgoogleMap.addMarker(orchard_hall);
        }
        else if (meeting_location.equals(roosevelt_hall.getTitle()))
        {
            mgoogleMap.addMarker(roosevelt_hall);
        }
        else if (meeting_location.equals(sinclair_hall.getTitle()))
        {
            mgoogleMap.addMarker(sinclair_hall);
        }
        else if (meeting_location.equals(thompson_hall.getTitle()))
        {
            mgoogleMap.addMarker(thompson_hall);
        }
        else if (meeting_location.equals(ward_hall.getTitle()))
        {
            mgoogleMap.addMarker(ward_hall);
        }
        else if (meeting_location.equals(whitman_hall.getTitle()))
        {
            mgoogleMap.addMarker(whitman_hall);
        }

    }

    //constructor for map fragment
    private void initMap()
    {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }


    //check if google play services is on the device
    public boolean googleServicesAvailable() {
        GoogleApiAvailability maps_api = GoogleApiAvailability.getInstance();
        int isAvailable = maps_api.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if(maps_api.isUserResolvableError(isAvailable))
        {
            Dialog dialog = maps_api.getErrorDialog(this, isAvailable, 0);
        }
        else
        {
            Toast.makeText(this, "Can't connect to Google Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;

        //coordinates for farmingdale state college lat, long, zoomed in amount
        goToLocationZoom(40.7525539,-73.4309275, 15);
        addAMarker();

    }

    //for testing purposes, this function is not really needed or used
    //map view: farmingdale state college
    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng (lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mgoogleMap.moveCamera(update);


    }

    //map view: farmingdale state college zoomed in
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng (lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mgoogleMap.moveCamera(update);
    }
}

