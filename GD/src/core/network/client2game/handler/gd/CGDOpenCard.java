package core.network.client2game.handler.gd;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.pk.gd.GDRoom;
import business.global.pk.gd.GDRoomSet;
import business.global.room.RoomMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import business.gd.c2s.iclass.CGD_OpenCard;

/**
 * 明牌
 * */

public class CGDOpenCard extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CGD_OpenCard clientPack = new Gson().fromJson(message, CGD_OpenCard.class);
    	
		GDRoom room = (GDRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CDDZOpenCard not find room:"+clientPack.roomID);
    		return;
    	}
    	GDRoomSet set =  (GDRoomSet) room.getCurSet();
    	if(null == set){
    		request.error(ErrorCode.NotAllow, "CDDZOpenCard not set room:"+clientPack.roomID);
    		return;
    	}
    	set.onOpenCard(request, player.getPid(), clientPack);		
	}
}
