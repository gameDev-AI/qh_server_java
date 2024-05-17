package business.gd.c2s.iclass;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.PKRoom_Record;


public class SGD_RoomEnd extends BaseSendMsg {

    public PKRoom_Record record;
    //public List<NNRoom_SetEnd> records;

    public static SGD_RoomEnd make(PKRoom_Record record/*, List<NNRoom_SetEnd> records*/) {
        SGD_RoomEnd ret = new SGD_RoomEnd();
        ret.record = record;
        //ret.records = records;
        return ret;


    }
}
