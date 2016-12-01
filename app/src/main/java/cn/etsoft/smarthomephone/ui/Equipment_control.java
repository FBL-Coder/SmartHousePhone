package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.domain.DevControl_Result;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by fbl on 16-11-17.
 */
public class Equipment_control extends Activity implements View.OnClickListener {
    private ListView equi_control;
    private TextView title, add_equi;
    private ImageView back;
    private Dev_Adapter adapter;
    private List<WareDev> devs;
    private int edit_dev_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equi_cont);

        initView();

        event();

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    if (MyApplication.getWareData().getDev_result() != null
                            && MyApplication.getWareData().getDev_result().getSubType2() == 1) {

                        for (int i = 0; i < devs.size(); i++) {
                            if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 0) {
                                if (MyApplication.getWareData().getAirConds().size() <= i && MyApplication.getWareData().getAirConds().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getAirConds().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getAirConds().remove(i);
                                }
                            }
                            if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 0) {
                                if (MyApplication.getWareData().getLights().size() <= i && MyApplication.getWareData().getLights().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getLights().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getLights().remove(i);
                                }

                            }
                            if (MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType() == 0) {

                                if (MyApplication.getWareData().getCurtains().size() <= i && MyApplication.getWareData().getCurtains().get(i).getDev().getDevId()
                                        == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                        && MyApplication.getWareData().getCurtains().get(i).getDev().getCanCpuId()
                                        .equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                    MyApplication.getWareData().getCurtains().remove(i);
                                }
                            }
                            if (devs.get(i).getType() == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType()
                                    && devs.get(i).getDevId() == MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID()
                                    && devs.get(i).getCanCpuId().equals(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID())) {
                                devs.remove(i);

                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                                else {
                                    adapter = new Dev_Adapter();
                                    equi_control.setAdapter(adapter);
                                }
                            }
                        }
                        Toast.makeText(Equipment_control.this, "操作成功", Toast.LENGTH_SHORT).show();
                    }
                }

                if (msg.what == 5) {
                    if (MyApplication.getWareData().getDev_result() != null
                            && MyApplication.getWareData().getDev_result().getSubType2() == 1) {
                        Toast.makeText(Equipment_control.this, "操作成功", Toast.LENGTH_SHORT).show();
                        DevControl_Result result = MyApplication.getWareData().getDev_result();
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
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        else {
                            adapter = new Dev_Adapter();
                            equi_control.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(Equipment_control.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                if (msg.what == 6) {
                    if (MyApplication.getWareData().getDev_result() != null
                            && MyApplication.getWareData().getDev_result().getSubType2() == 1 && edit_dev_id != -1) {

                        WareDev dev = new WareDev();
                        dev.setDevId((byte) MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevID());
                        dev.setCanCpuId(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getCanCpuID());
                        dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getRoomName())));
                        dev.setType((byte) MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevType());
                        dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(MyApplication.getWareData().getDev_result().getDev_rows().get(0).getDevName())));
                        devs.set(edit_dev_id, dev);
                        Toast.makeText(Equipment_control.this, "操作成功", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Equipment_control.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }
                MyApplication.getWareData().setDev_result(null);
                super.handleMessage(msg);
            }
        };
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                Message message = mHandler.obtainMessage(what);
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onRestart() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new Dev_Adapter();
            equi_control.setAdapter(adapter);
        }
        super.onRestart();
    }

    private void event() {
        title.setText("设 备 控 制");
        adapter = new Dev_Adapter();
        equi_control.setAdapter(adapter);
        add_equi.setOnClickListener(this);
        back.setOnClickListener(this);

        equi_control.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Equipment_control.this, Devs_Detail_Activity.class).putExtra("id", position));
                edit_dev_id = position;
            }
        });

        equi_control.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(Equipment_control.this);
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

                        final String chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                "\"datType\":" + 7 + "," +
                                "\"subType1\":0," +
                                "\"subType2\":0," +
                                "\"canCpuID\":\"" + devs.get(position).getCanCpuId() + "\"," +
                                "\"devType\":" + devs.get(position).getType() + "," +
                                "\"devID\":" + devs.get(position).getDevId() + "," +
                                "\"cmd\":" + 1 + "}";

                        MyApplication.sendMsg(chn_str);
                        dialog.dismiss();
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
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.add_equi:
                startActivity(new Intent(Equipment_control.this, Add_Dev_Activity.class));
                break;
        }
    }

    class Dev_Adapter extends BaseAdapter {

        Dev_Adapter() {
            devs = MyApplication.getWareData().getDevs();
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
                convertView = LayoutInflater.from(Equipment_control.this).inflate(R.layout.equipment_listview_control_item, null);
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
