package vn.bcl.garanbcl.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;

import vn.bcl.garanbcl.R;
import com.geniusforapp.fancydialog.FancyAlertDialog;


public class CheckInternetConnection {

    Context ctx;

    public CheckInternetConnection(Context context){
        ctx=context;
    }

    public void checkConnection(){

        if(!isInternetConnected()) {

                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(ctx)
                        .setBackgroundColor(R.color.white)
                        .setimageResource(R.drawable.internetconnection)
                        .setTextTitle(R.string.connection_not_found)
                        .setTextSubTitle(R.string.cannot_connect)
                        .setBody(R.string.noconnection)
                        .setPositiveButtonText(R.string.connect_now)
                        .setPositiveColor(R.color.colorPrimaryDark)
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {

                                if(isInternetConnected()){

                                    dialog.dismiss();

                                }else {

                                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ctx.startActivity(dialogIntent);
                                }
                             }
                        })
                        .setNegativeButtonText(R.string.quit_app)
                        .setNegativeColor(R.color.colorPrimaryDark)
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                System.exit(0);
                            }
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setCancelable(false)
                        .build();
                alert.show();
        }
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}
