package business.global.pk.gd.pkbase.optype.zhadanex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 炸弹  没有处理赖子
 * */
public class ZhaDanCardEx_XYZB implements OpCardEx {

	protected final int COUNT = 4;  //三带的数量
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		if (null == cardList || (cardList != null && cardList.size() < 3)) {
			return false;
		}
				
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		
		ArrayList<Integer> trumpList = getTrumpList();
		ArrayList<Integer> haveTrumpCardList = setSound.getSet().deleteRazzCard(valueList, trumpList);
		
		if (valueList.size() <= 0 && haveTrumpCardList.size() >= 3) {
			return true;
		}
		
		if (cardList.size() < COUNT) {
			return false;
		}
		
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(valueList);
		
		if (valueMap.size() == 1) {
			return true;
		}else{
			int count = 0;
			for (Map.Entry<Integer, List<Integer>>  mEntry: valueMap.entrySet()) {
				if (BasePocker.getCardValueEx(mEntry.getValue().get(0)) < 0x10) {
					count++;
				}
			}
			
			if (count <= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		if(null == setSound || (null != setSound &&  null != setSound.getCardList() &&setSound.getCardList().size() <= 0))
			return checkCard(opCard, setSound);
		
		
		
		PK_OpCard tempOpCard = new PK_OpCard(opCard);
		PK_OpCard tempCompareOpCard = new PK_OpCard(setSound.getLastSpecificOpCard());
		
		if (!checkCard(tempOpCard, setSound) ) {
			return false;
		}
		
		if (!checkCard(tempCompareOpCard, setSound)) {
			return true;
		}
		
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) tempOpCard.cardList.clone();
		ArrayList<Integer> tempCompareCard = (ArrayList<Integer>) tempCompareOpCard.cardList.clone();
		
		ArrayList<Integer> trumpList = getTrumpList();
		ArrayList<Integer> haveTrumpCardList = setSound.getSet().deleteRazzCard(tempCardList, trumpList);
		ArrayList<Integer> compareCardHaveTrumpCardList = setSound.getSet().deleteRazzCard(tempCompareCard, trumpList);
		
		
		if (tempCardList.size() == 0 && tempCompareCard.size() == 0) {
			return haveTrumpCardList.size() > compareCardHaveTrumpCardList.size();
		} else if(tempCardList.size() == 0 && tempCompareCard.size() != 0){
			return haveTrumpCardList.size() > tempCompareCard.size()/2;
		}else if(tempCardList.size() != 0 && tempCompareCard.size() == 0){
			return tempCardList.size()/2 >= compareCardHaveTrumpCardList.size();
		}else{
			if (opCard.cardList.size() != setSound.getCardList().size()) {
				return opCard.cardList.size() > setSound.getCardList().size();
			} else {
				tempCardList.removeAll(setSound.getRazzCardList());
				tempCompareCard.removeAll(setSound.getRazzCardList());
				tempCardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
				tempCompareCard.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
				return setSound.getSet().compareCard(tempCardList.get(0), tempCompareCard.get(0));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		
		ArrayList<PK_OpCard> outCardList = new ArrayList<>();
		
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();	
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(tempCardList);
		for (Map.Entry<Integer, List<Integer>> mEntry : valueMap.entrySet()) {
			int size = mEntry.getValue().size();
			if (size >= COUNT) {
				outCardList.add(new PK_OpCard(mEntry.getValue()));
				for (int i = 1; i < haveRazzCardList.size(); i++) {
					ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue());
					ArrayList<Integer> substituteCard = new ArrayList<>(Collections.nCopies(size, 0));
					tempList.addAll(haveRazzCardList.subList(0, i));
					substituteCard.addAll(Collections.nCopies(i, BasePockerLogic.getCardValue(tempList.get(0))));
					outCardList.add(new PK_OpCard(tempList, substituteCard));
				}
			} else if(COUNT - size <= haveRazzCardList.size() ){
				for (int i = COUNT - size; i <= haveRazzCardList.size(); i++) {
					ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue());
					ArrayList<Integer> substituteCard = new ArrayList<>(Collections.nCopies(size, 0));
					tempList.addAll(haveRazzCardList.subList(0, i));
					substituteCard.addAll(Collections.nCopies(i, BasePockerLogic.getCardValue(tempList.get(0))));
					outCardList.add(new PK_OpCard(tempList, substituteCard));
				}
			}
		}
		ArrayList<Integer> tempCardTrumpList = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> trumpList = getTrumpList();
		ArrayList<Integer> haveTrumpCardList = setSound.getSet().deleteRazzCard(tempCardTrumpList, trumpList);
		
		for (int i = 3; i < haveTrumpCardList.size(); i++) {
			outCardList.add(new PK_OpCard(haveTrumpCardList.subList(0, i)));
		}
		return outCardList;
	}

	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		
		PK_OpCard opCard = new PK_OpCard();
		ArrayList<PK_OpCard> outList = getOpCardList(cardList, setSound);
		for (PK_OpCard tempOpCard : outList) {
			if (checkCardAndCompare(setSound.getSet().getAccessToSpecific(tempOpCard) , setSound)) {
				opCard = tempOpCard;
				break;
			}
		}
		return opCard;
	}
	
	/**
	 * 获取王
	 * */
	public ArrayList<Integer>  getTrumpList() {
		ArrayList<Integer> trumpList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			for (Integer cardValue : BasePocker.Trump_PockerList) {
				trumpList.add(cardValue.intValue() + (BasePocker.LOGIC_MASK_COLOR_MOD)*i);
			}
		}
		return trumpList;
	}

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}
}
