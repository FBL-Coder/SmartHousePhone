package cn.etsoft.smarthome.domain;

import java.util.List;

/**
 * Author：FBL  Time： 2017/10/20.
 */

public class GroupList_Scene_KeyData {
    private static final long serialVersionUID = 9164065895025538399L;
    private String sceneName;
    private int devCnt;
    private int eventId;
    private boolean isSelect;
    private List<WareBoardKeyInput> keyInputs;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getDevCnt() {
        return devCnt;
    }

    public void setDevCnt(int devCnt) {
        this.devCnt = devCnt;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public List<WareBoardKeyInput> getKeyInputs() {
        return keyInputs;
    }

    public void setKeyInputs(List<WareBoardKeyInput> keyInputs) {
        this.keyInputs = keyInputs;
    }
}
