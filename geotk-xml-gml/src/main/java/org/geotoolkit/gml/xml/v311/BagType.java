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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * A non-abstract generic collection type that can be used as a document element for a collection of any GML types - Geometries, Topologies, Features ...
 *
 * FeatureCollections may only contain Features.  GeometryCollections may only contain Geometrys.
 * Bags are less constrained  they must contain objects that are substitutable for gml:_Object.
 * This may mix several levels, including Features, Definitions, Dictionaries, Geometries etc.
 *
 * The content model would ideally be
 *    member 0..*
 *    members 0..1
 *    member 0..*
 * for maximum flexibility in building a collection from both homogeneous and distinct components:
 * included "member" elements each contain a single Object
 * an included "members" element contains a set of Objects
 *
 * However, this is non-deterministic, thus prohibited by XSD.
 *
 * <p>Java class for BagType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BagType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}member" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}members" minOccurs="0"/>
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
@XmlType(name = "BagType", propOrder = {
    "member",
    "members"
})
public class BagType extends AbstractGMLType {

    private List<AssociationType> member;
    private ArrayAssociationType members;

    /**
     * Gets the value of the member property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link AssociationType }
     *
     *
     */
    public List<AssociationType> getMember() {
        if (member == null) {
            member = new ArrayList<AssociationType>();
        }
        return this.member;
    }

    /**
     * Gets the value of the members property.
     *
     * @return
     *     possible object is
     *     {@link ArrayAssociationType }
     *
     */
    public ArrayAssociationType getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     *
     * @param value
     *     allowed object is
     *     {@link ArrayAssociationType }
     *
     */
    public void setMembers(final ArrayAssociationType value) {
        this.members = value;
    }

}
