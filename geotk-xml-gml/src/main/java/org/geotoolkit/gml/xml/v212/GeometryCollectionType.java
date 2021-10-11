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
package org.geotoolkit.gml.xml.v212;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * A geometry collection must include one or more geometries,
 * referenced through geometryMember elements.
 * User-defined geometry collections that accept GML geometry classes as members must
 * instantiate--or derive from--this type.
 *
 *
 * <p>Java class for GeometryCollectionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeometryCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryCollectionBaseType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/gml}geometryMember"/>
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
@XmlType(name = "GeometryCollectionType", propOrder = {
    "geometryMember"
})
@XmlSeeAlso({
    MultiPolygonType.class,
    MultiPointType.class,
    MultiLineStringType.class
})
public class GeometryCollectionType extends AbstractGeometryCollectionBaseType {

    @XmlElementRef(name = "geometryMember", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends GeometryAssociationType>> geometryMember;

    public GeometryCollectionType() {

    }

    public GeometryCollectionType(final GeometryCollectionType that) {
        super(that);
        if (that != null && that.geometryMember != null) {
            this.geometryMember = new ArrayList<JAXBElement<? extends GeometryAssociationType>>();
            for (JAXBElement<? extends GeometryAssociationType> jb : that.geometryMember) {
                final GeometryAssociationType geom = jb.getValue().getClone();
                this.geometryMember.add(geom.getXmlElement());
            }
        }
    }

    /**
     * Gets the value of the geometryMember property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link PointMemberType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonMemberType }{@code >}
     * {@link JAXBElement }{@code <}{@link GeometryAssociationType }{@code >}
     * {@link JAXBElement }{@code <}{@link LineStringMemberType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends GeometryAssociationType>> getGeometryMember() {
        if (geometryMember == null) {
            geometryMember = new ArrayList<JAXBElement<? extends GeometryAssociationType>>();
        }
        return this.geometryMember;
    }

    @Override
    public AbstractGeometryType getClone() {
        return new GeometryCollectionType(this);
    }

}
