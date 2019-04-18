package sk.tuke.smart.makac.services.databaseServices;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class HttpAsyncTaskPost extends AsyncTask<String,Void,String> {




        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            System.out.println(arg0[0]+arg0[1]+arg0[2]+arg0[3]);


            InputStream inputStream = null;
            String result = "";
            try {

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost("http://10.0.2.2:8080/user");

                String json = "";


                // 3. build jsonObject
               /* JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("firstname", "Maťa");
                jsonObject.accumulate("lastname", "Kopčáková");
                jsonObject.accumulate("roleEnum", "student");
                jsonObject.accumulate("notificationEnum", "day");
*/


                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("tname", arg0[0]);
                jsonObject.accumulate("date", arg0[1]);
                jsonObject.accumulate("duration", arg0[2]);
                jsonObject.accumulate("distance", arg0[3]);
                jsonObject.accumulate("avgpace", arg0[4]);
                jsonObject.accumulate("calories", arg0[5]);


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json, HTTP.UTF_8);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            // 11. return result
            return result;


        }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    @Override
        protected void onPostExecute(String result) {


        }
    }
