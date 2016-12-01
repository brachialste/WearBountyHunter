package edu.training.wearbountyhunter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
/**
 * Created by brachialste on 29/11/16.
 */

public class NetServices extends AsyncTask<String, Void, Object> {

    private OnTaskCompleted listener;
    private Exception exception;

    //  Uri para la obtención de fugitivos via POST
    private static final String URL_WS_FUGITIVOS_CANTIDAD = "http://201.168.207.210/Services/wearLAB01.svc/fugitivosCantidad";
    // Uri para el registro del token en la nube
    private static final String URL_WS_REGISTRO = "http://201.168.207.210/GCM/Registro.svc/Registro";
    // Uri para el registro del topico en la nube
    private static final String URL_WS_REGISTRAR_TOPICO = "http://201.168.207.210/GCM/Topicos.svc/RegistroTopicos";
    // instancia de la base de datos para la inserción de fugitivos
    public static DBProvider oDBNetServices;
    // topicos
    public static String[] Topicos = { "wearBountyHunter" };
    // contexto de la interfaz de usuario
    Context contextUI;
    // Uri parala obtención de la temperatura via POST
    private static final String URL_WS_TEMPERATURA = "http://201.168.207.210/Services/wearLAB03.svc/ObtenerTemperatura";
    // Uri parala obtención de la temperatura via POST
    private static final String URL_WS_SIGNALR = "http://201.168.207.210/Services/SignalRConnection.svc/EncendidoMotor";

    public NetServices(OnTaskCompleted listener, Context context)
    {
        exception = null;
        this.listener = listener;
        oDBNetServices = new DBProvider(context);
        contextUI = context;
    }

    protected Object doInBackground(String... params) {
        Object x = null;
        String sResp = "";
        Boolean sExito = false;
        int iIdentificador = 0;
        if (params[0].equals("FugitivosFiltro")) {
            Log.d("[NETSERVICES]", "FugitivosFiltro");
            try{
                sResp = NetServices.connectFugitivosFiltro(URL_WS_FUGITIVOS_CANTIDAD, params[1]);
                String [] aFujs;
                // se obtienen los fujitivos del JSON
                JSONArray jsData = new JSONArray(sResp);
                aFujs = new String[jsData.length()];
                for(int i=0; i<jsData.length(); i++){
                    JSONObject joFuj = jsData.getJSONObject(i);
                    aFujs[i] = joFuj.getString("name");
                }
                if(aFujs != null){
                    for(int i=0; i< aFujs.length; i++){
                        NetServices.oDBNetServices.InsertFugitivo(aFujs[i]);
                    }
                }
                x = aFujs;
            }catch (Exception e){
                exception = e;
            }
        }else if(params[0].equals("Registro")){
            Log.d("[NETSERVICES]", "Registro");
            try{
                String IMEI = Utilidades.dameIMEI(contextUI);
                String registrationToken = Utilidades.obtenerRegistrationTokenEnGcm(contextUI);
                sResp = NetServices.connectRegistroGCM(URL_WS_REGISTRO, IMEI, "0", registrationToken);
                String sMsg = "";
                Log.d("[NETSERVICES]", "sResp = " + sResp);
                // se obtienen los fujitivos del JSON
                JSONObject jsData = new JSONObject(sResp);
                sMsg = jsData.getString("sMensaje");
                sExito = jsData.getBoolean("sExito");
                iIdentificador = jsData.getInt("iIdentificador");
                if(sExito) {
                    Log.d("[NETSERVICES]", "Exito");
                    Utilidades.savePreferences(Integer.toString(iIdentificador), registrationToken, contextUI);
                }else{
                    Log.e("[NETSERVICES]", "Fallo");
                }

                x = sMsg;
            }catch (Exception e){
                e.printStackTrace();
                exception = e;
            }
        }else if(params[0].equals("RegistroActualizacion")){
            Log.d("[NETSERVICES]", "RegistroActualizacion");
            try{
                String IMEI = Utilidades.dameIMEI(contextUI);
                String registrationToken = Utilidades.obtenerRegistrationTokenEnGcm(contextUI);
                sResp = NetServices.connectRegistroGCM(URL_WS_REGISTRO, IMEI, Utilidades.obtenerIdentificador(contextUI), registrationToken);
                String sMsg;
                // se obtienen los fujitivos del JSON
                JSONObject jsData = new JSONObject(sResp);
                sMsg = jsData.getString("sMensaje");
                sExito = jsData.getBoolean("sExito");
                iIdentificador = jsData.getInt("iIdentificador");
                if(sExito)
                    Utilidades.savePreferences(Integer.toString(iIdentificador), registrationToken, contextUI);

                x = sMsg;
            }catch (Exception e){
                exception = e;
            }
        }else if(params[0].equals("registrarTopicos")){
            Log.d("[NETSERVICES]", "registrarTopicos");
            try{
                Utilidades.subscribeTopics(Utilidades.obtenerToken(contextUI), Topicos, contextUI);
                String IMEI = Utilidades.dameIMEI(contextUI);
                sResp = NetServices.connectRegistrarTopico(URL_WS_REGISTRAR_TOPICO, IMEI);
                String sMsg;
                // se obtienen los fujitivos del JSON
                JSONObject jsData = new JSONObject(sResp);
                Log.v("[Respuesta_Reg_Topico]", jsData.toString());
                sMsg = jsData.toString();
                x = sMsg;
            }catch (Exception e){
                exception = e;
            }
        }else if(params[0].equals("Temperatura")){
            Log.d("[NETSERVICES]", "Temperatura");
            try{
                sResp = NetServices.connectTemperatura(URL_WS_TEMPERATURA);
                String[] aFlujs;
                // se obtienen los fujitivos del JSON
                JSONObject jaData = new JSONObject(sResp);
                x = jaData.getString("sTemperatura");
            }catch (Exception e){
                exception = e;
            }
        }else if(params[0].equals("SignalR")){
            Log.d("[NETSERVICES]", "SignalR");
            try{
                sResp = NetServices.connectSignalRMotor(URL_WS_SIGNALR, params[1]);
                // se obtienen los fujitivos del JSON
                JSONObject jaData = new JSONObject(sResp);
                x = jaData.getBoolean("bExito");
            }catch (Exception e){
                exception = e;
            }
        }

        return(x);
    }


