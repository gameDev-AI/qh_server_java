package core.network.client2game.handler.gd;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.room.CBase_ReadyRoom;

/**
 * 取消准备房间
 * 
 * @author Administrator
 *
 */
public class CGDUnReadyRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CBase_ReadyRoom req = new Gson().fromJson(message, CBase_ReadyRoom.class);
		// 获取房间信息
		AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.getRoomID());
		if (null == room) {
			request.error(ErrorCode.NotAllow, "CGDUnReadyRoom not find room:" + req.getRoomID());
			return;
		}
		SData_Result result = room.playerReady(false, player.getPid());
		if (ErrorCode.Success.equals(result.getCode())) {
			request.response();
		} else {
			request.error(result.getCode(), result.getMsg());
		}
	}
}
