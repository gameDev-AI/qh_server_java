package business.gd.c2s.iclass;
import cenum.RoomTypeEnum;
import jsproto.c2s.iclass.room.SBase_Config;


public class SGD_Config extends SBase_Config {

    public static SGD_Config make(CGD_CreateRoom cfg, RoomTypeEnum roomType) {
    	SGD_Config ret = new SGD_Config();
        ret.setRoomType(roomType);
        ret.setCfg(cfg);
        return ret;
    

    }
}
