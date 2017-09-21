package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 新风控制界面
 */
public class FreshAirAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareFreshAir> wareFreshAirs;
    private Context context;

    public FreshAirAdapter(List<WareFreshAir> wareFreshAirs, Context context) {
        this.wareFreshAirs = wareFreshAirs;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareFreshAirs) {
            return wareFreshAirs.size();
        } else {
            return 0;
        }
    }

    public void notifyDataSetChanged(List<WareFreshAir> wareFreshAirs) {
        this.wareFreshAirs = wareFreshAirs;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return wareFreshAirs.get(position);
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
            convertView = mInflater.inflate(R.layout.gridview_item_freshair, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wareFreshAirs.get(position).getbOnOff() == 0)
            viewHolder.mItemIv.setImageResource(R.drawable.freshair_close);
        else viewHolder.mItemIv.setImageResource(R.drawable.freshair_open);
        if (wareFreshAirs.get(position).getSpdSel() == 2) {
            viewHolder.mLowFreshair.setImageResource(R.drawable.freshair_low_down);
            viewHolder.mMinFreshair.setImageResource(R.drawable.freshair_min);
            viewHolder.mHigFreshair.setImageResource(R.drawable.freshair_hig);
        } else if (wareFreshAirs.get(position).getSpdSel() == 3) {
            viewHolder.mLowFreshair.setImageResource(R.drawable.freshair_low);
            viewHolder.mMinFreshair.setImageResource(R.drawable.freshair_min_dwom);
            viewHolder.mHigFreshair.setImageResource(R.drawable.freshair_hig);
        } else if (wareFreshAirs.get(position).getSpdSel() == 4) {
            viewHolder.mLowFreshair.setImageResource(R.drawable.freshair_low);
            viewHolder.mMinFreshair.setImageResource(R.drawable.freshair_min);
            viewHolder.mHigFreshair.setImageResource(R.drawable.freshair_hig_down);
        }else {
            viewHolder.mLowFreshair.setImageResource(R.drawable.freshair_low);
            viewHolder.mMinFreshair.setImageResource(R.drawable.freshair_min);
            viewHolder.mHigFreshair.setImageResource(R.drawable.freshair_hig);
        }
        viewHolder.mItemTv.setText(wareFreshAirs.get(position).getDev().getDevName());

        viewHolder.mItemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (wareFreshAirs.get(position).getbOnOff() == 1) {
                    SendDataUtil.controlDev(wareFreshAirs.get(position).getDev(), UdpProPkt.E_FRESHAIR_CMD.e_freshair_close.getValue());
                } else {
                    SendDataUtil.controlDev(wareFreshAirs.get(position).getDev(), UdpProPkt.E_FRESHAIR_CMD.e_freshair_open.getValue());
                }
            }
        });

        viewHolder.mLowFreshair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(wareFreshAirs.get(position).getDev(), UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_low.getValue());
            }
        });
        viewHolder.mMinFreshair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(wareFreshAirs.get(position).getDev(), UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_mid.getValue());
            }
        });
        viewHolder.mHigFreshair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                SendDataUtil.controlDev(wareFreshAirs.get(position).getDev(), UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_high.getValue());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        View view;
        ImageView mItemIv;
        TextView mItemTv;
        ImageView mHigFreshair;
        ImageView mMinFreshair;
        ImageView mLowFreshair;

        ViewHolder(View view) {
            this.view = view;
            this.mItemIv = (ImageView) view.findViewById(R.id.item_iv);
            this.mHigFreshair = (ImageView) view.findViewById(R.id.hig_freshair);
            this.mMinFreshair = (ImageView) view.findViewById(R.id.min_freshair);
            this.mLowFreshair = (ImageView) view.findViewById(R.id.low_freshair);
            this.mItemTv = (TextView) view.findViewById(R.id.item_tv);
        }
    }
}
