package com.example.minwoo.airound;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends AppCompatActivity {

    Handler h;
    ImageView logo;
    Animation riseup_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        logo = (ImageView)findViewById(R.id.logo);
        riseup_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.riseup);
        logo.startAnimation(riseup_animation);
        logo.setVisibility(View.INVISIBLE);

        h=new Handler();
        h.postDelayed(irun,1700);
    }

    Runnable irun=new Runnable(){
        public void run(){

            Intent i=new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        }
    };

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        h.removeCallbacks(irun);
    }
}
