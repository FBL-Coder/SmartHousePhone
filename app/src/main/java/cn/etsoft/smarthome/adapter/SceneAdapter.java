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
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 情景适配器
 */
public class SceneAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareSceneEvent> mWareSceneEvents;
    private Context context;
    int[] images = new int[]{R.drawable.comfort, R.drawable.disturbance, R.drawable.day, R.drawable.night, R.drawable.diner,
            R.drawable.replace, R.drawable.scenemodule, R.drawable.scan,
            R.drawable.shower, R.drawable.urgent, R.drawable.sleep, R.drawable.trade};


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
            convertView = mInflater.inflate(R.layout.home_gridview_item_air, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.home_gv_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.home_gv_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            viewHolder.image.setBackgroundResource(R.drawable.situationfullopen);//情景全开
        } else if (position == 1) {
            viewHolder.image.setBackgroundResource(R.drawable.situationalloff);//情景全关
        } else if (position >= 2) {
            viewHolder.image.setBackgroundResource(images[position - 2]);//其他情景
        }
        viewHolder.title.setText(mWareSceneEvents.get(position).getSceneName());

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //给点击情景按钮添加点击音效
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                createSceneEvents(mWareSceneEvents.get(position).getEventId());
            }
        });

        return convertView;

    }

    long TimeExit = 0;

    private void createSceneEvents(int eventId) {
        //连续点击，间隔小于1秒，不做反应
        if (System.currentTimeMillis() - TimeExit < 1000) {
            TimeExit = System.currentTimeMillis();
            return;
        }
        SendDataUtil.executelScene(eventId);
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
    }
}
