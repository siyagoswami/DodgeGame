package com.example.dodgegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    GameSurface gameSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
    }

    protected void onPause()
    {
        super.onPause();
        gameSurface.pause();
    }

    protected void onResume()
    {
        super.onResume();
        gameSurface.resume();
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener
    {
        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap background;
        Bitmap facebook;
        float facebookX;
        float facebookY;
        private Handler handler = new Handler();
        private Timer timer = new Timer();

        Paint paintProperty;
        int screenWidth;
        int screenHeight;

        public GameSurface(Context context)
        {
            super(context);
            holder = getHolder();

            background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            facebook = BitmapFactory.decodeResource(getResources(), R.drawable.facebook);
            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;
            facebookX = -80.0f;
            facebookY = screenHeight + 80.0f;

            paintProperty = new Paint();
        }

        public void resume()
        {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause()
        {
            running = false;
            while(true)
            {
                try {
                    gameThread.join();
                } catch (InterruptedException e)
                {

                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void run() {
            Canvas canvas = null;
            Drawable d = getResources().getDrawable(R.drawable.background, null);
            while(running) {
                if (holder.getSurface().isValid() == false)
                  continue;

                canvas = holder.lockCanvas(null);
                d.setBounds(getLeft(), getTop(), getRight(), getBottom());
                d.draw(canvas);
                canvas.drawBitmap(facebook, facebookX, facebookY, null);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePosition();
                            }
                        });
                    }
                }, 4000, 2000);

                holder.unlockCanvasAndPost(canvas);
            }

        }

        public void changePosition()
        {
            facebookY += 10;
            if(facebookY > screenHeight)
            {
                facebookX = (float)Math.floor(Math.random() * (screenWidth - facebook.getWidth()/2));
                facebookY = -100.0f;
            }
        }
    }


}