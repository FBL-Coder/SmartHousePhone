package cn.etsoft.smarthome.NetWorkListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;

/**
 * Created by cheng on 2016/11/28.
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    private static NetEvevtChangListener netEvevt;
    public static void setEvevt(NetEvevtChangListener evevt) {
        netEvevt = evevt;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = AppNetworkMgr.getNetworkState(MyApplication.mApplication);
            // 接口回调传过去状态的类型
            try {
                netEvevt.onNetChange(netWorkState);
                GlobalVars.setIsLAN(true);
            }catch (Exception e){
            }
        }
    }


    // 自定义接口
    public interface NetEvevtChangListener {
        void onNetChange(int netMobile);
    }
}
