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
package org.geotoolkit.gml.xml.v311modified;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * This type defines a container for an array of 
 *    polygon patches.
 * 
 * <p>Java class for PolygonPatchArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolygonPatchArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}SurfacePatchArrayPropertyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}PolygonPatch"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonPatchArrayPropertyType")
public class PolygonPatchArrayPropertyType extends SurfacePatchArrayPropertyType {

    @XmlElementRef(name = "PolygonPatch", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends PolygonPatchType>> polygonPatch;

    public PolygonPatchArrayPropertyType() {

    }

    public PolygonPatchArrayPropertyType(List<? extends PolygonPatchType> polygonPatch) {
        this.polygonPatch = new ArrayList<JAXBElement<? extends PolygonPatchType>>();

        if (polygonPatch != null) {
            ObjectFactory factory = new ObjectFactory();
            for (PolygonPatchType patch : polygonPatch) {
                this.polygonPatch.add(factory.createPolygonPatch(patch));
            }
        }
    }

    /**
     * @return the polygonPatch
     */
    public List<JAXBElement<? extends PolygonPatchType>> getPolygonPatch() {
        if (polygonPatch == null) {
            polygonPatch = new ArrayList<JAXBElement<? extends PolygonPatchType>>();
        }
        return polygonPatch;
    }

    /**
     * @param polygonPatch the polygonPatch to set
     */
    public void setPolygonPatch(List<JAXBElement<? extends PolygonPatchType>> polygonPatch) {
        this.polygonPatch = polygonPatch;
    }

}
