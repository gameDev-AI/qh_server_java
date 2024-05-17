package core.network.client2game.handler.gd;

import business.global.room.RoomMgr;
import business.global.room.base.AbsBaseRoom;
import business.global.room.base.AbsRoomPos;
import business.player.Player;
import cenum.room.RoomState;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import jsproto.c2s.iclass.room.CBase_ContinueGame;

import java.io.IOException;

public class CGDTimeOutContinue extends PlayerHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public void handle(Player player, WebSocketRequest request, String message) throws IOException {
        final CBase_ContinueGame req = new Gson().fromJson(message, CBase_ContinueGame.class);
        AbsBaseRoom room = RoomMgr.getInstance().getRoom(req.getRoomID());
        if (null == room){
            request.error(ErrorCode.NotAllow, "CGDTimeOutContinue not find room:"+req.getRoomID());
            return;
        }
        if(!RoomState.Playing.equals(room.getRoomState())){
            // 房间不处于游戏阶段
            request.error(ErrorCode.NotAllow, "continueGame RoomState Playing :"+room.getRoomState());
            return;
        }
        AbsRoomPos roomPos = room.getRoomPosMgr().getPosByPid(player.getPid());
        if (null == roomPos) {
            // 找不到通过pid获取玩家信息
            request.error(ErrorCode.NotAllow, "continueGame null == roomPos");
            return;
        }
        if(roomPos.isGameReady()){
            // 找不到通过pid获取玩家信息
            request.error(ErrorCode.NotAllow, "continueGame null == roomPos");
            return;
        }
        if(!room.canContinue()){
            request.error(ErrorCode.NotAllow, "can not time == roomPos");
            return;
        }
        roomPos.setTimeSec(CommTime.nowSecond());
        request.response();
    }

}
