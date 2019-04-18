package sk.tuke.smart.makac.services.databaseServices;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpGetJSONAsyncObj extends AsyncTask<String,Void,Response> {

    private Listener mListener;
    private JSONObject ob;
    JSONObject object = new JSONObject();

    public HttpGetJSONAsyncObj(Listener listener) {

        mListener = listener;
    }

    public interface Listener {

        void onLoaded(JSONObject jsonObject) ;

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

        mListener.onLoaded(object);

    }

    private String loadJSON(String jsonURL) throws IOException {
        String json = " ";

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


        // try parse the string to a JSON object
        try {
            this.object = new JSONObject(response.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }






        in.close();
        return object.toString();
    }





}
