package cn.etsoft.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
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
    private WareFreshAir freshAir;
    private WareFloorHeat floorHeat;
    private List<WareAirCondDev> AicList;
    private List<WareTv> TVList;
    private List<WareSetBox> tbsList;
    private List<WareLight> lightList;
    private List<WareCurtain> curtainList;
    private List<WareFreshAir> freshAirs;
    private List<WareFloorHeat> floorHeats;

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
            viewHolder.select_iv = (ImageView) convertView.findViewById(R.id.select_iv);
            viewHolder.name.setTextColor(0xff000000);
            viewHolder.state.setTextColor(0xff000000);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(dev_list.get(position).getDevName());

        if (dev_list.size() < 1)
            return convertView;
        if (dev_list.get(position).getType() == 0) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.kongtiao2);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.kongtiao1);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else {
                viewHolder.select_iv.setImageResource(R.drawable.select_no);
            }
        } else if (dev_list.get(position).getType() == 1) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.dsk);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.dsg);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (dev_list.get(position).getType() == 2) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.jdhk);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.jdhg);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (dev_list.get(position).getType() == 3) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.dengkai);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.dengguan);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (dev_list.get(position).getType() == 4) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.cl_dev_item_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.cl_dev_item_close);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (dev_list.get(position).getType() == 7) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.freshair_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.freshair_close);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        } else if (dev_list.get(position).getType() == 9) {
            viewHolder.name.setText(dev_list.get(position).getDevName());
            if (dev_list.get(position).getbOnOff() == 1) {
                viewHolder.type.setImageResource(R.drawable.floorheat_open);
                viewHolder.state.setText("开");
            } else {
                viewHolder.type.setImageResource(R.drawable.floorheat_close);
                viewHolder.state.setText("关");
            }
            if (dev_list.get(position).isSelect()) {
                viewHolder.select_iv.setImageResource(R.drawable.select_ok);
            } else viewHolder.select_iv.setImageResource(R.drawable.select_no);
        }

        viewHolder.select_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dev_list.get(position).isSelect()) {
                    dev_list.get(position).setSelect(false);
                    viewHolder.select_iv.setImageResource(R.drawable.select_no);
                } else {
                    dev_list.get(position).setSelect(true);
                    viewHolder.select_iv.setImageResource(R.drawable.select_ok);
                }
                notifyDataSetChanged(dev_list);
            }
        });


        viewHolder.dev_all_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dev_list.get(position).getType() == 0) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.kongtiao2);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.kongtiao1);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 1) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.dsk);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.dsg);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 2) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.jdhk);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.jdhg);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 3) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.dengkai);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.dengguan);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 4) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.cl_dev_item_open);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.cl_dev_item_close);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 7) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.freshair_open);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.freshair_close);
                        viewHolder.state.setText("关");
                    }
                } else if (dev_list.get(position).getType() == 9) {
                    if (dev_list.get(position).getbOnOff() == 0) {
                        dev_list.get(position).setbOnOff((byte) 1);
                        viewHolder.type.setImageResource(R.drawable.floorheat_open);
                        viewHolder.state.setText("开");
                    } else {
                        dev_list.get(position).setbOnOff((byte) 0);
                        viewHolder.type.setImageResource(R.drawable.floorheat_close);
                        viewHolder.state.setText("关");
                    }
                }
            }
        });
        return convertView;

    }

    private class ViewHolder {
        TextView name, state;
        ImageView type, select_iv;
        LinearLayout dev_all_click;
    }
}
