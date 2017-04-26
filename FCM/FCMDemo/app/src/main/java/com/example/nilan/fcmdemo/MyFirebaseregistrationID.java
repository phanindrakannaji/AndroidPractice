package com.example.nilan.fcmdemo;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nilan on 4/18/2017.
 */

public class MyFirebaseregistrationID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationtoServer(token);
    }
    private class UploadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String responsestring = "";
            String token = params[0];
            HttpURLConnection connection = null;
            try
            {

                String urlstring = "https://eclipse.umbc.edu/registertoken.php";
                urlstring += "?token="+token;
                URL url = new URL(urlstring);
                connection = (HttpURLConnection) url.openConnection();
                int code = connection.getResponseCode();
                if(code == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()
                    ));
                    line = br.readLine();
                    while(line != null){
                        responsestring += line;
                        line = br.readLine();
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally{
                if(connection != null)
                {
                    connection.disconnect();
                }
            }
            return responsestring;
        }
    }

    private void sendRegistrationtoServer(String token)
    {
        new UploadTask().execute(token);
    }
}
