package com.example.musicplayerapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int MOVE_STEP = 15000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean stopped;
    private SeekBar seekBar;

    private TextView songTitle;
    private TextView timeLeftText;
    private MediaPlayer mediaPlayer;
    private final Runnable updateSeekBarTask = () -> {
        while (!stopped) {
            int current = mediaPlayer.getCurrentPosition();
            int max = mediaPlayer.getDuration();
            seekBar.setProgress(current);
            timeLeftText.setText(((max - current)/1000) + " sec");
            sleep();
        }
    };

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();

        setupSeekBar();
        stopped = true;
    }

    private void setupViews() {
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(
                this,
                R.raw.audiotrack01
        );

        seekBar = findViewById(R.id.seekBar);
        songTitle = findViewById(R.id.songTitleTextView);
        songTitle.setText("audiotrack01");

        timeLeftText = findViewById(R.id.timeLeftTextView);

        findViewById(R.id.playButton)
                .setOnClickListener(v -> play());

        findViewById(R.id.pauseButton)
                .setOnClickListener(v -> pause());

        findViewById(R.id.fastForwardButton)
                .setOnClickListener(v -> fastForward());

        findViewById(R.id.fastRewindButton)
                .setOnClickListener(v -> fastRewind());
    }

    private void fastRewind() {
        int current = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(current - MOVE_STEP);
    }

    private void fastForward() {
        int current = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(current + MOVE_STEP);
    }

    private void pause() {
        mediaPlayer.pause();
        stopped = true;
    }

    private void setupSeekBar() {
        int max = mediaPlayer.getDuration();
        seekBar.setMax(max);

        if (stopped) {
            executor.submit(updateSeekBarTask);
        }
    }

    private void play() {
        mediaPlayer.start();
        setupSeekBar();
        stopped = false;
    }
}