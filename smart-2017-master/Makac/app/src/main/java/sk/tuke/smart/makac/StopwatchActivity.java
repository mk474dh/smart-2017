package sk.tuke.smart.makac;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.jar.Attributes;

import sk.tuke.smart.makac.services.TrackerService;
import sk.tuke.smart.makac.services.databaseServices.HttpAsyncTaskPost;

import static sk.tuke.smart.makac.helpers.MainHelper.formatCalories;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDistance;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDuration;
import static sk.tuke.smart.makac.helpers.MainHelper.formatPace;


public class StopwatchActivity extends AppCompatActivity {
    private TextView activitydateView;
    private TextView workoutNameView;
    private TextView sportActivityView;
    private TextView durationView;
    private TextView averagePaceView;
    private TextView caloriesView;
    private TextView distanceView;
    private TextView musicView;

    private String workoutName;
    private String activityDate;
    private int sportActivity = 0;
    private long duration;
    private double distance;
    private double pace;
    private double calories;
    private int state;
    private ArrayList<List<Location>> finalPositionList;
    private BroadcastReceiver broadcastReceiver;
    private int pause = 0;
    private int start = 0;
    private int startMusic = 0;
    private int pauseMusic = 1;
    private String selectSport;
    private int select = 0;
    private int stepBack = 0;
    private int nextClick = 0;
    private Button buttonStart;
    private Button buttonStop;
    private String tName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        final MediaPlayer DontLookDown = MediaPlayer.create(getApplicationContext(),R.raw.dontlookdown);
        final MediaPlayer RatherBe = MediaPlayer.create(getApplicationContext(),R.raw.ratherbe);
        final MediaPlayer Roses = MediaPlayer.create(getApplicationContext(),R.raw.roses);
        final MediaPlayer Rude = MediaPlayer.create(getApplicationContext(),R.raw.rude);
        final MediaPlayer Runnin = MediaPlayer.create(getApplicationContext(),R.raw.runnin);
        final MediaPlayer Timber = MediaPlayer.create(getApplicationContext(),R.raw.timber);

        final Button music = (Button) findViewById(R.id.music_play);

