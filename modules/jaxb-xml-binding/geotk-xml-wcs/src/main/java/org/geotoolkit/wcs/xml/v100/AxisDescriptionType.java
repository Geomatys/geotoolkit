/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.wcs.xml.v100;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * @author Cédric Briançon (Geomatys)
 *
 * @since 3.07
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxisDescriptionType")
public class AxisDescriptionType extends AbstractDescriptionType {
    private ValueEnumBaseType values;

    public AxisDescriptionType() {}

    public AxisDescriptionType(final List<MetadataLinkType> metadataLink, final String name, final String label,
            final String description, final ValueEnumBaseType values)
    {
        super(metadataLink, name, label, description);
        this.values = values;
    }

    public ValueEnumBaseType getValues() {
        return values;
    }
}
