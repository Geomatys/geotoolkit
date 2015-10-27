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

import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;


/**
 * <p>Java class for SurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractSurfaceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}patches"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SurfaceType", propOrder = {
    "patches"
})
@XmlSeeAlso({
    TinType.class
})
@XmlRootElement(name = "Surface")
public class SurfaceType extends AbstractSurfaceType {

    @XmlElementRef(name = "patches", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<? extends SurfacePatchArrayPropertyType> patches;

    /**
     * This element encapsulates the patches of the surface.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SurfacePatchArrayPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrianglePatchArrayPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonPatchArrayPropertyType }{@code >}
     *
     */
    public JAXBElement<? extends SurfacePatchArrayPropertyType> getJbPatches() {
        return patches;
    }

    /**
     * This element encapsulates the patches of the surface.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SurfacePatchArrayPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TrianglePatchArrayPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PolygonPatchArrayPropertyType }{@code >}
     *
     */
    public void setJbPatches(final JAXBElement<SurfacePatchArrayPropertyType> value) {
        this.patches = ((JAXBElement<SurfacePatchArrayPropertyType> ) value);
    }

    /**
     * This element encapsulates the patches of the surface.
     *
     * @return
     *     possible object is
     *     {@code <}{@link SurfacePatchArrayPropertyType }{@code >}
     *     {@code <}{@link TrianglePatchArrayPropertyType }{@code >}
     *     {@code <}{@link PolygonPatchArrayPropertyType }{@code >}
     *
     */
    public SurfacePatchArrayPropertyType getPatches() {
        if (patches != null) {
            return patches.getValue();
        }
        return null;
    }

    /**
     * This element encapsulates the patches of the surface.
     *
     * @param value
     *     allowed object is
     *     {@code <}{@link SurfacePatchArrayPropertyType }{@code >}
     *     {@code <}{@link TrianglePatchArrayPropertyType }{@code >}
     *     {@code <}{@link PolygonPatchArrayPropertyType }{@code >}
     *
     */
    public void setPatches(final SurfacePatchArrayPropertyType value) {
        if (value != null) {
            final ObjectFactory factory = new ObjectFactory();
            if (value instanceof TrianglePatchArrayPropertyType) {
                this.patches = factory.createTrianglePatches((TrianglePatchArrayPropertyType) value);
            } else if (value instanceof PolygonPatchArrayPropertyType) {
                this.patches = factory.createPolygonPatches((PolygonPatchArrayPropertyType) value);
            } else {
                this.patches = factory.createPatches( value);
            }
        } else {
            this.patches = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (patches != null) {
            s.append("patches:").append(patches.getValue()).append('\n');
        }
        return s.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof SurfaceType && super.equals(object, mode)) {
            final SurfaceType that = (SurfaceType) object;

            return Objects.equals(this.getPatches(), that.getPatches());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.getPatches() != null ? this.getPatches().hashCode() : 0);
        return hash;
    }

}
