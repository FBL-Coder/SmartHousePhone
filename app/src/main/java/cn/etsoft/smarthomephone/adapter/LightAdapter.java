package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareLight> wareLIght;
    private Context context;

    public LightAdapter(List<WareLight> wareLIght, Context context) {
        this.wareLIght = wareLIght;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareLIght) {
            return wareLIght.size();
        } else {
            return 0;
        }
    }

    public void notifyDataSetChanged(List<WareLight> wareLIght) {
        this.wareLIght = wareLIght;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return wareLIght.get(position);
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
            viewHolder.title.setTextColor(Color.BLACK);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wareLIght.get(position).getbOnOff() == 0) {
            viewHolder.image.setBackgroundResource(R.drawable.lightoff);//关闭
        } else {
            viewHolder.image.setBackgroundResource(R.drawable.lighton);//打开
        }
        viewHolder.title.setText(wareLIght.get(position).getDev().getDevName());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
    }
}
