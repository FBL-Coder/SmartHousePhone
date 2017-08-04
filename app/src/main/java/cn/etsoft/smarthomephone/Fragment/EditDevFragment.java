package cn.etsoft.smarthomephone.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.domain.AddDevControl_Result;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.ui.Add_Dev_Activity;
import cn.etsoft.smarthomephone.ui.Devs_Detail_Activity;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by fbl on 16-11-17.
 * 设备编辑，添加，删除页面
 */
public class EditDevFragment extends Fragment implements View.OnClickListener {
    private ListView equi_control;
    private TextView add_equi;
    private Dev_Adapter adapter;
    private List<WareDev> devs;
    private int edit_dev_id = -1;
    private Dialog mDialog;
    private FragmentActivity mActivity;
    private View view_parent;
    private LayoutInflater inflater;

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(mActivity);
        //允许返回
        mDialog.setCancelable(true);
        //显示
        mDialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDialog.dismiss();
            }
        };
        //加载数据进度条，5秒数据没加载出来自动消失
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if (mDialog.isShowing()) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e + "");
                }
            }
        }).start();
    }

    public EditDevFragment(FragmentActivity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_parent = inflater.inflate(R.layout.activity_edit_dev, container, false);
        this.inflater = inflater;
        //初始化控件
        initView(view_parent);
        event();
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (what == 5 || what == 6 || what == 7) {
                    if (mDialog != null)
                        mDialog.dismiss();
                }
                if (what == 7) {
                    if (MyApplication.getWareData().getDev_result() != null
                            && MyApplication.getWareData().getDev_result().getSubType2() == 1) {

                        if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 0) {
                            for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                                if (MyApplication.getWareData().getAirConds().size() <= i && MyApplication.getWareData().getAirConds().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getAirConds().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getAirConds().remove(i);
                                }
                            }
                        }
                        if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 3) {
                            for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                                if (MyApplication.getWareData().getLights().size() <= i && MyApplication.getWareData().getLights().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getLights().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getLights().remove(i);
                                }
                            }
                        }
                        if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 4) {
                            for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                                if (MyApplication.getWareData().getCurtains().size() <= i && MyApplication.getWareData().getCurtains().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getCurtains().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getCurtains().remove(i);
                                }
                            }
                        }

                        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("profile",
                                Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String jsondata = sharedPreferences.getString(GlobalVars.getDevid(), "");

                        List<WareDev> common_dev = new ArrayList<>();
                        if (!jsondata.equals("")) {
                            common_dev = gson.fromJson(jsondata, new TypeToken<List<WareDev>>() {
                            }.getType());
                        }

                        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                            if (MyApplication.getWareData().getDevs().get(i).getType() == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType()
                                    && MyApplication.getWareData().getDevs().get(i).getDevId() == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                    && MyApplication.getWareData().getDevs().get(i).getCanCpuId().equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {

                                for (int j = 0; j < common_dev.size(); j++) {
                                    if (common_dev.get(j).getDevId() == MyApplication.getWareData().getDevs().get(i).getDevId() && common_dev.get(j).getType() == MyApplication.getWareData().getDevs().get(i).getType() && common_dev.get(j).getCanCpuId().equals(MyApplication.getWareData().getDevs().get(i).getCanCpuId())) {
                                        common_dev.remove(j);
                                    }
                                }
                                MyApplication.getWareData().getDevs().remove(i);
                            }
                        }
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        else {
                            adapter = new Dev_Adapter();
                            equi_control.setAdapter(adapter);
                        }
                        String savdata = gson.toJson(common_dev);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(GlobalVars.getDevid(), savdata);
                        edit.commit();
                        Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (what == 5) {
                    if (MyApplication.getWareData().getAddDev_result() != null
                            && MyApplication.getWareData().getAddDev_result().getSubType1() == 1 && MyApplication.getWareData().getAddDev_result().getSubType2() == 1) {
                        AddDevControl_Result result = MyApplication.getWareData().getAddDev_result();

                        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                            if (result.getDev_rows().get(0).getDevID() == MyApplication.getWareData().getDevs().get(i).getDevId() &&
                                    result.getDev_rows().get(0).getCanCpuID().equals(MyApplication.getWareData().getDevs().get(i).getCanCpuId()) &&
                                    result.getDev_rows().get(0).getDevType() == MyApplication.getWareData().getDevs().get(i).getType()) {
                                return;
                            }
                        }
                        WareDev dev1 = new WareDev();
                        if (result.getDev_rows().get(0).getDevType() == 0) {
                            WareAirCondDev dev = new WareAirCondDev();
                            dev.setPowChn(result.getDev_rows().get(0).getPowChn());
                            dev1.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getDevName())));
                            dev1.setType((byte) result.getDev_rows().get(0).getDevType());
                            dev1.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getRoomName())));
                            dev1.setCanCpuId(result.getDev_rows().get(0).getCanCpuID());
                            dev1.setDevId((byte) result.getDev_rows().get(0).getDevID());
                            dev.setDev(dev1);
                            dev.setbOnOff((byte) result.getDev_rows().get(0).getBOnOff());
                            MyApplication.getWareData().getDevs().add(dev1);
                            MyApplication.getWareData().getAirConds().add(dev);
                        } else if (result.getDev_rows().get(0).getDevType() == 3) {
                            WareLight light = new WareLight();
                            light.setPowChn((byte) result.getDev_rows().get(0).getPowChn());
                            dev1.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getDevName())));
                            dev1.setType((byte) result.getDev_rows().get(0).getDevType());
                            dev1.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getRoomName())));
                            dev1.setCanCpuId(result.getDev_rows().get(0).getCanCpuID());
                            dev1.setDevId((byte) result.getDev_rows().get(0).getDevID());
                            light.setDev(dev1);
                            light.setbOnOff((byte) result.getDev_rows().get(0).getBOnOff());
                            MyApplication.getWareData().getDevs().add(dev1);
                            MyApplication.getWareData().getLights().add(light);
                        } else if (result.getDev_rows().get(0).getDevType() == 4) {
                            WareCurtain curtain = new WareCurtain();
                            curtain.setPowChn((byte) result.getDev_rows().get(0).getPowChn());
                            dev1.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getDevName())));
                            dev1.setType((byte) result.getDev_rows().get(0).getDevType());
                            dev1.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(result.getDev_rows().get(0).getRoomName())));
                            dev1.setCanCpuId(result.getDev_rows().get(0).getCanCpuID());
                            dev1.setDevId((byte) result.getDev_rows().get(0).getDevID());
                            curtain.setDev(dev1);
                            curtain.setbOnOff((byte) result.getDev_rows().get(0).getBOnOff());
                            MyApplication.getWareData().getDevs().add(dev1);
                            MyApplication.getWareData().getCurtains().add(curtain);
                        }

                        adapter = new Dev_Adapter();
                        equi_control.setAdapter(adapter);
                        Toast.makeText(mActivity, "添加成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (what == 6) {
                    if (MyApplication.getWareData().getSaveDev_result() != null
                            && MyApplication.getWareData().getSaveDev_result().getSubType1() == 1 && MyApplication.getWareData().getSaveDev_result().getSubType2() == 1) {
                        Toast.makeText(mActivity, "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                    adapter = new Dev_Adapter();
                    equi_control.setAdapter(adapter);
                }
                MyApplication.getWareData().setDev_result(null);
            }
        });
        return view_parent;
    }

    @Override
    public void onStart() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new Dev_Adapter();
            equi_control.setAdapter(adapter);
        }
        super.onStart();
    }

    private void event() {
        adapter = new Dev_Adapter();
        equi_control.setAdapter(adapter);
        add_equi.setOnClickListener(this);

        equi_control.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mActivity, Devs_Detail_Activity.class).putExtra("id", position));
                edit_dev_id = position;
            }
        });

        equi_control.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(mActivity);
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
                        //                        {
