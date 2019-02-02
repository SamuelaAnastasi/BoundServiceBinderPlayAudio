package com.example.android.boundservicebinderplayaudio;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.IOException;

public class LocalBinder extends Binder {

    // Audio web url
    private String audioWebUrl = "";

    // check if streamAudio
    private boolean isStream = false;

    // MediaPlayer reference
    private MediaPlayer mediaPlayer = null;

    // This Handler object is a reference to the caller activity's Handler.
    // In the caller activity's handler, it will update the audio play progress.
    private Handler audioProgressUpdateHandler;

    // This is the message signal that inform audio progress updater to update audio progress.
    final int UPDATE_AUDIO_PROGRESS_BAR = 1;

    // Getters and setters -----------------------------------------------------------

    private String getAudioWebUrl() {
        return audioWebUrl;
    }

    void setAudioWebUrl(String audioWebUrl) {
        this.audioWebUrl = audioWebUrl;
    }

    private boolean isStream() {
        return isStream;
    }

    void setIsStream(boolean isStream) {
        this.isStream = isStream;
    }

    public Handler getAudioProgressUpdateHandler() {
        return audioProgressUpdateHandler;
    }

    void setAudioProgressUpdateHandler(Handler audioProgressUpdateHandler) {
        this.audioProgressUpdateHandler = audioProgressUpdateHandler;
    }

    // End of getters and setters ----------------------------------------------------

    // Audio start, pause stop methods -----------------------------------------------
    void startAudio() {
        // initialize MediaPlayer
        initMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    void pauseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            destroyMediaPlayer();
        }
    }

    // End of audio start, pause stop methods ------------------------------------------

    // Helper method to initialize the MediaPlayer
    private void initMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                if (isStream()) {

                    if (android.os.Build.VERSION.SDK_INT >= 21) {
                        AudioAttributes.Builder b = new AudioAttributes.Builder();
                        b.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                    } else {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    }
                }
                if (!TextUtils.isEmpty(getAudioWebUrl())) {
                    mediaPlayer.setDataSource(getAudioWebUrl());
                }
                mediaPlayer.prepare();

                // This thread object will send update audio progress message
                // to caller activity every 1 second.
                Thread updateAudioProgressThread = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            // Create audio progress message
                            Message audioProgressMessage = new Message();
                            audioProgressMessage.what = UPDATE_AUDIO_PROGRESS_BAR;
                            // Send the message to caller activity's update audio progressbar
                            // Handler object.
                            audioProgressUpdateHandler.sendMessage(audioProgressMessage);

                            // Sleep 1 second
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                // Run above thread object.
                updateAudioProgressThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Destroy MediaPlayer
    private void destroyMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Get current play position
    private int getCurrentMediaPosition() {
        int position = 0;
        if (mediaPlayer != null) {
            position = mediaPlayer.getCurrentPosition();
        }
        return position;
    }

    // Get current media player progress value
    int getMediaProgress() {
        int currentPosition = getCurrentMediaPosition();
        int mediaDuration = 169 * 1000;
        return (currentPosition * 100) / mediaDuration;
    }
}
