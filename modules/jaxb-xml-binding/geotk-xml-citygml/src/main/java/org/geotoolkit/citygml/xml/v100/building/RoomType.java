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
package org.geotoolkit.citygml.xml.v100.building;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractCityObjectType;


/**
 * A Room is a thematic object for modelling the closed parts inside a building. It has to be closed,
 * if necessary by using closure surfaces. The geometry may be either a solid, or a MultiSurface if the boundary is
 * not topologically clean. The room connectivity may be derived by detecting shared thematic openings or closure
 * surfaces: two rooms are connected if both use the same opening object or the same closure surface. The thematic
 * surfaces bounding a room are referenced by the boundedBy property. As subclass of _CityObject, a Room inherits all
 * attributes and relations, in particular an id, names, external references, and generalization relations.
 *             
 * 
 * <p>Java class for RoomType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoomType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/building/1.0}RoomClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/building/1.0}RoomFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/building/1.0}RoomUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod4Solid" type="{http://www.opengis.net/gml}SolidPropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="boundedBy" type="{http://www.opengis.net/citygml/building/1.0}BoundarySurfacePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="interiorFurniture" type="{http://www.opengis.net/citygml/building/1.0}InteriorFurniturePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="roomInstallation" type="{http://www.opengis.net/citygml/building/1.0}IntBuildingInstallationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfRoom" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "RoomType", propOrder = {
    "rest"
})
public class RoomType
    extends AbstractCityObjectType
{

    @XmlElementRefs({
        @XmlElementRef(name = "class", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod4MultiSurface", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod4Solid", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "boundedBy", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "roomInstallation", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "interiorFurniture", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "_GenericApplicationPropertyOfRoom", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "function", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "usage", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> rest;

    /**
     * Gets the rest of the content model. 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
