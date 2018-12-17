package vn.bcl.garanbcl;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.flyco.tablayout.SlidingTabLayout;
import com.geniusforapp.fancydialog.FancyAlertDialog;
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
import vn.bcl.garanbcl.model.Suggestion;
import vn.bcl.garanbcl.users.SmartFacebookUser;
import vn.bcl.garanbcl.users.SmartGoogleUser;
import vn.bcl.garanbcl.users.SmartUser;
import vn.bcl.garanbcl.util.CheckInternetConnection;
import vn.bcl.garanbcl.util.CircleAnimationUtil;
import vn.bcl.garanbcl.util.Constants;
import vn.bcl.garanbcl.util.MenuItemBadge;
import vn.bcl.garanbcl.util.SmartLoginConfig;
import vn.bcl.garanbcl.util.UserSession;
import vn.bcl.likebutton.LikeButton;
import vn.bcl.likebutton.OnAnimationEndListener;
import vn.bcl.likebutton.OnLikeListener;

public class MainActivity extends AppCompatActivity implements OnLikeListener, NavigationView.OnNavigationItemSelectedListener,
        OnAnimationEndListener, ItemAdapter.IItemAdapterCallback, OrderAdapter.IOrderAdapterCallback{

    private UserSession session;
    private String primaryColor = "#DC143C";
    private String inactiveColor = "#000000";
    private String appBarTabColor = "#990000";

    private Activity context;
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    //appbar image slider
    private ViewPager sliderViewPager;
    private TabLayout sliderIndicator;

    private NavigationView navigationView;
    private ContentSlidingDrawerLayout slidingDrawer;

    private int tabsCount = 5;
    int[] tabsIcons = {
            R.drawable.home,
            R.drawable.food,
            R.drawable.cart,
            R.drawable.fire,
            R.drawable.user
    };
    String[] fragments = {
            "Home",
            "Menu",
            "Cart",
            "News",
            "Account"
    };

    int[] sliderImages = {
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_4,
            R.drawable.banner_5,
    };

    String[] tabsNames = {
            "Trang chủ",
            "Thực đơn",
            "Giỏ hàng",
            "Tin tức",
            "Tài khoản"
    };

    private TextSwitcher tabsTitle;

    private ArrayList<Order> orderList;
    private FrameLayout rlCart;
    private SlidingTabLayout tabLayoutAppBar;
    private CheckInternetConnection connected;
    private MenuItem menuItemMessage;

    private MenuItem searchMenu;
    private String mSearchString="";
    private ImageView searchAppBar;
    private FloatingSearchView searchView;
    private List<Suggestion> mSuggestions =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connected = new CheckInternetConnection(this);
        connected.checkConnection();

        session = new UserSession(getApplicationContext());

        Firebase.setAndroidContext(this);
        String firebase_url = "https://ga-ran-bcl-d02e7.firebaseio.com/";
        Firebase firebaseDB = new Firebase(firebase_url);

        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        initData();

        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setDismissFocusOnItemSelection(true);

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.swapSuggestions(getSuggestion(newQuery));
                } else {
                    searchView.showProgress();
                    searchView.swapSuggestions(getSuggestion(newQuery));
                    searchView.hideProgress();
                }
            }
        });
        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                searchView.showProgress();
                searchView.swapSuggestions(getSuggestion(searchView.getQuery()));
                searchView.hideProgress();
            }

            @Override
            public void onFocusCleared() {
                appBarLayout.setVisibility(AppBarLayout.VISIBLE);
                int pos = tabLayout.getSelectedTabPosition();
                if(pos == tabLayout.getTabCount()) tabLayout.getTabAt(pos++).select();
                else tabLayout.getTabAt(++pos).select();
                tabLayout.getTabAt(pos  - 1 ).select();
                searchView.setVisibility(FloatingSearchView.GONE);
            }
        });
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Suggestion suggestion= (Suggestion) searchSuggestion;
                Toast.makeText(getApplicationContext(),"Ban vua chon "+suggestion.getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchAction(String currentQuery) {
                appBarLayout.setVisibility(AppBarLayout.VISIBLE);
                searchView.setVisibility(FloatingSearchView.GONE);
            }
        });

        initializer();

        /*final Intent intent = new Intent(this, LoginActivity.class);
        final int REQUEST_CODE_EXAMPLE = 0x9345;

        // Start DetailActivity với request code vừa được khai báo trước đó
        startActivityForResult(intent, REQUEST_CODE_EXAMPLE);*/

    }

    private void initializer() {
        bindViews();

        //set up tabs
        setFragments();
        setUpTabsView();
        setTabsIcons();
        setTabsOnClickListener();
        setTabsIconCustomColor(0, primaryColor);
        setTabsTitle();

        //set up appbar
        setupAppBar();
        setAppBarOnOffsetChangedListener();

        //set up appbar images slider
        setupAppBarSlider();
        setupSliderTimer();

        //set up search icon listener on app bar
        setAppBarSearchIconListener();

        //set up navigation drawer
        setupDrawer();
        disableNavigationViewScrollbars(navigationView);

    }

    //set corresponding views for variables
    private void bindViews() {
        context = MainActivity.this;

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabAdapter = new TabAdapter(getSupportFragmentManager());

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        sliderIndicator = findViewById(R.id.sliderIndicator);

        navigationView = findViewById(R.id.nav_view);
        slidingDrawer = findViewById(R.id.drawer_layout);

        tabsTitle = findViewById(R.id.tab_title);
        tabLayoutAppBar = findViewById(R.id.tabLayoutAppBar);

        searchAppBar = findViewById(R.id.searchAppBar);
    }

    private void setTabsTitle() {
        tabsTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @TargetApi(Build.VERSION_CODES.M)
            public View makeView() {
                // TODO Auto-generated method stub
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                // set the gravity of text
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                // set displayed text size
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_height));
                t.setTextColor(getResources().getColor(R.color.white, null));
                Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa-Bold.ttf");
                t.setTypeface(type);

                return t;
            }
        });

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to TextSwitcher
        tabsTitle.setInAnimation(in);
        tabsTitle.setOutAnimation(out);

        //text appear on start
        tabsTitle.setCurrentText(tabsNames[0]);
    }

    //add fragments to tablayout
    public void setFragments(){
        for(int i = 0; i < tabsCount; i++){
            try {
                //get fragment activity name
                String frag_name = getResources().getString(R.string.package_name) + ".fragment." + fragments[i] + "Fragment";
                //create fragment activity
                Fragment f = (Fragment) (Class.forName(frag_name).newInstance());
                //add fragment to tab layout views
                tabAdapter.addFragment(f, tabsNames[i]);
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
        tabLayoutAppBar.setViewPager(viewPager);
        tabLayoutAppBar.setIndicatorColor(Color.parseColor(appBarTabColor));

        for (int i = 0; i < tabsCount; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setText("");
        }
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
                connected.checkConnection();

                //get position of selected tab
                int pos = tab.getPosition();
                if(pos == (tabLayout.getTabCount() - 1)){
                    if(!session.isLoggedIn()){
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MainActivity.this)
                                .setBackgroundColor(R.color.white)
                                .setimageResource(R.drawable.logo)
                                .setTextTitle("----------------")
                                .setTextSubTitle(R.string.user_not_found)
                                .setBody(R.string.please_login)
                                .setPositiveButtonText(R.string.login_now)
                                .setPositiveColor(R.color.colorPrimaryDark)
                                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                                    @Override
                                    public void OnClick(View view, Dialog dialog) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivityForResult(intent, Constants.LOGIN_REQUEST);
                                        }
                                })
                                .setNegativeButtonText(R.string.close_login_failed)
                                .setNegativeColor(R.color.black)
                                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                                    @Override
                                    public void OnClick(View view, Dialog dialog) {
                                        dialog.dismiss();
                                        tabLayout.getTabAt(0).select();
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
                //set selected color for icon
                setTabsIconCustomColor(pos, primaryColor);

                tabsTitle.setText(tabsNames[pos]);

                //if tab pos != 0 (Home)
                if(pos != 0){
                    //set appbar unexpanded
                    appBarLayout.setExpanded(false,true);
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
        for(int i = 0; i < tabsCount; i++){
            tabLayout.getTabAt(i).setIcon(tabsIcons[i]);
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

    //handle opening drawer on menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                slidingDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //actions on appbar expanding/collasing
    private void setAppBarOnOffsetChangedListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //Appbar is UNEXPANDED
                    tabsTitle.setVisibility(TextSwitcher.VISIBLE);
                    if(searchView.isSearchBarFocused()){
                        appBarLayout.setVisibility(AppBarLayout.GONE);
                    }
                }
                else //Appbar is EXPANDED
                {
                    tabsTitle.setVisibility(TextSwitcher.GONE);
                }
            }
        });
    }

    //set up appbar images slider
    private void setupAppBarSlider() {
        sliderViewPager.setAdapter(new SliderAdapter(this, sliderImages));
        sliderIndicator.setupWithViewPager(sliderViewPager, true);
    }

    private void setAppBarSearchIconListener(){
        searchAppBar.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                  /*  searchMenu.expandActionView();*/
                searchView.setVisibility(FloatingSearchView.VISIBLE);
                searchView.setSearchFocused(true);
            }
        });
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
        final ImageView imgThumbnail = (ImageView) view.findViewById(R.id.image_cartlist);
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
//        updateOrderTotal();
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

    private void updateBadge()
    {
        if (orderList.size() == 0)
        {
            MenuItemBadge.getBadgeTextView(menuItemMessage).setBadgeCount(0, true);
        } else
        {
            MenuItemBadge.getBadgeTextView(menuItemMessage).setBadgeCount(orderList.size());
        }
    }

    private void addItemToCartAnimation(ImageView targetView, final Item item, final int quantity)
    {
        FrameLayout destView = findViewById(R.id.rlCart);

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

       /* orderAdapter.notifyDataSetChanged();
        rvOrder.smoothScrollToPosition(orderList.size() - 1);
        updateOrderTotal();*/
        updateBadge();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String title = (String) menuItem.getTitle();

        for(int i = 0; i < tabsCount; i++){
            if(title.equals(tabsNames[i])){
                tabLayout.getTabAt(i).select();
                slidingDrawer.closeDrawers();
                return false;
            }
        }

        slidingDrawer.closeDrawers();
        return false;
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

        if (slidingDrawer.isDrawerOpen(GravityCompat.START)) {
            slidingDrawer.closeDrawer(GravityCompat.START);
        } else {

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
    }

    //TODO: integrate FB login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Constants.FACEBOOK_LOGIN_REQUEST){
            SmartFacebookUser user;
            try {
                user = data.getParcelableExtra(Constants.USER);
                //use this user object as per your requirement
            }catch (Exception e){
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }else if(resultCode == Constants.GOOGLE_LOGIN_REQUEST){
            SmartGoogleUser user;
            try {
                user = data.getParcelableExtra(Constants.USER);
                //use this user object as per your requirement
            }catch (Exception e){
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }else if(resultCode == Constants.CUSTOM_LOGIN_REQUEST){
            SmartUser user = data.getParcelableExtra(Constants.USER);
            //use this user object as per your requirement
        }else if(resultCode == RESULT_CANCELED){
            //Login Failed
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);

        final View actionCart = menu.findItem(R.id.actionCart).getActionView();

        menuItemMessage = menu.findItem(R.id.actionCart);
        MenuItemBadge.update(this, menuItemMessage, new MenuItemBadge.Builder()
                .iconDrawable(ContextCompat.getDrawable(this, R.drawable.cart_toolbar))
                .iconTintColor(Color.WHITE)
                .textBackgroundColor(Color.parseColor("#EF4738"))
                .textColor(Color.WHITE));

        rlCart = actionCart.findViewById(R.id.rlCart);

        orderList = new ArrayList<Order>();
        orderList.add(new Order(new Item(1, 1, 1, " x", 30, ""), 1));
        updateBadge();

        rlCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TabLayout.Tab tab = tabLayout.getTabAt(2);
                tab.select();
            }
        });
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.e("PRESSED", String.valueOf(keyCode));
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!slidingDrawer.isDrawerOpen(GravityCompat.START)) {
                slidingDrawer.openDrawer(GravityCompat.START);
            } else if (slidingDrawer.isDrawerOpen(GravityCompat.START)) {
                slidingDrawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();

        connected.checkConnection();

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText())
            hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void initData(){
        mSuggestions.add(new Suggestion("Ha Noi"));
        mSuggestions.add(new Suggestion("Ha nam"));
        mSuggestions.add(new Suggestion("Da nang"));
        mSuggestions.add(new Suggestion("Dong nai"));
        mSuggestions.add(new Suggestion("Phú Tho"));
        mSuggestions.add(new Suggestion("Quang ngai"));
        mSuggestions.add(new Suggestion("Thanh hoa"));
        mSuggestions.add(new Suggestion("Hue"));
        mSuggestions.add(new Suggestion("Ha Noi"));
        mSuggestions.add(new Suggestion("Ha nam"));
        mSuggestions.add(new Suggestion("Da nang"));
        mSuggestions.add(new Suggestion("Dong nai"));
        mSuggestions.add(new Suggestion("Phú Tho"));
        mSuggestions.add(new Suggestion("Quang ngai"));
        mSuggestions.add(new Suggestion("Thanh hoa"));
        mSuggestions.add(new Suggestion("Hue"));
        mSuggestions.add(new Suggestion("Ha Noi"));
        mSuggestions.add(new Suggestion("Ha nam"));
        mSuggestions.add(new Suggestion("Da nang"));
        mSuggestions.add(new Suggestion("Dong nai"));
        mSuggestions.add(new Suggestion("Phú Tho"));
        mSuggestions.add(new Suggestion("Quang ngai"));
        mSuggestions.add(new Suggestion("Thanh hoa"));
        mSuggestions.add(new Suggestion("Hue"));

    }

    private List<Suggestion> getSuggestion(String query){
        List<Suggestion> suggestions=new ArrayList<>();
        for(Suggestion suggestion:mSuggestions){
            if(suggestion.getName().toLowerCase().contains(query.toLowerCase())){
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }
}
