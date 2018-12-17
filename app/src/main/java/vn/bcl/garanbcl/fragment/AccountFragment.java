package vn.bcl.garanbcl.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import vn.bcl.garanbcl.LoginActivity;
import vn.bcl.garanbcl.MainActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.SplashActivity;
import vn.bcl.garanbcl.adapter.ProfileAdapter;
import vn.bcl.garanbcl.model.ProfileModel;
import vn.bcl.garanbcl.util.UserSession;


public class AccountFragment extends Fragment {

    private UserSession session;
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerview;
    private ArrayList<ProfileModel> profileModelArrayList;

    Integer inbox[]={
            R.drawable.ic_profile,
            R.drawable.ic_like,
            R.drawable.ic_inbox,
            R.drawable.ic_settings,
            R.drawable.ic_logout
    };

    Integer arrow[]={
            R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp,
            R.drawable.ic_chevron_right_black_24dp
    };
    String txttrades[]={
            "Thông tin cá nhân",
            "Danh sách yêu thích",
            "Danh sách đơn hàng",
            "Cài đặt ứng dụng",
            "Đăng xuất"
    };
    String txthistory[]={
            "Thay đổi thông tin cá nhân",
            "Xem các sản phẩm đã thích",
            "Xam danh sách đơn đặt hàng",
            "Thay đổi các tùy chỉnh",
            "Đăng xuất khỏi ứng dụng"
    };

    public AccountFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        //SharedPreference for Cart Value
        session = new UserSession(getActivity());

        checkLogin();

        recyclerview = view.findViewById(R.id.rvAccount);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        profileModelArrayList = new ArrayList<>();

        for (int i = 0; i < inbox.length; i++) {
            ProfileModel view = new ProfileModel(inbox[i],arrow[i],txttrades[i],txthistory[i]);
            profileModelArrayList.add(view);
        }
        profileAdapter = new ProfileAdapter(getActivity(), profileModelArrayList);
        recyclerview.setAdapter(profileAdapter);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null)
        {
            checkLogin();
        }

    }

    public void checkLogin(){
        if(session.isLoggedIn()){
            ((ImageView) view.findViewById(R.id.no_user)).setVisibility(ImageView.GONE);
        }
    }
}
