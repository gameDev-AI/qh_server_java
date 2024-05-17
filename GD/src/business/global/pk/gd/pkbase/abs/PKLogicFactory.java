package business.global.pk.gd.pkbase.abs;

import java.util.concurrent.ConcurrentHashMap;

public class PKLogicFactory {


	private static ConcurrentHashMap<Class<?>, OpCardEx> allHuCard = new ConcurrentHashMap<Class<?>, OpCardEx>();

	public static OpCardEx getOpCard(Class<?> name) {
		if (allHuCard.get(name) == null) {
			synchronized (allHuCard) {
				if (allHuCard.get(name) == null) {
					PKLogicConcreteCreator hCreator = new PKLogicConcreteCreator();
					allHuCard.put(name, hCreator.createProduct(name) );
				}
			}
		}
		return allHuCard.get(name);
	}

}
