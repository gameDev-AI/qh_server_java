package business.gd.c2s.iclass;

import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.room.RoomPosInfo;

import java.util.List;

/**
 * 干瞪眼更新位置
 * @author Administrator
 *
 */
public class SGD_PosUpdate extends BaseSendMsg {
    // 房间ID
    private long roomID;
    // 玩家位置
    private int pos;
    // 房间玩家信息
    private RoomPosInfo posInfo;
    // 自定义数据
    private int custom;
    public List<RoomPosInfo> posList;


    public static SGD_PosUpdate make(long roomID, int pos, RoomPosInfo posInfo, int custom,List<RoomPosInfo> posList) {
        SGD_PosUpdate ret = new SGD_PosUpdate();
        ret.setRoomID(roomID);
        ret.setPos(pos);
        ret.setPosInfo(posInfo);
        ret.setCustom(custom);
        ret.setPosList(posList);
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

    public RoomPosInfo getPosInfo() {
        return posInfo;
    }

    public void setPosInfo(RoomPosInfo posInfo) {
        this.posInfo = posInfo;
    }

    public int getCustom() {
        return custom;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }


    public List<RoomPosInfo> getPosList() {
        return posList;
    }

    public void setPosList(List<RoomPosInfo> posList) {
        this.posList = posList;
    }
}
