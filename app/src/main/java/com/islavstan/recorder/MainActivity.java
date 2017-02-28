package com.islavstan.recorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    SeekBar seekBar;
    private int duration = 0;
    private boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileName = Environment.getExternalStorageDirectory() + "/record.3gpp";
        seekBar = (SeekBar) findViewById(R.id.seek);

    }

    public void recordStart(View v) {
        try {
            releaseRecorder();

            File outFile = new File(fileName);
            if (outFile.exists()) {
                outFile.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recordStop(View v) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    public void playStart(View v) {

        try {
            releasePlayer();

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setDataSource(fileName);

            mediaPlayer.prepare();
            mediaPlayer.start();


            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mediaPlayer.getDuration();
                    seekBar.setMax(duration);
                    seekBar.post(onEverySecond);

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (running) {
                if (seekBar != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }

                if (mediaPlayer.isPlaying()) {
                    seekBar.postDelayed(onEverySecond, 10);

                }
            }
        }
    };


    public void playStop(View v) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }

}