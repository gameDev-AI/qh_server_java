package business.global.pk.gd;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPosMgr;

public class GDRoomPosMgr extends AbsRoomPosMgr {

	public GDRoomPosMgr(AbsBaseRoom room) {
		super(room);
	}

	@Override
	protected void initPosList() {
		for (int i = 0; i < getPlayerNum(); i++) {
			posList.add(new GDRoomPos(i, room));
		}
	}
}
