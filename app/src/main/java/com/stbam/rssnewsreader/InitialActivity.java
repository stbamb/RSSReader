package com.stbam.rssnewsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.facebook.Session;
import com.facebook.widget.LoginButton;

import java.util.Arrays;


public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Session actual = Session.getActiveSession();
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("basic_info", "email"));
        if (actual != null && actual.isOpened())
            startSplashActivity();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.initial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void startSignupActivity(View view) {

        // launch List activity
        Intent intent = new Intent(InitialActivity.this, MainActivity.class);
        startActivity(intent);

        // kill this activity
        finish();
    }

    public void startLoginActivity(View view) {

        // launch List activity
        Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
        startActivity(intent);

        // kill this activity
        finish();
    }

    public void startSplashActivity() {

        // launch List activity
        Intent intent = new Intent(InitialActivity.this, SplashActivity.class);
        startActivity(intent);

        // kill this activity
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        Session actual = Session.getActiveSession();
        if (actual != null && actual.isOpened())
            startSplashActivity();
    }


}
