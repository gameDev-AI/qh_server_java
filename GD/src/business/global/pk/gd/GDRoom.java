package business.global.pk.gd;

import java.util.ArrayList;
import java.util.List;

import business.gd.c2s.cclass.GD_define;
import business.gd.c2s.iclass.*;
import business.global.pk.gd.pkbase.PKRoom;
import business.global.room.ContinueRoomInfoMgr;
import business.global.room.RoomRecordMgr;
import business.global.room.base.AbsRoomPos;
import business.global.room.base.AbsRoomPosMgr;
import cenum.ChatType;
import cenum.ClassType;
import cenum.PrizeType;
import cenum.room.GaoJiTypeEnum;
import cenum.room.RoomState;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import business.player.Player;
import business.player.PlayerMgr;
import business.player.Robot.Robot;
import business.player.Robot.RobotMgr;
import com.google.gson.Gson;
import core.db.entity.clarkGame.ClubMemberBO;
import business.gd.c2s.cclass.GDRoom_RecordPosInfo;
import business.gd.c2s.cclass.GD_define.GD_Sign;
import business.gd.c2s.cclass.GD_define.GD_WINFLAG;
import core.db.other.AsyncInfo;
import jsproto.c2s.cclass.BaseSendMsg;
import jsproto.c2s.cclass.RoomEndResult;
import jsproto.c2s.cclass.pk.BasePockerLogic;
import jsproto.c2s.cclass.pk.PKRoom_Record;
import jsproto.c2s.cclass.pk.PKRoom_RecordPosInfo;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.ContinueRoomInfo;
import jsproto.c2s.cclass.room.GetRoomInfo;
import jsproto.c2s.cclass.room.RoomPosInfo;
import jsproto.c2s.iclass.S_GetRoomInfo;
import jsproto.c2s.iclass.room.SBase_Dissolve;
import jsproto.c2s.iclass.room.SBase_PosLeave;

public class GDRoom extends PKRoom {
	public CGD_CreateRoom roomCfg;
	private ArrayList<Long>  m_XiPaiPidList = new ArrayList<Long>();
	private GDConfigMgr configMgr = new GDConfigMgr();
	protected int m_thePostionOfTheFirstDeal = 0;//记录上局赢家
	protected ArrayList<Integer> m_lastOpPosList = new ArrayList<>();		//出牌顺序
	protected int  m_redSteps = 0x02; //级数
	protected int  m_blueSteps = 0x02; //级数
	protected int  m_redStepsBack = 0x02; //级数
	protected int  m_blueStepsBack = 0x02; //级数
	protected boolean		  m_lastSetIsRedWin = true;//是否是红方赢

	/**
	 *
	 * @param baseRoomConfigure
	 * @param roomKey
	 * @param ownerID
	 */
	protected GDRoom(BaseRoomConfigure baseRoomConfigure, String roomKey, long ownerID) {
		super(baseRoomConfigure, roomKey, ownerID);
		this.roomCfg = (CGD_CreateRoom) baseRoomConfigure.getBaseCreateRoom();
		for (int i = 0; i < this.getPlayerNum(); i++) {
			m_lastOpPosList.add(i);
		}

		m_thePostionOfTheFirstDeal = 0;

		if (GD_Sign.GD_SIGN_JD.equals(getGDType()) && isGodCard()) {
			m_redStepsBack = BasePockerLogic.getCardValueEx( configMgr.getRedSteps());
			m_blueStepsBack = BasePockerLogic.getCardValueEx(configMgr.getBlueSteps());
		}
	}

	@Override
	public boolean isGodCard() {
		return this.getConfigMgr().isGodCard();
	}

	@Override
	public String dataJsonCfg() {
		return new Gson().toJson(this.getRoomCfg());
	}

	@Override
	public AbsRoomPosMgr initRoomPosMgr() {
		return new GDRoomPosMgr(this);
	}

	//创建set
	@SuppressWarnings("rawtypes")
	public void  createSet(){
		this.m_blueSteps = this.m_blueStepsBack;
		this.m_redSteps = this.m_redStepsBack;
		if (null != this.getCurSet()) {
			this.getCurSet().clear();
			this.setCurSet(null);
		}
		this.setCurSet(new GDRoomSet(this));
	}

	@Override
	public void notifyAllRoomEnd(PKRoom_Record sRecord) {

	}

	@Override
	public void startNewSet() {
		this.setCurSetID(this.getCurSetID() + 1);
		this.getRoomPosMgr().clearGameReady();
		this.createSet();
		this.getRoomTyepImpl().roomSetIDChange();
	}

