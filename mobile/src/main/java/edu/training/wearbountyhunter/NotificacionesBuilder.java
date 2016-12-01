package edu.training.wearbountyhunter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.text.Html;
/**
 * Created by brachialste on 29/11/16.
 */

public class NotificacionesBuilder {

    private static final String EXTRA_VOICE_REPLY = "EXTRA_RESPUESTA";

    String replyLabel = "Comando";

    RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
            .setLabel(replyLabel)
            .build();

    // Respuestas que se encuentran en el archivo de strings
    String[] respuestas;
    // Input remoto para voz con opciones
    RemoteInput remoteInputAlarma;

    private Context context;

    public NotificacionesBuilder(Context context) {
        this.context = context;
        respuestas = context.getResources().getStringArray(R.array.respuestas);
        remoteInputAlarma = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(respuestas)
                .build();
    }

    // metodo para la notificacion enviada con capacidades para voz en el wear
    public void notificacionVoz(){
        int notificacionId = 1;

        // se define el texto en un formato extendido por medio del BigTextStyle para ser
        // añadido a la notificacion
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(Html.fromHtml("<h2>Responder con la siguiente estructura:</h2><br>" +
        "<b>Descargar (numero_fugitivos|nombre_figitivo|'todos')<b>"));

        // se crea el intent y el pending intent para el envio del anuncio global
        Intent replyIntent = new Intent("edu.training.wearbountyhunter.VOZ");
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(context, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // creacion de la accion para la respuesta y agregando en input remoto
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_btn_speak_now,
                        "Respuesta", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        // construccion de la notificacion y adicion de la accion mediante WereableExtender
        Notification notification =
                new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_input_get)
                .setContentTitle("Responder Comando")
                .setDefaults(Notification.DEFAULT_ALL)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .setStyle(bigTextStyle)
                .build();

        // se obtiene una instancia del NotificacionManagerCompat
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        // se lanza la notificacion
        notificationManagerCompat.notify(notificacionId, notification);
    }

    // metodo para la notificacion enviada cuando se importen los fugitivos
    public void notificacionImportacion(String[] fugitivos){
        int notificationId = 2;

        // se crea el intent y el pending intent para el envio del anuncio global
        Intent replyIntent = new Intent(context, Home.class);
        PendingIntent viewPending =
                PendingIntent.getActivity(context, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // se crea la tarjeta principal de la notificacion
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Fugitivos Importados")
                .setContentText("Detalle en 2da tarjeta")
                .setContentIntent(viewPending);

        String sfugitivos = "";
        for(int cont=0; cont<fugitivos.length; cont++){
            sfugitivos += fugitivos[cont] + "<br/>";
        }

        // creamos el estilo para el txto seleccionado
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Detalle")
                .bigText(Html.fromHtml("<h2>Los Importados fueron:</h2><br>" +
                "<b>" + sfugitivos + "<b>"));

        // se obtiene una instancia del NOtificationmanagerCompat
        Notification secondPageNotification =
                new NotificationCompat.Builder(context)
                .setStyle(secondPageStyle)
                .build();

        // se adjunta la pagina etendida a la notifiacion
        Notification notification = notificationBuilder
                .extend(new NotificationCompat.WearableExtender()
                .addPage(secondPageNotification))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        // se obtiene una instancia de NotificationManagerCompat
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);

        // se lanza la notificacion
        notificationManagerCompat.notify(notificationId, notification);

    }

    // metodo para enviar notificaciones generales
    public void notificacionGeneral(String titulo, String mensaje){
        int notificacionId = 3;

        // se crea la tarjeta principal de la notifiacion
        Notification notification =
                new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        // se crea una instancia de NotificationManagerCompat
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);

        // se lanza la notifiacion
        notificationManagerCompat.notify(notificacionId, notification);
    }

    // metodo para la notificacion enviada con capacidades para voz en el wear
    public void notificacionVozAlarma(String temperatura){
        int notificacionId = 4;

        // se define el texto en un formato extendido por medio del BigTextStyle para ser
        // añadido a la notificacion
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(Html.fromHtml("<h2>Alarma de Temperatura</h2><br>" +
                "<b>La temperatura subió por encima de " + temperatura + " °C<b>"));

        // se crea el intent y el pending intent para el envio del anuncio global
        Intent replyIntent = new Intent("edu.training.wearbountyhunter.ALARMA");
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(context, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // creacion de la accion para la respuesta y agregando en input remoto
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_lock_idle_charging,
                        "¿Encender?", replyPendingIntent)
                        .addRemoteInput(remoteInputAlarma)
                        .build();

        // construccion de la notificacion y adicion de la accion mediante WereableExtender
        Notification notification =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("Responder a Alarma")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .setStyle(bigTextStyle)
                        .build();

        // se obtiene una instancia del NotificacionManagerCompat
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        // se lanza la notificacion
        notificationManagerCompat.notify(notificacionId, notification);
    }
}