        musicView = (TextView) findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startMusic == 0) {
                    music.setText("Pause");
                    if(Objects.equals(musicView.getText().toString(), "Martin Garrix feat. Usher - Dont Look Down")){
                        DontLookDown.start();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "MAGIC - Rude (Zedd Remix)")){
                        Rude.start();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "The Chainsmokers ft  ROZES   Roses ZAXX Remix")){
                        Roses.start();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "Naughty Boy - Runnin (Lose It All)")){
                        Runnin.start();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "Pitbull - Timber ft. Kesha")){
                        Timber.start();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "Clean Bandit - Rather Be feat. Jess Glynne")){
                        RatherBe.start();
                    }
                    startMusic = 1;
                } else if (startMusic == 1) {
                    music.setText("Play");
                    startMusic=0;
                    if(Objects.equals(musicView.getText().toString(), "Martin Garrix feat. Usher - Dont Look Down")){
                        DontLookDown.pause();

                    }
                    else if(Objects.equals(musicView.getText().toString(), "MAGIC - Rude (Zedd Remix)")){
                        Rude.pause();

                    }
                    else if(Objects.equals(musicView.getText().toString(), "The Chainsmokers ft  ROZES   Roses ZAXX Remix")){
                        Roses.pause();

                    }
                    else if(Objects.equals(musicView.getText().toString(), "Naughty Boy - Runnin (Lose It All)")){
                        Runnin.pause();
                    }
                    else if(Objects.equals(musicView.getText().toString(), "Pitbull - Timber ft. Kesha")){
                        Timber.pause();

                    }
                    else if(Objects.equals(musicView.getText().toString(), "Clean Bandit - Rather Be feat. Jess Glynne")){
                        RatherBe.pause();

                    }

                }
            }
        });

        final Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(nextClick==0) {
                    musicView.setText(R.string.Rude);
                    nextClick++;
                }
                else if(nextClick==1){
                    musicView.setText(R.string.Roses);
                    nextClick++;
                }
                else if(nextClick==2){
                    musicView.setText(R.string.Runnin);
                    nextClick++;
                }
                else if(nextClick==3){
                    musicView.setText(R.string.Rather);
                    nextClick++;
                }
                else if(nextClick==4){
                    musicView.setText(R.string.Timber);
                    nextClick++;
                }
                else if(nextClick==5){
                    musicView.setText(R.string.DontLook);
                    nextClick=0;
                }

            }
        });









        durationView = (TextView) findViewById(R.id.textview_stopwatch_duration);
        caloriesView = (TextView) findViewById(R.id.textview_stopwatch_calories);
        distanceView = (TextView) findViewById(R.id.textview_stopwatch_distance);
        averagePaceView = (TextView) findViewById(R.id.textview_stopwatch_pace);


          buttonStart = (Button) findViewById(R.id.button_stopwatch_start);
          buttonStop = (Button) findViewById(R.id.button_stopwatch_endworkout);


        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(select==1) {
                    if (start == 0) {
                        buttonStart.setBackground(getResources().getDrawable(R.drawable.pausemario));
                        buttonStop.setVisibility(View.VISIBLE);
                        buttonStart.setText(R.string.stopwatch_stop);
                        Intent send = new Intent(StopwatchActivity.this, TrackerService.class);
                        send.setAction("sk.tuke.smart.makac.COMMAND_START");
                        send.putExtra("sportactivity",sportActivity);
                        System.out.println(sportActivity);
                        startService(send);
                        start = 1;
                    } else if (pause == 0) {
                        buttonStart.setBackground(getResources().getDrawable(R.drawable.startmario));
                        buttonStart.setText(R.string.stopwatch_stop);
                        Intent send = new Intent(StopwatchActivity.this, TrackerService.class);
                        send.setAction("sk.tuke.smart.makac.COMMAND_PAUSE");
                        startService(send);
                        pause = 1;
                    } else {
                        buttonStart.setBackground(getResources().getDrawable(R.drawable.pausemario));
                        buttonStart.setText(R.string.stopwatch_continue);
                        Intent send = new Intent(StopwatchActivity.this, TrackerService.class);
                        send.setAction("sk.tuke.smart.makac.COMMAND_CONTINUE");
                        startService(send);
                        pause = 0;
                    }
                }
                else{
                    Toast.makeText(
                            StopwatchActivity.this,
                            "First you need to select sport activity",
                            Toast.LENGTH_SHORT
                    ).show();
                }


            }
        });



        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonStop.setText(R.string.stopwatch_stop);
                select = 0;
                stepBack = 1;
                Intent send = new Intent(StopwatchActivity.this, TrackerService.class);
                send.setAction("sk.tuke.smart.makac.COMMAND_STOP");
                startService(send);
            }
        });

        final Button selectButton = (Button) findViewById(R.id.button_stopwatch_selectsport);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(StopwatchActivity.this, selectButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        select=1;
                        selectSport = item.getTitle().toString();
                        String walk = "Walking";
                        String run = "Running";
                        String cycl = "Cycling";
                        if(Objects.equals(selectSport, run)){
                            sportActivity = 0;
                        }
                        else if (Objects.equals(selectSport, walk)){
                            sportActivity = 1;
                        }
                        else if(Objects.equals(selectSport, cycl)){
                            sportActivity = 2;
                        }
                        Toast.makeText(
                                StopwatchActivity.this,
                                "You select : " + sportActivity,
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method


    }


    @Override
    protected void onResume() {
        super.onResume();
        if(stepBack==1){
            Intent send = new Intent(StopwatchActivity.this, StopwatchActivity.class);
            startActivity(send);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "sk.tuke.smart.makac.TICK":
                        if(finalPositionList==null){
                            finalPositionList = new ArrayList<>();
                        }
                        state = intent.getExtras().getInt("state");
                        if(state == 2 ){
                            finalPositionList.add(intent.getExtras().<Location>getParcelableArrayList("location"));
                        }
                        if(state ==0){
                            finalPositionList.add(intent.getExtras().<Location>getParcelableArrayList("location"));
                            for (List n : finalPositionList ){
                                System.out.println(n);
                            }
                                final Intent send = new Intent(StopwatchActivity.this, WorkoutDetailActivity.class);
                                send.setAction("sk.tuke.smart.makac.START");

                                send.putExtra("sportActivity", sportActivity);
                            duration = intent.getExtras().getLong("duration");
                            distance = intent.getExtras().getDouble("distance");
                            pace = intent.getExtras().getDouble("pace");
                            calories = intent.getExtras().getDouble("calories");
                            long millis = new Date().getTime();
                            activityDate = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.UK).format(millis);
                            send.putExtra("duration",duration);
                                send.putExtra("distance",distance);
                                send.putExtra("pace",pace);
                                send.putExtra("calories", calories);
                                send.putExtra("finalLocations", finalPositionList);


                                    String update[] = new String[6];
                            if (sportActivity == 0) {
                                update[0]= String.valueOf("Running");
                            } else if (sportActivity == 1) {
                                update[0]= String.valueOf("Walking");

                            } else if (sportActivity == 2) {
                                update[0]= String.valueOf("Cycling");
                            }
                                    update[1]= String.valueOf(activityDate);
                                    update[2]= formatDuration(duration);
                                    update[3]= formatDistance(distance);
                                    update[4]= formatPace(pace);
                                    update[5]= formatCalories(calories);
                                    //System.out.println(update[0]+update[1]+update[2]+update[3]);
                                    new HttpAsyncTaskPost().execute(update);
                                    startActivity(send);

                        }
                        else {
                            duration = intent.getExtras().getLong("duration");
                            distance = intent.getExtras().getDouble("distance");
                            pace = intent.getExtras().getDouble("pace");
                            calories = intent.getExtras().getDouble("calories");
                            averagePaceView.setText(formatPace(pace));
                            durationView.setText(formatDuration(duration));
                            caloriesView.setText(formatCalories(calories));
                            distanceView.setText(formatDistance(distance));
                            averagePaceView.setText(formatPace(pace));
                        }
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("sk.tuke.smart.makac.TICK"));

    }



    @Override
    protected void onPause() {
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("workoutName", workoutName);
        savedInstanceState.putString("activityDate", activityDate);
        savedInstanceState.putLong("duration", duration);
        savedInstanceState.putDouble("distance", distance);
        savedInstanceState.putDouble("pace",pace);
        savedInstanceState.putDouble("calories", calories);
        savedInstanceState.putInt("sportActivity", sportActivity);
//        savedInstanceState.putParcelableArrayList("finalLocation", (ArrayList<? extends Parcelable>) finalPositionList);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        workoutName = savedInstanceState.getString("workoutName");
        activityDate = savedInstanceState.getString("activityDate");
        duration = savedInstanceState.getLong("duration");
        distance = savedInstanceState.getDouble("distance");
        pace = savedInstanceState.getDouble("pace");
        calories = savedInstanceState.getDouble("calories");
        sportActivity = savedInstanceState.getInt("sportActivity");
       // finalPositionList = savedInstanceState.getParcelableArrayList("finalLocation");
    }



    }


