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

import org.opengis.filter.TemporalOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimeAnyInteractsType extends BinaryTemporalOpType {
    /**
     * An empty constructor used by JAXB
     */
    public TimeAnyInteractsType() {
    }

    public TimeAnyInteractsType(final String propertyName, final Object temporal) {
        super(propertyName, temporal);
    }

    public TimeAnyInteractsType(final TimeAnyInteractsType that) {
        super(that);
    }

    @Override
    public TemporalOpsType getClone() {
        return new TimeAnyInteractsType(this);
    }

    @Override
    public TemporalOperatorName getOperatorType() {
        return TemporalOperatorName.ANY_INTERACTS;
    }
}