    protected void onPostExecute(Object feed) {
        if(exception == null)
        {
            listener.onTaskCompleted(feed);
        }
        else
        {
            listener.onTaskError(exception.toString());
        }
    }

    private static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String connectFugitivosFiltro(String purl, String filtro) throws IOException{
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";

        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // parametros
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CantidadNombre", filtro);
            Log.d("[RequestJSON]", jsonObject.toString());
            String urlParameters = jsonObject.toString();
            writer.write(urlParameters);
            writer.flush();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                // a simplemJSON reposnse read
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = convertStreamToString(inputStream);
                Log.v("[CHECK]", result);
                sRes = result;

                inputStream.close();
            }


        }catch(Exception e){
            Log.v("[CHECK]", e.toString());
        }

        return sRes;
    }

    public static String connectRegistroGCM(String purl, String IMEI, String identificador, String token) throws IOException {
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";

        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // parametros
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("iIDDevice", identificador);
            jsonObject.put("sIMEI", IMEI);
            jsonObject.put("sToken", token);
            Log.d("[JSON]", jsonObject.toString());
            String urlParameters = jsonObject.toString();
            writer.write(urlParameters);
            writer.flush();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                // a simplemJSON reposnse read
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = convertStreamToString(inputStream);
                Log.v("[CHECK]", result);
                sRes = result;

                inputStream.close();
            }else{
                Log.e("[CHECK]", "No respondio");
            }


        }catch(Exception e){
            Log.v("[CHECK]", e.toString());
        }

        return sRes;
    }

    public static String connectRegistrarTopico(String purl, String IMEI) throws IOException {
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";

        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // parametros
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            JSONArray jarray = new JSONArray();
            for(String topic:Topicos){
                jarray.put(topic);
            }
            jsonObject.put("sIMEI", IMEI);
            jsonObject.put("sTopicos", jarray);
            Log.d("[JSON]", jsonObject.toString());
            String urlParameters = jsonObject.toString();
            writer.write(urlParameters);
            writer.flush();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                // a simplemJSON reposnse read
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = convertStreamToString(inputStream);
                Log.v("[CHECK]", result);
                sRes = result;

                inputStream.close();
            }


        }catch(Exception e){
            Log.v("[CHECK]", e.toString());
        }

        return sRes;
    }

    public static String connectTemperatura(String purl) throws IOException{
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";
        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setConnectTimeout(500);
            httpURLConnection.setReadTimeout(500);
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                // a simplemJSON reposnse read
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = convertStreamToString(inputStream);
                Log.v("[CHECK]", result);
                sRes = result;

                inputStream.close();
            }

        }catch(Exception e){
            Log.v("[CHECK]", e.toString());
        }

        return sRes;
    }

    public static String connectSignalRMotor(String purl, String comando) throws IOException {
        URL url = new URL(purl);
        URLConnection urlConnection = url.openConnection();
        String sRes = "";

        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            // parametros
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("banderaEncendido", comando);
            Log.d("[RequestJSON]", jsonObject.toString());
            String urlParameters = jsonObject.toString();
            writer.write(urlParameters);
            writer.flush();

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                // a simplemJSON reposnse read
                InputStream inputStream = httpURLConnection.getInputStream();
                String result = convertStreamToString(inputStream);
                Log.v("[CHECK]", result);
                sRes = result;

                inputStream.close();
            }


        }catch(Exception e){
            Log.v("[CHECK]", e.toString());
        }

        return sRes;
    }
}
