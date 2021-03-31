/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class Functions {

    private final Map<String, FunctionName> functions;

    /** For JAXB. */
    protected Functions() {
        functions = Collections.emptyMap();
    }

    public Functions(final FunctionName[] functions) {
        if(functions == null){
            throw new IllegalArgumentException("Functions must not be null");
        }
        this.functions = new HashMap<>();
        for (FunctionName fn : functions) {
            this.functions.put(fn.getName(), fn);
        }
    }

    public Collection<FunctionName> getFunctionNames() {
        return functions.values();
    }

    public FunctionName getFunctionName(final String name) {
        return functions.get(name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Functions other = (Functions) obj;
        return functions.equals(other.functions);
    }

    @Override
    public int hashCode() {
        return (7*23) + functions.hashCode();
    }
}
