package business.global.pk.gd.pkbase.optype.shunziex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 顺子
 * */
public class ShunZiCardEx_GD_NotZhangShu implements OpCardEx {

	protected final int COUNT = 5;  //数量 
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		if ((null != opCard && opCard.cardList.size() <= 0) || null == opCard ) {
			return false;
		}
	
		
		ArrayList<Integer> valueList = (ArrayList<Integer>) opCard.cardList.clone(); 
		ArrayList<Integer> haveRazzCardList = new ArrayList<>();
		if(opCard.getSubStituteSize() > 0)
			haveRazzCardList = setSound.getSet().deleteRazzCard(valueList);
		
		valueList.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
		int cardValue = BasePocker.getCardValueEx(valueList.get(0)) ;
		int count = opCard.cardList.size();
		
		for (int i = 1; i < valueList.size() ; i++) {
			if (cardValue == 0x0E && BasePocker.getCardValueEx(valueList.get(i)) == count) {
				cardValue = count;
			}
			else if (Math.abs(cardValue - BasePocker.getCardValueEx(valueList.get(i))) != 1) {
				if (haveRazzCardList.size() <= 0) {
					return false;
				}else if(haveRazzCardList.size() > 0){
					haveRazzCardList.remove(0);
					cardValue--;
					i--;
				}
			}else{
				cardValue = BasePocker.getCardValueEx(valueList.get(i));
			}
		}
		
		return true;
	}
	
	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		ArrayList<Integer> razzCardList = setSound.getRazzCardList();
		if(null == setSound || (null != setSound && setSound.checkLastOpCardIsNull()))
			return checkCard(opCard, setSound);
		
		PK_OpCard tempOpCard = new PK_OpCard(opCard);
		PK_OpCard tempCompareOpCard = new PK_OpCard(setSound.getLastSpecificOpCard());
		

		
		if (!checkCard(tempOpCard, setSound) ) {
			return false;
		}
		if (!checkCard(tempCompareOpCard, setSound)) {
			return false;
		}
		
		if (tempOpCard.cardList.size() != tempCompareOpCard.cardList.size()) {
			return false;
		}
		
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) setSound.getSet().getAccessToSpecific(tempOpCard).cardList.clone();
		ArrayList<Integer> tempCompareCard = (ArrayList<Integer>)setSound.getSet().getAccessToSpecific(tempCompareOpCard).cardList.clone();
		
		tempCardList.removeAll(razzCardList);
		tempCompareCard.removeAll(razzCardList);
		
		tempCardList.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
		tempCompareCard.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
		

		int leftCard = BasePockerLogic.getCardValue(tempCardList.get(0));
		int rightCard = BasePockerLogic.getCardValue(tempCompareCard.get(0));
		
		if (BasePockerLogic.getCardValue(leftCard) == 0x0E && BasePocker.getCardValueEx(tempCardList.get(1)) == tempCardList.size()) {
			leftCard = 1;
		}
		if (BasePockerLogic.getCardValue(rightCard) == 0x0E && BasePocker.getCardValueEx(tempCompareCard.get(1)) == tempCompareCard.size()) {
			rightCard =1;
		}
		
		
		if (BasePockerLogic.compareCard(leftCard,rightCard)) {
			return true;
		}

				
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();
		
		
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		Map<Integer, List<Integer>> collectorsMap =  setSound.getSet().getCardMap(tempCardList);
		ArrayList<Integer> keySets =  new ArrayList<>( collectorsMap.keySet() );
		ArrayList< ArrayList<Integer> > tempOutList = getShunZi(keySets, haveRazzCardList, count, setSound);
		ArrayList< ArrayList<Integer> > outList = new ArrayList<>();
		
		for (ArrayList<Integer> arrayList : tempOutList) {
			ArrayList< ArrayList<Integer> > tempShunZilist = new ArrayList<>();
			tempShunZilist.add((ArrayList<Integer>) arrayList.clone());
			for (int i = 0; i < arrayList.size(); i++) {
				int card = arrayList.get(i);
				List<Integer> list = collectorsMap.get(card);
				int num = tempShunZilist.size();
				if (!haveRazzCardList.contains(card) && (null != list && list.size() >= 1)) {
					for(int k = 0; k < num; k++){
						ArrayList<Integer> tmp = (ArrayList<Integer>) tempShunZilist.get(k).clone();
						for(int h = 0; h < list.size(); h++){
							if(list.get(h) == card || list.get(h) == null){
								continue;
							}
							tmp.set(i, list.get(h));
							tempShunZilist.add((ArrayList<Integer>) tmp.clone());
						}
					}
				}
			}
			for (ArrayList<Integer> arrayList2 : tempShunZilist) {
				if (cardList.containsAll(arrayList2) && checkCard(new  PK_OpCard(arrayList2), setSound)) {
					outList.add(arrayList2);
				}
			}
//			outList.addAll(tempShunZilist);
		}
		BasePockerLogic.rechecking(outList);
		
		return setSound.getSet().insteadOfRazzCard(outList, haveRazzCardList);
	}

	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		cardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		ArrayList<PK_OpCard> tempList = getCard(cardList, setSound.getCardList().size(), setSound);
		for (PK_OpCard tempOpCard : tempList) {
			if(checkCardAndCompare(setSound.getSet().getAccessToSpecific(tempOpCard) , setSound)){
				return tempOpCard;
			}
		}
		return new PK_OpCard();
	}
	
	/**
	 * 根据传入的数组  获取顺子
	 * */
	public ArrayList<ArrayList<Integer>> getShunZi(ArrayList<Integer> inCardList, ArrayList<Integer> haveRazzCardList, int Count,  PKRoomSetSound setSound) {
		ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
		int razzCardCount = haveRazzCardList.size();
		
		
		for(int i = 0 ; i <= inCardList.size() - Count; i++){
			ArrayList<Integer> temp = new ArrayList<Integer>(Count);
			Integer value = inCardList.get(i);
			temp.add(value);
			for(int j = 1; j < Count; j++){
				int card = inCardList.get(i+j);
				if (card > 0x0E) {
					continue;
				}
				if(Math.abs(card - inCardList.get(i+j-1)) != 1  ){
					if(razzCardCount > 0){
						razzCardCount--;
					}else
						break;
				}
				temp.add(card);
			}
			if(temp.size() != Count){
				ArrayList<Integer> razzCardList = setSound.getSet().deleteRazzCard(temp, haveRazzCardList);
				razzCardCount += razzCardList.size();
				continue;
			}
			outList.add(temp);
		}
		
		return outList;
	}

	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}	
}
