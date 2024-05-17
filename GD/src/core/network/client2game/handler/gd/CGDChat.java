package core.network.client2game.handler.gd;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.player.Player;
import cenum.ChatType;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.exception.WSException;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import jsproto.c2s.iclass.C1104_Chat;

import java.io.IOException;

public class CGDChat extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException, WSException {
        final C1104_Chat req = new Gson().fromJson(message, C1104_Chat.class);
        ChatType chatType = ChatType.valueOf(req.type);
        AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.targetID);
        if (room == null) {
            request.error(ErrorCode.NotAllow, "CGDChat room you in room:" + req.targetID);
            return;
        }
        SData_Result result = room.opChat(player, req.content, chatType, req.targetID, req.quickID);
        if (ErrorCode.Success.equals(result.getCode())) {
            request.response();
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }
}
