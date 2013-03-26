package fr.eisti.icc.PFE_appli;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;

public class Menu extends Activity{

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        launchButtonSetUp();
        phoneButtonSetUp();
    }
}
