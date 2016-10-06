package com.mlieber.KanjiKing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.content.pm.PackageInfo;

public class About extends Activity
{
    private TextView txt_about;
    private Button btn_close;

    private static final String TAG = "KanjiKing/About";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // use the search view here
        setContentView(R.layout.about);

        txt_about = (TextView) findViewById(R.id.txt_about);
        btn_close = (Button) findViewById(R.id.btn_close);

        txt_about.setText(
                "KanjiKing\n\n"
                + "Author:  Mathias Lieber\n"
                + "Version: " + getVersionNumber() + "\n"
        );

        btn_close.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private String getVersionNumber()
    {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName; // pInfo.versionCode;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
