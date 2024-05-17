package business.global.pk.gd;

import java.util.*;
import java.util.stream.Collectors;

import business.gd.c2s.cclass.PKRoomSet_Pos;
import business.gd.c2s.iclass.*;
import business.global.pk.gd.pkbase.PKRoomPos;
import business.global.pk.gd.pkbase.PKRoomSet;
import business.global.pk.gd.pkbase.PKSetPos;
import business.global.room.base.AbsRoomPos;
import cenum.room.RoomDissolutionState;
import cenum.room.TrusteeshipState;
import core.db.entity.clarkGame.GameSetBO;
import core.db.service.clarkGame.GameSetBOService;
import core.ioc.ContainerMgr;
import jsproto.c2s.cclass.BaseSendMsg;
import business.gd.c2s.cclass.GDRoomSet_Pos;
import business.gd.c2s.cclass.GDRoom_PosEnd;
import business.gd.c2s.cclass.GD_define.GD_GONGFLAG;
import business.gd.c2s.cclass.GD_define.GD_GameStatus;
import business.gd.c2s.cclass.GD_define.GD_Sign;
import business.gd.c2s.cclass.GD_define.GD_WINFLAG;

import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import jsproto.c2s.cclass.pk.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * 仙游炸棒一局游戏逻辑
 * @author zaf
 */
public  class GDRoomSet extends PKRoomSet {

	protected GD_GameStatus status = GD_GameStatus.GD_GAME_STATUS_SENDCARD;

	protected ArrayList<Integer> m_opPosList ;		//出牌顺序
	protected ArrayList<Integer> m_redPosList= new ArrayList<Integer>();		//记录红方 翻牌位置  给客户端

	protected boolean		  m_isRedWin = false;//是否是红方赢

	protected ArrayList<Integer> m_finishOrderList = new ArrayList<Integer>(); 		//出完牌的顺序

	protected GD_WINFLAG		 m_winFlag = GD_WINFLAG.GD_WINFLAG_SIGN;//赢的方式 0：默认值  1单赢 2双赢

	protected int  m_steps = 0x02; //级数
	protected boolean m_isRedStep;//是否是红方级数

	protected ArrayList<ThreeParamentVictory> m_openCardList = new ArrayList<>();

	protected ArrayList<ThreeParamentVictoryEx> m_gongPaiList = new ArrayList<>();

	protected boolean m_isKangGong = false;

	protected int score = 1;

	public Map<Integer, List<PKRoomSet_Pos>> sourceCardPosList = new HashMap<>();

	public Map<Integer, Integer> sourceCardMap = new HashMap<>();

	public int bombScore = 1;

	@SuppressWarnings("rawtypes")
	public GDRoomSet( GDRoom room) {
		super(room);

		m_setPosMgr = new GDSetPosMgr(this);

		this.m_opPosList 			= new ArrayList<Integer>(Collections.nCopies(this.room.getPlayerNum(), -1));

		this.m_steps  = room.getSteps();
		this.m_isRedStep = room.LastSetIsRedWin();
		this.addGameConfig();
		this.startSet();
	}

	/**
	 * 回放记录添加游戏配置
	 */
	@Override
	public void addGameConfig() {
		this.getRoomPlayBack().addPlaybackList(SGD_Config.make(room.getCfg(),this.room.getRoomTyepImpl().getRoomTypeEnum()), null);
	}

	@Override
	public int getTabId() {
		return this.room.getTabId();
	}

	/**
	 * 记录发起解散的玩家
	 */
	public void addDissolveRoom(BaseSendMsg baseSendMsg) {
		if (this.status == GD_GameStatus.GD_GAME_STATUS_RESULT) {
			return;
		}
		this.getRoomPlayBack().addPlaybackList(baseSendMsg,m_setPosMgr.getAllPlayBackNotify());
	}

