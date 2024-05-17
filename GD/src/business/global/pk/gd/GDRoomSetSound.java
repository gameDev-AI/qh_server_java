package business.global.pk.gd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.PKLogicFactory;
import business.global.pk.gd.pkbase.optype.danzhangex.DanZhangCardEx_GD;
import business.global.pk.gd.pkbase.optype.duiziex.DuiZiCardEx_GD;
import business.global.pk.gd.pkbase.optype.feijiex.SanBuDaiFeiJiEx_GD;
import business.global.pk.gd.pkbase.optype.lianduiex.LianDuiCardEx_GD;
import business.global.pk.gd.pkbase.optype.sanzhangex.SanBuDaiCardEx_GD;
import business.global.pk.gd.pkbase.optype.sanzhangex.SanDaiDuiZiCardEx_GD;
import business.global.pk.gd.pkbase.optype.shunziex.ShunZiCardEx_GD;
import business.global.pk.gd.pkbase.optype.shunziex.ShunZiCardEx_TongHua_GD;
import business.global.pk.gd.pkbase.optype.zhadanex.ZhaDanCardEx_GD_LiuZha;
import business.global.pk.gd.pkbase.optype.zhadanex.ZhaDanCardEx_GD_SiTrump;
import business.global.pk.gd.pkbase.optype.zhadanex.ZhaDanCardEx_GD_SiWuZha;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import business.gd.c2s.cclass.GD_define.GD_CARD_TYPE;
import business.gd.c2s.cclass.GD_define.GD_WINFLAG;
import business.gd.c2s.iclass.CGD_OpCard;
import business.gd.c2s.iclass.SGD_OpCard;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 仙游炸棒一局游戏逻辑
 * @author zaf
 *
 */

public class GDRoomSetSound extends PKRoomSetSound {
	public GDRoomSet zRoomSet = null;
	
	public GDRoomSetSound( GDRoomSet set) {
		super(set);
		zRoomSet = set;
		m_opCardType = GD_CARD_TYPE.GD_CARD_TYPE_NOMARL.value();
	}
	
	
	
