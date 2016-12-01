package edu.training.wearbountyhunter;

import android.os.Bundle;
import android.util.Log;

import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;


/**
 * Created by brachialste on 30/11/16.
 */

public class MiGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "-> onMessageReceived()");
        String message = data.getString("Json");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        try{
            if(from.startsWith("/topics/")){
                NotificacionesBuilder notificacionesBuilder;
                JSONObject jsonObject = new JSONObject(message);
                String UDIDNube = jsonObject.getString("UDID");
                // se obtiene el ID del dispositvo
                String UDIDDevice = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

                if(UDIDNube.equalsIgnoreCase(UDIDDevice)){
                    if(Home.oDB == null){
                        Home.oDB = new DBProvider(this);
                    }
                    if(Home.oDB.ContarFugitivos() <= 0){
                        notificacionesBuilder = new NotificacionesBuilder(this);
                        notificacionesBuilder.notificacionVoz();
                    }else{
                        notificacionesBuilder = new NotificacionesBuilder(this);
                        notificacionesBuilder.notificacionGeneral("PUSH IGNORADA", "Ya se cuentan con figutivos localmente");
                        Toast.makeText(this, "Ya se cuentan con fugitivos en la base de datos (PUSH IGNORADA)", Toast.LENGTH_LONG).show();
                    }
                } else if(UDIDNube.equalsIgnoreCase("Alarma de Temperatura")) {
                    // se obtiene la temperatura de referencia
                    String temperaturareferencia = jsonObject.getString("tempReferencia");
                    notificacionesBuilder = new NotificacionesBuilder(this);
                    notificacionesBuilder.notificacionVozAlarma(temperaturareferencia);
                } else{
                    Toast.makeText(this, "La notificacion se envio por device token y no por topico.", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception ex){
            Log.d(TAG, "Exception: " + ex.toString());
        }
    }
}
