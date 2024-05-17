package business.global.pk.gd.pkbase.abs;

import java.util.ArrayList;

import business.global.pk.gd.pkbase.PKRoomSetSound;
import business.global.pk.gd.pkbase.PK_OpCard;

public interface OpCardEx {
	/**
	 * 检查牌是否能组合到牌型
	 * @param opCard  检查的牌
	 * @param  setSound  赖子牌
	 * */
	public boolean checkCard(PK_OpCard opCard, PKRoomSetSound setSound);
	
	/**
	 * 检查牌的大小及检查牌是否能组合到牌型
	 * @param opCard  检查的牌
	 * @param setSound 当前setround
	 * */
	public boolean checkCardAndCompare(PK_OpCard opCard, PKRoomSetSound setSound);
	
	/**
	 * 获取牌
	 *@param cardList  从牌中获取
	 *@param  setSound  赖子牌
	 */
	public ArrayList<PK_OpCard> getOpCardList(ArrayList<Integer> cardList, PKRoomSetSound setSound); 
	
	/**
	 * 获取牌
	 *@param cardList  从牌中获取
	 * @param setSound 当前setround
	 */
	public PK_OpCard getCard(ArrayList<Integer> cardList, PKRoomSetSound setSound); 
	
	/**
	 * 获取牌
	 *@param cardList  从牌中获取
	 *@param count  个数
	 *@param  setSound  赖子牌
	 */
	public ArrayList<PK_OpCard> getCard(ArrayList<Integer> cardList, int count, PKRoomSetSound setSound); 
	
}
