package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.domain.ChnOpItem_scene;

/**
 * Created by Say GoBay on 2016/9/1.
 * 高级设置-控制设置-按键情景—按键的适配器
 */
public class KeyAdapter_keyScene extends BaseAdapter {
    private Context context;
    private List<String> listData;
    private List<String> listData_all;
    private List<String> listData_beRemove;
    int mSelect = 0;   //选中项
    private int SceneId;
    private int keyInputPosition;
    private boolean IsClose = false;
    private String[] keyName;
    private List<ChnOpItem_scene.Key2sceneItemBean> items;
    private int KeyCnt;

    public KeyAdapter_keyScene(Context context, int SceneId, int keyInputPosition, boolean IsClose) {
        items = MyApplication.getWareData().getChnOpItem_scene().getKey2scene_item();
        this.SceneId = SceneId;
        this.keyInputPosition = keyInputPosition;
        this.IsClose = IsClose;
        keyName = MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyName();
        KeyCnt = MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyCnt();
        //按键名称集合
        listData = new ArrayList<>();
        listData_all = new ArrayList<>();
        //将要移除的按键名
        listData_beRemove = new ArrayList<>();

        if (KeyCnt > keyName.length) {
            for (int i = 0; i < KeyCnt; i++) {
                if (i >= keyName.length) {
                    listData.add("按键" + i);
                    listData_all.add("按键" + i);
                } else {
                    listData.add(keyName[i]);
                    listData_all.add(keyName[i]);
                }
            }
        } else {
            for (int i = 0; i < KeyCnt; i++) {
                listData.add(keyName[i]);
                listData_all.add(keyName[i]);
            }
        }
        if (IsClose) {
            //打开只看选中按键的时候，先清空赋值
            for (int k = 0; k < 8; k++) {
                try {
                    MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[k] = 0;
                } catch (Exception e) {
                    Log.e("Exception", k + "----" + e + "");
                }
            }
            //打开只看选中按键的时候，赋值
            for (int k = 0; k < items.size(); k++) {
                if (items.get(k).getCanCpuID().equals(MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getDevUnitID()) &&
                        items.get(k).getEventId() == SceneId) {
                    int index = items.get(k).getKeyIndex();
                    MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[index] = 1;
                }
            }
            //打开只看选中按键的时候，将未选中的按键去掉
            for (int i = 0; i < MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect().length; i++) {
                if (MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[i] == 0) {
                    listData_beRemove.add(listData.get(i));
                }
            }

            for (int i = 0; i < listData.size(); i++) {
                for (int j = 0; j < listData_beRemove.size(); j++) {
                    if (listData.get(i).equals(listData_beRemove.get(j)))
                        listData.remove(i);
                }
            }
        }
        this.context = context;
    }

    public void notifyDataSetChanged(List<String> listData) {
        this.listData = listData;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != listData) {
            return listData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    boolean isContain = true;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.gridview_item_light, null, false);
            viewHolder.appliance = (ImageView) convertView.findViewById(R.id.appliance);
            viewHolder.mark = (ImageView) convertView.findViewById(R.id.mark);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(listData.get(position));


        if (items == null || items.size() == 0) {
            for (int i = 0; i < listData.size(); i++) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.appliance.setImageResource(R.drawable.key);
                viewHolder.mark.setImageResource(R.drawable.select);
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getCanCpuID().equals(MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getDevUnitID()) &&
                        items.get(i).getEventId() == SceneId) {
                    int index = items.get(i).getKeyIndex();
                    //index位置的按键名称与原始按键名称进行匹配，相同的时候，此按键进行选中操作
                    if (listData.get(position).equals(listData_all.get(index))) {
                        MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[index] = 1;
                        viewHolder.mark.setImageResource(R.drawable.selected);
                        isContain = false;
                        break;
                    }
                }
            }
        }
        //解决一个情景下多个按键被选中，只显示选中一个的问题
        if (isContain) {
            MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[position] = 0;
            viewHolder.mark.setImageResource(R.drawable.select);
        }

        viewHolder.appliance.setImageResource(R.drawable.key);

        viewHolder.mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[position] == 1) {
                    viewHolder.mark.setImageResource(R.drawable.select);
                    //将状态改为未选中
                    MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[position] = 0;
                    //如果是选中，去掉选中之后，将选中的数据去掉
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCanCpuID().equals(MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getDevUnitID()) &&
                                items.get(i).getEventId() == SceneId) {
                            items.remove(i);
                        }
                    }
                } else {
                    viewHolder.mark.setImageResource(R.drawable.selected);
                    //如果是未选中，选中之后，将选中的数据添加进去
                    ChnOpItem_scene.Key2sceneItemBean item = new ChnOpItem_scene.Key2sceneItemBean();
                    item.setCanCpuID(MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getDevUnitID());
                    item.setEventId(SceneId);
                    item.setKeyIndex(position);
                    items.add(item);
                    //将状态改为选中
                    MyApplication.getWareData().getKeyInputs().get(keyInputPosition).getKeyIsSelect()[position] = 1;
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView appliance, mark;
        TextView title;
    }
}
