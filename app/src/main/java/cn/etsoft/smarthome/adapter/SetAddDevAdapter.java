package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UserBean;
import cn.etsoft.smarthome.domain.WareDev;

/**
 * Author：FBL  Time： 2017/10/19.
 * 用户界面添加设备界面
 */

public class SetAddDevAdapter extends BaseAdapter {

    private List<WareDev> devs;
    private Context context;


    public SetAddDevAdapter(Context context, List<WareDev> devs) {
        this.context = context;
        this.devs = devs;
    }

    @Override
    public int getCount() {
        return devs.size();
    }

    @Override
    public Object getItem(int i) {
        return devs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.parlour_gridview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (devs.size() < 1)
            return view;
        if (devs.get(position).getType() == 0) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.kongtiao2);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.kongtiao1);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else {
                viewHolder.select_iv.setImageResource(R.drawable.select_no);
            }
        } else if (devs.get(position).getType() == 1) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.dsk);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.dsg);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (devs.get(position).getType() == 2) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.jdhk);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.jdhg);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (devs.get(position).getType() == 3) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.dengkai);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.dengguan);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (devs.get(position).getType() == 4) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.cl_dev_item_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.cl_dev_item_close);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (devs.get(position).getType() == 7) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.freshair_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.freshair_close);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (devs.get(position).getType() == 9) {
            viewHolder.name.setText(devs.get(position).getDevName());
            if (devs.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.floorheat_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.floorheat_close);
                viewHolder.state.setText("关");
            }
            if (devs.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        }
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.select_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (devs.get(position).isSelect()) {
                    devs.get(position).setSelect(false);
                    finalViewHolder.select_iv.setImageResource(R.drawable.select_no);
                } else {
                    devs.get(position).setSelect(true);
                    finalViewHolder.select_iv.setImageResource(R.drawable.select_ok);
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }


    static class ViewHolder {
        View view;
        TextView name;
        ImageView select_iv;
        ImageView type;
        TextView state;
        LinearLayout mDevAllClick;

        ViewHolder(View view) {
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.equip_name);
            this.select_iv = (ImageView) view.findViewById(R.id.select_iv);
            this.type = (ImageView) view.findViewById(R.id.equip_type);
            this.state = (TextView) view.findViewById(R.id.equip_style);
            this.mDevAllClick = (LinearLayout) view.findViewById(R.id.dev_all_click);
        }
    }
}
