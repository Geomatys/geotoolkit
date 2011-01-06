/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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

import java.io.Serializable;
import java.util.Map;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.Converters;

/**
 * Accesor for Map collections.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class MapAccessorFactory implements PropertyAccessorFactory{

    private static final MapAccessor ACCESSOR = new MapAccessor();

    @Override
    public PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target, Hints hints) {
        if(ACCESSOR.canHandle(type, xpath, target)){
            return ACCESSOR;
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    private static class MapAccessor implements PropertyAccessor,Serializable{

        @Override
        public boolean canHandle(Class clazz, String xpath, Class target) {
            return Map.class.isAssignableFrom(clazz);
        }

        @Override
        public Object get(Object object, String xpath, Class target) throws IllegalArgumentException {
            final Map map = (Map) object;
            return Converters.convert(map.get(xpath),target);
        }

        @Override
        public void set(Object object, String xpath, Object value, Class target) throws IllegalArgumentException {
            final Map map = (Map) object;
            map.put(xpath, Converters.convert(value, target));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            //unique instance of this class
            return this == obj;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }
        
    }

}
