package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareLight;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareLight> wareLight;
    private Context context;

    public LightAdapter(List<WareLight> wareLight, Context context) {
        this.wareLight = wareLight;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareLight) {
            return wareLight.size();
        } else {
            return 0;
        }
    }

    public void notifyDataSetChanged(List<WareLight> wareLight) {
        this.wareLight = wareLight;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return wareLight.get(position);
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
            convertView = mInflater.inflate(R.layout.home_gridview_item_air, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.home_gv_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.home_gv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wareLight.get(position).getbOnOff() == 0) {
            viewHolder.image.setBackgroundResource(R.drawable.lightoff);//关闭
        } else {
            viewHolder.image.setBackgroundResource(R.drawable.lighton);//打开
        }
        viewHolder.title.setText(wareLight.get(position).getDev().getDevName());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
    }
}
