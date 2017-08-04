package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;

/**
 * Created by Say GoBay on 2016/9/1.
 * 用户界面UserActivity,UserFragment的适配器
 */
public class GridViewAdapter_user extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareDev> devList;
    private Context context;

    public GridViewAdapter_user(List<WareDev> dev, Context context) {
        super();
        devList = dev;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void notifyDataSetChanged(List<WareDev> dev) {
        devList = dev;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != devList) {
            return devList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return devList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_item_user, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.equip_name);
            viewHolder.type = (ImageView) convertView.findViewById(R.id.equip_type);
            viewHolder.state = (TextView) convertView.findViewById(R.id.equip_style);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int type_dev = devList.get(position).getType();
        if (type_dev == 0) {
            for (int j = 0; j < MyApplication.getWareData().getAirConds().size(); j++) {
                WareAirCondDev AirCondDev = MyApplication.getWareData().getAirConds().get(j);
                if (devList.get(position).getCanCpuId().equals(AirCondDev.getDev().getCanCpuId()) && devList.get(position).getDevId() == AirCondDev.getDev().getDevId()) {
                    viewHolder.name.setText(AirCondDev.getDev().getDevName());
                    if (AirCondDev.getbOnOff() == 0) {
                        viewHolder.type.setImageResource(R.drawable.kongtiao1);
                        viewHolder.state.setText("关");
                    } else {
                        viewHolder.type.setImageResource(R.drawable.kongtiao2);
                        viewHolder.state.setText("开");
                    }

                }
            }
        } else if (type_dev == 1) {
            for (int j = 0; j < MyApplication.getWareData().getTvs().size(); j++) {
                WareTv tv = MyApplication.getWareData().getTvs().get(j);
                if (devList.get(position).getCanCpuId().equals(tv.getDev().getCanCpuId()) && devList.get(position).getDevId() == tv.getDev().getDevId()) {
                    viewHolder.name.setText(tv.getDev().getDevName());
                    if (tv.getbOnOff() == 0) {
                        viewHolder.type.setImageResource(R.drawable.dsg);
                        viewHolder.state.setText("关");
                    } else {
                        viewHolder.type.setImageResource(R.drawable.dsk);
                        viewHolder.state.setText("开");
                    }
                }
            }
        } else if (type_dev == 2) {
            for (int j = 0; j < MyApplication.getWareData().getStbs().size(); j++) {
                WareSetBox box = MyApplication.getWareData().getStbs().get(j);
                if (devList.get(position).getCanCpuId().equals(box.getDev().getCanCpuId()) && devList.get(position).getDevId() == box.getDev().getDevId()) {
                    viewHolder.name.setText(box.getDev().getDevName());
                    if (box.getbOnOff() == 0) {
                        viewHolder.type.setImageResource(R.drawable.jidinghe);
                        viewHolder.state.setText("关");
                    } else {
                        viewHolder.type.setImageResource(R.drawable.jidinghe1);
                        viewHolder.state.setText("开");
                    }
                }
            }
        } else if (type_dev == 3) {
            for (int j = 0; j < MyApplication.getWareData().getLights().size(); j++) {
                WareLight Light = MyApplication.getWareData().getLights().get(j);
                if (devList.get(position).getCanCpuId().equals(Light.getDev().getCanCpuId()) && devList.get(position).getDevId() == Light.getDev().getDevId()) {
                    viewHolder.name.setText(Light.getDev().getDevName());
                    if (Light.getbOnOff() == 0) {
                        viewHolder.type.setImageResource(R.drawable.dengguan);
                        viewHolder.state.setText("关");
                    } else {
                        viewHolder.type.setImageResource(R.drawable.dengkai);
                        viewHolder.state.setText("开");
                    }
                }
            }
        } else if (type_dev == 4) {
            for (int j = 0; j < MyApplication.getWareData().getCurtains().size(); j++) {
                WareCurtain Curtain = MyApplication.getWareData().getCurtains().get(j);
                if (devList.get(position).getCanCpuId().equals(Curtain.getDev().getCanCpuId()) && devList.get(position).getDevId() == Curtain.getDev().getDevId()) {
                    viewHolder.name.setText(Curtain.getDev().getDevName());
                    if (Curtain.getbOnOff() == 0) {
                        viewHolder.type.setImageResource(R.drawable.quanguan);
                        viewHolder.state.setText("关");
                    } else {
                        viewHolder.type.setImageResource(R.drawable.quankai);
                        viewHolder.state.setText("开");
                    }
                }
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView name, state;
        private ImageView type;
    }
}
