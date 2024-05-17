package business.global.pk.gd.pkbase.abs;

/**
 * 胡牌创建工程
 * @author Administrator
 *
 */
public class PKLogicConcreteCreator extends PKLogicCreator {  
  

	@SuppressWarnings("unchecked")
	@Override
	public <T extends OpCardEx> T createProduct(Class<?> clazz) {
		OpCardEx baseRoom = null;  
        try {  
        	baseRoom = (OpCardEx) Class.forName(clazz.getName()).newInstance();  
        } catch (Exception e) { //异常处理  
            e.printStackTrace();  
        }  
        return (T) baseRoom;  
	}


} 
