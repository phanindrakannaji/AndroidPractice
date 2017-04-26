package com.example.nilan.fcmdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button myButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = (Button)findViewById(R.id.button2);
        myButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button2:
                String token = FirebaseInstanceId.getInstance().getToken();
                Toast.makeText(MainActivity.this, token, Toast.LENGTH_LONG).show();
                sendRegistrationtoServer(token);
                break;
        }
    }

    private class UploadTask extends AsyncTask<String, Integer, String>{

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
