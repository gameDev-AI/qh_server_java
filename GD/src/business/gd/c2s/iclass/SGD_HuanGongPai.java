package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoomSet_Pos;

public class SGD_HuanGongPai extends BaseSendMsg {
	public long roomID;
	public int  huanCard;//还贡牌
	public int 	huanCardPos;//还贡牌位置
	public GDRoomSet_Pos 	huanSetPos;//还贡牌位置信息
//	public GDRoomSet_Pos 	gainSetPos;//获得还贡牌位置信息

	public static SGD_HuanGongPai make(long roomID, int  huanCardPos, int  huanCard , 
			GDRoomSet_Pos 	huanSetPos/*, GDRoomSet_Pos 	gainSetPos*/) {
		SGD_HuanGongPai ret = new SGD_HuanGongPai();
		ret.roomID = roomID;
		ret.huanCardPos = huanCardPos;
		ret.huanCard = huanCard;
		ret.huanSetPos  = huanSetPos;
//		ret.gainSetPos = gainSetPos;
		return ret;

	}
}
