/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import jakarta.xml.bind.annotation.XmlRootElement;
import org.opengis.filter.SpatialOperatorName;

/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlRootElement(name = "Overlaps")
public class OverlapsType extends BinarySpatialOpType {
    /**
     * An empty constructor used by JAXB
     */
    public OverlapsType() {
    }

    /**
     * Build a new Overlaps Type
     */
    public OverlapsType(final String propertyName, final Object geometry) {
        super(propertyName, geometry);
    }

    public OverlapsType(final OverlapsType that) {
        super(that);
    }

    @Override
    public SpatialOpsType getClone() {
        return new OverlapsType(this);
    }

    @Override
    public SpatialOperatorName getOperatorType() {
        return SpatialOperatorName.OVERLAPS;
    }

    @Override
    public String getOperator() {
        return "Overlaps";
    }
}