	/**
	 * 每200ms更新1次   秒 
	 * @return T 是 F 否
	 * YGLFL_GAME_STATUS_SENDCARD(0), //发牌
	YGLFL_GAME_STATUS_COMPAER_ONE(1), //比牌
	YGLFL_GAME_STATUS_COMPAER(2), //比牌
	YGLFL_GAME_STATUS_RESULT(3), //结算
	 */
	@Override
	public boolean update(int sec) {
		boolean isClose = false;
		switch(this.getStatus()){
			case GD_GAME_STATUS_SENDCARD:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					if(!checkGongPai()){
						if (m_isKangGong) {
							this.setStatus(GD_GameStatus.GD_GAME_STATUS_KANGGONG);
						} else {
							this.setStatus(GD_GameStatus.GD_GAME_STATUS_COMPAER);
						}
					}else{
						this.setStatus(GD_GameStatus.GD_GAME_STATUS_GONGPAI);
					}
					getRoomPlayBack().playBack2All(SGD_ChangeStatus.make(this.room.getRoomID(), this.getStatus().value()));
				}
				break;
			case GD_GAME_STATUS_GONGPAI:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					setGongPai();
				}
				break;
			case GD_GAME_STATUS_HUANPAI:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					setHuAnGongPai();
				}
				break;
			case GD_GAME_STATUS_COMPAER:
				if(this.curRound == null){
					if(!this.startNewRound()){
						this.endSet();
					}
				}else if(this.curRound != null){
					boolean isRoundClosed = this.curRound.update(sec);
					if (isRoundClosed) {
						if(this.curRound.isSetEnd()){
							this.endSet();
						}else if (!this.startNewRound()) {
							this.endSet();
						}
					}
				}
				break;
			case GD_GAME_STATUS_RESULT:
				isClose = true;
				break;
			case GD_GAME_STATUS_KANGGONG:
				if( CommTime.nowMS() - this.startMS >=  this.getWaitTimeByStatus()){
					this.setStatus(GD_GameStatus.GD_GAME_STATUS_COMPAER);
					getRoomPlayBack().playBack2All(SGD_ChangeStatus.make(this.room.getRoomID(), this.getStatus().value()));
				}
				break;
			default:
				break;
		}

		return isClose;
	}

	@Override
	public void clear() {

	}

	@Override
	public void clearBo() {

	}

	/**
	 * 判断是不是需要贡牌<br>
	 * 1：流局不进贡
	 * 2：掼蛋团团转不进贡
	 * 3：首局不需要进贡
	 * @return
	 */
	private boolean checkGongPai() {
		GDRoom gdRoom = (GDRoom) this.room;
		//团团转和第一局不进贡
		if(GD_Sign.GD_SIGN_TTZ.equals(gdRoom.getGDType()) || gdRoom.getCurSetID() == 1) {
			return false;
		}
		GDRoomSet roomSet = gdRoom.getLastHistorySet();
		if (null == roomSet) {
			CommLogD.error("checkGongPai roomSet is null");
			return false;
		}
		if(roomSet.getFinishOrder().size() <= 0){
			return false;
		}
		//更新还贡和进贡的位置
		ArrayList<Integer> gongPosList = new ArrayList<>();
		ArrayList<Integer> huAnGongPosList = new ArrayList<>();
		if (roomSet.getWinFlag().equals(GD_WINFLAG.GD_WINFLAG_WINWIN)) {
			//双下
			for (int i = 0; i < gdRoom.getPlayerNum(); i++) {
				if (!roomSet.getFinishOrder().contains(i)) {
					gongPosList.add(i);
				}else{
					huAnGongPosList.add(i);
				}
			}
		} else {
			//单下
			gongPosList.add(roomSet.getFinishOrder().get(gdRoom.getPlayerNum() - 1));
			huAnGongPosList.add(roomSet.getFinishOrder().get(0));
		}
		int maxTrumpCount = 0;
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			GDSetPos setPos = (GDSetPos) this.getSetPosMgr().getSetPos(i);
			Map<Integer, List<Integer>> cardMap = null;
			if (gongPosList.contains(i)) {
				cardMap = getCardMap(setPos.getPrivateCards(), false);
				setPos.setGongflag(GD_GONGFLAG.GD_GONGFLAG_GONG_HAVE);
			}
			if (huAnGongPosList.contains(i)) {
				cardMap = getCardMap(setPos.getPrivateCards(), true);
				setPos.setGongflag(GD_GONGFLAG.GD_GONGFLAG_HUAANGONG_HAVE);
			}
			//更新下游大王张数
			if (null != cardMap) {
				Map.Entry<Integer, List<Integer>> entry =  cardMap.entrySet().iterator().next();
				setPos.setGongCardBack( entry.getValue().get(0));
				if (BasePockerLogic.getCardValueEx(BasePocker.Trump_PockerList[1]) == entry.getKey()) {
					maxTrumpCount += entry.getValue().size();
				}
			}
		}

		if (maxTrumpCount >= 2) {
			//抗贡头游玩家出牌
			((GDSetPosMgr) m_setPosMgr).clearGongFlag();
			setOpPos(roomSet.getFinishOrder().get(0));
			m_isKangGong = true;
			return false;
		}
//		else{
//			//普通贡，下游出牌
//			if (!roomSet.getWinFlag().equals(GD_WINFLAG.GD_WINFLAG_WINWIN))
//				setOpPos(roomSet.getFinishOrder().get(this.room.getPlayerNum() - 1));
//		}
		return true;
	}

	/**
	 * 设置贡牌
	 * */
	private void setGongPai(){
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			GDSetPos setPos = (GDSetPos) this.getSetPosMgr().getSetPos(i);
			//需要进贡
			if (setPos.getGongflag().equals(GD_GONGFLAG.GD_GONGFLAG_GONG_HAVE)) {
				Map<Integer, List<Integer>> map = getCardMap(setPos.getPrivateCards(), false);
				for(Map.Entry<Integer, List<Integer>> entry:map.entrySet()){
					//选出除了红心参谋的最大牌进行进贡
					List<Integer> leaveList = entry.getValue().stream().filter(n -> !getRazzCardList().contains(n)).collect(Collectors.toList());
					if(CollectionUtils.isNotEmpty(leaveList)){
						opGongPai(null, setPos, CGD_GongPai.make(this.room.getRoomID(), leaveList.get(0)));
						break;
					}
				}
			}
		}
		//获取还贡的牌并进入还贡
		gongPaiEnd();
	}

	/**
	 * 进贡结束
	 */
	private void gongPaiEnd() {
		GDRoom gdRoom = (GDRoom) this.room;
		GDRoomSet roomSet = gdRoom.getLastHistorySet();
		if (null == roomSet) {
			CommLogD.error("gongPaiEnd roomSet is null");
			return ;
		}
		//头游玩家还贡
		ThreeParamentVictoryEx victoryEx = gongCard(roomSet.getFinishOrder().get(0));
		//贡牌位置
		if (-1 == victoryEx.getPos1()) {
			CommLogD.error("gongPaiEnd pos is -1");
			return ;
		}
		setOpPos(victoryEx.getPos1());
		m_gongPaiList.add(victoryEx);
		if (roomSet.getWinFlag().equals(GD_WINFLAG.GD_WINFLAG_WINWIN)) {
			//双下获取二游还贡
			ThreeParamentVictoryEx victoryEx1 = gongCard(roomSet.getFinishOrder().get(1));
			m_gongPaiList.add(victoryEx1);
		}
		//通知进入还牌阶段
		this.setStatus(GD_GameStatus.GD_GAME_STATUS_HUANPAI);
		getRoomPlayBack().playBack2All(SGD_ChangeStatus.make(this.room.getRoomID(), this.getStatus().value()));
	}


	/**
	 * 还牌
	 */
	public void setHuAnGongPai(){
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			GDSetPos setPos = (GDSetPos) this.getSetPosMgr().getSetPos(i);
			if (setPos.getGongflag().equals(GD_GONGFLAG.GD_GONGFLAG_HUAANGONG_HAVE)) {
				List<Integer> privateCards = setPos.getPrivateCards();
				//头游和尾游是搭档，还贡的牌需要是小于A的
				if (getHuoBanPos(setPos.getPosID()) == setPos.getHuangongCardPos()) {
					if (getFinishOrderByPos(setPos.getPosID()) == 1 && this.room.getPlayerNum() == getFinishOrderByPos(setPos.getHuangongCardPos())) {
						privateCards = setPos.getPrivateCards().stream().filter(n -> !getRazzCardList().contains(n) && BasePocker.getCardValueEx(n) < 0x0E).collect(Collectors.toList());
					}
				}
				if(CollectionUtils.isNotEmpty(privateCards)){
					opHuAnGongPai(null, setPos, CGD_HuanGongPai.make(this.room.getRoomID(),privateCards.get(new Random().nextInt(privateCards.size()))));
				}
			}
		}
		//还贡结束
