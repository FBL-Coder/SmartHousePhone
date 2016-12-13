package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class SceneAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareSceneEvent> mWareSceneEvents;
    private Context context;
    int[] images = new int[]{R.drawable.comfort, R.drawable.day, R.drawable.diner, R.drawable.disturbance, R.drawable.night,
            R.drawable.replace, R.drawable.scan, R.drawable.scenemodule,
            R.drawable.shower, R.drawable.sleep, R.drawable.trade, R.drawable.urgent};


    public SceneAdapter(List<WareSceneEvent> mWareSceneEvents, Context context) {
        this.mWareSceneEvents = mWareSceneEvents;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != mWareSceneEvents) {
            return mWareSceneEvents.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mWareSceneEvents.get(position);
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
            convertView = mInflater.inflate(R.layout.home_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.home_gv_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.home_gv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mWareSceneEvents.get(position).getEventld() == 0) {
            viewHolder.image.setBackgroundResource(R.drawable.situationfullopen);//情景全开
        } else if (mWareSceneEvents.get(position).getEventld() == 1) {
            viewHolder.image.setBackgroundResource(R.drawable.situationalloff);//情景全关
        } else {
            for (int i = 0; i < images.length; i++) {

                viewHolder.image.setBackgroundResource(images[i]);//其他情景
            }

        }
        viewHolder.title.setText(mWareSceneEvents.get(position).getSceneName());

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSceneEvents(mWareSceneEvents.get(position).getEventld());
            }
        });

        return convertView;

    }

    private void createSceneEvents(int eventId) {

        String exec_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_exeSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + eventId + "}";
        CommonUtils.sendMsg(exec_str);
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
    }
}
