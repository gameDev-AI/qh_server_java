package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_PosContinueGame extends BaseSendMsg {
    // 房间ID
    private long roomID;
    // 位置
    private int pos;

    public static SGD_PosContinueGame make(long roomID, int pos) {
        SGD_PosContinueGame ret = new SGD_PosContinueGame();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        return ret;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

}