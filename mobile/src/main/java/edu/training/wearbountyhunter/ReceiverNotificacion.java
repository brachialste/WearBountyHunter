package edu.training.wearbountyhunter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by brachialste on 29/11/16.
 */

public class ReceiverNotificacion extends BroadcastReceiver {

    // llave utilizada para la obtención del extra del intent con la secuencia de caracteres
    // introducidos en la respuesta
    private static final String EXTRA_VOICE_REPLY = "EXTRA_RESPUESTA";
    private static final String TAG = "ReceiverNotificacion";

    // variable para el contexto
    Context classContext;

    private CharSequence obtenerTextMensaje(Intent intent){
        Log.d(TAG, "-> obtenerTextMensaje()");
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
        }
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "-> onReceive()");
        // id de notificacion de voz
        int notificationId = 1;

        classContext = context;

        // se obtiene la secuencia de caracteres del intent que se disparo por Google Now
        String remoteInput = obtenerTextMensaje(intent) == null ? "" : obtenerTextMensaje(intent).toString();
        if(intent.getAction().equalsIgnoreCase("edu.training.wearbountyhunter.VOZ")) {
            if (remoteInput.length() > 0) {
                // se extrae el comando
                String comando = remoteInput.substring(0, 9);
                if (comando.equalsIgnoreCase("DESCARGAR")) {
                    // se extrae el filtro
                    String filtro = remoteInput.substring(10);

                    NetServices oNS = new NetServices(new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(Object feed) {
                            try {
                                if (((String[]) feed).length > 0) {
                                    NotificacionesBuilder notificacionesBuilder = new NotificacionesBuilder(classContext);
                                    notificacionesBuilder.notificacionImportacion((String[]) feed);
                                    Home.UpdateLists(0);
                                } else {
                                    NotificacionesBuilder notificacionesBuilder = new NotificacionesBuilder(classContext);
                                    notificacionesBuilder.notificacionGeneral("Warning PULL", "Sin coincidencias en la nube.");
                                }
                            } catch (Exception ex) {
                                Log.d("[EXCEPTION]", "Error al intentar actualizar las listas de fugitivos " + ex.getMessage());
                            }
                        }

                        @Override
                        public void onTaskError(Object feed) {
                            Toast.makeText(classContext, "Error al comunicarse con el WebService!!!", Toast.LENGTH_SHORT).show();
                        }
                    }, context);
                    oNS.execute("FugitivosFiltro", filtro);
                }
            }
        }
        if (intent.getAction().equalsIgnoreCase("edu.training.wearbountyhunter.ALARMA")){
            notificationId = 4;
            // se extrae el comando
            String comando = remoteInput;
            if (comando.equalsIgnoreCase("encender")) {
                NetServices oNS = new NetServices(new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Object feed) {
                        Toast.makeText(classContext, "Exito: " + feed.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTaskError(Object feed) {
                        Toast.makeText(classContext, "Error al comunicarse con el WebService!!!", Toast.LENGTH_SHORT).show();
                    }
                }, context);
                oNS.execute("SignalR", comando);
            }
        } else{
            Toast.makeText(context, "Comando incorrecto", Toast.LENGTH_SHORT).show();
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(notificationId);

    }
}
