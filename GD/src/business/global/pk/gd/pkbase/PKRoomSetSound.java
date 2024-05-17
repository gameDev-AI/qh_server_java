package business.global.pk.gd.pkbase;

import java.util.ArrayList;

import business.global.room.base.AbsBaseRoom;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;

/**
 * 仙游炸棒一局游戏逻辑
 * @author zaf
 *
 */

public abstract class PKRoomSetSound {

	public AbsBaseRoom room = null;
	public PKRoomSet set = null;
	protected int m_OpPos = -1 ; //当前操作位置
	protected int m_lastOpPos = -1;//最后一次操作位置
	protected int m_lastOpPosBack = -1;//最后一次操作位置
	protected ArrayList<Integer> m_cardList = new ArrayList<Integer>();
	protected ArrayList<Integer> m_substituteCardList = new ArrayList<>();//赖子代替牌数组
	protected boolean m_bTurnEnd = false;
	protected boolean m_bSetEnd = false;
	
	protected static final int INTERVAL = 30000;//时间间隔

	protected int m_opCardType = 0;  // 操作类型及牌的类型
	protected int m_opCardTypeBackUp = 0;  //YGLFL_CARD_TYPE 操作类型及牌的类型
		
	public PKRoomSetSound( PKRoomSet set) {
		this.set = set;
		this.room = set.room;
		m_lastOpPosBack = /*this.m_lastOpPos =*/  this.m_OpPos = set.getOpPos();
		CommLogD.info("set.getOpPos()="+set.getOpPos());
	}
	
		
	/**
	 *  尝试开始回合, 如果失败，则set结束
	 * @return
	 */
	public boolean tryStartRound() {
		return true;
	}
	
	public boolean update(int sec){
		if(m_bTurnEnd) return true;
		if(m_bSetEnd) return true;
//		if(CommTime.nowMS() - this.set.startMS > INTERVAL){
//			this.roomTrusteeship(m_OpPos);
//		}
		return false;
	}
	
	
	public abstract void roomTrusteeship(int pos);
	
	
	/**
	 * 还有多少玩家
	 * **/
	public int getPlayerPlaying(){
		int count = 0;
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			PKSetPos setPos =  this.set.getSetPosMgr().getSetPos(i);
			if(setPos.getPrivateCards().size() > 0){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 验证是否是自己的牌
	 * */
	public boolean checkIsMyCard(int pos,ArrayList<Integer> cardList){
		PKSetPos setPos = (PKSetPos) this.set.getSetPosMgr().getSetPos(pos);
		return setPos.checkIsMyCard(cardList);
	}

	/**
	 * @return m_OpPos
	 */
	public int getOpPos() {
		return m_OpPos;
	}

	/**
	 * @return m_lastOpPos
	 */
	public int getLastOpPos() {
		return m_lastOpPosBack;
	}

	/**
	 * @return m_opCardType
	 */
	public int getOpCardType() {
		return m_opCardType;
	}

	/**
	 * @return m_cardList
	 */
	public ArrayList<Integer> getCardList() {
		return m_cardList;
	}

	/**
	 * @return m_bSetEnd
	 */
	public boolean isSetEnd() {
		return m_bSetEnd;
	}


	public ArrayList<Integer> getSubstituteCardList() {
		return m_substituteCardList;
	}
	
	public ArrayList<Integer> getRazzCardList() {
		return set.getRazzCardList();
	}
	
	/**
	 * 获取赖子值
	 * */
	public int getRazzCardValue() {
		return set.getRazzCardValue();
	}
	
	/**
	 * 获取opCard
	 * */
	public PK_OpCard getLastSpecificOpCard() {
		return set.getAccessToSpecific(new PK_OpCard(m_cardList, m_substituteCardList)) ;
	}
	
	public PK_OpCard getLastOpCard() {
		return new PK_OpCard(m_cardList, m_substituteCardList) ;
	}
	
	/**
	 * 判断最好出牌是否为空
	 * */
	public boolean checkLastOpCardIsNull() {
		if (null == m_cardList || null == m_substituteCardList) {
			return true;
		}
		if (null != m_cardList && m_cardList.size() <= 0) {
			return true;
		}
		return false;
	}


	/**
	 * @return set
	 */
	public PKRoomSet getSet() {
		return set;
	}

}
