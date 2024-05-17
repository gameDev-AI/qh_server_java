package business.global.pk.gd.pkbase;


import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;

public class PKRoomPos extends AbsRoomPos {

	private  	int  					m_nWin  = 0 ; // 赢场数
	private  	int    					m_nLose = 0; // 输场数
	private  	int 					m_nFlat = 0; // 平场数
	public 		int 					m_gold = 0; //身上携带的金币
	
	public PKRoomPos(int posID, AbsBaseRoom room){
		super(posID, room);
	}

	

	/**
	 * @return m_nWin
	 */
	public int getWin() {
		return m_nWin;
	}

	/**
	 * @param m_nWin 要设置的 m_nWin
	 */
	public void addWin(int nWin) {
		this.m_nWin += nWin;
	}

	/**
	 * @return m_nLose
	 */
	public int getLose() {
		return m_nLose;
	}

	/**
	 * @param m_nLose 要设置的 m_nLose
	 */
	public void addLose(int nLose) {
		this.m_nLose += nLose;
	}

	/**
	 * @return m_nFlat
	 */
	public int getFlat() {
		return m_nFlat;
	}

	/**
	 * @param m_nFlat 要设置的 m_nFlat
	 */
	public void addFlat(int nFlat) {
		this.m_nFlat += nFlat;
	}
}
