package vn.bcl.garanbcl.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.bcl.garanbcl.R;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private List<Integer> drawables;

    public SliderAdapter(Context context, List<Integer> color) {
        this.context = context;
        this.drawables = color;
    }

    public SliderAdapter(Context context, int[] drawables) {
        this.context = context;
        this.drawables = intArrtoList(drawables);
    }

    private List<Integer> intArrtoList(int[] ints) {
        List<Integer> intList = new ArrayList<Integer>();
        for(int i : ints)
        {
            intList.add(i);
        }
        return intList;
    }


    @Override
    public int getCount() {
        return drawables.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_full);
        imageView.setImageResource(drawables.get(position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}