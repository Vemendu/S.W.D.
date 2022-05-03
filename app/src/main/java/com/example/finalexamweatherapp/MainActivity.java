package com.example.finalexamweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView;
    int PERMISSION_ID = 44;

    private EditText user_field;
    private Button main_btn;
    private Button main_btn1;
    private TextView result_info;
    private TextView result_info1;
    private TextView result_info2;
    private TextView result_info3;
    private TextView result_info4;
    private TextView result_info5;
    private TextView result_info6;
    private TextView result_info7;
    private TextView result_info8;
    private TextView result_info9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        main_btn1 = findViewById(R.id.main_btn1);
        result_info = findViewById(R.id.result_info);
        result_info1 = findViewById(R.id.result_info1);
        result_info2 = findViewById(R.id.result_info2);
        result_info3 = findViewById(R.id.result_info3);
        result_info4 = findViewById(R.id.result_info4);
        result_info5 = findViewById(R.id.result_info5);
        result_info6 = findViewById(R.id.result_info6);
        result_info7 = findViewById(R.id.result_info7);
        result_info8 = findViewById(R.id.result_info8);
        result_info9 = findViewById(R.id.result_info9);

        //КОД ДЛЯ ГЕОЛОКА
        latitudeTextView = findViewById(R.id.latTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        //КОД ДЛЯ ГЕОЛОКА

        main_btn.setOnClickListener(view -> {
            if(user_field.getText().toString().trim().equals(""))
                Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
            else{
                String city = user_field.getText().toString();
                String key = "0dfa972e43cefaa1abf721d95542480d\n";
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +"&appid="+ key +"&units=metric";


                new GetURLData().execute(url);



            }
        });

        main_btn1.setOnClickListener(view -> {
            getLastLocation();


                String city = (String) latitudeTextView.getText();
                String key = "0dfa972e43cefaa1abf721d95542480d\n";
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +"&appid="+ key +"&units=metric";


                new GetURLData().execute(url);




        });


    }

    //НАЧАЛО КОДА ДЛЯ ГЕОЛОКА

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {


            if (isLocationEnabled()) {


                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String city = "???";
                            try {
                                city = getCity(location.getLatitude(), location.getLongitude());
                            } catch (IOException e) {
                                System.out.println("OOPS");
                                e.printStackTrace();
                            }
                            latitudeTextView.setText(city + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            String city = "???";
            try {
                city = getCity(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } catch (IOException e) {
                System.out.println("OOPS");
                e.printStackTrace();
            }
            latitudeTextView.setText("City: " + city + "");
        }
    };

    private String getCity(double latitude, double longitude) throws IOException {

        Geocoder geocoder = new Geocoder(this, new Locale("en"));
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        return addresses.get(0).getLocality();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    //КОНЕЦ КОДА ДЛЯ ГЕОЛОКА

    private class GetURLData extends AsyncTask<String, String , String > {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Wait...");
            result_info1.setText("Wait...");
            result_info2.setText("Wait...");
            result_info3.setText("Wait...");
            result_info4.setText("Wait...");
            result_info5.setText("Wait...");
            result_info6.setText("Wait...");
            result_info7.setText("Wait...");
            result_info8.setText("Wait...");
            result_info9.setText("Wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();


                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                result_info.setText("Temperature: " + jsonObject.getJSONObject("main").getDouble("temp") + "C");
                result_info1.setText("Feels like: " + jsonObject.getJSONObject("main").getDouble("feels_like") + "C");
                result_info2.setText("Pressure: " + jsonObject.getJSONObject("main").getDouble("pressure") + "hPa");
                result_info3.setText("Weather: " + jsonObject.getJSONArray("weather").getJSONObject(0).get("main"));
                result_info4.setText("Description: " + jsonObject.getJSONArray("weather").getJSONObject(0).get("description"));
                result_info5.setText("Temp min: " + jsonObject.getJSONObject("main").getDouble("temp_min") + "C");
                result_info6.setText("Temp max: " + jsonObject.getJSONObject("main").getDouble("temp_max") + "C");
                result_info7.setText("Humidity: " + jsonObject.getJSONObject("main").getInt("humidity") + "%");
                result_info8.setText("Wind speed: " + jsonObject.getJSONObject("wind").getInt("speed") + "m/s");
                result_info9.setText("Country: " + jsonObject.getJSONObject("sys").getString("country"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}


