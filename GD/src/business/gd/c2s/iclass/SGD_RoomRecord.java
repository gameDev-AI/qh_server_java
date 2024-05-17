package business.gd.c2s.iclass;
import java.util.List;

import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoom_SetEnd;


public class SGD_RoomRecord<T> extends BaseSendMsg {

    public List<T> records;


    public static  <T>SGD_RoomRecord<T> make(List<T> records) {
        SGD_RoomRecord<T> ret = new SGD_RoomRecord<T>();
        ret.records = records;

        return ret;


    }
}
