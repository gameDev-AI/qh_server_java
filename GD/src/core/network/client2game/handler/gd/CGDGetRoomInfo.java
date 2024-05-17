package core.network.client2game.handler.gd;

import java.io.IOException;

import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.global.room.RoomMgr;
import business.player.Player;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.room.CBase_GetRoomInfo;

/**
 * 获取房间信息
 * @author Huaxing
 *
 */
public class CGDGetRoomInfo extends PlayerHandler {


    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {

		final CBase_GetRoomInfo req = new Gson().fromJson(message, CBase_GetRoomInfo.class);
		AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.getRoomID());
		// 检查房间是否存在
		if (null == room) {
			request.error(ErrorCode.NotFind_Room, "CGDGetRoomInfo null == room");
			return;
		}
		AbsRoomPos roomPos = room.getRoomPosMgr().getPosByPid(player.getPid());
		// 检查玩家是否在房间中
		if (null == roomPos) {
			request.error(ErrorCode.NotFind_Player, "CGDGetRoomInfo null == roomPos");
			return;
		}
		// 获取房间信息
		request.response(room.getRoomInfo(player.getPid()));// 无账号情况下返回为空
    }
}