	@Override
	public void cancelTrusteeship(AbsRoomPos pos) {
		((GDRoomSet) this.getCurSet()).roomTrusteeship(pos.getPosID());
	}

	@Override
	public void RobotDeal(int pos) {
		((GDRoomSet) this.getCurSet()).roomTrusteeship(pos);
	}

	@Override
	public void roomTrusteeship(int pos) {
		if (getCurSet() != null && ((GDRoomSet) getCurSet()).getCurRound() != null) {
			((GDRoomSet) this.getCurSet()).roomTrusteeship(pos);
		}
	}

	@Override
	public void setEndRoom() {
		if (null != this.getCurSet()) {
			RoomRecordMgr.getInstance().add(this);
			this.getRoomPosMgr().notify2All(SGD_RoomEnd.make(this.getPKRoomRecordInfo()));
			refererReceiveList();
		}
	}

	/**
	 */
	public PKRoom_Record getPKRoomRecordInfo(){
		PKRoom_Record pkRoom_record = new PKRoom_Record();
		pkRoom_record.setCnt = this.getHistorySetSize();
		pkRoom_record.recordPosInfosList  = this.getRecordPosInfoList();
		pkRoom_record.roomID = this.getRoomID();
		pkRoom_record.endSec = this.getGameRoomBO().getEndTime();
		pkRoom_record.roomKey=this.getRoomKey();
		return pkRoom_record;
	}

	@Override
	public void calcEnd() {
		super.calcEnd();
		if (null != this.getGameRoomBO()) {
			RoomEndResult sEndResult = getRoomEndResult();
			if (null != sEndResult) {
				String gsonEndResult = new Gson().toJson(sEndResult);
				this.getGameRoomBO().setDataJsonRes(gsonEndResult);
			}
		}
		String playerPosList = new Gson().toJson(getRoomPosMgr().getRoomPlayerPosList());
		int num = (this.getRoomPosMgr()).getPlayTheGameNum();
		this.getGameRoomBO().setPlayerNum(num);
		this.getGameRoomBO().setPlayerList(playerPosList);
		this.getGameRoomBO().setEndTime(CommTime.nowSecond());
		this.getGameRoomBO().getBaseService().saveOrUpDate(this.getGameRoomBO(), new AsyncInfo(this.getGameRoomBO().getId()));
	}

	@Override
	public BaseSendMsg Trusteeship(long roomID, long pid, int pos, boolean trusteeship) {
		return SGD_Trusteeship.make(roomID, pid, pos, trusteeship);
	}

	@Override
	public BaseSendMsg PosLeave(SBase_PosLeave posLeave) {
		return SGD_PosLeave.make(posLeave);
	}

	@Override
	public BaseSendMsg LostConnect(long roomID, long pid, boolean isLostConnect,boolean isShowLeave) {
		return SGD_LostConnect.make(roomID, pid, isLostConnect,isShowLeave);
	}

	@Override
	public BaseSendMsg PosContinueGame(long roomID, int pos) {
		return  SGD_PosContinueGame.make(roomID, pos);
	}

	@Override
	public BaseSendMsg PosUpdate(long roomID, int pos, RoomPosInfo posInfo, int custom) {
		return SGD_PosUpdate.make(roomID, pos, posInfo, custom,getRoomPosMgr().getNotify_PosList());
	}

	@Override
	public BaseSendMsg PosReadyChg(long roomID, int pos, boolean isReady) {
		return SGD_PosReadyChg.make(roomID, pos, isReady);
	}

	@Override
	public BaseSendMsg Dissolve(SBase_Dissolve dissolve) {
		return SGD_Dissolve.make(dissolve);
	}

	@Override
	public BaseSendMsg StartVoteDissolve(long roomID, int createPos, int endSec) {
		return SGD_StartVoteDissolve.make(roomID, createPos, endSec);
	}

	@Override
	public BaseSendMsg PosDealVote(long roomID, int pos, boolean agreeDissolve,int endSec) {
		return SGD_PosDealVote.make(roomID, pos, agreeDissolve);
	}

	@Override
	public BaseSendMsg Voice(long roomID, int pos, String url) {
		return SGD_Voice.make(roomID, pos, url);
	}

	@Override
	public BaseSendMsg XiPai(long roomID, long pid, ClassType cType) {
		return SGD_XiPai.make(roomID, pid, cType);
	}

	@Override
	public BaseSendMsg ChatMessage(long pid, String name, String content, ChatType type, long toCId, int quickID) {
		return SGD_ChatMessage.make(pid, name, content, type, toCId, quickID);
	}

	@Override
	public <T> BaseSendMsg RoomRecord(List<T> records) {
		return SGD_RoomRecord.make(records);
	}

