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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.Polygon;


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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonType", propOrder = {
    "exterior",
    "interior"
})
@XmlRootElement(name = "Polygon")
public class PolygonType extends AbstractSurfaceType implements Polygon {

    @XmlElementRef(name = "exterior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<AbstractRingPropertyType> exterior;
    @XmlElementRef(name = "interior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<AbstractRingPropertyType>> interior;

    public PolygonType() {

    }

    public PolygonType(final String srsName, final AbstractRingType exterior, final List<? extends AbstractRingType> interiors) {
        super(srsName);
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
        if (exterior != null) {
            return exterior.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the exterior property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public JAXBElement<AbstractRingPropertyType> getJbExterior() {
        return exterior;
    }

    /**
     * Sets the value of the exterior property.
     *
     * @param value allowed object is {@link AbstractRingPropertyType }
     */
    public void setExterior(final AbstractRingPropertyType value) {
        final ObjectFactory factory = new ObjectFactory();
        this.exterior = factory.createExterior(value);
    }

    /**
     * Sets the value of the exterior property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public void setJbExterior(final JAXBElement<AbstractRingPropertyType> value) {
        this.exterior = ((JAXBElement<AbstractRingPropertyType> ) value);
    }

    /**
     * Gets the value of the interior property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     *
     */
    @Override
    public List<AbstractRingPropertyType> getInterior() {
        final List<AbstractRingPropertyType> result = new ArrayList<AbstractRingPropertyType>();
        if (interior == null) {
            interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        for (JAXBElement<AbstractRingPropertyType> inte : interior) {
            result.add(inte.getValue());
        }
        return result;
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
    public List<JAXBElement<AbstractRingPropertyType>> getJbInterior() {
        if (interior == null) {
            interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        return this.interior;
    }

    public void setJbInterior(final List<JAXBElement<AbstractRingPropertyType>> interior) {
        this.interior = interior;
    }

    public void setInterior(final List<AbstractRingPropertyType> interior) {
        if (this.interior == null) {
            this.interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        if (interior != null) {
            final ObjectFactory factory = new ObjectFactory();
            for (AbstractRingPropertyType ring : interior) {
                this.interior.add(factory.createInterior(ring));
            }
        }
    }

    public void setInterior(final AbstractRingPropertyType interior) {
        if (this.interior == null) {
            this.interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        if (interior != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.interior.add(factory.createInterior(interior));
        }
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
                sb.append(inte.getValue()).append('\n');
            }
        } else {
            sb.append("interior null").append('\n');
        }
        return sb.toString();
    }

}
