/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.ogc.xml.v110;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.temporal.Meets;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimeMeetsType extends BinaryTemporalOpType implements Meets {

    /**
     * An empty constructor used by JAXB
     */
    public TimeMeetsType() {

    }

    public TimeMeetsType(final String propertyName, final Object temporal) {
        super(propertyName, temporal);
    }

    @Override
    public boolean evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(FilterVisitor fv, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
