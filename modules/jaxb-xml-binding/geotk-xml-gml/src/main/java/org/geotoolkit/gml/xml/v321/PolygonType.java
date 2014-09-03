/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.Polygon;


/**
 * <p>Java class for PolygonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolygonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractSurfaceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}exterior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}interior" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonType", propOrder = {
    "exterior",
    "interior"
})
public class PolygonType extends AbstractSurfaceType implements Polygon {

    private AbstractRingPropertyType exterior;
    private List<AbstractRingPropertyType> interior;

    public PolygonType() {

    }
    
    public PolygonType(final String srsName, final AbstractRingType exterior, final List<? extends AbstractRingType> interiors) {
        super(srsName);
        if (exterior != null) {
            this.exterior = new AbstractRingPropertyType(exterior);
        }
        if (interiors != null) {
            this.interior = new ArrayList<AbstractRingPropertyType>();
            for (AbstractRingType inte : interiors) {
                this.interior.add(new AbstractRingPropertyType(inte));
            }
        }
    }
    
    public PolygonType(final AbstractRingType exterior, final List<? extends AbstractRingType> interiors) {
        this(null, exterior, interiors);
    }
    
    /**
     * Gets the value of the exterior property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractRingPropertyType }
     *     
     */
    @Override
    public AbstractRingPropertyType getExterior() {
        return exterior;
    }

    /**
     * Sets the value of the exterior property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractRingPropertyType }
     *     
     */
    public void setExterior(AbstractRingPropertyType value) {
        this.exterior = value;
    }

    /**
     * Gets the value of the interior property.
     * 
     */
    @Override
    public List<AbstractRingPropertyType> getInterior() {
        if (interior == null) {
            interior = new ArrayList<>();
        }
        return this.interior;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (interior != null && !interior.isEmpty()) {
            sb.append("interior:\n");
            for (AbstractRingPropertyType s : interior) {
                sb.append(s).append('\n');
            }
        }
        
        if (exterior != null) {
            sb.append("exterior:").append(exterior).append('\n');
        }
        return sb.toString();
    }
}
