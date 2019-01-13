package vn.bcl.garanbcl.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cielyang.android.clearableedittext.ClearableEditText;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tyrantgit.explosionfield.ExplosionField;
import vn.bcl.garanbcl.LoginActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.model.CartViewHolder;
import vn.bcl.garanbcl.model.FavViewHolder;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.util.Constants;

import static java.lang.Float.parseFloat;


public class AccountFavFragment extends Fragment {
    private ExplosionField mExplosionField;

    private StaggeredGridLayoutManager mLayoutManager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private RecyclerView mRecyclerView;
    FirebaseRecyclerAdapter<Item, FavViewHolder> adapter;
    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DecimalFormat formatter = new DecimalFormat("###,###,###.##");
    int quantity = 0;

    public AccountFavFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fav, container, false);
        mExplosionField = ExplosionField.attach2Window(getActivity());
        mRecyclerView = view.findViewById(R.id.rvFav);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        checkEmpty();
        populateRecyclerView();
        return view;
    }

    private void checkEmpty(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("yeu-thich");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getUid())) {
                    ((ImageView) view.findViewById(R.id.no_item)).setVisibility(ImageView.GONE);
                }
                else{
                    ((ImageView) view.findViewById(R.id.no_item)).setVisibility(ImageView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void populateRecyclerView() {
        if (mAuth.getCurrentUser() != null) {
           adapter = new FirebaseRecyclerAdapter<Item, FavViewHolder>(
                    Item.class,
                    R.layout.fav_item_layout,
                    FavViewHolder.class,
                    //referencing the node where we want the database to store the data from our Object
                    mDatabaseReference.child("yeu-thich").child(mAuth.getCurrentUser().getUid()).getRef()
            ) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void populateViewHolder(final FavViewHolder viewHolder, final Item model, final int position) {
                    viewHolder.cardname.setText(model.name);
                    viewHolder.cardprice.setText("VNĐ " + formatter.format(model.unitPrice));
                    Picasso.get().load(model.url).into(viewHolder.cardimage);
                    quantity++;
                    viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference ref = getRef(position);
                            ref.removeValue();
                            reloadRV(position);
                            quantity--;
                            mExplosionField.explode(viewHolder.itemView);
                            v.setOnClickListener(null);
                        }
                    });
                }
            };

            mRecyclerView.setAdapter(adapter);

        } else {
            }
    }

    private void reloadRV(int position) {
        checkEmpty();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position, quantity);
    }

}
