package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;

/**
 * Author：FBL  Time： 2017/10/24.
 */

public class Scene_KeyAdapter extends BaseAdapter {

    private List<WareBoardKeyInput> inputs;
    private Activity mActivity;

    public Scene_KeyAdapter(Activity activity) {
        mActivity = activity;
        inputs = MyApplication.getWareData().getKeyInputs();
    }

    @Override
    public int getCount() {
        return inputs.size();
    }

    @Override
    public Object getItem(int i) {
        return inputs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.listview_grouplist_father_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.inputborad);
        viewHolder.mGrouplistIvIcon.setPadding(10, 10, 10, 10);
        viewHolder.mGrouplistTvTitle.setText(inputs.get(i).getBoardName());
        viewHolder.mGrouplistTvTest.setVisibility(View.GONE);
        viewHolder.mGrouplistIvEditname.setVisibility(View.GONE);
        viewHolder.mGrouplistIvSet.setVisibility(View.GONE);
        return view;
    }


    static class ViewHolder {
        View view;
        ImageView mGrouplistIvIcon;
        TextView mGrouplistTvTitle;
        TextView mGrouplistTvTest;
        ImageView mGrouplistIvEditname;
        ImageView mGrouplistIvSet;

        ViewHolder(View view) {
            this.view = view;
            this.mGrouplistIvIcon = (ImageView) view.findViewById(R.id.grouplist_iv_icon);
            this.mGrouplistTvTitle = (TextView) view.findViewById(R.id.grouplist_tv_title);
            this.mGrouplistTvTest = (TextView) view.findViewById(R.id.grouplist_tv_test);
            this.mGrouplistIvEditname = (ImageView) view.findViewById(R.id.grouplist_iv_editname);
            this.mGrouplistIvSet = (ImageView) view.findViewById(R.id.grouplist_iv_set);
        }
    }
}
