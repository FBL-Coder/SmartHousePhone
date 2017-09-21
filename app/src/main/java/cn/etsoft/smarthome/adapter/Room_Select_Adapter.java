package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.R;

/**
 * 作者：FBL  时间： 2017/2/17.
 * 选择房间的适配器
 */
public class Room_Select_Adapter extends BaseAdapter {

    private Context context;
    private List<String> list;


    public Room_Select_Adapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.equipment_listview_control_item, null);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.title.setText(list.get(position));
        return convertView;
    }

    public class ViewHolder {
        public TextView title;
        public ImageView image;
    }
}
