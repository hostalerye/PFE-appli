package fr.eisti.icc.PFE_appli;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMBroadcastReceiver;
import com.google.android.gcm.GCMRegistrar;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Menu extends Activity{

    Utils utils;

    private String launchBenchmark(CharSequence bench_name){
        if(bench_name.equals("Pi")) {

        } else if(bench_name.equals("Matrix")) {

        } else if (bench_name.equals("Bench")) {

        }

        return "";
    }

    private void launchButtonSetUp(){
        final Button launchButton = (Button)findViewById(R.id.launchButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder
                        (Menu.this);

                final CharSequence[] items = getResources()
                        .getStringArray(R.array.launchArray);

                builder.setTitle(getString(R.string.launch_dialog_title))
                        .setItems(items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        String result = launchBenchmark(items[item]);
                                        Toast.makeText(Menu.this,
                                                result, Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void pingButtonSetUp(){
        final Button pingButton = (Button)findViewById(R.id.pingButton);
        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String,Void,String> question = new AskInfos();
                question.execute();

                String ping_id = "";
                try {
                    ping_id = question.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                AsyncTask<String,Void,JSONArray> task = new GetInfos();
                task.execute(ping_id);
                try {
                    JSONArray array = task.get();
                    if(array != null){
                        JSONObject json;
                        for(int i=0; i < array.length(); i++){
                            json = array.getJSONObject(0);
                            Log.i("PING RESPONSE", json.getString("ip") + "    " + json
                                    .getString("phone_number"));
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e("INTERRUPTED TASK", "Can't get JSONArray response");
                } catch (ExecutionException e) {
                    Log.e("EXECUTION TASK", "Can't get JSONArray response");
                } catch (JSONException e) {
                    Log.e("JSON TASK","Can't get JSONObject from array");
                }
            }
        });
    }

    private void phoneButtonSetUp(){
        final Button phoneButton = (Button) findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);

                // Set dialog layout
                builder.setView(getLayoutInflater().inflate(
                        R.layout.dialog_phone_number,
                        null, false));

                // Set dialog buttons
                builder.setPositiveButton(R.string.register,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Needed for context
                                Dialog d = (Dialog) dialog;
                                EditText edit = (EditText)d.findViewById(R
                                        .id
                                        .phoneNumber);
                                String phoneNumber = edit.getText().toString()
                                        .trim();

                                // Write to internal storage
                                String filename = getString(R.string.filename);
                                try{
                                    FileOutputStream fos = openFileOutput(filename,
                                            Context.MODE_PRIVATE);
                                    fos.write(phoneNumber.getBytes());
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                    Log.d("DEBUG","Can't create File!");
                                } catch (IOException e) {
                                    Log.d("DEBUG","Can't make file output " +
                                            "stream!");
                                }
                            }
                        });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    public void registerDevice(){
        Intent intent = new Intent(Menu.this, GCMIntentService.class);
        startService(intent);

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);

        if (regId.equals("")) {
            Log.i("GCM", "Trying register with " + getString(R.string
                    .senderId));
            GCMRegistrar.register(this, getString(R.string.senderId));
        } else {
            Log.i("REGISTER", "Already registered");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        launchButtonSetUp();
        phoneButtonSetUp();
        registerDevice();
        pingButtonSetUp();

        utils = new Utils(getApplicationContext());

    }

    class AskInfos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String value = "";
            if(params.length != 0){
                value = params[0];
            }

            HttpResponse response = utils.getRequest("/devices/available",
                    "phone_number",value);

            String result = "";
            if(response.getStatusLine().getStatusCode() == HttpStatus
                    .SC_OK){
                try {
                    result = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            return result;
        }
    }

    class GetInfos extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            String value = "";
            if(params.length != 0){
                value = params[0];
            }

            HttpResponse response = utils.getRequest("/devices/infos",
                    "ping_id",value);

            ResponseHandler<String> handler = new BasicResponseHandler();

            JSONArray array = null;
            try {
                array = new JSONArray(handler.handleResponse(response));
            } catch (JSONException e) {
                Log.e("PING RESPONSE JSON", "Can't parse JSONArray");
            } catch (IOException e) {
                Log.e("PING RESPONSE IO", "Can't parse JSONArray");
            }

            return array;
        }
    }
}
