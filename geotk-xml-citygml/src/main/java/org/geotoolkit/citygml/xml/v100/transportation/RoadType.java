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
package org.geotoolkit.citygml.xml.v100.transportation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type describing the class for roads. As subclass of _CityObject, a Road inherits all attributes and
 *                 relations, in particular an id, names, external references, and generalization relations.
 *
 * <p>Java class for RoadType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RoadType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/transportation/1.0}TransportationComplexType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/transportation/1.0}_GenericApplicationPropertyOfRoad" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoadType", propOrder = {
    "genericApplicationPropertyOfRoad"
})
public class RoadType extends TransportationComplexType {

    @XmlElement(name = "_GenericApplicationPropertyOfRoad")
    protected List<Object> genericApplicationPropertyOfRoad;

    /**
     * Gets the value of the genericApplicationPropertyOfRoad property.
     */
    public List<Object> getGenericApplicationPropertyOfRoad() {
        if (genericApplicationPropertyOfRoad == null) {
            genericApplicationPropertyOfRoad = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfRoad;
    }

}
