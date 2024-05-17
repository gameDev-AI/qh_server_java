package core.network.client2game.handler.gd;

import java.io.IOException;

import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;

import business.global.pk.gd.GDRoom;
import business.global.room.RoomMgr;
import business.player.Player;
import core.network.client2game.handler.PlayerHandler;
import business.gd.c2s.iclass.CGD_SitDown;

/**
 * 操作牌
 */

public class CGDSitDown extends PlayerHandler {

    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws WSException, IOException {
        final CGD_SitDown clientPack = new Gson().fromJson(message, CGD_SitDown.class);

        GDRoom room = (GDRoom) RoomMgr.getInstance().getRoom(clientPack.roomID);
        if (null == room) {
            request.error(ErrorCode.NotAllow, "CYGLFLOpCard not find room:" + clientPack.roomID);
            return;
        }

        room.onSitDown(request, player.getPid(), clientPack.pos);
    }
}