//                            "devUnitID": "37ffdb05424e323416702443",
//                                "datType": 7,
//                                "subType1": 0,
//                                "subType2": 0,
//                                "canCpuID": "31ffdf054257313827502543",
//                                "devType": 3,
//                                "devID": 6,
//                                "cmd": 1
//                       }
                        dialog.dismiss();
                        final String chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                "\"datType\":" + 7 + "," +
                                "\"subType1\":0," +
                                "\"subType2\":0," +
                                "\"canCpuID\":\"" + devs.get(position).getCanCpuId() + "\"," +
                                "\"devType\":" + devs.get(position).getType() + "," +
                                "\"devID\":" + devs.get(position).getDevId() + "," +
                                "\"cmd\":" + 1 + "}";

                        MyApplication.sendMsg(chn_str);
                        initDialog("正在删除...");
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void initView(View view) {
        equi_control = (ListView) view.findViewById(R.id.equi_cont_list);
        add_equi = (TextView) view.findViewById(R.id.add_equi);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_equi:
                startActivity(new Intent(mActivity, Add_Dev_Activity.class));
                break;
        }
    }

    class Dev_Adapter extends BaseAdapter {

        Dev_Adapter() {
            devs = new ArrayList<>();
            for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                devs.add(MyApplication.getWareData().getDevs().get(i));
            }
        }

        @Override
        public void notifyDataSetChanged() {
            devs = new ArrayList<>();
            for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                devs.add(MyApplication.getWareData().getDevs().get(i));
            }
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
            int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0,
                    R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.equipment_listview_control_item2, null);
                viewHolder = new ViewHolder();

                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);

                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.title.setText(devs.get(position).getDevName());

            if (devs.get(position).getType() == 0)
                viewHolder.image.setImageResource(image[0]);
            else if (devs.get(position).getType() == 1)
                viewHolder.image.setImageResource(image[1]);
            else if (devs.get(position).getType() == 2)
                viewHolder.image.setImageResource(image[2]);
            else if (devs.get(position).getType() == 3)
                viewHolder.image.setImageResource(image[3]);
            else
                viewHolder.image.setImageResource(image[4]);

            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}