	@Override
	public BaseSendMsg ChangePlayerNum(long roomID, int createPos, int endSec, int playerNum) {
		return SGD_ChangePlayerNum.make(roomID, createPos, endSec, playerNum);
	}

	@Override
	public BaseSendMsg ChangePlayerNumAgree(long roomID, int pos, boolean agreeChange) {
		return SGD_ChangePlayerNumAgree.make(roomID, pos, agreeChange);
	}

	@Override
	public BaseSendMsg ChangeRoomNum(long roomID, String roomKey, int createType) {
		return SGD_ChangeRoomNum.make(roomID, roomKey, createType);
	}

	@Override
	public GetRoomInfo getRoomInfo(long pid) {
		S_GetRoomInfo ret = new S_GetRoomInfo();
		this.getBaseRoomInfo(ret);
		if (null != this.getCurSet()) {
			ret.setSet(this.getCurSet().getNotify_set(pid));
		} else {
			GDRoomSetInfo setInfo=new GDRoomSetInfo();
			setInfo.redSteps = m_redSteps;
			setInfo.blueSteps = m_blueSteps;
			ret.setSet(setInfo);
		}
		return ret;
	}

	@Override
	public boolean isCanChangePlayerNum() {
		return false;
	}

	@Override
	public <T> T getCfg() {
		return (T) getRoomCfg();
	}

	@Override
	protected List<PKRoom_RecordPosInfo> getRecordPosInfoList() {
		List<PKRoom_RecordPosInfo> sRecord = new ArrayList<PKRoom_RecordPosInfo>();
		for(int i = 0; i < this.getPlayerNum() ; i++){
			GDRoom_RecordPosInfo posInfo = new GDRoom_RecordPosInfo();
			
			GDRoomPos roomPos = (GDRoomPos) this.getRoomPosMgr().getPosByPosID(i);
			//六副里特殊处理
			posInfo.flatCount = roomPos.getFlat();
			posInfo.loseCount = roomPos.getLose();
			posInfo.winCount = roomPos.getWin();
			posInfo.firstFinishOrderCount = roomPos.getFirstFinishOrderCount();
			posInfo.point = roomPos.getPoint();
			posInfo.pos = i;
			posInfo.pid = roomPos.getPid();
			posInfo.setSportsPoint(roomPos.sportsPoint());
			sRecord.add(posInfo);
		}
		return sRecord;
	}

	/**
	 * @return configMgr
	 */
	public GDConfigMgr getConfigMgr() {
		return configMgr;
	}

	/**
	 *
	 * @return
	 */
	public CGD_CreateRoom getRoomCfg() {
		if(this.roomCfg == null){
			return (CGD_CreateRoom) getBaseRoomConfigure().getBaseCreateRoom();
		}
		return this.roomCfg;
	}


	@Override
	public void clearEndRoom() {
		super.clear();
		this.roomCfg = null;
		this.configMgr = null;

	}

	/**
	 * @return m_XiPaiPidList
	 */
	@Override
	public ArrayList<Long> geXiPaiPidList() {
		return m_XiPaiPidList;
	}

	/**
	 * @return m_XiPaiPidList
	 */
	@Override
	public void clearXiPaiPidList() {
		m_XiPaiPidList.clear();
	}

	/**
	 * 获取房间人数
	 */
	@Override
	public int getPlayerNum() {
		return this.getRoomCfg().getPlayerNum();
	}
	
	/**
	 * @return m_lastWinPos
	 */
	public int getThePostionOfTheFirstDeal() {
		return m_thePostionOfTheFirstDeal;
	}

	/**
	 */
	public void setThePostionOfTheFirstDeal(int thePostionOfTheFirstDeal) {
		this.m_thePostionOfTheFirstDeal = thePostionOfTheFirstDeal;
	}

	/**
	 * @return m_lastOpPosList
	 */
	public ArrayList<Integer> getLastOpPosList() {
		return m_lastOpPosList;
	}

	/**
	 * @param m_lastOpPosList 要设置的 m_lastOpPosList
	 */
	public void setLastOpPosList(ArrayList<Integer> m_lastOpPosList) {
		this.m_lastOpPosList = m_lastOpPosList;
	}
	


	@Override
	public void endSet() {
		// TODO 自动生成的方法存根
		if(null != this.getCurSet()){
			this.getCurSet().endSet();
			GDRoomSet roomSet = (GDRoomSet) this.getCurSet();
			if (isGodCard()) {
				roomSet.setWinFlag(GD_WINFLAG.GD_WINFLAG_WINWIN);
				roomSet.m_finishOrderList.add(0);
				roomSet.m_finishOrderList.add(1);
			}
		}
	}
	
