package business.global.pk.gd.pkbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.Txt2Utils;

/**
 * 仙游炸棒 配置文件
 * @author zaf
 * */
public class PKConfigMgr {
	protected String fileName = "WSKConfig.txt";
	protected static final String filePath = "conf/";
	protected Map<String, String> configMap = new HashMap<String, String>();
	protected int handleCard; //底分
	protected int God_Card ;
	protected ArrayList<Integer> Private_Card1; 
	protected ArrayList<Integer> Private_Card2; 
	protected ArrayList<Integer> Private_Card3; 
	protected ArrayList<Integer> Private_Card4; 
	
	public PKConfigMgr(String fileName){
		try {
			this.fileName = fileName;
			this.configMap = Txt2Utils.txt2Map(filePath, fileName, "GBK");
			this.handleCard = Integer.valueOf(this.configMap.get("handleCard"));
			this.God_Card = Integer.valueOf(this.configMap.get("God_Card"));
			this.Private_Card1 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card1"));
			this.Private_Card2 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card2"));
			this.Private_Card3 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card3"));
			this.Private_Card4 = Txt2Utils.String2ListInteger(this.configMap.get("Private_Card4"));
		} catch (Exception e) {
			// TODO: handle exception
			CommLogD.error("PKConfigMgr e.toMsg="+e.toString());
		}
		
	}
	/**
	 * @return handleCard
	 */
	public int getHandleCard() {
		return handleCard;
	}
	/**
	 * @return god_Card
	 */
	public boolean isGodCard() {
		return God_Card == 1;
	}
	/**
	 * @param god_Card 要设置的 god_Card
	 */
	public void setGodCard(int godCard) {
		God_Card = godCard;
	}
	/**
	 * @return private_Card1
	 */
	public ArrayList<Integer> getPrivate_Card1() {
		return Private_Card1;
	}
	/**
	 * @return private_Card2
	 */
	public ArrayList<Integer> getPrivate_Card2() {
		return Private_Card2;
	}
	/**
	 * @return private_Card3
	 */
	public ArrayList<Integer> getPrivate_Card3() {
		return Private_Card3;
	}
	/**
	 * @return private_Card4
	 */
	public ArrayList<Integer> getPrivate_Card4() {
		return Private_Card4;
	}
}
