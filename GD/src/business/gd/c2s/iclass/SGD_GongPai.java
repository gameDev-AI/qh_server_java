package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoomSet_Pos;

public class SGD_GongPai extends BaseSendMsg {
	public long roomID;
	public int  gongCard;//贡牌
	public int  gongPos;//贡牌位置
	public GDRoomSet_Pos 	setPos;//获得还贡牌信息

	public static SGD_GongPai make(long roomID, int  gongPos,  int  gongCard,GDRoomSet_Pos setPos) {
		SGD_GongPai ret = new SGD_GongPai();
		ret.roomID = roomID;
		ret.gongCard = gongCard;
		ret.gongPos = gongPos;
		ret.setPos = setPos;
		return ret;

	}
}
