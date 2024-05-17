package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_ChangeRoomNum extends BaseSendMsg {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public long roomID;
    public String roomKey;
    public int createType;
    public static SGD_ChangeRoomNum make(long roomID, String roomKey, int createType) {
        SGD_ChangeRoomNum ret = new SGD_ChangeRoomNum();
        ret.roomID = roomID;
        ret.roomKey = roomKey;
        ret.createType = createType;
        return ret;
    }
}