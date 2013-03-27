
package org.geotoolkit.pending.demo.filter.customaccessor;

import java.util.Date;
import org.geotoolkit.filter.binding.AbstractBinding;
import org.geotoolkit.util.Converters;


public class PojoBinding extends AbstractBinding<Pojo>{

    public PojoBinding() {
        super(Pojo.class, 0);
    }
    
    @Override
    public boolean support(String xpath) {
        return true;
    }
    
    @Override
    public <T> T get(Pojo candidate, String xpath, Class<T> target) throws IllegalArgumentException {

        if("depth".equals(xpath)){
            return (T) (Integer)candidate.getDepth();
        }else if("family".equals(xpath)){
            return (T) candidate.getFamily();
        }else if("birth".equals(xpath)){
            return (T) candidate.getBirth();
        }else{
            throw new IllegalArgumentException("Unknowned property : " + xpath);
        }
    }

    @Override
    public void set(Pojo candidate, String xpath, Object value) throws IllegalArgumentException {

        if("depth".equals(xpath)){
            candidate.setDepth(Converters.convert(value, Integer.class));
        }else if("family".equals(xpath)){
            candidate.setFamily(Converters.convert(value, String.class));
        }else if("birth".equals(xpath)){
            candidate.setBirth(Converters.convert(value, Date.class));
        }else{
            throw new IllegalArgumentException("Unknowned property : " + xpath);
        }
    }
    
}
