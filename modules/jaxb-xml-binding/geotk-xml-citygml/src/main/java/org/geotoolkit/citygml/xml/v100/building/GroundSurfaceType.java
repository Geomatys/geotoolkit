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
 * <p>Java class for GroundSurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GroundSurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/building/1.0}AbstractBoundarySurfaceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfGroundSurface" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroundSurfaceType", propOrder = {
    "genericApplicationPropertyOfGroundSurface"
})
public class GroundSurfaceType extends AbstractBoundarySurfaceType {

    @XmlElement(name = "_GenericApplicationPropertyOfGroundSurface")
    private List<Object> genericApplicationPropertyOfGroundSurface;

    /**
     * Gets the value of the genericApplicationPropertyOfGroundSurface property.
     */
    public List<Object> getGenericApplicationPropertyOfGroundSurface() {
        if (genericApplicationPropertyOfGroundSurface == null) {
            genericApplicationPropertyOfGroundSurface = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfGroundSurface;
    }

}
