package vn.bcl.garanbcl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;

import butterknife.ButterKnife;
import vn.bcl.garanbcl.adapter.TabAdapter;
import vn.bcl.garanbcl.fragment.LoginFragment;
import vn.bcl.garanbcl.fragment.SignupFragment;
import vn.bcl.garanbcl.users.SmartUser;
import vn.bcl.garanbcl.util.CheckInternetConnection;
import vn.bcl.garanbcl.util.SmartLogin;
import vn.bcl.garanbcl.util.SmartLoginConfig;

public class LoginActivity extends AppCompatActivity {
    private TabAdapter tabAdapter;
    private DachshundTabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    private String primaryColor = "#ff2222";
    private String inactiveColor = "#ffffff";

    private SmartUser currentUser;
    //GoogleApiClient mGoogleApiClient;
    private SmartLoginConfig config;
    private SmartLogin smartLogin;
    private CheckInternetConnection connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connected = new CheckInternetConnection(this);
        connected.checkConnection();

        Firebase.setAndroidContext(this);
        String firebase_url = "https://ga-ran-bcl-d02e7.firebaseio.com/";
        Firebase firebaseDB = new Firebase(firebase_url);

        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initializer();
    }


    private void initializer() {
        bindViews();

        //set up tabs
        setFragments();
        setUpTabsView();
        setTabsOnClickListener();
        setCurrentTabTextColor(primaryColor);

        //set up appbar
        setupAppBar();
    }

    private void setCurrentTabTextColor(String colorString) {
        //pos = position of the tab
        //colorString = color hex ex: #ffffff for white
        tabLayout.setTabTextColors(Color.parseColor(inactiveColor), Color.parseColor(colorString));
    }

    //set corresponding views for variables
    private void bindViews() {
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabLayout = findViewById(R.id.loginTabLayout);
        toolbar = findViewById(R.id.loginToolbar);
        viewPager = findViewById(R.id.loginViewPager);
    }


    //add fragments to tablayout
    public void setFragments() {
        tabAdapter.addFragment(new LoginFragment(), "Đăng nhập");
        tabAdapter.addFragment(new SignupFragment(), "Đăng ký");
    }

    //set up viewpager and tablayout
    private void setUpTabsView() {
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //activate appbar
    private void setupAppBar() {
        setSupportActionBar(toolbar);
        //Use Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //change icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
    }

    //press back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        //get selected tab position
        int tab_position = tabLayout.getSelectedTabPosition();

        //decide what to do
        if (tab_position != 0) {
            //select the first tab
            tabLayout.getTabAt(0).select();
        } else {
            //quit
            super.onBackPressed();
        }
    }

    //handle opening drawer on menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTabsOnClickListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //set selected tab text color
                setCurrentTabTextColor(primaryColor);

                View v = findViewById(R.id.loginViewPager);
                clearText(v);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void clearText(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    // recursively call this method
                    clearText(child);
                }
            } else if (v instanceof EditText) {
               ((EditText) v).setText(null);
               ((EditText) v).setError(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (smartLogin != null) {
            smartLogin.onActivityResult(requestCode, resultCode, data, config);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onResume() {
        super.onResume();

        connected.checkConnection();
    }

    public void changeTab(int pos){
        if(pos <= tabLayout.getTabCount())
            tabLayout.getTabAt(pos).select();
    }
}
