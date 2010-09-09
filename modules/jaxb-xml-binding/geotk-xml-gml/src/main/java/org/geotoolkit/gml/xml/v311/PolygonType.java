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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * A Polygon is a special surface that is defined by a single surface patch. 
 * The boundary of this patch is coplanar and the polygon uses planar interpolation in its interior.
 * It is backwards compatible with the Polygon of GML 2, GM_Polygon of ISO 19107 is implemented by PolygonPatch.
 * 
 * <p>Java class for PolygonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolygonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractSurfaceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}exterior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}interior" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonType", propOrder = {
    "exterior",
    "interior"
})
public class PolygonType extends AbstractSurfaceType {

    @XmlElementRef(name = "exterior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<AbstractRingPropertyType> exterior;
    @XmlElementRef(name = "interior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<AbstractRingPropertyType>> interior;

    public PolygonType() {

    }
    
    public PolygonType(AbstractRingType exterior, List<? extends AbstractRingType> interiors) {
        ObjectFactory factory = new ObjectFactory();
        if (exterior != null) {
            this.exterior = factory.createExterior(new AbstractRingPropertyType(exterior));
        }
        if (interiors != null) {
            this.interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
            for (AbstractRingType inte : interiors) {
                this.interior.add(factory.createInterior(new AbstractRingPropertyType(inte)));
            }
        }
    }

    /**
     * Gets the value of the exterior property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     
     */
    public JAXBElement<AbstractRingPropertyType> getExterior() {
        return exterior;
    }

    /**
     * Sets the value of the exterior property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     
     */
    public void setExterior(JAXBElement<AbstractRingPropertyType> value) {
        this.exterior = ((JAXBElement<AbstractRingPropertyType> ) value);
    }

    /**
     * Gets the value of the interior property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<AbstractRingPropertyType>> getInterior() {
        if (interior == null) {
            interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        return this.interior;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (exterior != null) {
            sb.append("exterior:").append(exterior.getValue()).append('\n');
        } else {
            sb.append("exterior null").append('\n');
        }
        if (interior != null) {
            sb.append("interior:").append('\n');
            for (JAXBElement<AbstractRingPropertyType> inte : interior) {
                sb.append(inte).append('\n');
            }
        } else {
            sb.append("interior null").append('\n');
        }
        return sb.toString();
    }

}
