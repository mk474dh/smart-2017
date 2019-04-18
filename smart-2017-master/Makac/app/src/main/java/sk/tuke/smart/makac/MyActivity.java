package sk.tuke.smart.makac;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sk.tuke.smart.makac.services.TrackerService;
import sk.tuke.smart.makac.services.databaseServices.HttpAsyncTaskPost;
import sk.tuke.smart.makac.services.databaseServices.HttpGetJSONAsync;

import static sk.tuke.smart.makac.helpers.MainHelper.formatCalories;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDistance;
import static sk.tuke.smart.makac.helpers.MainHelper.formatDuration;
import static sk.tuke.smart.makac.helpers.MainHelper.formatPace;


public class MyActivity extends AppCompatActivity implements HttpGetJSONAsync.Listener {

    ArrayAdapter arrayAdapter;
    ListView smsListView;
    ArrayList<String> smsMessagesList = new ArrayList<String>();

    public static final String URL = "http://10.0.2.2:8080/user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        smsListView = (ListView) findViewById(R.id.incomesData);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        new HttpGetJSONAsync(this).execute(URL);
        final Button gplus = (Button) findViewById(R.id.back);
        gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MyActivity.this, StopwatchActivity.class);
               startActivity(intent);
            }
        });

    }


    @Override
    public void onLoaded(List<JSONObject> objectList) {

        for(JSONObject object: objectList) {
            try {
                arrayAdapter.add(" Workout:   "+(object.get("tname"))+ "\n Time:    "+(object.get("date"))+ "\n Duration:   "+(object.get("duration"))+ "\n Distance:   "+(object.get("distance"))+ " km"+ "\n Calories:    "+(object.get("calories"))+ " kcal"+ "\n Pace:  "+(object.get("avgpace"))+ " min/km");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError() {

    }






}


