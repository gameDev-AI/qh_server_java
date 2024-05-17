package core.network.client2game.handler.gd;

import business.player.Player;
import business.player.feature.PlayerRoom;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;

import java.io.IOException;

/**
 * 继续加入房间
 */
public class CGDContinueEnterRoom extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        SData_Result result = player.getFeature(PlayerRoom.class).continueFindAndEnter();
        if (ErrorCode.Success.equals(result.getCode())) {
            request.response(result.getData());
        } else {
            request.error(result.getCode(), result.getMsg());
        }
    }

}
