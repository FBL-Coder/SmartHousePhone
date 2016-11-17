package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.domain.StbBean;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class StbAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<StbBean> listViewItems;

    public StbAdapter(int[] image, Context context) {
        super();
        listViewItems = new ArrayList<StbBean>();
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < image.length; i++) {
            StbBean item = new StbBean( image[i]);
            listViewItems.add(item);
        }
    }
    @Override
    public int getCount() {
        if (null != listViewItems) {
            return listViewItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
            return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tv_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.tv_gv_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(listViewItems.get(position).getImageId());
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
    }
}
