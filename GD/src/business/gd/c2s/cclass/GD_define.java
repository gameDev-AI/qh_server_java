package business.gd.c2s.cclass;

/**
 * 掼蛋宏定义
 * @author zaf
 * **/
public class GD_define {
	
	//掼蛋类型
	public static enum GD_Sign{
		GD_SIGN_JD(0), //惯蛋经典
		GD_SIGN_TTZ(1), //惯蛋团团转
		;

		private int value;
		private GD_Sign(int value) {this.value = value;}
		public int value() {return this.value;}

		public static GD_Sign getGameType(String value) {
			String gameTypyName = value.toUpperCase();
			for (GD_Sign flow : GD_Sign.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return GD_Sign.GD_SIGN_JD;
		}

		public static GD_Sign valueOf(int value) {
			for (GD_Sign flow : GD_Sign.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return GD_Sign.GD_SIGN_JD;
		}
	};

	//可选玩法
	public enum GD_KeXuanWanFa1{
		ShuangXia4, //双下4级
		FanBei6, //六炸翻倍
		FanBei4, //4王炸4倍
		;
	};

	/**
	 * 掼蛋游戏状态 0发牌 1比牌 2结算
	 * @author Administrator
	 *
	 */
	public static enum GD_GameStatus{
		GD_GAME_STATUS_SENDCARD(0), //发牌
		GD_GAME_STATUS_GONGPAI(1), //贡牌
		GD_GAME_STATUS_HUANPAI(2), //还牌
		GD_GAME_STATUS_COMPAER(3), //比牌
		GD_GAME_STATUS_RESULT(4), //结算
		GD_GAME_STATUS_KANGGONG(5), //抗贡
		;

		private int value;
		private GD_GameStatus(int value) {this.value = value;}
		public int value() {return this.value;}

		public static GD_GameStatus getGameStatus(String value) {
			String gameTypyName = value.toUpperCase();
			for (GD_GameStatus flow : GD_GameStatus.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return GD_GameStatus.GD_GAME_STATUS_SENDCARD;
		}

		public static GD_GameStatus valueOf(int value) {
			for (GD_GameStatus flow : GD_GameStatus.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return GD_GameStatus.GD_GAME_STATUS_SENDCARD;
		}
	};

	//牌类型
	public static enum GD_CARD_TYPE{
		GD_CARD_TYPE_NOMARL(0),			//默认状态
		GD_CARD_TYPE_BUCHU(1),			//不出
		GD_CARD_TYPE_SING(2), 			//单牌
		GD_CARD_TYPE_DUIZI(3),  		//对子
		GD_CARD_TYPE_THREE(4),  		//三张
		GD_CARD_TYPE_THREEDAIDUI(5),  	//三张	三带对
		GD_CARD_TYPE_LIANDUI(6),  		//联队 	三连对
		GD_CARD_TYPE_SANSHUN(7),  		//三顺 	三连同张(钢板)
		GD_CARD_TYPE_SHUNZI(8),  		//顺子
		GD_CARD_TYPE_ZHADAN(9),  		//炸弹 五张及五张 以下炸弹
		GD_CARD_TYPE_TONGHUASHUNZI(10),  //同花顺 (火箭)
		GD_CARD_TYPE_ZHADAN6(11),  		//炸弹  六炸
		GD_CARD_TYPE_SIWANGZHA(12),  	//炸弹 	四大天王
		;
		private int value;
		private GD_CARD_TYPE(int value) {this.value = value;}
		public int value() {return this.value;}

		public static GD_CARD_TYPE getGameType(String value) {
			String gameTypyName = value.toUpperCase();
			for (GD_CARD_TYPE flow : GD_CARD_TYPE.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return GD_CARD_TYPE.GD_CARD_TYPE_DUIZI;
		}

		public static GD_CARD_TYPE valueOf(int value) {
			for (GD_CARD_TYPE flow : GD_CARD_TYPE.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return GD_CARD_TYPE.GD_CARD_TYPE_DUIZI;
		}
	};
	
	/**
	 * 掼蛋方式 赢的方式 0：默认值  1单下 2双下
	 * @author Administrator
	 *
	 */
	public static enum GD_WINFLAG{
		GD_WINFLAG_NORMAL(0), //默认值
		GD_WINFLAG_SIGN(1), //单下
		GD_WINFLAG_WINWIN(2), //双下
		;

		private int value;
		private GD_WINFLAG(int value) {this.value = value;}
		public int value() {return this.value;}

		public static GD_WINFLAG getWinFlag(String value) {
			String gameTypyName = value.toUpperCase();
			for (GD_WINFLAG flow : GD_WINFLAG.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return GD_WINFLAG.GD_WINFLAG_NORMAL;
		}

		public static GD_WINFLAG valueOf(int value) {
			for (GD_WINFLAG flow : GD_WINFLAG.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return GD_WINFLAG.GD_WINFLAG_NORMAL;
		}
	};
	
	
	/**
	 * 掼蛋方式 贡牌  -2:已经还贡 -1:需要还贡  0：默认值  1:需要进贡  2:已经进贡
	 * @author Administrator
	 *
	 */
	public static enum GD_GONGFLAG{
		GD_GONGFLAG_HUAANGONGED(-2), //已经还贡
		GD_GONGFLAG_HUAANGONG_HAVE(-1), //需要还贡
		GD_GONGFLAG_NORMAL(0), //默认值
		GD_GONGFLAG_GONG_HAVE(1), //需要进贡
		GD_GONGFLAG_GONGED(2), //已经进贡
		;

		private int value;
		private GD_GONGFLAG(int value) {this.value = value;}
		public int value() {return this.value;}

		public static GD_GONGFLAG getGongFlag(String value) {
			String gameTypyName = value.toUpperCase();
			for (GD_GONGFLAG flow : GD_GONGFLAG.values()) {
				if (flow.toString().equals(gameTypyName)) {
					return flow;
				}
			}
			return GD_GONGFLAG.GD_GONGFLAG_NORMAL;
		}

		public static GD_GONGFLAG valueOf(int value) {
			for (GD_GONGFLAG flow : GD_GONGFLAG.values()) {
				if (flow.value == value) {
					return flow;
				}
			}
			return GD_GONGFLAG.GD_GONGFLAG_NORMAL;
		}
	};


	public enum GDGameRoomConfigEnum {
		/**
		 * 自动准备
		 */
		ZiDongZhunBei,
		/**
		 * 小局托管解散
		 */
		SetAutoJieSan,
		;
	}

}
