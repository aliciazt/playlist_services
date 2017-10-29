package com.outlook.gonzasosa.code.firebaseanonymousauth;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Alicia Zarate on 26/10/2017.
 */

public class my_services extends Service implements MediaPlayer.OnPreparedListener{
    @Nullable

    MediaPlayer mediaplayer = new MediaPlayer();

    public static String ante=null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();
    }

    //@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ante!=null){
         onDestroy();
        }

        try {
            ante= TracksAdapter.canci;
            mediaplayer.setDataSource("/storage/6FC2-7617/musica/"+TracksAdapter.canci+".mp3");
            mediaplayer.setOnPreparedListener(this);
            mediaplayer.prepare();


        } catch (Exception e) {
        }

        return START_NOT_STICKY;}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaplayer.stop();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaplayer.start();
    }
}
