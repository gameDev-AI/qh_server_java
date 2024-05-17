package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class CGD_GongPai extends BaseSendMsg {
	public long roomID;
	public int  card;


	public static CGD_GongPai make(long roomID, int  card) {
		CGD_GongPai ret = new CGD_GongPai();
		ret.roomID = roomID;
		ret.card = card;
		return ret;

	}
}
