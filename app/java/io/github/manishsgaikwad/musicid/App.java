package io.github.manishsgaikwad.musicid;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by manish on 31-05-2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String TAG = "FFmpeg..";
                FFmpeg ffmpeg = FFmpeg.getInstance(context);

                try{
                    ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                        @Override
                        public void onFailure() {
                            Log.i(TAG,"Failure");
                        }

                        @Override
                        public void onSuccess() {
                            Log.i(TAG,"Success");
                        }

                        @Override
                        public void onStart() {
                            Log.i(TAG,"Start");
                        }

                        @Override
                        public void onFinish() {
                            Log.i(TAG,"Finish");
                        }
                    });
                }
                catch (FFmpegNotSupportedException e){
                    Log.i(TAG,e.getMessage());
                }
            }
        }).start();
        //Loading FFmpeg Binary



    }
}
