package edu.training.wearbountyhunter;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by brachialste on 29/11/16.
 */

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // se reliza el registro ante GCM
        registrarAGCM();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    private void registrarTopicos(){
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
        oNS.execute("registrarTopicos");
    }

    private void registrarAGCM(){
        String token = Utilidades.obtenerToken(this);
        if(token != "" && token != null){
            Toast.makeText(this, "Ya se cuenta con un registro en GCM", Toast.LENGTH_SHORT).show();
            return;
        }

        NetServices oNS = new NetServices(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object feed) {
                Toast.makeText(getApplicationContext(), feed.toString(), Toast.LENGTH_LONG).show();
                registrarTopicos();
            }

            @Override
            public void onTaskError(Object feed) {
                Toast.makeText(getApplicationContext(), feed.toString(), Toast.LENGTH_LONG).show();
            }
        }, this);

        oNS.execute("Registro");
    }

}
