package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneDevItem;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class ParlourGridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareDev> listViewItems;
    private int eventId;

    public ParlourGridViewAdapter(Context context, List<WareDev> listViewItems, int eventId) {
        this.listViewItems = listViewItems;
        mInflater = LayoutInflater.from(context);
        this.eventId = eventId;
    }

    @Override
    public int getCount() {
        if (null != listViewItems) {
            return listViewItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parlour_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.light_gv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(listViewItems.get(position).getDevName());

        List<WareSceneDevItem> items = null;
        for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++){
            if(eventId == MyApplication.getWareData().getSceneEvents().get(i).getEventld()){
                items = MyApplication.getWareData().getSceneEvents().get(i).getItemAry();
                break;
            }
        }
        if (items == null) {
            for (int i = 0; i < listViewItems.size(); i++) {
                if (listViewItems.get(position).getType() == UdpProPkt.E_WARE_TYPE.e_ware_light.getValue())
                    viewHolder.title.setBackgroundResource(R.drawable.lightoff);
                if (listViewItems.get(position).getType() == UdpProPkt.E_WARE_TYPE.e_ware_airCond.getValue())
                    viewHolder.title.setBackgroundResource(R.drawable.off3);
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (listViewItems.get(position).getType() == UdpProPkt.E_WARE_TYPE.e_ware_light.getValue()) {
                    if (items.get(i).getDevID() == listViewItems.get(position).getDevId()
                            && items.get(i).getbOnOff() == 1) {
                        viewHolder.title.setBackgroundResource(R.drawable.lighton);
                        break;
                    } else
                        viewHolder.title.setBackgroundResource(R.drawable.lightoff);
                }
                if (listViewItems.get(position).getType() == UdpProPkt.E_WARE_TYPE.e_ware_airCond.getValue()) {
                    if (items.get(i).getDevID() == listViewItems.get(position).getDevId()
                            && items.get(i).getbOnOff() == 1) {
                        viewHolder.title.setBackgroundResource(R.drawable.on3);
                        break;
                    } else
                        viewHolder.title.setBackgroundResource(R.drawable.off3);
                }
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView title;
    }
}
