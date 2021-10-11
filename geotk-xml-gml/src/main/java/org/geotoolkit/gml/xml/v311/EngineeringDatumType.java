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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.EngineeringDatum;


/**
 * An engineering datum defines the origin of an engineering coordinate reference system,
 * and is used in a region around that origin.
 * This origin can be fixed with respect to the earth (such as a defined point at a construction site),
 * or be a defined point on a moving vehicle (such as on a ship or satellite).
 *
 * <p>Java class for EngineeringDatumType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EngineeringDatumType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractDatumType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EngineeringDatumType")
public class EngineeringDatumType extends AbstractDatumType implements EngineeringDatum {

    public EngineeringDatumType() {

    }

    public EngineeringDatumType(final String id, final String datumName, final CodeType anchorPoint) {
        super(id, datumName, anchorPoint);
    }

}
