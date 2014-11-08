package com.stbam.rssnewsreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {

    public static String current_user_name = "";
    public static String current_user_id = "";
    public static String current_user_email = "";
    public static boolean terminado = false;
    JSONObject jsonObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        llenarInfoFacebook();

        getInfo a = new getInfo();
        a.execute();

        int abc = 0;
        while (!terminado)
            abc++;

        jsonObj = new JSONObject();
        try {
            jsonObj.put("name", current_user_name);
            jsonObj.put("id", current_user_id);
            jsonObj.put("email", current_user_email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObj);


        AlertDialog alertDialog1 = new AlertDialog.Builder(
                LoginActivity.this).create();

        alertDialog1.setTitle("RSS Reader");
        alertDialog1.setMessage("Usuario registrado exitosamente");
        alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                startSplashActivity();
            }
        });

        alertDialog1.show();


    }

    public void startSplashActivity() {

        // launch List activity
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);

        // kill this activity
        finish();
    }

    public void llenarInfoFacebook()
    {
        // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {

                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                EditText user_name = (EditText) findViewById(R.id.name4);
                                TextView user_email = (TextView) findViewById(R.id.email4);
                                TextView user_id = (TextView) findViewById(R.id.facebookID4);
                                user_name.setText(user.getName());
                                user_email.setText("" + user.asMap().get("email"));
                                user_id.setText(user.getId());
                                current_user_name = user_name.getText().toString();
                                current_user_id = user.getId();
                                current_user_email = (String) user.asMap().get("email");
                                GraphObject responseGraphObject = response.getGraphObject();

                            }
                        }
                    }).executeAsync();


                }
            }
        });
    }

    public class getInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Session session = Session.getActiveSession();

            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        current_user_id = user.getId();
                        current_user_name = user.getName();
                        current_user_email = (String) user.asMap().get("email");
                        terminado = true;
                        System.out.println("La cochinada esta esta llegando hasta aqui");
                    }
                }
            }).executeAndWait();



            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            terminado = true;
        }

    }
}