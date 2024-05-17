package business.global.pk.gd.pkbase;

import java.util.ArrayList;

import business.global.pk.gd.GDConfigMgr;
import business.global.room.pk.PockerRoom;
import jsproto.c2s.cclass.pk.PKRoom_Record;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

public abstract class PKRoom extends PockerRoom {
	private ArrayList<Long>  m_XiPaiPidList = new ArrayList<Long>(); //洗牌pid 大于0标识洗牌玩家pid
	private GDConfigMgr configMgr = new GDConfigMgr();

    /**
     * 扑克公共父类构造函数
     *
     * @param baseRoomConfigure 公共配置
     * @param roomKey           房间key
     * @param ownerID
     */
    protected PKRoom(BaseRoomConfigure baseRoomConfigure, String roomKey, long ownerID) {
        super(baseRoomConfigure, roomKey, ownerID);
    }

	/**
	 * @return m_XiPaiPidList
	 */
	public ArrayList<Long> geXiPaiPidList() {
		return m_XiPaiPidList;
	}

	/**
	 * @return m_XiPaiPidList
	 */
	public void clearXiPaiPidList() {
		m_XiPaiPidList.clear();
	}


	public void endSet() {
		// TODO 自动生成的方法存根
		if(null != this.getCurSet())
			this.getCurSet().endSet();
	}

	public abstract void createSet();
	
	@Override
	public void roomTrusteeship(int pos) {
		((PKRoomSet)this.getCurSet()).roomTrusteeship(pos);
	}
	
	@SuppressWarnings("rawtypes")
	public abstract void  notifyAllRoomEnd(PKRoom_Record sRecord);


	@Override
	public int getTimerTime() {
		return 500;
	}

	/**
	 * @return configMgr
	 */
	public GDConfigMgr getConfigMgr() {
		return configMgr;
	}

}
