package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

@SuppressWarnings("serial")
public class CGD_ContinueRoom extends BaseSendMsg {
	public long roomID;
	public int continueType;

    public static CGD_ContinueRoom make(long roomID, int continueType) {
        CGD_ContinueRoom ret = new CGD_ContinueRoom();
        ret.roomID = roomID;
        ret.continueType = continueType;
        return ret;
    }
}