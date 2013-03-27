/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.filter.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.factory.FactoryRegistry;

/**
 * Utility class to access bindings.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class Bindings {

    private static final Binding[] BINDINGS;
    
    private Bindings() {}
    
    static{
        final FactoryRegistry fr = new FactoryRegistry(Binding.class);
        final Iterator<Binding> factories = fr.getServiceProviders(Binding.class, null, null, null);

        final List<Binding> lst = new ArrayList<Binding>();
        while(factories.hasNext()){
            lst.add(factories.next());
        }

        Collections.sort(lst, new Comparator<Binding>(){
            @Override
            public int compare(Binding t, Binding t1) {
                return t1.getPriority() - t.getPriority();
            }
        });

        BINDINGS = lst.toArray(new Binding[lst.size()]);
    }

    /**
     * Find binding for given glass and path.
     * 
     * @param <C> binding class
     * @param type binding class
     * @param xpath searched path
     * @param target output class
     * @return Binding or null if not found
     */
    public static <C> Binding<C> getBinding(final Class<C> type, final String xpath){
        for(Binding b : BINDINGS){
            if(b.getBindingClass().isAssignableFrom(type) && b.support(xpath)){
                return b;
            }
        }
        return null;
    }

    /**
     * Get an array of all available bindings.
     * @return Binding[], never null
     */
    public static Binding[] getBindings(){
        return BINDINGS.clone();
    }
    
}
