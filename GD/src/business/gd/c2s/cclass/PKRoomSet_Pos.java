package business.gd.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 一局中每个位置信息
 * @author zaf
 *
 */
public class PKRoomSet_Pos<T> {
	public int 			posID = 0; 					// 座号ID
	public long 		pid = 0; 					// 账号
	public List<Integer> 	cardList = new ArrayList<>();	//牌
	public List<Integer>    lastOutCardList = new ArrayList<>(); //上次打的牌
	public ArrayList<Integer> lastSubstituteCard = new ArrayList<>();//赖子代替牌数组
	public int 			lastOpType = 0;		//上次打牌的类型
	public int 			point;						//得分
	public long 		totalPoint; 				//总得分
}
