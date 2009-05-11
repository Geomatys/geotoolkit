/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.filter.accessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;

/**
 * Utility class to obtain a propertyAccesor.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Accessors {

    private static final List<PropertyAccessorFactory> ACCESSOR_FACTORIES = new ArrayList<PropertyAccessorFactory>();

    static{
        final Iterator<PropertyAccessorFactory> factories = ServiceRegistry.lookupProviders(PropertyAccessorFactory.class);

        while(factories.hasNext()){
            ACCESSOR_FACTORIES.add(factories.next());
        }

    }

    private Accessors(){}

    /**
     * Obtain a PropertyAccessor.
     *
     * @param type : candidate object class
     * @param xpath : attribut xpath
     * @param target : expected output type
     * @return PropertyAccessor or null if none could match the given classes
     */
    public static final PropertyAccessor getAccessor(Class type, String xpath, Class target){
        for(PropertyAccessorFactory pf : ACCESSOR_FACTORIES){
            PropertyAccessor pa = pf.createPropertyAccessor(type, xpath, target,null);
            if(pa != null) return pa;
        }
        return null;
    }

}
