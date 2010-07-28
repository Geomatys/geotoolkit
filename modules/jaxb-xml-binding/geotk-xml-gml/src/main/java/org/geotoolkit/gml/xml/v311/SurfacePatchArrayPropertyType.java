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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * A container for an array of surface patches.
 * 
 * <p>Java class for SurfacePatchArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SurfacePatchArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}_SurfacePatch"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SurfacePatchArrayPropertyType", propOrder = {
    "abstractSurfacePatch"
})
@XmlSeeAlso({
    TrianglePatchArrayPropertyType.class,
    PolygonPatchArrayPropertyType.class
})
public class SurfacePatchArrayPropertyType {

    @XmlElementRef(name = "AbstractSurfacePatch", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractSurfacePatchType>> abstractSurfacePatch;

    /**
     * Gets the value of the surfacePatch property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractParametricCurveSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangleType }{@code >}
     * {@link JAXBElement }{@code <}{@link RectangleType }{@code >}
     * {@link JAXBElement }{@code <}{@link ConeType }{@code >}
     * {@link JAXBElement }{@code <}{@link CylinderType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGriddedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link SphereType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfacePatchType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonPatchType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractSurfacePatchType>> getJbAbstractSurfacePatch() {
        if (abstractSurfacePatch == null) {
            abstractSurfacePatch = new ArrayList<JAXBElement<? extends AbstractSurfacePatchType>>();
        }
        return this.abstractSurfacePatch;
    }

    /**
     * Gets the value of the surfacePatch property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractParametricCurveSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangleType }{@code >}
     * {@link JAXBElement }{@code <}{@link RectangleType }{@code >}
     * {@link JAXBElement }{@code <}{@link ConeType }{@code >}
     * {@link JAXBElement }{@code <}{@link CylinderType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGriddedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link SphereType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfacePatchType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonPatchType }{@code >}
     *
     *
     */
    public void setJbAbstractSurfacePatch(List<JAXBElement<? extends AbstractSurfacePatchType>> abstractSurfacePatch) {
        this.abstractSurfacePatch = abstractSurfacePatch;
    }

    /**
     * Gets the value of the surfacePatch property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link AbstractParametricCurveSurfaceType }{@code >}
     * {@code <}{@link TriangleType }{@code >}
     * {@code <}{@link RectangleType }{@code >}
     * {@code <}{@link ConeType }{@code >}
     * {@code <}{@link CylinderType }{@code >}
     * {@code <}{@link AbstractGriddedSurfaceType }{@code >}
     * {@code <}{@link SphereType }{@code >}
     * {@code <}{@link AbstractSurfacePatchType }{@code >}
     * {@code <}{@link PolygonPatchType }{@code >}
     *
     *
     */
    public List<? extends AbstractSurfacePatchType> getAbstractSurfacePatch() {
        if (abstractSurfacePatch == null) {
            abstractSurfacePatch = new ArrayList<JAXBElement<? extends AbstractSurfacePatchType>>();
        }
        final List<AbstractSurfacePatchType> result = new ArrayList<AbstractSurfacePatchType>();
        for (JAXBElement<? extends AbstractSurfacePatchType> jb : abstractSurfacePatch) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Gets the value of the surfacePatch property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link AbstractParametricCurveSurfaceType }{@code >}
     * {@code <}{@link TriangleType }{@code >}
     * {@code <}{@link RectangleType }{@code >}
     * {@code <}{@link ConeType }{@code >}
     * {@code <}{@link CylinderType }{@code >}
     * {@code <}{@link AbstractGriddedSurfaceType }{@code >}
     * {@code <}{@link SphereType }{@code >}
     * {@code <}{@link AbstractSurfacePatchType }{@code >}
     * {@code <}{@link PolygonPatchType }{@code >}
     *
     *
     */
    public void setAbstractSurfacePatch(AbstractSurfacePatchType abstractSurfacePatch) {
        if (abstractSurfacePatch != null) {
            if (this.abstractSurfacePatch == null) {
                this.abstractSurfacePatch = new ArrayList<JAXBElement<? extends AbstractSurfacePatchType>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            if (abstractSurfacePatch instanceof TriangleType) {
                this.abstractSurfacePatch.add(factory.createTriangle((TriangleType)abstractSurfacePatch));
            } else if (abstractSurfacePatch instanceof RectangleType) {
                this.abstractSurfacePatch.add(factory.createRectangle((RectangleType)abstractSurfacePatch));
            } else if (abstractSurfacePatch instanceof ConeType) {
                this.abstractSurfacePatch.add(factory.createCone((ConeType)abstractSurfacePatch));
            } else if (abstractSurfacePatch instanceof CylinderType) {
                this.abstractSurfacePatch.add(factory.createCylinder((CylinderType)abstractSurfacePatch));
            } else if (abstractSurfacePatch instanceof SphereType) {
                this.abstractSurfacePatch.add(factory.createSphere((SphereType)abstractSurfacePatch));
            } else if (abstractSurfacePatch instanceof PolygonPatchType) {
                this.abstractSurfacePatch.add(factory.createPolygonPatch((PolygonPatchType)abstractSurfacePatch));
            }
        } else {
            this.abstractSurfacePatch = null;
        }
    }

     /**
     * Gets the value of the surfacePatch property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link AbstractParametricCurveSurfaceType }{@code >}
     * {@code <}{@link TriangleType }{@code >}
     * {@code <}{@link RectangleType }{@code >}
     * {@code <}{@link ConeType }{@code >}
     * {@code <}{@link CylinderType }{@code >}
     * {@code <}{@link AbstractGriddedSurfaceType }{@code >}
     * {@code <}{@link SphereType }{@code >}
     * {@code <}{@link AbstractSurfacePatchType }{@code >}
     * {@code <}{@link PolygonPatchType }{@code >}
     *
     *
     */
    public void setAbstractSurfacePatch(List<? extends AbstractSurfacePatchType> abstractSurfacePatch) {
        if (abstractSurfacePatch != null) {
            this.abstractSurfacePatch = new ArrayList<JAXBElement<? extends AbstractSurfacePatchType>>();
            final ObjectFactory factory = new ObjectFactory();
            for (AbstractSurfacePatchType jb : abstractSurfacePatch) {
                if (jb instanceof TriangleType) {
                    this.abstractSurfacePatch.add(factory.createTriangle((TriangleType)jb));
                } else if (jb instanceof RectangleType) {
                    this.abstractSurfacePatch.add(factory.createRectangle((RectangleType)jb));
                } else if (jb instanceof ConeType) {
                    this.abstractSurfacePatch.add(factory.createCone((ConeType)jb));
                } else if (jb instanceof CylinderType) {
                    this.abstractSurfacePatch.add(factory.createCylinder((CylinderType)jb));
                } else if (jb instanceof SphereType) {
                    this.abstractSurfacePatch.add(factory.createSphere((SphereType)jb));
                } else if (jb instanceof PolygonPatchType) {
                    this.abstractSurfacePatch.add(factory.createPolygonPatch((PolygonPatchType)jb));
                }
            }
        } else {
            this.abstractSurfacePatch = null;
        }
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[SurfacePatchArrayPropertyType]").append('\n');
        if (abstractSurfacePatch != null) {
            s.append(" abstractSurfacePatch:");
            for (JAXBElement<? extends AbstractSurfacePatchType>  sp: abstractSurfacePatch) {
                s.append("patches:").append(sp.getValue()).append('\n');
            }
        }
        return s.toString();
    }

}
