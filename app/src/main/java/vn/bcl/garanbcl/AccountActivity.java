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
import vn.bcl.garanbcl.fragment.AccountFavFragment;
import vn.bcl.garanbcl.fragment.AccountInfoFragment;
import vn.bcl.garanbcl.fragment.AccountOrdersFragment;
import vn.bcl.garanbcl.util.CheckInternetConnection;
import vn.bcl.garanbcl.util.Constants;

public class AccountActivity extends AppCompatActivity {
    private TabAdapter tabAdapter;
    private DachshundTabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    private String primaryColor = "#ff2222";
    private String inactiveColor = "#ffffff";

    int[] tabsIcons = {
            R.drawable.user_info,
            R.drawable.likes,
            R.drawable.order
    };
    String txttrades[]={
            "Thông tin cá nhân",
            "Danh sách yêu thích",
            "Danh sách đơn hàng"
    };
    private CheckInternetConnection connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        connected = new CheckInternetConnection(this);
        connected.checkConnection();

        Firebase.setAndroidContext(this);
        String firebase_url = "https://ga-ran-bcl-d02e7.firebaseio.com/";
        Firebase firebaseDB = new Firebase(firebase_url);

        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initializer();

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("Tab");
            for(int i = 0; i < txttrades.length; i++){
                if(txttrades[i].equals(j))
                    changeTab(i);
            }
        }
    }


    private void initializer() {
        bindViews();

        //set up tabs
        setFragments();
        setUpTabsView();
        setTabsIcons();
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
        tabAdapter.addFragment(new AccountInfoFragment(), "Tài khoản");
        tabAdapter.addFragment(new AccountFavFragment(), "Yêu thích");
        tabAdapter.addFragment(new AccountOrdersFragment(), "Đơn hàng");
    }

    private void setTabsIcons() {
        for(int i = 0; i < tabsIcons.length; i++){
            tabLayout.getTabAt(i).setIcon(tabsIcons[i]);
        }
    }

    //set up viewpager and tablayout
    private void setUpTabsView() {
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabsIcons.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setText("");
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }

    private void setTabsOnClickListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = findViewById(R.id.loginViewPager);
                getSupportActionBar().setTitle(txttrades[tab.getPosition()]);
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
