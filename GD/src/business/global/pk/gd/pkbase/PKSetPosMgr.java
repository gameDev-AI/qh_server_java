package business.global.pk.gd.pkbase;

import business.gd.c2s.cclass.PKRoomSet_Pos;

import java.util.ArrayList;
import java.util.List;

public class PKSetPosMgr {

	protected PKRoomSet  roomSet;
	protected ArrayList<PKSetPos> posList = new ArrayList<>();

	
	public PKSetPosMgr(PKRoomSet  roomSet) {
		this.roomSet = roomSet;
	}
	
	
	
	/**
	 * 获取牌的位置
	 * @param card
	 * @return
	 */
	public int checkCard(Integer card){
		int pos = -1;
		for (PKSetPos setPos: posList) {
			if(setPos.checkCard(card)){
				pos = setPos.getPosID();
				break;
			}
		}
		return pos;
	}
	
	/**
	 * 获取所有玩家的牌
	 */
	@SuppressWarnings("unchecked")
	public  ArrayList<ArrayList<Integer>> getAllPlayBackNotify(){
		ArrayList<ArrayList<Integer>> cardList = new ArrayList<ArrayList<Integer>>();
		for (PKSetPos setPos: posList) {
			cardList.add((ArrayList<Integer>) setPos.getPrivateCards().clone());
		}
		return cardList;
	}
	
	public  PKSetPos getSetPos(int index) {
		PKSetPos posRet = null;
		for (int i = 0,size = this.posList.size();i<size;i++) {
			PKSetPos pos = this.posList.get(i);
			if (pos.getPosID() == index) {
				posRet = pos;
				break;
			}
		}
		return posRet;
	}
	
	public  PKSetPos getSetPosByPid(long pid) {
		PKSetPos posRet = null;
		for (int i = 0; i < this.posList.size(); i++) {
			PKSetPos pos = posList.get(i);
			if (pos.getPid() == pid) {
				posRet = pos;
				break;
			}
		}
		return posRet;
	}



	/**
	 * @return posList
	 */
	public ArrayList<PKSetPos> getPosList() {
		return posList;
	}
	
	
	/**
	 * 获取得分列表
	 * */
	public ArrayList<Integer> getPointList() {
		ArrayList<Integer> pointList = new ArrayList<>();
		for (PKSetPos setPos: posList) {
			pointList.add(setPos.getPoint());
		}
		return pointList;
	}
	
	
	/**
	 * 获取玩家posinfo
	 * */
	public List<PKRoomSet_Pos> getPosInfo(long pid) {
		List<PKRoomSet_Pos> posInfo = new ArrayList<>(); // 一局玩家列表
		for (PKSetPos setPos: posList) {
			posInfo.add(setPos.getSetPosInfo(pid));
		}
		return  posInfo;
	}
	
	public List<PKRoomSet_Pos> getPosInfo(Boolean isSelf) {
		List<PKRoomSet_Pos> posInfo = new ArrayList<>(); // 一局玩家列表
		for (PKSetPos setPos: posList) {
			posInfo.add(setPos.getSetPosInfo(isSelf));
		}
		return  posInfo;
	}
}
