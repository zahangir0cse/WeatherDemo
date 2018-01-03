package mobileapps.bitm.com.weatherdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mobileapps.bitm.com.weatherdemo.model.UrlParameter;
import mobileapps.bitm.com.weatherdemo.model.WeatherResponse;
import mobileapps.bitm.com.weatherdemo.service.CurrentWeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private CurrentWeatherService service;
    private UrlParameter urlParameter;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    private Geocoder geocoder;
    private List<Address> addresses = new ArrayList<>();
    private String location;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

        urlParameter = new UrlParameter();
        urlParameter.setDataType("weather");
        urlParameter.setUnit("metric");

        geocoder = new Geocoder(this);
        client = LocationServices.getFusedLocationProviderClient(this);
        request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        location = null;

        if (location== null){
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        urlParameter.setLat(latitude);
                        urlParameter.setLon(longitude);
                    }
                }
            };
        }else {
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                        try {
                            addresses = geocoder.getFromLocationName(location,1);
                            if (addresses.get(0).equals(null)){
                                Toast.makeText(getApplicationContext(), "Please give correct address", Toast.LENGTH_LONG).show();
                            }else {
                                urlParameter.setLon(addresses.get(0).getLongitude());
                                urlParameter.setLat(addresses.get(0).getLatitude());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            };
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0);
            return;
        }
        client.requestLocationUpdates(request, callback, null);

        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //task.getResult().getLongitude();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CurrentWeatherService.class);

        String url = String.format("%s?lat=%f&lon=%f&units=%s&appid=%s", urlParameter.getDataType(), urlParameter.getLat(), urlParameter.getLon(), urlParameter.getUnit(), "0d383ae00da294a620afc0559c64365c");

        Call<WeatherResponse> weatherResponseCall = service.getWeatherResponse(url);

        weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    textView.setText(weatherResponse.getMain().getTemp().toString());
                    String iconString = weatherResponse.getWeather().get(0).getIcon();
                    Uri iconUri = Uri.parse("http://openweathermap.org/img/w/" + iconString + ".png");
                    Picasso.with(MainActivity.this)
                            .load(iconUri)
                            .into(((ImageView) findViewById(R.id.image)));

                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("current", "onFailure: " + t.getMessage());
            }

        });
    }
}