//		huAnGongEnd();
	}

	/**
	 * 还牌 结束
	 * */
	private void huAnGongEnd() {
		//开始发牌
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			PKSetPos setPos = this.m_setPosMgr.getSetPos(i);
			if (0 == i) {
				this.getRoomPlayBack().playBack2Pos(i, SGD_FenPeiGongPai.make(this.room.getRoomID(), m_OpPos, m_gongPaiList, setPos.getNotifyCard(true)), m_setPosMgr.getAllPlayBackNotify());
			} else {
				this.room.getRoomPosMgr().notify2Pos(i, SGD_FenPeiGongPai.make(this.room.getRoomID(), m_OpPos, m_gongPaiList, setPos.getNotifyCard(true)));
			}
		}
		this.setStatus(GD_GameStatus.GD_GAME_STATUS_COMPAER);
		getRoomPlayBack().playBack2All(SGD_ChangeStatus.make(this.room.getRoomID(), this.getStatus().value()));
	}

	/**
	 * 还贡和进贡换牌
	 * @param pos 还贡位置
	 * @return
	 */
	private ThreeParamentVictoryEx gongCard(int pos) {
		ThreeParamentVictoryEx victory = new ThreeParamentVictoryEx();
		GDSetPos gongPaiSetPos = ((GDSetPosMgr) this.m_setPosMgr).getMaxGongCardPos(pos);
		if (null == gongPaiSetPos) {
			CommLogD.error("changeCard get gongPaiSetPos is null");
			return victory;
		}
		//更新进贡玩家位置和进贡的牌
		GDSetPos huAnGongSetPos = (GDSetPos) this.getSetPosMgr().getSetPos(pos);
		huAnGongSetPos.addCard(gongPaiSetPos.getGongCard());
		huAnGongSetPos.setHuangongCardAndPos(gongPaiSetPos.getPosID(), gongPaiSetPos.getGongCard());
		//更新还贡玩家位置
		gongPaiSetPos.setHuangongCardPos(huAnGongSetPos.getPosID());
		victory.setParamentVictory(gongPaiSetPos.getGongCard(), gongPaiSetPos.getPosID(), huAnGongSetPos.getPosID());
		return victory;
	}



	/**
	 * 满足什么条件不继续round的roomTrusteeship
	 * */
	public boolean roomTrusteeshipCondition() {
		if(this.getStatus().equals(GD_GameStatus.GD_GAME_STATUS_GONGPAI))
			return true;
		else
			return false;
	}

	/**
	 * 结算
	 * */
	public void resultCalc(){
		if(null != this.curRound)
			((GDRoomSetSound) this.curRound).checkEndSet();
		GDGameResult result = new GDGameResult((GDRoom) this.room);
		result.resultCalc();

		if(m_finishOrderList.size() > 0){
			int firstFinishOrder = m_finishOrderList.get(0);
			((GDRoom) this.room).setThePostionOfTheFirstDeal(firstFinishOrder);
			GDRoomPos roomPos = (GDRoomPos) this.room.getRoomPosMgr().getPosByPosID(firstFinishOrder);
			roomPos.addFirstFinishOrderCount(1);
		}
	}

	/**
	 * 设置状态
	 * */
	public void setStatus(GD_GameStatus state){
		if(this.status == state) return;
		this.status = state;
		this.startMS = CommTime.nowMS();
	}

	/**
	 * 获取状态
	 * */
	public GD_GameStatus getStatus(){
		return this.status;
	}


	/***
	 *  开启新的回合
	 * @return
	 */
	public boolean startNewRound() {
		if (this.curRound != null) {
			this.historyRound.add(this.curRound);
		}

		this.curRound = new GDRoomSetSound(this); // 开启第一轮
		return this.curRound.tryStartRound();
	}

	/**
	 * 开始设置
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void startSet() {
		//设置参与游戏的玩家
		for(AbsRoomPos pos : this.room.getRoomPosMgr().posList){
			PKRoomPos roomPos = (PKRoomPos) pos;
			if((pos.isReady() && this.room.getCurSetID() == 1) || (this.room.getCurSetID() > 1 && pos.getPid() != 0)){
				roomPos.setPlayTheGame(true);
			}
		}
		GDRoom yglflRoom = (GDRoom) this.room;
		// 洗底牌
		this.setCard = new GDSetCard(yglflRoom);
		for (int i = 0; i < yglflRoom.geXiPaiPidList().size(); i++) {
			this.setCard.onXiPai();
		}

		yglflRoom.clearXiPaiPidList();

		//是否开启神牌模式
		if (yglflRoom.getConfigMgr().isGodCard())
			godCard();


		int cardNum = yglflRoom.getConfigMgr().getHandleCard();

		for (int j = 0; j < this.room.getPlayerNum(); j++) {
			GDSetPos setPos = (GDSetPos) this.m_setPosMgr.getSetPos((m_OpPos+j)%this.room.getPlayerNum());
			//如果是DEBUG模式发送神牌
			if (yglflRoom.getConfigMgr().isGodCard()) {
				setPos.init(hMap.get(j));
			} else {
				setPos.init(this.setCard.popList(cardNum));
			}
		}


		this.setDefeault();
		this.startMS = CommTime.nowMS();
		GDRoom gdRoom = (GDRoom) this.room;
		//开始发牌
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			long pid = this.room.getRoomPosMgr().getPosByPosID(i).getPid();
			GDRoomSetInfo notify_set = this.getNotify_set(pid);
			if(GD_Sign.GD_SIGN_TTZ.equals(gdRoom.getGDType())){
				List<PKRoomSet_Pos> set_pos = sourceCardPosList.containsKey(i)?sourceCardPosList.get(i):new ArrayList<>();
				notify_set.oldPosInfo = set_pos;
			}

			if (0 == i) {
				this.getRoomPlayBack().playBack2Pos(i, SGD_SetStart.make(this.room.getRoomID(), notify_set), m_setPosMgr.getAllPlayBackNotify());
			} else {
				this.room.getRoomPosMgr().notify2Pos(i, SGD_SetStart.make(this.room.getRoomID(), notify_set));
			}
		}
		if(GD_Sign.GD_SIGN_TTZ.equals(gdRoom.getGDType())){
			if(sourceCardMap.size()>0){
				m_openCardList.stream().forEach(z->{
					z.setPos(sourceCardMap.get(z.getPos()));
				});
			}
		}
//		this.room.getRoomPosMgr().setAllLatelyOutCardTime();
		yglflRoom.getTrusteeship().setTrusteeshipState(TrusteeshipState.Normal);
	}


	/**
	 * 局结束
	 */
	public void endSet() {
		setEnd(true);
		if (this.status == GD_GameStatus.GD_GAME_STATUS_RESULT)
			return;
		this.setStatus(GD_GameStatus.GD_GAME_STATUS_RESULT);
		//	中途解散不算分。
		if (RoomDissolutionState.Dissolution.equals(room.getRoomRealDissolutionState())) {
			return;
		}
		GDRoom gdRoom = (GDRoom) this.room;
		int num = 1;
		if (getFinishOrder().size() > 0) {
			if (getWinFlag().equals(GD_WINFLAG.GD_WINFLAG_WINWIN)) {
				num = gdRoom.isShuangXiaSiJi()?4:3;
			} else if(!getWinFlag().equals(GD_WINFLAG.GD_WINFLAG_WINWIN) &&
					this.m_redPosList.contains(this.getFinishOrder().get(0)) == this.m_redPosList.contains(this.getFinishOrder().get(2))){
				num = 2;
			}
		}
		//Todo 算出炸弹
		num = bombScore*num;
		setScore(num);
		this.calcPoint();
		if (GD_Sign.GD_SIGN_JD.equals(gdRoom.getGDType()) && getFinishOrder().size() > 0) {
			gdRoom.setStepsByWinFlag(m_isRedWin, num);
		}

		// 小局托管自动解散
		this.setTrusteeshipAutoDissolution();
		// 广播
		boolean isRoomEnd = gdRoom.getCurSetID() >= gdRoom.getEndCount() || gdRoom.isEnd();
		getRoomPlayBack().playBack2All(SGD_SetEnd.make(this.room.getRoomID(), this.room.getCurSetID(), this.status.value(), this.startMS,m_setPosMgr.getPointList(), m_isRedWin, ((GDSetPosMgr) getSetPosMgr()).getPosInfo(true),isRoomEnd, gdRoom.getRedSteps(), gdRoom.getBlueSteps(),getPlayBackDateTimeInfo().getPlayBackCode(),this.sportsPointList));
		roomPlayBack();
	}

	/**
	 * 结算积分
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void calcPoint(){
		GameSetBOService gameSetBoService = ContainerMgr.get().getComponent(GameSetBOService.class);
		GameSetBO gameSetBO = gameSetBoService.findOne(room.getRoomID(), this.room.getCurSetID());
		this.bo = gameSetBO == null ? new GameSetBO() : gameSetBO;
		if (gameSetBO == null) {
			bo.setRoomID(room.getRoomID());
			bo.setSetID(this.room.getCurSetID());
		}
		this.resultCalc();

		for(int i = 0; i < this.room.getPlayerNum(); i++){
			GDSetPos roomPos = (GDSetPos) this.m_setPosMgr.getSetPos(i);

			GDRoom_PosEnd posEnd = roomPos.calcPosEnd();
			if (CollectionUtils.isNotEmpty(this.sportsPointList)) {
				this.sportsPointList.set(posEnd.pos, Objects.isNull(posEnd.sportsPoint) ? 0D : posEnd.sportsPoint);
			}
			this.setEnd.posResultList.add(posEnd);
		}

		room.getRoomPosMgr().setAllLatelyOutCardTime();

		this.setEnd.endTime = CommTime.nowSecond();
		String gsonSetEnd = new Gson().toJson(this.setEnd);
		bo.setDataJsonRes(gsonSetEnd);
		bo.setEndTime(this.setEnd.endTime);
		gameSetBoService.saveOrUpDate(bo);
	}

	/**
	 * 获取通知设置
	 * @param pid 用户ID
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GDRoomSetInfo getNotify_set(long pid) {
		GDRoomSetInfo ret = new GDRoomSetInfo();
		ret.roomID = this.room.getRoomID();
		ret.setSetID(this.room.getCurSetID());
		ret.startTime = this.startMS;
		ret.state = this.status.value();
		ret.opPos = m_OpPos;
		ret.opPosList = m_opPosList;
		ret.isFirstOp = m_bFirstOp;
		ret.redPosList = m_redPosList;
		ret.thePostionOfTheFirstDeal = ((GDRoom) this.room).getThePostionOfTheFirstDeal();
		ret.isRedWin = m_isRedWin;
		ret.openCardList = m_openCardList;
		ret.currStep = m_steps;
		ret.isRedStep = m_isRedStep;
		GDRoom gdRoom = (GDRoom) this.room;
		ret.redSteps = gdRoom.getRedSteps();
		ret.blueSteps = gdRoom.getBlueSteps();
		// 位置更新列表
		ret.setPosList(room.getRoomPosMgr().getNotify_PosList());
		if(null != this.curRound){
			ret.opPos 	  = this.curRound.getOpPos();
			ret.isSetEnd  = this.curRound.isSetEnd();
			ret.opType 	  = ((GDRoomSetSound) this.curRound).getOpCardType();
			ret.lastOpPos = this.curRound.getLastOpPos();
			ret.cardList  = this.curRound.getCardList();
			ret.substituteCard = this.curRound.getSubstituteCardList();
		}

		// 每个玩家的牌面
		ret.posInfo = m_setPosMgr.getPosInfo(pid);
		return ret;
	}

	/**
	 * 获取阶段时间
	 * @return
	 */
	public int getWaitTimeByStatus() {
		int waitTime  = 0;
		switch (this.status) {
			case GD_GAME_STATUS_SENDCARD:
				waitTime = 8000;
				break;
			case GD_GAME_STATUS_RESULT:
				waitTime = 10000;
				break;
			case GD_GAME_STATUS_KANGGONG:
				waitTime = 5000;
				break;
			default:
				waitTime = 15000;
				break;
		}
		return waitTime;
	}

	/**
	 * 获取玩家第几个出牌
	 * */
	public int  getOutCardIndex(int pos) {
		int firstPos = ((GDRoom) this.room).getThePostionOfTheFirstDeal();
		int firstIndex = this.m_opPosList.indexOf(firstPos);
		int curIndex = this.m_opPosList.indexOf(pos);
		int playerNum = this.room.getPlayerNum();
		return (curIndex - firstIndex + playerNum) % playerNum;
	}

	/**
	 * 默认位置
	 * **/
	@Override
	public  void  setDefeault(){
		GDRoom gdRoom = (GDRoom) this.room;

		if (GD_Sign.GD_SIGN_TTZ.equals(gdRoom.getGDType())) {
			List<Integer> heartThree = new ArrayList<>(Arrays.asList(0x33,0x83));
			for (int i = 0; i < 2; i++) {
//				int  openCard = this.setCard.getRandomCard();
				int  openCard = heartThree.get(i);
				int pos =  m_setPosMgr.checkCard(openCard);
				GDSetPos roomPos = (GDSetPos) this.m_setPosMgr.getSetPos(pos);
				int cardPos = roomPos.getCardPos(openCard);
				int  openCardPos =getOutCardIndex(pos) + cardPos  * gdRoom.getPlayerNum();
				m_openCardList.add(new ThreeParamentVictory(pos, openCard, openCardPos));
				if (m_redPosList.contains(pos)) {
					for (int j = 0; j < gdRoom.getPlayerNum(); j++) {
						int numPos = gdRoom.getLastOpPosList().get(j);
						if (numPos == m_OpPos) {
							pos = gdRoom.getLastOpPosList().get((j+2)%this.room.getPlayerNum());
							break;
						}
					}
					m_redPosList.add(pos);
				} else {
					m_redPosList.add(pos);
				}

				if (i == 0) {
					setOpPos(pos);
				}
			}

			//换座位
			this.ChangePartnerPos();
		} else {
			if (gdRoom.getCurSetID() == 1) {
				int card = 0x23;
				setOpPos(m_setPosMgr.checkCard(card));
				int xiaoWangPos = m_OpPos;
				for (int i = 0; i < gdRoom.getPlayerNum(); i++) {
					int numPos = gdRoom.getLastOpPosList().get(i);
					if (numPos == m_OpPos) {
						xiaoWangPos = gdRoom.getLastOpPosList().get((i+2)%this.room.getPlayerNum());
						break;
					}
				}

				m_redPosList.add(m_OpPos);
				m_redPosList.add(xiaoWangPos);

				GDSetPos roomPos = (GDSetPos) this.m_setPosMgr.getSetPos(m_OpPos);
				int cardPos = roomPos.getCardPos(card);
				int  openCardPos =getOutCardIndex(m_OpPos) + cardPos  * this.room.getPlayerNum();
				m_openCardList.add(new ThreeParamentVictory(m_OpPos, card, openCardPos));
			} else {
				GDRoomSet roomSet = gdRoom.getLastHistorySet();
				if (null != roomSet) {
					m_redPosList = roomSet.m_redPosList;
					checkGongPai();
				}else{
					CommLogD.error("setDefeault roomSet get null");
					return;
				}
			}
		}

		if(!GD_Sign.GD_SIGN_TTZ.equals(gdRoom.getGDType())){
			m_opPosList.set(0, m_OpPos);
			if (m_redPosList.contains(m_OpPos)) {
				for (int i = 0; i < m_redPosList.size(); i++) {
					if (m_OpPos != m_redPosList.get(i)) {
						m_opPosList.set(2, m_redPosList.get(i));
					}
				}
				int firstOpPos =  (m_OpPos + 1 )  % this.room.getPlayerNum();
				if (m_redPosList.contains(firstOpPos)) {
					for (int i = 0; i <  this.room.getPlayerNum(); i++) {
						firstOpPos =  (firstOpPos + 1 )  % this.room.getPlayerNum();
						if (!m_redPosList.contains(firstOpPos)) {
							break;
						}
					}
				}
				m_opPosList.set(1, firstOpPos);
				for (int i = 0; i < this.room.getPlayerNum(); i++) {
					if (!m_opPosList.contains(i)) {
						m_opPosList.set(3, i);
						break;
					}
				}
			} else {
				m_opPosList.set(1, m_redPosList.get(0));
				m_opPosList.set(3, m_redPosList.get(1));
				for (int i = 0; i < this.room.getPlayerNum(); i++) {
					if (!m_opPosList.contains(i)) {
						m_opPosList.set(2, i);
						break;
					}
				}
			}
		}

		gdRoom.setLastOpPosList(m_opPosList);

		this.setRazzCardList();
	}

	/**
	 * 更换位置
	 *
	 * @param opPosList 位置信息
	 */
	private void ChangePartnerPos() {
		m_opPosList.set(0, m_OpPos);
		if (m_redPosList.contains(m_OpPos)) {
			for (int i = 0; i < m_redPosList.size(); i++) {
				if (m_OpPos != m_redPosList.get(i)) {
					m_opPosList.set(2, m_redPosList.get(i));
				}
			}
			int firstOpPos =  (m_OpPos + 1 )  % this.room.getPlayerNum();
			if (m_redPosList.contains(firstOpPos)) {
				for (int i = 0; i <  this.room.getPlayerNum(); i++) {
					firstOpPos =  (firstOpPos + 1 )  % this.room.getPlayerNum();
					if (!m_redPosList.contains(firstOpPos)) {
						break;
					}
				}
			}
			m_opPosList.set(1, firstOpPos);
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				if (!m_opPosList.contains(i)) {
					m_opPosList.set(3, i);
					break;
				}
			}
		} else {
			m_opPosList.set(1, m_redPosList.get(0));
			m_opPosList.set(3, m_redPosList.get(1));
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				if (!m_opPosList.contains(i)) {
					m_opPosList.set(2, i);
					break;
				}
			}
		}

		//开始发牌
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			long pid = this.room.getRoomPosMgr().getPosByPosID(i).getPid();
			List<PKRoomSet_Pos> notify_set = m_setPosMgr.getPosInfo(pid);
			sourceCardPosList.put(i,notify_set);
		}

		ArrayList<Integer> newRedPosList= new ArrayList<>();
		ArrayList<PKSetPos> newPosDict = new ArrayList<>();
		List<AbsRoomPos> newRoomPosList = Collections.synchronizedList(new ArrayList<>());
		for (int i = 0; i < room.getPlayerNum(); i++) {
			sourceCardMap.put(m_opPosList.get(i),i);
			GDSetPos pos = (GDSetPos) ((GDSetPosMgr)getSetPosMgr()).getSetPos1(m_opPosList.get(i));
			pos.getRoomPos().setPosID(i);
			newPosDict.add(pos);
			newRoomPosList.add(pos.getRoomPos());
//			int partnerPos = i <= 1 ? (i + 2) : (i - 2);
			if(m_redPosList.contains(m_opPosList.get(i))){
				newRedPosList.add(i);
			}

		}
		((GDSetPosMgr)getSetPosMgr()).setPosList(newPosDict);
		room.getRoomPosMgr().setPosList(newRoomPosList);

		this.m_opPosList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
		this.m_OpPos = 0;

		this.m_redPosList.clear();
		for (int i = 0; i < room.getPlayerNum(); i++) {
			if(newRedPosList.contains(i)){
				this.m_redPosList.add(i);
			}
		}
