package business.global.pk.gd.pkbase.optype.sanzhangex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import business.global.pk.gd.pkbase.abs.PKLogicFactory;
import business.global.pk.gd.pkbase.optype.duiziex.DuiZiCardEx_GD;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 三带
 * */
public class SanDaiDuiZiCardEx_GD implements OpCardEx {

	protected final int COUNT = 3;  //三带的数量
	protected final int DAIPAI = 2;	//带牌数
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		if ((null != opCard.cardList && opCard.cardList.size() != COUNT + DAIPAI) || null == opCard.cardList ) {
			return false;
		}
		
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) opCard.cardList.clone();
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		if (haveRazzCardList.size() <= 0 ) {
			ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
			BasePockerLogic.getSameCardByTypeEx(outList, opCard.cardList, BasePocker.PockerValueType.POCKER_VALUE_TYPE_THREE);
			
			if (null == outList || (null != outList && outList.size() <= 0)) {
				return false;
			}
			return true;
		}else if(haveRazzCardList.size() == 1 ){
			ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
			BasePockerLogic.getSameCardByTypeEx(outList, opCard.cardList, BasePocker.PockerValueType.POCKER_VALUE_TYPE_SUB);
			
			if (null == outList || (null != outList && outList.size() <= 0)) {
				return false;
			}
			
			for (Integer integer : haveRazzCardList) {
				int index = opCard.cardList.indexOf(integer);
				int substituteCard = opCard.substituteCard.get(index);
				if (0 < substituteCard &&  substituteCard != BasePockerLogic.getCardValue(outList.get(0).get(0)) ) {
					return false;
				}
			}
			return true;
		}else if(haveRazzCardList.size() == 2){
			return true;
		}else if(haveRazzCardList.size() == 3){
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		if(null == setSound || (null != setSound && setSound.checkLastOpCardIsNull() ))
			return checkCard(opCard, setSound);
		
		
		PK_OpCard tempOpCard = new PK_OpCard(opCard);
		PK_OpCard tempCompareOpCard = new PK_OpCard(setSound.getLastSpecificOpCard());
		
		if (!checkCard(tempOpCard, setSound) ) {
			return false;
		}
		if (!checkCard(tempCompareOpCard, setSound)) {
			return false;
		}
		PK_OpCard tempOpCard2 = getThreeCard(tempOpCard, setSound);
		PK_OpCard tempCompareOpCard2 =  getThreeCard(tempCompareOpCard, setSound);
		

		
		int leftCard = !tempOpCard2.cardList.isEmpty() ? tempOpCard2.cardList.get(0) : 0;
		int rightCard = !tempCompareOpCard2.cardList.isEmpty() ? tempCompareOpCard2.cardList.get(0) : 0;
		
		if (setSound.getSet().compareCard(leftCard,rightCard)) {
			return true;
		}
				
		return false;
	}

	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		ArrayList<PK_OpCard> outCardList = getOpCardList(cardList, setSound);
		for (PK_OpCard tmepOpCard : outCardList) {
			if (checkCardAndCompare(setSound.getSet().getAccessToSpecific(tmepOpCard) , setSound)) {
				return tmepOpCard;
			}
		}
		return new PK_OpCard();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		ArrayList<PK_OpCard> outCardList = new ArrayList<>();
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		valueList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		
		
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(valueList);
		
		
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(valueList);
		
		ArrayList<PK_OpCard> tempOutCardList = new ArrayList<>();
		for (Map.Entry<Integer, List<Integer>>  mEntry : valueMap.entrySet()) {
			ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue());
			int size  = tempList.size();
			PK_OpCard tempOpCard = new PK_OpCard(tempList);
			if (COUNT != size ) {
				if (COUNT > size && COUNT - size <= haveRazzCardList.size()) {
					tempOpCard.cardList.addAll(haveRazzCardList.subList(0,COUNT - size));
					if(BasePocker.getCardValueEx(tempList.get(0)) != setSound.getRazzCardValue()){
						tempOpCard.addSameObject(size,BasePocker.getCardValueEx(tempList.get(0)), COUNT - size);
					}else{
						tempOpCard.addSameObject(size,0, COUNT - size);
					}
					getDaiPai(tempOpCard, valueList, setSound);
					if(tempOpCard.cardList.size() == COUNT + DAIPAI)
						tempOutCardList.add(tempOpCard);
				}
				//这里考虑在没有获取到对子时拆分三张以上的
				else if(COUNT <= size){
					PK_OpCard opCard2 = new PK_OpCard(tempList.subList(0, COUNT));
					getDaiPai(tempOpCard, valueList, setSound);
					if(tempList.size() == COUNT + DAIPAI)
						tempOutCardList.add(opCard2);
				}
				continue;
			}
			getDaiPai(tempOpCard, valueList, setSound);
			if(tempOpCard.cardList.size() == COUNT + DAIPAI)
				outCardList.add(tempOpCard);
		}
		outCardList.addAll(tempOutCardList);
		return outCardList;
	}

	/**
	 * 获取带牌
	 * */
	@SuppressWarnings("unchecked")
	public void getDaiPai(PK_OpCard opCard , ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		ArrayList<Integer> tempList = (ArrayList<Integer>) cardList.clone();
		tempList.removeAll(opCard.cardList);
		ArrayList<PK_OpCard> daiPaiList =  PKLogicFactory.getOpCard(DuiZiCardEx_GD.class).getCard(tempList, DAIPAI, setSound);
		if (null != daiPaiList && daiPaiList.size() == DAIPAI) {
			if (opCard.substituteCard.size() != opCard.cardList.size()) {
				opCard.substituteCard.addAll(Collections.nCopies(opCard.cardList.size() - opCard.substituteCard.size(), 0));
			}
			for (PK_OpCard pk_OpCard : daiPaiList) {
				opCard.cardList.addAll(pk_OpCard.cardList);
				if (pk_OpCard.substituteCard.size() <= 0) {
					opCard.substituteCard.addAll(Collections.nCopies(pk_OpCard.cardList.size() , 0));
				}else{
					opCard.substituteCard.addAll(pk_OpCard.substituteCard);
				}
			}
		}
	}

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}
	
	public PK_OpCard getThreeCard(PK_OpCard opCard, PKRoomSetSound setSound){
		PK_OpCard opCard2 = setSound.getSet().getAccessToSpecific(opCard);
		ArrayList<PK_OpCard> list =  PKLogicFactory.getOpCard(SanBuDaiCardEx_GD.class).getOpCardList(opCard2.cardList, setSound);
		if (null != list && list.size() > 0) {
			for (PK_OpCard pk_OpCard : list) {
				for (int i = 0; i < pk_OpCard.substituteCard.size(); i++) {
					if (BasePockerLogic.getCardValueEx(pk_OpCard.cardList.get(i)) == setSound.getRazzCardValue()) {
						pk_OpCard.substituteCard.set(i, 0);
					}
				}
				ArrayList<Integer> cardList = setSound.getSet().getAccessToSpecific(pk_OpCard).cardList;
				if (opCard2.cardList.containsAll(cardList) && PKLogicFactory.getOpCard(SanBuDaiCardEx_GD.class).checkCard(pk_OpCard, setSound)) {
					return pk_OpCard;
				}
			}
			return list.get(0);
		}
		return new PK_OpCard();
	}
}
