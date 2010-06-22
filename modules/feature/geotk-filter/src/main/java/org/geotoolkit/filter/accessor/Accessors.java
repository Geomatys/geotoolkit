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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.lang.Static;

/**
 * Utility class to obtain a propertyAccesor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public class Accessors {

    private static final PropertyAccessorFactory[] ACCESSOR_FACTORIES;

    static{
        final FactoryRegistry fr = new FactoryRegistry(PropertyAccessorFactory.class);
        final Iterator<PropertyAccessorFactory> factories = fr.getServiceProviders(PropertyAccessorFactory.class, null, null, null);

        final Set<PropertyAccessorFactory> lst = new TreeSet<PropertyAccessorFactory>(
                new Comparator<PropertyAccessorFactory>(){
                    @Override
                    public int compare(PropertyAccessorFactory t, PropertyAccessorFactory t1) {
                        return t.getPriority() - t1.getPriority();
                    }
            });

        while(factories.hasNext()){
            lst.add(factories.next());
        }

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
    public static PropertyAccessor getAccessor(Class type, String xpath, Class target){
        for(final PropertyAccessorFactory pf : ACCESSOR_FACTORIES){
            final PropertyAccessor pa = pf.createPropertyAccessor(type, xpath, target,null);
            if(pa != null) return pa;
        }
        return null;
    }

}
