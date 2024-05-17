package business.global.pk.gd;

import business.global.pk.gd.pkbase.PKRoomPos;
import business.global.pk.gd.pkbase.PKSetPosMgr;
import business.global.room.base.AbsBaseRoom;
import com.ddm.server.common.utils.CommTime;

import core.db.entity.clarkGame.ClubMemberBO;

public class GDRoomPos extends PKRoomPos {

	private  	int  	m_firstFinishOrderCount  = 0 ; // 赢场数
	public 		long 	m_sitMS;//坐下时间
	public    final static int INTERVAL = 60000;
	
	public GDRoomPos(int posID, AbsBaseRoom room){
		super(posID, room);
	}

	/**
	 * @return m_firstOrderCount
	 */
	public int getFirstFinishOrderCount() {
		return m_firstFinishOrderCount;
	}

	/**
	 */
	public void addFirstFinishOrderCount(int firstFinishOrderCount) {
		this.m_firstFinishOrderCount += firstFinishOrderCount;
	}

	public boolean checkSitDownMS() {
		return !isReady &&  CommTime.nowMS() > this.m_sitMS + INTERVAL;
	}
	
	/**
	 * 重新选择位置
	 */
	public void resetSelectPos() {
		this.clear();
		this.getRoom().getRoomTyepImpl().roomPlayerChange(this);
	}

	public boolean seatPos(long pid, int initPoint, boolean isReady, ClubMemberBO clubMemberBO) {
		return this.doSeat(pid, initPoint, false, isReady, clubMemberBO);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Double getSportPoint(){
		GDRoomSet set = (GDRoomSet) this.getRoom().getCurSet();
		PKSetPosMgr setPosMgr = set.getSetPosMgr();
		this.calcRoomPoint(setPosMgr.getPointList().get(this.getPosID()));
		int setPoint = setPosMgr.getPointList().get(this.getPosID());
		return setSportsPoint(setPoint);
	}
}
