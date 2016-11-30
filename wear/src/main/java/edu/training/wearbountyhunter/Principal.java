package edu.training.wearbountyhunter;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircularButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by brachialste on 30/11/16.
 */

public class Principal extends WearableActivity {

    public static DBProvider oDB;
    private CircularButton btnFugitivo, btnCapturado;
    private TextView txtFugitivo, txtCapturado;
    private ImageButton btnAgregar;

    public Principal() {
        oDB = new DBProvider(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.activity_menu);

        // se obtienen los controles para controlar el modo ambiente
        btnAgregar = (ImageButton) findViewById(R.id.ibtnAgregar);
        txtFugitivo = (TextView) findViewById(R.id.txtFugitivos);
        txtCapturado = (TextView) findViewById(R.id.txtCapturados);
        btnCapturado = (CircularButton) findViewById(R.id.btnAzul);
        btnFugitivo = (CircularButton) findViewById(R.id.btnRojo);

        btnAgregar.setScaleX(1.3f);
        btnAgregar.setScaleY(1.3f);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        Log.d("[Ambiente]", "onEnterAmbient");
        btnCapturado.setVisibility(View.GONE);
        btnFugitivo.setVisibility(View.GONE);
        txtCapturado.setAlpha(40f / 100);
        txtFugitivo.setAlpha(40f / 100);
        btnAgregar.setImageAlpha(30);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        Log.d("[Ambiente]", "onExitAmbient");
        btnCapturado.setVisibility(View.VISIBLE);
        btnFugitivo.setVisibility(View.VISIBLE);
        txtCapturado.setAlpha(1f);
        txtFugitivo.setAlpha(1f);
        btnAgregar.setImageAlpha(255);
    }

    public void mostrarFugitivos(View view){
        Intent fugitivos = new Intent(this, MainActivity.class);
        fugitivos.putExtra("modo", 0);
        startActivity(fugitivos);
    }

    public void mostrarCapturados(View view) {
        Intent fugitivos = new Intent(this, MainActivity.class);
        fugitivos.putExtra("modo", 1);
        startActivity(fugitivos);
    }

    public void agregar(View view){
        Intent delay = new Intent(this, DelayMessage.class);
        startActivity(delay);
    }

}
