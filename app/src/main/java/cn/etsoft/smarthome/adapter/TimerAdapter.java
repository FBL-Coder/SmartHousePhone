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

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.Timer_Data;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.SwipeItemLayout;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public class TimerAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int[] image = {R.drawable.zaijiamoshi, R.drawable.waichumoshi,
            R.drawable.yingyuanmoshi, R.drawable.jiuqingmoshi,
            R.drawable.huikemoshi};
    private Context context;
    private List<Timer_Data.TimerEventRowsBean> list;

    public TimerAdapter(Context context,Timer_Data result) {
        mInflater = LayoutInflater.from(context);
        if (list == null)
            list = new ArrayList<>();
        for (int i = 0; i < result.getTimerEvent_rows().size(); i++) {
            list.add(result.getTimerEvent_rows().get(i));
        }
    }

    public void notifyDataSetChanged(Timer_Data result) {
        super.notifyDataSetChanged();
        if (list == null)
            list = new ArrayList<>();
        for (int i = 0; i < result.getTimerEvent_rows().size(); i++) {
            list.add(result.getTimerEvent_rows().get(i));
        }
    }

    @Override
    public int getCount() {
        if (null != list) {
            return list.size();
        } else {
            return 0;
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sceneset_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.sceneSet_iv);
            viewHolder.title = (TextView) convertView.findViewById(R.id.sceneSet_tv);
            viewHolder.hui = (ImageView) convertView.findViewById(R.id.sceneSet_hui);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (list.size() == 0) {
            ToastUtil.showText("没有收到防区信息");
            return convertView;
        }
        if (list.size() > 0) {
            viewHolder.image.setImageResource(image[position % 5]);
            viewHolder.title.setText(list.get(position).getTimerName());
            viewHolder.hui.setImageResource(R.drawable.huijiantou);
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView image, hui;
        public TextView title, delete;
    }
}
