package cn.etsoft.smarthome.pullmi.common;

public class Constants {

	public static final String DATABASE_NAME = "etong.db";
	
	public static final String CACHE_DIRECTORY = "/data/data/com.example.smarthouse/cache";
	
	public static final String DATABASE_DIRECTORY = "/data/data/com.example.smarthouse/databases";
	
	public static int sn = 100;
	
	public static enum UDP_CMD {
		UCMD_NULL(0),
		ALARM(1),
		SENDMESSAGE(3),
		REPORTSTATUS(4),
		QUERYSTATUS(5),
		SENDCOMMSRV(6),
		WRITEADDRESS(40),
		READADDRESS(41),
		WRITECOMMMENU(42),
		READCOMMMENU(43),
		WRITEHELP(50),
		READHELP(51),
		WRITESETUP(52),
		READSETUP(53),
		WRITEIDCARD(54),
		READIDCARD(55),
		BRUSHIDCARD(56),
		VIDEOTALK(150),
		VIDEOTALKTRANS(151),
		VIDEOWATCH(152),
		VIDEOWATCHTRANS(153),
		NSORDER(154),
		NSSERVERORDER(155),
		FINDEQUIP(170),
		DOWNLOADFILE(224),
		DOWNLOADIMAGE(225),
		DOWNLOADJPG(226);
//		UCMD_FREE_ARP(22)
		
		int value;
		
		UDP_CMD(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public static enum SUB_CMD {
		ASK(1),
		REPLY(2),
		CALL(1),
		LINEUSE(2),
		QUERYFAIL(3),
		CALLANSWER(4),
		CALLSTART(6),
		CALLUP(7),
		CALLDOWN(8),
		CALLCONFIRM(9),
		REMOTEOPENLOCK(10),
		FORCEIFRAME(11),
		ZOOMOUT(15),
		ZOOMIN(16),
		CALLEND(30);
		
		int value;
		
		SUB_CMD(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
}
