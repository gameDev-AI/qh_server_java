package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 明牌
 * @author zaf
 *
 */

public class CGD_OpenCard extends BaseSendMsg {

	public long roomID;
    public int  OpenCard;//是否明牌  0:不明牌 1：明牌
    public int  multiple;//倍数

    public static CGD_OpenCard make(long roomID, int OpenCard, int multiple) {
    	CGD_OpenCard ret = new CGD_OpenCard();
        ret.roomID = roomID;
        ret.OpenCard = OpenCard;
        ret.multiple = multiple;
        return ret;
    }
}
