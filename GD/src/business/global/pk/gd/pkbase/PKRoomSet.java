package business.global.pk.gd.pkbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import business.gd.c2s.cclass.PKRoom_SetEnd;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.RoomPlayBack;
import business.player.Player;
import business.player.PlayerMgr;
import business.player.Robot.RobotMgr;
import business.player.feature.PlayerCurrency;
import cenum.PrizeType;
import cenum.RoomTypeEnum;
import com.ddm.server.common.utils.CommTime;
import core.db.entity.clarkGame.GameSetBO;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import jsproto.c2s.cclass.playback.PlayBackData;

/**
 * 牛牛一局游戏逻辑
 * 
 * @author zaf
 *
 */

public abstract class PKRoomSet extends PokerLogic {
	public AbsBaseRoom room = null;
	public long startMS = 0;
	public PKSetCard setCard = null;
	public GameSetBO bo = null;
	public ArrayList<Double> sportsPointList = null;
	public ArrayList<Integer> pointList; //得分
	public HashMap<Integer,List<Integer>> hMap = new HashMap<Integer, List<Integer>>();
	protected static final int WAITTRUSTEESHIPTIME = 3000;//托管延迟2s
	protected int  m_OpPos = 0 ; 				//当前操作位置
	protected PKSetPosMgr  m_setPosMgr = null;
	public PKRoomSetSound curRound = null;
	public List<PKRoomSetSound> historyRound = new ArrayList<>();
	protected ArrayList<Integer> m_RazzCardList= new ArrayList<Integer>();//赖子
	protected boolean 	m_bFirstOp = true;		//是否是一轮的首出
	protected RoomPlayBack roomPlayBack;	//回放
	
	public PKRoom_SetEnd setEnd = new PKRoom_SetEnd();
	
	public PKRoomSet( AbsBaseRoom room) {
		super(room.getCurSetID());
		this.room = room;
		this.pointList = new ArrayList<Integer>(Collections.nCopies(this.room.getPlayerNum(), 0));
		this.initSportsPointList();
	}

	private void initSportsPointList () {
		if(RoomTypeEnum.UNION.equals(this.room.getRoomTypeEnum())) {
			this.sportsPointList = new ArrayList<>(Collections.nCopies(this.room.getPlayerNum(), 0D));
		}
	}
	
	/**
	 * 记录发起解散的玩家
	 */
	public void addDissolveRoom(BaseSendMsg baseSendMsg) {
	}

	
	/**
	 * 每200ms更新1次   秒
	 * @param sec 
	 * @return T 是 F 否
	 */
	public abstract boolean update(int sec);
	
	/**
	 * 设置默认庄家位置
	 * **/
	public abstract void  setDefeault();
	
	/**
	 * 结算
	 * */
	public abstract void resultCalc();
	
	/**
	 * 开始设置
	 */
	public abstract void startSet();
	
	/**
	 * @return m_OpPos
	 */
	public int getOpPos() {
		return m_OpPos;
	}

	/**
	 * @param m_OpPos 要设置的 m_OpPos
	 */
	public void setOpPos(int m_OpPos) {
		this.m_OpPos = m_OpPos;
		AbsRoomPos tempRoomPos = this.room.getRoomPosMgr().getPosByPosID(m_OpPos);
		tempRoomPos.setLatelyOutCardTime(CommTime.nowMS());
	}

	public PKSetPosMgr getSetPosMgr() {
		return m_setPosMgr;
	}
	
	/**
	 * @return curRound
	 */
	public PKRoomSetSound getCurRound() {
		return curRound;
	}
	
	/***
	 * 托管
	 * @param pos
	 */
	public void roomTrusteeship(int pos) {
		if (roomTrusteeshipCondition()) {
			return;
		}
		if(null != this.curRound)  this.curRound.roomTrusteeship(pos);
	}
	
	/**
	 * 满足什么条件不继续round的roomTrusteeship
	 * */
	public boolean roomTrusteeshipCondition() {
		return false;
	}
	
	/**
	 * 获取赖子
	 * */
	@Override
	public ArrayList<Integer> getRazzCardList() {
		return m_RazzCardList;
	}
	
	/**
	 * @param m_bFirstOp 要设置的 m_bFirstOp
	 */
	public void setFirstOp(boolean m_bFirstOp) {
		this.m_bFirstOp = m_bFirstOp;
	}

	/**
	 * @return m_bFirstOp
	 */
	public boolean isFirstOp() {
		return m_bFirstOp;
	}
	
	/**
	 * 获取房间回放记录
	 * @return
	 */
	public RoomPlayBack getRoomPlayBack() {
		if (null == this.roomPlayBack) {
			this.roomPlayBack = new PKRoomPlayBackImpl(this.room);
		}
		return this.roomPlayBack;
	}
	
