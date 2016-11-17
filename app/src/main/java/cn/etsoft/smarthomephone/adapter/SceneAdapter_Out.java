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
import cn.etsoft.smarthomephone.domain.SceneBean;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public class SceneAdapter_Out extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SceneBean> listViewItems;

    public SceneAdapter_Out(int[] image, String[] title, int[] hui, Context context) {
        super();
        listViewItems = new ArrayList<SceneBean>();
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < image.length; i++) {
            SceneBean item = new SceneBean(image[i], title[i], hui[0]);
            listViewItems.add(item);
        }
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
            convertView = mInflater.inflate(R.layout.sceneset_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.sceneSet_iv);
            viewHolder.title = (TextView) convertView.findViewById(R.id.sceneSet_tv);
            viewHolder.hui = (ImageView) convertView.findViewById(R.id.sceneSet_hui);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(listViewItems.get(position).getImageId());
        viewHolder.title.setText(listViewItems.get(position).getTitle());
        viewHolder.hui.setImageResource(listViewItems.get(position).getHuiId());
        return convertView;

    }

    public class ViewHolder {
        private ImageView image, hui;
        public TextView title;
    }
}
