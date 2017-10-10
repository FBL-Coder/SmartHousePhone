package cn.semtec.community2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.Http_Result;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.domain.WareData;
import cn.etsoft.smarthome.pullmi.utils.Data_Cache;
import cn.etsoft.smarthome.ui.HomeActivity;
import cn.etsoft.smarthome.ui.NewWorkSetActivity;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HTTPRequest_BackCode;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.semtec.community2.MyApplication;
import cn.semtec.community2.model.LoginHelper;
import cn.semtec.community2.model.MyHttpUtil;
import cn.semtec.community2.util.ToastUtil;

import static android.content.ContentValues.TAG;

public class LoginActivity extends MyBaseActivity implements OnClickListener {

    private Button btn_login;
    private TextView btn_regist;
    private EditText et_account;
    private EditText et_password;
    private TextView btn_forget, btn_tourist;
    private String cellphone;
    private String password;
    public static LoginActivity instace;

    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MyHttpUtil.SUCCESS0:
                    MyApplication.mApplication.setVisitor(false);
                    Log.i(TAG, "handleMessage: 云对讲登陆成功");
                    login(cellphone, password);
                    break;
                case MyHttpUtil.SUCCESSELSE:
                    ToastUtil.l(LoginActivity.this, getString(R.string.login_error));
                    cancelProgress();
                    break;
                case MyHttpUtil.CATCH:
                    cancelProgress();
                    break;
                case MyHttpUtil.FAILURE:
                    cancelProgress();
                    break;
                default:
                    cancelProgress();
                    break;
            }
        }
    };

    public void login(String input_id, String input_pass) {
        if (!(HTTPRequest_BackCode.id_rule.matcher(input_id).matches() && HTTPRequest_BackCode.pass_rule.matcher(input_pass).matches())) {
            ToastUtil.s(this, "账号或密码输入人不正确");
            return;
        }
        showProgress();
        Map<String, String> param = new HashMap<>();
        param.put("userName", input_id);
        param.put("passwd", input_pass);
        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.LOGIN, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                cancelProgress();
                Log.i("LOGIN", resultDesc.getResult());
                super.onSuccess(resultDesc);
                Log.i(TAG, "onSuccess: " + resultDesc.getResult());
                Gson gson = new Gson();
                Http_Result result = gson.fromJson(resultDesc.getResult(), Http_Result.class);

                if (result.getCode() == HTTPRequest_BackCode.LOGIN_OK) {
                    // 登陆成功
                    ToastUtil.s(LoginActivity.this, "登陆成功");
                    Log.i(TAG, "智能家居onSuccess: " + resultDesc.getResult());
                    setRcuInfoList(result);
                } else if (result.getCode() == HTTPRequest_BackCode.LOGIN_ERROR) {
                    // 登陆失败
                    ToastUtil.s(LoginActivity.this, "登陆失败，请稍后再试");
                } else if (result.getCode() == HTTPRequest_BackCode.LOGIN_USER_NOTFIND) {
                    //用户不存在
                    ToastUtil.s(LoginActivity.this, "登陆失败，用户不存在");
                } else if (result.getCode() == HTTPRequest_BackCode.LOGIN_ERROR_Exception) {
                    //服务器查询失败
                    ToastUtil.s(LoginActivity.this, "登陆失败，服务器查询失败");
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                cancelProgress();
                Log.i(TAG, "onFailure: " + code + "****" + message);
                //登陆失败
                ToastUtil.s(LoginActivity.this, "登陆失败，网络不可用或服务器异常");
            }
        });
    }


    public void setRcuInfoList(Http_Result result) {
        if (result == null)
            return;
        Gson gson = new Gson();
        List<RcuInfo> rcuInfos = new ArrayList<>();
        for (int i = 0; i < result.getData().size(); i++) {
            RcuInfo rcuInfo = new RcuInfo();
            rcuInfo.setCanCpuName(result.getData().get(i).getCanCpuName());
            rcuInfo.setDevUnitID(result.getData().get(i).getDevUnitID());
            rcuInfo.setDevUnitPass(result.getData().get(i).getDevPass());
            rcuInfos.add(rcuInfo);
        }
        AppSharePreferenceMgr.put(GlobalVars.USERID_SHAREPREFERENCE, cellphone);
        AppSharePreferenceMgr.put(GlobalVars.USERPASSWORD_SHAREPREFERENCE, password);
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, gson.toJson(rcuInfos));
        if (rcuInfos.size() == 0)
            startActivity(new Intent(this, NewWorkSetActivity.class));
        else {
            AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE, result.getData().get(0).getDevUnitID());
            GlobalVars.setDevpass(result.getData().get(0).getDevPass());
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initEvent();
    }

    public void initEvent() {
        setView();
        setListener();
    }

    private void setView() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_regist = (TextView) findViewById(R.id.btn_regist);
        btn_forget = (TextView) findViewById(R.id.btn_forget);
        btn_tourist = (TextView) findViewById(R.id.btn_tourist);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_account.setText((String) AppSharePreferenceMgr.get(GlobalVars.USERID_SHAREPREFERENCE, ""));
        et_password.setText((String) AppSharePreferenceMgr.get(GlobalVars.USERPASSWORD_SHAREPREFERENCE, ""));
    }


    private void setListener() {
        btn_login.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        btn_forget.setOnClickListener(this);
        btn_tourist.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tourist:
                cn.etsoft.smarthome.MyApplication.mApplication.setVisitor(true);
                Data_Cache.writeFile(GlobalVars.getDevid(), new WareData());
                MyApplication.setNewWareData();
                GlobalVars.setDevid("");
                GlobalVars.setDevpass("");
                GlobalVars.setUserid("");
                AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");
                AppSharePreferenceMgr.put(GlobalVars.USERID_SHAREPREFERENCE, "");
                AppSharePreferenceMgr.put(GlobalVars.SAFETY_TYPE_SHAREPREFERENCE, 0);
                AppSharePreferenceMgr.put(GlobalVars.USERPASSWORD_SHAREPREFERENCE, "");
                AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, "");
                Intent intent = new Intent(this, cn.etsoft.smarthome.ui.NewWorkSetActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                cellphone = et_account.getText().toString();
                password = et_password.getText().toString();

                Pattern p = Pattern.compile("^1\\d{10}$");
                Pattern w = Pattern.compile("\\w{6,12}");
                if (!(p.matcher(cellphone).matches() && w.matcher(password).matches())) {
                    ToastUtil.s(this, getString(R.string.login_error1));
                    break;
                }
                LoginHelper loginHelper = new LoginHelper(handler);
                loginHelper.loginServer(cellphone, password);
//                login(cellphone,password);
                showProgress();
                break;
            case R.id.btn_forget:
                Intent intent3 = new Intent(this, RepickPasswordActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_regist:
                startActivityForResult(new Intent(LoginActivity.this, cn.semtec.community2.activity.RegistActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getBundleExtra("bundle");
            et_account.setText(bundle.getString("id"));
            et_password.setText(bundle.getString("pass"));
            cellphone = bundle.getString("id");
            password = bundle.getString("pass");
        }
    }

    @Override
    protected void onDestroy() {
        instace = null;
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