//		m_openCardList.add(new ThreeParamentVictory(pos, openCard, openCardPos));
	}

	/**
	 * 设置赖子
	 * */
	public void  setRazzCardList() {
		m_RazzCardList.clear();
		m_RazzCardList.add(m_steps + BasePocker.PockerColorType.POCKER_COLOR_TYPE_SPADE.value() * 0x10 );
		m_RazzCardList.add(m_steps + BasePocker.PockerColorType.POCKER_COLOR_TYPE_SPADE.value() * 0x10 + BasePocker.LOGIC_MASK_COLOR_MOD);
	}

	/**
	 * 获取王牌
	 * */
	public ArrayList<Integer> getTrumpList() {
		ArrayList<Integer> trumpList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			for (Integer cardValue : BasePocker.Trump_PockerList) {
				trumpList.add(cardValue.intValue() + (BasePocker.LOGIC_MASK_COLOR_MOD)*i);
			}
		}
		return trumpList;
	}

	/**
	 *获取赢家
	 * */
	public int getWinPos() {
		Victory vic = new Victory(-1, -1);
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			PKSetPos setPos = this.m_setPosMgr.getSetPos(i);
			if (-1 == vic.getPos()) {
				vic.setPos(i);
				vic.setNum(setPos.getPoint());
			} else if(vic.getNum() < setPos.getPoint()){
				vic.setPos(i);
				vic.setNum(setPos.getPoint());
			}
		}
		return vic.getPos();
	}


	/**
	 * 逻辑数值
	 * @param cbCardData
	 * @return
	 */
	public   int GetCardLogicValue(int cbCardData)
	{
		//扑克属性
		int bCardValue=BasePocker.getCardValue(cbCardData);

		//转换数值
		return (bCardValue>10)?(10):bCardValue;
	}


	/**
	 * @return m_isRedWin
	 */
	public boolean isRedWin() {
		return m_isRedWin;
	}


	/**
	 */
	public void setIsRedWin(boolean isRedWin) {
		this.m_isRedWin = isRedWin;
	}

	/**
	 * 添加游位置
	 * */
	public void  addFinishOrder(int pos) {
		if (!m_finishOrderList.contains(pos)) {
			m_finishOrderList.add(pos);
		}
	}

	/**
	 * 获取出牌顺序
	 * */
	public ArrayList<Integer> getFinishOrder() {
		return m_finishOrderList;
	}

	/**
	 * 获取几游
	 * */
	public int getFinishOrderByPos(int pos) {
		if (this.room.getHistorySetSize() < 0) {
			return getFinishOrderByPosEx(pos);
		} else {
			if (getStatus().equals(GD_GameStatus.GD_GAME_STATUS_COMPAER) || getStatus().equals(GD_GameStatus.GD_GAME_STATUS_RESULT)) {
				return getFinishOrderByPosEx(pos);
			} else {
				GDRoom gdRoom = (GDRoom) this.room;
				GDRoomSet roomSet = gdRoom.getLastHistorySet();
				if (null == roomSet) {
					return getFinishOrderByPosEx(pos);
				} else {
					int order =  roomSet.getFinishOrderByPosEx(pos);
					if (0 == order) {
						order = roomSet.room.getPlayerNum();
					}
					return order;
				}
			}
		}
	}


	public int getFinishOrderByPosEx(int pos) {
		int finishOrder = 0;
		for (int i = 0; i < m_finishOrderList.size(); i++) {
			if (m_finishOrderList.get(i).equals(pos)) {
				finishOrder = i+1;
			}
		}
		if (getStatus().equals(GD_GameStatus.GD_GAME_STATUS_RESULT) && 0 ==  finishOrder) {
			finishOrder = room.getPlayerNum();
		}
		return finishOrder;
	}


	/**
	 * 理牌操作
	 * @param request
	 * @param pid
	 */
	public void opLiPai(WebSocketRequest request, long pid, ArrayList<ArrayList<Integer>> liPais) {
		if (!getStatus().equals(GD_GameStatus.GD_GAME_STATUS_COMPAER)) {
			request.error(ErrorCode.NotAllow, "state is not YGLFL_GAME_STATUS_COMPAER_ONE curSetStatus="+getStatus());
			return;
		}

		GDSetPos setPos = (GDSetPos) this.m_setPosMgr.getSetPosByPid(pid);
		if (null == setPos) {
			request.error(ErrorCode.NotAllow, "not find your pos:pid="+pid);
			return;
		}

		// 检查选中的理牌是否手上的牌
		if (!setPos.checkLiPai(liPais)) {
			request.error(ErrorCode.NotAllow, "not checkLiPai");
			return;
		}
		request.response();
	}

	/**
	 * 进行进贡
	 * @param request 请求
	 * @param setPos 进贡位置
	 * @param gongPai 贡牌
	 */
	public void opGongPai(WebSocketRequest request, GDSetPos setPos, CGD_GongPai gongPai) {
		if (!getStatus().equals(GD_GameStatus.GD_GAME_STATUS_GONGPAI)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, "state is not GD_GAME_STATUS_GONGPAI curSetStatus="+getStatus());
			return;
		}
		///不需要进贡
		if (!setPos.getGongflag().equals(GD_GONGFLAG.GD_GONGFLAG_GONG_HAVE)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, " gong  flag error, your gong flag ="+setPos.getGongflag());
			return;
		}
		//校验进贡的牌是不是自己的
		ArrayList<Integer> tempList = new ArrayList<>(Collections.singletonList(gongPai.card));
		if (!setPos.checkIsMyCard(tempList)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, "card is not your");
			return;
		}
		//进贡的不能是红心参谋
		if (getRazzCardList().contains(gongPai.card)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, " card is razz cards  gongPai Card ="+gongPai.card );
			return;
		}
		//校验是不是除红心参谋外最大的牌
		Map<Integer, List<Integer>> map = getCardMap(setPos.getPrivateCards(), false);
		for(Map.Entry<Integer, List<Integer>> entry:map.entrySet()){
			List<Integer> leaveList = entry.getValue().stream().filter(n -> !getRazzCardList().contains(n)).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(leaveList)){
				if (!leaveList.contains(gongPai.card)) {
					if(null != request)
						request.error(ErrorCode.NotAllow, " card is not max your max cards  ="+entry.getValue().toString() );
					return;
				}else{
					break;
				}
			}
		}
		//删除牌
		if(!setPos.deleteCard(tempList)){
			if(null != request)
				request.error(ErrorCode.NotAllow, " card delete error delete card ="+tempList.toString()+", your PrivateCards="+setPos.getPrivateCards().toString() );
			return;
		}
		//设置进贡的牌
		setPos.setGongCard(gongPai.card);
		//设置已经进贡
		setPos.setGongflag(GD_GONGFLAG.GD_GONGFLAG_GONGED);
		//通知进贡完成
		GDRoomSet_Pos selfSetPosInfo  = setPos.getSetPosInfo(true);
		GDRoomSet_Pos otherSetPosInfo  = setPos.getSetPosInfo(false);
		getRoomPlayBack().playBack2Pos(setPos.getPosID(),SGD_GongPai.make(this.room.getRoomID(), setPos.getPosID(), gongPai.card,selfSetPosInfo),
				this.getSetPosMgr().getAllPlayBackNotify());
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			if (i == setPos.getPosID())
				continue;
			room.getRoomPosMgr().notify2Pos(i, SGD_GongPai.make(this.room.getRoomID(), setPos.getPosID(), gongPai.card,otherSetPosInfo));
		}
		//所有人都已经进贡，标志进贡结束，进入还贡阶段
		if(!((GDSetPosMgr) m_setPosMgr).checkAllGongPai(GD_GONGFLAG.GD_GONGFLAG_GONG_HAVE))
			gongPaiEnd();
	}


	/**
	 * 还贡
	 * @param request 请求
	 * @param setPos 贡牌位置
	 * @param huAnGong 还贡
	 */
	public void opHuAnGongPai(WebSocketRequest request, GDSetPos setPos, CGD_HuanGongPai huAnGong) {
		if (!getStatus().equals(GD_GameStatus.GD_GAME_STATUS_HUANPAI)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, "state is not GD_GAME_STATUS_HUANPAI curSetStatus="+getStatus());
			return;
		}
		//校验牌是不是自己
		ArrayList<Integer> tempList = new ArrayList<>(Collections.singletonList(huAnGong.huanCard));
		if (!setPos.checkIsMyCard(tempList)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, "card is not your");
			return;
		}
		//是不是需要进贡
		if (!setPos.getGongflag().equals(GD_GONGFLAG.GD_GONGFLAG_HUAANGONG_HAVE)) {
			if(null != request)
				request.error(ErrorCode.NotAllow, " gong  flag error, your gong flag ="+setPos.getGongflag());
			return;
		}
		//需要进贡给的人是否存在
		GDSetPos gainGongPos = (GDSetPos) m_setPosMgr.getSetPos(setPos.getHuangongCardPos());
		if (null == gainGongPos) {
			if(null != request)
				request.error(ErrorCode.NotAllow, " HuangongCardPos is null setPos.getHuangongCardPos()="+setPos.getHuangongCardPos() );
			return;
		}
		//头游和尾游是搭档，还贡的牌需要是小于A的
		if (getHuoBanPos(setPos.getPosID()) == gainGongPos.getPosID()) {
			if (getFinishOrderByPos(setPos.getPosID()) == 1 && this.room.getPlayerNum() == getFinishOrderByPos(gainGongPos.getPosID())) {
				if (BasePockerLogic.getCardValueEx(huAnGong.huanCard) >=  0x0E || BasePockerLogic.getCardValueEx(huAnGong.huanCard) == getRazzCardValue()) {
					if(null != request)
						request.error(ErrorCode.NoPower_CARD_NOTALLOW, " HuangongCardPos huan gong card is less than 0x0E  your huanCard is "+huAnGong.huanCard );
					return;
				}
			}
		}
		//删除牌
		if(!setPos.deleteCard(tempList)){
			if(null != request)
				request.error(ErrorCode.NotAllow, " card delete error delete card ="+tempList.toString()+", your PrivateCards="+setPos.getPrivateCards().toString() );
			return;
		}
		ThreeParamentVictoryEx victory = new ThreeParamentVictoryEx();
		victory.setParamentVictory(huAnGong.huanCard, setPos.getPosID(), gainGongPos.getPosID());
		m_gongPaiList.add(victory);

		setPos.setGongCard(huAnGong.huanCard);
		setPos.setGongflag(GD_GONGFLAG.GD_GONGFLAG_HUAANGONGED);
		gainGongPos.addCard(huAnGong.huanCard);
		gainGongPos.setHuangongCardAndPos(setPos.getPosID(), huAnGong.huanCard);
		//通知还贡
		getRoomPlayBack().playBack2Pos(setPos.getPosID(),
				SGD_HuanGongPai.make(this.room.getRoomID(), setPos.getPosID(), huAnGong.huanCard,setPos.getSetPosInfo(true)/*, gainGongPos.getSetPosInfo(false)*/),
				this.getSetPosMgr().getAllPlayBackNotify());
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			if (i == setPos.getPosID())
				continue;
			room.getRoomPosMgr().notify2Pos(i, SGD_HuanGongPai.make(this.room.getRoomID(), setPos.getPosID(), huAnGong.huanCard, setPos.getSetPosInfo(false)/*,  gainGongPos.getSetPosInfo(false)*/));
		}
		//校验都还贡
		if(!((GDSetPosMgr) m_setPosMgr).checkAllGongPai(GD_GONGFLAG.GD_GONGFLAG_HUAANGONG_HAVE))
			huAnGongEnd();
	}


	public void onOpenCard(WebSocketRequest request,long pid, CGD_OpenCard openCard){

		if(this.status != GD_GameStatus.GD_GAME_STATUS_RESULT){
			if(null != request)
				request.error(ErrorCode.NotAllow, "onOpenCard error:state is not  can open card  state:"+this.status);
			return;
		}

		GDSetPos pos = (GDSetPos) this.m_setPosMgr.getSetPosByPid(pid);
		if (null == pos) {
			if(null != request)
				request.error(ErrorCode.NotAllow, "onOpenCard error:not find player pos  pid:"+pid);
			return;
		}



		if(null != request) request.response();

		this.room.getRoomPosMgr().notify2All(SGD_OpenCard.make(openCard.roomID, pos.getPosID(), openCard.OpenCard, pos.getPrivateCards()));
	}


	public GD_WINFLAG getWinFlag() {
		return m_winFlag;
	}


	public void setWinFlag(GD_WINFLAG m_winFlag) {
		this.m_winFlag = m_winFlag;
	}

