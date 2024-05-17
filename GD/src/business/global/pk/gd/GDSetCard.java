package business.global.pk.gd;


import business.global.pk.gd.pkbase.PKSetCard;
import jsproto.c2s.cclass.pk.BasePocker;
import jsproto.c2s.cclass.pk.BasePockerLogic;

/**
 * 设置牌
 * @author Huaxing
 *
 */
public class GDSetCard extends PKSetCard {
                                                                                                                                                                                                                                                                                                                                               

	public GDSetCard(GDRoom room){
		super(room);	
	}
	
	/**
	 * 获取牌
	 */
	@Override
	public void randomCard(){
		this.leftCards = BasePockerLogic.getOnlyRandomPockerList(2, 2, BasePocker.PockerListType.POCKERLISTTYPE_AEND);
	}
}

