package business.gd.c2s.cclass;

import jsproto.c2s.cclass.room.RoomSetEndInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 仙游炸棒配置
 * @author Clark
 *
 */


// 一局结束的信息
public class PKRoom_SetEnd extends RoomSetEndInfo {
	public int endTime = 0;
	public List posResultList = new ArrayList<>(); // 每个玩家的结算
}

