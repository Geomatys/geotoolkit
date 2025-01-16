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

import org.opengis.filter.Expression;
import org.opengis.filter.TemporalOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimeAfterType extends BinaryTemporalOpType {
    /**
     * An empty constructor used by JAXB
     */
    public TimeAfterType() {
    }

    public TimeAfterType(final TimeAfterType that) {
        super(that);
    }

    /**
     * Build a new TIme After Type
     */
    public TimeAfterType(final String propertyName, final Expression<?,?> temporal) {
        super(propertyName, temporal);
    }

    @Override
    public TemporalOpsType getClone() {
        return new TimeAfterType(this);
    }

    @Override
    public TemporalOperatorName getOperatorType() {
        return TemporalOperatorName.AFTER;
    }
}
