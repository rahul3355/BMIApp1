package com.ex2m75e.rahul.bmiapp1;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView imgbglogo; //imgbgtext;
    Animation fromtop;
    TextView tvBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgbglogo = findViewById(R.id.imgbglogo);
        tvBMI = findViewById(R.id.tvBMI);
       // imgbgtext = findViewById(R.id.imgbgtext);

        //frombottom = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.frombottom);
       // imgbgtext.setAnimation(frombottom);

        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        imgbglogo.startAnimation(fromtop);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).start(); */

        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread()
        {
            public void run()
            {
                try{
                    sleep(4000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }

        };
        timer.start();



    }
}
