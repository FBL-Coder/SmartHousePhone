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
import cn.etsoft.smarthome.domain.WareFloorHeat;

/**
 * Created by Say GoBay on 2016/9/1.
 * 新风控制界面
 */
public class FloorHeatAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareFloorHeat> wareFloorHeats;
    private Context context;

    public FloorHeatAdapter(List<WareFloorHeat> wareFloorHeats, Context context) {
        this.wareFloorHeats = wareFloorHeats;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareFloorHeats) {
            return wareFloorHeats.size();
        } else {
            return 0;
        }
    }

    public void notifyDataSetChanged(List<WareFloorHeat> wareFloorHeats) {
        this.wareFloorHeats = wareFloorHeats;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return wareFloorHeats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.home_gridview_item_air, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wareFloorHeats.get(position).getbOnOff() == 0)
            viewHolder.mHomeGvImage.setImageResource(R.drawable.floorheat_close);
        else viewHolder.mHomeGvImage.setImageResource(R.drawable.floorheat_open);
        viewHolder.mHomeGvTitle.setText(wareFloorHeats.get(position).getDev().getDevName());

        return convertView;
    }

    static class ViewHolder {
        View view;
        ImageView mHomeGvImage;
        TextView mHomeGvTitle;

        ViewHolder(View view) {
            this.view = view;
            this.mHomeGvImage = (ImageView) view.findViewById(R.id.home_gv_image);
            this.mHomeGvTitle = (TextView) view.findViewById(R.id.home_gv_title);
        }
    }
}
