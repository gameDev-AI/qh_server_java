package business.global.pk.gd;

public class GDGameResult {
	public GDRoom room; //房间
	
	public  GDGameResult(GDRoom room){
		this.room = room;
	}
	
	/**
	 * 结算
	 * */
	public  void resultCalc(){
		GDRoomSet roomSet = (GDRoomSet) this.room.getCurSet();
		GDSetPosMgr setPosMgr = (GDSetPosMgr) roomSet.getSetPosMgr();
		int basePoint = this.room.getBaseMark();
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			GDSetPos setPos = (GDSetPos) setPosMgr.getSetPos(i);
			if (roomSet.isRedWin()) {
				if (roomSet.m_redPosList.contains(i)) {
					setPos.setPoint( roomSet.getScore());
					setPos.getRoomPos().addWin(1);
				} else {
					setPos.setPoint(-1*roomSet.getScore());
					setPos.getRoomPos().addLose(1);
				}
			} else {
				if (roomSet.m_redPosList.contains(i)) {
					setPos.setPoint(-1*roomSet.getScore());
					setPos.getRoomPos().addLose(1);
				} else {
					setPos.setPoint(roomSet.getScore());
					setPos.getRoomPos().addWin(1);
				}
			}
		}
	}
	
	
}
