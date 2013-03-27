package fr.eisti.icc.PFE_appli;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GCMIntentService extends GCMBaseIntentService {

    private void returnPing(Bundle extras){
        String ping_id = extras.getString("ping_id");

        Map<String,String> tmp = new HashMap<String, String>();
        tmp.put("ping_id",ping_id);
        tmp.put("phone_number",getPhoneNumber());
        tmp.put("ip",getIP());

        JSONObject payload = new JSONObject(tmp);

        postRequest("/messages/ping",payload);
    }

    private String getIP(){
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

    private String getPhoneNumber(){
        String result = "";
        // Read phoneNumber from internal storage
        try {
            FileInputStream fis = openFileInput(getString(R.string.filename));
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

    private void postRequest(String url, JSONObject json){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(getString(R.string.nodeServer) + url);

        try {
            // Add the JSON to the POST request
            StringEntity se = new StringEntity(json.toString());
            post.setEntity(se);
            post.setHeader("Content-type","application/json");
            post.setHeader("Accept","application/json");
        } catch (UnsupportedEncodingException e) {
            Log.e("Encoding error","Can't change JSON object to string " +
                    "entity");
        }

        try {
            client.execute(post);
        } catch (IOException e) {
            Log.d("IO on POST","Can't execute post request to node server");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int status = extras.getInt("status_code");
        switch(status){
            case 42: returnPing(extras);break;
            default: Log.e("ERROR","Wrong status code");
        }
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.e("GCM Error", errorId);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.i("REGISTER SUCCESS",regId);

        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("reg_id",regId);
        tmp.put("phone_number",getPhoneNumber());

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        postRequest("/devices/register",upload);

    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.i("UNREGISTER SUCCESS",regId);

        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("reg_id",regId);

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        postRequest("/devices/unregister",upload);
    }

}
