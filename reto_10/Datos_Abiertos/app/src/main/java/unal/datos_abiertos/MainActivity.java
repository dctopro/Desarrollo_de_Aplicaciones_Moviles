package unal.datos_abiertos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {
    private static final String[] departamentos = {"Amazonas","Antioquia","Arauca","Archipiélago de San Andrés, Providencia y Santa Catalina",
            "Atlántico","Bogotá, D. C.", "Bolívar","Boyacá", "Caldas","Caquetá","Casanare","Cauca","Cesar","Chocó",
            "Córdoba", "Cundinamarca","Guaviare","Huila","La Guajira","Magdalena","Meta", "Nariño",
            "Norte de Santander","Quindío", "Risaralda","Santander","Sucre","Tolima","Valle del Cauca","Vaupés","Vichada","No disponible"
    };//Guaviare
    private static final String[][] departamentos_values={
            {"Amazonas"},{"Antioquia"},{"Arauca"},{"Archipiélago de San Andrés, Providencia y Santa Catalina"},
            {"Atlántico"},{"Bogotá, D. C."},{"Bolívar"},{"Boyacá"},{"Caldas"},{"Caquetá"},{"Casanare"},{"Cauca"},{"Cesar"},{"Chocó"},
            {"Córdoba"},{"Cundinamarca"},{"Guaviare"},{"Huila"},{"La Guajira"},{"Magdalena"},{"Meta"},{"Nariño"},
            {"Norte de Santander"},{"Quindío"},{"Risaralda"},{"Santander"},{"Sucre"},{"Tolima"},{"Valle del Cauca"},{"Vaupés"},{"Vichada"},{"No disponible"}
    };
    private static final String[] categorias={"A","A1","B","C","D","Reconocido"};
    private static final int CONNECTION_TIMEOUT = 60000;//tiempos de espera de coneccion y de peticion
    private static final int DATARETRIEVAL_TIMEOUT = 60000;

    private static final String uribase = "https://www.datos.gov.co/resource/hrhc-c4wu.json?";
    RecyclerView recyclerView;

    Context context;
    ProgressDialog pd;
    Button busquedaButton, departamentoButton, categoriaButton;
    ItemRecyclerViewAdapter itemAdapter;
    ArrayList<ItemData> data = new ArrayList<>();
    String departamento = departamentos[0], categoria = categorias[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        AlertDialog.Builder departamentoPopup = new AlertDialog.Builder(this);
        AlertDialog.Builder categoriaPopup = new AlertDialog.Builder(this);

        departamentoPopup.setTitle("Escoge el departamento que deseas consultar");
        categoriaPopup.setTitle("Escoge la categoria que deseas consultar");

        int checkedItem = 0;
        departamentoPopup.setSingleChoiceItems(departamentos, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                departamento = departamentos[which];
                departamentoButton.setText(departamento);
            }
        });

        departamentoPopup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });

        departamentoPopup.setNegativeButton("Cancel", null);

        categoriaPopup.setSingleChoiceItems(categorias, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                categoria = categorias[which];
                categoriaButton.setText(categoria);
            }
        });

        categoriaPopup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });

        categoriaPopup.setNegativeButton("Cancel", null);

        AlertDialog departamentoDialog = departamentoPopup.create();
        AlertDialog categoriaDialog = categoriaPopup.create();

        departamentoButton = findViewById(R.id.departamento);
        departamentoButton.setOnClickListener(v -> departamentoDialog.show());

        categoriaButton = findViewById(R.id.categoria);
        categoriaButton.setOnClickListener(v -> categoriaDialog.show());

        busquedaButton = findViewById(R.id.buscar);
        busquedaButton.setOnClickListener(v -> new JsonTask().execute());
    }

    public static JSONArray requestWebService(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)  urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // handle unauthorized (if service requires user login)
                System.out.println("Error de autorización");
            }

            else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
                System.out.println("Error Miscelaneo");
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return new JSONArray(getResponseText(in));
        }

        catch (MalformedURLException e) { System.out.println("URL is invalid"); }
        catch (SocketTimeoutException e) { System.out.println("data retrieval or connection timed out"); }
        catch (IOException e) { System.out.println("could not read response body"); }
        catch (JSONException e) { System.out.println("response body is no valid JSON string"); }
        finally { if (urlConnection != null) { urlConnection.disconnect(); } }
        return null;
    }

    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    public void getData(String uribase, String query) {
        JSONArray serviceResult = requestWebService(uribase + query);
        data.clear();

        try {
            //if(!serviceResult.toString().equals("[]")) {
            for (int i = 0; i < serviceResult.length(); i++) {
                JSONObject d = serviceResult.getJSONObject(i);

                data.add(
                        new ItemData(
                                d.getString("nme_grupo_gr").toString(),
                                d.getString("cod_grupo_gr").toString(),
                                d.getString("inst_aval").toString()
                        )
                );
            }
            //}
        }

        catch (JSONException e) { Log.e("Error", e.toString()); }
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            //departamento = "'" +  departamento + "'";
            //String query = "$select=cod_grupo_gr,nme_grupo_gr,inst_aval&$where=nme_departamento_gr in ('" + departamento + "') and nme_clasificacion_gr in ('" + categoria + "')";
            String query="$select=cod_grupo_gr,nme_grupo_gr,inst_aval&$where=nme_departamento_gr%20in%20(%22"+ departamento +"%22)%20and%20nme_clasificacion_gr%20in%20(%22"+categoria+"%22)";
            try { getData(uribase, query); }
            catch (Throwable e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) { pd.dismiss(); }
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            itemAdapter = new ItemRecyclerViewAdapter(context, data);
            recyclerView.setAdapter(itemAdapter);
        }
    }
}