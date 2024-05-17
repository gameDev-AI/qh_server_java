package business.gd.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_PosLeave;

public class SGD_PosLeave extends SBase_PosLeave {

    public static SGD_PosLeave make(SBase_PosLeave posLeave) {
        SGD_PosLeave ret = new SGD_PosLeave();
        ret.setRoomID(posLeave.getRoomID());
        ret.setPos(posLeave.getPos());
        ret.setBeKick(posLeave.isBeKick());
        ret.setOwnerID(posLeave.getOwnerID());
        ret.setKickOutTYpe(posLeave.getKickOutTYpe());
        ret.setMsg(posLeave.getMsg());
        return ret;
    }
}