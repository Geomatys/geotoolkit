
package org.geotoolkit.pending.demo.filter.customaccessor;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.filter.accessor.PropertyAccessorFactory;

public class PojoPropertyAccessorFactory implements PropertyAccessorFactory{

    private static final PojoPropertyAccessor ACCESSOR = new PojoPropertyAccessor();

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target, Hints hints) {
        if(Pojo.class.isAssignableFrom(type)){
            return ACCESSOR;
        }
        return null;
    }

}
