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
package org.geotoolkit.citygml.xml.v100.building;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Type for windows in walls. Used in LOD3 and LOD4 only . As subclass of _CityObject, a window
 *  inherits all attributes and relations, in particular an id, names, external references, and generalization
 *  relations. 
 * 
 * <p>Java class for WindowType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WindowType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/building/1.0}AbstractOpeningType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfWindow" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WindowType", propOrder = {
    "genericApplicationPropertyOfWindow"
})
public class WindowType extends AbstractOpeningType {

    @XmlElement(name = "_GenericApplicationPropertyOfWindow")
    private List<Object> genericApplicationPropertyOfWindow;

    /**
     * Gets the value of the genericApplicationPropertyOfWindow property.
     */
    public List<Object> getGenericApplicationPropertyOfWindow() {
        if (genericApplicationPropertyOfWindow == null) {
            genericApplicationPropertyOfWindow = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfWindow;
    }

}
