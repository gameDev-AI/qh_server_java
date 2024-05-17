package business.global.pk.gd.pkbase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import business.global.room.base.AbsRoomSet;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 纸牌
 * */
public abstract class PokerLogic extends AbsRoomSet {

	public PokerLogic(int setID) {
		super(setID);
	}

	/**
	 * 获取牌值
	 * */
	public int getCardValueEx(int card)
	{
		return BasePockerLogic.getCardValueEx(card);
	}
	
	/**
	 * 将数组按值变成map并排序
	 * */
	public Map<Integer, List<Integer>> getCardMap(ArrayList<Integer> cardList) {
		return getCardMap(cardList, true);
	}
	public Map<Integer, List<Integer>> getCardMap(ArrayList<Integer> cardList, boolean isSmallToBig) {
		Map<Integer, List<Integer>> inCardMap =  cardList.stream().collect(Collectors.groupingBy(p -> getCardValueEx(p)));
		if (inCardMap == null ) {
            return new ConcurrentHashMap<>();
        }
		
		if (null != inCardMap && inCardMap.isEmpty()) {
			return inCardMap;
		}
		Map<Integer, List<Integer>> sortMap = null;
		if (isSmallToBig) {
			sortMap = new TreeMap<Integer, List<Integer>>(
					new Comparator<Integer> (){
	                	public int compare(Integer s1, Integer s2) {
	                		return s1.compareTo(s2);  //从小到大排序
	                	}
					});
			
		} else {
			sortMap = new TreeMap<Integer, List<Integer>>(
					new Comparator<Integer>(){
	                	public int compare(Integer s1, Integer s2) {
	                		return s2.compareTo(s1);  //从大到小排序
	                	}
	                });
		}

        sortMap.putAll(inCardMap);
        return sortMap;
	}
	
	
	/**
	 * 比较值的大小 包含大小王 left比rigth大返回ture否侧false
	 * */
	public boolean compareCard(int left, int rigth){
		if( BasePockerLogic.getCardColor(left) == BasePockerLogic.getCardColor(rigth)){
			if(getCardValueEx(rigth) < getCardValueEx(left)){
				return true;
			}
		}else{ 
			if(BasePockerLogic.getCardColor(rigth) == BasePocker.PockerColorType.POCKER_COLOR_TYPE_TRUMP.value() ){
			
			}else if(BasePockerLogic.getCardColor(left) == BasePocker.PockerColorType.POCKER_COLOR_TYPE_TRUMP.value()){
				return true;
			}else if( getCardValueEx(rigth) < getCardValueEx(left)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 删除牌组信息
	 * @return
	 */
	public ArrayList<Integer> deleteRazzCard(ArrayList<Integer> cradList){
		return deleteRazzCard(cradList, getRazzCardList());
	}
	public ArrayList<Integer> deleteRazzCard(ArrayList<Integer> cradList, ArrayList<Integer> razzCardList) {
		ArrayList<Integer> list = new ArrayList<>();
		if(null == razzCardList || (null != razzCardList && razzCardList.size() <= 0))
			return list;
		for (int i = 0; i < cradList.size(); ) {
			if (i >= cradList.size()) {
				break;
			}
			int card = cradList.get(i);
			if (checkIsRazzCard(card, razzCardList)) {
				list.add(card);
				cradList.remove(i);
			}else{
				i++;
			}
		}
		return list;
	}
	
	/**
	 * 判断牌是不是赖子牌
	 * */
	public boolean checkIsRazzCard(int card){
		return checkIsRazzCard(card, getRazzCardList());
	}
	
	public boolean checkIsRazzCard(int card, ArrayList<Integer> razzCardList){
		if(null == razzCardList || (null != razzCardList && razzCardList.size() <= 0))
			return false;
		if (razzCardList.contains(card)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取不是赖子的最小牌
	 * */
	@SuppressWarnings("unchecked")
	public int getSmallCard(ArrayList<Integer> cardList) {
		int card = 0;
		ArrayList<Integer> tempCardList = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> haveRazzCardList = deleteRazzCard(tempCardList);
		haveRazzCardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		tempCardList.sort(BasePockerLogic.sorterSmallToBigHaveTrump);
		if (haveRazzCardList.size() == cardList.size()) {
			card = haveRazzCardList.get(0);
		} else {
			card = tempCardList.get(0);
		}
		return card;
	}
	
	/**
	 * 判断牌是否是自己的
	 * */
	public boolean checkCardIsMyCard(ArrayList<Integer> outCardList, ArrayList<Integer> cardList) {
		ArrayList<Integer> razzCardList = getRazzCardList();
		int size = outCardList.size();
		outCardList.removeAll(razzCardList);
		ArrayList<Integer> haveRazzCardList = deleteRazzCard(cardList);
		if (haveRazzCardList.size() >= size -outCardList.size()) {
			outCardList.addAll(haveRazzCardList.subList(0, size - outCardList.size()));
			return true;
		}else{
			outCardList.clear();
			return false;
		}
	}
	
	/**
	 * 获取赖子值
	 * */
	public int  getRazzCardValue(ArrayList<Integer> arrayList, int card,ArrayList<Integer> haveRazzCardList) {
		int razzCardIndex = arrayList.indexOf(card);
		int notRazzCardInde = -1;
		for (int i = 0; i < arrayList.size(); i++) {
			if (-1 == notRazzCardInde && !haveRazzCardList.contains(arrayList.get(i)) ) {
				notRazzCardInde = i;
				break;
			}
		}
		return arrayList.get(notRazzCardInde) - (notRazzCardInde - razzCardIndex);
	}
	
	/**
	 * 获取赖子代替的牌
	 * */
	public ArrayList<PK_OpCard> insteadOfRazzCard(ArrayList<ArrayList<Integer>> outListCard,ArrayList<Integer> haveRazzCardList) {
		ArrayList<PK_OpCard> tempOutOpCardList  = new ArrayList<>();
		for (ArrayList<Integer> arrayList : outListCard) {
			PK_OpCard opCard = new PK_OpCard(arrayList);
			for (int i = 0; i < arrayList.size(); i++) {
				int card = arrayList.get(i);
				if (haveRazzCardList.contains(card)) {
					opCard.substituteCard.add(getRazzCardValue(arrayList, card, haveRazzCardList));
				} else {
					opCard.substituteCard.add(0);
				}
			}
			tempOutOpCardList.add(opCard);
		}
		return tempOutOpCardList;
	}
		
	/**
	 * 获取特定的pk_opCard  及将substituteCard 和 cardList 组合成新的pk_opcard
	 * */
	public PK_OpCard getAccessToSpecific(PK_OpCard opCard) {
		ArrayList<Integer> razzCardList = getRazzCardList();
		PK_OpCard pk_OpCard = new PK_OpCard(opCard);
		for (int i = 0; i < pk_OpCard.cardList.size(); i++) {
			if(null == pk_OpCard || (null != pk_OpCard && pk_OpCard.checkIsNull()))
				continue;
			int  card = pk_OpCard.cardList.get(i);
			if (razzCardList.contains(card) ) {
				if (i < pk_OpCard.substituteCard.size() && pk_OpCard.substituteCard.get(i) > 0) {
					pk_OpCard.cardList.set(i, pk_OpCard.substituteCard.get(i));
					pk_OpCard.substituteCard.set(i, 0);
				} 
			}
		}
		return pk_OpCard;
	}
	
	/**
	 * 获取赖子
	 * */
	public abstract ArrayList<Integer> getRazzCardList() ;
}
