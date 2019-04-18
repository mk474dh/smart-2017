package sk.tuke.smart.makac;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sk.tuke.smart.makac.helpers.MainHelper;
import sk.tuke.smart.makac.services.databaseServices.HttpAsyncTaskPost;

import static android.content.ContentValues.TAG;
import static sk.tuke.smart.makac.helpers.MainHelper.formatCalories;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDistance;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDuration;
import static sk.tuke.smart.makac.helpers.MainHelper.formatPace;

public class WorkoutDetailActivity extends AppCompatActivity {
    private TextView activitydateView;
    private TextView workoutNameView;
    private TextView sportActivityView;
    private TextView durationView;
    private TextView averagePaceView;
    private TextView caloriesView;
    private TextView distanceView;

    private String workoutName;
    private String activityDate;
    private int sportActivity;
    private long duration;
    private double distance;
    private double pace;
    private double calories;
    private ArrayList<? extends List<Location>> finalPositionList;
    private String WORKOUT_TYPE;
    private Bitmap myBitmap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_detail);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(getIntent().getSerializableExtra("path")!=null){
            int type = getIntent().getExtras().getInt("shareType");
            sharing(type);
        }
        else {


            workoutNameView = (TextView) findViewById(R.id.textview_workoutdetail_workouttitle);
            sportActivityView = (TextView) findViewById(R.id.textview_workoutdetail_sportactivity);
            activitydateView = (TextView) findViewById(R.id.textview_workoutdetail_activitydate);
            durationView = (TextView) findViewById(R.id.textview_workoutdetail_valueduration);
            caloriesView = (TextView) findViewById(R.id.textview_workoutdetail_valuecalories);
            distanceView = (TextView) findViewById(R.id.textview_workoutdetail_valuedistance);
            averagePaceView = (TextView) findViewById(R.id.textview_workoutdetail_valueavgpace);


            workoutName ="Workouts";
            long millis = new Date().getTime();
            activityDate = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.UK).format(millis);
            duration = (Long) getIntent().getSerializableExtra("duration");
            distance = (Double) getIntent().getSerializableExtra("distance");
            pace = (Double) getIntent().getSerializableExtra("pace");
            calories = (Double) getIntent().getSerializableExtra("calories");
            workoutNameView.setText(workoutName);
            sportActivity = (Integer) getIntent().getSerializableExtra("sportActivity");
            if (sportActivity == 0) {
                WORKOUT_TYPE = (String) getText(R.string.workout_sportactivity1);
            } else if (sportActivity == 1) {
                WORKOUT_TYPE = (String) getText(R.string.workout_sportactivity2);

            } else if (sportActivity == 2) {
                WORKOUT_TYPE = (String) getText(R.string.workout_sportactivity3);
            }
            finalPositionList = getIntent().getParcelableArrayListExtra("finalLocations");
            System.out.println(WORKOUT_TYPE);
            sportActivityView.setText(WORKOUT_TYPE);
            activitydateView.setText(activityDate);
            durationView.setText(formatDuration(duration));
            caloriesView.setText(formatCalories(calories) + " kcal");
            distanceView.setText(formatDistance(distance) + " km");
            averagePaceView.setText(formatPace(pace) + " min/km");

            final Button activities = (Button) findViewById(R.id.back);
            activities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WorkoutDetailActivity.this, MyActivity.class);
                    startActivity(intent);
                }
            });

            final Button gplus = (Button) findViewById(R.id.button_workoutdetail_gplusshare);
            gplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharing(0);
                }
            });


            final Button twitter = (Button) findViewById(R.id.button_workoutdetail_twittershare);


            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharing(1);
                }
            });

            final Button mail = (Button) findViewById(R.id.button_workoutdetail_emailshare);
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharing(2);
                }
            });
            final Button fb = (Button) findViewById(R.id.button_workoutdetail_fbsharebtn);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharing(3);
                }
            });

            final Button map = (Button) findViewById(R.id.button_workoutdetail_showmap);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent send = new Intent(WorkoutDetailActivity.this, MapsActivity.class);
                    send.putExtra("finalLocations", finalPositionList);
                    startActivity(send);
                }
            });

        }


    }


    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }

    private void sharing(final int shareType) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WorkoutDetailActivity.this);
        if (sportActivity == 0) {
            // Setting Dialog Title
            alertDialogBuilder.setTitle(String.valueOf(getText(R.string.workout_sportactivity1)));
        } else if (sportActivity == 1) {
            alertDialogBuilder.setTitle(String.valueOf(getText(R.string.workout_sportactivity2)));
        } else if (sportActivity == 2) {
            alertDialogBuilder.setTitle(String.valueOf(getText(R.string.workout_sportactivity3)));
        }

        //mapStringUrl
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://maps.googleapis.com/maps/api/staticmap?size=360x360");
        if(finalPositionList!=null) {
            for (List<Location> locations : finalPositionList) {

                urlBuilder.append("&path=color:0xFFE082|weight:5");
                if(locations!=null) {
                    for (Location location : locations) {
                        urlBuilder.append('|');
                        urlBuilder.append(location.getLatitude());
                        urlBuilder.append(',');
                        urlBuilder.append(location.getLongitude());
                    }
                }
            }
        }
        String mapImageUrl = urlBuilder.toString();
        System.out.println(mapImageUrl);
        try {
            URL url = new URL(mapImageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception

        }
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap, "title", null);
            final Uri bitmapUri = Uri.parse(bitmapPath);


            // Setting Dialog Message
            alertDialogBuilder.setMessage(String.valueOf(getText(R.string.workout_share_comment)));
            final EditText input = new EditText(WorkoutDetailActivity.this);
            input.setId(R.id.share_message);
            input.setText(getText(R.string.go).toString() +"  " + WORKOUT_TYPE + "  " + getText(R.string.track).toString() +
                    "  " + String.valueOf(MainHelper.formatDistance(distance)) +
                    " " + getText(R.string.workout_distance_km) + "  " + getText(R.string.v).toString()+ "  " +
                    String.valueOf(MainHelper.formatDuration(duration)) + ".");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialogBuilder.setView(input);
            alertDialogBuilder.setPositiveButton(String.valueOf(getText(R.string.workout_share_me)),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (shareType) {
                                case 0:

                                   Intent shareIntent = ShareCompat.IntentBuilder.from(WorkoutDetailActivity.this)
                                           .setText(input.getText().toString())
                                            .setType("*/*")
                                            .setStream(bitmapUri)
                                            .getIntent()
                                            .setPackage("com.google.android.apps.plus");
                                    startActivity(shareIntent);
                                    break;
                                case 1:
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT, input.getText().toString());
                                    intent.setType("*/*");
                                    intent.setPackage("com.twitter.android");
                                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("*/*");
                                    sharingIntent.setPackage("com.google.android.gm");
                                    String shareBody = input.getText().toString();
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                                    startActivity(sharingIntent);
                                    break;
                                case 3:
                                    Intent normalIntent = new Intent(Intent.ACTION_SEND);
                                    normalIntent.setType("*/*");
                                    normalIntent.setPackage("com.facebook.katana");
                                    normalIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                                    startActivity(normalIntent);
                            }
                        }
                    });
            alertDialogBuilder.setNegativeButton(String.valueOf(getText(R.string.workout_share_cancel)),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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
        savedInstanceState.putParcelableArrayList("finalLocation", (ArrayList<? extends Parcelable>) finalPositionList);

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
        finalPositionList = savedInstanceState.getParcelableArrayList("finalLocation");
    }




}



