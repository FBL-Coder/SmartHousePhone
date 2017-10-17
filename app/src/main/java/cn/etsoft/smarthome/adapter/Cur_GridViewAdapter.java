package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.GridViewBean;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 窗帘控制适配器
 */
public class Cur_GridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareCurtain> curtains;

    public Cur_GridViewAdapter(List<WareCurtain> list,Context context) {
        super();
        curtains = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return curtains.size();
    }

    @Override
    public Object getItem(int position) {
        return curtains.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoler = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cur_gridview_item, null);
            viewHoler = new ViewHolder(convertView);
            convertView.setTag(viewHoler);
        } else viewHoler = (ViewHolder) convertView.getTag();

        viewHoler.mControlGridViewItemName.setText(curtains.get(position).getDev().getDevName());
        viewHoler.mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(curtains.get(position).getDev(),UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue());
            }
        });
        viewHoler.mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(curtains.get(position).getDev(),UdpProPkt.E_CURT_CMD.e_curt_stop.getValue());
            }
        });
        viewHoler.mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(curtains.get(position).getDev(), UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        View view;
        TextView mControlGridViewItemName;
        ImageView mControlGridViewItemIV;
        ImageView mOpen;
        ImageView mStop;
        ImageView mClose;

        ViewHolder(View view) {
            this.view = view;
            this.mControlGridViewItemName = (TextView) view.findViewById(R.id.Control_GridView_Item_Name);
            this.mControlGridViewItemIV = (ImageView) view.findViewById(R.id.Control_GridView_Item_IV);
            this.mOpen = (ImageView) view.findViewById(R.id.open);
            this.mStop = (ImageView) view.findViewById(R.id.stop);
            this.mClose = (ImageView) view.findViewById(R.id.close);
        }
    }
}
