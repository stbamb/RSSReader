package com.stbam.rssnewsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Esteban on 11/05/2014.
 */

// esta clase sirve para recibir mensajes desde otros Activities
// y una vez recibido el mensaje se cierra MainActivity
public class Receptor extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Mensaje recibido");
        // Implement code here to be performed when
        // broadcast is detected

    }
}
