package fr.eisti.icc.PFE_appli;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class Utils {

    Context context;

    public Utils(Context context){
        this.context = context;
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

    public void postRequest(String url, JSONObject json){
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

        try {
            client.execute(post);
        } catch (IOException e) {
            Log.d("IO on POST","Can't execute post request to node server");
        }
    }

    public HttpResponse getRequest(String url, JSONObject json){
        Log.i("GET REQUEST","TROLOLOLOLO");
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(context.getResources().getString(R.string
                .nodeServer) + url);
        HttpParams params = new BasicHttpParams();

        // Add the JSON to the POST request
        params.setParameter("json",json);
        //get.setHeader("Content-type", "application/json");
        //get.setHeader("Accept", "application/json");

        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            Log.d("IO on GET","Can't execute post request to node server");
        }

        return response;
    }

}
