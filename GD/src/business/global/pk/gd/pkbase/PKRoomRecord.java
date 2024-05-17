package business.global.pk.gd.pkbase;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.RoomRecord;
import jsproto.c2s.cclass.pk.PKRoom_Record;

/**
 * 房间记录
 * @author zaf
 *
 */
public class PKRoomRecord extends RoomRecord{
    private PKRoom_Record record = new PKRoom_Record();


    
    public PKRoomRecord(AbsBaseRoom room) {
		super(room);
		initRecord(room);
	}
    
	/**
	 * 初始房间记录
	 */
   public void initRecord(AbsBaseRoom room){
	   record.roomID = room.getGameRoomBO().getId();
	   record.endSec = room.getGameRoomBO().getEndTime();
	   record.setCnt = room.getGameRoomBO().getSetCount();
    }
   
    @SuppressWarnings({ "unchecked" })
	public <T> T getShortInfo(){
    	return (T) record;
    }
}
