package vn.bcl.garanbcl.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import vn.bcl.garanbcl.R;
import vn.bcl.garanbcl.model.OnItemClickListener;
import vn.bcl.garanbcl.model.ProfileModel;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    Context context;
    private ArrayList<ProfileModel> profileModelArrayList;
    private OnItemClickListener listener;


    public ProfileAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList, OnItemClickListener listener) {
        this.context = context;
        this.profileModelArrayList = profileModelArrayList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {

        holder.inbox.setImageResource(profileModelArrayList.get(position).getInbox());
        holder.arrow.setImageResource(profileModelArrayList.get(position).getArrow());
        holder.txttrades.setText(profileModelArrayList.get(position).getTxttrades());
        holder.txthistory.setText(profileModelArrayList.get(position).getTxthistory());

        holder.bind(profileModelArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return profileModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView inbox,arrow;
        TextView txttrades,txthistory;

        public ViewHolder(View itemView) {
            super(itemView);

            inbox=itemView.findViewById(R.id.inbox);
            arrow=itemView.findViewById(R.id.arrow);
            txttrades=itemView.findViewById(R.id.txttrades);
            txthistory=itemView.findViewById(R.id.txthistory);
        }

        public void bind(final ProfileModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
