package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_PosReadyChg extends BaseSendMsg {
    // 房间ID
    private long roomID;
    // 位置
    private int pos;
    // T:准备，F:取消准备
    private boolean isReady;

    public static SGD_PosReadyChg make(long roomID, int pos, boolean isReady) {
        SGD_PosReadyChg ret = new SGD_PosReadyChg();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setReady(isReady);
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

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

}
