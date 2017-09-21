package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;


/**
 * Author：FBL  Time： 2017/6/22.
 * 联网模块设置 页面
 */

public class NetWork_Adapter extends BaseAdapter {
    private List<RcuInfo> list;
    private Activity mContext;
    private NetWork_Adapter adapter;

    public NetWork_Adapter(Activity context) {
        mContext = context;
        list = MyApplication.mApplication.getRcuInfoList();
        adapter = this;
    }

    @Override
    public void notifyDataSetChanged() {
        list = MyApplication.mApplication.getRcuInfoList();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHoler = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.equi_list_item, null);
            viewHoler = new ViewHolder(convertView);
            convertView.setTag(viewHoler);
        } else viewHoler = (ViewHolder) convertView.getTag();

        viewHoler.mEquiName.setText(list.get(position).getCanCpuName());

        if (list.get(position).getDevUnitID().equals(AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, "")))
            viewHoler.mEquiIvUse.setImageResource(R.drawable.select_ok);
        else viewHoler.mEquiIvUse.setImageResource(R.drawable.select_no);
        return convertView;
    }

    static class ViewHolder {
        View view;
        TextView mEquiName;
        LinearLayout mLlIpLl;
        ImageView mEquiIvUse;

        ViewHolder(View view) {
            this.view = view;
            this.mEquiName = (TextView) view.findViewById(R.id.equi_name);
            this.mLlIpLl = (LinearLayout) view.findViewById(R.id.ll_ip_ll);
            this.mEquiIvUse = (ImageView) view.findViewById(R.id.equi_iv_use);
        }
    }
}
