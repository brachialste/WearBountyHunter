package edu.training.wearbountyhunter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by brachialste on 29/11/16.
 */

public class DetalleActivity extends AppCompatActivity {

    String sID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Se obtiene la informacion del intent...
        Bundle oExt = this.getIntent().getExtras();
        //Se pone el nombre del fugitivo como titulo...
        this.setTitle(oExt.getString("title") + " - [" + oExt.getString("id") + "]");
        this.sID = oExt.getString("id");
        setContentView(R.layout.activity_detalle);
        TextView oMsg = (TextView)this.findViewById(R.id.lblMsg);
        //Se identifica si es fugitivo o capturado para el mensaje...
        if(oExt.getInt("mode") == 0)
            oMsg.setText("El fugitivo sigue suelto...");
        else {
            Button oBtn1 = (Button)this.findViewById(R.id.btnCap);
            oBtn1.setVisibility(View.GONE);
            oMsg.setText("Atrapado!!!");
        }
    }

    public void onDeleteClick(View view) {
        //Se utiliza la instancia de la base de datos para eliminar el registro...
        Home.oDB.DeleteFugitivo(sID);
        setResult(0);
        finish();
    }

    public void onCaptureClick(View view) {
        //Se utiliza la instancia de la base de datos para eliminar el registro...
        Home.oDB.UpdateFugitivo("1", sID);
        setResult(0);
        finish();
    }
}
