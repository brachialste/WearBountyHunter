package edu.training.wearbountyhunter;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by brachialste on 29/11/16.
 */

public class Utilidades {

    public static String obtenerRegistrationTokenEnGcm(Context context) throws Exception{
        InstanceID instanceID = InstanceID.getInstance(context);
        String token = instanceID.getToken("450248823008", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        return token;
    }

    public static void subscribeTopics(String token, String[] TOPICS, Context context) throws IOException{
        com.google.android.gms.gcm.GcmPubSub pubStub = GcmPubSub.getInstance(context);
        for(String topic : TOPICS){
            pubStub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public static String dameIMEI(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static void savePreferences(String identificador, String token, Context context){
        SharedPreferences appPrefs;
        appPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        appPrefs.edit().putString("Identificador", identificador).apply();
        appPrefs.edit().putString("sToken", token).apply();
    }

    public static String obtenerIdentificador(Context context){
        SharedPreferences appPrefs;
        appPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        if(appPrefs.contains("Identificador"))
            return appPrefs.getString("Identificador", "0");

        return "";
    }

    public static String obtenerToken(Context context){
        SharedPreferences appPrefs;
        appPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        if(appPrefs.contains("sToken"))
            return appPrefs.getString("sToken", "");

        return "";
    }
}
