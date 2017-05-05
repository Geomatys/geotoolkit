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

package org.geotoolkit.ogc.xml.v200;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.temporal.OverlappedBy;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimeOverlappedByType extends BinaryTemporalOpType implements OverlappedBy {

    /**
     * An empty constructor used by JAXB
     */
    public TimeOverlappedByType() {

    }

    public TimeOverlappedByType(final String propertyName, final Object temporal) {
        super(propertyName, temporal);
    }

    public TimeOverlappedByType(final TimeOverlappedByType that) {
        super(that);
    }

    @Override
    public TemporalOpsType getClone() {
        return new TimeOverlappedByType(this);
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
