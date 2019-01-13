package vn.bcl.garanbcl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import vn.bcl.garanbcl.adapter.TabAdapter;
import vn.bcl.garanbcl.fragment.LoginFragment;
import vn.bcl.garanbcl.fragment.SignupFragment;
import vn.bcl.garanbcl.model.CartViewHolder;
import vn.bcl.garanbcl.model.Category;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.OrdViewHolder;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.model.SubCategory;
import vn.bcl.garanbcl.util.CheckInternetConnection;
import vn.bcl.garanbcl.util.Constants;

import static java.lang.Float.parseFloat;

public class CheckoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String primaryColor = "#ff2222";
    private String inactiveColor = "#ffffff";
    private TextView txtTotalValue;

    private EditText txtEdit;
    float totalcost = 0;
    private List<Order> cartlist = new ArrayList();
    private CheckInternetConnection connected;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();

    FirebaseRecyclerAdapter<Order,OrdViewHolder> adapter;

    DecimalFormat formatter = new DecimalFormat("###,###,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        connected = new CheckInternetConnection(this);
        connected.checkConnection();

        Firebase.setAndroidContext(this);
        String firebase_url = "https://ga-ran-bcl-d02e7.firebaseio.com/";
        Firebase firebaseDB = new Firebase(firebase_url);

        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initializer();

        mRecyclerView = findViewById(R.id.rvCart);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        txtTotalValue = findViewById(R.id.txtTotalValue);

        setAddress("");

        checkEmpty();

        populateRecyclerView();


    }

    private void setAddress(String txt) {
        txtEdit.setSelectAllOnFocus(true);
        txtEdit.setText(txt);
    }

    private void checkEmpty(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("dia-chi");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    FirebaseDatabase firebaseQ;
                    DatabaseReference root;
                    firebaseQ = FirebaseDatabase.getInstance();
                    root = firebaseQ.getReference("dia-chi").child(mAuth.getCurrentUser().getUid());

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            setAddress(dataSnapshot.child("address").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
                else{
                    setAddress("Nhập vào một địa chỉ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateRecyclerView() {
            cartlist = new ArrayList<>();
            adapter = new FirebaseRecyclerAdapter<Order, OrdViewHolder>(
                    Order.class,
                    R.layout.ord_item_layout,
                    OrdViewHolder.class,
                    //referencing the node where we want the database to store the data from our Object
                    mDatabaseReference.child("gio-hang").child(mAuth.getCurrentUser().getUid()).getRef()
            ) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void populateViewHolder(final OrdViewHolder viewHolder, final Order model, final int position) {
                    viewHolder.cardname.setText(model.getItem().name);
                    viewHolder.cardprice.setText("VNĐ " + formatter.format(parseFloat(model.getPrice())));
                    viewHolder.cardcount.setText(String.valueOf(model.getQuantity()));
                    Picasso.get().load(model.getItem().url).into(viewHolder.cardimage);
                    cartlist.add(model);


                    loadCost();
                }
            };
            mRecyclerView.setAdapter(adapter);
    }

    private void initializer() {
        bindViews();
        //set up appbar
        setupAppBar();
    }

    //set corresponding views for variables
    private void bindViews() {
        toolbar = findViewById(R.id.loginToolbar);
        txtTotalValue = findViewById(R.id.txtTotalValue);
        txtEdit = findViewById(R.id.address);
        txtEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    confirmAdd();
                }
            }
        });
    }

    private void confirmAdd(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        updateAddress(txtEdit.getText().toString());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        setAddress("Nhập vào một địa chỉ");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Chọn địa chỉ này?").setPositiveButton("Đồng ý", dialogClickListener)
                .setNegativeButton("Hủy", dialogClickListener).show();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void loadCost(){
        for (Order order : cartlist)
        {
            totalcost += parseFloat(order.getPrice());
        }

        totalcost = Math.round((totalcost - (totalcost * 10 / 100))/1000) * 1000;
        txtTotalValue.setText(formatter.format(totalcost));
    }

    //activate appbar
    private void setupAppBar() {
        setSupportActionBar(toolbar);
        //Use Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //change icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.checkout_title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        connected.checkConnection();

        loadCost();
    }

    //press back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void checkout(final View v){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("dia-chi");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("hoa-don").child(mAuth.getCurrentUser().getUid());
                    String key = mDatabase.push().getKey();
                    mDatabase = mDatabase.child(key);
                    mDatabase.child("id").setValue(key);
                    mDatabase.child("address").setValue(txtEdit.getText().toString());
                    Date date = new Date();
                    String strDateFormat = "dd/mm/yyyy hh:mm:ss a";
                    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                    String formattedDate= dateFormat.format(date);
                    mDatabase.child("date").setValue(formattedDate);
                    mDatabase.child("total").setValue(txtTotalValue.getText().toString());
                    for(Order x : cartlist){
                        mDatabase.child(String.valueOf(x.getItem().id)).setValue(x);
                    }

                    mDatabase = FirebaseDatabase.getInstance().getReference("gio-hang");
                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(null);

                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    startActivity(intent);
                    CheckoutActivity.this.finish();
                }
                else{
                    setAddress("Nhập vào một địa chỉ");
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    setAddress("Nhập vào một địa chỉ");
                                    txtEdit.requestFocus();
                                    showKeyboard(v);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                    builder.setMessage("Chưa có địa chỉ giao!").setPositiveButton("Xem lại", dialogClickListener)
                            .setNegativeButton("Hủy", dialogClickListener).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateAddress(String add){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("dia-chi").child(mAuth.getCurrentUser().getUid());
        mDatabase.child("address").setValue(add);
    }
}
