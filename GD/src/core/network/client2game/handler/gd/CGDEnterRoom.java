package core.network.client2game.handler.gd;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.player.Player;
import business.player.feature.PlayerRoom;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.room.CBase_EnterRoom;

/**
 * 进入房间
 * @author Administrator
 *
 */
public class CGDEnterRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws IOException {
		final CBase_EnterRoom req = new Gson().fromJson(message, CBase_EnterRoom.class);
		// 进入房间
		SData_Result result = player.getFeature(PlayerRoom.class).findAndEnter(req.getPosID(), req.getRoomKey(),req.getClubId(),req.getPassword());
		if (ErrorCode.Success.equals(result.getCode())) {
			request.response(result.getData());
		} else {
			request.error(result.getCode(), result.getMsg());
		}
	}

}
