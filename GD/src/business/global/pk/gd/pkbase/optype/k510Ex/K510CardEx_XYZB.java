package business.global.pk.gd.pkbase.optype.k510Ex;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import business.global.pk.gd.pkbase.abs.PKLogicFactory;
import business.global.pk.gd.pkbase.optype.zhadanex.ZhaDanCardEx_XYZB;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 顺子
 * */
public class K510CardEx_XYZB implements OpCardEx {

	public static final int COUNT = 3; 
	public static final ArrayList<Integer> scoreList = new ArrayList<>(Arrays.asList(0x05, 0x0A, 0x0D));
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		if ((null != opCard && cardList.size() != COUNT) || null == opCard ) {
			return false;
		}
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(valueList);
		if (haveRazzCardList.size() == cardList.size()) {
			return false;
		}
		if (haveRazzCardList.size() > 0) {
			return false;
		}
		
		Map<Integer, List<Integer>> valueMap =  valueList.stream().filter(i->scoreList.contains(BasePocker.getCardValueEx(i))).collect(Collectors.groupingBy(p->BasePocker.getCardValueEx(p)));
		
		if (valueMap.size() + haveRazzCardList.size() < COUNT) {
			return false;
		}
		
		for (Map.Entry<Integer, List<Integer>> mEntry : valueMap.entrySet()) {
			if (!scoreList.contains(BasePocker.getCardValueEx(mEntry.getKey()))) {
				return false;
			}else {
				if (mEntry.getValue().size() != 1) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		
		
		if (!checkCard(opCard, setSound)) {
			return false;
		}
		
		if (checkCard(setSound.getLastSpecificOpCard(), setSound)) {
			return false;
		} else {
			if (PKLogicFactory.getOpCard(ZhaDanCardEx_XYZB.class).checkCard(setSound.getLastSpecificOpCard(), setSound)) {
				return false;
			} else {
				return true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList,  PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();
		
		
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		
		Map<Integer, List<Integer>> collectorsMap =  tempCardList.stream().filter(i->scoreList.contains(BasePocker.getCardValueEx(i))).collect(Collectors.groupingBy(p->BasePocker.getCardValueEx(p)));
		
		if (collectorsMap.size() <= 0) {
			return new ArrayList<>();
		}
		ArrayList<Integer> shunZiList = new ArrayList<>();
		for (Integer integer : scoreList) {
			List<Integer> list = collectorsMap.get(BasePocker.getCardValueEx( integer));
//			ArrayList<Integer> tempList = new ArrayList<>( collectorsMap.get(BasePockerLogic.getCardValue( integer)));
			if (null == list || (null != list && list.size() <= 0)) {
//				if(haveRazzCardList.size() > 0)
//					shunZiList.add(haveRazzCardList.get(0));
			}else {
				shunZiList.add(list.get(0));
			}
		}
		if (collectorsMap.size() < scoreList.size()) {
			return new ArrayList<>();
		}
		
		ArrayList< ArrayList<Integer> > tempShunZilist = new ArrayList<>();
		tempShunZilist.add((ArrayList<Integer>) shunZiList.clone());
		for (int i = 0; i < shunZiList.size() ; i++) {
			ArrayList<Integer> tempList = new ArrayList<>( collectorsMap.get(BasePocker.getCardValueEx( shunZiList.get(i))));
			if (null != tempList && tempList.size() > 0) {
				for (Integer integer : tempList) {
					shunZiList.set(i, integer);
					tempShunZilist.add((ArrayList<Integer>) shunZiList.clone());
				}
			}
		}
		BasePockerLogic.rechecking(tempShunZilist);
		return setSound.getSet().insteadOfRazzCard(tempShunZilist, haveRazzCardList);
	}

	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		ArrayList<PK_OpCard> opCards = getOpCardList(cardList, setSound);
		for (PK_OpCard pk_OpCard : opCards) {
			if (checkCardAndCompare(pk_OpCard, setSound)) {
				return pk_OpCard;
			}
		}
		return new PK_OpCard();
	}

	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		return null;
	}

}
