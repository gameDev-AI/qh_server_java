package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_ChangePlayerNumAgree extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public int pos;
    public boolean agreeChange;
    public static SGD_ChangePlayerNumAgree make(long roomID, int pos, boolean agreeChange) {
        SGD_ChangePlayerNumAgree ret = new SGD_ChangePlayerNumAgree();
        ret.roomID = roomID;
        ret.pos = pos;
        ret.agreeChange = agreeChange;
        return ret;


    }
}