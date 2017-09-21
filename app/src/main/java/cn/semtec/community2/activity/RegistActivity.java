package cn.semtec.community2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.Http_Result;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HTTPRequest_BackCode;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.semtec.community2.model.MyHttpUtil;
import cn.semtec.community2.tool.Constants;
import cn.semtec.community2.util.CatchUtil;
import cn.semtec.community2.util.TimeCountUtil;
import cn.semtec.community2.util.ToastUtil;

public class RegistActivity extends MyBaseActivity implements View.OnClickListener {

    private View btn_back;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_verify;
    private Button btn_verify;
    private View btn_commit;
    private String cellphone;
    private String verify;
    private String password;
    private Intent intent;
    private int ADDOK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        setView();
        setListener();
        intent = getIntent();
    }

    private void setView() {
        btn_back = findViewById(R.id.btn_back);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_verify = (EditText) findViewById(R.id.et_verify);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_commit = findViewById(R.id.btn_commit);

    }

    private void setListener() {
        btn_back.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_verify:
                getVerifycode();
                break;
            case R.id.btn_commit:
                if (!CheckInput()) {
                    break;
                }
                sendToNET();
                break;
        }

    }

    private boolean CheckInput() {
        cellphone = et_phone.getText().toString();
        verify = et_verify.getText().toString();
        password = et_password.getText().toString();

        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(cellphone);
        if (!m.matches()) {
            ToastUtil.s(this, getString(R.string.regist_error));
            return false;
        }
        if (verify.length() < 4) {
            ToastUtil.s(this, getString(R.string.repickpassword_verify_error));
            return false;
        }
        Pattern w = Pattern.compile("\\w{6,12}");
        if (!w.matcher(password).matches()) {
            ToastUtil.s(this, getString(R.string.changePassword_pwtips));
            return false;
        }
        return true;
    }

    //注册
    private void sendToNET() {
        try {
            JSONObject user = new JSONObject();
            user.put("cellphone", cellphone);
            user.put("password", password);

            JSONObject json = new JSONObject();
            json.put("cellphone", cellphone);
            json.put("smscode", verify);
            json.put("user", user);

            RequestParams params = new RequestParams();
            params.addHeader("Content-type", "application/json; charset=utf-8");
            params.setHeader("Accept", "application/json");
            HttpEntity entity;
            try {
                entity = new StringEntity(json.toString(), "UTF-8");
                params.setBodyEntity(entity);
            } catch (UnsupportedEncodingException e1) {
                CatchUtil.catchM(e1);
                cancelProgress();
            }
            String url = Constants.CONTENT_NEW;
            MyHttpUtil httpUtil = new MyHttpUtil(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String mResult = responseInfo.result.toString();
                    try {
                        JSONObject jo = new JSONObject(mResult);
                        if (jo.getInt("returnCode") == ADDOK) {
                            //添加智能家居后台用户注册
                            register(et_phone,et_password);

                        } else {
                            ToastUtil.s(RegistActivity.this, jo.getString("msg"));
                            LogUtils.i(jo.getString("msg"));
                            cancelProgress();
                        }
                    } catch (JSONException e) {
                        CatchUtil.catchM(e);
                        cancelProgress();
                        ToastUtil.s(RegistActivity.this, getString(R.string.data_abnormal));
                        LogUtils.i(getString(R.string.data_abnormal));
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    cancelProgress();
                    LogUtils.i(getString(R.string.net_abnormal) + msg);
                    ToastUtil.s(RegistActivity.this, getString(R.string.net_abnormal));
                }

            });
            httpUtil.send();
        } catch (Exception e) {
            CatchUtil.catchM(e);
        }
    }

    public void register( EditText mRegisterId, EditText mRegisterPass) {
        final String id_input = mRegisterId.getText().toString();
        final String pass_input = mRegisterPass.getText().toString();

        if (!(HTTPRequest_BackCode.id_rule.matcher(id_input).matches() && HTTPRequest_BackCode.pass_rule.matcher(pass_input).matches())) {
            ToastUtil.s(this,"账号或密码输入人不正确");
            cancelProgress();
            return;
        }

        Map<String, String> param = new HashMap<>();
        param.put("userName", id_input);
        param.put("passwd", pass_input);
        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.REGISTER, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                cancelProgress();
                Log.i("REGISTER", resultDesc.getResult());
                Gson gson = new Gson();
                Http_Result result = gson.fromJson(resultDesc.getResult(), Http_Result.class);
                if (result.getCode() == HTTPRequest_BackCode.REGISTER_OK){
                    // 注册成功
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID",id_input);
                    bundle.putString("PASS",pass_input);
                    intent.putExtra("bundle",bundle);
                    setResult(0,intent);
                    finish();
                }else if (result.getCode() == HTTPRequest_BackCode.REGISTER_EXIST){
                    //账号已存在
                    ToastUtil.s(RegistActivity.this,"账号已存在，请重新输入");

                }else {
                    //注册失败
                    ToastUtil.s(RegistActivity.this,"注册失败，请稍后再试");
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                //注册失败
                cancelProgress();
                ToastUtil.s(RegistActivity.this,"注册失败，网络不可用或服务器异常");
            }
        });
    }

    // 获取验证码
    private void getVerifycode() {
        cellphone = et_phone.getText().toString();
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(cellphone);
        if (!m.matches()) {
            ToastUtil.s(this, getString(R.string.regist_error));
            return;
        }
        //倒计时
        TimeCountUtil timeCountUtil = new TimeCountUtil(this, 60000, 1000, btn_verify);
        timeCountUtil.start();
        String url = Constants.CONTENT_CELLPHONE + cellphone + Constants.CONTENT_VERIFYCODE;
        MyHttpUtil http = new MyHttpUtil(HttpRequest.HttpMethod.GET, url,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        cancelProgress();
                        if (responseInfo.statusCode == 200) {
                            String mResult = responseInfo.result.toString();
                            //获得回传的 json字符串
                            JSONObject jo;
                            try {
                                jo = new JSONObject(mResult);
                                //0为成功  <0为系统异常  其他待定
                                if (jo.getInt("returnCode") == 0) {
                                    JSONObject args = (JSONObject) jo.get("args");
                                    if (!args.isNull("smscode")) {
                                        String smscode = args.getString("smscode");
                                        ToastUtil.l(getApplication(), smscode);
                                        et_verify.setText(smscode);
                                    }
                                } else {
                                    ToastUtil.s(getApplication(), jo.getString("msg"));
                                }
                            } catch (JSONException e) {
                                CatchUtil.catchM(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        cancelProgress();
                        ToastUtil.s(RegistActivity.this, getString(R.string.net_abnormal));
                        LogUtils.i(getString(R.string.net_abnormal) + msg);
                    }
                });
        http.send();
        showProgress();
    }
}
