package business.gd.c2s.cclass;

import java.util.ArrayList;

/**
 * 一局中每个位置信息
 * @author zaf
 *
 */
public class GDRoomSet_Pos<T> extends PKRoomSet_Pos<T>{

	public int 			baseScore;					//基础分
	public int 			rewardScore;				//奖励分数
	
	public boolean 		isRed; 						//红方
	public int 			finishOrder;				//游数  0为默认值
	public ArrayList<ArrayList<Integer>> liPaiList = new ArrayList<>();	//理牌链表
	/**
	 * @see business.gd.c2s.cclass.GD_define.GD_GONGFLAG
	 * */
	public int 			gongFlag;				//贡牌状态    -2:已经还贡 -1:需要还贡  0：默认值  1:需要进贡  2:已经进贡
	public int 			gongCard;               //贡牌还牌
	
	public int 			huanCard;				//还牌的牌值
	public int 			huanCardPos;			//还牌位置
	/**
	 * 竞技点
	 */
	public Double sportsPoint;
}
