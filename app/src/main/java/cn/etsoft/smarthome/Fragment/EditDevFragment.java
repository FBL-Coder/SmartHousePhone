package cn.etsoft.smarthome.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.ui.Add_Dev_Activity;
import cn.etsoft.smarthome.ui.Devs_Detail_Activity;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.Circle_Progress;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;

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
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 5 || datType == 6 || datType == 7) {
                    if (mDialog != null)
                        mDialog.dismiss();
                    if (subtype2 == 1) {
                        ToastUtil.showText("操作成功");
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        else {
                            adapter = new Dev_Adapter();
                            equi_control.setAdapter(adapter);
                        }
                    }
                }
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
                        dialog.dismiss();
                        SendDataUtil.deleteDev(devs.get(position));
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
            else if (devs.get(position).getType() == 4)
                viewHolder.image.setImageResource(image[4]);
            //TODO  新设备
//            else if (devs.get(position).getType() == 7)
//                viewHolder.image.setImageResource(0);
//            else if (devs.get(position).getType() == 9)
//                viewHolder.image.setImageResource(0);
//            else
//                viewHolder.image.setImageResource(0);
            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}
