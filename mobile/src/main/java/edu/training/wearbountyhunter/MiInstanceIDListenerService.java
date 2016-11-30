package edu.training.wearbountyhunter;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by brachialste on 30/11/16.
 */

public class MiInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "ListenerService";

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "-> onTokenRefresh()");
        NetServices oNS = new NetServices(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object feed) {
                Toast.makeText(getApplicationContext(), feed.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskError(Object feed) {
                Toast.makeText(getApplicationContext(), feed.toString(), Toast.LENGTH_LONG).show();
            }
        }, this);
        oNS.execute("RegistroActualizacion");
    }
}
