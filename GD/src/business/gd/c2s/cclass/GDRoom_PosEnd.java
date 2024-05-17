package business.gd.c2s.cclass;

/**
 * 仙游炸棒 配置
 * @author zaf
 *
 */

// 位置结束的信息
public class GDRoom_PosEnd{
	public boolean 		isRed; 						//红方
	public int 			finishOrder;				//游数  0为默认值
	public int pos = 0; //位置
	public long pid = 0;//玩家pid
	public int point = 0; // 本局积分变更
	public int baseScore = 0;//加倍
	public int score = 0;//捉的分数
	public int 			rewardScore;				//奖励分数
	public long 		totalPoint; 				//总得分
	public int		zhangShu;//张数
	/**
	 * 竞技点
	 */
	public Double sportsPoint;
	public int  redSteps ; //红方级数
	public int  blueSteps ; //蓝方级数
}
