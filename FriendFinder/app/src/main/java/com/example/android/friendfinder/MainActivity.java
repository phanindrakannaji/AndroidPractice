package com.example.android.friendfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private static final String TAG = "MainActivity";
    EditText reg_email, reg_fullName, reg_password, login_email, login_password;
    Button register, login;
    Handler handler = new Handler();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reg_email = (EditText) findViewById(R.id.et_email);
        reg_password = (EditText) findViewById(R.id.et_password);
        reg_fullName = (EditText) findViewById(R.id.et_fullname);
        login_email = (EditText) findViewById(R.id.et_login_email);
        login_password = (EditText) findViewById(R.id.et_login_password);
        register = (Button) findViewById(R.id.bt_register);
        login = (Button) findViewById(R.id.bt_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
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
                final String loginEmail = String.valueOf(login_email.getText());
                final String loginPassword = String.valueOf(login_password.getText());
                if (loginEmail.equalsIgnoreCase("")){
                    login_email.setError("Email cannot be blank!");
                }
                if (loginPassword.equalsIgnoreCase("")){
                    login_password.setError("Password cannot be blank!");
                }
                if (!loginEmail.equalsIgnoreCase("") && !loginPassword.equalsIgnoreCase("")){
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String[] input = new String[3];
                                        input[0] = "login";
                                        input[1] = loginEmail;
                                        input[2] = loginPassword;
                                        UserTask createUserTask = new UserTask();
                                        createUserTask.execute(input);
                                        updateUI(user);
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
                break;

            case R.id.bt_register:
                final String email = String.valueOf(reg_email.getText());
                final String password = String.valueOf(reg_password.getText());
                final String fullName = String.valueOf(reg_fullName.getText());
                if (email.equalsIgnoreCase("")){
                    reg_email.setError("Email cannot be blank!");
                }
                if (password.equalsIgnoreCase("")){
                    reg_password.setError("Password cannot be blank!");
                }
                if (fullName.equalsIgnoreCase("")){
                    reg_fullName.setError("Name cannot be blank!");
                }
                if (!email.equalsIgnoreCase("") && !password.equalsIgnoreCase("") && !fullName.equalsIgnoreCase("")){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String[] input = new String[4];
                                    input[0] = "register";
                                    input[1] = email;
                                    input[2] = fullName;
                                    input[3] = password;
                                    UserTask createUserTask = new UserTask();
                                    createUserTask.execute(input);
                                    updateUI(user);
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
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
                            .put("email", strings[1])
                            .put("password", strings[2])
                            .toString();
                } else if (strings[0].equalsIgnoreCase("register")){
                    requestJsonString = new JSONObject()
                            .put("email", strings[1])
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
                            Friend friend = new Friend(childJsonObj.getString("email"),
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
                    editor.putString("email", String.valueOf(friend.getEmail()));
                    editor.putString("fullName", String.valueOf(friend.getFullName()));
                    editor.apply();
                    Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                    myIntent.putExtra("email", String.valueOf(friend.getEmail()));
                    myIntent.putExtra("fullName", String.valueOf(friend.getFullName()));
                    startActivity(myIntent);
                }
            }
        }
    }
}
