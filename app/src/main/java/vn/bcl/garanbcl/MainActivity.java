package vn.bcl.garanbcl;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.steelkiwi.library.IncrementProductView;
import com.steelkiwi.library.listener.OnStateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

import live.utkarshdev.maincontentslidingnavview.ContentSlidingDrawerLayout;
import vn.bcl.garanbcl.adapter.ItemAdapter;
import vn.bcl.garanbcl.adapter.OrderAdapter;
import vn.bcl.garanbcl.adapter.SliderAdapter;
import vn.bcl.garanbcl.adapter.TabAdapter;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.util.CircleAnimationUtil;
import vn.bcl.likebutton.LikeButton;
import vn.bcl.likebutton.OnAnimationEndListener;
import vn.bcl.likebutton.OnLikeListener;

public class MainActivity extends AppCompatActivity implements OnLikeListener,
        OnAnimationEndListener, NavigationView.OnNavigationItemSelectedListener,
        ItemAdapter.IItemAdapterCallback, OrderAdapter.IOrderAdapterCallback {

    private String primaryColor = "#DC143C";
    private String inactiveColor = "#000000";

    private Activity context;
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout toolbar_name_bcl;

    //appbar image slider
    private ViewPager sliderViewPager;
    private TabLayout sliderIndicator;

    private NavigationView navigationView;
    private ContentSlidingDrawerLayout slidingDrawer;

    private int tab_nums = 5;
    int[] tabs_icons = {
            R.drawable.home,
            R.drawable.fire,
            R.drawable.cart,
            R.drawable.user,
            R.drawable.menu
    };
    String[] fragments = {
            "Home",
            "News",
            "Cart",
            "Account",
            "Settings"
    };

    int[] sliderImages = {
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_4,
            R.drawable.banner_5,
    };



    private ArrayList<Order> orderList;
    private TextView txtTotal;
    private TextView txtCount;
    private OrderAdapter orderAdapter;
    private RecyclerView rvOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        String firebase_url = "https://ga-ran-bcl-d02e7.firebaseio.com/";
        Firebase firebaseDB = new Firebase(firebase_url);


        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initializer();


        txtTotal = findViewById(R.id.txtTotal);
        rvOrder = findViewById(R.id.rvOrder);


     /* final Intent intent = new Intent(this, LoginActivity.class);
        final int REQUEST_CODE_EXAMPLE = 0x9345;

        // Start DetailActivity với request code vừa được khai báo trước đó
        startActivityForResult(intent, REQUEST_CODE_EXAMPLE);*/
    }

    private void initializer() {
        mappingViews();

        //set up tabs
        setFragments();
        setUpTabsView();
        setTabsIcons();
        setTabsOnClickListener();
        setTabsIconCustomColor(0, primaryColor);

        //set up appbar
        setupAppBar();
        setAppBarOnOffsetChangedListener();

        //set up appbar images slider
        setupAppBarSlider();
        setupSliderTimer();

        //set up navigation drawer
        setupDrawer();
    }

    //set corresponding views for variables
    private void mappingViews() {
        context = MainActivity.this;

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabAdapter = new TabAdapter(getSupportFragmentManager());

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

        toolbar_name_bcl = findViewById(R.id.toolbar_name_bcl);

        sliderViewPager = (ViewPager)findViewById(R.id.sliderViewPager);
        sliderIndicator = (TabLayout)findViewById(R.id.sliderIndicator);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        slidingDrawer = findViewById(R.id.drawer_layout);
    }

    //add fragments to tablayout
    public void setFragments(){
        for(int i = 0; i < tab_nums; i++){
            try {
                //get fragment activity name
                String frag_name = getResources().getString(R.string.package_name) + ".fragment." + fragments[i] + "Fragment";
                //create fragment activity
                Fragment f = (Fragment) (Class.forName(frag_name).newInstance());
                //add fragment to tab layout views
                tabAdapter.addFragment(f, fragments[i]);
            }
            catch (Exception e){
                Log.e("Error", e.getMessage());
            }
        }
    }

    //set up viewpager and tablayout
    private void setUpTabsView() {
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //change color of tabs icon
    private void setTabsIconCustomColor(int pos, String colorString) {
        //pos = position of the tab
        //colorString = color hex ex: #ffffff for white
        tabLayout.getTabAt(pos).getIcon().setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_IN);
    }

    //actions when change selected tab
    private void setTabsOnClickListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //get position of selected tab
                int pos = tab.getPosition();
                //set selected color for icon
                setTabsIconCustomColor(pos, primaryColor);
                //if tab pos != 0 (Home)
                if(pos != 0){
                    //set appbar unexpanded
                    appBarLayout.setExpanded(false);
                }
                else{
                    //set appbar expanded
                    appBarLayout.setExpanded(true, true);
                }

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //set unselected tabs icons to be gray
                setTabsIconCustomColor(tab.getPosition(), inactiveColor);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //set icon for tablayout indicator
    private void setTabsIcons() {
        for(int i = 0; i < tab_nums; i++){
            tabLayout.getTabAt(i).setIcon(tabs_icons[i]);
        }
    }

    //activate appbar
    private void setupAppBar() {
        setSupportActionBar(toolbar);
        //Use Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Set custom icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_toolbar);
    }

    //actions on appbar expanding/collasing
    private void setAppBarOnOffsetChangedListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //Appbar is UNEXPANDED

                    //Show title: Yes
                    collapsingToolbarLayout.setTitleEnabled(true);
                    //this one for showing toolbar when appbar collapse
                    toolbar_name_bcl.setVisibility(LinearLayout.VISIBLE);
                }
                else //Appbar is EXPANDED
                {
                    //Show title: No
                    collapsingToolbarLayout.setTitleEnabled(false);
                    //Change custom title for appbar
                    toolbar.setTitle("");
                    //this one for hiding toolbar when appbar expand
                    toolbar_name_bcl.setVisibility(LinearLayout.GONE);
                }
            }
        });
    }

    //set up appbar images slider
    private void setupAppBarSlider() {
        sliderViewPager.setAdapter(new SliderAdapter(this, sliderImages));
        sliderIndicator.setupWithViewPager(sliderViewPager, true);
    }

    @Override
    public void onItemCallback(Item item) {
        dialogItemDetail(item);
    }

    private void dialogItemDetail(final Item item)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_item_detail, null);

        final IncrementProductView incrementProductView = (IncrementProductView) view.findViewById(R.id.productView);
        TextView txtItemName = (TextView) view.findViewById(R.id.txtItemName);
        final TextView txtUnitPrice = (TextView) view.findViewById(R.id.txtUnitPrice);
        final TextView txtExtendedPrice = (TextView) view.findViewById(R.id.txtExtendedPrice);
        final TextView txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
        final ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);

        txtItemName.setText(item.name);
        txtUnitPrice.setText(String.format("%.2f", item.unitPrice));
        txtQuantity.setText("1");
        txtExtendedPrice.setText(String.format("%.2f", item.unitPrice * 1));

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DetailDialogAnimation;
        dialog.show();

        Glide.with(MainActivity.this)
                .load(item.url)
                .into(imgThumbnail);

        incrementProductView.getAddIcon();

        incrementProductView.setOnStateListener(new OnStateListener()
        {
            @Override
            public void onCountChange(int count)
            {
                txtQuantity.setText(String.valueOf(count));
                txtExtendedPrice.setText(String.format("%.2f", item.unitPrice * count));
            }

            @Override
            public void onConfirm(int count)
            {

            }

            @Override
            public void onClose()
            {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addItemToCartAnimation(imgThumbnail, item, Integer.parseInt(txtQuantity.getText().toString()));

                dialog.dismiss();
            }
        });
    }

    @Override
    public void onAddItemCallback(ImageView imageView, Item item) {
        addItemToCartAnimation(imageView, item, 1);
    }

    @Override
    public void onIncreaseDecreaseCallback() {
        updateOrderTotal();
        updateBadge();
    }

    private double getOrderTotal()
    {
        double total = 0.0;

        for (Order order : orderList)
        {
            total += order.extendedPrice;
        }

        return total;
    }

    private void updateOrderTotal()
    {
        double total = getOrderTotal();
        txtTotal.setText(String.format("%.2f", total));
    }

    private void updateBadge()
    {
        if (orderList.size() == 0)
        {
            txtCount.setVisibility(View.INVISIBLE);
        } else
        {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(String.valueOf(orderList.size()));
        }
    }

    private void addItemToCartAnimation(ImageView targetView, final Item item, final int quantity)
    {
        RelativeLayout destView = (RelativeLayout) findViewById(R.id.rlCart);

        new CircleAnimationUtil().attachActivity(MainActivity.this).setTargetView(targetView).setMoveDuration(300).setDestView(destView).setAnimationListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                addItemToCart(item, quantity);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        }).startAnimation();
    }

    private void addItemToCart(Item item, int quantity)
    {
        boolean isAdded = false;

        for (Order order : orderList)
        {
            if (order.item.id == item.id)
            {
                //if item already added to cart, dont add new order
                //just add the quantity
                isAdded = true;
                order.quantity += quantity;
                order.extendedPrice += item.unitPrice;
                break;
            }
        }

        //if item's not added yet
        if (!isAdded)
        {
            orderList.add(new Order(item, quantity));
        }

        orderAdapter.notifyDataSetChanged();
        rvOrder.smoothScrollToPosition(orderList.size() - 1);
        updateOrderTotal();
        updateBadge();
    }


    //Timer for image slide on appbar
    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderViewPager.getCurrentItem() < sliderImages.length - 1) {
                        sliderViewPager.setCurrentItem(sliderViewPager.getCurrentItem() + 1);
                    } else {
                        sliderViewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
    private void setupSliderTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 5000);
    }

    //set navigation drawer, bound with appbar
    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        slidingDrawer.init(this, navigationView, getSupportActionBar());
    }

    //press back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        //get selected tab position
        int tab_position = tabLayout.getSelectedTabPosition();

        //decide what to do
        if(tab_position != 0){
            //select the first tab
            tabLayout.getTabAt(0).select();
        } else {
            //quit
            super.onBackPressed();
        }
    }

    //TODO: integrate FB login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //actions after liked a shine button
    @Override
    public void onAnimationEnd(LikeButton likeButton) {

    }

    //actions after liked a shine button
    @Override
    public void liked(LikeButton likeButton) {

    }

    //actions on unlike a shine button
    @Override
    public void unLiked(LikeButton likeButton) {

    }

    //actions on select an option on drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //  AccessToken accessToken = AccessToken.getCurrentAccessToken();
   // boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//  Then you can later perform the actual login, such as in a custom button's OnClickListener:
 //   LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile"));

}
