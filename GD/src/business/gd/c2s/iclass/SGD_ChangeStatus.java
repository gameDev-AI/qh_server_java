package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 状态改变
 * @author zaf
 *
 */

public class SGD_ChangeStatus extends BaseSendMsg {

	public long roomID;
    public int state;  //状态

    public static SGD_ChangeStatus make(long roomID,int setStatus) {
    	SGD_ChangeStatus ret = new SGD_ChangeStatus();
        ret.roomID = roomID;
        ret.state = setStatus;
        return ret;
    }
}
