package core.network.client2game.handler.gd;

import business.player.Player;
import business.player.feature.PlayerClubRoom;
import business.gd.c2s.cclass.GD_define;
import business.gd.c2s.iclass.CGD_CreateRoom;
import cenum.PrizeType;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.server.gd.GDAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 亲友圈房间
 * 
 * @author Administrator
 *
 */
public class CGDClubRoom extends PlayerHandler {

	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws IOException {

		final CGD_CreateRoom clientPack = new Gson().fromJson(message,
				CGD_CreateRoom.class);
		// 公共房间配置
		BaseRoomConfigure<CGD_CreateRoom> configure = new BaseRoomConfigure<CGD_CreateRoom>(
				PrizeType.RoomCard,
				GDAPP.GameType(),
				clientPack.clone());
		player.getFeature(PlayerClubRoom.class).createNoneClubRoom(request,configure);
	}
}
