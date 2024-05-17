package business.global.pk.gd.pkbase;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.RoomPlayBackImplAbstract;
import jsproto.c2s.cclass.BaseSendMsg;

public class PKRoomPlayBackImpl extends RoomPlayBackImplAbstract {
	private static final String opCard = "OpCard";	//监听的消息
	private final static  String SetStart	= "SetStart";
	private final static  String SetEnd	= "SetEnd";
	private final static  String FenPeiGongPai	= "FenPeiGongPai";

	public PKRoomPlayBackImpl(AbsBaseRoom room) {
		super(room);
	}

	@Override
	public boolean isOpCard(BaseSendMsg msg) {
		if(msg.getOpName().indexOf(opCard) > 0 || msg.getOpName().indexOf(SetStart) > 0|| msg.getOpName().indexOf(SetEnd) > 0 || msg.getOpName().indexOf(FenPeiGongPai) > 0) {
			return true;
		}
		return false;
	}
}
