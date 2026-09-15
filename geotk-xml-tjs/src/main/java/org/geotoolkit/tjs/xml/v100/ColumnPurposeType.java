/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Primary purpose of an attribute column, indicating whether it contains attribute data or a
 * linkage key to some other spatial framework or nonspatial data table.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlType(name = "ColumnPurposeType")
@XmlEnum
public enum ColumnPurposeType {

    @XmlEnumValue("SpatialComponentIdentifier")
    SPATIAL_COMPONENT_IDENTIFIER,
    @XmlEnumValue("SpatialComponentProportion")
    SPATIAL_COMPONENT_PROPORTION,
    @XmlEnumValue("SpatialComponentPercentage")
    SPATIAL_COMPONENT_PERCENTAGE,
    @XmlEnumValue("TemporalIdentifier")
    TEMPORAL_IDENTIFIER,
    @XmlEnumValue("TemporalValue")
    TEMPORAL_VALUE,
    @XmlEnumValue("VerticalIdentifier")
    VERTICAL_IDENTIFIER,
    @XmlEnumValue("VerticalValue")
    VERTICAL_VALUE,
    @XmlEnumValue("OtherSpatialIdentifier")
    OTHER_SPATIAL_IDENTIFIER,
    @XmlEnumValue("NonSpatialIdentifier")
    NON_SPATIAL_IDENTIFIER,
    @XmlEnumValue("Attribute")
    ATTRIBUTE;
}
