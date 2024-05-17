package core.network.client2game.handler.gd;

import business.global.room.ContinueRoomInfoMgr;
import business.gd.c2s.iclass.CGD_ContinueRoom;
import business.gd.c2s.iclass.CGD_CreateRoom;
import business.player.Player;
import business.player.PlayerMgr;
import business.player.feature.PlayerRoom;
import cenum.PrizeType;
import cenum.room.RoomContinueEnum;
import com.ddm.server.common.utils.CommTime;
import com.ddm.server.websocket.def.ErrorCode;
import com.ddm.server.websocket.handler.requset.WebSocketRequest;
import com.google.gson.Gson;
import core.network.client2game.handler.PlayerHandler;
import core.network.http.proto.SData_Result;
import core.server.gd.GDAPP;
import jsproto.c2s.cclass.room.BaseRoomConfigure;
import jsproto.c2s.cclass.room.ContinueRoomInfo;
import jsproto.c2s.iclass.room.SRoom_ContinueRoom;
import jsproto.c2s.iclass.room.SRoom_ContinueRoomInfo;
import jsproto.c2s.iclass.room.SRoom_CreateRoom;

import java.io.IOException;

/**
 * 继续房间功能
 * 
 * @author Administrator
 *
 */
public class CGDContinueRoom extends PlayerHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Player player, WebSocketRequest request, String message)
			throws IOException {

		final CGD_ContinueRoom continueRoom = new Gson().fromJson(message,
				CGD_ContinueRoom.class);
		ContinueRoomInfo continueRoomInfo= player.getFeature(PlayerRoom.class).getContinueRoomInfo(continueRoom.roomID);
		//如果找不到的话 说明已经被删除了  过了十分钟的有效时间
		if(continueRoomInfo==null){
			request.error(ErrorCode.Object_IsNull, "ContinueRoomInfo Not Find",continueRoom.roomID);
			return;
		}
		//找到的话已经被使用了
		if(continueRoomInfo.isUseFlag()){
			request.error(ErrorCode.NotAllow, "ContinueRoomInfo has been used",continueRoom.roomID);
			return;
		}
		CGD_CreateRoom createRoom=(CGD_CreateRoom)continueRoomInfo.getBaseRoomConfigure().getBaseCreateRoom();
		createRoom.setPaymentRoomCardType(continueRoom.continueType);
//		 公共房间配置
		BaseRoomConfigure<CGD_CreateRoom> configure = new BaseRoomConfigure<>(
				PrizeType.RoomCard,
				GDAPP.GameType(),
				createRoom.clone());
		SData_Result result = player.getFeature(PlayerRoom.class).createRoomAndConsumeCard(configure);
		if (ErrorCode.Success.equals(result.getCode())) {
			SRoom_CreateRoom data=(SRoom_CreateRoom)result.getData();
			//创建成功了 把这条信息改为true存回管理器中
			continueRoomInfo.setUseFlag(true);
			ContinueRoomInfoMgr.getInstance().putContinueRoomInfo(continueRoomInfo);
			//结束时间五分钟内 向上一把的那些人员发送继续房间的消息  该人员必须不在房间里
			if(CommTime.nowSecond()-continueRoomInfo.getRoomEndTime()< RoomContinueEnum.RoomContinueTimeEnum.FiveMinute.value()){
				for(Long pid:continueRoomInfo.getPlayerIDList()){
					if(pid==player.getPid()) continue;
					Player playerLast=PlayerMgr.getInstance().getOnlinePlayerByPid(pid);
					if(playerLast!=null&&playerLast.getRoomInfo().getRoomId()==0L) playerLast.pushProto(SRoom_ContinueRoomInfo.make(data.getRoomID(),data.getRoomKey(),player.getName(),continueRoom.continueType,continueRoom.roomID));
				}
			}
			//创建成功的时候
			request.response(SRoom_ContinueRoom.make(data.getRoomID(),data.getRoomKey(),data.getCreateType(),data.getGameType(),continueRoom.continueType));
		} else {
			request.error(result.getCode(),result.getMsg());
		}
	}
}
