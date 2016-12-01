package edu.training.wearbountyhunter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by brachialste on 1/12/16.
 */

public class ServicioTemperatura extends Service {

    // Instancia del servicio en memoria
    private static ServicioTemperatura instance = null;
    // propiedad del timer para ejecutar prolongadamente
    private Timer mTimer = null;
    // constante de tipo llave para el MapRequest de Google Play Services
    private static final String TEMP_KEY = "dwtraining.mx.weartemperature";
    // propiedad de tipo string para almacenamiento en memoria de la temperatura leida
    private String sTemperatura = "";
    // propiedad de tipo Handler para hacer el puente entre el hilo secuendario y el de UI
    final Handler myHandler = new Handler();
    // propiedad de tipo GoogleApiClient para establecer la comunicación por Google Play Service
    GoogleApiClient mGoogleApiClient;
    // Instancia de Interfaz runnable para utilizar con el puente para actualizar la UI
    final Runnable myRunnable = new Runnable(){
        public void run(){
            try{
                Home.txtTemperatura.setText(sTemperatura + " °C");
            }catch (Exception ex){
            }
        }
    };

    @Override
    public void onCreate() {
        Toast.makeText(this, "Servicio creado", Toast.LENGTH_SHORT).show();
        instance = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("[HANDHELD]", "onConnected: " + bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("[HANDHELD]", "onConnectionSuspended: " + i);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("[HANDHELD]", "onConnectionFailed: " + connectionResult);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    public static boolean isRunning(){
        return instance != null;
    }

    private void actualizarEtiqueta(){
        myHandler.post(myRunnable);
    }

    private void sincronizarTemperatura(){
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/temperatura");
        putDataMapRequest.getDataMap().putString(TEMP_KEY, sTemperatura);
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    public void obtenerTemperatura(){
        NetServices ons = new NetServices(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object feed) {
                sTemperatura = feed.toString();
                actualizarEtiqueta();
                sincronizarTemperatura();
            }

            @Override
            public void onTaskError(Object feed) {
                Log.e("[Service]", "error: " + feed.toString());
            }
        }, this);
        ons.execute("Temperatura");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Servicio arrancado " + startId, Toast.LENGTH_SHORT).show();
        this.mTimer = new Timer();
        this.mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                obtenerTemperatura();
            }
        }, 0, 1000 * 1);

        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio detenido", Toast.LENGTH_SHORT).show();
        instance = null;
        mGoogleApiClient.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
