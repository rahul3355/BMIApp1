package com.ex2m75e.rahul.bmiapp1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    TextView tvBMI, tvResult, tvUnderweight,tvNormalweight,tvOverweight,tvObesity;
    Button btnShare, btnSave, btnBack;
    SharedPreferences sp;
    DatabaseHelper myDb;
    LocationManager locationManager;
    LocationListener locationListener;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        myDb = new DatabaseHelper(this);
        tvBMI = findViewById(R.id.tvBMI);
        tvResult = findViewById(R.id.tvResult);
        tvUnderweight = findViewById(R.id.tvUnderweight);
        tvNormalweight = findViewById(R.id.tvNormalweight);
        tvOverweight = findViewById(R.id.tvOverweight);
        tvObesity = findViewById(R.id.tvObesity);
        btnShare = findViewById(R.id.btnShare);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean isGPS_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPS_enabled){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    try{
                        Geocoder geocoder = new Geocoder(ResultActivity.this, Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);

                        city = addressList.get(0).getLocality();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
        else{
            Toast.makeText(this, "Check GPS", Toast.LENGTH_SHORT).show();
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,locationListener);
        }


        sp = getSharedPreferences("f1",MODE_PRIVATE);

        final String name = sp.getString("name","");
        final int age = sp.getInt("age",-1);
        final long phone = sp.getLong("phone",-1);
        final String gender = sp.getString("gender", "");
        final String bmi = sp.getString("bmi","");

        Toast.makeText(this, "BMI is "+bmi, Toast.LENGTH_SHORT).show();

        tvBMI.setText("Your BMI is "+bmi);
        float bmi1 = Float.parseFloat(bmi);

        if(bmi1 < 18.5) {
            tvResult.setText("You are Underweight");
            tvUnderweight.setTextColor(Color.RED);
        }
        if(bmi1 >=18.5 && bmi1<=24.99) {
            tvResult.setText("You are Normal");
            tvNormalweight.setTextColor(Color.RED);
        }
        if(bmi1 >=25 && bmi1 <= 29.99) {
            tvResult.setText("You are Overweight");
            tvOverweight.setTextColor(Color.RED);
        }
        if(bmi1 > 30) {
            tvResult.setText("You are Obese");
            tvObesity.setTextColor(Color.RED);
        }


        tvUnderweight.setText("BMI below 18.5 is Underweight");
        tvNormalweight.setText("BMI between 18.5 and 24.9 is Normal weight");
        tvOverweight.setText("BMI between 25 and 29.9 is Overweight");
        tvObesity.setText("BMI above 30 is Obesity");

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "Name: "+name +"\nAge: "+age +"\nPhone: "+phone +"\nSex: "+gender +"\nBMI is "+bmi );
                startActivity(i);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(ResultActivity.this, CalculationActivity.class);
                startActivity(i1);
            }
        });

        AddData();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,locationListener);
                Toast.makeText(this, "getting location", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Access not granted", Toast.LENGTH_SHORT).show();
        }
    }



    public void AddData(){
        final String bmi = sp.getString("bmi","");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy G 'at' HH.mm.ss z");
        final String dnt = sdf.format(new Date());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInserted = myDb.insertData(null,dnt,bmi);
                if(isInserted==true)
                    Toast.makeText(ResultActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ResultActivity.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!= null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.website){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://" + "www.medicalnewstoday.com"));
            startActivity(i);
        }
        if(item.getItemId()==R.id.about){
            Toast.makeText(this, "Developer: Rahul", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.temp){
            if(isNetworkAvailable()) {
                String loc1 = city;
                ResultActivity.MeraTask t1 = new ResultActivity.MeraTask();
                String w1 = "http://api.openweathermap.org/data/2.5/weather?units=metric";
                String w2 = "&q=" + loc1;
                String w3 = "&appid=c6e315d09197cec231495138183954bd";
                String w = w1 + w2 + w3;

                t1.execute(w);
            }
            else{
                Toast.makeText(this, "Check Network", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
    class MeraTask extends AsyncTask<String, Void, Double> {
        double temp1;

        @Override
        protected Double doInBackground(String... strings) {
            String json = "", line = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();

                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                while ((line = br.readLine()) != null) {
                    json = json + line + "\n";
                }
                JSONObject o = new JSONObject(json);
                JSONObject p = o.getJSONObject("main");
                temp1 = p.getDouble("temp");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return temp1;
        }
        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            //tvResult.setText(" Temp " + aDouble);
            if(city == null || temp1 == 0.0){
                Toast.makeText(ResultActivity.this, "fetching data...", Toast.LENGTH_SHORT).show();
            }
            else{
            Toast.makeText(ResultActivity.this, "temp "+temp1+" Location: "+city, Toast.LENGTH_SHORT).show();
        }}
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ResultActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
