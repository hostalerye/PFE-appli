package fr.eisti.icc.PFE_appli;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: hostalerye
 * Date: 13/03/13
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class Menu extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button button = (Button)findViewById(R.id.launchButton);
        button.setOnClickListener(new View.OnClickListener() {
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

                                    }
                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
