package edu.training.wearbountyhunter;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by brachialste on 30/11/16.
 */

public class MainActivity extends WearableActivity implements WearableListView.ClickListener {

    // elementos para llenar la lista
    String [][] elements;
    // indica si la lista tendra fugitivos o capturados
    int iModo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // se obtiene el extra modo que indica si la lista
        actualizarListas();
        // si la lista esta vacia se lanzara un mensaje con el error
        if(elements.length <= 0){
            mensajeListasVacias();
        }
    }

    public void obtenerFugitivos(int estatus){
        elements = Principal.oDB.ObtenerFugitivos(estatus==1);
    }

    public void mensajeListasVacias(){
        String mensaje;
        if(iModo == 0){
            mensaje = "No hay fugitivos";
        }else{
            mensaje = "No hay capturados";
        }
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, mensaje);
        startActivity(intent);
        finish();
    }

    private void actualizarListas(){
        // obtenemos la lista de nuestra activity
        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);
        // se obtienen los registros de la BD
        obtenerFugitivos(iModo);
        // se asigna el adapter los elementos
        listView.setAdapter(new Adapter(this, elements, iModo));
        // se añade a la lista el listener impĺementado en la clase actual
        listView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Integer tag = (Integer) viewHolder.itemView.getTag();
        // use this data to complete some action
        TextView txtName = (TextView) viewHolder.itemView.findViewById(R.id.name);
        String texto = txtName.getText().toString();
        String id = txtName.getTag().toString();

        Intent intent = new Intent(this, ActivityDetalle.class);
        intent.putExtra("modo", iModo);
        intent.putExtra("nom", texto);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == 10){
            // se actualizan la lista
            actualizarListas();
            // si la lista esta vacia se manda un mensaje informando el error
            if(elements.length <= 0){
                mensajeListasVacias();
            }
        }
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "No hay elemento.", Toast.LENGTH_SHORT).show();
    }
}
