package fr.eisti.icc.PFE_appli;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GCMIntentService extends IntentService {

    private static Utils utils;
    private static PowerManager.WakeLock sWakeLock;
    private static final Object LOCK = GCMIntentService.class;

    public GCMIntentService(){
        super("GCMIntentService");
    }

    public GCMIntentService(String name) {
        super(name);
    }

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

    static void runIntentInService(Context context, Intent intent) {
        synchronized(LOCK) {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
            }
        }
        sWakeLock.acquire();
        intent.setClassName(context, GCMIntentService.class.getName());
        utils = new Utils(context);
        context.startService(intent);
    }

    @Override
    public final void onHandleIntent(Intent intent) {

        try {
            String action = intent.getAction();
            if (action != null){
                if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
                    handleRegistration(intent);
                } else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                    handleMessage(intent);
                }
            }
        } finally {
            if(sWakeLock != null){
                synchronized(LOCK) {
                    sWakeLock.release();
                }
            }
        }
    }

    private void handleRegistration(Intent intent) {
        String registrationId = intent.getStringExtra("registration_id");
        String error = intent.getStringExtra("error");
        String unregistered = intent.getStringExtra("unregistered");

        if (registrationId != null) {
            // registration succeeded
            // commit to shared preferences
            utils.setRegId(registrationId);
            // Fill map with infos
            Map<String, String> tmp = new HashMap<String,String>();
            tmp.put("reg_id",registrationId);
            tmp.put("phone_number",utils.getPhoneNumber());

            // Transform map into JSONObject
            JSONObject upload = new JSONObject(tmp);

            utils.postRequest("/devices/register",upload);
        }

        if (unregistered != null) {
            // unregistration succeeded
            // Fill map with infos
            Map<String, String> tmp = new HashMap<String,String>();
            tmp.put("reg_id", utils.getRegId());
            // remove from shared preferences
            utils.removeRegId();

            // Transform map into JSONObject
            JSONObject upload = new JSONObject(tmp);

            utils.postRequest("/devices/unregister",upload);
        }

        // last operation (registration or unregistration) returned an error;
        if (error != null) {
            Log.e("GCM ERROR", "Received error: " + error);
        }
    }

    private void handleMessage(Intent intent) {
        Bundle extras = intent.getExtras();
        String status = extras.getString("status_code");

        switch(Integer.parseInt(status)){
            case 42: returnPing(extras);break;
            default: Log.e("ERROR","Wrong status code");
        }
    }
}
