package com.example.android.boundservicebinderplayaudio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayAudioService extends Service {

    private IBinder localBinder = new LocalBinder();

    public PlayAudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }
}
