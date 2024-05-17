package core.network.client2game.handler.gd;

import java.io.IOException;

import business.gd.c2s.iclass.CGD_CreateRoom;
import business.player.Player;
import business.player.feature.PlayerRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.gd.GDAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

public class CGDCreateRoom extends PlayerHandler{

	@Override
	public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
		final CGD_CreateRoom clientPack = new Gson().fromJson(message,
				CGD_CreateRoom.class);
		// 公共房间配置
		BaseRoomConfigure<CGD_CreateRoom> configure = new BaseRoomConfigure<CGD_CreateRoom>(
				PrizeType.RoomCard,
				GDAPP.GameType(),
				clientPack.clone());
		SData_Result resule = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
		if (ErrorCode.Success.equals(resule.getCode())) {
			request.response(resule.getData());
		} else {
			request.error(resule.getCode(),resule.getMsg());
		}
	}

}
