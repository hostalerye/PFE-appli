package fr.eisti.icc.PFE_appli;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GCMIntentService extends GCMBaseIntentService {

    Utils utils;

    private void returnPing(Bundle extras){
        Log.i("PING MESSAGE","TRYING");
        utils = new Utils(getBaseContext());
        String ping_id = extras.getString("ping_id");
        Map<String,String> tmp = new HashMap<String, String>();
        tmp.put("ping_id",ping_id);
        tmp.put("phone_number",utils.getPhoneNumber());
        tmp.put("ip",utils.getIP());

        JSONObject payload = new JSONObject(tmp);

        utils.postRequest("/messages/ping",payload);
        Log.i("RETURN PING","SUCCESS");
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String status = extras.getString("status_code");

        switch(Integer.parseInt(status)){
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
        utils = new Utils(getBaseContext());
        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("reg_id",regId);
        tmp.put("phone_number",utils.getPhoneNumber());

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        utils.postRequest("/devices/register",upload);

    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        utils = new Utils(getBaseContext());
        // Fill map with infos
        Map<String, String> tmp = new HashMap<String,String>();
        tmp.put("reg_id",regId);

        // Transform map into JSONObject
        JSONObject upload = new JSONObject(tmp);

        utils.postRequest("/devices/unregister",upload);
    }

}
