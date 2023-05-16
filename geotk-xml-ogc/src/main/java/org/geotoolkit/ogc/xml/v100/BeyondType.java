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
package org.geotoolkit.ogc.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.filter.DistanceOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Beyond")
@XmlRootElement(name = "Beyond")
public class BeyondType extends DistanceBufferType {
    public BeyondType() {
    }

    /**
     * Build a new Beyond Type
     */
    public BeyondType(final String propertyName, final Object geometry, final double distance, final String unit) {
        super(propertyName, geometry, distance, unit);
    }

    public BeyondType(final BeyondType that) {
        super(that);
    }

    @Override
    public SpatialOpsType getClone() {
        return new BeyondType(this);
    }

    @Override
    public DistanceOperatorName getOperatorType() {
        return DistanceOperatorName.BEYOND;
    }

    @Override
    public String getOperator() {
        return "Beyond";
    }
}
