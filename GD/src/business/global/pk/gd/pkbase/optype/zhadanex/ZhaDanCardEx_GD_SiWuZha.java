package business.global.pk.gd.pkbase.optype.zhadanex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 炸弹  没有处理赖子
 * */
public class ZhaDanCardEx_GD_SiWuZha implements OpCardEx {

	protected final int MINCOUNT = 4;  //数量
	protected final int MAXCOUNT = 5;  //数量
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		if (null == cardList || (cardList != null && cardList.size() < MINCOUNT) || (cardList != null && cardList.size() > MAXCOUNT)) {
			return false;
		}
				
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		
		setSound.getSet().deleteRazzCard(valueList);
		
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(valueList);
		
		if (valueMap.size() <= 1) {
			return true;
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

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		
		ArrayList<PK_OpCard> outCardList = new ArrayList<>();
		
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();	
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(tempCardList);
		for (Map.Entry<Integer, List<Integer>> mEntry : valueMap.entrySet()) {
			int size = mEntry.getValue().size();
			if (size >= MINCOUNT) {
				for (int i = MINCOUNT; i < MAXCOUNT; i++) {
					if(i  <= size)
						outCardList.add(new PK_OpCard(mEntry.getValue().subList(0, i)));
				}
				
				for (int i = 1; i < haveRazzCardList.size(); i++) {
					for (int j = MINCOUNT - i; j < MAXCOUNT; j++) {
						if(j  <= size){
							ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue().subList(0, j));
							ArrayList<Integer> substituteCard = new ArrayList<>(Collections.nCopies(j, 0));
							tempList.addAll(haveRazzCardList.subList(0, i));
							if (BasePockerLogic.getCardValue(tempList.get(0)) != setSound.getRazzCardValue()) {
								substituteCard.addAll(Collections.nCopies(i, BasePockerLogic.getCardValue(tempList.get(0))));
							} else {
								substituteCard.addAll(Collections.nCopies(i, 0));
							}
							outCardList.add(new PK_OpCard(tempList, substituteCard));
						}
					}
				}
			} else if(MINCOUNT - size <= haveRazzCardList.size() ){
				for (int i = MINCOUNT - size; i <= haveRazzCardList.size() && i + size <= MAXCOUNT; i++) {
					ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue());
					ArrayList<Integer> substituteCard = new ArrayList<>(Collections.nCopies(size, 0));
					tempList.addAll(haveRazzCardList.subList(0, i));
					if (BasePockerLogic.getCardValue(tempList.get(0)) != setSound.getRazzCardValue()) {
						substituteCard.addAll(Collections.nCopies(i, BasePockerLogic.getCardValue(tempList.get(0))));
					} else {
						substituteCard.addAll(Collections.nCopies(i, 0));
					}
					outCardList.add(new PK_OpCard(tempList, substituteCard));
				}
			}
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

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}
}
