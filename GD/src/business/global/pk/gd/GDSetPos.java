package business.global.pk.gd;

import java.util.ArrayList;

import business.global.pk.gd.pkbase.PKRoomPos;
import business.global.pk.gd.pkbase.PKSetPos;
import business.gd.c2s.cclass.GDRoomSet_Pos;
import business.gd.c2s.cclass.GDRoom_PosEnd;
import business.gd.c2s.cclass.GD_define.GD_GONGFLAG;
import business.gd.c2s.cclass.GD_define.GD_GameStatus;

public class GDSetPos extends PKSetPos {
	private		ArrayList<ArrayList<Integer>> m_LiPaiList = new ArrayList<>(); //理牌
	
	protected GD_GONGFLAG m_Gongflag = GD_GONGFLAG.GD_GONGFLAG_NORMAL;//贡牌状态
	protected int m_gongCard = -1;//贡牌值
	protected int m_gongCardBack = -1;//贡牌值备份
	protected int m_huangongCard = -1;//获取贡牌的值
	protected int m_huangongCardPos = -1;//获取贡牌的位置
	protected int checkPos = -1;//修改位置

	public GDSetPos(GDRoomSet		roomSet, PKRoomPos roomPos){
		super(roomSet, roomPos);
		checkPos = roomPos.getPosID();
	}

