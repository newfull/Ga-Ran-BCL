package vn.bcl.garanbcl.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.bcl.garanbcl.R;

public class NewsViewHolder extends RecyclerView.ViewHolder{
    public TextView cardname;
    public ImageView cardimage;
    public TextView cardcontent;
    public TextView carddate;

    View mView;
    public NewsViewHolder(View v) {
        super(v);
        mView = v;
        cardname = v.findViewById(R.id.cart_prtitle);
        cardimage = v.findViewById(R.id.image_cartlist);
        cardcontent = v.findViewById(R.id.cart_prcontent);
        carddate = v.findViewById(R.id.cart_prdate);
    }
}