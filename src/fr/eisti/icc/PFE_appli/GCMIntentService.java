package fr.eisti.icc.PFE_appli;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GCMIntentService extends GCMBaseIntentService {
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

    }

    @Override
    protected void onError(Context context, String errorId) {

    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.i("REGISTER SUCCESS",regId);

        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("regId",regId);
        tmp.put("phoneNumber",getPhoneNumber());

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        postRequest("/register",upload);

    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.i("UNREGISTER SUCCESS",regId);

        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("regId",regId);

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        postRequest("/unregister",upload);
    }

}
