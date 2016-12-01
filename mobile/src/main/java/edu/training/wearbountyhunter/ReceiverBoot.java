package edu.training.wearbountyhunter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by brachialste on 1/12/16.
 */

public class ReceiverBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, ServicioTemperatura.class);
        if(!ServicioTemperatura.isRunning()) context.startService(serviceIntent);
    }
}
