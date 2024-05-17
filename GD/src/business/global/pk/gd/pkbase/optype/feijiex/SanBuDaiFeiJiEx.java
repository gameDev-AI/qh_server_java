package business.global.pk.gd.pkbase.optype.feijiex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.ddm.server.common.CommLogD;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import business.global.pk.gd.pkbase.abs.PKLogicFactory;
import business.global.pk.gd.pkbase.optype.sanzhangex.SanBuDaiCardEx;
import business.global.pk.gd.pkbase.optype.shunziex.ShunZiCardEx;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 两连队
 * */
public class SanBuDaiFeiJiEx implements OpCardEx {
	public static final int SAMENUM = 3;
	public static final int DAINUM = 0;
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		// TODO 自动生成的方法存根
		if(null == opCard) return false;
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		int size = cardList.size();
		if(size%SAMENUM != 0) return false;
		int count = size/SAMENUM;
		
		
		
		PK_OpCard tempOpCard = getLianDuiShunZi(cardList, setSound);
		if (tempOpCard == null || (null != tempOpCard && tempOpCard.checkIsNull())) {
			return false;
		}
		
		if (tempOpCard.cardList.size() != count) {
			return false;
		}
		
		if(!PKLogicFactory.getOpCard(ShunZiCardEx.class).checkCard(tempOpCard, setSound))
			return false;
		
		return true;
	}

	@Override
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound) {
		if(null == setSound || (null != setSound &&  null != setSound.getCardList() &&setSound.getCardList().size() <= 0))
			return checkCard(opCard, setSound);
		
		PK_OpCard tempOpCard = new PK_OpCard(opCard);
		PK_OpCard tempCompareOpCard = new PK_OpCard(setSound.getLastSpecificOpCard());
		
		
		if (!checkCard(tempOpCard, setSound)) {
			return false;
		}
		
		if (!checkCard(tempCompareOpCard, setSound)) {
			return false;
		}
		
		PK_OpCard shunOpCard = getLianDuiShunZi(tempOpCard.cardList, setSound);	
		if (shunOpCard == null || (null != shunOpCard && shunOpCard.checkIsNull()) || !PKLogicFactory.getOpCard(ShunZiCardEx.class).checkCard(shunOpCard, setSound)) {
			return false;
		}
		
		PK_OpCard compareShunOpCard = getLianDuiShunZi(tempCompareOpCard.cardList, setSound);	
		if (compareShunOpCard == null || (null != compareShunOpCard && compareShunOpCard.checkIsNull()) || !PKLogicFactory.getOpCard(ShunZiCardEx.class).checkCard(compareShunOpCard, setSound)) {
			return false;
		}
		shunOpCard.cardList.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
		compareShunOpCard.cardList.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
		return setSound.getSet().compareCard(shunOpCard.cardList.get(0),compareShunOpCard.cardList.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound) {
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();
		
		
		ArrayList<Integer> haveRazzCardList = setSound.getSet().deleteRazzCard(tempCardList);
		Map<Integer, List<Integer>> collectorsMap =  setSound.getSet().getCardMap(tempCardList);
		ArrayList<Integer> keySets =  new ArrayList<>( collectorsMap.keySet() );
		ArrayList< ArrayList<Integer> > tempOutList = getShunZi(keySets, haveRazzCardList, count,setSound);
		CommLogD.info("getCard tempOutList="+tempOutList.toString());
		ArrayList< PK_OpCard > outList = new ArrayList<>();
		
		
		for (ArrayList<Integer> arrayList : tempOutList) {
			ArrayList<Integer> templist = new ArrayList<>();
			ArrayList<Integer> substituteCardList = new ArrayList<>();
			for (Integer integer : arrayList) {
				if (!haveRazzCardList.contains(integer)) {
					ArrayList<Integer> haveCardList = (ArrayList<Integer>) collectorsMap.get(integer);
					if (haveCardList.size() >= SAMENUM) {
						templist.addAll(haveCardList.subList(0, SAMENUM));
						substituteCardList.addAll(Collections.nCopies(SAMENUM, 0));
					} else {
						if(haveRazzCardList.size() >= SAMENUM - haveCardList.size()){
							templist.addAll(haveCardList);
							substituteCardList.addAll(Collections.nCopies(haveCardList.size(), 0));
							templist.addAll(haveRazzCardList.subList(0, SAMENUM - haveCardList.size()));
							substituteCardList.addAll(Collections.nCopies(haveCardList.size(), BasePockerLogic.getCardValue(integer)));
						}
					}
					
				}else{
					if (haveRazzCardList.size() >= SAMENUM) {
						templist.addAll(haveRazzCardList.subList(0, SAMENUM ));
						substituteCardList.addAll(Collections.nCopies(SAMENUM,setSound.getSet().getRazzCardValue(arrayList, integer, setSound.getRazzCardList())));
					}
				}
			}
			
			if(templist.size() == count * SAMENUM)
				outList.add(new PK_OpCard(templist, substituteCardList));
		}
		
		
		return outList;
	}

	

	

	@Override
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		PK_OpCard opCard = new PK_OpCard();
		int cardSize = setSound.getCardList().size();
		ArrayList<PK_OpCard> outList = getCard(cardList, cardSize/SAMENUM, setSound);

		for (PK_OpCard tempOpCard : outList) {
			if (tempOpCard.checkIsNull()) {
				continue;
			}
			if (checkCardAndCompare(setSound.getSet().getAccessToSpecific(tempOpCard) , setSound)) {
				opCard = tempOpCard;
				setSound.getSet().checkCardIsMyCard(tempOpCard.cardList, cardList);
				if (tempOpCard.cardList.size() == cardSize) {
					break;
				} 
			}
		}
		setSound.getSet().checkCardIsMyCard(opCard.cardList, cardList);
		if (opCard.cardList.size() == cardSize) {
			return opCard;
		}
		return new PK_OpCard();
	}
	
	/**
	 * 根据传入的数组  获取顺子
	 * */
	public ArrayList<ArrayList<Integer>> getShunZi(ArrayList<Integer> inCardList, ArrayList<Integer> haveRazzCardList, int Count, PKRoomSetSound setSound) {
		ArrayList<ArrayList<Integer>> outList = new ArrayList<>();
//		inCardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
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
						if(haveRazzCardList.size() > 0) temp.add(haveRazzCardList.get(0));
					}
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
	
	/**
	 * 获取联队顺子
	 * */
	public PK_OpCard getLianDuiShunZi(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		ArrayList<PK_OpCard> duiZiList = PKLogicFactory.getOpCard(SanBuDaiCardEx.class).getOpCardList(cardList, setSound);
		if (duiZiList == null || duiZiList.size() <= 0) {
			return new PK_OpCard();
		}
		ArrayList<Integer> opList = new ArrayList<Integer>();
		ArrayList<Integer> substituteCard = new ArrayList<Integer>();
		for (int j = 0; j < duiZiList.size(); j++) {
			PK_OpCard tempOpCard = duiZiList.get(j);
			if (null != tempOpCard && !tempOpCard.checkIsNull()) {
				opList.add(tempOpCard.cardList.get(0));
			}else{
				if(null != tempOpCard && null != tempOpCard.substituteCard && tempOpCard.substituteCard.size() > 0)
					substituteCard.add(tempOpCard.substituteCard.get(0));
			}
		}
		return setSound.getSet().getAccessToSpecific( new PK_OpCard(opList,substituteCard));
	}

	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		CommLogD.error("SanBuDaiFeiJiEx getCard 没有实现 ");
		return null;
	}
	


}
