package business.global.pk.gd.pkbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ddm.server.common.CommLogD;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 接收客户端数据
 * 打牌操作
 * @author zaf
 *
 */

public class PK_OpCard  {
//    public int opType = 0;  //PDK_CARD_TYPE 操作类型及牌的类型
    public ArrayList<Integer> cardList = new ArrayList<>();//手牌数组
    public ArrayList<Integer> substituteCard = new ArrayList<>();//赖子代替牌数组
   
    public  PK_OpCard () {
    }
    
    public  PK_OpCard ( List<Integer> cardList,List<Integer> substituteCard) {
        if(null != cardList && cardList.size() > 0)
        	this.cardList.addAll( cardList.subList(0, cardList.size()));
        if(null != substituteCard && substituteCard.size() > 0)
        	this.substituteCard.addAll(substituteCard.subList(0, substituteCard.size()));
    }
    
    public  PK_OpCard (List<Integer> cardList) {
        this.cardList.addAll(cardList.subList(0, cardList.size()));
    }
    
    @SuppressWarnings("unchecked")
	public  PK_OpCard (PK_OpCard opCard) {
        this.cardList.addAll((Collection<? extends Integer>) opCard.cardList.clone());
        this.substituteCard.addAll((Collection<? extends Integer>) opCard.substituteCard.clone());
    }
    
    public void addSameObject(int countZero, int card, int num) {
    	this.substituteCard.addAll(Collections.nCopies(countZero, 0));
		this.substituteCard.addAll(Collections.nCopies(num, card));
	}
    
    /**
     * 判断是否控
     * */
    public boolean checkIsNull() {
		if (null == cardList || (null != cardList && cardList.size() <= 0)) {
			return true;
		}
		if (null == substituteCard ) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "PK_OpCard [cardList=" + cardList + ", substituteCard=" + substituteCard + "]";
	}
	
	/**
	 * 获取几个赖子
	 * */
	public int  getSubStituteSize() {
		int count = 0;
		if (null != substituteCard) {
			for (Integer integer : substituteCard) {
				if (integer != 0) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * 排序
	 * */
	public void  sortBigToSmall() {
		if (null == substituteCard  || (null != substituteCard && getSubStituteSize() == 0)) {
			cardList.sort(BasePockerLogic.sorterBigToSmallHaveTrump);
			return ;
		}
		for (int i = 0; i < cardList.size(); i++) {
			for (int j = 0; j < cardList.size(); j++) {
				CommLogD.info("aa");
				if(i == j) continue;
				if (BasePockerLogic.getCardValueEx(cardList.get(i)) > BasePockerLogic.getCardValueEx(cardList.get(j))) {
					int tempI = cardList.get(i);
					cardList.set(i, cardList.get(j));
					cardList.set(j, tempI);
					if ((null != substituteCard && getSubStituteSize() != 0)) {
						int temp = substituteCard.get(i);
						substituteCard.set(i, substituteCard.get(j));
						substituteCard.set(j, temp);
					}
				}
			}
		}
	}
}
