package business.global.pk.gd.pkbase.optype.zhadanex;

import java.util.ArrayList;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import business.global.pk.gd.pkbase.abs.OpCardEx;
import jsproto.c2s.cclass.pk.BasePocker;

/**
 * 炸弹  没有处理赖子
 * */
public class ZhaDanCardEx_GD_SiTrump implements OpCardEx {

	protected final int COUNT = 4;  //三带的数量
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound) {
		if (null == opCard) {
			return false;
		}
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		if (null == cardList || (cardList != null && cardList.size() != COUNT)) {
			return false;
		}
				
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		
		ArrayList<Integer> trumpList = getTrumpList();
		ArrayList<Integer> haveTrumpCardList = setSound.getSet().deleteRazzCard(valueList, trumpList);
		
		if (valueList.size() <= 0 && trumpList.containsAll(haveTrumpCardList)) {
			return true;
		}
		return false;
	}
	
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
		
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound) {
		
		ArrayList<PK_OpCard> outCardList = new ArrayList<>();
		
		ArrayList<Integer> tempCardTrumpList = (ArrayList<Integer>) cardList.clone();
		ArrayList<Integer> trumpList = getTrumpList();
		ArrayList<Integer> haveTrumpCardList = setSound.getSet().deleteRazzCard(tempCardTrumpList, trumpList);
		if(tempCardTrumpList.size() == 0 && haveTrumpCardList.containsAll(trumpList)) {
			outCardList.add(new PK_OpCard(haveTrumpCardList));
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
