package business.gd.c2s.iclass;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 打牌操作
 * @author zaf
 *
 */

public class CGD_OpCard extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置
    public int opType;  //PDK_CARD_TYPE 操作类型及牌的类型
    public ArrayList<Integer> cardList;//手牌数组
    public ArrayList<Integer> substituteCard;//赖子代替牌数组
    public boolean isFlash = false;


    public static CGD_OpCard make(long roomID,int pos,int opType,  ArrayList<Integer> cardList,ArrayList<Integer> substituteCard,boolean isFlash) {
    	CGD_OpCard ret = new CGD_OpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.opType = opType;
        ret.cardList = cardList;
        ret.substituteCard = substituteCard;
        ret.isFlash = isFlash;
        return ret;
    }
}
