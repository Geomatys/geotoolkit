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
package org.geotoolkit.ogc.xml.v110;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.opengis.filter.SpatialOperatorName;

/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Crosses")
@XmlRootElement(name = "Crosses")
public class CrossesType extends BinarySpatialOpType {
    /**
     * An empty constructor used by JAXB
     */
    public CrossesType() {
    }

    /**
     * Build a new Crosses Type
     */
    public CrossesType(final String propertyName, final AbstractGeometryType geometry) {
        super(propertyName, geometry);
    }

    /**
     * Build a new Crosses Type
     */
    public CrossesType(final PropertyNameType propertyName, final Object geometry) {
        super(propertyName, geometry);
    }

    public CrossesType(final CrossesType that) {
        super(that);
    }

    @Override
    public SpatialOpsType getClone() {
        return new CrossesType(this);
    }

    @Override
    public SpatialOperatorName getOperatorType() {
        return SpatialOperatorName.CROSSES;
    }

    @Override
    public String getOperator() {
        return "Crosses";
    }
}
