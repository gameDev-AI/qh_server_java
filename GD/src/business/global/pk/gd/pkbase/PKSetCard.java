package business.global.pk.gd.pkbase;

import business.global.room.base.AbsBaseRoom;
import jsproto.c2s.cclass.pk.BasePockerLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * 扑克，设置牌
 * @author Huaxing
 *
 */
public  abstract class PKSetCard {

	public ArrayList<Integer> leftCards = new ArrayList<>(); // 扑克牌编号
	public ArrayList<Integer> leftCardsBack = new ArrayList<>(); // 扑克牌编号备份
	public AbsBaseRoom room;//房间
	protected Random random;	
	
	@SuppressWarnings("unchecked")
	public PKSetCard(AbsBaseRoom room){
		this.room = room;
		random = new Random();
		this.randomCard();
		this.onXiPai();
		this.leftCardsBack = (ArrayList<Integer>) this.leftCards.clone();
	}
	
	public void clean () {
		if (null != this.leftCards) {
			this.leftCards.clear();
			this.leftCards = null;
		}
		if (null != this.leftCardsBack) {
			this.leftCardsBack.clear();
			this.leftCardsBack = null;
		}
		this.room = null;
		this.random = null;
	}


	/**
	 * 获取牌
	 */
	public abstract void randomCard();
	
	/**
	 * 洗牌
	 * **/
	@SuppressWarnings("unchecked")
	public void  onXiPai() {
		Collections.shuffle(this.leftCards);
		this.leftCardsBack = (ArrayList<Integer>) this.leftCards.clone();
	}

	/**
	 * 发牌
	 * @param cnt
	 * @return
	 */
	public ArrayList<Integer> popList(int cnt){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		if(this.leftCards.size() <= 0) return ret;
		for (int i = 0; i < cnt; i++) {
			if(this.leftCards.size() <= 0) return ret;
			Integer byte1 = this.leftCards.remove(random.nextInt(this.leftCards.size()));
			ret.add(byte1);
		}
		return ret;
	}


	public ArrayList<Integer> getLeftCards() {
		return leftCards;
	}


	public ArrayList<Integer> getLeftCardsBack() {
		return leftCardsBack;
	}
	
	/**
	 * 随机获取一张牌
	 * */
	public int getRandomCard() {
		return this.leftCardsBack.get(random.nextInt(this.getLeftCardsBack().size()));
	}
	
	/**
	 * 获取某一张牌的值
	 * */
	public int getCard(int cardValue) {
		for (int i = 0; i < leftCardsBack.size(); i++) {
			int card = this.leftCardsBack.get(i);
			if (BasePockerLogic.getCardValueEx(card) == BasePockerLogic.getCardValueEx(cardValue)) {
				return card;
			}
		}
		return -1;
	}
}

