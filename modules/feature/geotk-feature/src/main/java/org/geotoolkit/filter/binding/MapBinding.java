/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2013, Geomatys
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

import java.io.Serializable;
import java.util.Map;
import org.geotoolkit.util.Converters;

/**
 * Binding for Maps.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class MapBinding extends AbstractBinding<Map> implements Serializable{

    public MapBinding() {
        super(Map.class, 1);
    }

    @Override
    public boolean support(String xpath) {
        return true;
    }

    @Override
    public <T> T get(Map candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        return Converters.convert(candidate.get(xpath),target);
    }

    @Override
    public void set(Map candidate, String xpath, Object value) throws IllegalArgumentException {
        candidate.put(xpath, value);
    }
    
}
