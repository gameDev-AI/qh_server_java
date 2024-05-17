package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;

public class SGD_Voice extends BaseSendMsg {
    // 房间ID
    private long roomID;
    // 位置
    private int pos;
    // 语音地址
    private String url;

    public static SGD_Voice make(long roomID, int pos, String url) {
        SGD_Voice ret = new SGD_Voice();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setUrl(url);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

