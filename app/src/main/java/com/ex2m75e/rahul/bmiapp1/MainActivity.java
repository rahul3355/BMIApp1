package com.ex2m75e.rahul.bmiapp1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.Range;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    TextView tvDetails;
    EditText etName, etAge, etPhone;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale;
    Button btnRegister;
    SharedPreferences sp;
    LocationManager locationManager;
    LocationListener locationListener;
    String city;

    //AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        tvDetails = findViewById(R.id.tvDetails);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnRegister = findViewById(R.id.btnRegister);
        //awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        /*//validations
        awesomeValidation.addValidation(this, R.id.etName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etPhone, "^[2-9]{2}[0-9]{8}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etAge, Range.closed(1, 130), R.string.ageerror);
        */
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
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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


        sp = getSharedPreferences("f1", MODE_PRIVATE);
        String name = sp.getString("name","");
        int age = sp.getInt("age", -1);
        long phone = sp.getLong("phone", -1);
        String gender = sp.getString("gender","");
        String bmi = sp.getString("bmi","");

        /* awesomeValidation.addValidation(this, R.id.etName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etPhone, "^[2-9]{2}[0-9]{8}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.etAge, Range.closed(1, 130), R.string.ageerror); */

        if(name.length()==0)
        {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = etName.getText().toString();
                    if(name.length()==0)
                    {
                        etName.setError("name cannot be empty");
                        etName.requestFocus();
                        return;
                    }
                    if(!((name.matches("[a-zA-Z]+"))&&(name.length()>=2)))
                    {
                        etName.setError("Enter valid name");
                        etName.requestFocus();
                        return;
                    }


                    String s1 = etAge.getText().toString();
                    if(s1.length()==0)
                    {
                        etAge.setError("age cannot be empty");
                        etAge.requestFocus();
                        return;
                    }

                    int age = Integer.parseInt(s1);
                    String s2 = etPhone.getText().toString();
                    if(s2.length()==0)
                    {
                        etPhone.setError("phone cannot be empty");
                        etPhone.requestFocus();
                        return;
                    }
                    long phone = Long.parseLong(s2);
                    long phone1 = phone;

                    if(phone <= 0)
                    {
                        etPhone.setError("invalid phone number");
                        etPhone.requestFocus();
                        return;
                    }

                    int count = 0;

                    while(phone1>0)
                    {
                        phone1 = phone1/10;
                        count = count +1;
                    }

                    if(count!=10)
                    {
                        etPhone.setError("enter 10 digits");
                        etPhone.requestFocus();
                        return;
                    }

                    int id = rgGender.getCheckedRadioButtonId();
                    RadioButton rab = findViewById(id);
                    String gender = rab.getText().toString();
                    String bmi = null;


                    if(age<=0)
                    {
                        etAge.setError("enter valid age");
                        etAge.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(etAge.getText().toString()))
                    {
                        etAge.setError("cannot be empty");
                        etAge.requestFocus();
                        return;
                    }




                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name",name);
                    editor.putInt("age",age);
                    editor.putLong("phone",phone);
                    editor.putString("gender",gender);
                    editor.putString("bmi",bmi);
                    editor.commit();


                    Intent i = new Intent(MainActivity.this, CalculationActivity.class);
                    startActivity(i);
                    finish();


                }
            });

        }
        else
        {

            /*if(awesomeValidation.validate())
            {
                Toast.makeText(this, "Validation Successful and Registration Done", Toast.LENGTH_SHORT).show();
            } */
            Intent i = new Intent(MainActivity.this,CalculationActivity.class);
            startActivity(i);
            finish();
        }







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

//            try{
//                LocClass lc = new LocClass();
//                Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
//                List<Address> addresses = geo.getFromLocation(lc.latitude, lc.longitude, 1);
//                if(addresses.isEmpty()){
//                    Toast.makeText(this, "Unable to connect to network", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    StringBuilder sb1 = new StringBuilder();
//                    sb1.append(addresses.get(0).getLocality());
//                    String locResult = sb1.toString();
//                }
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
            if(isNetworkAvailable()) {
                String loc1 = city;
                MeraTask t1 = new MeraTask();
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
//
//    public class LocClass implements LocationListener{
//        double latitude, longitude;
//
//        public void onLocationChanged(Location location){
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    }

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
                Toast.makeText(MainActivity.this, "fetching data...", Toast.LENGTH_SHORT).show();
            }
            else{
            Toast.makeText(MainActivity.this, "temp "+temp1+" Location:"+city, Toast.LENGTH_SHORT).show();
        }}
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