	public GD_Sign getGDType(){
		return GD_Sign.valueOf(this.getRoomCfg().getSign());
	}

	/**
	 * 获取结束条件
	 *
	 * @return
	 */
	@Override
	public int getEndCount() {
		if (GD_Sign.GD_SIGN_TTZ.equals(getGDType())) {
			return this.getBaseRoomConfigure().getBaseCreateRoom().getSetCount();
		}else{
			return this.getBaseRoomConfigure().getBaseCreateRoom().getSetCount()+1;
		}
	}

	/**
	 * 房间是否结束
	 * @return
	 */
	public boolean isEnd(){
		if (GD_Sign.GD_SIGN_TTZ.equals(getGDType())) {
			return false;
		}else{
			int steps = m_redStepsBack > m_blueStepsBack ? m_redStepsBack : m_blueStepsBack;
			return steps > this.getRoomCfg().getSetCount()%400;
		}
	}

	/**
	 * @return m_redSteps
	 */
	public int getRedSteps() {
		return m_redSteps;
	}

	/**
	 * @return m_blueSteps
	 */
	public int getBlueSteps() {
		return m_blueSteps;
	}

	/**
	 */
	public void setStepsByWinFlag(boolean isRedWin, int num) {
		m_lastSetIsRedWin  = isRedWin;
		if (m_lastSetIsRedWin) {
			if (GD_Sign.GD_SIGN_JD.equals(getGDType()) && m_redStepsBack != this.getRoomCfg().getSetCount()%400) {
				m_redStepsBack = Math.min(m_redStepsBack+num, this.getRoomCfg().getSetCount()%400);
			}else if(GD_Sign.GD_SIGN_JD.equals(getGDType()) && m_redStepsBack == this.getRoomCfg().getSetCount()%400 && 1 == num ){
				m_redStepsBack = Math.min(m_redStepsBack+num, this.getRoomCfg().getSetCount()%400);
			}else{
				m_redStepsBack += num;
			}
		} else {
			if (GD_Sign.GD_SIGN_JD.equals(getGDType()) && m_blueStepsBack != this.getRoomCfg().getSetCount()%400) {
				m_blueStepsBack = Math.min(m_blueStepsBack+num, this.getRoomCfg().getSetCount()%400);
			}else if(GD_Sign.GD_SIGN_JD.equals(getGDType()) && m_blueStepsBack == this.getRoomCfg().getSetCount()%400 && 1 == num ){
				m_blueStepsBack = Math.min(m_blueStepsBack+num, this.getRoomCfg().getSetCount()%400);
			}else{
				m_blueStepsBack += num;
			}
		}
	}

	/**
	 * 获取下局级数
	 * */
	public int  getSteps() {
		if (m_lastSetIsRedWin) {
			return m_redSteps;
		} else {
			return m_blueSteps;
		}
	}

	public boolean LastSetIsRedWin() {
		return m_lastSetIsRedWin;
	}
	
	/**
	 * 换位置
	 * */
	public boolean  onSitDown(WebSocketRequest request,long pid,   int posID) {

		boolean ret = true;
		try {
			lock();
			if (!getRoomState().equals(RoomState.Init)) {
				request.error(ErrorCode.NotAllow, "room state ="+getRoomState());
				ret = false;
				return ret;
			}

			GDRoomPos posPre = (GDRoomPos) this.getRoomPosMgr().getPosByPid(pid);
			//检查玩家位置没有人。
			if (null == posPre){
				ret = false;
				request.error(ErrorCode.NotAllow,"null == posPre");
				return ret;
			}
			// 位置上的玩家ID != 选择位置的人
			if (posPre.getPid() != pid) {
				ret = false;
				request.error(ErrorCode.NotAllow,"posPre.pid != pid");
				return ret;
			}

			GDRoomPos pos = (GDRoomPos) this.getRoomPosMgr().getEmptyPos(posID);
			// 检查选择的位置ID正确
			if (null == pos) {
				ret = false;
				request.error(ErrorCode.NotAllow,"null == pos");
				return ret;
			}
			boolean isReady = posPre.isReady();
			ClubMemberBO clubMemberBO = posPre.getClubMemberBO();
			// 重新选择位置
			posPre.resetSelectPos();
			//进入座位。
			pos.seatPos(pid,0,isReady,clubMemberBO);

			//检查玩家是否机器人。
			if (pid < RobotMgr.getInstance().limitID && pid != 0) {
				Robot rb = RobotMgr.getInstance().getRobot((int) pid);
				//机器人是否存在
				if (null != rb && this.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold) {
					// 设置用户准备
					pos.setReady(true);
				}
			} else {
				Player player = PlayerMgr.getInstance().getPlayer(pid);
				//获取玩家信息
				if (null != player) {
					//设置玩家当前游戏
					player.setCurrentGameType(getBaseRoomConfigure().getGameType().getId());
					//正式进入房间
					player.onEnterRoom(getRoomID());
					//进入练习场
					if (this.getBaseRoomConfigure().getPrizeType() == PrizeType.Gold) {
						// 设置用户准备
						pos.setReady(true);
					}
				}
			}
			request.response();
		}catch (Exception e){
			CommLogD.error(e.getMessage());
		}finally {
			unlock();
		}
		return ret;
	}

