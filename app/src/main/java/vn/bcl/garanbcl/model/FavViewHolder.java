package vn.bcl.garanbcl.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.bcl.garanbcl.R;

public class FavViewHolder extends RecyclerView.ViewHolder{
    public TextView cardname;
    public ImageView cardimage;
    public TextView cardprice;
    public ImageView carddelete;

    View mView;
    public FavViewHolder(View v) {
        super(v);
        mView = v;
        cardname = v.findViewById(R.id.cart_prtitle);
        cardimage = v.findViewById(R.id.image_cartlist);
        cardprice = v.findViewById(R.id.cart_prcontent);
        carddelete = v.findViewById(R.id.deletecard);
    }
}