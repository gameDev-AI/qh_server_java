package business.global.pk.gd.pkbase;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPosMgr;

public class PKRoomPosMgr extends AbsRoomPosMgr {

	public PKRoomPosMgr(AbsBaseRoom room) {
		super(room);
		initPosList();
	}

	@Override
	protected void initPosList() {
		for (int i = 0; i < this.getPlayerNum(); i++) {
			posList.add(new PKRoomPos(i, room));
		}
	}
}
