/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.lang.Static;

/**
 * Utility class to obtain a propertyAccesor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Accessors extends Static {

    private static final PropertyAccessorFactory[] ACCESSOR_FACTORIES;

    static{
        final FactoryRegistry fr = new FactoryRegistry(PropertyAccessorFactory.class);
        final Iterator<PropertyAccessorFactory> factories = fr.getServiceProviders(PropertyAccessorFactory.class, null, null, null);

        final List<PropertyAccessorFactory> lst = new ArrayList<PropertyAccessorFactory>();
        while(factories.hasNext()){
            lst.add(factories.next());
        }

        Collections.sort(lst, new Comparator<PropertyAccessorFactory>(){
            @Override
            public int compare(PropertyAccessorFactory t, PropertyAccessorFactory t1) {
                return t1.getPriority() - t.getPriority();
            }
        });


        ACCESSOR_FACTORIES = lst.toArray(new PropertyAccessorFactory[lst.size()]);
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
    public static PropertyAccessor getAccessor(final Class type, final String xpath, final Class target){
        for(final PropertyAccessorFactory pf : ACCESSOR_FACTORIES){
            final PropertyAccessor pa = pf.createPropertyAccessor(type, xpath, target,null);
            if(pa != null) return pa;
        }
        return null;
    }

    /**
     * Get an array of all property accessor factories in priority order.
     * @return PropertyAccessorFactory[]
     */
    public static PropertyAccessorFactory[] getAccessorFactories(){
        return ACCESSOR_FACTORIES.clone();
    }

}
