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

package org.geotoolkit.filter.capability;

import java.util.List;
import org.opengis.filter.capability.FunctionName;

/**
 * Immutable function name.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFunctionName implements FunctionName{

    private final String name;
    private final List<String> argNames;
    private final int size;

    public DefaultFunctionName(String name, List<String> argNames, int size) {
        this.name = name;
        this.argNames = argNames;
        this.size = size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getArgumentCount() {
        return size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> getArgumentNames() {
        return argNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return name;
    }

}
