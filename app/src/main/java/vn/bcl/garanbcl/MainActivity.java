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
import android.support.v4.app.ActivityCompat;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.flyco.tablayout.SlidingTabLayout;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.steelkiwi.library.IncrementProductView;
import com.steelkiwi.library.listener.OnStateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import live.utkarshdev.maincontentslidingnavview.ContentSlidingDrawerLayout;
import vn.bcl.garanbcl.adapter.ItemAdapter;
import vn.bcl.garanbcl.adapter.SliderAdapter;
import vn.bcl.garanbcl.adapter.TabAdapter;
import vn.bcl.garanbcl.fragment.HomeFragment;
import vn.bcl.garanbcl.fragment.MenuFragment;
import vn.bcl.garanbcl.model.Category;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.model.SubCategory;
import vn.bcl.garanbcl.model.Suggestion;
import vn.bcl.garanbcl.util.CheckInternetConnection;
import vn.bcl.garanbcl.util.CircleAnimationUtil;
import vn.bcl.garanbcl.util.Constants;
import vn.bcl.garanbcl.util.MenuItemBadge;
import vn.bcl.likebutton.LikeButton;
import vn.bcl.likebutton.OnAnimationEndListener;
import vn.bcl.likebutton.OnLikeListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ItemAdapter.IItemAdapterCallback{

    boolean shouldExecuteOnResume;

    private String primaryColor = "#DC143C";
    private String inactiveColor = "#000000";
    private String appBarTabColor = "#990000";

    private Activity context;
    private TabAdapter tabAdapter;
    private static TabLayout tabLayout;
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
    private ArrayList<Item> favList;
    private FrameLayout rlCart;
    private SlidingTabLayout tabLayoutAppBar;
    private CheckInternetConnection connected;
    private MenuItem menuItemMessage;

    private MenuItem searchMenu;
    private String mSearchString="";
    private ImageView searchAppBar;
    private FloatingSearchView searchView;
    private List<Suggestion> mSuggestions =new ArrayList<>();

    protected Firebase firebaseDB;
    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldExecuteOnResume = false;
        setContentView(R.layout.activity_main);
        connected = new CheckInternetConnection(this);
        connected.checkConnection();

        Firebase.setAndroidContext(this);
        firebaseDB = new Firebase(Constants.firebase_url);

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
                setTab(1);
                dialogItemDetail(suggestion.getItem());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                appBarLayout.setVisibility(AppBarLayout.VISIBLE);
                searchView.setVisibility(FloatingSearchView.GONE);
            }
        });

        initializer();
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

        //set firebase logged in listener
        setupfirebaseAuthListener();

        orderList = new ArrayList<Order>();
        favList = new ArrayList<Item>();

        fetchCart();
        fetchFav();

    }

    //set firebase logged in listener
    private void setupfirebaseAuthListener(){
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null)
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 2).setVisible(false);
                else
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 2).setVisible(true);
            }
        });
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
                if(fragments[pos].equals("Account") || fragments[pos].equals("Cart")){
                    if(mAuth.getCurrentUser() == null){
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
                                        startActivityForResult(intent, Constants.REQUEST_CODE);
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
                if(mAuth.getCurrentUser() != null)
                    navigationView.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
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

    public void dialogItemDetail(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_item_detail, null);

        final IncrementProductView incrementProductView = (IncrementProductView) view.findViewById(R.id.productView);
        TextView txtItemName = (TextView) view.findViewById(R.id.txtItemName);
        final TextView txtUnitPrice = (TextView) view.findViewById(R.id.txtUnitPrice);
        final TextView txtExtendedPrice = (TextView) view.findViewById(R.id.txtExtendedPrice);
        final TextView txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
        final ImageView imgThumbnail = (ImageView) view.findViewById(R.id.image_cartlist);
        final LikeButton btnShare = (LikeButton) view.findViewById(R.id.btnShare);
        final LikeButton btnFav = (LikeButton) view.findViewById(R.id.btnFav);
        final LikeButton btnClose = (LikeButton) view.findViewById(R.id.btnClose);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnOk = (Button) view.findViewById(R.id.btnOk);

        txtItemName.setText(item.name);
        txtUnitPrice.setText(String.format("%.0f", item.unitPrice) + " VNĐ");
        txtQuantity.setText("1");
        txtExtendedPrice.setText(String.format("%.0f", item.unitPrice * 1)  + " VNĐ");

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
                txtExtendedPrice.setText(String.format("%.0f", item.unitPrice * count) + " VNĐ");
            }

            @Override
            public void onConfirm(int count)
            {
                final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MainActivity.this)
                        .setBackgroundColor(R.color.white)
                        .setimageResource(R.drawable.logo)
                        .setTextTitle("----------------")
                        .setTextSubTitle(R.string.item_add_to_cart)
                        .setBody(R.string.item_add_to_cart_desc)
                        .setPositiveButtonText(R.string.item_add_to_cart_now)
                        .setPositiveColor(R.color.colorPrimaryDark)
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                addItemToCartAnimation(imgThumbnail, item, Integer.parseInt(txtQuantity.getText().toString()));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButtonText(R.string.item_add_to_cart_cancel)
                        .setNegativeColor(R.color.black)
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                txtQuantity.setText(String.valueOf(1));
                                txtExtendedPrice.setText(String.format("%.0f", item.unitPrice * 1));
                            }
                        })
                        .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                        .setCancelable(false)
                        .build();
                alert.show();
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

        btnClose.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {
                dialog.dismiss();
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });

        if(checkLiked(item))
            btnFav.setLiked(true);

        btnFav.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {
                addItemToFav(item);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeItemFromFav(item);
            }
        });

        btnShare.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
    }

    @Override
    public void onAddItemCallback(ImageView imageView, Item item) {
        addItemToCartAnimation(imageView, item, 1);
    }

    private void updateBadge() {
        if(MenuItemBadge.getBadgeTextView(menuItemMessage) != null) {
            if (orderList.size() == 0) {
                MenuItemBadge.getBadgeTextView(menuItemMessage).setBadgeCount(0, true);
            } else {
                MenuItemBadge.getBadgeTextView(menuItemMessage).setBadgeCount(orderList.size());
            }
        }
    }

    private void addItemToCartAnimation(ImageView targetView, final Item item, final int quantity) {
        if(mAuth.getCurrentUser() != null) {
            FrameLayout destView = findViewById(R.id.rlCart);

            new CircleAnimationUtil().attachActivity(MainActivity.this).setTargetView(targetView).setMoveDuration(300).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    addItemToCart(item, quantity);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).startAnimation();
        }else{
            showLogin();
        }
    }

    private void addItemToCart(Item item, int quantity) {
        boolean isAdded = false;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("gio-hang").child(mAuth.getCurrentUser().getUid());
        for (Order order : orderList)
        {
            Item orderItem = order.getItem();
            if (orderItem.id == item.id)
            {
                isAdded = true;
                order.addQuantity(quantity);
                mDatabase.child(String.valueOf(orderItem.id)).child("quantity").setValue(order.getQuantity());
                break;
            }
        }

        //if item's not added yet
        if (!isAdded)
        {
            Order ord = new Order(item, quantity);
            orderList.add(ord);
            mDatabase.child(String.valueOf(item.id)).setValue(ord);
        }
    }

    private void addItemToFav(Item it) {
        boolean isAdded = false;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("yeu-thich").child(mAuth.getCurrentUser().getUid());
        for (Item item : favList)
        {
            if (item.id == it.id)
            {
                isAdded = true;
                break;
            }
        }

        if (!isAdded)
        {
            favList.add(it);
            mDatabase.child(String.valueOf(it.id)).setValue(it);
        }
    }

    private void removeItemFromFav(Item it) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("yeu-thich").child(mAuth.getCurrentUser().getUid());
        mDatabase.child(String.valueOf(it.id)).setValue(null);
        favList.remove(it);
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

        if(title.equals(getResources().getString(R.string.nav_help))){
            //TODO: Help activity
            slidingDrawer.closeDrawers();
        }

        if(title.equals(getResources().getString(R.string.nav_terms))){
          //TODO: terms and conditions activity
            slidingDrawer.closeDrawers();
        }

        if(title.equals(getResources().getString(R.string.nav_logout))){
            final Toast logout_done = Toast.makeText(this, R.string.logout_succeeded, Toast.LENGTH_LONG);
            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                    .setBackgroundColor(R.color.white)
                    .setimageResource(R.drawable.logo)
                    .setTextTitle(null)
                    .setTextSubTitle(R.string.logout_confirm)
                    .setBody(R.string.logout_question)
                    .setPositiveButtonText(R.string.logout_ok)
                    .setPositiveColor(R.color.colorPrimaryDark)
                    .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            mAuth.signOut();
                            logout_done.show();
                            navigationView.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                            tabLayout.getTabAt(0).select();
                        }
                    })
                    .setNegativeButtonText(R.string.logout_cancel)
                    .setNegativeColor(R.color.black)
                    .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setCancelable(false)
                    .build();
            alert.show();
            slidingDrawer.closeDrawers();
        }

        if(title.equals(getResources().getString(R.string.nav_exit))){
            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                    .setBackgroundColor(R.color.white)
                    .setimageResource(R.drawable.logo)
                    .setTextTitle(null)
                    .setTextSubTitle(R.string.quit_confirm)
                    .setBody(R.string.quit_question)
                    .setPositiveButtonText(R.string.quit_ok)
                    .setPositiveColor(R.color.colorPrimaryDark)
                    .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            ActivityCompat.finishAffinity(MainActivity.this);
                        }
                    })
                    .setNegativeButtonText(R.string.quit_cancel)
                    .setNegativeColor(R.color.black)
                    .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                    .setCancelable(false)
                    .build();
            alert.show();
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
        if(mAuth.getCurrentUser() == null){
            navigationView.getMenu().getItem(navigationView.getMenu().size()- 2).setVisible(false);
        }else{
            navigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            navigationView.getMenu().getItem(navigationView.getMenu().size()- 2).setVisible(true);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mAuth.getCurrentUser() != null)
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        tabLayout.getTabAt(0).select();
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
        //TODO: MEnu button didn't work (open/close drawer)
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

        if(shouldExecuteOnResume){
            if(mAuth.getCurrentUser() != null)
                navigationView.findViewById(R.id.sign_in_button).setVisibility(View.GONE);

        } else{
            shouldExecuteOnResume = true;
        }
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
        /*List<Category> cats = new ArrayList<Category>();
        cats.add(new Category(0, "Tất cả", "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/hot-dish.png?alt=media&token=d352850f-a954-4b78-9563-bfb93267d2a1"));
        cats.add(new Category(1, "Gà rán", "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/chicken-bucket.png?alt=media&token=716687fd-19f4-426d-a9c6-d3abd6a26475"));
        cats.add(new Category(2, "Burger & cơm", "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/sandwich.png?alt=media&token=4d22cc04-08ac-4ede-805d-99a8787dddf6"));
        cats.add(new Category(3, "Thức ăn nhẹ", "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/french-fries.png?alt=media&token=6c4d4b16-3e16-40eb-96e0-3353c37277e4"));
        cats.add(new Category(4, "Thức uống & tráng miệng", "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/drink.png?alt=media&token=7a530e9e-f39c-4575-b7ca-47d703872126"));

        List<SubCategory> subcat1 = new ArrayList<SubCategory>();
        subcat1.add(new SubCategory(1, 1, "Gà miếng"));
        subcat1.add(new SubCategory(2, 1, "Gà phần"));

        List<SubCategory> subcat2 = new ArrayList<SubCategory>();
        subcat2.add(new SubCategory(3, 2, "Cơm"));
        subcat2.add(new SubCategory(4, 2, "Hamburger"));

        List<SubCategory> subcat3 = new ArrayList<SubCategory>();
        subcat3.add(new SubCategory(5, 3, "Món chiên"));
        subcat3.add(new SubCategory(6, 3, "Khoai tây chiên"));
        subcat3.add(new SubCategory(7, 3, "Salad"));

        List<SubCategory> subcat4 = new ArrayList<SubCategory>();
        subcat4.add(new SubCategory(8, 4, "Thức uống"));
        subcat4.add(new SubCategory(9, 4, "Kem"));
        subcat4.add(new SubCategory(10, 4, "Bánh ngọt"));

        List<List<SubCategory>> subcats = new ArrayList<List<SubCategory>>();
        subcats.add(subcat1);
        subcats.add(subcat2);
        subcats.add(subcat3);
        subcats.add(subcat4);

        List<Item> item1_1 = new ArrayList<Item>();
        item1_1.add(new Item(1, 1, 1, "Gà giòn cay", 25000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/be064003a56beabc4304bb9de7d63117.JPG?alt=media&token=e73a180e-e83b-45bd-908b-a7b0b18d3ba1"));
        item1_1.add(new Item(2, 1, 1, "Gà truyền thống", 23000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/be064003a56beabc4304bb9de7d63117.JPG?alt=media&token=e73a180e-e83b-45bd-908b-a7b0b18d3ba1"));
        item1_1.add(new Item(3, 1, 1, "Gà quay tiêu", 50000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/8a6f957ff760d860bee9f0e9f5194891.jpg?alt=media&token=426e77c4-35ba-4202-9db9-7ab1aa394924"));

        List<Item> item1_2 = new ArrayList<Item>();
        item1_2.add(new Item(4, 1, 2, "Gà giòn cay (3 miếng)", 69000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/b0914223717890b6e022cdda5bbf9157.JPG?alt=media&token=7bd3b8c4-eef8-41c4-b73d-18f1b9feaf70"));
        item1_2.add(new Item(5, 1, 2, "Gà giòn cay (6 miếng)", 135000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/55e4aa27e3d2101a72871728c609f3fd.JPG?alt=media&token=4e8bb056-3021-461c-91ed-8caec12c461a"));

        List<Item> item2_3 = new ArrayList<Item>();
        item2_3.add(new Item(6, 2, 3, "Cơm gà giòn cay", 37000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/8b858b57c3fc228c84da80d852f6a533.jpg?alt=media&token=408967f1-61d5-4a9f-ab6e-fe00a31a185e"));
        item2_3.add(new Item(7, 2, 3, "Cơm gà truyền thống", 35000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/34e7d33183130c8d86cb6a18ad5b899a.jpg?alt=media&token=b1e58bcc-cae0-4ad0-8482-0521a4a41a4d"));

        List<Item> item2_4 = new ArrayList<Item>();
        item2_4.add(new Item(8, 2, 4, "Burger Zinger", 37000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/14fbac8736ec4188209a9f8996747e57.jpg?alt=media&token=65ef7420-23c5-4a04-9101-1443e2d8e8f3"));
        item2_4.add(new Item(9, 2, 4, "Burger Tôm", 35000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/0786da34d00e9f2f72c293e4faa332f2.jpg?alt=media&token=8eb7a794-4b0c-4a05-a991-8eed2e06995c"));

        List<Item> item3_5 = new ArrayList<Item>();
        item3_5.add(new Item(10, 3, 5, "Phô mai que", 15000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/resize.png?alt=media&token=d1f55d39-0092-4e11-9b12-ae031d2f46d9"));
        item3_5.add(new Item(11, 3, 5, "Thanh cá", 20000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/1984c78778bc8623a9bb91bc5a05821f.jpg?alt=media&token=11055942-5dbc-458e-8c31-5d585996856b"));

        List<Item> item3_6 = new ArrayList<Item>();
        item3_6.add(new Item(12, 3, 6, "Khoai tây chiên (Vừa)", 18000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/resize%20(1).png?alt=media&token=732bfec5-828a-40c0-b74e-e72a29739d83"));

        List<Item> item3_7 = new ArrayList<Item>();
        item3_7.add(new Item(13, 3, 7, "Salad rau quả", 26000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/49c2e866c16eb3-phananphu042.png?alt=media&token=1f311f69-235b-48af-9bcd-80df524b4d84"));

        List<Item> item4_8 = new ArrayList<Item>();
        item4_8.add(new Item(14, 4, 8, "Pepsi (Vừa)", 10000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/8b858b57c3fc228c84da80d852f6a533.jpg?alt=media&token=408967f1-61d5-4a9f-ab6e-fe00a31a185e"));

        List<Item> item4_9 = new ArrayList<Item>();
        item4_9.add(new Item(15, 4, 9, "Kem Vani", 5000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/813f69a38f29337488f0d30dcc299bd5.jpg?alt=media&token=139fdc72-8b9e-4a7d-a95a-062820971cec"));
        item4_9.add(new Item(16, 4, 9, "Kem Vani phủ Socola", 8000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/1370dddff397feb477c13cf007cad409.jpg?alt=media&token=35c09de8-37c4-4ac7-824f-479985f51d11"));

        List<Item> item4_10 = new ArrayList<Item>();
        item4_10.add(new Item(17, 4, 10, "Bánh trứng nướng", 5000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/6b5e6fa66b96309a58d6eec9bba9a888.jpg?alt=media&token=f343271c-29ff-4610-8fed-026fb435b1d1"));
        item4_10.add(new Item(18, 4, 10, "Mashies Rau củ", 12000, "https://firebasestorage.googleapis.com/v0/b/ga-ran-bcl-d02e7.appspot.com/o/40fb89b75a656b0eb25b960acf531463.png?alt=media&token=f10c913a-9bdc-49e0-96af-8dc42f1a1f6a"));

        List<List<Item>> items = new ArrayList<List<Item>>();
        items.add(item1_1);
        items.add(item1_2);
        items.add(item2_3);
        items.add(item2_4);
        items.add(item3_5);
        items.add(item3_6);
        items.add(item3_7);
        items.add(item4_8);
        items.add(item4_9);
        items.add(item4_10);


        Firebase sudo = firebaseDB.child("thuc-don");
        String folder = sudo.push().getKey();
        Category temp = cats.get(0);
        sudo.child(folder).child("desc").setValue(temp);

        int k = 1;
        while(k < 5) {
            Firebase wd = firebaseDB.child("thuc-don");
            String key = wd.push().getKey();
            Category tempcat = cats.get(k++);
            wd.child(key).child("desc").setValue(tempcat);

            int p = 0;
            while(p < subcats.get(tempcat.id - 1).size()) {
                wd = firebaseDB.child("thuc-don").child(key);
                String subkey = wd.push().getKey();
                SubCategory tempsubcat = subcats.get(tempcat.id - 1).get(p++);
                wd.child(subkey).child("desc").setValue(tempsubcat);

                int l = 0;
                while(l < items.get(tempsubcat.id - 1).size()) {
                    wd = firebaseDB.child("thuc-don").child(key).child(subkey);
                    String itemkey = wd.push().getKey();
                    wd.child(itemkey).child("desc").setValue(items.get(tempsubcat.id - 1).get(l++));
                }
            }

        }*/

        FirebaseDatabase mDB = FirebaseDatabase.getInstance();
        DatabaseReference root = mDB.getReference("thuc-don");

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cats : dataSnapshot.getChildren()) {
                    for (DataSnapshot subcats : cats.getChildren()) {
                        if (!subcats.getKey().equals("desc")) {
                            for (DataSnapshot items : subcats.getChildren()) {
                                if (!items.getKey().equals("desc")) {
                                    mSuggestions.add(new Suggestion(items.child("desc").getValue(Item.class)));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

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

    public static void setTab(int pos){
        tabLayout.getTabAt(pos).select();
    }

    public void setMenuPage(int catId){
        ((MenuFragment) tabAdapter.getItem(1)).changeTab(catId);
        setTab(1);
    }

    public void showLogin(View v){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void fetchCart() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase firebaseDB;
            DatabaseReference root;
            firebaseDB = FirebaseDatabase.getInstance();
            root = firebaseDB.getReference("gio-hang").child(String.valueOf(mAuth.getCurrentUser().getUid()));

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    orderList = new ArrayList<Order>();

                    for (DataSnapshot ords : dataSnapshot.getChildren()) {
                        Order ord = ords.getValue(Order.class);
                        orderList.add(ord);
                    }

                    updateBadge();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }

    private void fetchFav() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase firebaseDB;
            DatabaseReference root;
            firebaseDB = FirebaseDatabase.getInstance();
            root = firebaseDB.getReference("yeu-thich").child(String.valueOf(mAuth.getCurrentUser().getUid()));

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    favList = new ArrayList<Item>();

                    for (DataSnapshot items : dataSnapshot.getChildren()) {
                        Item item = items.getValue(Item.class);
                        favList.add(item);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }

    public void checkout(View view) {
        Intent i = new Intent(MainActivity.this, CheckoutActivity.class);
        startActivity(i);
    }

    private boolean checkLiked(final Item item){
        for(Item i : favList){
            if(i.id == item.id)
                return true;
        }

        return false;
    }
}
