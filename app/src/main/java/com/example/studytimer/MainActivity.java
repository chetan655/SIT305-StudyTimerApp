package com.example.studytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView userInfoText, timerText;
    ImageButton pauseButton, playButton, stopButton;
    EditText taskNameText;

    Boolean isRunning = false;
    Boolean isPaused = false;

    Timer timer;
    TimerTask timertask;
    Double time = 0.0;

    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF = "my_pref";
    private static String TIME;
    private static String IS_RUNNING;
    private static String IS_PAUSED;
    private static String USER_INFO_TEXT;
    private static String TASK_NAME;
    String infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInfoText = findViewById(R.id.studyHourText);
        timerText = findViewById(R.id.timerTextView);
        pauseButton = findViewById(R.id.pauseButton);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);
        taskNameText = findViewById(R.id.taskNameText);

        timer = new Timer();
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        sharedPreferencesChecker();

        if (savedInstanceState!=null){
            time=savedInstanceState.getDouble(TIME);
            isRunning=savedInstanceState.getBoolean(IS_RUNNING);
            isPaused = savedInstanceState.getBoolean(IS_PAUSED);
            userInfoText.setText(savedInstanceState.getString(USER_INFO_TEXT));
            taskNameText.setText(savedInstanceState.getString(TASK_NAME));

            if(isRunning) {
                startTimer();
            }
            else if((timertask==null)&&(isPaused)){
                timerText.setText(getTime());
            }
        }

        playButton.setOnClickListener(view1 -> {
            if (taskNameText.getText().toString().isEmpty()){
                Toast.makeText(MainActivity.this, "Please Enter Task Name", Toast.LENGTH_SHORT).show();
            }
            else {
                isRunning = true;
                isPaused = false;
                startTimer();
                Toast.makeText(MainActivity.this, "Timer Started", Toast.LENGTH_SHORT).show();
            }
        });
        pauseButton.setOnClickListener(view2 -> {
            pauseTimer();
            Toast.makeText(MainActivity.this, "Timer Paused", Toast.LENGTH_SHORT).show();
        });
        stopButton.setOnClickListener(view3 -> {
            stopTimer();
            Toast.makeText(MainActivity.this, "Timer Stopped", Toast.LENGTH_SHORT).show();
        });
    }

    private void startTimer(){
        //isRunning = true;
        timertask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    time++;
                    timerText.setText(getTime());
                });
            }
        };timer.scheduleAtFixedRate(timertask, 0, 1000);
    }

    private void pauseTimer(){
        if (isRunning){
            timertask.cancel();
            isRunning = false;
            isPaused = true;
        }
    }

    private void stopTimer(){
        if(timertask!=null){
            timertask.cancel();

            if(TextUtils.isEmpty(userInfoText.getText().toString())){
                sharedPreferences.edit().putString(infoText,"You spent 00:00 on ... last time.").apply();
            }
            else{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(infoText,String.format("You spent %s on %s last time.",getTime(),taskNameText.getText().toString()));
                editor.apply();
            }

            isRunning = false;
            isPaused = false;
            time = 00.00;
            timerText.setText(String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
        }
    }

    private String getTime() {
        int roundOff = (int) Math.round(time);

        int hours = ((roundOff % 86400) / 3600);
        int minutes = ((roundOff % 86400) % 3600) / 60;
        int seconds = ((roundOff % 86400) % 3600) % 60;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    // private String formattedString(int hours, int minutes, int seconds){ return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds); }

    private void sharedPreferencesChecker() {
        if(sharedPreferences != null){
            String text = sharedPreferences.getString(infoText,"You spent 00:00 on ... last time.");
            userInfoText.setText(text);}
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(TIME,time);
        outState.putBoolean(IS_RUNNING,isRunning);
        outState.putBoolean(IS_PAUSED,isPaused);
        outState.putString(USER_INFO_TEXT,userInfoText.getText().toString());
        if(!TextUtils.isEmpty(taskNameText.getText().toString())){
            outState.putString(TASK_NAME,taskNameText.getText().toString());
        }
    }

}