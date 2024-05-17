package business.global.pk.gd.pkbase.optype.danzhangex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 单张
 * */
public class DanZhangCardEx implements OpCardEx {

	protected final int COUNT = 1;  //单张牌的数量
	
	@Override
	public boolean checkCard(PK_OpCard opCard,PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		if ((null != opCard.cardList && opCard.cardList.size() != COUNT) || null == opCard.cardList ) {
			return false;
		}
		
		return true;
	}
	

	
	
	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		if(null == setSound.getCardList() || (null != setSound.getCardList() && setSound.getCardList().size() <= 0))
			return checkCard(opCard, setSound);
		
		PK_OpCard tempOpCard = new PK_OpCard(opCard);
		PK_OpCard tempCompareOpCard = new PK_OpCard(setSound.getLastSpecificOpCard());
		
		if (!checkCard(tempOpCard, setSound) ) {
			return false;
		}
		if (!checkCard(tempCompareOpCard, setSound) ) {
			return false;
		}
		
		if (setSound.getSet().compareCard(opCard.cardList.get(0),tempCompareOpCard.cardList.get(0))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		ArrayList<PK_OpCard> outCardList = new ArrayList<>();
		cardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone();
		
		setSound.getSet().deleteRazzCard(valueList);
		
		
		Map<Integer, List<Integer>> valueMap = setSound.getSet().getCardMap(valueList);
		
		if (null == valueMap || (null != valueMap && valueMap.size() <= 0)) {
			return outCardList;
		}
		
		ArrayList<PK_OpCard> tempOutCardList = new ArrayList<>();
		for (Map.Entry<Integer, List<Integer>>  mEntry : valueMap.entrySet()) {
			int size  = mEntry.getValue().size();
			ArrayList<Integer> tempList = new ArrayList<>(mEntry.getValue());
			if (COUNT != size ) {
				//这里不考虑在没有获取到时拆分对子以上的
				if(COUNT <= size){
					tempOutCardList.add(new PK_OpCard(tempList.subList(0, COUNT)));
				}
				continue;
			}
			outCardList.add(new PK_OpCard(tempList));
		}
		outCardList.addAll(tempOutCardList);
		return outCardList;
	}

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		ArrayList<PK_OpCard> tempList = new ArrayList<>();
		cardList.removeAll(setSound.getRazzCardList());
		ArrayList<PK_OpCard> outList = getOpCardList(cardList, setSound);
		for (PK_OpCard opCard : outList) {
			tempList.add(opCard);
			if (tempList.size() == count) {
				break;
			}
		}
		return tempList;
	}




	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		PK_OpCard tempOpCard = new PK_OpCard();
		ArrayList<PK_OpCard> outList = getOpCardList(cardList, setSound);
		
		for (PK_OpCard opCard : outList) {
			if (checkCardAndCompare( setSound.getSet().getAccessToSpecific(opCard), setSound)) {
				tempOpCard = opCard;
				break;
			}
		}
		return tempOpCard;
	}
}
