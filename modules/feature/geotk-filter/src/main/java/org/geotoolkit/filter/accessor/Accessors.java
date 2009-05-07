

package org.geotoolkit.filter.accessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;

public class Accessors {

    private static final List<PropertyAccessorFactory> ACCESSOR_FACTORIES = new ArrayList<PropertyAccessorFactory>();

    static{
        final Iterator<PropertyAccessorFactory> factories = ServiceRegistry.lookupProviders(PropertyAccessorFactory.class);

        while(factories.hasNext()){
            ACCESSOR_FACTORIES.add(factories.next());
        }

    }

    public static final PropertyAccessor getAccessor(Class type, String xpath, Class target){
        for(PropertyAccessorFactory pf : ACCESSOR_FACTORIES){
            PropertyAccessor pa = pf.createPropertyAccessor(type, xpath, target,null);
            if(pa != null) return pa;
        }
        return null;
    }

}
