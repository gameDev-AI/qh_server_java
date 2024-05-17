package business.gd.c2s.iclass;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoomSet_Pos;

/**
 * 接收客户端数据
 * 操作牌
 * @author zaf
 *
 */

public class SGD_OpCard extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置
    public int opType;  //PDK_CARD_TYPE 操作类型及牌的类型
    public ArrayList<Integer> cardList;
    public ArrayList<Integer> substituteCard;//赖子代替牌数组
    public int nextPos ;//下一个操作位
    public boolean turnEnd ;//是否一轮结束
    public boolean isSetEnd = false;
    public GDRoomSet_Pos  setPos;//打牌玩家信息
    public ArrayList<Integer>  huoBanHandCardList;//伙伴玩家手牌
    public boolean isFlash = false; //是否自动打牌

    public static SGD_OpCard make(long roomID,int pos,int opType, int nextPos,  ArrayList<Integer> cardList, boolean turnEnd,  boolean isSetEnd
    		,ArrayList<Integer> substituteCard
    		, GDRoomSet_Pos  setPos,ArrayList<Integer>  huoBanHandCardList,boolean isFlash) {
    	SGD_OpCard ret = new SGD_OpCard();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.opType = opType;
        ret.cardList = cardList;
        ret.nextPos = nextPos;
        ret.turnEnd = turnEnd;
        ret.isSetEnd =isSetEnd;
        ret.substituteCard = substituteCard;
        ret.setPos = setPos;
        ret.huoBanHandCardList = huoBanHandCardList;
        ret.isFlash = isFlash;
        return ret;
    }
}
