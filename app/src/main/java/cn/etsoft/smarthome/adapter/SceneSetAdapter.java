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
import cn.etsoft.smarthome.domain.SceneSetBean;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public class SceneSetAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SceneSetBean> listViewItems;

    public SceneSetAdapter(int[] image, String[] title, int[] hui, Context context) {
        super();
        listViewItems = new ArrayList<SceneSetBean>();
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < image.length; i++) {
            SceneSetBean item = new SceneSetBean(image[i], title[i], hui[0]);
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
