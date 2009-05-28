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
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelatedTimeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelatedTimeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}TimePrimitivePropertyType">
 *       &lt;attribute name="relativePosition">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Before"/>
 *             &lt;enumeration value="After"/>
 *             &lt;enumeration value="Begins"/>
 *             &lt;enumeration value="Ends"/>
 *             &lt;enumeration value="During"/>
 *             &lt;enumeration value="Equals"/>
 *             &lt;enumeration value="Contains"/>
 *             &lt;enumeration value="Overlaps"/>
 *             &lt;enumeration value="Meets"/>
 *             &lt;enumeration value="OverlappedBy"/>
 *             &lt;enumeration value="MetBy"/>
 *             &lt;enumeration value="BegunBy"/>
 *             &lt;enumeration value="EndedBy"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelatedTimeType")
public class RelatedTimeType extends TimePrimitivePropertyType {

    @XmlAttribute
    private String relativePosition;

    /**
     * Gets the value of the relativePosition property.
     */
    public String getRelativePosition() {
        return relativePosition;
    }
}
