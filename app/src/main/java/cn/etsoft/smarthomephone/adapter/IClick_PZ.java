package cn.etsoft.smarthomephone.adapter;

import android.view.View;

import cn.etsoft.smarthomephone.pullmi.entity.IcLick_Tag;

/**
 * Created by Say GoBay on 2016/8/29.
 */
public abstract class IClick_PZ implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        IcLick_Tag tag = (IcLick_Tag) v.getTag();
        listViewItemClick(tag.getPosition(), v);
    }

    public abstract void listViewItemClick(int position, View v);

}
