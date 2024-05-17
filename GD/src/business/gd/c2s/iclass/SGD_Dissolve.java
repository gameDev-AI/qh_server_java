package business.gd.c2s.iclass;

import jsproto.c2s.iclass.room.SBase_Dissolve;

public class SGD_Dissolve extends SBase_Dissolve {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static SGD_Dissolve make(SBase_Dissolve dissolve) {
        SGD_Dissolve ret = new SGD_Dissolve();
        ret.setOwnnerForce(dissolve.isOwnnerForce());
        ret.setRoomID(dissolve.getRoomID());
        ret.setDissolveNoticeType(dissolve.getDissolveNoticeType());
        ret.setMsg(dissolve.getMsg());
        return ret;
    }
}
