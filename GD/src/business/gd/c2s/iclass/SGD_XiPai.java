package business.gd.c2s.iclass;

import cenum.ClassType;
import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_XiPai extends BaseSendMsg {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public long pid;
    public ClassType cType;
    public static SGD_XiPai make(long roomID, long pid, ClassType cType) {
        SGD_XiPai ret = new SGD_XiPai();
        ret.roomID = roomID;
        ret.pid = pid;
        ret.cType = cType;
        return ret;


    }
}