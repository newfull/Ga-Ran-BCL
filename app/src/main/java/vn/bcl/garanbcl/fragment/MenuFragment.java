package vn.bcl.garanbcl.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.adapter.CategoryPagerAdapter;
import vn.bcl.garanbcl.model.Category;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.model.Order;
import vn.bcl.garanbcl.model.Solution;
import vn.bcl.garanbcl.model.SubCategory;
import vn.bcl.garanbcl.util.Constants;


public class MenuFragment extends Fragment{

    private ArrayList<Category> categoryList;
    private ArrayList<SubCategory> subCategoryList;
    private ArrayList<Item> itemList;
    private ArrayList<Solution> solutionList;
    private ArrayList<Order> orderList;

    private TabLayout tabCategory;
    FirebaseDatabase firebaseDB;
    DatabaseReference root;

    public MenuFragment() {}

    View view;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        firebaseDB = FirebaseDatabase.getInstance();
        root = firebaseDB.getReference("thuc-don");

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList = new ArrayList<Category>();
                subCategoryList = new ArrayList<SubCategory>();
                itemList = new ArrayList<Item>();

                for (DataSnapshot cats : dataSnapshot.getChildren()) {
                    Category cat = cats.child("desc").getValue(Category.class);
                    categoryList.add(cat);
                    for (DataSnapshot subcats : cats.getChildren()) {
                        if (!subcats.getKey().equals("desc")) {
                            SubCategory subcat = subcats.child("desc").getValue(SubCategory.class);
                            subCategoryList.add(subcat);
                            for (DataSnapshot items : subcats.getChildren()) {
                                if (!items.getKey().equals("desc")) {
                                    Item item = items.child("desc").getValue(Item.class);
                                    itemList.add(item);
                                }
                            }
                        }
                    }
                }

                prepareData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return view;
    }

    private void prepareData()
    {
         /** Populates solutionList.
         * For each category, gets sub-categories, items and a map
         * showing the connection between the sub-category and its items.*/
        solutionList = new ArrayList<Solution>();
        orderList = new ArrayList<Order>();

        for (Category categoryItem : categoryList)
        {
            // Temporary list of the current sıb-categories
            ArrayList<SubCategory> tempSubCategoryList = new ArrayList<>();

            // Temporary list of the current items
            ArrayList<Item> tempItemList = new ArrayList<>();

            // Temporary map
            Map<SubCategory, ArrayList<Item>> itemMap = new HashMap<SubCategory, ArrayList<Item>>();

            // categoryId == 1 means category with all items and sub-categories.
            // That's why i add all the sub-categories and items directly.
            if (categoryItem.id == 0)
            {
                itemMap = getItemMap(subCategoryList);

                solutionList.add(new Solution(categoryItem, subCategoryList, itemList, itemMap));
            }
            else
            {
                tempSubCategoryList = getSubCategoryListByCategoryId(categoryItem.id);
                tempItemList = getItemListByCategoryId(categoryItem.id);
                itemMap = getItemMap(tempSubCategoryList);

                solutionList.add(new Solution(categoryItem, tempSubCategoryList, tempItemList, itemMap));
            }
        }

        // Get the ViewPager and set it's CategoryPagerAdapter so that it can display items
        ViewPager vpItem = (ViewPager) view.findViewById(R.id.vpItem);
        CategoryPagerAdapter categoryPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager(), getActivity(), solutionList);
        vpItem.setOffscreenPageLimit(categoryPagerAdapter.getCount());
        vpItem.setAdapter(categoryPagerAdapter);

        // Give the TabLayout the ViewPager
        tabCategory = view.findViewById(R.id.tabCategory);
        tabCategory.setupWithViewPager(vpItem);

        for (int i = 0; i < tabCategory.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabCategory.getTabAt(i);
            tab.setCustomView(categoryPagerAdapter.getTabView(i));
        }

    }

    private Map<SubCategory, ArrayList<Item>> getItemMap(ArrayList<SubCategory> subCategoryList)
    {
        Map<SubCategory, ArrayList<Item>> itemMap = new HashMap<SubCategory, ArrayList<Item>>();

        for (SubCategory subCategory : subCategoryList)
        {
            itemMap.put(subCategory, getItemListBySubCategoryId(subCategory.id));
        }

        return itemMap;
    }

    private ArrayList<Item> getItemListBySubCategoryId(int subCategoryId)
    {
        ArrayList<Item> tempItemList = new ArrayList<Item>();

        for (Item item : itemList)
        {
            if (item.subCategoryId == subCategoryId)
            {
                tempItemList.add(item);
            }
        }

        return tempItemList;
    }

    private ArrayList<SubCategory> getSubCategoryListByCategoryId(int categoryId)
    {
        ArrayList<SubCategory> tempSubCategoryList = new ArrayList<SubCategory>();

        for (SubCategory subCategory : subCategoryList)
        {
            if (subCategory.categoryId == categoryId)
            {
                tempSubCategoryList.add(new SubCategory(subCategory));
            }
        }

        return tempSubCategoryList;
    }

    private ArrayList<Item> getItemListByCategoryId(int categoryId)
    {
        ArrayList<Item> tempItemList = new ArrayList<Item>();

        for (Item item : itemList)
        {
            if (item.categoryId == categoryId)
            {
                tempItemList.add(item);
            }
        }

        return tempItemList;
    }

    public void changeTab(int pos){
        if(view != null){
            tabCategory.getTabAt(pos).select();
        }
    }
}
