package fr.eisti.icc.PFE_appli;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class Utils {

    Context context;
    SharedPreferences sh;

    float BATTERY_ALERT = 14;

    public Utils(Context context){
        this.context = context;
        sh = context.getSharedPreferences(
                context.getString(R.string.sharedPref), 0);

    }

    public String getRegId(){
        return sh.getString("registrationId","");
    }

    public void setRegId(String registrationId){
        sh.edit().putString("registrationId",registrationId).apply();
    }

    public void removeRegId(){
        sh.edit().remove("registrationId").apply();
    }

    public float getBatteryLevel(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent battery = context.registerReceiver(null, ifilter);

        int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

        return (level/(float)scale);
    }

    public void setCheckBoxState(boolean state){
        sh.edit().putBoolean("checkbox_state", state).apply();
    }

    public boolean getCheckBoxState(){
        return sh.getBoolean("checkbox_state",false);
    }

    public void removeCheckBoxState(){
        sh.edit().remove("checkbox_state").apply();
    }

    public String getIP(){
        String sAddr = "";
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for(NetworkInterface intf : interfaces){
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        sAddr = addr.getHostAddress().toUpperCase();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("INET SOCKET","Can't connect to socket to determine IP " +
                    "adress");
        }

        return sAddr;
    }

    public String getPhoneNumber(){
        String result = "";
        // Read phoneNumber from internal storage
        try {
            FileInputStream fis = context.openFileInput(context
                    .getResources().getString(R
                            .string
                            .filename));
            InputStreamReader reader = new
                    InputStreamReader(fis);
            BufferedReader buff = new BufferedReader(
                    reader);
            result = buff.readLine();
            buff.close();
            reader.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFound","Can't find config file");
        } catch (IOException e){
            Log.e("IO","Can't open config file");
        }

        return result;
    }

    public HttpResponse postRequest(String url, JSONObject json){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(context.getResources().getString(R.string
                .nodeServer)
                + url);

        try {
            // Add the JSON to the POST request
            StringEntity se = new StringEntity(json.toString());
            post.setEntity(se);
            post.setHeader("Content-type","application/json");
            post.setHeader("Accept","application/json");
        } catch (UnsupportedEncodingException e) {
            Log.e("Encoding error", "Can't change JSON object to string " +
                    "entity");
        }

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            Log.e("IO on POST","Can't execute post request to node server");
        }

        return response;
    }

    public HttpResponse getRequest(String url, String label, String value){
        HttpClient client = new DefaultHttpClient();

        String requestUrl = context.getResources().getString(R.string
                .nodeServer) +
                url;

        if(!value.equals("")){
            requestUrl += "?" + label + "=" + value;
        }

        HttpGet get = new HttpGet(requestUrl);

        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            Log.e("IO on GET","Can't execute get request to node server");
        }

        return response;
    }

    public HttpResponse putRequest(String url, JSONObject json){
        HttpClient client = new DefaultHttpClient();
        HttpPut put = new HttpPut(context.getResources().getString(R.string
                .nodeServer)
                + url);

        try {
            // Add the JSON to the PUT request
            StringEntity se = new StringEntity(json.toString());
            put.setEntity(se);
            put.setHeader("Content-type","application/json");
            put.setHeader("Accept","application/json");
        } catch (UnsupportedEncodingException e) {
            Log.e("Encoding error", "Can't change JSON object to string " +
                    "entity");
        }

        HttpResponse response = null;
        try {
            response = client.execute(put);
        } catch (IOException e) {
            Log.e("IO on PUT","Can't execute put request to node server");
        }

        return response;
    }
}