	/**
	 * 获取通知设置结束
	 * @return
	 */
	public PKRoom_SetEnd getNotify_setEnd() {
		return setEnd;
	}
	
	/**
	 * 结束当前小局
	 * **/
	public abstract void endSet();
	
	/**
	 * 获取赖子值
	 * */
	public int getRazzCardValue() {
		if(this.getRazzCardList().size() > 0) {
			return BasePockerLogic.getCardValueEx(this.getRazzCardList().get(0));
		} else {
			return -1;
		}
	}
	
	
	/**
	 * 插入牌 并在牌堆里面删除
	 * */
	public ArrayList<Integer> getGodCard(ArrayList<Integer> list) {
		PKRoom pkRoom = (PKRoom) this.room;
		if(!pkRoom.isGodCard()) return new ArrayList<Integer>();
		int cardNum = pkRoom.getConfigMgr().getHandleCard();
		ArrayList<Integer> cardList = new ArrayList<Integer>(cardNum);
		cardList.addAll(list);
		int count = cardNum - cardList.size();
		ArrayList<Integer> tempList = this.setCard.popList(count);
		BasePockerLogic.deleteCard(this.setCard.getLeftCards(), tempList );
		cardList.addAll(tempList);
		return cardList;
	}
	
	
	
	
	/**
	 * 设置神牌
	 */
//	0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,  0x0E,	0x0F, //方块3~2
//	0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,  0x1E,	0x1F, //梅花3~2
//	0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,  0x2E,	0x2F, //红桃3~2
//	0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,  0x3E,	0x3F, //黑桃3~2
	public void godCard () {
		PKRoom pkRoom = (PKRoom) this.room;
		if(!pkRoom.isGodCard()) return;
		boolean flag1 =  BasePockerLogic.deleteCard(this.setCard.getLeftCards(), (ArrayList<Integer>) pkRoom.getConfigMgr().getPrivate_Card1());
		boolean flag2 =  BasePockerLogic.deleteCard(this.setCard.getLeftCards(), (ArrayList<Integer>) pkRoom.getConfigMgr().getPrivate_Card2() );
		boolean flag3 =  BasePockerLogic.deleteCard(this.setCard.getLeftCards(), (ArrayList<Integer>) pkRoom.getConfigMgr().getPrivate_Card3() );
		boolean flag4 =  BasePockerLogic.deleteCard(this.setCard.getLeftCards(), (ArrayList<Integer>) pkRoom.getConfigMgr().getPrivate_Card4() );
		if (flag1 && flag2 && flag3 && flag4) {
			ArrayList<Integer> card1 = getGodCard(pkRoom.getConfigMgr().getPrivate_Card1());
			
			ArrayList<Integer> card2  = getGodCard(pkRoom.getConfigMgr().getPrivate_Card2());
			
			ArrayList<Integer> card3  = getGodCard(pkRoom.getConfigMgr().getPrivate_Card3());
			
			ArrayList<Integer> card4  = getGodCard(pkRoom.getConfigMgr().getPrivate_Card4());

			hMap.put(0, card1);
			hMap.put(1, card2);
			hMap.put(2, card3);
			hMap.put(3, card4);
		} else {
			this.setCard.randomCard();
			int cardNum = pkRoom.getConfigMgr().getHandleCard();
			hMap.put(0, this.setCard.popList(cardNum));
			hMap.put(1, this.setCard.popList(cardNum));
			hMap.put(2, this.setCard.popList(cardNum));
			hMap.put(3, this.setCard.popList(cardNum));
		}
	}

	/**
	 * 如果是房卡类型，才需要回放记录
	 */
	public void roomPlayBack() {
		if (this.checkExistPrizeType(PrizeType.RoomCard)) {
			PlayBackData playBackData = new PlayBackData(this.room.getRoomID(),
					this.room.getCurSetID(), 0, this.room.getCount(),
					this.room.getRoomKey(),
					this.room.getBaseRoomConfigure().getGameType().getId(),getPlayBackDateTimeInfo());
			this.bo.savePlayBackCode(getPlayBackDateTimeInfo().getPlayBackCode());
			this.getRoomPlayBack().addPlayBack(playBackData);
		}
	}
	
	public void clean () {
		if (null != roomPlayBack ) {
			this.roomPlayBack .clear();
			this.roomPlayBack = null;
		}
		if(null != this.historyRound) {
			this.historyRound.clear();
			this.historyRound = null;
		}
		if (null != setCard) {
			this.setCard.clean();
			this.setCard = null;
		}
		this.m_setPosMgr = null;
		this.bo = null;
	}

	/**
	 * 检查是否存在指定消耗类型
	 * @return
	 */
	@Override
	public boolean checkExistPrizeType(PrizeType prizeType) {
		return prizeType.equals(this.room.getBaseRoomConfigure().getPrizeType());
	}
}
