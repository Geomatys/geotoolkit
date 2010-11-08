
package org.geotoolkit.pending.demo.filter.customaccessor;

import java.util.Date;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.util.Converters;


public class PojoPropertyAccessor implements PropertyAccessor{

    @Override
    public boolean canHandle(Class object, String xpath, Class target) {
        return (Pojo.class.isInstance(object));
    }

    @Override
    public Object get(Object object, String xpath, Class target) throws IllegalArgumentException {
        final Pojo pojo = (Pojo) object;

        if("depth".equals(xpath)){
            return pojo.getDepth();
        }else if("family".equals(xpath)){
            return pojo.getFamily();
        }else if("birth".equals(xpath)){
            return pojo.getBirth();
        }else{
            throw new IllegalArgumentException("Unknowned property : " + xpath);
        }
    }

    @Override
    public void set(Object object, String xpath, Object value, Class target) throws IllegalArgumentException {
        final Pojo pojo = (Pojo) object;

        if("depth".equals(xpath)){
            pojo.setDepth(Converters.convert(value, Integer.class));
        }else if("family".equals(xpath)){
            pojo.setFamily(Converters.convert(value, String.class));
        }else if("birth".equals(xpath)){
            pojo.setBirth(Converters.convert(value, Date.class));
        }else{
            throw new IllegalArgumentException("Unknowned property : " + xpath);
        }
    }
    
}
