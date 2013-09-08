package com.cinemagram;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(R.color.brown));
        bar.setStackedBackgroundDrawable(new ColorDrawable(R.color.green));


        Button start = (Button) findViewById(R.id.start);
//        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/JosefinSlab-Thin.ttf");
//        start.setTypeface(typeface);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TabActivity.class));
            }
        });
    }
}
