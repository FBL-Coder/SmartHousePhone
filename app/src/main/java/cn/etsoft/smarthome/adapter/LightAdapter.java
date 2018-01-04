package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaygoo.widget.RangeSeekBar;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareLight> wareLight;
    private Context context;

    public LightAdapter(List<WareLight> wareLight, Context context) {
        this.wareLight = wareLight;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (null != wareLight) {
            return wareLight.size();
        } else {
            return 0;
        }
    }

    public void notifyDataSetChanged(List<WareLight> wareLight) {
        this.wareLight = wareLight;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return wareLight.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.home_gridview_item_light, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.Control_GridView_Item_Name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.Control_GridView_Item_IV);
            viewHolder.seekBar = (RangeSeekBar) convertView.findViewById(R.id.Control_GridView_Item_Slide);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (wareLight.size() == 0 || position > wareLight.size() - 1)
            return convertView;

        if (wareLight.get(position).getbTuneEn() == 1) {
            viewHolder.seekBar.setVisibility(View.VISIBLE);
            if (wareLight.get(position).getLmVal() == 9)
                viewHolder.seekBar.setValue(wareLight.get(position).getLmVal() - 1);
            else viewHolder.seekBar.setValue(wareLight.get(position).getLmVal());
        } else viewHolder.seekBar.setVisibility(View.INVISIBLE);


        if (wareLight.get(position).getbOnOff() == 0) {
            viewHolder.image.setImageResource(R.drawable.lightoff);//关闭
        } else {
            viewHolder.image.setImageResource(R.drawable.lighton);//打开
        }
        viewHolder.title.setText(wareLight.get(position).getDev().getDevName());

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (wareLight.get(position).getbOnOff() == 0)
                    SendDataUtil.controlDev(wareLight.get(position).getDev(), 0);
                else SendDataUtil.controlDev(wareLight.get(position).getDev(), 1);
            }
        });


        viewHolder.seekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            int min = 0;

            @Override
            public void onRangeChanged(RangeSeekBar rangeSeekBar, float v, float v1, boolean b) {
                min = (int) v;
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar, boolean b) {
                if (min < wareLight.get(position).getLmVal())
                    SendDataUtil.controlLight(wareLight.get(position), UdpProPkt.E_LGT_CMD.e_lgt_dark.getValue(), min);
                else if (min > wareLight.get(position).getLmVal()) {
                    if (min == 8)
                        min = 9;
                    SendDataUtil.controlLight(wareLight.get(position), UdpProPkt.E_LGT_CMD.e_lgt_bright.getValue(), min);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
        RangeSeekBar seekBar;
    }
}