	/**
	 * 托管
	 * @param pos
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void roomTrusteeship(int pos) {
		try {
			if(m_bSetEnd) return;
			
			
			if(this.m_OpPos != pos){
				return;
			}
			
			if(CommTime.nowMS()  - this.set.startMS <= 1000 ){
				return;
			}
			
			GDSetPos setPos = (GDSetPos) this.set.getSetPosMgr().getSetPos(pos);
			if (setPos == null) {
				return;
			}
			
			if(setPos.getPrivateCards().size() <= 0){
				this.checkEndSet();
				return;
			}
			
			ArrayList<Integer> tempCard =(ArrayList<Integer>) setPos.getPrivateCards().clone();
			tempCard.sort(BasePockerLogic.sorterBigToSmallNotTrump);

			PK_OpCard outList = new PK_OpCard();

			int opCardType;
			m_opCardTypeBackUp = m_opCardType;
			GDRoom xyzbRoom = (GDRoom) this.room;
			switch (GD_CARD_TYPE.valueOf(m_opCardType)) {
			case GD_CARD_TYPE_DUIZI:  			//对子
				{
					outList =  PKLogicFactory.getOpCard(DuiZiCardEx_GD.class).getCard(tempCard, this);
				}
				break;
			case GD_CARD_TYPE_THREE:  		//3不带
				{
					outList =  PKLogicFactory.getOpCard(SanBuDaiCardEx_GD.class).getCard(tempCard, this);
				}
				break;
			case 	GD_CARD_TYPE_ZHADAN:  			//炸弹
				{
					outList =  PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiWuZha.class).getCard(tempCard, this);
				}
				break;
			case 	GD_CARD_TYPE_SING:
				{
					outList =  PKLogicFactory.getOpCard(DanZhangCardEx_GD.class).getCard(tempCard, this);
				}
				break;
			case GD_CARD_TYPE_NOMARL:
				{
					outList = this.getNomarlTypeCard(pos, tempCard);
				}
				break;
			case GD_CARD_TYPE_LIANDUI:
				{
					outList =  PKLogicFactory.getOpCard(LianDuiCardEx_GD.class).getCard(tempCard, this);
				}
				break;
			case GD_CARD_TYPE_SANSHUN:
				{
					outList =  PKLogicFactory.getOpCard(SanBuDaiFeiJiEx_GD.class).getCard(tempCard, this);
				}
				break;
			case GD_CARD_TYPE_SHUNZI:
				{
					outList = PKLogicFactory.getOpCard(ShunZiCardEx_GD.class).getCard(tempCard, this);
					break;
				}
			case GD_CARD_TYPE_SIWANGZHA:
				{
					outList = PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiTrump.class).getCard(tempCard, this);
					break;
				}
			case GD_CARD_TYPE_ZHADAN6:
				{
					outList = PKLogicFactory.getOpCard(ZhaDanCardEx_GD_LiuZha.class).getCard(tempCard, this);
					break;
				}
			case GD_CARD_TYPE_THREEDAIDUI:
				{
					outList = PKLogicFactory.getOpCard(SanDaiDuiZiCardEx_GD.class).getCard(tempCard, this);
					break;
				}
			case GD_CARD_TYPE_TONGHUASHUNZI:
				{
					outList = PKLogicFactory.getOpCard(ShunZiCardEx_TongHua_GD.class).getCard(tempCard, this);
					break;
				}
			default:
				CommLogD.error("not find opTYpe ="+m_opCardType +","+ GD_CARD_TYPE.valueOf(m_opCardType));
				break;
			}
			opCardType = m_opCardTypeBackUp;
			if (!checkZhaDan(GD_CARD_TYPE.valueOf(opCardType))) {
				if ( null == outList || outList.checkIsNull()) {
					
					
					if ( null == outList || outList.checkIsNull()) {
						ArrayList< PK_OpCard >opOutList =   PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiWuZha.class).getOpCardList(tempCard, this);
						if(opOutList.size() > 0){
							opCardType = GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN.value();
							outList = opOutList.get(0);
						}
					}
					
					if ( null == outList || outList.checkIsNull()) {
						ArrayList< PK_OpCard >opOutList =   PKLogicFactory.getOpCard(ShunZiCardEx_TongHua_GD.class).getOpCardList(tempCard, this);
						if(opOutList.size() > 0){
							opCardType = GD_CARD_TYPE.GD_CARD_TYPE_TONGHUASHUNZI.value();
							outList = opOutList.get(0);
						}
					}
					
					if ( null == outList || outList.checkIsNull()) {
						ArrayList< PK_OpCard >opOutList =   PKLogicFactory.getOpCard(ZhaDanCardEx_GD_LiuZha.class).getOpCardList(tempCard, this);
						if(opOutList.size() > 0){
							opCardType = GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN6.value();
							outList = opOutList.get(0);
						}
					}
					
					if ( null == outList || outList.checkIsNull()) {
						ArrayList< PK_OpCard >opOutList =   PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiTrump.class).getOpCardList(tempCard, this);
						if(opOutList.size() > 0){
							opCardType = GD_CARD_TYPE.GD_CARD_TYPE_SIWANGZHA.value();
							outList = opOutList.get(0);
						}
					}
				}
			}
			
			if (null == outList || outList.checkIsNull()) {
				opCardType = GD_CARD_TYPE.GD_CARD_TYPE_BUCHU.value();
			}
			boolean flag =  this.onOpCard(null, CGD_OpCard.make(this.room.getRoomID(), pos, opCardType, outList.cardList, outList.substituteCard,true));
			if (!flag) {
				CommLogD.error("aa 11  roomTrusteeship cardList"+m_cardList + ",m_opCardType="+m_opCardType+",opCardType="+opCardType+",outList="+outList.toString()+",roomPos.getPrivateCards()="+setPos.getPrivateCards().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommLogD.error("roomTrusteeship errMsg="+e.toString() + ",msg="+e.getMessage());
		}
	}
	
	public boolean onOpCard(WebSocketRequest request, CGD_OpCard opCard){
		if(opCard.pos != m_OpPos){
			if(null != request) request.error(ErrorCode.NotAllow, "onOpCard error: not current pos op oppos: "+m_OpPos);
			return false;
		}
		if (null != opCard.substituteCard && opCard.substituteCard.size() < opCard.cardList.size()) {
			opCard.substituteCard.addAll(Collections.nCopies(opCard.cardList.size()-opCard.substituteCard.size(), 0));
		}
		
		
		GD_CARD_TYPE type = GD_CARD_TYPE.valueOf(opCard.opType) ;
		
		if( GD_CARD_TYPE.GD_CARD_TYPE_BUCHU.equals(type)){
			if( GD_CARD_TYPE.GD_CARD_TYPE_NOMARL.value() == this.m_opCardType &&  GD_CARD_TYPE.GD_CARD_TYPE_BUCHU.equals(type)){
				if(null != request) request.error(ErrorCode.NotAllow, "onOpCard error:optype do not op,because you first op do not don`t out:");
				return false;
			}
		}else{
			if (/* !GD_CARD_TYPE.GD_CARD_TYPE_510K.equals(type) && */!checkZhaDan(type)) {
				if( GD_CARD_TYPE.GD_CARD_TYPE_NOMARL.value() != this.m_opCardType &&  this.m_opCardType != opCard.opType){
					if(null != request) request.error(ErrorCode.NotAllow, "onOpCard error:optype do not op,this last opType:"+ this.m_opCardType+",your optype:"+opCard.opType);
					return false;
				}
			}
			
			if(!this.checkIsMyCard(opCard.pos, opCard.cardList)){
				if(null != request) request.error(ErrorCode.NotAllow, "onOpCard error:card is not myself");
				return false;
			}
			
			if(!this.checkCardList(opCard)){
				if(null != request) request.error(ErrorCode.NotAllow, "onOpCard error:card check fail");
				return false;
			}
			
			if (!checkSubstituteCard(opCard.substituteCard)) {
				if(null != request) request.error(ErrorCode.NotAllow, "onOpCard laizi do not than 16");
				return false;
			}
		}
		
	
		GDSetPos setPos = (GDSetPos) set.getSetPosMgr().getSetPos(opCard.pos);
		if(opCard.cardList.size() > 0 && !setPos.deleteCard(opCard.cardList)){
			if(null != request) request.error(ErrorCode.NotAllow, "card delete error : your cards:"+opCard.cardList.toString() + ", posCard:" + setPos.getPrivateCards().toString());
			return false;
		}
		
		if(null != request) {
			request.response();
		}

		this.set.setFirstOp(false);
		
		setPos.setLastOpCard(opCard.opType, opCard.cardList, opCard.substituteCard);
		setPos.getRoomPos().setLatelyOutCardTime(0);
		if(GD_CARD_TYPE.GD_CARD_TYPE_BUCHU.equals(type)){
			this.addOpPos();
			this.checkEndSet();
			
			notify2AllOpCard(opCard);
			return true;
		}
		
		
		if(GD_CARD_TYPE.GD_CARD_TYPE_NOMARL.value() == this.m_opCardType){
			this.m_opCardType = opCard.opType;
		}
		GDRoom xyzbRoom = (GDRoom) this.room;
		if(checkZhaDan(type)){
			if(xyzbRoom.isFanBei6() && GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN6.equals(type)){
				zRoomSet.bombScore*=2;
			}
			if(GD_CARD_TYPE.GD_CARD_TYPE_SIWANGZHA.equals(type)){
				if(xyzbRoom.isFanBei4()){
					zRoomSet.bombScore*=4;
				}else if(xyzbRoom.isFanBei6()){
					zRoomSet.bombScore*=2;
				}
			}
			this.m_opCardType = opCard.opType;
		}
		
		
		setPos.addPblicCardList(opCard.cardList);

		
		m_lastOpPosBack = this.m_lastOpPos = opCard.pos;
		this.m_cardList = opCard.cardList;
		this.m_substituteCardList = opCard.substituteCard;
		
		
		if(setPos.getPrivateCards().size() <= 0){
			if(zRoomSet.m_finishOrderList.size() < this.room.getPlayerNum() - 1) 
				zRoomSet.addFinishOrder(opCard.pos);
		}
	
		this.addOpPos();
		this.checkEndSet();
		
		
		notify2AllOpCard(opCard);				
		return true;
	}
	
	/**
	 * 通知打牌消息
	 * */
	public void notify2AllOpCard(CGD_OpCard opCard) {
		GDSetPos setPos = (GDSetPos) set.getSetPosMgr().getSetPos(opCard.pos);
				
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			if (i == opCard.pos){
				this.set.getRoomPlayBack().playBack2Pos(i,
						SGD_OpCard.make(opCard.roomID, opCard.pos, opCard.opType, m_OpPos, opCard.cardList, m_bTurnEnd,  m_bSetEnd, 
								opCard.substituteCard, setPos.getSetPosInfo(true), getHuoBanCardList(i),opCard.isFlash),
						this.set.getSetPosMgr().getAllPlayBackNotify());
			}else{
				room.getRoomPosMgr().notify2Pos(i,
						SGD_OpCard.make(opCard.roomID, opCard.pos, opCard.opType, m_OpPos, opCard.cardList, m_bTurnEnd,  m_bSetEnd, 
					opCard.substituteCard,setPos.getSetPosInfo(false), getHuoBanCardList(i),opCard.isFlash));
			}
		}
	}
	
	/***
	 * 获取伙伴的牌
	 * */
	public ArrayList<Integer> getHuoBanCardList(int pos) {
		GDSetPos setPos = (GDSetPos) set.getSetPosMgr().getSetPos(pos);
		
		int otherPos = zRoomSet.getHuoBanPos(pos);
		GDSetPos otherSetPos = (GDSetPos) set.getSetPosMgr().getSetPos(otherPos);
		boolean isShowCardList = false;
		if (null != setPos && setPos.getPrivateCards().size() <= 0) {
			isShowCardList = true;
		}
		return otherSetPos.getNotifyCard(isShowCardList);
	}
	
	/**
	 * 检查是否结束
	 * **/
	public void checkEndSet(){
		if (getPlayerPlaying() == this.room.getPlayerNum()) {
			return;
		}
		if (zRoomSet.m_finishOrderList.size() <= 0) {
			return;
		}
		int iFirstPos  = (int) zRoomSet.getFinishOrder().get(0);
		boolean isRedFirstFinishOrder = zRoomSet.m_redPosList.contains(iFirstPos);
		
		int fourFinishOrder = -1;
		
		if (zRoomSet.m_finishOrderList.size() == this.room.getPlayerNum() - 1) {
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				if (!zRoomSet.m_finishOrderList.contains(i)) {
					fourFinishOrder = i;
				}
			}
			if (-1 != fourFinishOrder) {
				zRoomSet.m_finishOrderList.add(fourFinishOrder);
			}
		}

		
		 if (zRoomSet.m_finishOrderList.size() == 2) {
			boolean isRedSecondFinishOrder = zRoomSet.m_redPosList.contains( zRoomSet.m_finishOrderList.get(1));
			if (isRedFirstFinishOrder && isRedSecondFinishOrder) {
				this.m_bSetEnd = true;
				zRoomSet.setIsRedWin(true);
				zRoomSet.setWinFlag(GD_WINFLAG.GD_WINFLAG_WINWIN);
			} else if(!isRedFirstFinishOrder && !isRedSecondFinishOrder){
				this.m_bSetEnd = true;
				zRoomSet.setIsRedWin(false);
				zRoomSet.setWinFlag(GD_WINFLAG.GD_WINFLAG_WINWIN);
			}
			return;
		}
		
	
		
		if (getPlayerPlaying() <= 1) {
			this.m_bSetEnd = true;
			zRoomSet.setIsRedWin(isRedFirstFinishOrder);
		}
	}
	
	/**
	 * 牌验证 
	 * **/
	public boolean  checkCardList(CGD_OpCard opCard) {
		boolean flag = false;
		
		PK_OpCard pk_OpCard = set.getAccessToSpecific( new PK_OpCard(opCard.cardList, opCard.substituteCard));
		
		GD_CARD_TYPE cardType = GD_CARD_TYPE.valueOf(opCard.opType);
		GDRoom xyzbRoom = (GDRoom) this.room;
		switch (cardType) {
		case GD_CARD_TYPE_DUIZI:  			//对子
			{
				flag = PKLogicFactory.getOpCard(DuiZiCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_THREE:  		//3不带
			{
				flag = PKLogicFactory.getOpCard(SanBuDaiCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case 	GD_CARD_TYPE_ZHADAN:  			//炸弹
			{
				flag = PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiWuZha.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case 	GD_CARD_TYPE_SING:
			{
				flag = PKLogicFactory.getOpCard(DanZhangCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_LIANDUI:
			{
				flag = PKLogicFactory.getOpCard(LianDuiCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_SANSHUN:
			{
				flag = PKLogicFactory.getOpCard(SanBuDaiFeiJiEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_SHUNZI:
			{
				flag = PKLogicFactory.getOpCard(ShunZiCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_TONGHUASHUNZI:
			{
				flag = PKLogicFactory.getOpCard(ShunZiCardEx_TongHua_GD.class).checkCardAndCompare(new PK_OpCard(opCard.cardList, opCard.substituteCard), this);
			}
			break;
		case GD_CARD_TYPE_SIWANGZHA:
			{
				flag = PKLogicFactory.getOpCard(ZhaDanCardEx_GD_SiTrump.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_ZHADAN6:
			{
				flag = PKLogicFactory.getOpCard(ZhaDanCardEx_GD_LiuZha.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		case GD_CARD_TYPE_THREEDAIDUI:
			{
				flag = PKLogicFactory.getOpCard(SanDaiDuiZiCardEx_GD.class).checkCardAndCompare(pk_OpCard, this);
			}
			break;
		default:
			CommLogD.error("checkCardList No processing  cardType="+cardType);
			break;
		}
		return flag;
	}
	

	/**
	 * 根据当前出牌的位置获取下一个出牌的位置
	 * */
	public int  getNextOpPos(int pos) {
		int opPos = pos;
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			int numPos = (int) zRoomSet.m_opPosList.get(i);
			if (numPos == opPos) {
				return (int) zRoomSet.m_opPosList.get((i+1)%this.room.getPlayerNum());
			}
		}
		return opPos;
	}
	
	/**
	 * 操作位改变
	 * */
	public void addOpPos(){	
		for (int i = 0; i < this.room.getPlayerNum(); i++) {
			m_OpPos = this.getNextOpPos(m_OpPos);
			GDSetPos tempRoomPos = (GDSetPos) this.set.getSetPosMgr().getSetPos(m_OpPos);
			if(null !=tempRoomPos && tempRoomPos.getPrivateCards().size() > 0){
				break;
			}
		}
		this.set.startMS = CommTime.nowMS();
		this.set.setOpPos(m_OpPos);
		
		int tempLastOpPos = m_lastOpPos;
		GDSetPos setPos =  (GDSetPos) this.set.getSetPosMgr().getSetPos(m_lastOpPos);
	
		if(null != setPos && setPos.getPrivateCards().size() <= 0) {
			for (int i = 0; i < this.room.getPlayerNum(); i++) {
				m_lastOpPos = this.getNextOpPos(m_lastOpPos);
				GDSetPos tempSetPos = (GDSetPos) this.set.getSetPosMgr().getSetPos(m_lastOpPos);
				if(tempSetPos.getPrivateCards().size() > 0){
					break;
				}
			}
		}
		if(tempLastOpPos != m_lastOpPos) return;
		if(m_OpPos == m_lastOpPos){
			m_bTurnEnd = true;
			setPos =  (GDSetPos) this.set.getSetPosMgr().getSetPos(m_lastOpPosBack);
			if (null != setPos) {
				CommLogD.info("size ="+setPos.getPrivateCards().toString());
			}
			if(null != setPos && setPos.getPrivateCards().size() <= 0) {
				m_OpPos = zRoomSet.getHuoBanPos(m_lastOpPosBack);
				this.set.setOpPos(m_OpPos);
			}
		}
	}
	

	
	
	/**
	 * 首出 或新一轮首出
	 * @return 带牌数量
	 * **/
	@SuppressWarnings("unchecked")
	public PK_OpCard   getNomarlTypeCard (int pos , ArrayList<Integer> intList){
		if(intList.size() <= 0) return new PK_OpCard();
		ArrayList<Integer> outList = new ArrayList<>();
		ArrayList<Integer> tempInlist = (ArrayList<Integer>) intList.clone();
		tempInlist.removeAll(set.getRazzCardList());
		tempInlist.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		
		Map<Integer, List<Integer>> inCardMap =  set.getCardMap(tempInlist);
		
		
		if ((tempInlist.size() <= 0 && intList.size() > 0) || inCardMap.size() == 1) {
			outList.addAll(intList);
			setCardTypeBackUp (outList.size());
			return new PK_OpCard(outList);
		}

		
		ArrayList<Integer> keyList = new ArrayList<>(inCardMap.keySet());
		for (int i = 0; i < keyList.size(); i++) {
			outList.addAll(inCardMap.get(keyList.get(i)));
			if (outList.size() == 1) {
				m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_SING.value() ;
				break;
			} else if(outList.size() == 2){
				m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_DUIZI.value() ;
				List<Integer> tempList1 =  inCardMap.get(BasePockerLogic.getCardValue(outList.get(0)) + 1);
				List<Integer> tempList2 =  inCardMap.get(BasePockerLogic.getCardValue(outList.get(0)) + 2);
				if (null != tempList1 &&  tempList1.size() == outList.size() && null != tempList2 &&  tempList2.size() == outList.size()) {
					outList.addAll(tempList1);
					outList.addAll(tempList2);
					m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_LIANDUI.value() ;
				}
				break;
			}else if(outList.size() == 3){
				m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_THREE.value() ;
				List<Integer> tempList =  inCardMap.get(BasePockerLogic.getCardValue(outList.get(0)) + 1);
				if (null != tempList &&  tempList.size() == outList.size()) {
					outList.addAll(tempList);
					m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_SANSHUN.value() ;
				}
				break;
			}else{
				outList.clear();
			}
		}

		if (outList.size() <= 0 &&  inCardMap.size() > 0) {
			outList.addAll(inCardMap.get(keyList.get(0)));
			setCardTypeBackUp (outList.size());
		}
		return new PK_OpCard(outList);
	}
	
	/**
	 * 设置打牌类型
	 * */
	public void setCardTypeBackUp(int cardSize) {
		switch (cardSize) {
		case 1:
			m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_SING.value() ;
			break;
		case 2:
			m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_DUIZI.value() ;
			break;
		case 3:
			m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_THREE.value() ;
			break;
		default:
			m_opCardTypeBackUp =  GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN.value() ;
			break;
		}
	}
	
	
	/**
	 * 判断是否是炸弹以上
	 * */
	public boolean checkZhaDan(GD_CARD_TYPE type) {
		if (GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN.equals(type) || GD_CARD_TYPE.GD_CARD_TYPE_ZHADAN6.equals(type) ||
				GD_CARD_TYPE.GD_CARD_TYPE_SIWANGZHA.equals(type) || GD_CARD_TYPE.GD_CARD_TYPE_TONGHUASHUNZI.equals(type)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * 赖子不能代替大小王
	 * */
	public boolean checkSubstituteCard(ArrayList<Integer> substituteCard) {
		for (Integer integer : substituteCard) {
			if (BasePockerLogic.getCardValueEx(integer) >= 0x10) {
				return false;
			}
		}
		return  true;
	}
	
}