	public int getCheckPos() {
		return checkPos;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GDRoom_PosEnd calcPosEnd(){
		Double sportPoint = ((GDRoomPos) roomPos).getSportPoint();
		GDRoom_PosEnd posEnd =  new GDRoom_PosEnd();
		posEnd.point = m_Point;
		posEnd.pos = roomPos.getPosID();
		posEnd.pid = roomPos.getPid();
		posEnd.isRed = getRoomSet().m_redPosList.contains(roomPos.getPosID());
		posEnd.totalPoint = roomPos.getPoint();
		posEnd.finishOrder = getRoomSet().getFinishOrderByPos(roomPos.getPosID());
		posEnd.blueSteps = ((GDRoom)(roomPos).getRoom()).getBlueSteps();
		posEnd.redSteps = ((GDRoom)(roomPos).getRoom()).getRedSteps();
		posEnd.sportsPoint = sportPoint;
		return posEnd;
	}





	public ArrayList<ArrayList<Integer>> getLiPaiList() {
		return m_LiPaiList;
	}

	public void setLiPaiList(ArrayList<ArrayList<Integer>> m_LiPaiList) {
		this.m_LiPaiList = m_LiPaiList;
	}
	
	/**
	 * 检查理牌的牌是否正确。
	 * 
	 * @param liPais
	 * @return
	 */
	public boolean checkLiPai(ArrayList<ArrayList<Integer>> liPais) {
		// 如果理牌数 <= 0
		if (liPais.size() <= 0) {
			this.m_LiPaiList.clear();
			return true;
		}
		ArrayList<Integer> cardList = new ArrayList<>();
		for (ArrayList<Integer> cards : liPais) {
			cardList.addAll(cards);
		}
		// 牌数组 > 手上牌，则是错误。
		if (cardList.size() > this.getPrivateCards().size())
			return false;
		// 检查是否自己的牌
		if (checkIsMyCard(cardList)) {
			this.m_LiPaiList = liPais;
			return true;
		}
		return false;
	}
	
	/**
	 * 删除牌组信息
	 * @return
	 */
	@Override
	public boolean deleteCard(ArrayList<Integer> cradList) {
		boolean flag =  super.deleteCard(cradList);
		if (!flag) {
			return flag;
		}
		for (Integer card : cradList) {
			for (ArrayList<Integer> arrayList : m_LiPaiList) {
				if (arrayList.contains(card)) {
					arrayList.remove(card);
					break;
				}
			}
		}
		for (int i = 0; i < m_LiPaiList.size(); ) {
			if(i >= m_LiPaiList.size())continue;
			if (m_LiPaiList.get(i).size() <= 0) {
				m_LiPaiList.remove(i);
			}else
				i++;
		}
		return true;
	}
	
	/**
	 * 获取手上的牌的分数
	 * */
//	public int  getHandlerScore() {
//		int score = getRoomSet().getScoreByCardList(this.getPrivateCards());
//		return score;
//	}
	
	/**
	 * 获取某一个位置上的信息
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GDRoomSet_Pos getSetPosInfo(long pid) {
		return getSetPosInfo(pid == roomPos.getPid());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GDRoomSet_Pos getSetPosInfo(boolean isSelf) {
		GDRoomSet_Pos roomSet_Pos = new GDRoomSet_Pos();
		roomSet_Pos.posID = roomPos.getPosID();
		roomSet_Pos.pid = roomPos.getPid();
		roomSet_Pos.cardList = this.getNotifyCard(isSelf);
		roomSet_Pos.totalPoint = roomPos.getPoint();
		roomSet_Pos.isRed = getRoomSet().m_redPosList.contains(roomPos.getPosID());
		roomSet_Pos.finishOrder = getRoomSet().getFinishOrderByPos(roomPos.getPosID());
		
		if (getRoomSet().getStatus().equals( GD_GameStatus.GD_GAME_STATUS_COMPAER) ) {
			roomSet_Pos.lastOpType = this.m_lastOpCardType;
			roomSet_Pos.lastOutCardList = this.m_lastOpCardList;
			roomSet_Pos.lastSubstituteCard = this.m_lastSubstituteCardList;
		}
		
		if (!getRoomSet().getStatus().equals( GD_GameStatus.GD_GAME_STATUS_RESULT)) {
			roomSet_Pos.liPaiList = this.getLiPaiList();
			roomSet_Pos.gongFlag = this.getGongflag().value();
			roomSet_Pos.gongCard = this.getGongCard();
			roomSet_Pos.huanCard = this.getHuanGongCard();
			roomSet_Pos.huanCardPos = this.getHuangongCardPos();
		}
		if (getRoomSet().getStatus().equals( GD_GameStatus.GD_GAME_STATUS_RESULT)) {
			roomSet_Pos.point = m_Point;
			int setPoint = getRoomSet().getSetPosMgr().getPointList().get(getPosID());
			roomSet_Pos.sportsPoint = roomPos.setSportsPoint(setPoint);
		}
		
		return roomSet_Pos;
	}

	@Override
	public GDRoomSet getRoomSet() {
		return (GDRoomSet) roomSet;
	}

	/**
	 * @return m_Gongflag
	 */
	public GD_GONGFLAG getGongflag() {
		return m_Gongflag;
	}

	/**
	 */
	public void setGongflag(GD_GONGFLAG Gongflag) {
		this.m_Gongflag = Gongflag;
	}

	/**
	 * @return m_gongCard
	 */
	public int getGongCard() {
		return m_gongCard;
	}

	/**
	 */
	public void setGongCard(int gongCard) {
		this.m_gongCard = gongCard;
	}

	/**
	 * @return m_gaingongCard
	 */
	public int getHuanGongCard() {
		return m_huangongCard;
	}

	/**
	 */
	public void setHuangongCardAndPos(int gaingongCardPos,int gaingongCard) {
		this.m_huangongCardPos = gaingongCardPos;
		this.m_huangongCard = gaingongCard;
	}

	/**
	 * @return m_gaingongCardPos
	 */
	public int getHuangongCardPos() {
		return m_huangongCardPos;
	}

	/**
	 * @param m_huangongCardPos 要设置的 m_huangongCardPos
	 */
	public void setHuangongCardPos(int m_huangongCardPos) {
		this.m_huangongCardPos = m_huangongCardPos;
	}

	/**
	 * @return m_gongCardBack
	 */
	public int getGongCardBack() {
		return m_gongCardBack;
	}

	/**
	 */
	public void setGongCardBack(int gongCardBack) {
		this.m_gongCardBack = gongCardBack;
	}



	
}
