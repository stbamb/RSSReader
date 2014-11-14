package com.stbam.rssnewsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;


public class AccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        llenarInfoFacebook();
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton2);

        // se le asigna un listener al boton de facebook
        authButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Session session = Session.getActiveSession();
                if (session != null)
                    session.closeAndClearTokenInformation();

                // sirve para cerrar MainActivity
                startInitialActivity();
                android.os.Process.killProcess(android.os.Process.myPid());
                // se cierra la sesion y se mata la primera actividad en la pila de android

                // inmediatamente despues la app vuelve a la pagina principal



            }
        });
    }


    // se crea el menu de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account, menu);
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

    // este metodo tambien es de Facebook
    // este metodo es llamado cuando se hace click en el boton de login de FB
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
       // startInitialActivity();
    }

    // este metodo llama a la siguiente actividad a ser ejecutada
    public void startInitialActivity() {

        // launch List activity
        Intent intent = new Intent(AccountActivity.this, InitialActivity.class);
        startActivity(intent);

        // kill this activity
        finish();
    }

    // codigo de Facebook
    // como su nombre la informacion
    // dicha informacion proviene de Facebook
    // se necesita tener FB SDK para que funcione
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

                                // encuentra cada uno de los labels y les asigna el nombre
                                // y la informacion que les corresponde
                                TextView user_name = (TextView) findViewById(R.id.label1);
                                TextView user_email = (TextView) findViewById(R.id.label2);
                                TextView user_id = (TextView) findViewById(R.id.label3);
                                user_name.setText("Nombre de usuario: " + user.getName());
                                user_email.setText("Email de usuario: " + user.asMap().get("email"));
                                user_id.setText("ID de usuario: " + user.getId());
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }
}
