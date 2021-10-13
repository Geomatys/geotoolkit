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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A polyhedral surface is a surface composed
 *    of polygon surfaces connected along their common boundary
 *    curves. This differs from the surface type only in the
 *    restriction on the types of surface patches acceptable.
 *
 * <p>Java class for PolyhedralSurfaceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PolyhedralSurfaceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}SurfaceType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/gml}StandardObjectProperties"/>
 *         &lt;element ref="{http://www.opengis.net/gml}polygonPatches"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolyhedralSurfaceType")
@XmlRootElement(name = "PolyhedralSurface")
public class PolyhedralSurfaceType extends SurfaceType {

    @XmlElementRef(name = "polygonPatches", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends PolygonPatchArrayPropertyType> polygonPatches;

    PolyhedralSurfaceType() {}

    public PolyhedralSurfaceType(final PolygonPatchArrayPropertyType polygonPatches) {
        if (polygonPatches != null) {
            ObjectFactory factory = new ObjectFactory();
            this.polygonPatches = factory.createPolygonPatches(polygonPatches);
        }
    }

    /**
     * @return the polygonPatches
     */
    public JAXBElement<? extends PolygonPatchArrayPropertyType> getPolygonPatches() {
        return polygonPatches;
    }

    /**
     * @param polygonPatches the polygonPatches to set
     */
    public void setPolygonPatches(final JAXBElement<? extends PolygonPatchArrayPropertyType> polygonPatches) {
        this.polygonPatches = polygonPatches;
    }

}
