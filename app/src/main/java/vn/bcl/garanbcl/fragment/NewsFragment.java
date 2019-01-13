package vn.bcl.garanbcl.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.model.FavViewHolder;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.News;
import vn.bcl.garanbcl.model.NewsViewHolder;

public class NewsFragment extends Fragment {
    private StaggeredGridLayoutManager mLayoutManager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private RecyclerView mRecyclerView;
    FirebaseRecyclerAdapter<News, NewsViewHolder> adapter;
    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public NewsFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        mRecyclerView = view.findViewById(R.id.rvNews);
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
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("tin-tuc")) {
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
        adapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(
                News.class,
                R.layout.news_item_layout,
                NewsViewHolder.class,
                //referencing the node where we want the database to store the data from our Object
                mDatabaseReference.child("tin-tuc").getRef()
        ) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void populateViewHolder(final NewsViewHolder viewHolder, final News model, final int position) {
                viewHolder.cardname.setText(model.getTitle());
                viewHolder.carddate.setText(model.getDate());
                Picasso.get().load(model.getUrl()).into(viewHolder.cardimage);
                viewHolder.cardcontent.setText(model.getContent());
            }
        };

        mRecyclerView.setAdapter(adapter);
    }
}
