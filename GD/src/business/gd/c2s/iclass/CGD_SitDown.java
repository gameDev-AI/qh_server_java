package business.gd.c2s.iclass;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;

/**
 * 接收客户端数据
 * 打牌操作
 * @author zaf
 *
 */

public class CGD_SitDown extends BaseSendMsg {

	public long roomID;
    public int pos;  //位置

    public static CGD_SitDown make(long roomID,int pos) {
    	CGD_SitDown ret = new CGD_SitDown();
        ret.roomID = roomID;
        ret.pos = pos;
        return ret;
    }
}
