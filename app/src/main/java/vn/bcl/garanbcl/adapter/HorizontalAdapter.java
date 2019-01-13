package vn.bcl.garanbcl.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.bcl.garanbcl.MainActivity;
import vn.bcl.garanbcl.model.Item;
import vn.bcl.garanbcl.R;

import static vn.bcl.garanbcl.MainActivity.setTab;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>{

    private List<Item> popularFoods;
    private Context context;


    public static class HorizontalViewHolder extends RecyclerView.ViewHolder{

        LinearLayout horizontalLayout;
        RoundedImageView popularImage;
        TextView popularTitle;

        public HorizontalViewHolder(View itemView) {
            super(itemView);

            horizontalLayout = itemView.findViewById(R.id.horizontal_parent_layout);
            popularImage = itemView.findViewById(R.id.popular_food_pc);
            popularTitle = itemView.findViewById(R.id.popular_food_title);

        }
    }

    public HorizontalAdapter(List<Item> popularFoods, int horizontal_recyclerview, Context context){
        this.context = context;
        this.popularFoods = popularFoods;
    }

    @NonNull
    @Override
    public HorizontalAdapter.HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_horizontal, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapter.HorizontalViewHolder holder, final int position) {
        holder.popularTitle.setText(popularFoods.get(position).name);
        Picasso.get().load(popularFoods.get(position).url).fit().into(holder.popularImage);

        holder.horizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return popularFoods.size();
    }
}
