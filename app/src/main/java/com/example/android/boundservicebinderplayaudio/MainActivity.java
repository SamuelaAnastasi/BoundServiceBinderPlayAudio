package com.example.android.boundservicebinderplayaudio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static LocalBinder binder = null;
    private Handler audioProgressUpdateHandler = null;
    boolean isBound = false;
    
    static ProgressBar progressBar;

    // This service connection object is the bridge between activity and bound service.
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (LocalBinder) iBinder;
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
            binder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        progressBar = findViewById(R.id.progressBar);
        bindPlayAudioService();
    }

    // Helper methods to bind/unbind service ---------------------------------------------
    private void bindPlayAudioService() {
        if(binder == null) {
            Intent intent = new Intent(this, PlayAudioService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    private void unbindPlayAudioService() {
        if(binder != null) {
            unbindService(connection);
            isBound = false;
        }
    }
    // End of helper methods to bind/unbind service ------------------------------------------

    @Override
    protected void onDestroy() {
        unbindPlayAudioService();
        super.onDestroy();
    }

    // onClick methods for play, pause, stop buttons ----------------------------------
    public void playMedia(View view) {
        String mediaUrl = "https://www.dev2qa.com/demo/media/test.mp3";
        binder.setAudioWebUrl(mediaUrl);
        binder.setIsStream(true);

        // Initialize audio progress bar updater Handler object.
        createAudioProgressbarUpdater();
        binder.setAudioProgressUpdateHandler(audioProgressUpdateHandler);

        binder.startAudio();
//        progressBar.setVisibility(ProgressBar.VISIBLE);
        Toast.makeText(getApplicationContext(),
                "Start play web audio file.", Toast.LENGTH_SHORT).show();
    }

    public void pauseMedia(View view) {
        binder.pauseAudio();
        Toast.makeText(getApplicationContext(),
                "Web audio file is paused.", Toast.LENGTH_SHORT).show();
    }

    public void stopMedia(View view) {
        binder.stopAudio();
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
        Toast.makeText(getApplicationContext(),
                "Stop play web audio file.", Toast.LENGTH_SHORT).show();
    }

    // End of onClick methods for play, pause, stop buttons -----------------------------

    // Helper method to receive media progress updates from service
    private void createAudioProgressbarUpdater() {
        if (audioProgressUpdateHandler == null) {
            audioProgressUpdateHandler = new ProgressHandler();
        }
    }

    // Static class for Handler to avoid memory leaks
    private static class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // The update process message is sent from LocalBinder class's thread object.
            if (binder != null) {
                    if (msg.what == binder.UPDATE_AUDIO_PROGRESS_BAR) {
                        // Calculate the percentage of progress.
                        int currProgress = binder.getMediaProgress();
                        // Update progressbar. Make the value 10 times to show more clear UI change.
                        progressBar.setProgress(currProgress);
                }
            }
        }
    }
}
