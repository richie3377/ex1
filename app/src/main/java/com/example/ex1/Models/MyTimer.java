package com.example.ex1.Models;

import android.os.CountDownTimer;
import android.util.Log;

public class MyTimer {
    private CountDownTimer timer;
    private long milisToRun;
    private long startAt;
    private final long intervalMilis;
    private TimerCallback callback;
    private boolean expired;

    public MyTimer(long secondsToRun, long intervalSeconds, TimerCallback callback) {
        this.milisToRun = 1000 * secondsToRun;
        this.intervalMilis = 1000 * intervalSeconds;
        this.timer = null;
        this.expired = false;
        this.callback = callback;
    }
        public void startTimer() {
            startAt = System.currentTimeMillis();

            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            long eta = milisToRun + System.currentTimeMillis();
            //Log.d("MyTimer", "startTimer: eta "+eta);
            //Log.d("MyTimerRestart", "startTimer: interval "+intervalMilis);

            // milistorun get updated when timer gets paused and restarted
            // based on startAt. in discardTimer()
            timer = new CountDownTimer(milisToRun, intervalMilis) {
                public void onTick(long millisUntilFinished) {
                    callback.OnTick(millisUntilFinished);
                }

                public void onFinish() {
                    //super.OnFinish(); <- abstract
                    Log.d("Mytimer", "onFinish: systime: "+ System.currentTimeMillis());
                    expired = true;
                    callback.OnTimeout();
                }
            }.start();
        }

        public void discardTimer() {
            Log.d("MyTimer", "discardTimer: ");
            milisToRun = milisToRun - (System.currentTimeMillis() - startAt);
            if (milisToRun < 0)
            {
                milisToRun = 0;
            }
            timer.cancel();
        }

}
