package fr.eisti.icc.PFE_appli;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

        utils = new Utils(getApplicationContext());


        new RetreiveFeedTask().execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... params) {
            Map<String,String> tmp = new HashMap();
            tmp.put("phone_number", utils.getPhoneNumber());
            JSONObject json = new JSONObject(tmp);
            HttpResponse response = utils.getRequest("/devices/available",
                    json);

            Log.i("RESPONSE","GOT IT");
                while(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                }
            Log.i("LOOP OVER","LOOP OVER");

            JSONArray json2 = null;
            try {
                json2 = new JSONArray(EntityUtils.toString(response
                        .getEntity
                                ()));
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            JSONObject tmp_json;
            for(int i=0; i<json2.length(); i++){
                try {
                    tmp_json = json2.getJSONObject(i);
                    Log.i("GET RESPONSE", tmp_json.getString("ip") + "  " +
                            tmp_json.getString("phone_number"));
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }

            return null;
        }
    }


}
