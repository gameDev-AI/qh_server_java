package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class CGD_HuanGongPai extends BaseSendMsg {
	public long roomID;
	public int  huanCard;
//	public int 	huanCardPos;//还贡牌位置
//	public int 	gainhuanCardPos;//获得还贡牌位置

	public static CGD_HuanGongPai make(long roomID, /*int  huanCardPos, */int  huanCard/*, int gainhuanCardPos*/) {
		CGD_HuanGongPai ret = new CGD_HuanGongPai();
		ret.roomID = roomID;
//		ret.huanCardPos = huanCardPos;
		ret.huanCard = huanCard;
//		ret.gainhuanCardPos  = gainhuanCardPos;
		return ret;

	}
}
