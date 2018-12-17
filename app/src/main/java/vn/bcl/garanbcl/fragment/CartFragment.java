package vn.bcl.garanbcl.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tyrantgit.explosionfield.ExplosionField;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.model.CartViewHolder;
import vn.bcl.garanbcl.model.SingleProductModel;
import vn.bcl.garanbcl.util.UserSession;


public class CartFragment extends Fragment{

    private ExplosionField mExplosionField;

    private UserSession session;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();

    private ArrayList<SingleProductModel> cartcollect;
    private float totalcost=0;
    private int totalproducts=0;

    public CartFragment() {}

    Button btn;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        mExplosionField = ExplosionField.attach2Window(getActivity());
        session = new UserSession(getActivity().getApplicationContext());

        //validating session
        session.isLoggedIn();

        mRecyclerView = view.findViewById(R.id.rvCart);
        cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);


        populateRecyclerView();

        return view;
    }

    private void populateRecyclerView() {
        final FirebaseRecyclerAdapter<SingleProductModel,CartViewHolder> adapter = new FirebaseRecyclerAdapter<SingleProductModel, CartViewHolder>(
                SingleProductModel.class,
                R.layout.cart_item_layout,
                CartViewHolder.class,
                //referencing the node where we want the database to store the data from our Object
                mDatabaseReference.child("cart").child("7719846014").getRef()
        ) {
            @Override
            protected void populateViewHolder(final CartViewHolder viewHolder, final SingleProductModel model, final int position) {
                viewHolder.cardname.setText(model.getPrname());
                viewHolder.cardprice.setText("₹ "+model.getPrprice());
                viewHolder.cardcount.setQuantity(model.getNo_of_items());
                Picasso.get().load(model.getPrimage()).into(viewHolder.cardimage);

                totalcost += model.getNo_of_items()*Float.parseFloat(model.getPrprice());
                totalproducts += model.getNo_of_items();
                cartcollect.add(model);

                viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = getRef(position);
                        ref.removeValue();
                  //      session.decreaseCartValue();
                        mExplosionField.explode(viewHolder.itemView);
                        v.setOnClickListener(null);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

}
