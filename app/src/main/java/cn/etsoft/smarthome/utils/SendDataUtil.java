package cn.etsoft.smarthome.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareKeyOpItem;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;

import static android.content.ContentValues.TAG;

/**
 * Author：FBL  Time： 2017/6/9.
 */

public class SendDataUtil {


    public static void getGroupSetInfo() {
        String GETGROUPSETINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid()
                + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getGroupInfo.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 255" + "}";
        MyApplication.mApplication.getUdpServer().send(GETGROUPSETINFO, 66);
    }

    public static void getSafetyInfo() {
        String GETSECURITYINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_security_info.getValue() + "," + "\"subType1\": 3," + "\"subType2\": 255" + "}";
        MyApplication.mApplication.getUdpServer().send(GETSECURITYINFO, 32);
    }

    public static void setCheFangSafetyInfo() {
        String GETSECURITYINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_security_info.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 255" + "}";
        MyApplication.mApplication.getUdpServer().send(GETSECURITYINFO, 32);
    }

    public static void setBuFangSafetyInfo(int type) {
        String GETSECURITYINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_security_info.getValue() + "," + "\"subType1\": 0," + "\"subType2\":" + type + "}";
        MyApplication.mApplication.getUdpServer().send(GETSECURITYINFO, 32);
    }


    public static void getConditionInfo() {
        String GETCONDITIONINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getEnvEvents.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETCONDITIONINFO, 27);
    }


    public static void getTimerInfo() {
        String GETTIMERINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getTimerEvents.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETTIMERINFO, 17);
    }


    public static void getScene_KeysData() {
        String GETSCENEKEYSDATA = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_get_key2scene.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETSCENEKEYSDATA, 58);
    }


    public static void getInputBoardInfo() {
        String GETINPUTBOARDINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getBoards.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 1" + "}";
        MyApplication.mApplication.getUdpServer().send(GETINPUTBOARDINFO, 8);
    }


    public static void getOutBoardInfo() {
        String GETOUTBOARDINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getBoards.getValue() + "," + "\"subType1\": 1," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETOUTBOARDINFO, 8);
    }


    public static void getSceneInfo() {
        String GETSCENEINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getSceneEvents.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETSCENEINFO, 22);
    }

    public static void getDevInfo() {
        String GETDEVINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() + "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getDevsInfo.getValue() + "," + "\"subType1\": 0," + "\"subType2\": 0" + "}";
        MyApplication.mApplication.getUdpServer().send(GETDEVINFO, 3);
    }

    public static void getNetWorkInfo() {
        MyApplication.mApplication.getUdpServer().sendSeekNet(false);
        String GETNETWORKINFO = "{\"devUnitID\": \"" + GlobalVars.getDevid() +
                "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getRcuInfo.getValue() +
                "," + "\"subType1\": 0," + "\"subType2\": 0," + "\"localIP\":\"" + GlobalVars.WIFI_IP + "\"}";
        MyApplication.mApplication.getUdpServer().send(GETNETWORKINFO, 80);
        MyApplication.mApplication.setCanChangeNet(false);
    }

    public static void getHeart() {
        if ("".equals(GlobalVars.getDevid())) {
            Log.i(TAG, "UdpHeard: 心跳包  没有ID");
            return;
        }
        String GETHEART = "{\"devUnitID\": \"" + GlobalVars.getDevid() +
                "\"," + "\"datType\": " + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getHeart.getValue() +
                "," + "\"subType1\": 0," + "\"subType2\": 0," + "\"localIP\":\"" + GlobalVars.WIFI_IP + "\"}";
        Log.i(TAG, "UdpHeard: 心跳包请求 \n" + GETHEART);
        MyApplication.mApplication.getUdpServer().UdpSendMsg(GETHEART);
    }


    public static void changeNetInfo(String name, String pass, String ip, String Ip_mask,
                                     String GetWay, String Server, String macAddr, int IsState) {
        String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_setRcuInfo.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"name\":\"" + name + "\"" +
                ",\"devPass\":\"" + pass + "\"" +
                ",\"IpAddr\":\"" + ip + "\"" +
                ",\"SubMask\":\"" + Ip_mask + "\"" +
                ",\"Gateway\":\"" + GetWay + "\"" +
                ",\"centerServ\":\"" + Server + "\"" +
                ",\"roomNum\":\"\"" +
                ",\"macAddr\":\"" + macAddr + "\"" +
                ",\"bDhcp\":" + IsState +
                "}";
        Log.i("changeNetInfo", ctlStr);
        MyApplication.mApplication.getUdpServer().send(ctlStr, 1);
    }

    public static void controlDev(WareDev dev, int cmd) {
        String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"canCpuID\":\"" + dev.getCanCpuId() + "\"" +
                ",\"devType\":" + dev.getType() +
                ",\"devID\":" + dev.getDevId() +
                ",\"cmd\":" + cmd +
                "}";
        MyApplication.mApplication.getUdpServer().send(ctlStr, 4);
    }

    public static void deleteDev(WareDev dev) {
        String str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delDev.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0," +
                "\"canCpuID\":\"" + dev.getCanCpuId() + "\"," +
                "\"devType\":" + dev.getType() + "," +
                "\"devID\":" + dev.getDevId() + "," +
                "\"cmd\":" + 1 + "}";
        MyApplication.mApplication.getUdpServer().send(str, 7);
    }

    public static void executelScene(int sceneid) {
        String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_exeSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + sceneid + "}";
        MyApplication.mApplication.getUdpServer().send(str, 26);
    }

    public static void addscene(int sceneid, String name) {
        byte[] data = {0};
        try {
            data = name.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str_gb = CommonUtils.bytesToHexString(data);
        String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"sceneName\":\"" + str_gb + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_addSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + sceneid +
                ",\"devCnt\":" + 0 +
                ",\"itemAry\":[{" +
                "\"canCpuId\":\"\"" +
                ",\"devType\":" + 0 +
                ",\"devID\":" + 0 +
                ",\"bOnOff\":" + 0 +
                ",\"lmVal\":0" +
                ",\"param1\":0" +
                ",\"param2\":0" +
                "}]}";
        MyApplication.mApplication.getUdpServer().send(str, 23);
    }

    public static void deleteScene(WareSceneEvent event) {
        String name = CommonUtils.bytesToHexString(event.getSceneName().getBytes());
        String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"sceneName\":\"" + name + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + event.getEventId() +
                ",\"devCnt\":" + 0 +
                ",\"itemAry\":[{" +
                "\"canCpuId\":\"\"" +
                ",\"devType\":" + 0 +
                ",\"devID\":" + 0 +
                ",\"bOnOff\":" + 0 +
                ",\"lmVal\":0" +
                ",\"param1\":0" +
                ",\"param2\":0" +
                "}]}";
        MyApplication.mApplication.getUdpServer().send(str, 25);
    }

    public static void getKeyItemInfo(int inputKey_position, String CancupID) {
        MyApplication.getWareData().setKeyOpItems(new ArrayList<WareKeyOpItem>());
        //uid  按键所在的输入板id
        final String str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getKeyOpItems.getValue() + "," +
                "\"canCpuId\":" + "\"" + CancupID + "\"," +
                "\"subType1\":0," +
                "\"subType2\":0," +
                "\"key_index\":" + inputKey_position + "}";
        MyApplication.mApplication.getUdpServer().send(str, 11);
    }

    public static void getChnItemInfo(String uid,int devid,int typedev) {
        //uid  设备所在输出板ID
        final String str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getChnOpItems.getValue() + "," +
                "\"canCpuId\":" + "\"" + uid + "\"," +
                "\"subType1\":0," +
                "\"subType2\":0," +
                "\"devID\":" + devid + "," +
                "\"devType\":" + typedev + "}";
        MyApplication.mApplication.getUdpServer().send(str, 14);
    }
}
