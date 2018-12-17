package vn.bcl.garanbcl;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import vn.bcl.garanbcl.util.CheckInternetConnection;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private CheckInternetConnection connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        connected = new CheckInternetConnection(this);

        connected.checkConnection();

        if (connected.isInternetConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        connected.checkConnection();

        if (connected.isInternetConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
