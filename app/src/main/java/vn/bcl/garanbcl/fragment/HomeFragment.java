package vn.bcl.garanbcl.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.ArrayList;
import java.util.Random;

import vn.bcl.garanbcl.MainActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.adapter.HorizontalAdapter;
import vn.bcl.garanbcl.cards.SliderAdapter;
import vn.bcl.garanbcl.model.Category;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.SubCategory;

//TODO: populate popular items, design homepage

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    FirebaseDatabase firebaseDB;
    DatabaseReference root;
    private final int[] pics = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};

    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, 5, new OnCardClickListener());

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;

    private TextView cat1TextView;
    private TextView cat2TextView;
    private int catOffset1;
    private int catOffset2;
    private long catAnimDuration;
    private int currentPosition;
    private RecyclerView recyclerViewHorizontal;
    private ArrayList<Category> categoryList;
    private ArrayList<Item> itemList;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseDB = FirebaseDatabase.getInstance();
        root = firebaseDB.getReference("thuc-don");

        recyclerViewHorizontal = view.findViewById(R.id.horizontal_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontal.setLayoutManager(linearLayoutManager);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList = new ArrayList<Category>();
                itemList = new ArrayList<Item>();

                for (DataSnapshot cats : dataSnapshot.getChildren()) {
                    Category cat = cats.child("desc").getValue(Category.class);
                    categoryList.add(cat);
                    for (DataSnapshot subcats : cats.getChildren()) {
                        if (!subcats.getKey().equals("desc")) {
                            for (DataSnapshot items : subcats.getChildren()) {
                                if (!items.getKey().equals("desc")) {
                                    Random r = new Random();
                                    if (itemList.size() < 5 && (r.nextInt(3) - 1) > 0) {
                                        Item item = items.child("desc").getValue(Item.class);
                                        itemList.add(item);
                                    }
                                }
                            }
                        }
                    }
                }

                recyclerViewHorizontal.setAdapter(new HorizontalAdapter(itemList, R.layout.recyclerview_horizontal, getActivity()));

                initRecyclerView();
                initCountryText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        return view;
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.cat_recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

    private void initCountryText() {
        catAnimDuration = 350;
        catOffset1 = 61;
        catOffset2 = 148;
        cat1TextView = (TextView) view.findViewById(R.id.tvCatName1);
        cat2TextView = (TextView) view.findViewById(R.id.tvCatName2);

        cat1TextView.setX(catOffset1);
        cat2TextView.setX(catOffset2);
        cat1TextView.setText(categoryList.get(0).name);
        cat2TextView.setAlpha(0f);
    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (cat1TextView.getAlpha() > cat2TextView.getAlpha()) {
            visibleText = cat1TextView;
            invisibleText = cat2TextView;
        } else {
            visibleText = cat2TextView;
            invisibleText = cat1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = catOffset2;
        } else {
            invisibleText.setX(catOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", catOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(catAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setCountryText(categoryList.get(pos % categoryList.size()).name, left2right);

        currentPosition = pos;
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                ((MainActivity) getActivity()).setMenuPage(clickedPosition);
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }
}


