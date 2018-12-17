package com.example.poweruser.myapplication1217;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btStart, btStop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Intent intent = getIntent();

        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);

        btStart.setOnClickListener(this);
        btStop.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == btStart) {
            startService(new Intent(this, MyService.class));
        }else if (view ==btStop){
            stopService(new Intent(this, MyService.class));
        }

    }
}

