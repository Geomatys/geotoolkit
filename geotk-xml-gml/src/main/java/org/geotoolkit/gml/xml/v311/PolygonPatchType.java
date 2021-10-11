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
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * A PolygonPatch is a surface patch that is defined by a set of boundary curves and an underlying surface to which these curves adhere.
 * The curves are coplanar and the polygon uses planar interpolation in its interior.
 * Implements GM_Polygon of ISO 19107.
 *
 * <p>Java class for PolygonPatchType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PolygonPatchType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractSurfacePatchType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}exterior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}interior" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="interpolation" type="{http://www.opengis.net/gml}SurfaceInterpolationType" fixed="planar" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonPatchType", propOrder = {
    "exterior",
    "interior"
})
public class PolygonPatchType extends AbstractSurfacePatchType {

    @XmlElementRef(name = "exterior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<AbstractRingPropertyType> exterior;
    @XmlElementRef(name = "interior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<AbstractRingPropertyType>> interior;
    @XmlAttribute
    private SurfaceInterpolationType interpolation;

    PolygonPatchType() {}

    public PolygonPatchType(final SurfaceInterpolationType interpolation, final AbstractRingType exterior, final List<? extends AbstractRingType> interiors) {
        this.interpolation = interpolation;
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
    public JAXBElement<AbstractRingPropertyType> getJbExterior() {
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
    public void setJbExterior(final JAXBElement<AbstractRingPropertyType> value) {
        this.exterior = ((JAXBElement<AbstractRingPropertyType> ) value);
    }

    /**
     * Gets the value of the exterior property.
     *
     * @return
     *     possible object is
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public AbstractRingPropertyType getExterior() {
        if (exterior != null) {
            return exterior.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the exterior property.
     *
     * @param value
     *     allowed object is
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public void setExterior(final AbstractRingPropertyType value) {
        ObjectFactory factory = new ObjectFactory();
        this.exterior =  factory.createExterior(value);
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

    /**
     * Sets the value of the interior property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     *
     */
    public void setJbInterior(final List<JAXBElement<AbstractRingPropertyType>> interior) {
        this.interior = interior;
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
    public List<AbstractRingPropertyType> getInterior() {
        if (interior == null) {
            interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
        }
        final List<AbstractRingPropertyType> result = new ArrayList<AbstractRingPropertyType>();
        for (JAXBElement<AbstractRingPropertyType> jb : interior) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Sets the value of the interior property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     *
     */
    public void setInterior(final List<AbstractRingPropertyType> interior) {
        if (interior != null) {
            if (this.interior == null) {
                this.interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            for (AbstractRingPropertyType jb : interior) {
                this.interior.add(factory.createInterior(jb));
            }
        } else {
            this.interior = null;
        }
    }

    /**
     * Sets the value of the interior property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *
     *
     */
    public void setInterior(final AbstractRingPropertyType interior) {
        if (interior != null) {
            if (this.interior == null) {
                this.interior = new ArrayList<JAXBElement<AbstractRingPropertyType>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            this.interior.add(factory.createInterior(interior));
        }
    }

    /**
     * Gets the value of the interpolation property.
     *
     * @return
     *     possible object is
     *     {@link SurfaceInterpolationType }
     *
     */
    public SurfaceInterpolationType getInterpolation() {
        /*if (interpolation == null) {
            return SurfaceInterpolationType.PLANAR;
        } else {*/
        return interpolation;
    }

    /**
     * Sets the value of the interpolation property.
     *
     * @param value
     *     allowed object is
     *     {@link SurfaceInterpolationType }
     *
     */
    public void setInterpolation(final SurfaceInterpolationType value) {
        this.interpolation = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PolygonPatchType) {
            final PolygonPatchType that = (PolygonPatchType) object;

            return Objects.equals(this.getExterior(),    that.getExterior()) &&
                   Objects.equals(this.getInterior(),    that.getInterior()) &&
                   Objects.equals(this.interpolation,    that.interpolation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.getExterior() != null ? this.getExterior().hashCode() : 0);
        hash = 37 * hash + (this.getInterior() != null ? this.getInterior().hashCode() : 0);
        hash = 37 * hash + (this.interpolation != null ? this.interpolation.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[PolygonPatchType]").append("\n");
        if (interpolation != null) {
            sb.append("interpolation:").append(interpolation).append('\n');
        }
        if (interior != null) {
            sb.append("interior:").append('\n');
            for (JAXBElement<AbstractRingPropertyType> inte : interior) {
                sb.append(inte.getValue()).append('\n');
            }
        }
        if (exterior != null) {
            sb.append("exterior:").append(exterior.getValue()).append('\n');
        }
        return sb.toString();
    }

}