	@Override
	public int getPlayingCount() {
		return this.getPlayerNum();
	}

	/**
	 * 贡牌操作
	 * */
	public void opGongPai(WebSocketRequest request, GDSetPos setPos, CGD_GongPai gongPai) {
		lock();
		do {
			GDRoomSet set =  (GDRoomSet) this.getCurSet();
	    	if(null == set){
	    		request.error(ErrorCode.NotAllow, "onOpCard not set room:"+gongPai.roomID);
	    		break;
	    	}
	    	set.opGongPai(request,  setPos, gongPai);
		} while (false);
		unlock();
	}
	
	/**
	 * 贡牌操作
	 */
	public void opHuanGongPai(WebSocketRequest request, GDSetPos setPos, CGD_HuanGongPai huanGong) {
		lock();
		do {
			GDRoomSet set =  (GDRoomSet) this.getCurSet();
	    	if(null == set){
	    		request.error(ErrorCode.NotAllow, "onOpCard not set room:"+huanGong.roomID);
	    		break;
	    	}
	    	set.opHuAnGongPai(request,  setPos , huanGong);
		} while (false);
		unlock();
	}


	/**
	 * 继续功能
	 */
	@Override
	protected void continueRoom() {
		ContinueRoomInfo continueRoomInfo=new ContinueRoomInfo();
		continueRoomInfo.setRoomID(this.getRoomID());
		continueRoomInfo.setBaseRoomConfigure(this.getBaseRoomConfigure().deepClone());
		continueRoomInfo.setRoomEndTime(this.getGameRoomBO().getEndTime());
		continueRoomInfo.setPlayerIDList(this.getRoomPidAll());
		ContinueRoomInfoMgr.getInstance().putContinueRoomInfo(continueRoomInfo);
	}

	public GDRoomSet getLastHistorySet() {
		if(getHistorySetSize()  > 0)
			return (GDRoomSet) getHistorySet().get(getHistorySetSize() - 1);
		else
			return null;
	}

	/**
	 * 神牌消息
	 *
	 * @param msg
	 * @param pid
	 */
	@Override
	public void godCardMsg(String msg, long pid) {
		if (isGodCard() && "x".equals(msg)) {
			getCurSet().endSet();
		}
	}

	public boolean isShuangXiaSiJi(){
		return getRoomCfg().getKexuanwanfa().contains(GD_define.GD_KeXuanWanFa1.ShuangXia4.ordinal());
	}

	public boolean isFanBei6(){
		return getRoomCfg().getKexuanwanfa().contains(GD_define.GD_KeXuanWanFa1.FanBei6.ordinal());
	}

	public boolean isFanBei4(){
		return getRoomCfg().getKexuanwanfa().contains(GD_define.GD_KeXuanWanFa1.FanBei4.ordinal());
	}

	/**
	 * 自动准备游戏 玩家加入房间时，自动进行准备。
	 */
	@Override
	public boolean autoReadyGame() {
		return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GD_define.GDGameRoomConfigEnum.ZiDongZhunBei.ordinal());
	}

	/**
	 * 房主是否需要准备
	 *
	 * @return
	 */
	@Override
	public boolean ownerNeedReady() {
		return !this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GD_define.GDGameRoomConfigEnum.ZiDongZhunBei.ordinal());
	}

	/**
	 * 是否小局自动解散
	 *
	 * @return boolean
	 */
	public boolean isSetAutoJieSan() {
		return this.getBaseRoomConfigure().getBaseCreateRoom().getFangjian().contains(GD_define.GDGameRoomConfigEnum.SetAutoJieSan.ordinal());
	}

	/**
	 * 30秒未准备自动退出
	 * @return
	 */
	@Override
	public boolean is30SencondTimeOut() {
		return checkGaoJiXuanXiang(GaoJiTypeEnum.SECOND_TIMEOUT_30);
	}
}
