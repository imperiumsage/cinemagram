package com.cinemagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.cinemagram.util.ImageLoader;
import com.cinemagram.util.ScaleImage;

public class StaggeredViewAdapter extends ArrayAdapter<String> {

    private ImageLoader mLoader;

    public StaggeredViewAdapter(Context context, int textViewResourceId,
                            String[] objects) {
        super(context, textViewResourceId, objects);
        mLoader = new ImageLoader(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(getContext());
            convertView = layoutInflator.inflate(R.layout.row_staggered, null);
            holder = new ViewHolder();
            holder.imageView = (ScaleImage) convertView .findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        mLoader.DisplayImage(getItem(position), holder.imageView);

        return convertView;
    }

    static class ViewHolder {
        ScaleImage imageView;
    }
}