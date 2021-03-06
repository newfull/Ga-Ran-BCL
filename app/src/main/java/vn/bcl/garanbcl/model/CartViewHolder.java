package vn.bcl.garanbcl.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.travijuu.numberpicker.library.NumberPicker;

import vn.bcl.garanbcl.R;

public class CartViewHolder extends RecyclerView.ViewHolder{

    public TextView cardname;
    public ImageView cardimage;
    public TextView cardprice;
    public NumberPicker cardcount;
    public ImageView carddelete;

    View mView;
    public CartViewHolder(View v) {
        super(v);
        mView = v;
        cardname = v.findViewById(R.id.cart_prtitle);
        cardimage = v.findViewById(R.id.image_cartlist);
        cardprice = v.findViewById(R.id.cart_prcontent);
        cardcount = v.findViewById(R.id.cart_prcount);
        carddelete = v.findViewById(R.id.deletecard);
    }
}