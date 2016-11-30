package cn.etsoft.smarthomephone.pullmi.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WareDev implements Serializable {

    private String canCpuId;
    private String devName;
    private String roomName;
    private int devType;
    private int devId;
    private int devCtrlType; // 取值范围E_DEV_TYPE

    public String getCanCpuId() {
        return canCpuId;
    }

    public void setCanCpuId(String canCpuId) {
        this.canCpuId = canCpuId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getType() {
        return devType;
    }

    public void setType(byte type) {
        this.devType = type;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(byte devId) {
        this.devId = devId;
    }

    public int getDevCtrlType() {
        return devCtrlType;
    }

    public void setDevCtrlType(byte devCtrlType) {
        this.devCtrlType = devCtrlType;
    }
}