package com.example.android.friendfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText reg_username, reg_fullName, reg_password, login_username, login_password;
    Button register, login;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reg_username = (EditText) findViewById(R.id.et_username);
        reg_password = (EditText) findViewById(R.id.et_password);
        reg_fullName = (EditText) findViewById(R.id.et_fullname);
        login_username = (EditText) findViewById(R.id.et_login_username);
        login_password = (EditText) findViewById(R.id.et_login_password);
        register = (Button) findViewById(R.id.bt_register);
        login = (Button) findViewById(R.id.bt_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        register.setOnClickListener(null);
        login.setOnClickListener(null);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_login:
                String username = String.valueOf(login_username.getText());
                String password = String.valueOf(login_password.getText());
                if (username.equalsIgnoreCase("")){
                    login_username.setError("Username cannot be blank!");
                }
                if (password.equalsIgnoreCase("")){
                    login_password.setError("Password cannot be blank!");
                }
                if (!username.equalsIgnoreCase("") && !password.equalsIgnoreCase("")){
                    String[] input = new String[3];
                    input[0] = "login";
                    input[1] = username;
                    input[2] = password;
                    UserTask createUserTask = new UserTask();
                    createUserTask.execute(input);
                }
                break;

            case R.id.bt_register:
                username = String.valueOf(reg_username.getText());
                password = String.valueOf(reg_password.getText());
                String fullName = String.valueOf(reg_fullName.getText());
                if (username.equalsIgnoreCase("")){
                    reg_username.setError("Username cannot be blank!");
                }
                if (password.equalsIgnoreCase("")){
                    reg_password.setError("Password cannot be blank!");
                }
                if (fullName.equalsIgnoreCase("")){
                    reg_fullName.setError("Name cannot be blank!");
                }
                if (!username.equalsIgnoreCase("") && !password.equalsIgnoreCase("") && !fullName.equalsIgnoreCase("")){
                    String[] input = new String[4];
                    input[0] = "register";
                    input[1] = username;
                    input[2] = fullName;
                    input[3] = password;
                    UserTask createUserTask = new UserTask();
                    createUserTask.execute(input);
                }
                break;
        }
    }

    private class DisplayToast implements Runnable{

        String message;

        DisplayToast(String message){
            this.message = message;
        }
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private class UserTask extends AsyncTask<String, Integer, List<Friend>> {

        String error = "";
        @Override
        protected List<Friend> doInBackground(String... strings) {
            URL url;
            String response = "";
            String domain = getString(R.string.domain);
            String requestUrl = "";
            if (strings[0].equalsIgnoreCase("login")){
                requestUrl = domain + "/friendfinder/login.php";
            } else if (strings[0].equalsIgnoreCase("register")){
                requestUrl = domain + "/friendfinder/registerUser.php";
            }
            List<Friend> friends = new ArrayList<>();
            try{
                url = new URL(requestUrl);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setReadTimeout(15000);
                myConnection.setConnectTimeout(15000);
                myConnection.setRequestMethod("POST");
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);

                OutputStream os = myConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                String requestJsonString = "";
                if (strings[0].equalsIgnoreCase("login")){
                    requestJsonString = new JSONObject()
                            .put("username", strings[1])
                            .put("password", strings[2])
                            .toString();
                } else if (strings[0].equalsIgnoreCase("register")){
                    requestJsonString = new JSONObject()
                            .put("username", strings[1])
                            .put("fullName", strings[2])
                            .put("password", strings[3])
                            .toString();
                }

                Log.d("REQUEST BODY : ", requestJsonString);
                bw.write(requestJsonString);
                bw.flush();
                bw.close();

                int responseCode = myConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                    line = br.readLine();
                    while(line != null){
                        response += line;
                        line = br.readLine();
                    }
                    br.close();
                }
                Log.d("RESPONSE BODY: ", response);

                JSONArray jsonArray = new JSONArray(response);
                if(jsonArray.length() > 0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject childJsonObj = jsonArray.getJSONObject(i);
                        if (childJsonObj.getString("status").equalsIgnoreCase("S")) {
                            Friend friend = new Friend(childJsonObj.getString("username"),
                                    childJsonObj.getString("fullName"),
                                    childJsonObj.getDouble("latitude"),
                                    childJsonObj.getDouble("longitude"),
                                    childJsonObj.getString("latestTimestamp"));
                            friends.add(friend);
                        } else if (childJsonObj.getString("status").equalsIgnoreCase("F")){
                            error = childJsonObj.getString("errorMessage");
                            DisplayToast displayToast = new DisplayToast(error);
                            handler.post(displayToast);
                        }
                    }
                }
                myConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return friends;
        }

        @Override
        protected void onPostExecute(List<Friend> friends) {
            if (error.equalsIgnoreCase("")) {
                super.onPostExecute(friends);
                for(Friend friend : friends){
                    SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = userDetails.edit();
                    editor.putString("username", String.valueOf(friend.getUsername()));
                    editor.putString("fullName", String.valueOf(friend.getFullName()));
                    editor.apply();
                    Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                    myIntent.putExtra("userName", String.valueOf(friend.getUsername()));
                    myIntent.putExtra("fullName", String.valueOf(friend.getFullName()));
                    startActivity(myIntent);
                }
            }
        }
    }
}
