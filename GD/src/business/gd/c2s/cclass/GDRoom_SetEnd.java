package business.gd.c2s.cclass;

import java.util.ArrayList;
import java.util.List;

/**
 * 仙游炸棒配置
 * @author Clark
 *
 */


// 一局结束的信息
public class GDRoom_SetEnd{
	public int endTime = 0;
	public List<GDRoom_PosEnd> posResultList = new ArrayList<>(); // 每个玩家的结算
}

