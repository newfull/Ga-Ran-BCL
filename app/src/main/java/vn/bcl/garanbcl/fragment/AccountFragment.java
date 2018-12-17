package vn.bcl.garanbcl.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import vn.bcl.garanbcl.MainActivity;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.adapter.ProfileAdapter;
import vn.bcl.garanbcl.model.OnItemClickListener;
import vn.bcl.garanbcl.model.ProfileModel;


public class AccountFragment extends Fragment {

    protected FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        profileAdapter = new ProfileAdapter(getActivity(), profileModelArrayList, new OnItemClickListener() {
            @Override public void onItemClick(ProfileModel item) {
                if(item.getTxttrades().equals(txttrades[4])){
                    final Toast logout_done = Toast.makeText(getActivity(), R.string.logout_succeeded, Toast.LENGTH_LONG);
                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(getActivity())
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
                                    ((MainActivity) getActivity()).setTab(0);
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
                }
                else
                if(item.getTxttrades().equals(txttrades[4])){
                    final Toast logout_done = Toast.makeText(getActivity(), R.string.logout_succeeded, Toast.LENGTH_LONG);
                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(getActivity())
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
                                    ((MainActivity) getActivity()).setTab(0);
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
                }


            }
        });
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
        if(mAuth.getCurrentUser() != null){
            ((ImageView) view.findViewById(R.id.no_user)).setVisibility(ImageView.GONE);
        }
    }
}
