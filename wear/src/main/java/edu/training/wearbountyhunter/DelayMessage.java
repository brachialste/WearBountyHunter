package edu.training.wearbountyhunter;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brachialste on 30/11/16.
 */

public class DelayMessage extends Activity
        implements DelayedConfirmationView.DelayedConfirmationListener {

    private DelayedConfirmationView mDelayedView;
    public String fugitivo;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delay_message);
        // se obtiene la referencia del control de delayedconfirmation de la ui
        mDelayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        // se setea el listener al control
        mDelayedView.setListener(this);

        displaySpeechRecognizer();
    }

    private void displaySpeechRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // arrancamos el intent esperando un resultado
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onTimerFinished(View view) {
        // cuando el usuario no calcela la accion y se ejecuta
        Principal.oDB.InsertFugitivo(fugitivo);

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "Agregado con exito");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onTimerSelected(View view) {
        // cuando el usuario aborta la accion
        mDelayedView.reset();

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "Tarea cancelada");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK){
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // se recupera la secuencia de caracteres que se obtienen de las capacidades de voz
            fugitivo = spokenText;
            TextView txtContenido = (TextView) findViewById(R.id.lblContenido);

            if(fugitivo.length() > 12){
                txtContenido.setText("Agregando a " + fugitivo.substring(0,11) + "...");
            }else{
                txtContenido.setText("Agregando a \"" + fugitivo + "\"");
            }

            // tres segundos para cancelar la accion
            mDelayedView.setTotalTimeMs(3000);
            // arrancar el timmer
            mDelayedView.start();

        }else{
            finish();
        }
    }
}