//	/**
//	 * 根据牌获取分数
//	 * */
//	public int  getScoreByCardList(ArrayList<Integer> cardList) {
//		int score = 0;
//		GDRoom xyzbRoom = (GDRoom) this.room;
//		GDConfigMgr configMgr = (GDConfigMgr) xyzbRoom.getConfigMgr();
//		for (Integer integer : cardList) {
//			Integer cardValue =  BasePockerLogic.getCardValue(integer);
//			if (!configMgr.getScoreCardList().contains(cardValue)) {
//				continue;
//			}
//			score += this.GetCardLogicValue(integer) ;
//		}
//		return score;
//	}


	/**
	 * @return m_steps
	 */
	public int getSteps() {
		return m_steps;
	}

	/**
	 * 获取级数牌值
	 * */
	public int getStepsValue(){
		return BasePockerLogic.getCardValueEx(getSteps());
	}

	/**
	 * 根据位置获取对家的位置
	 * */
	public int  getHuoBanPos(int pos) {
		int otherPos = - 1;
		if (this.m_redPosList.contains(pos)) {
			for (Integer integer : this.m_redPosList) {
				if (integer != pos) {
					otherPos = integer;
				}
			}
		} else {
			for (Integer integer : this.m_opPosList) {
				if (!this.m_redPosList.contains(integer) && integer != pos) {
					otherPos = integer;
				}
			}
		}
		return otherPos;
	}

	/***
	 * ------------------算法修改--------------------------
	 * ***/

	/**
	 * 获取牌值
	 * */
	@Override
	public int getCardValueEx(int card)
	{
		int cardValue =  BasePockerLogic.getCardValueEx(card);
		if (cardValue == getStepsValue()) {
			cardValue = 0x10;
		}
		return cardValue;
	}


	/**
	 * 比较值的大小 包含大小王 left比rigth大返回ture否侧false
	 * */
	public boolean compareCard(int left, int rigth){
		if (BasePockerLogic.getCardValueEx(left) == this.curRound.getRazzCardValue() &&
				BasePockerLogic.getCardValueEx(rigth) == this.curRound.getRazzCardValue()) {
			return false;
		}else if (BasePockerLogic.getCardValueEx(left) == this.curRound.getRazzCardValue() &&
				BasePockerLogic.getCardValueEx(rigth) != this.curRound.getRazzCardValue()) {
			return BasePockerLogic.getCardValueEx(rigth) < 0x10;
		}else if (BasePockerLogic.getCardValueEx(left) != this.curRound.getRazzCardValue() &&
				BasePockerLogic.getCardValueEx(rigth) == this.curRound.getRazzCardValue()) {
			return BasePockerLogic.getCardValueEx(left) > 0x10;
		}
		else if (super.compareCard(left,rigth)) {
			return true;
		}
		return false;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	/***
	 * ------------------算法修改结束--------------------------
	 * ***/

	public void setBombScore(int bombScore) {
		this.bombScore = bombScore;
	}

	/**
	 * 检查小局托管自动解散
	 */
	public boolean checkSetEndTrusteeshipAutoDissolution() {
		if(((GDRoom) room).isSetAutoJieSan()){
			return true;
		}
		return false;
	}

	/**
	 * 小局托管自动解散
	 */
	public void setTrusteeshipAutoDissolution() {
		// 检查小局托管自动解散
		if (checkSetEndTrusteeshipAutoDissolution()) {
			// 获取托管玩家pid列表
			List<Long> trusteeshipPlayerList = room.getRoomPosMgr().getRoomPosList().stream()
					.filter(n -> n.isTrusteeship()).map(n -> n.getPid()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(trusteeshipPlayerList)) {
				// 记录回放中
				getRoomPlayBack().addPlaybackList(
						DissolveTrusteeship(room.getRoomID(), trusteeshipPlayerList, CommTime.nowSecond()),
						null);
				room.setTrusteeshipDissolve(true);
			}
		}
	}

	/**
	 * 小局托管自动解散回放记录 注意：需要自己重写
	 *
	 * @param roomId  房间id
	 * @param pidList 托管玩家Pid
	 * @param sec     记录时间
	 * @return
	 */
	public BaseSendMsg DissolveTrusteeship(long roomId, List<Long> pidList, int sec) {
		SGD_DissolveTrusteeship ret = new SGD_DissolveTrusteeship();
		ret.setRoomID(roomId);
		ret.setTrusteeship(pidList);
		ret.setEndSec(sec);
		return ret;
	}

}
