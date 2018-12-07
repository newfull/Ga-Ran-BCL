package vn.bcl.garanbcl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import vn.bcl.garanbcl.adapter.TabAdapter;

public class MainActivity extends AppCompatActivity {

    private Activity context;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout toolbar_name_bcl;

    private int tab_nums = 5;
    int[] tabs_icons = {
            R.drawable.home,
            R.drawable.hot,
            R.drawable.cart,
            R.drawable.account,
            R.drawable.menu
    };
    String[] fragments = {
            "Home",
            "News",
            "Cart",
            "Account",
            "Settings"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializer();
    }

    private void initializer() {
        context = MainActivity.this;

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        toolbar_name_bcl = findViewById(R.id.toolbar_name_bcl);

        for(int i = 0; i < tab_nums; i++){
            try {
                String frag_name = getResources().getString(R.string.package_name) + ".fragment." + fragments[i] + "Fragment";
                Fragment f = (Fragment) (Class.forName(frag_name).newInstance());
                adapter.addFragment(f, fragments[i]);
            }
            catch (Exception e){
                Log.e("Error", e.getMessage());
            }
        }
        viewPager.setAdapter(adapter);


        tabLayout.setupWithViewPager(viewPager);

        for(int i = 0; i < tab_nums; i++){
            tabLayout.getTabAt(i).setIcon(tabs_icons[i]);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#DC143C"), PorterDuff.Mode.SRC_IN);
                int pos = tab.getPosition();

                if(pos != 0){
                    appBarLayout.setExpanded(false);
                }
                else{
                    appBarLayout.setExpanded(true, true);
                }

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#DC143C"), PorterDuff.Mode.SRC_IN);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    collapsingToolbarLayout.setTitleEnabled(true);
                    toolbar_name_bcl.setVisibility(LinearLayout.VISIBLE);
                }
                else
                {
                    collapsingToolbarLayout.setTitleEnabled(false);
                    toolbar.setTitle("");
                    toolbar_name_bcl.setVisibility(LinearLayout.GONE);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        int tab_position = tabLayout.getSelectedTabPosition();

        //decide what to do
        if(tab_position != 0){
            tabLayout.getTabAt(0).select();
        } else {
            super.onBackPressed();
        }
    }
}
