package com.example.retrofitpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button buttonFetch;
    TextView textView;
    Context context;
    WebService webService;
    private FusedLocationProviderClient fusedLocationClient;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        buttonFetch = findViewById(R.id.buttonFetch);
        webService = Utils.getAPI(context);
        textView = findViewById(R.id.textViewRetro);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat=location.getLatitude();
        double lng=location.getLongitude();
        city=getCityName(lat,lng);
        Log.d("CityName",city);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retroCallForGetData();
            }
        });
    }
    private void retroCallForGetData() {
        try {
            //if below function is connected to internet it will return true and Show dialog will be called
            if(Utils.isConnectedToInternet(context, false)){
                Utils.ShowDialog(this);
                //Pass the Query

                System.out.println(city);
                Call<WeatherModel> outputCall = webService.products("ee21fc1d0b804ca4ab7203645222212",city,"yes");
                outputCall.enqueue(new Callback<WeatherModel>() {
                    @Override
                    public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                        //when we get response from website we called and it's body is not null/empty then it will hide the loading dialog
                        if(response.isSuccessful() && response.body() != null){
                            Utils.hideDialog();
                            WeatherModel response1 = response.body();
                            textView.setText("Retrofit \n"+"Temperature: "+response1.getCurrent().getTempC()+"\n"
                                            +"Humidity: "+response1.getCurrent().getHumidity()+"\n"
                                            +"Feels Like : "+response1.getCurrent().getFeelslike_c()+"\n"
                                            +"Region: "+response1.getLocation().getRegion()+"\n"
                                            +"Weather: "+response1.getCurrent().getCondition().getText());
                        }
                    }

                    @Override
                    //when failed to get response from website or some error occurred
                    public void onFailure(Call<WeatherModel> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        Utils.hideDialog();
                    }

                });
            }
        } catch (Exception e) {
            Utils.hideDialog();
        }
    }
    private String getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            String cityName = addresses.get(0).getAdminArea();//this returns State of current Lat, Lng
            return cityName;
        }

        return "";
    }

}