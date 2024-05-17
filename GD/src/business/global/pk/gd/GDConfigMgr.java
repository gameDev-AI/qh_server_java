package business.global.pk.gd;

import business.global.pk.gd.pkbase.PKConfigMgr;
import com.ddm.server.common.CommLogD;
import com.ddm.server.common.utils.Txt2Utils;

/**
 * 仙游炸棒 配置文件
 * @author zaf
 * */
public class GDConfigMgr extends PKConfigMgr {
	public static final String myfileName = "GDConfig.txt";
	private int redSteps ;
	private int blueSteps ;
	
	public GDConfigMgr(){
		super(myfileName);
		try {
			this.redSteps = Txt2Utils.string2Integer(this.configMap.get("redSteps"));
			this.blueSteps = Txt2Utils.string2Integer(this.configMap.get("blueSteps"));
		} catch (Exception e) {
			// TODO: handle exception
			CommLogD.error("GDConfigMgr e.toMsg="+e.toString());
		}
		
	}
	

	/**
	 * @return redSteps
	 */
	public int getRedSteps() {
		return redSteps;
	}

	/**
	 * @return blueSteps
	 */
	public int getBlueSteps() {
		return blueSteps;
	}
}
