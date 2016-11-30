package edu.training.wearbountyhunter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by brachialste on 29/11/16.
 */

public class AgregarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setTitle("Nuevo fugitivo");
        setContentView(R.layout.activity_agregar);

    }

    public void onSaveClick(View view) {
        TextView oTxtN = (TextView)this.findViewById(R.id.txtNew);
        if(!oTxtN.getText().toString().isEmpty())
        {
            //Se genera la instancia de base de datos para guardar el dato...
            Home.oDB.InsertFugitivo(oTxtN.getText().toString());
            setResult(0);
            finish();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Alerta")
                    .setMessage("Favor de capturar el nombre del fugitivo")
                    .show();
        }
    }
}