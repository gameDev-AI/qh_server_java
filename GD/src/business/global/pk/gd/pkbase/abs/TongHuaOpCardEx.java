package business.global.pk.gd.pkbase.abs;

import java.util.ArrayList;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;
import jsproto.c2s.cclass.pk.BasePockerLogic;

public abstract class TongHuaOpCardEx implements OpCardEx{
	/**
	 * 检查花色
	 * */
	@SuppressWarnings("unchecked")
	public boolean CheckColor(PK_OpCard opCard,PKRoomSetSound setSound) {
		if(opCard.checkIsNull()){
			return false;
		}
		ArrayList<Integer> cardList = (ArrayList<Integer>) opCard.cardList.clone();
		ArrayList<Integer> valueList = (ArrayList<Integer>) cardList.clone(); 
		setSound.getSet().deleteRazzCard(valueList);
		int color = BasePockerLogic.getCardColor(valueList.get(0));
		for (int i = 1; i < valueList.size(); i++) {
			if (color != BasePockerLogic.getCardColor(valueList.get(i))) {
				
			}
		}
		return true;
	}
}
