package business.global.pk.gd.pkbase.optype.sanzhangex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 三带
 * */
public class SanBuDaiCardEx implements OpCardEx {

	protected final int COUNT = 3;  //三带的数量
	protected final int DAIPAI = 0;	//带牌数
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		if ((null != opCard.cardList && opCard.cardList.size() != COUNT + DAIPAI) || null == opCard.cardList ) {
			return false;
		}
		
		int card = -1;
		int razzCardCount = 0;
		for (Integer byte1 : opCard.cardList) {
			if (!setSound.getSet().checkIsRazzCard(byte1)) {
				if (-1 ==  card) {
					card = BasePocker.getCardValueEx(byte1);
				}else if( -1 !=  card && card != BasePocker.getCardValueEx(byte1)){
					return false;
				}
			}else{
				razzCardCount++;
			} 
		}
		if (razzCardCount == COUNT) {
			return false;
		}
		if (null !=  opCard.substituteCard && opCard.substituteCard.size() > 0) {
			ArrayList<Integer> valueList = (ArrayList<Integer>) opCard.cardList.clone();
			ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(valueList);
			for (Integer integer : haveRazzCardList) {
				int index = opCard.cardList.indexOf(integer);
				int substituteCard = opCard.substituteCard.get(index);
				if (0 < substituteCard &&  substituteCard != card && -1 != card) {
					return false;
				}
			}
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
		
		if (setSound.getSet().compareCard(setSound.getSet().getSmallCard(tempOpCard.cardList), setSound.getSet().getSmallCard(tempCompareOpCard.cardList))) {
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
			ArrayList<Integer> tempList = (ArrayList<Integer>) mEntry.getValue();
			int size  = tempList.size();
			
			PK_OpCard tempOpCard = new PK_OpCard(mEntry.getValue());
			if (COUNT != size ) {
				if (COUNT > size && COUNT - size <= haveRazzCardList.size()) {
					tempList.addAll(haveRazzCardList.subList(0, COUNT - size));
					tempOpCard.addSameObject(size,BasePocker.getCardValueEx(tempList.get(0)), COUNT - size);
					tempOutCardList.add(tempOpCard);
				}
				//这里考虑在没有获取到对子时拆分三张以上的
				else if(COUNT <= size){
					tempOutCardList.add(new PK_OpCard(tempList.subList(0, COUNT)));
				}
				continue;
			}
			outCardList.add(tempOpCard);
		}
		outCardList.addAll(tempOutCardList);
		return outCardList;
	}

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}
}
