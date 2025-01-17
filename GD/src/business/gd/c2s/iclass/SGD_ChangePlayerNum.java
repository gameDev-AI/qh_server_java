package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_ChangePlayerNum extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public int playerNum; // 不用设置为位置 值为-1 否侧为设置固定位置
    public long roomID;
    public int createPos;
    public int endSec;

    public static SGD_ChangePlayerNum make(long roomID, int createPos, int endSec, int playerNum) {
        SGD_ChangePlayerNum ret = new SGD_ChangePlayerNum();
        ret.roomID = roomID;
        ret.playerNum = playerNum;
        ret.createPos = createPos;
        ret.endSec = endSec;
        return ret;
    }
}
