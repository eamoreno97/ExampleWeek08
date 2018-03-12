package com.example.andresacosta.exampleweek08;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static String TAG = "ElTag";
    private Context context;
    private List<Usuario> usuariosList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        lv = findViewById(R.id.listView);
    }

    public boolean verificarRed() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            //Toast.makeText(this, "Network is Available", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "Network not Available", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void onClickVerificarRed(View view) {
        if(verificarRed() == true){
            new GetData().execute();
            //sendRequest();
        }
    }

    public void sendRequest(){

    }

    protected static String getData(){
        Log.d(TAG,"get");
        String response = null;
        try {
            URL url = null;
            url = new URL("https://api.randomuser.me/?results=25&format=jason");
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine())!=null) {
                //Log.d(TAG, inputLine);
                response = inputLine;
            }
            in.close();
            return response;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    protected static void send(){

    }

    private class GetData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String response = getData();
            usuariosList = new ArrayList<Usuario>();
            if (response != null){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray usuarios = jsonObject.getJSONArray("results");
                    Log.d(TAG,"response "+usuarios.length());
                    for (int i=0;i<usuarios.length();i++){
                        JSONObject c = usuarios.getJSONObject(i);
                        JSONObject name = c.getJSONObject("name");
                        Log.d(TAG,"name "+i+" "+name.getString("first"));
                        usuariosList.add(new Usuario(name.getString("first"),name.getString("last")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG,"response is null");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d(TAG,"usuariosList "+usuariosList.size());
            ArrayAdapter<Usuario> adapter = new ArrayAdapter<Usuario>(context, android.R.layout.simple_list_item_1, android.R.id.text1, usuariosList);
            lv.setAdapter(adapter);
        }
    }
}
