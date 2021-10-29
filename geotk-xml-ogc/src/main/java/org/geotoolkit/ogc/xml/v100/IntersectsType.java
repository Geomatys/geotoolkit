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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.SpatialOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Intersects")
public class IntersectsType extends BinarySpatialOpType {

    public IntersectsType() {
    }

    public IntersectsType(String propertyName, Object geometry) {
        super(propertyName, geometry);
    }

    public IntersectsType(final IntersectsType that) {
        super(that);
    }

    @Override
    public SpatialOpsType getClone() {
        return new IntersectsType(this);
    }

    @Override
    public SpatialOperatorName getOperatorType() {
        return SpatialOperatorName.INTERSECTS;
    }

    @Override
    public String getOperator() {
        return "Intersects";
    }
}