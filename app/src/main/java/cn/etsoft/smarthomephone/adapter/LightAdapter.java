package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private WareData wareData;
    private Context context;

    public LightAdapter(WareData wareData, Context context) {
        this.wareData = wareData;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareData) {
            return wareData.getLights().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return wareData.getLights().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.home_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.home_gv_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.home_gv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wareData.getLights().get(position).getbOnOff() == 0) {
            viewHolder.image.setBackgroundResource(R.drawable.lightoff);//关闭
        } else {
            viewHolder.image.setBackgroundResource(R.drawable.lighton);//打开
        }
        viewHolder.title.setText(wareData.getLights().get(position).getDev().getDevName());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
    }
}
