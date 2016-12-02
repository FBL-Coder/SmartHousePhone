package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.weidget.CustomDialog;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2016/9/2.
 */
public class NetWorkActivity extends Activity implements View.OnClickListener {
    private ImageView back;
    private TextView title;
    private TextView networking;
    private ImageView add;
    private EditText name, id, pwd;
    private Button sure, cancel;
    private ListView equi_list;
    private Equi_ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
    }

    @Override
    protected void onRestart() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else
            adapter = new Equi_ListAdapter();
        equi_list.setAdapter(adapter);
        super.onRestart();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        adapter = new Equi_ListAdapter();
        networking = (TextView) findViewById(R.id.networking_title);
        networking.setText(getIntent().getStringExtra("title"));
        add = (ImageView) findViewById(R.id.network_add);
        add.setOnClickListener(this);
        equi_list = (ListView) findViewById(R.id.equi_list);
        equi_list.setAdapter(adapter);
        add.setOnClickListener(this);

        equi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //list条目点击事件；
                startActivity(new Intent(NetWorkActivity.this, SetNewWorkActivity.class).putExtra("id", position));

            }
        });

        equi_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(NetWorkActivity.this);
                builder.setTitle("提示 :");

                builder.setMessage("您确定要删除此条目吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getWareData().getRcuInfos().remove(position);
                        initView();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    public void getDialog() {
        dialog = new CustomDialog(this, R.style.customDialog, R.layout.dialog_network);
        dialog.show();
        name = (EditText) dialog.findViewById(R.id.network_et_name);
        id = (EditText) dialog.findViewById(R.id.network_et_id);
        pwd = (EditText) dialog.findViewById(R.id.network_et_pwd);
        sure = (Button) dialog.findViewById(R.id.network_btn_sure);
        cancel = (Button) dialog.findViewById(R.id.network_btn_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.network_add:
                getDialog();
                break;
            case R.id.network_btn_sure:
                String name_equi = name.getText().toString();
                String id_equi = id.getText().toString();
                String pass_equi = pwd.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                String json_rcuinfo_list = sharedPreferences.getString("list", "");
                List<RcuInfo> json_list = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(json_rcuinfo_list);

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        RcuInfo info = new RcuInfo();
                        info.setDevUnitID(object.getString("devUnitID"));
                        info.setDevUnitPass(object.getString("devUnitPass"));
                        info.setName(object.getString("name"));

                        json_list.add(info);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RcuInfo info = new RcuInfo();
                info.setDevUnitID(id_equi);
                info.setDevUnitPass(pass_equi);
                info.setName(name_equi);
                json_list.add(info);

                Gson gson = new Gson();
                String str = gson.toJson(json_list);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("list", str);
                editor.commit();
                initView();
                dialog.dismiss();
                break;
            case R.id.network_btn_cancel:
                dialog.dismiss();
                break;
        }
    }

    class Equi_ListAdapter extends BaseAdapter {
        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        List<RcuInfo> json_list;

        Equi_ListAdapter() {

            List<RcuInfo> list = MyApplication.getWareData().getRcuInfos();

            RcuInfo rcuinfo = list.get(0);

            String json_rcuinfo_list = sharedPreferences.getString("list", "");
//            [{"devUnitID":"39ffe005484d303433630443","devUnitPass":"33630443","name":"手机","rev1":0,"rev2":0,"bDhcp":0}]
            Log.i("JSON", json_rcuinfo_list);

            json_list = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(json_rcuinfo_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    RcuInfo info = new RcuInfo();
                    info.setDevUnitID(object.getString("devUnitID"));
                    info.setDevUnitPass(object.getString("devUnitPass"));
                    info.setName(object.getString("name"));
                    json_list.add(info);
                }

            } catch (JSONException e) {
//                e.printStackTrace();
                Log.i("NetWorkActivity", e + "");
            }

            for (int i = 0; i < json_list.size(); i++) {
                if (rcuinfo.getDevUnitID().equals(json_list.get(i).getDevUnitID())) {
                    json_list.set(i, rcuinfo);
                }
            }
        }


        @Override
        public int getCount() {
            return json_list.size();
        }

        @Override
        public Object getItem(int position) {
            return json_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LinearLayout.inflate(NetWorkActivity.this, R.layout.equi_list_item, null);
                viewHolder.equi_name = (TextView) convertView.findViewById(R.id.equi_name);
                viewHolder.ll_ip_ll = (LinearLayout) convertView.findViewById(R.id.ll_ip_ll);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if ("".equals(json_list.get(position).getMacAddr()) || json_list.get(position).getMacAddr() == null) {
                viewHolder.ll_ip_ll.setBackgroundColor(0xffffff);
            }else {
                viewHolder.ll_ip_ll.setBackgroundColor(0x00ff00);
            }
            viewHolder.equi_name.setText(json_list.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView equi_name;
            LinearLayout ll_ip_ll;
        }
    }
}
