package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;

/**
 * Created by fbl on 16-11-17.
 * 设备编辑，添加，删除页面
 */
public class EditDevActivity extends Activity implements View.OnClickListener {
    private ListView equi_control;
    private TextView add_equi;
    private Dev_Adapter adapter;
    private List<WareDev> devs;
    private ImageView bacd;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dev);
        initView();
        event();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new Dev_Adapter();
        equi_control.setAdapter(adapter);
    }

    private void event() {
        devs = new ArrayList<>();
        devs.addAll(MyApplication.getWareData().getDevs());
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 5 || datType == 6 || datType == 7) {
                    MyApplication.mApplication.dismissLoadDialog();
                    event();
                }
            }
        });
        adapter = new Dev_Adapter();
        equi_control.setAdapter(adapter);
        add_equi.setOnClickListener(this);

        equi_control.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditDevActivity.this, Devs_Detail_Activity.class);
                intent.putExtra("cpu", devs.get(position).getCanCpuId());
                intent.putExtra("id", devs.get(position).getDevId());
                intent.putExtra("type", devs.get(position).getType());
                startActivity(intent);
            }
        });

        equi_control.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(EditDevActivity.this);
                builder.setTitle("提示 :");
                builder.setMessage("您确定要删除此设备吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SendDataUtil.deleteDev(devs.get(position));
                        MyApplication.mApplication.showLoadDialog(EditDevActivity.this);
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void initView() {
        equi_control = (ListView) findViewById(R.id.equi_cont_list);
        add_equi = (TextView) findViewById(R.id.add_equi);
        bacd = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText("设备编辑");
        bacd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_equi:
                startActivity(new Intent(this, Add_Dev_Activity.class));
                break;
        }
    }

    class Dev_Adapter extends BaseAdapter {


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return devs.size();
        }

        @Override
        public Object getItem(int position) {
            return devs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int[] image = new int[]{R.drawable.aircood_icon, R.drawable.tv_icon,
                    R.drawable.stb_icon, R.drawable.light_icon, R.drawable.curtains_icon,
                    R.drawable.freshair_icon, R.drawable.floorheat_icon};
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(EditDevActivity.this).inflate(R.layout.equipment_listview_control_item2, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            if (devs.get(position).getType() == 0) {
                viewHolder.image.setImageResource(image[0]);
                List<WareAirCondDev> Airs = MyApplication.getWareData().getAirConds();
                for (int i = 0; i < Airs.size(); i++) {
                    if (devs.get(position).getCanCpuId().equals(Airs.get(i).getDev().getCanCpuId())
                            && devs.get(position).getDevId() == Airs.get(i).getDev().getDevId() &&
                            devs.get(position).getType() == Airs.get(i).getDev().getType()) {
                        WareAirCondDev Air = Airs.get(i);
                        viewHolder.title.setText(Air.getDev().getDevName());
                    }
                }
            } else if (devs.get(position).getType() == 1)
                viewHolder.image.setImageResource(image[1]);
            else if (devs.get(position).getType() == 2)
                viewHolder.image.setImageResource(image[2]);
            else if (devs.get(position).getType() == 3) {
                viewHolder.image.setImageResource(image[3]);
                List<WareLight> lights = MyApplication.getWareData().getLights();
                for (int i = 0; i < lights.size(); i++) {
                    if (devs.get(position).getCanCpuId().equals(lights.get(i).getDev().getCanCpuId())
                            && devs.get(position).getDevId() == lights.get(i).getDev().getDevId() &&
                            devs.get(position).getType() == lights.get(i).getDev().getType()) {
                        WareLight light = lights.get(i);
                        viewHolder.title.setText(light.getDev().getDevName());
                    }
                }
            } else if (devs.get(position).getType() == 4) {
                viewHolder.image.setImageResource(image[4]);
                List<WareCurtain> curtains = MyApplication.getWareData().getCurtains();
                for (int i = 0; i < curtains.size(); i++) {
                    if (devs.get(position).getCanCpuId().equals(curtains.get(i).getDev().getCanCpuId())
                            && devs.get(position).getDevId() == curtains.get(i).getDev().getDevId() &&
                            devs.get(position).getType() == curtains.get(i).getDev().getType()) {
                        WareCurtain curtain = curtains.get(i);
                        viewHolder.title.setText(curtain.getDev().getDevName());
                    }
                }
            } else if (devs.get(position).getType() == 7) {
                viewHolder.image.setImageResource(image[5]);
                List<WareFreshAir> freshAirs = MyApplication.getWareData().getFreshAirs();
                for (int i = 0; i < freshAirs.size(); i++) {
                    if (devs.get(position).getCanCpuId().equals(freshAirs.get(i).getDev().getCanCpuId())
                            && devs.get(position).getDevId() == freshAirs.get(i).getDev().getDevId() &&
                            devs.get(position).getType() == freshAirs.get(i).getDev().getType()) {
                        WareFreshAir freshAir = freshAirs.get(i);
                        viewHolder.title.setText(freshAir.getDev().getDevName());
                    }
                }
            } else if (devs.get(position).getType() == 9) {
                viewHolder.image.setImageResource(image[6]);
                List<WareFloorHeat> floorHeats = MyApplication.getWareData().getFloorHeat();
                for (int i = 0; i < floorHeats.size(); i++) {
                    if (devs.get(position).getCanCpuId().equals(floorHeats.get(i).getDev().getCanCpuId())
                            && devs.get(position).getDevId() == floorHeats.get(i).getDev().getDevId() &&
                            devs.get(position).getType() == floorHeats.get(i).getDev().getType()) {
                        WareFloorHeat floorHeat = floorHeats.get(i);
                        viewHolder.title.setText(floorHeat.getDev().getDevName());
                    }
                }
            }
            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}
