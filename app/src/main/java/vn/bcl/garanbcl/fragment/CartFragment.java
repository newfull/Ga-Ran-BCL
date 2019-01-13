package vn.bcl.garanbcl.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.model.CartViewHolder;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;

import static java.lang.Float.parseFloat;

public class CartFragment extends Fragment{

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ExplosionField mExplosionField;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private float totalcost=0, totalcost_tmp=0, discount=0, totalval=0;
    private TextView txtSumValue, txtDiscountValue, txtTotalValue;
    DecimalFormat formatter = new DecimalFormat("###,###,###.##");
    public CartFragment() {}

    private List<Order> cartlist = new ArrayList();

    Button btn;
    View view;

    FirebaseRecyclerAdapter<Order,CartViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        mExplosionField = ExplosionField.attach2Window(getActivity());
        cartlist = new ArrayList<>();
        ((ImageView) view.findViewById(R.id.no_user)).setVisibility(ImageView.VISIBLE);

        mRecyclerView = view.findViewById(R.id.rvCart);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        txtSumValue = view.findViewById(R.id.txtSumValue);
        txtDiscountValue = view.findViewById(R.id.txtDiscountValue);
        txtTotalValue = view.findViewById(R.id.txtTotalValue);

        checkEmpty();
        populateRecyclerView();

        return view;
    }

    private void checkEmpty(){
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("gio-hang");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                    } else {
                        ((ImageView) view.findViewById(R.id.no_item)).setVisibility(ImageView.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void populateRecyclerView() {
        if (mAuth.getCurrentUser() != null) {
            ((ImageView) view.findViewById(R.id.no_user)).setVisibility(ImageView.GONE);
            cartlist = new ArrayList<>();
            adapter = new FirebaseRecyclerAdapter<Order, CartViewHolder>(
                    Order.class,
                    R.layout.cart_item_layout,
                    CartViewHolder.class,
                    //referencing the node where we want the database to store the data from our Object
                    mDatabaseReference.child("gio-hang").child(mAuth.getCurrentUser().getUid()).getRef()
            ) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void populateViewHolder(final CartViewHolder viewHolder, final Order model, final int position) {
                    viewHolder.cardname.setText(model.getItem().name);
                    viewHolder.cardprice.setText("VNĐ " + formatter.format(parseFloat(model.getPrice())));
                    viewHolder.cardcount.setValue(model.getQuantity());
                    Picasso.get().load(model.getItem().url).into(viewHolder.cardimage);
                    cartlist.add(model);

                    viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference ref = getRef(position);
                            ref.removeValue();
                            cartlist.remove(model);
                            reloadRV(position);
                            mExplosionField.explode(viewHolder.itemView);
                            loadCost();
                            v.setOnClickListener(null);
                        }
                    });

                    viewHolder.cardcount.setValueChangedListener(new ValueChangedListener() {
                        @Override
                        public void valueChanged(int value, ActionEnum acton) {
                            DatabaseReference ref = getRef(position);
                            ref.child("quantity").setValue(value);
                            cartlist.remove(model);
                        }
                    });

                    loadCost();
                }
            };
            mRecyclerView.setAdapter(adapter);
        } else {
        }
    }

    private void reloadRV(int position) {
        if(cartlist.isEmpty())
            ((ImageView) view.findViewById(R.id.no_item)).setVisibility(ImageView.VISIBLE);

        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position,cartlist.size());
    }

    private void loadCost(){
        totalcost = 0;

        for (Order order : cartlist)
        {
            totalcost += parseFloat(order.getPrice());
        }

        txtSumValue.setText(formatter.format(totalcost));
        discount = totalcost * 10 / 100;
        txtDiscountValue.setText(formatter.format(discount));
        totalval = Math.round((totalcost - discount)/1000) * 1000;
        txtTotalValue.setText(formatter.format(totalval));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null)
        {
            checkLogin();
        }
        if(!cartlist.isEmpty())
            ((ImageView) view.findViewById(R.id.no_item)).setVisibility(ImageView.GONE);

    }

    public void checkLogin(){
        if(mAuth.getCurrentUser() != null){
            ((ImageView) view.findViewById(R.id.no_user)).setVisibility(ImageView.GONE);
        }
    }
}
