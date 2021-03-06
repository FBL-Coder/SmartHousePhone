package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.ParlourFourBean;

/**
 * Created by Say GoBay on 2016/8/29.
 * 组合设置--输入板配置页面适配器
 */
public class TextDeployAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ParlourFourBean> listViewItems;
    private IClick mListener;

    public TextDeployAdapter(String[] title, int keycat, String[] text, String[] deploy, Context context, IClick listener) {
        super();
        listViewItems = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        for (int i = 0; i < keycat; i++) {
            ParlourFourBean item = null;
            if (i > title.length - 1)
                item = new ParlourFourBean("按键" + i, text[0], deploy[0]);
            else
                item = new ParlourFourBean(title[i], text[0], deploy[0]);
            listViewItems.add(item);
        }
    }

    public TextDeployAdapter(List<String> title, List<String> text, List<String> deploy, Context context, IClick listener) {
        super();
        listViewItems = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mListener = listener;

        for (int i = 0; i < title.size(); i++) {
            ParlourFourBean item = new ParlourFourBean(title.get(i), text.get(i), deploy.get(i));
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
            convertView = mInflater.inflate(R.layout.parlour_four_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.parlour_four_tv);
            viewHolder.text = (Button) convertView.findViewById(R.id.parlour_four_text);
            viewHolder.deploy = (Button) convertView.findViewById(R.id.parlour_four_deploy);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(listViewItems.get(position).getTitleId());
        viewHolder.text.setText(listViewItems.get(position).getTextId());
        viewHolder.deploy.setText(listViewItems.get(position).getDeployId());
        viewHolder.text.setOnClickListener(mListener);
        viewHolder.text.setTag(position);
        viewHolder.deploy.setOnClickListener(mListener);
        viewHolder.deploy.setTag(position);
        return convertView;
    }

    public class ViewHolder {
        public TextView title;
        public Button text, deploy;
    }
}
