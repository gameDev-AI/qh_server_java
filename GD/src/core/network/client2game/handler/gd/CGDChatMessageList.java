package core.network.client2game.handler.gd;

import java.io.IOException;
import java.util.List;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.proto.ChatMessage;
import jsproto.c2s.iclass.room.CBase_GetRoomInfo;

/**
 * 房间聊天记录20条
 * @author liyan
 *
 */
public class CGDChatMessageList extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {	
		
		List<ChatMessage> chatList = null;
		final CBase_GetRoomInfo req = new Gson().fromJson(message, CBase_GetRoomInfo.class);
		AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.getRoomID());
		if(null ==room){
			request.error(ErrorCode.NotAllow, "Game not find room:"+req.getRoomID());
    		return;
		}
		else{			
			chatList = room.getChatList();
			if (chatList != null) {
				request.response(chatList);
				return;
			}
			request.response();
		}
	}

}
