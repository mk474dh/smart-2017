package sk.tuke.smart.makac.services.databaseServices;

import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Response;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HttpGetJSONAsync extends AsyncTask<String,Void,Response> {

    private Listener mListener;
    private JSONObject ob;
    List<JSONObject> objectList = new ArrayList<>();

    public HttpGetJSONAsync(Listener listener) {

        mListener =  listener;
    }

    public interface Listener {

        void onLoaded(List<JSONObject> objectList) throws JSONException;

        void onError();
    }



    @Override
    protected Response doInBackground(String... strings) {
        try {

            String stringResponse = loadJSON(strings[0]);
            Gson gson = new Gson();

            return gson.fromJson(stringResponse, Response.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {

        try {
            mListener.onLoaded(objectList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String loadJSON(String jsonURL) throws IOException {
        String json = " ";
        JSONObject object = new JSONObject();
        URL url = new URL(jsonURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        JSONArray Jarray = new JSONArray();
        // try parse the string to a JSON object
        try {
            Jarray = new JSONArray(response.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }




        for(int i = 0; i < Jarray.length(); i++)  // using for loop for parsing
        {

            try {
                objectList.add(Jarray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(object);

        in.close();
        return object.toString();
    }





}
