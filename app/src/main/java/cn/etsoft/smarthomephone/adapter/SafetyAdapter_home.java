package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.domain.Safety_Data;


/**
 * Created by Say GoBay on 2016/9/1.
 */
public class SafetyAdapter_home extends BaseAdapter {
    private List<Safety_Data.Data_Data> data_data;
    private Context context;
    private List<String> listName;

    public SafetyAdapter_home(List<Safety_Data.Data_Data> data_data, Context context) {
        this.context = context;
        this.data_data = data_data;
    }

    public void notifyDataSetChanged(List<Safety_Data.Data_Data> data_data) {
        this.data_data = data_data;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != data_data) {
            return data_data.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return data_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.listview_item_safety, null, false);
            viewHolder = new ViewHolder();
            viewHolder.safetyMessage = (TextView) convertView.findViewById(R.id.safetyMessage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int size = MyApplication.getWareData().getResult_safety().getSec_info_rows().size();
        listName = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            listName.add(MyApplication.getWareData().getResult_safety().getSec_info_rows().get(i).getSecName());
        }

        viewHolder.safetyMessage.setText(listName.get(data_data.get(position).getId() - 1) + ",于" + data_data.get(position).getYear() + "/" + data_data.get(position).getMonth() + "/" + data_data.get(position).getDay() + "/," + data_data.get(position).getH() + ":" + data_data.get(position).getM() + ":" + data_data.get(position).getS() + "被触发");

        return convertView;
    }

    private class ViewHolder {
        TextView safetyMessage;
    }
}
