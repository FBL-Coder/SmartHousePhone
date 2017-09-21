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
import cn.etsoft.smarthome.domain.GroupSet_Data;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.SwipeItemLayout;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public class CroupSetAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int[] image = {R.drawable.zaijiamoshi, R.drawable.waichumoshi,
            R.drawable.yingyuanmoshi, R.drawable.jiuqingmoshi,
            R.drawable.huikemoshi};
    private Context context;
    private List<GroupSet_Data.SecsTriggerRowsBean> list;

    public CroupSetAdapter(Context context, GroupSet_Data result) {
        mInflater = LayoutInflater.from(context);
        if (list == null)
            list = new ArrayList<>();
        for (int i = 0; i < result.getSecs_trigger_rows().size(); i++) {
            list.add(result.getSecs_trigger_rows().get(i));
        }
    }

    public void notifyDataSetChanged(GroupSet_Data result) {
        super.notifyDataSetChanged();
        if (list == null)
            list = new ArrayList<>();
        for (int i = 0; i < result.getSecs_trigger_rows().size(); i++) {
            list.add(result.getSecs_trigger_rows().get(i));
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
            View convertView1 = mInflater.inflate(R.layout.sceneset_listview_item, null);
            View convertView2 = mInflater.inflate(R.layout.equipmentdeploy_listview_item_scene, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView1.findViewById(R.id.sceneSet_iv);
            viewHolder.title = (TextView) convertView1.findViewById(R.id.sceneSet_tv);
            viewHolder.hui = (ImageView) convertView1.findViewById(R.id.sceneSet_hui);
            convertView = new SwipeItemLayout(convertView1, convertView2, null, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (list.size() == 0) {
            ToastUtil.showText("没有收到组合器信息");
            return convertView;
        }
        if (list.size() > 0) {
            viewHolder.image.setImageResource(image[position % 5]);
            viewHolder.title.setText(list.get(position).getTriggerName());
            viewHolder.hui.setImageResource(R.drawable.huijiantou);
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView image, hui;
        public TextView title, delete;
    }
}
