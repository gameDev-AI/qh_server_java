package core.network.client2game.handler.gd;

import com.ddm.server.common.data.AbstractRefDataMgr;
import com.wanzi.test.BaseGameTest;
import com.wanzi.test.MJProcess;
import core.network.client2game.handler.PlayerHandler;
import core.server.gd.GDAPP;
import jsproto.c2s.cclass.GameType;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * @author zhujianming
 * @date 2020-08-11 15:41
 */
@PrepareForTest(value = {AbstractRefDataMgr.class, GDAPP.class}) //静态方法使用,可以使多类
public class CGDOpCardTest extends BaseGameTest {
    protected PlayerHandler gdLiPaiHandler;

    @Override
    public void setUp() throws Exception {
        createRoomHandler = new CGDCreateRoom();
        enterRoomHandler = new CGDEnterRoom();
        readyRoomHandler = new CGDReadyRoom();
        trusteeshipHandler = new CGDTrusteeship();
        opCardHandler = new CGDOpCard();
        gdLiPaiHandler = new CGDLiPai();
        GameType gameType = new GameType(38, "GD", 2);
        setCityAndRoomID(103050110039012004L,1030501);
        PowerMockito.mockStatic(GDAPP.class);
        PowerMockito.when(GDAPP.GameType()).thenReturn(gameType);
        initRequestLength(4);
        super.setUp();
        mockGameType(gameType);
    }

    @Test
    public void handle() throws Exception {
//        getMJPlayerBackCode(1579162);
        baseCreateRoom = "{\"setCount\":12,\"playerNum\":\"4\",\"paymentRoomCardType\":1,\"sign\":1,\"gaoji\":[],\"createType\":1}";
        //创建房间并打牌
        createRoom(new CallBackEvent() {
            @Override
            public void resolveEvent(Object object) {
                try {
                    Thread.sleep(10000);
                    gdLiPaiHandler.handle(mockRequestList.get(0).getPlayer(), mockRequestList.get(0).getWebSocketRequest(), "{\"roomID\":" + mockRequestList.get(0).getPlayer().getRoomInfo().getRoomId() + ",\"liPaiList\":[[3,115,35,51,101,133,102,134,87,23,55,24,136,89,121,137,59,13,141,14,94,46,114,50,18,2,146]]}");
                    MJProcess.getInstance().process(room, mockRequestList, opCardHandler);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public String getName() {
                return null;
            }
        });
        Thread.sleep(10000000);
    }

}