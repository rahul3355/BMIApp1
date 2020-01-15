package com.ex2m75e.rahul.bmiapp1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class CalculationActivity extends AppCompatActivity {

    TextView tvName,tvHeight, tvFeet, tvInch;
    Spinner spnFeet, spnInch;
    EditText etWeight;
    Button btnCalculate, btnHistory;
    SharedPreferences sp;
    DatabaseHelper myDb;
    LocationManager locationManager;
    LocationListener locationListener;
    String city;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        myDb = new DatabaseHelper(this);
        tvName = findViewById(R.id.tvName);
        tvFeet = findViewById(R.id.tvFeet);
        tvInch = findViewById(R.id.tvInch);
        tvHeight = findViewById(R.id.tvHeight);
        spnFeet = findViewById(R.id.spnFeet);
        spnInch = findViewById(R.id.spnInch);
        etWeight = findViewById(R.id.etWeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnHistory = findViewById(R.id.btnHistory);

        sp = getSharedPreferences("f1",MODE_PRIVATE);

        String name = sp.getString("name","");
        int age = sp.getInt("age",-1);
        long phone = sp.getLong("phone",-1);
        String gender = sp.getString("gender", "");
        String bmi = sp.getString("bmi","");

        tvName.setText("Welcome, "+name);

        Integer[] feet = new Integer[]{1,2,3,4,5,6,7,8,9};
        ArrayAdapter<Integer> feetAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, feet);
        spnFeet.setAdapter(feetAdapter);

        Integer[] inch = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11};
        ArrayAdapter<Integer> inchAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, inch);
        spnInch.setAdapter(inchAdapter);

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
                        Geocoder geocoder = new Geocoder(CalculationActivity.this, Locale.getDefault());
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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0,locationListener);
        }

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = etWeight.getText().toString();
                if(s1.length()==0)
                {
                    etWeight.setError("cannot be empty");
                    etWeight.requestFocus();
                    return;
                }
                int weight = Integer.parseInt(s1);
                if(weight <= 0 )
                {
                    etWeight.setError("Invalid weight");
                    etWeight.requestFocus();
                    return;
                }

                String s2 = spnFeet.getSelectedItem().toString();
                int feet = Integer.parseInt(s2);

                String s3 = spnInch.getSelectedItem().toString();
                int inch = Integer.parseInt(s3);

                int inch1 = feet * 12;
                int total_inch = inch1 + inch;
                double height_ms = total_inch * 0.0254;


                double bmi2 = weight / (height_ms * height_ms);
                String bmi = String.format("%.02f",bmi2);

               // Toast.makeText(CalculationActivity.this, "BMI : "+bmi, Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("bmi",bmi);
                editor.commit();

                Intent i = new Intent(CalculationActivity.this, ResultActivity.class);
                startActivity(i);




            }
        });

        viewAll();


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

    public void viewAll(){
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = myDb.getAllData();
                if(res.getCount()==0){
                    //show message
                    showMessage("Error","No data Found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append(""+ res.getString(0)+")"+" BMI: "+res.getString(3)+ " on "+res.getString(2) + "\n\n");

                }
                showMessage("Data",buffer.toString());
            }
        });
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!= null && activeNetworkInfo.isConnected();
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
                CalculationActivity.MeraTask t1 = new CalculationActivity.MeraTask();
                String w1 = "http://api.openweathermap.org/data/2.5/weather?units=metric";
                String w2 = "&q=" + loc1;
                String w3 = "&appid=c6e315d09197cec231495138183954bd";
                String w = w1 + w2 + w3;

                t1.execute(w);
            }
            else{
                Toast.makeText(this, "Check network", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CalculationActivity.this, "fetching data...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(CalculationActivity.this, "temp "+temp1+" "+"Location: "+city, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CalculationActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
