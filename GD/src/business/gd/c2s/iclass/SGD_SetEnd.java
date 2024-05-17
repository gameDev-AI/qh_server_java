package business.gd.c2s.iclass;
import java.util.ArrayList;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;


public class SGD_SetEnd<T> extends BaseSendMsg {

    public long roomID;
    public int  setID = 0; // 游戏局ID
    public int state;
    public long startTime;
	public List<Integer> pointList; //得分
    public List<T> posInfo = new ArrayList<>(); // 一局玩家列表
	public boolean		  isRedWin ;//是否是红方赢
	public boolean        isRoomEnd;//房间是否结束
	public int  redSteps ; //红方级数
    public int  blueSteps ; //蓝方级数
    public int playBackCode;
    public List<Double> sportsPointList;


    public static <T>SGD_SetEnd make(long roomID,int  setID, int state,  long startTime,List<Integer> pointList,/*List<Integer> baseScoreList,List<Integer> rewardScoreList, ArrayList< ArrayList<Integer>> cardList,*/
    		boolean	isRedWin, List<T> posInfo, boolean    isRoomEnd,int  redSteps, int  blueSteps,int playBackCode,List<Double> sportsPointList) {
    	SGD_SetEnd ret = new SGD_SetEnd();
        ret.roomID = roomID;
        ret.setID = setID;
        ret.state = state;
        ret.startTime = startTime;
        ret.pointList = pointList;
        ret.isRedWin = isRedWin;
        ret.posInfo = posInfo;
        ret.isRoomEnd = isRoomEnd;
        ret.redSteps = redSteps;
        ret.blueSteps = blueSteps;
        ret.playBackCode = playBackCode;
        ret.sportsPointList = sportsPointList;
        return ret;
    }
}
