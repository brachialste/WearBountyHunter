package edu.training.wearbountyhunter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircularButton;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by brachialste on 30/11/16.
 */

public class Principal extends WearableActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static DBProvider oDB;
    private static CircularButton btnFugitivo, btnCapturado;
    private static TextView txtFugitivo, txtCapturado;
    private static ImageButton btnAgregar;

    private static TextView txtTemperatura;
    private static final String TEMP_KEY = "dwtraining.mx.weartemperature";
    private GoogleApiClient mGoogleApiClient;

    public Principal() {
        oDB = new DBProvider(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
//        setContentView(R.layout.activity_menu);
        setContentView(R.layout.activity_grid);

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new GridPageAdapter(getFragmentManager()));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        /*
        // se obtienen los controles para controlar el modo ambiente
        btnAgregar = (ImageButton) findViewById(R.id.ibtnAgregar);
        txtFugitivo = (TextView) findViewById(R.id.txtFugitivos);
        txtCapturado = (TextView) findViewById(R.id.txtCapturados);
        btnCapturado = (CircularButton) findViewById(R.id.btnAzul);
        btnFugitivo = (CircularButton) findViewById(R.id.btnRojo);

        btnAgregar.setScaleX(1.3f);
        btnAgregar.setScaleY(1.3f);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Conexion Suspendida", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Conexion Fallida", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for(DataEvent event : dataEventBuffer){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                // DataItem changed
                DataItem item = event.getDataItem();
                if(item.getUri().getPath().compareTo("/temperatura") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    actualizarEtiqueta(dataMap.getString(TEMP_KEY));
                }
            }else if(event.getType() == DataEvent.TYPE_DELETED){
                // dataitem deleted
            }
        }
    }

    public static class MenuFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            View iView = inflater.inflate(R.layout.activity_menu, container, false);

            // se obtienen los controles para controlar el modo ambiente
            btnAgregar = (ImageButton) iView.findViewById(R.id.ibtnAgregar);
            txtFugitivo = (TextView) iView.findViewById(R.id.txtFugitivos);
            txtCapturado = (TextView) iView.findViewById(R.id.txtCapturados);
            btnCapturado = (CircularButton) iView.findViewById(R.id.btnAzul);
            btnFugitivo = (CircularButton) iView.findViewById(R.id.btnRojo);

            btnAgregar.setScaleX(1.3f);
            btnAgregar.setScaleY(1.3f);

            return iView;
        }
    }

    public static class TemperaturaFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            // se hace referencia al Fragment generado por XML en los Layouts y
            // se instancia en una View
            View iView = inflater.inflate(R.layout.activity_temperatura, container, false);

            txtTemperatura = (TextView) iView.findViewById(R.id.txtTemperatura);

            return iView;
        }
    }

    public class GridPageAdapter extends FragmentGridPagerAdapter {

        public GridPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getFragment(int row, int column) {
            if(row == 0){
                return new MenuFragment();
            }else{
                return new TemperaturaFragment();
            }
        }

        @Override
        public int getRowCount() {
            return 2;
        }

        @Override
        public int getColumnCount(int i) {
            return 1;
        }
    }

    public void actualizarEtiqueta(String temperatura){
        try{
            txtTemperatura.setText(temperatura + " °C");
        }catch (Exception e){

        }
    }
}
