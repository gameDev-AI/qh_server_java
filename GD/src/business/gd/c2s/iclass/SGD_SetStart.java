package business.gd.c2s.iclass;
import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoom_Set;

/**
 * 一局游戏开始
 * @author zaf
 * @param <T>
 * */
public class SGD_SetStart<T> extends BaseSendMsg {

    public long roomID;
    public T setInfo;

    public static <T>SGD_SetStart make(long roomID, T setInfo) {
        SGD_SetStart ret = new SGD_SetStart();
        ret.roomID = roomID;
        ret.setInfo = setInfo;
        return ret;
    }
}
