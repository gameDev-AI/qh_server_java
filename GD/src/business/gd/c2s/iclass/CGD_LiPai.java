package business.gd.c2s.iclass;

import java.util.ArrayList;

import jsproto.c2s.cclass.BaseSendMsg;

public class CGD_LiPai extends BaseSendMsg {
	public long roomID;
	public ArrayList<ArrayList<Integer>> liPaiList = new ArrayList<>();

	public static CGD_LiPai make(long roomID,  ArrayList<ArrayList<Integer>> liPaiList) {
		CGD_LiPai ret = new CGD_LiPai();
		ret.roomID = roomID;
		ret.liPaiList = liPaiList;
		return ret;

	}
}
