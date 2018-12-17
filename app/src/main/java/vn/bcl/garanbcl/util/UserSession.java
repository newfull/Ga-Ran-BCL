package vn.bcl.garanbcl.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;

import vn.bcl.garanbcl.LoginActivity;
import vn.bcl.garanbcl.users.SmartFacebookUser;
import vn.bcl.garanbcl.users.SmartGoogleUser;
import vn.bcl.garanbcl.users.SmartUser;
import vn.bcl.garanbcl.util.Constants;


public class UserSession {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserSessionPref";

    // First time logic Check
    public static final String FIRST_TIME = "firsttime";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // number of items in our cart
    public static final String KEY_CART = "cartvalue";

    // check first time app launch
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    // Constructor
    public UserSession(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     **/
    public static boolean setUserSession(Context context, SmartUser smartUser){
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        if(smartUser != null) {
            try {
                preferences = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
                editor = preferences.edit();

                // Storing login value as TRUE
                editor.putBoolean(IS_LOGIN, true);

                if (smartUser instanceof SmartFacebookUser) {
                    editor.putString(Constants.USER_TYPE, Constants.FACEBOOKFLAG);
                } else if (smartUser instanceof SmartGoogleUser) {
                    editor.putString(Constants.USER_TYPE, Constants.GOOGLEFLAG);
                } else {
                    editor.putString(Constants.USER_TYPE, Constants.CUSTOMUSERFLAG);
                }

                Gson gson = new Gson();
                String sessionUser = gson.toJson(smartUser);
                Log.d("GSON", sessionUser);
                editor.putString(Constants.USER_SESSION, sessionUser);
                editor.apply();
                return true;
            } catch (Exception e) {
                Log.e("Session Error", e.getMessage());
                return false;
            }
        } else {
            Log.e("Session Error", "User is null");
            return false;
        }
    }


    /** Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */

    /*public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);
        }

    }*/



    /**
     * Get stored session data
     * */
    public static SmartUser getCurrentUser(Context context){
        SmartUser smartUser = null;
        SharedPreferences preferences = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String sessionUser = preferences.getString(Constants.USER_SESSION, Constants.DEFAULT_SESSION_VALUE);
        String user_type = preferences.getString(Constants.USER_TYPE, Constants.CUSTOMUSERFLAG);
        if(!sessionUser.equals(Constants.DEFAULT_SESSION_VALUE)){
            try {
                switch (user_type) {
                    case Constants.FACEBOOKFLAG:
                        smartUser = gson.fromJson(sessionUser, SmartFacebookUser.class);
                        break;
                    case Constants.GOOGLEFLAG:
                        smartUser = gson.fromJson(sessionUser, SmartGoogleUser.class);
                        break;
                    default:
                        smartUser = gson.fromJson(sessionUser, SmartUser.class);
                        break;
                }
            }catch (Exception e){
                Log.e("GSON", e.getMessage());
            }
        }
        return smartUser;
    }

    /**
     * Clear session details
     * */

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN,false);
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(i);
    }

    /**
     * Quick check for login
     **/

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public int getCartValue(){
        return pref.getInt(KEY_CART,0);
    }

    public Boolean  getFirstTime() {
        return pref.getBoolean(FIRST_TIME, true);
    }

    public void setFirstTime(Boolean n){
        editor.putBoolean(FIRST_TIME,n);
        editor.commit();
    }


    public void increaseCartValue(){
        int val = getCartValue()+1;
        editor.putInt(KEY_CART,val);
        editor.commit();
        Log.e("Cart Value PE", "Var value : "+val+"Cart Value :"+getCartValue()+" ");
    }

    public void decreaseCartValue(){
        int val = getCartValue()-1;
        editor.putInt(KEY_CART,val);
        editor.commit();
        Log.e("Cart Value PE", "Var value : "+val+"Cart Value :"+getCartValue()+" ");
    }

    public void setCartValue(int count){
        editor.putInt(KEY_CART,count);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
