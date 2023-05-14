package com.example.ex1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ex1.Logic.GameManager;
import com.example.ex1.Models.Obstacle;
import com.example.ex1.Models.ObstacleListener;
import com.example.ex1.Models.Position;
import com.example.ex1.Models.TimerCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ObstacleListener {

    private AppCompatImageView main_IMG_background;
    private MaterialTextView main_LBL_score;

    private int driverImageResource;

    private Button main_BTN_left;
    private Button main_BTN_right;
    private ShapeableImageView[] main_IMG_hearts;
    private ShapeableImageView main_IMG_driver;
    private RelativeLayout main_LAY_main;
    private GameManager gameManager;

    private HashMap<Obstacle, ImageView> obstacleImageviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        obstacleImageviews = new HashMap<Obstacle, ImageView>();
        this.driverImageResource = R.drawable.ic_spaceship;

        gameManager = new GameManager(main_IMG_hearts.length);
        /*
        Glide
                .with(this)
                .load("https://images.pexels.com/photos/1450082/pexels-photo-1450082.jpeg")
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(main_IMG_background);
         */
        final ViewTreeObserver textViewTreeObserver = main_LAY_main.getViewTreeObserver();

        ViewTreeObserver.OnGlobalLayoutListener a = new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                Log.d("lyf", "onGlobalLayout: ");
                Position p = new Position(main_IMG_driver.getX(), main_IMG_driver.getY());
                gameManager.setDriverPos(p).gameStart();
                main_LAY_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
        textViewTreeObserver.addOnGlobalLayoutListener(a);

        gameManager.addObstacleListener(this);
        // obstacle speeds
        gameManager.setSpeedx(0).setSpeedy(80).
                setObstacleImageResource(R.drawable.asteroid1);

        refreshUI();

        setAnswersClickListeners();
        main_BTN_left.setOnClickListener(v -> gameManager.moveDriverLeft());
        main_BTN_right.setOnClickListener(v -> gameManager.moveDriverRight());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lyf", "onResume: ");

        gameManager.gameResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lyf", "onStop: ");
        gameManager.gamePause();
    }

    @Override
    protected void onPause() {
        // stop timer
        super.onPause();
        Log.d("lyf", "onPause: ");
        gameManager.gamePause();
    }
    @Override
    protected void onDestroy() {
        // destroy timer
        super.onDestroy();
        Log.d("lyf", "onDestroy: ");
        gameManager.gameDestroy();
    }

    // todo: set button move shit left right
    private void setAnswersClickListeners() {
        //for (MaterialButton mb : main_BTN_options) {
        //    mb.setOnClickListener(v -> clicked(mb.getText().toString()));
        //}
    }

    private void clicked(String selectedAnswer) {
        /*
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        gameManager.checkAnswer(getApplicationContext() ,v, selectedAnswer);

        refreshUI();
        */
    }

    private void refreshUI() {
        if (gameManager.isGameEnded()){
            openScoreScreen("Game Over", gameManager.getScore());
        }else{
            main_IMG_driver.setImageResource(driverImageResource);
            main_LBL_score.setText(String.valueOf(gameManager.getScore()));

            // start timer

            if(gameManager.getDeaths() != 0)
                main_IMG_hearts[main_IMG_hearts.length - gameManager.getDeaths()].setVisibility(View.INVISIBLE);
        }
    }

    private void openScoreScreen(String status, int score) {
        Intent scoreIntent = new Intent(this, ScoreActivity.class);
        scoreIntent.putExtra(ScoreActivity.KEY_SCORE, score);
        scoreIntent.putExtra(ScoreActivity.KEY_STATUS, status);
        startActivity(scoreIntent);
        finish();
    }


    private void findViews() {
        main_IMG_background = findViewById(R.id.main_IMG_background);

        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_right = findViewById(R.id.main_BTN_right);

        main_IMG_hearts = new ShapeableImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)};

        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_IMG_driver = findViewById(R.id.main_IMG_driver);

        main_LAY_main = findViewById(R.id.main_LAY_main);
    }

    @Override
    public void ObstacleMoved(Obstacle obs) {
        // if object position left the screen
        // 1. remove from gamemanager
        // 2. remove from map
        // 3. update score

        // updates score and lives ui counters
        refreshUI();

        int layh = main_LAY_main.getHeight();

        // when layout hasnt been inflated yet
        if (layh==0)
        {
            return;
        }

        ImageView imgv = obstacleImageviews.get(obs);

        float x = (float) obs.getPosition().getTopleftx();
        float y = (float) obs.getPosition().getToplefty();

        if (imgv != null) {
            if (imgv.getY() >= layh) {
                gameManager.removeBlock(obs);
                return;
            }

            imgv.animate().x(x).
                    y(y).
                    setDuration(1200);
            //Log.d("event", "ObstacleMoved: id: "+obs.hashCode() +"x: " + imgv.getX() +" y: "+
            //        imgv.getY() + " set to " +x +","+y);
        }
        //imgv.getDrawingRect();

    }

    private static int hash1 = 0;
    @Override
    public void ObstacleCreated(Obstacle obs) {
        Log.d("Created", "ObstacleCreated: "+ System.currentTimeMillis());
        // todo: add object and imageview to map
        ImageView imgv = new ImageView(this);
        imgv.setImageResource(obs.getImageResource());
        //imgv.setMaxWidth(100);
        //imgv.setMaxHeight(100);
        imgv.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        imgv.setX((float) obs.getPosition().getTopleftx());
        imgv.setY((float) obs.getPosition().getToplefty());
        obstacleImageviews.put(obs, imgv);

        main_LAY_main.addView(imgv);

    }

    @Override
    public void ObstacleHit(Obstacle obs) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 200 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(200);
        }
        refreshUI();
        gameManager.gamePause();
        Toast.makeText(this,"🥳 Head-on Collision!",Toast.LENGTH_LONG).show();
        gameManager.gameResume();

    }

    @Override
    public void ObstacleRemoved(Obstacle obs) {
        ImageView imgv = obstacleImageviews.get(obs);

        if (imgv != null) {
            main_LAY_main.removeView(imgv);
        }
    }

    @Override
    public void driverMoved(Position pos) {
        float x = (float) pos.getTopleftx();
        float y = (float) pos.getToplefty();

        if (main_IMG_driver != null) {

            main_IMG_driver.animate().x(x).
                    y(y).
                    setDuration(200);
            //Log.d("event", "ObstacleMoved: id: "+obs.hashCode() +"x: " + imgv.getX() +" y: "+
            //        imgv.getY() + " set to " +x +","+y);
        }
    }
}