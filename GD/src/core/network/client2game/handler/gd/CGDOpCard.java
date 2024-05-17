package core.network.client2game.handler.gd;

import java.io.IOException;

import business.global.pk.gd.GDRoomSetSound;
import business.global.pk.gd.pkbase.PKRoomSet;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.pk.gd.GDRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import business.gd.c2s.iclass.CGD_OpCard;

/**
 * 操作牌
 * */

public class CGDOpCard extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CGD_OpCard clientPack = new Gson().fromJson(message, CGD_OpCard.class);
    	
		GDRoom room = (GDRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
    	if (null == room){
    		request.error(ErrorCode.NotAllow, "CYGLFLOpCard not find room:"+clientPack.roomID);
    		return;
    	}
		PKRoomSet set =  (PKRoomSet) room.getCurSet();
		if(null == set){
			request.error(ErrorCode.NotAllow, "CYGLFLOpCard not set room:"+clientPack.roomID);
			return;
		}
		GDRoomSetSound sound = (GDRoomSetSound) set.getCurRound();
		if(null == sound){
			request.error(ErrorCode.NotAllow, "CYGLFLOpCard not sound room:"+clientPack.roomID);
			return;
		}
		sound.onOpCard(request,  clientPack);
	}
}
