package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UserBean;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareSceneEvent;

/**
 * Author：FBL  Time： 2017/10/19.
 * 用户界面添加设备界面
 */

public class SetAddSceneAdapter extends BaseAdapter {

    private List<WareSceneEvent> sceneEvents;
    private Context context;


    public SetAddSceneAdapter(Context context, List<WareSceneEvent> sceneEvents) {
        this.context = context;
        this.sceneEvents = sceneEvents;
    }

    @Override
    public int getCount() {
        return sceneEvents.size();
    }

    @Override
    public Object getItem(int i) {
        return sceneEvents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.parlour_gridview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (sceneEvents.size() < 1)
            return view;
        viewHolder.name.setText(sceneEvents.get(position).getSceneName());
        viewHolder.type.setImageResource(R.drawable.situationfullopen);
        viewHolder.state.setText("");
        if (sceneEvents.get(position).isSelect()) {
            viewHolder.select_iv.setImageResource(R.drawable.select_ok);
        } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.select_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sceneEvents.get(position).isSelect()) {
                    sceneEvents.get(position).setSelect(false);
                    finalViewHolder.select_iv.setImageResource(R.drawable.select_no);
                } else {
                    sceneEvents.get(position).setSelect(true);
                    finalViewHolder.select_iv.setImageResource(R.drawable.select_ok);
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }


    static class ViewHolder {
        View view;
        TextView name;
        ImageView select_iv;
        ImageView type;
        TextView state;
        LinearLayout mDevAllClick;

        ViewHolder(View view) {
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.equip_name);
            this.select_iv = (ImageView) view.findViewById(R.id.select_iv);
            this.type = (ImageView) view.findViewById(R.id.equip_type);
            this.state = (TextView) view.findViewById(R.id.equip_style);
            this.mDevAllClick = (LinearLayout) view.findViewById(R.id.dev_all_click);
        }
    }
}
