package business.global.pk.gd;

import java.util.ArrayList;
import java.util.List;

import business.gd.c2s.cclass.PKRoomSet_Pos;
import business.global.pk.gd.pkbase.PKRoomPos;
import business.global.pk.gd.pkbase.PKSetPos;
import business.global.pk.gd.pkbase.PKSetPosMgr;
import com.ddm.server.common.CommLogD;
import business.gd.c2s.cclass.GD_define.GD_GONGFLAG;
import jsproto.c2s.cclass.pk.BasePockerLogic;

public class GDSetPosMgr extends PKSetPosMgr {
	GDRoomSet zRoomSet;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GDSetPosMgr(GDRoomSet  roomSet) {
		super(roomSet);
		zRoomSet = roomSet;
		for (int i = 0; i < roomSet.room.getPlayerNum(); i++) {
			posList.add(new GDSetPos(roomSet,  (PKRoomPos) roomSet.room.getRoomPosMgr().getPosByPosID(i)));
		}
	}

	public  PKSetPos getSetPos1(int index) {
		PKSetPos posRet = null;
		for (int i = 0,size = this.posList.size();i<size;i++) {
			GDSetPos pos = (GDSetPos)this.posList.get(i);
			if (pos.getCheckPos() == index) {
				posRet = pos;
				break;
			}
		}
		return posRet;
	}

	/**
	 * 清楚所有玩家的贡牌状态
	 * */
	public void clearGongFlag(){
		for (PKSetPos setPos: posList) {
			GDSetPos gdSetPos = (GDSetPos) setPos;
			gdSetPos.setGongflag(GD_GONGFLAG.GD_GONGFLAG_NORMAL);
		}
	}
	
	/**
	 * 检查所有的贡牌都已经供完
	 * */
	public boolean  checkAllGongPai(GD_GONGFLAG gongflag) {
		for (PKSetPos setPos: posList) {
			GDSetPos gdSetPos = (GDSetPos) setPos;
			if (gdSetPos.getGongflag().equals(gongflag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取最大的贡牌位置，给头游玩家
	 * @param firstOrderPos 头游位置
	 * @return
	 */
	public GDSetPos getMaxGongCardPos(int firstOrderPos) {
		GDSetPos maxGongSetPos = null;
		int playerNum = this.roomSet.room.getPlayerNum();
		for (int i = 0 ; i < playerNum; i++ ) {
			int index = (i + firstOrderPos)%playerNum;
			GDSetPos gdSetPos = (GDSetPos) getSetPos(index);
			if (gdSetPos.getGongflag().equals(GD_GONGFLAG.GD_GONGFLAG_GONGED)) {
				//进贡的牌没分配的玩家中选最大贡牌位置
				if (null == maxGongSetPos && -1 == gdSetPos.getHuangongCardPos()) {
					maxGongSetPos = gdSetPos;
				} else if(null != maxGongSetPos && -1 == gdSetPos.getHuangongCardPos()  &&
						BasePockerLogic.getCardValueEx(gdSetPos.getGongCard()) > BasePockerLogic.getCardValueEx(maxGongSetPos.getGongCard())){
					maxGongSetPos = gdSetPos;
				}
			}
		}
		return maxGongSetPos;
	}
	
	/**
	 * 获取玩家posinfo
	 * */
	public List<PKRoomSet_Pos> getPosInfo(long pid) {
		List<PKRoomSet_Pos> posInfo = new ArrayList<>(); // 一局玩家列表
		GDSetPos selfPos = (GDSetPos) this.zRoomSet.getSetPosMgr().getSetPosByPid(pid);
		int otherPos = zRoomSet.getHuoBanPos(selfPos.getPosID());
		boolean isShowHuoBanCardList = false;
		if (null != selfPos && selfPos.getPrivateCards().size() <= 0) {
			isShowHuoBanCardList = true;
		}
		for (int i = 0; i < this.zRoomSet.room.getPlayerNum(); i++) {
			GDSetPos setPos = (GDSetPos) this.zRoomSet.getSetPosMgr().getSetPos(i);
			if (i == otherPos) {
				posInfo.add(setPos.getSetPosInfo(isShowHuoBanCardList));
			} else {
				posInfo.add(setPos.getSetPosInfo(pid));
			}
		}
		return  posInfo;
	}

	public void setPosList(ArrayList<PKSetPos> posList) {
		this.posList = posList;
	}
}
