package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;
import cn.etsoft.smarthomephone.weidget.SwipeItemLayout;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public class KeySceneAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareSceneEvent> listViewItems;
    private List<WareSceneEvent> mSceneEvents;
    private int[] image = {R.drawable.zaijiamoshi, R.drawable.waichumoshi,
            R.drawable.yingyuanmoshi, R.drawable.jiuqingmoshi,
            R.drawable.huikemoshi};
    private Context context;

    public KeySceneAdapter(List<WareSceneEvent> lst, Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        listViewItems = lst;
        mSceneEvents = new ArrayList<>();
        mSceneEvents.addAll(listViewItems);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mSceneEvents.clear();
        mSceneEvents.addAll(listViewItems);
    }

    @Override
    public int getCount() {
        if (null != mSceneEvents) {
            return mSceneEvents.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mSceneEvents.get(position);
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

        if (listViewItems.size() == 0) {
            ToastUtil.showToast(context, "没有收到情景信息");
            return convertView;
        }
        if (listViewItems.size() > 0) {
            viewHolder.image.setImageResource(image[position % 5]);
            viewHolder.title.setText(mSceneEvents.get(position).getSceneName());
            viewHolder.hui.setImageResource(R.drawable.huijiantou);
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView image, hui;
        public TextView title, delete;
    }
}
