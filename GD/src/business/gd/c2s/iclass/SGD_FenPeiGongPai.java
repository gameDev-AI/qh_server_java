package business.gd.c2s.iclass;

import java.util.ArrayList;
import java.util.List;

import core.db.persistence.BaseDao;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.ThreeParamentVictoryEx;

public class SGD_FenPeiGongPai extends BaseSendMsg {
	public long roomID;
	public List<ThreeParamentVictoryEx> gongPaiList;
	public List<Integer> 	cardList = new ArrayList<>();	//牌
	public int  opPos = -1;// 当前操作位

	
	public static SGD_FenPeiGongPai make(long roomID, int  opPos,List<ThreeParamentVictoryEx> gongPaiList, List<Integer> cardList) {
		SGD_FenPeiGongPai ret = new SGD_FenPeiGongPai();
		ret.roomID = roomID;
		ret.gongPaiList = gongPaiList;
		ret.cardList = cardList;
		ret.opPos = opPos;
		return ret;

	}
}
