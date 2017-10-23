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

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.ChnOpItem_scene;

/**
 * Author：FBL  Time： 2017/6/26.
 * 按键配设备 设置——设备适配器
 */

public class KeyAdapter_keyScene extends BaseAdapter {

    private Context mContext;
    private List<ChnOpItem_scene.Key2sceneItemBean> listData_all;
    private List<ChnOpItem_scene.Key2sceneItemBean> listData;
    private List<String> KeyNames;
    private int Scene_ID = 0;

    private int keyinpur_position_id;

    public KeyAdapter_keyScene(int Scene_ID, int keyinpur_position, Context context, boolean isShowSelect) {
        this.Scene_ID = Scene_ID;
        listData = new ArrayList<>();
        KeyNames = new ArrayList<>();
        keyinpur_position_id = keyinpur_position;
        if (MyApplication.getWareData().getKeyInputs().size() == 0)
            return;
        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyCnt(); i++) {
            if (MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyCnt()
                    < MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyName().length)
                KeyNames.add(MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyName()[i]);
            else KeyNames.add("按键" + i);
        }
        this.listData_all = WareDataHliper.initCopyWareData().getScenekeysResult().getKey2scene_item();
        mContext = context;
        Secne_ID_Select();
    }

    public void notifyDataSetChanged(int Scene_ID, int keyinpur_position, Context context, boolean isShowSelect) {
        this.Scene_ID = Scene_ID;
        listData = new ArrayList<>();
        keyinpur_position_id = keyinpur_position;
        this.listData_all = WareDataHliper.initCopyWareData().getScenekeysResult().getKey2scene_item();
        KeyNames = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyCnt(); i++) {
            if (MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyCnt()
                    < MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyName().length)
                KeyNames.add(MyApplication.getWareData().getKeyInputs().get(keyinpur_position).getKeyName()[i]);
            else KeyNames.add("按键" + i);
        }
        Secne_ID_Select();
        super.notifyDataSetChanged();
    }

    public void Secne_ID_Select() {
        for (int i = 0; i < listData_all.size(); i++) {
            if (listData_all.get(i).getEventId() == Scene_ID) {
                listData.add(listData_all.get(i));
            }
        }
    }

    @Override
    public int getCount() {
        return KeyNames.size();
    }

    @Override
    public Object getItem(int position) {
        return KeyNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoler = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            viewHoler = new ViewHolder(convertView);
            convertView.setTag(viewHoler);
        } else viewHoler = (ViewHolder) convertView.getTag();
        viewHoler.mDevSelectIv.setImageResource(R.drawable.select_no);
        viewHoler.mDevIv.setImageResource(R.drawable.key);
        for (int i = 0; i < listData.size(); i++) {
            if (position == listData.get(i).getKeyIndex()
                    && MyApplication.getWareData().getKeyInputs().get(keyinpur_position_id).getCanCpuID()
                    .equals(listData.get(i).getKeyUId())) {
                viewHoler.mDevSelectIv.setImageResource(R.drawable.select_ok);
            }
        }
        viewHoler.mCupName.setText(MyApplication.getWareData().getKeyInputs().get(keyinpur_position_id).getBoardName());
        viewHoler.mDevTvName.setText(KeyNames.get(position));
        return convertView;
    }

    static class ViewHolder {
        View view;
        ImageView mDevSelectIv;
        ImageView mDevIv;
        TextView mCupName;
        TextView mDevTvName;
        TextView mDevTvCMD;

        ViewHolder(View view) {
            this.view = view;
            this.mDevSelectIv = (ImageView) view.findViewById(R.id.Dev_select_iv);
            this.mDevIv = (ImageView) view.findViewById(R.id.Dev_iv);
            this.mCupName = (TextView) view.findViewById(R.id.CupName);
            this.mDevTvName = (TextView) view.findViewById(R.id.Dev_tv_Name);
            this.mDevTvCMD = (TextView) view.findViewById(R.id.Dev_tv_CMD);
            mDevTvCMD.setVisibility(View.GONE);
        }
    }
}
