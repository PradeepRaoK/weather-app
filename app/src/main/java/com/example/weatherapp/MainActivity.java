package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText etCity;
    TextView tvResult;
    ProgressBar pb;
    public static final String MyPrefs = "MyPrefs";
    public static final String City="City";
    public static final String Country="Country";
    public static final String Temperature="Temperature";
    public static final String FeelsLike="FeelsLike";
    public static final String Humidity="Humidity";
    public static final String Description="Description";
    public static final String Wind="Wind";
    public static final String Clouds="Clouds";
    public static final String Pressure="Pressure";


    SharedPreferences sp;
    SharedPreferences.Editor er;
    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "8fcc0b1f57ab3f731361b541649a3c3c";

    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity=findViewById(R.id.etCity);
        pb=findViewById(R.id.progressBar);
        tvResult=findViewById(R.id.tvResult);
        sp=getSharedPreferences(MyPrefs,MODE_PRIVATE);
        etCity.setText(sp.getString(City,null));
        if(sp.getString(City,null)!=null){
            String output="Current weather of "+sp.getString(City,null)+"("+sp.getString(Country,null)+")"+"\n Temp: "+sp.getString(Temperature,null)+" °C"+"\n Feels Like: "+sp.getString(FeelsLike,null)+ " °C"+"\n Humidity: "+sp.getString(Humidity,null)+"%"+"\n Description: "+sp.getString(Description,null)+"\n Wind Speed: "+sp.getString(Wind,null)+"m/s (meters per second)"+"\n Cloudiness: "+sp.getString(Clouds,null)+"%"+"\n Pressure: "+sp.getString(Pressure,null)+" hPa";
            tvResult.setText(output);
        }
        pb.setVisibility(View.GONE);
    }

    public void getWeatherDetails(View view) {
        String tempUrl="";
        String city=etCity.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("City field can not be empty!");
        }else{
            tempUrl=url+"?q="+city+"&appid="+appid;
            pb.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pb.setVisibility(View.GONE);
//                    Log.d("response",response);
                    String output="";
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp")-273.15;
                        double feelsLike = jsonObjectMain.getInt("feels_like")-273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        tvResult.setTextColor(Color.WHITE);
                        output+="Current weather of "+cityName+"("+countryName+")"+"\n Temp: "+df.format(temp)+" °C"+"\n Feels Like: "+df.format(feelsLike)+ " °C"+"\n Humidity: "+humidity+"%"+"\n Description: "+description+"\n Wind Speed: "+wind+"m/s (meters per second)"+"\n Cloudiness: "+clouds+"%"+"\n Pressure: "+pressure+" hPa";
                        tvResult.setText(output);
                        er=sp.edit();
                        er.putString(City,city);
                        er.putString(Country,countryName);
                        er.putString(Temperature,df.format(temp));
                        er.putString(FeelsLike,df.format(feelsLike));
                        er.putString(Humidity, String.valueOf(humidity));
                        er.putString(Description,description);
                        er.putString(Wind,wind);
                        er.putString(Clouds,clouds);
                        er.putString(Pressure, String.valueOf(pressure));
                        er.commit();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}