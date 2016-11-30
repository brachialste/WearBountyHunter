package edu.training.wearbountyhunter;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by brachialste on 30/11/16.
 */

public class ActivityDetalle extends WearableActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Bundle extras = this.getIntent().getExtras();

        TextView txtTitulo = (TextView) findViewById(R.id.txtTitulo);
        TextView txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);
        ImageButton btnCapturar = (ImageButton) findViewById(R.id.btnCapturar);

        txtTitulo.setText(extras.getString("nom"));
        this.id = extras.getString("id");
        if(extras.getInt("modo") == 0){
            txtDescripcion.setText("El figitivo esta suelto...");
        }else{
            txtDescripcion.setText("Atrapado!!!");
            btnCapturar.setVisibility(View.GONE);
        }
    }

    public void mensaje(String mensaje){
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mensaje);
        startActivityForResult(intent, 0);
    }

    public void capturar(View view){
        Principal.oDB.UpdateFugitivo("1", id);
        mensaje("Capturado con exito");
    }

    public void eliminar(View view){
        Principal.oDB.DeleteFugitivo(id);
        mensaje("Eliminado con exito");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            setResult(10);
            finish();
        }
    }
}
