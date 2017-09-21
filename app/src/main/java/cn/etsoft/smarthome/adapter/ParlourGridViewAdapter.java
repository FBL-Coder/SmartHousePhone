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

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.domain.WareSceneDevItem;
import cn.etsoft.smarthome.domain.WareSetBox;
import cn.etsoft.smarthome.domain.WareTv;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class ParlourGridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<WareDev> dev_list;
    private int eventId;
    private WareAirCondDev AirCondDev;
    private WareTv TV;
    private WareSetBox TVUP;
    private WareCurtain Curtain;
    private WareLight Light;
    private List<WareAirCondDev> AicList;
    private List<WareTv> TVList;
    private List<WareSetBox> tbsList;
    private List<WareLight> lightList;
    private List<WareCurtain> curtainList;

    public ParlourGridViewAdapter(Context context, List<WareDev> listViewItems, int eventId) {
        mInflater = LayoutInflater.from(context);
        this.eventId = eventId;
        dev_list = listViewItems;
    }

    @Override
    public int getCount() {
        return dev_list.size();
    }

    public void notifyDataSetChanged(List<WareDev> listViewItems) {
        dev_list = listViewItems;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return dev_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parlour_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.equip_name);
            viewHolder.dev_all_click = (LinearLayout) convertView.findViewById(R.id.dev_all_click);
            viewHolder.type = (ImageView) convertView.findViewById(R.id.equip_type);
            viewHolder.state = (TextView) convertView.findViewById(R.id.equip_style);
            viewHolder.name.setTextColor(0xff000000);
            viewHolder.state.setTextColor(0xff000000);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(dev_list.get(position).getDevName());

        List<WareSceneDevItem> items = null;
        for (int i = 0; i < WareDataHliper.initCopyWareData().getCopyScenes().size(); i++) {
            if (eventId == WareDataHliper.initCopyWareData().getCopyScenes().get(i).getEventId()) {
                items = WareDataHliper.initCopyWareData().getCopyScenes().get(i).getItemAry();
                break;
            }
        }
        if (dev_list.size() < 1)
            return convertView;
        if (dev_list.get(position).getType() == 0) {
            AicList = MyApplication.getWareData().getAirConds();
            for (int i = 0; i < AicList.size(); i++) {
                if (dev_list.get(position).getDevId() == AicList.get(i).getDev().getDevId() &&
                        dev_list.get(position).getCanCpuId().equals(AicList.get(i).getDev().getCanCpuId())) {
                    AirCondDev = AicList.get(i);
                    viewHolder.type.setImageResource(R.drawable.kongtiao1);
                    viewHolder.state.setText("关");
                }
            }
        } else if (dev_list.get(position).getType() == 1) {
            TVList = MyApplication.getWareData().getTvs();
            for (int i = 0; i < TVList.size(); i++) {
                if (dev_list.get(position).getDevId() == TVList.get(i).getDev().getDevId() &&
                        dev_list.get(position).getCanCpuId().equals(TVList.get(i).getDev().getCanCpuId())) {
                    TV = TVList.get(i);
                    viewHolder.type.setImageResource(R.drawable.dsg);
                    viewHolder.state.setText("关");
                }
            }
        } else if (dev_list.get(position).getType() == 2) {
            tbsList = MyApplication.getWareData().getStbs();
            for (int i = 0; i < tbsList.size(); i++) {
                if (dev_list.get(position).getDevId() == tbsList.get(i).getDev().getDevId() &&
                        dev_list.get(position).getCanCpuId().equals(tbsList.get(i).getDev().getCanCpuId())) {
                    TVUP = tbsList.get(i);
                    viewHolder.type.setImageResource(R.drawable.jdhg);
                    viewHolder.state.setText("关");
                }
            }
        } else if (dev_list.get(position).getType() == 3) {
            lightList = MyApplication.getWareData().getLights();
            for (int i = 0; i < lightList.size(); i++) {
                if (dev_list.get(position).getDevId() == lightList.get(i).getDev().getDevId() &&
                        dev_list.get(position).getCanCpuId().equals(lightList.get(i).getDev().getCanCpuId())) {
                    Light = lightList.get(i);
                    viewHolder.type.setImageResource(R.drawable.dengguan);
                    viewHolder.state.setText("关");
                }
            }
        } else if (dev_list.get(position).getType() == 4) {
            curtainList = MyApplication.getWareData().getCurtains();
            for (int i = 0; i < curtainList.size(); i++) {
                if (dev_list.get(position).getDevId() == curtainList.get(i).getDev().getDevId() &&
                        dev_list.get(position).getCanCpuId().equals(curtainList.get(i).getDev().getCanCpuId())) {
                    Curtain = curtainList.get(i);
                    viewHolder.type.setImageResource(R.drawable.quanguan);
                    viewHolder.state.setText("关");
                }
            }
        }

        if (items == null) {
            if (dev_list.get(position).getType() == 0) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.type.setImageResource(R.drawable.kongtiao1);
                viewHolder.state.setText("关");
            } else if (dev_list.get(position).getType() == 1) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.type.setImageResource(R.drawable.dsg);
                viewHolder.state.setText("关");
            } else if (dev_list.get(position).getType() == 2) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.type.setImageResource(R.drawable.jdhg);
                viewHolder.state.setText("关");
            } else if (dev_list.get(position).getType() == 3) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.type.setImageResource(R.drawable.dengguan);
                viewHolder.state.setText("关");
            } else if (dev_list.get(position).getType() == 4) {
                //设备默认图标  或者是默认为关闭状态
                viewHolder.type.setImageResource(R.drawable.quanguan);
                viewHolder.state.setText("关");
            }
        } else {
            if (dev_list.get(position).getType() == 0) {
                for (int i = 0; i < items.size(); i++) {//根据设给的数据判断状态以及显示图标
                    if (items.get(i).getDevType() == 0) {
                        if (items.get(i).getDevID() == dev_list.get(position).getDevId() && items.get(i).getCanCpuID().equals(dev_list.get(position).getCanCpuId())) {
                            AicList = MyApplication.getWareData().getAirConds();
                            for (int j = 0; j < AicList.size(); j++) {
                                if (AicList.get(j).getDev().getDevId() == dev_list.get(position).getDevId() &&
                                        AicList.get(j).getDev().getCanCpuId().equals(dev_list.get(position).getCanCpuId())) {
                                    viewHolder.name.setText(AirCondDev.getDev().getDevName());
                                    AicList.get(j).setbOnOff((byte) 1);
                                    viewHolder.type.setImageResource(R.drawable.kongtiao2);
                                    viewHolder.state.setText("开");
                                }
                            }
                        }
                    }
                }
            } else if (dev_list.get(position).getType() == 1) {
                for (int i = 0; i < items.size(); i++) {//根据设给的数据判断状态以及显示图标
                    if (items.get(i).getDevType() == 1) {
                        if (items.get(i).getDevID() == dev_list.get(position).getDevId() && items.get(i).getCanCpuID().equals(dev_list.get(position).getCanCpuId())) {
                            TVList = MyApplication.getWareData().getTvs();
                            for (int j = 0; j < TVList.size(); j++) {
                                if (TVList.get(j).getDev().getDevId() == dev_list.get(position).getDevId() &&
                                        TVList.get(j).getDev().getCanCpuId().equals(dev_list.get(position).getCanCpuId())) {

                                    viewHolder.name.setText(TV.getDev().getDevName());
                                    TVList.get(j).setbOnOff((byte) 1);
                                    viewHolder.type.setImageResource(R.drawable.dsk);
                                    viewHolder.state.setText("开");
                                }
                            }
                        }
                    }
                }
            } else if (dev_list.get(position).getType() == 2) {
                for (int i = 0; i < items.size(); i++) {//根据设给的数据判断状态以及显示图标
                    if (items.get(i).getDevType() == 2) {
                        if (items.get(i).getDevID() == dev_list.get(position).getDevId() && items.get(i).getCanCpuID().equals(dev_list.get(position).getCanCpuId())) {
                            tbsList = MyApplication.getWareData().getStbs();
                            for (int j = 0; j < tbsList.size(); j++) {
                                if (tbsList.get(j).getDev().getDevId() == dev_list.get(position).getDevId() &&
                                        tbsList.get(j).getDev().getCanCpuId().equals(dev_list.get(position).getCanCpuId())) {
                                    viewHolder.name.setText(TVUP.getDev().getDevName());
                                    tbsList.get(j).setbOnOff((byte) 1);
                                    viewHolder.type.setImageResource(R.drawable.jdhk);
                                    viewHolder.state.setText("开");
                                }
                            }
                        }
                    }
                }
            } else if (dev_list.get(position).getType() == 3) {
                for (int i = 0; i < items.size(); i++) {//根据设给的数据判断状态以及显示图标
                    if (items.get(i).getDevType() == 3) {
                        if (items.get(i).getDevID() == dev_list.get(position).getDevId() && items.get(i).getCanCpuID().equals(dev_list.get(position).getCanCpuId())) {
                            lightList = MyApplication.getWareData().getLights();
                            for (int j = 0; j < lightList.size(); j++) {
                                if (lightList.get(j).getDev().getDevId() == dev_list.get(position).getDevId() &&
                                        lightList.get(j).getDev().getCanCpuId().equals(dev_list.get(position).getCanCpuId())) {
                                    viewHolder.name.setText(Light.getDev().getDevName());
                                    lightList.get(j).setbOnOff((byte) 1);
                                    viewHolder.type.setImageResource(R.drawable.dengkai);
                                    viewHolder.state.setText("开");

                                }
                            }
                        }
                    }
                }
            } else if (dev_list.get(position).getType() == 4) {
                for (int i = 0; i < items.size(); i++) {//根据设给的数据判断状态以及显示图标
                    if (items.get(i).getDevType() == 4) {
                        if (items.get(i).getDevID() == dev_list.get(position).getDevId() && items.get(i).getCanCpuID().equals(dev_list.get(position).getCanCpuId())) {
                            curtainList = MyApplication.getWareData().getCurtains();
                            for (int j = 0; j < curtainList.size(); j++) {
                                if (curtainList.get(j).getDev().getDevId() == dev_list.get(position).getDevId() &&
                                        curtainList.get(j).getDev().getCanCpuId().equals(dev_list.get(position).getCanCpuId())) {
                                    viewHolder.name.setText(Curtain.getDev().getDevName());
                                    curtainList.get(j).setbOnOff((byte) 1);
                                    viewHolder.type.setImageResource(R.drawable.quankai);
                                    viewHolder.state.setText("开");

                                }
                            }
                        }
                    }
                }
            }
        }

        viewHolder.dev_all_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dev_list.get(position).getType() == 0) {
                    for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                        if (dev_list.get(position).getDevId() == MyApplication.getWareData().getAirConds().get(i).getDev().getDevId() && dev_list.get(position).getCanCpuId().equals(MyApplication.getWareData().getAirConds().get(i).getDev().getCanCpuId())) {
                            AirCondDev = MyApplication.getWareData().getAirConds().get(i);
                            if (AirCondDev.getbOnOff() == 0) {
                                AirCondDev.setbOnOff((byte) 1);
                                MyApplication.getWareData().getAirConds().get(i).setbOnOff((byte) 1);
                                viewHolder.type.setImageResource(R.drawable.kongtiao2);
                                viewHolder.state.setText("开");
                            } else {
                                AirCondDev.setbOnOff((byte) 0);
                                MyApplication.getWareData().getAirConds().get(i).setbOnOff((byte) 0);
                                viewHolder.type.setImageResource(R.drawable.kongtiao1);
                                viewHolder.state.setText("关");
                            }
                        }
                    }
                } else if (dev_list.get(position).getType() == 1) {
                    for (int i = 0; i < MyApplication.getWareData().getTvs().size(); i++) {
                        if (dev_list.get(position).getDevId() == MyApplication.getWareData().getTvs().get(i).getDev().getDevId() && dev_list.get(position).getCanCpuId().equals(MyApplication.getWareData().getTvs().get(i).getDev().getCanCpuId())) {

                            TV = MyApplication.getWareData().getTvs().get(i);
                            if (TV.getbOnOff() == 0) {
                                TV.setbOnOff((byte) 1);
                                MyApplication.getWareData().getTvs().get(i).setbOnOff((byte) 1);
                                viewHolder.type.setImageResource(R.drawable.dsk);
                                viewHolder.state.setText("开");
                            } else {
                                TV.setbOnOff((byte) 0);
                                MyApplication.getWareData().getTvs().get(i).setbOnOff((byte) 0);
                                viewHolder.type.setImageResource(R.drawable.dsg);
                                viewHolder.state.setText("关");
                            }
                        }
                    }
                } else if (dev_list.get(position).getType() == 2) {
                    for (int i = 0; i < MyApplication.getWareData().getStbs().size(); i++) {
                        if (dev_list.get(position).getDevId() == MyApplication.getWareData().getStbs().get(i).getDev().getDevId() && dev_list.get(position).getCanCpuId().equals(MyApplication.getWareData().getStbs().get(i).getDev().getCanCpuId())) {

                            TVUP = MyApplication.getWareData().getStbs().get(i);
                            if (TVUP.getbOnOff() == 0) {
                                TVUP.setbOnOff((byte) 1);
                                MyApplication.getWareData().getStbs().get(i).setbOnOff((byte) 1);
                                viewHolder.type.setImageResource(R.drawable.jdhk);
                                viewHolder.state.setText("开");
                            } else {
                                TVUP.setbOnOff((byte) 0);
                                MyApplication.getWareData().getStbs().get(i).setbOnOff((byte) 0);
                                viewHolder.type.setImageResource(R.drawable.jdhg);
                                viewHolder.state.setText("关");
                            }
                        }
                    }
                } else if (dev_list.get(position).getType() == 3) {
                    for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                        if (dev_list.get(position).getDevId() == MyApplication.getWareData().getLights().get(i).getDev().getDevId() && dev_list.get(position).getCanCpuId().equals(MyApplication.getWareData().getLights().get(i).getDev().getCanCpuId())) {
                            Light = MyApplication.getWareData().getLights().get(i);
                            if (Light.getbOnOff() == 0) {
                                Light.setbOnOff((byte) 1);
                                MyApplication.getWareData().getLights().get(i).setbOnOff((byte) 1);
                                viewHolder.type.setImageResource(R.drawable.dengkai);
                                viewHolder.state.setText("开");
                            } else {
                                Light.setbOnOff((byte) 0);
                                MyApplication.getWareData().getLights().get(i).setbOnOff((byte) 0);
                                viewHolder.type.setImageResource(R.drawable.dengguan);
                                viewHolder.state.setText("关");
                            }
                        }
                    }
                } else if (dev_list.get(position).getType() == 4) {
                    for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                        if (dev_list.get(position).getDevId() == MyApplication.getWareData().getCurtains().get(i).getDev().getDevId() && dev_list.get(position).getCanCpuId().equals(MyApplication.getWareData().getCurtains().get(i).getDev().getCanCpuId())) {
                            Curtain = MyApplication.getWareData().getCurtains().get(i);
                            if (Curtain.getbOnOff() == 0) {
                                Curtain.setbOnOff((byte) 1);
                                MyApplication.getWareData().getCurtains().get(i).setbOnOff((byte) 1);
                                viewHolder.type.setImageResource(R.drawable.quankai);
                                viewHolder.state.setText("开");
                            } else {
                                Curtain.setbOnOff((byte) 0);
                                MyApplication.getWareData().getCurtains().get(i).setbOnOff((byte) 0);
                                viewHolder.type.setImageResource(R.drawable.quanguan);
                                viewHolder.state.setText("关");
                            }
                        }
                    }
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView name, state;
        ImageView type;
        LinearLayout dev_all_click;
    }
}
