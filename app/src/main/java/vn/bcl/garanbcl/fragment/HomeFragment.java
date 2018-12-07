package vn.bcl.garanbcl.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.bcl.garanbcl.R;

class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {
    private String[] mTitles;
    private LayoutInflater mInflater;

    public SampleAdapter(String[] titles) {
        mTitles = titles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View v = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mTitles[position]);
    }

    @Override
    public int getItemCount() {
        return mTitles != null ? mTitles.length : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(android.R.id.text1);
        }

        public void bindData(String title) {
            if (title != null) {
                mTextTitle.setText(title);
            }
        }
    }
}

public class HomeFragment extends Fragment {

    private String[] mTitles = {
            "Android", "iOS", "PHP", "Ruby", "Python", "Java", "C#", "C+", "C++", "Node js",
            "React", "Rail", "Java", ".Net"
    };

    public HomeFragment() {}

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_sample);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SampleAdapter adapter = new SampleAdapter(mTitles);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
