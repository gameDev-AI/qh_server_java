package core.network.client2game.handler.gd;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.pk.gd.GDRoom;
import business.global.pk.gd.GDRoomSet;
import business.global.pk.gd.GDSetPos;
import business.global.room.RoomMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import business.gd.c2s.iclass.CGD_HuanGongPai;

/**
 * 还贡操作
 * @author Huaxing
 *
 */
public class CGDHuanGongPai extends PlayerHandler {
	

    @SuppressWarnings("rawtypes")
	@Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
    	final CGD_HuanGongPai req = new Gson().fromJson(message, CGD_HuanGongPai.class);
    	long roomID = req.roomID;

    	
    	GDRoom room = (GDRoom) RoomMgr.getInstance().getRoom(roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CGDHuanGongPai not find room:"+roomID);
    		return;
    	}
    	
    	GDRoomSet roomSet = (GDRoomSet) room.getCurSet();
    	if (null == roomSet) {
    		request.error(ErrorCode.NotAllow, "CGDHuanGongPai not find set:"+roomID);
    		return;
		}
    	GDSetPos setPos = (GDSetPos) roomSet.getSetPosMgr().getSetPosByPid(player.getId());
		if (null == setPos) {
			request.error(ErrorCode.NotAllow, "CGDHuanGongPai not find your pos:pid="+player.getId());
			return;
		}
    
    	// 理牌操作
		room.opHuanGongPai(request, setPos,  req);
    }
}
