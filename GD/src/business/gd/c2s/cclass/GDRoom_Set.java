package business.gd.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

import business.gd.c2s.cclass.GD_define.GD_CARD_TYPE;
import business.gd.c2s.cclass.GD_define.GD_GameStatus;
import jsproto.c2s.cclass.pk.ThreeParamentVictory;

/**
 * 仙游炸棒当前局游戏信息
 * @author zaf
 *
 */
public class GDRoom_Set<T> {
	public long roomID = 0; // 房间ID
	public int  setID = 0; // 游戏局ID
	public int  setStatus = GD_GameStatus.GD_GAME_STATUS_SENDCARD.value(); // 游戏状态
	public long startTime = 0;
	public int  opPos = -1;// 当前操作位
	public boolean isSetEnd = false;
	public List<T> posInfo = new ArrayList<>(); // 一局玩家列表
	public ArrayList<Integer> opPosList = new ArrayList<>();		//出牌顺序

	public ArrayList<Integer> redPosList = new ArrayList<>();  //有明牌的玩家位置
	public int  thePostionOfTheFirstDeal =-1;//起始发牌位置
	public int  opType = GD_CARD_TYPE.GD_CARD_TYPE_NOMARL.value();//最后出牌的类型
	public int  lastOpPos = -1;//最后出牌的位置
	public List<Integer> cardList = new ArrayList<>();//最后打的牌
	public List<Integer> substituteCard = new ArrayList<>();//赖子代替牌数组
	public boolean isFirstOp = true;//是否是首出
	
	public boolean		  isRedWin = false;//是否是红方赢
	
	public int 		currStep;//当前级数
	public boolean	isRedStep;//当前级数是否是红方
	
	public int  redSteps ; //红方级数
    public int  blueSteps ; //蓝方级数
	
	public ArrayList<ThreeParamentVictory> openCardList = new ArrayList<>(); //list的size大于0是表示有翻牌 值以此是翻牌获取的者  翻牌的值  翻牌的位置
}
