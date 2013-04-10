package fr.eisti.icc.PFE_appli;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hostalerye
 * Date: 10/04/13
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */
public class PingResult extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ping_result);
        // Get the message from the intent
        Intent intent = getIntent();
        String results = intent.getStringExtra(Menu.PING_RESULT);
        final ListView listview = (ListView) findViewById(R.id.list);

        try {
            Log.i("PING RESPONSE",results);
            JSONArray array = new JSONArray(results);
            JSONObject json;
            final ArrayList<String> list = new ArrayList<String>();
            for(int i=0; i < array.length(); i++){
                json = array.getJSONObject(i);
                list.add("ip: "+json.getString("ip") + " / " + "tel: "+json.getString("phone_number"));
            }
            final CustomListAdapter adapter = new CustomListAdapter(this,
                    android.R.layout.simple_list_item_1, list, array);
            listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private class CustomListAdapter  extends ArrayAdapter<String> {
        private ArrayList<String> list;
        private JSONArray jsonArray;

        public CustomListAdapter(Context context, int resID, ArrayList<String> items, JSONArray jsonItems) {
            super(context, resID, items);
            list = items;
            jsonArray = jsonItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            try {
                int status = jsonArray.getJSONObject(position).getInt("status_code");
                switch(status) {
                    case 100:
                        ((TextView) v).setTextColor(Color.GREEN);
                        break;
                    case 101:
                        ((TextView) v).setTextColor(Color.YELLOW);
                        break;
                    case 102:
                        ((TextView) v).setTextColor(Color.RED);
                        break;
                    default:
                        ((TextView) v).setTextColor(Color.RED);
                        break;
                }
            } catch (JSONException e) {
                Log.e("JSON", "Error while parsing");
            }
            return v;
        }

    }
}
