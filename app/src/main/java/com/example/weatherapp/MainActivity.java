package com.example.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import pl.droidsonroids.gif.GifImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener {
private TextView city;
private LocationManager locationManager;
private double longitude;
private double latitude;
private TextView area,updated_atTxt,degree,weather;
    private TextView time2;
    private TextView textView3;
    private ConstraintLayout myLayout;


    private final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Calendar calender= Calendar.getInstance();
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        String time = simpleDateFormat.format(calander.getTime());
        String temp = time.substring(0, 2);
        int i = Integer.parseInt(temp);


        String currentDate= DateFormat.getDateInstance(DateFormat.FULL).format(calender.getTime());
        TextView day=findViewById(R.id.day);
         weather=(TextView)findViewById(R.id.weather);
         degree=(TextView)findViewById(R.id.degree);

        day.setText(currentDate);

        city =(TextView) findViewById(R.id.city);
        area =(TextView) findViewById(R.id.area);

        pl.droidsonroids.gif.GifImageView gf = (GifImageView) findViewById(R.id.abcd);
        updated_atTxt = findViewById(R.id.updated_at);
        myLayout = findViewById(R.id.mainlayout);
        if(i >= 19 || i <= 5){
            gf.setVisibility(View.VISIBLE);
            weather.setTextColor(Integer.parseInt("ffffff", 16)+0xFF000000);
            area.setTextColor(Integer.parseInt("ffffff", 16)+0xFF000000);
            degree.setTextColor(Integer.parseInt("ffffff", 16)+0xFF000000);
            updated_atTxt.setTextColor(Integer.parseInt("ffffff", 16)+0xFF000000);
        } else{
            gf.setVisibility(View.INVISIBLE);
            weather.setTextColor(Integer.parseInt("000000", 16)+0xFF000000);
            area.setTextColor(Integer.parseInt("000000", 16)+0xFF000000);
            degree.setTextColor(Integer.parseInt("000000", 16)+0xFF000000);
            updated_atTxt.setTextColor(Integer.parseInt("000000", 16)+0xFF000000);
        }
        //else gf.setImageResource(R.drawable.night);



        //try {
            //JSONObject jsonObj = new JSONObject("");
            //Long updatedAt = jsonObj.getLong("dt");
            //String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
            //updated_atTxt.setText(updatedAtText);
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}


        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        showExplanation("Warning", "ask for permission", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            return;

        }

        Location location=locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Log.d("Checking","city");
        loc_func(location);






    }

    @Override
    public void onLocationChanged(Location location){
        longitude=location.getLongitude();
        latitude=location.getLatitude();
    }
    @Override
    public void onStatusChanged(String s,int i,Bundle bundle){

    }
    @Override
    public void onProviderEnabled(String s){

    }
    @Override
    public void onProviderDisabled(String s){

    }

    private void loc_func(Location location){
        try {
            Geocoder geocoder=new Geocoder(this);
            List<Address> addresses=null;
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            String city=addresses.get(0).getLocality();
            String area = addresses.get(0).getSubLocality();
            Log.d("Checking",city);
            this.city.setText(city);
            this.area.setText(area);
            find_weather(location);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error:"+e,Toast.LENGTH_SHORT).show();
        }
    }
    public void find_weather(Location location){
        String url="https://api.openweathermap.org/data/2.5/weather?lat=latitude&lon=longitude&appid=07d1276b5795099b69cd970a6e80a5f9&units=metric";

        JsonObjectRequest jor=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main_obj=response.getJSONObject("main");
                    JSONArray array= response.getJSONArray("weather");
                    JSONObject object=array.getJSONObject(0);
                    String temperature=String.valueOf(main_obj.getDouble("temp"));
                    String description= object.getString("description");
                    String city=response.getString("name");

                    degree.setText(temperature);
                    weather.setText(description);




                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            public void onErrorResponse(VolleyError error){

            }
        }
        );
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(jor);
    }
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

}
