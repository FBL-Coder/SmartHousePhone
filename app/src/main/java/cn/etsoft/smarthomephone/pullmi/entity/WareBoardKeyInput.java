package cn.etsoft.smarthomephone.pullmi.entity;

import java.io.Serializable;

public class WareBoardKeyInput implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8372972355485111907L;
	private String devUnitID; //12
	private String boardName; //8
	private String roomName;
	private byte boardType;
	private byte keyCnt;
	private byte bResetKey;
	private byte ledBkType;
	private String keyName[]; //6-12
	private String canCpuID; //12
	private byte keyinput;
	private int keyAllCtrlType[]; //6
	public void setCanCpuID(String canCpuID) {
		this.canCpuID = canCpuID;
	}
	public String getCanCpuID() {
		return canCpuID;
	}
	/**
	 * KeyAdapter_keyScene所需属性
	 * 选中为1，不然为0，默认为键名数组长度的int数组全为0；
	 */
	private int keyIsSelect[]; //6-12

	public String getDevUnitID() {
		return devUnitID;
	}

	public void setDevUnitID(String devUnitID) {
		this.devUnitID = devUnitID;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public byte getBoardType() {
		return boardType;
	}

	public void setBoardType(byte boardType) {
		this.boardType = boardType;
	}

	public byte getKeyCnt() {
		return keyCnt;
	}

	public void setKeyCnt(byte keyCnt) {
		this.keyCnt = keyCnt;
	}

	public byte getbResetKey() {
		return bResetKey;
	}

	public void setbResetKey(byte bResetKey) {
		this.bResetKey = bResetKey;
	}

	public byte getLedBkType() {
		return ledBkType;
	}

	public void setLedBkType(byte ledBkType) {
		this.ledBkType = ledBkType;
	}

	public String[] getKeyName() {
		return keyName;
	}

	public void setKeyName(String[] keyName) {
		this.keyName = keyName;
	}

	//解决8个按键只有6个按键名的问题
	public int[] getKeyIsSelect() {
		if (keyIsSelect == null || keyIsSelect.length < 8)
			return keyIsSelect = new int[8];
		return keyIsSelect;
	}
	public int[] getKeyAllCtrlType() {
		return keyAllCtrlType;
	}

	public void setKeyAllCtrlType(int[] keyAllCtrlType) {
		this.keyAllCtrlType = keyAllCtrlType;
	}
	public byte getKeyinput() {
		return keyinput;
	}

	public void setKeyinput(byte keyinput) {
		this.keyinput = keyinput;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomName() {
		return roomName;
	}

}
