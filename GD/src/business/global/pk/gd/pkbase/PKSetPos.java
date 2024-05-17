package business.global.pk.gd.pkbase;

import business.gd.c2s.cclass.PKRoomSet_Pos;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.util.ArrayList;
import java.util.List;

public abstract class PKSetPos {
	protected		PKRoomSet		roomSet;
	protected		PKRoomPos 		roomPos ; // 0-3->1-4号位
	protected 		ArrayList<Integer>  	privateCards = new ArrayList<>(); // 手牌
	protected		ArrayList<ArrayList<Integer>> m_publicCardList = new ArrayList<>();//已经打出的牌
	
	protected int m_lastOpCardType =0;  //操作类型及牌的类型
	protected ArrayList<Integer> m_lastOpCardList = new ArrayList<Integer>();
	protected ArrayList<Integer> m_lastSubstituteCardList = new ArrayList<>();//赖子代替牌数组
	
	
	protected 	int 				m_Point;	//单句得分
	
	public PKSetPos(PKRoomSet roomSet,PKRoomPos roomPos){ 
		this.roomSet = roomSet;
		this.roomPos=roomPos;
	}
	
	
	/**
	 * 验证是否是自己的牌
	 */
	public boolean checkIsMyCard(ArrayList<Integer> opCards) {
		for (Integer card : opCards) {
			if (!this.getPrivateCards().contains(card)) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<ArrayList<Integer>> getPublicCardList() {
		return m_publicCardList;
	}
	
	public void addPblicCardList(  ArrayList<Integer> publicCardList) {
		this.m_publicCardList.add(publicCardList);
	}
	
	/**
	 * 二维数组返回一维数组
	 * */
	public ArrayList<Integer> TwoArrayListToArray() {
		ArrayList<Integer> tempList = new ArrayList<>();
		for (ArrayList<Integer>  list: m_publicCardList) {
			tempList.addAll(list);
		}
		return tempList;
	}
	
	/**
	 * 获取位置
	 * */
	public int getPosID() {
		return roomPos.getPosID();
	}

	/**
	 * 获取位置
	 * */
	public long getPid() {
		return roomPos.getPid();
	}


	/**
	 * 获取某一张牌的位置
	 * */
	public int  getCardPos(int card) {
		return this.privateCards.indexOf(card);
	}
	
	
	/**
	 * 初始化手牌
	 * @param cards
	 */
	public void init(List<Integer> cards) {
		this.privateCards = new ArrayList<>(cards);
	}
	
	/**
	 * 初始化手牌
	 */
	public void addCard(Integer card) {
		this.privateCards.add(card);
	}
	

	/**
	 * 获取牌组信息
	 * @return
	 */
	public ArrayList<Integer> getNotifyCard(long pid, boolean isOpenCard) {
		boolean isSelf = pid == this.getPid();
		if (isOpenCard) {
			isSelf = true;
		}
		return getNotifyCard(isSelf);
	}
	
	public ArrayList<Integer> getNotifyCard(boolean isOpenCard) {
		boolean isSelf = isOpenCard;
		ArrayList<Integer> sArrayList = new ArrayList<Integer>();
		// 是自己
		int length = privateCards.size();
		for (int i = 0; i < length; i++) {
			sArrayList.add(isSelf ? privateCards.get(i) : 0x00);
		}
		return sArrayList;
	}
	
	
	/**
	 * 删除牌组信息
	 * @return
	 */
	public boolean deleteCard(ArrayList<Integer> cradList) {
		if(!checkHaveRepeat(cradList)) return false;
		for (Integer byte1 : cradList) {
			boolean flag =  this.privateCards.remove((Integer)byte1);
			if (!flag) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查重复的牌
	 * */
	@SuppressWarnings("unchecked")
	public boolean checkHaveRepeat(ArrayList<Integer> cradList) {
		ArrayList<Integer> tempList = (ArrayList<Integer>) cradList.clone();
		for(int i = 0; i < tempList.size() ;  ){
			Integer  temp = tempList.remove(i);
			if(!tempList.contains(temp)){
				tempList.add(i, temp);
				i++;
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取牌的位置
	 * @param card
	 * @return
	 */
	public boolean checkCard(Integer card){
		return BasePockerLogic.getCardCount(privateCards, card, false) > 0;
	}
	

	/**
	 * @return privateCards
	 */
	public ArrayList<Integer> getPrivateCards() {
		return privateCards;
	}


	public PKRoomSet getRoomSet() {
		return roomSet;
	}


	public PKRoomPos getRoomPos() {
		return roomPos;
	}

	/**
	 * 设置最后一次打牌的类型
	 * */
	public void  setLastOpCard(int opType, ArrayList<Integer> lastOpCardList, ArrayList<Integer> substituteCardList) {
		m_lastOpCardType = opType;
		m_lastOpCardList = lastOpCardList;
		m_lastSubstituteCardList = substituteCardList;
	}


	/**
	 * @return m_Point
	 */
	public int getPoint() {
		return m_Point;
	}


	/**
	 */
	public void setPoint(int point) {
		this.m_Point = point;
	}
	
	
	/**
	 * 获取某一个位置上的信息
	 * */
	public PKRoomSet_Pos getSetPosInfo(long pid) {
		return getSetPosInfo(pid == roomPos.getPid());
	}


	public abstract PKRoomSet_Pos getSetPosInfo(boolean isSelf);
}
