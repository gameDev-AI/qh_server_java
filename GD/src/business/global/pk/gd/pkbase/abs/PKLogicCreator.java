package business.global.pk.gd.pkbase.abs;

/**
 * 胡牌工厂
 * @author Administrator
 *
 */
public abstract class PKLogicCreator {  
    //创建一个产品对象，其输入参数类型可以自行设置  
    public abstract <T extends OpCardEx> T createProduct(Class<?> clazz);  
}  
