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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.citygml.xml.v100.AbstractSiteType;


/**
 * Type describing the thematic and geometric attributes and the associations of buildings.
 * It is an abstract type, only its subclasses Building and BuildingPart can be instantiated.
 * An _AbstractBuilding may consist of BuildingParts, which are again _AbstractBuildings by inheritance.
 * Thus an aggregation hierarchy between _AbstractBuildings of arbitrary depth may be specified.
 * In such an hierarchy, top elements are Buildings, while all other elements are BuildingParts.
 * Each element of such a hierarchy may have all attributes and geometries of _AbstractBuildings.
 * It must, however, be assured than no inconsistencies occur
 * (for example, if the geometry of a Building does not correspond to the geometries of its parts,
 * or if the roof type of a Building is saddle roof, while its parts have an hip roof).
 * As subclass of _CityObject, an _AbstractBuilding inherits all attributes and relations,
 * in particular an id, names, external references, and generalization relations.
 * 
 * <p>Java class for AbstractBuildingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractBuildingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractSiteType">
 *       &lt;sequence>
 *         &lt;element name="class" type="{http://www.opengis.net/citygml/building/1.0}BuildingClassType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/citygml/building/1.0}BuildingFunctionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usage" type="{http://www.opengis.net/citygml/building/1.0}BuildingUsageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="yearOfConstruction" type="{http://www.w3.org/2001/XMLSchema}gYear" minOccurs="0"/>
 *         &lt;element name="yearOfDemolition" type="{http://www.w3.org/2001/XMLSchema}gYear" minOccurs="0"/>
 *         &lt;element name="roofType" type="{http://www.opengis.net/citygml/building/1.0}RoofTypeType" minOccurs="0"/>
 *         &lt;element name="measuredHeight" type="{http://www.opengis.net/gml}LengthType" minOccurs="0"/>
 *         &lt;element name="storeysAboveGround" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="storeysBelowGround" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="storeyHeightsAboveGround" type="{http://www.opengis.net/gml}MeasureOrNullListType" minOccurs="0"/>
 *         &lt;element name="storeyHeightsBelowGround" type="{http://www.opengis.net/gml}MeasureOrNullListType" minOccurs="0"/>
 *         &lt;element name="lod1Solid" type="{http://www.opengis.net/gml}SolidPropertyType" minOccurs="0"/>
 *         &lt;element name="lod1MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod1TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2Solid" type="{http://www.opengis.net/gml}SolidPropertyType" minOccurs="0"/>
 *         &lt;element name="lod2MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2MultiCurve" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod2TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="outerBuildingInstallation" type="{http://www.opengis.net/citygml/building/1.0}BuildingInstallationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="interiorBuildingInstallation" type="{http://www.opengis.net/citygml/building/1.0}IntBuildingInstallationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="boundedBy" type="{http://www.opengis.net/citygml/building/1.0}BoundarySurfacePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lod3Solid" type="{http://www.opengis.net/gml}SolidPropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3MultiCurve" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod3TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4Solid" type="{http://www.opengis.net/gml}SolidPropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiSurface" type="{http://www.opengis.net/gml}MultiSurfacePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4MultiCurve" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="lod4TerrainIntersection" type="{http://www.opengis.net/gml}MultiCurvePropertyType" minOccurs="0"/>
 *         &lt;element name="interiorRoom" type="{http://www.opengis.net/citygml/building/1.0}InteriorRoomPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="consistsOfBuildingPart" type="{http://www.opengis.net/citygml/building/1.0}BuildingPartPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.opengis.net/citygml/1.0}AddressPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/building/1.0}_GenericApplicationPropertyOfAbstractBuilding" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AbstractBuildingType", propOrder = {
    "rest"
})
@XmlSeeAlso({
    BuildingType.class,
    BuildingPartType.class
})
public abstract class AbstractBuildingType extends AbstractSiteType {

    @XmlElementRefs({
        @XmlElementRef(name = "lod4TerrainIntersection", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "storeyHeightsAboveGround", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "storeyHeightsBelowGround", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "boundedBy", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "class", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod4Solid", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod2Solid", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod3MultiCurve", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod1MultiSurface", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "address", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod3Solid", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod2MultiCurve", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "storeysBelowGround", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "measuredHeight", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "outerBuildingInstallation", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod2MultiSurface", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod2TerrainIntersection", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod3MultiSurface", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "_GenericApplicationPropertyOfAbstractBuilding", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "function", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod4MultiCurve", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod1TerrainIntersection", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "roofType", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod4MultiSurface", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "yearOfDemolition", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "interiorRoom", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "usage", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "yearOfConstruction", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "interiorBuildingInstallation", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "storeysAboveGround", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod3TerrainIntersection", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "lod1Solid", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "consistsOfBuildingPart", namespace = "http://www.opengis.net/citygml/building/1.0", type = JAXBElement.class)
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
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (rest != null && rest.size() > 0) {
            s.append("\nBuilding properties:").append('\n');
            for (JAXBElement<?> fp : rest) {
                s.append(fp.getName().getLocalPart()).append(":\n");
                s.append(fp.getValue()).append('\n');
            }
        }
        return s.toString();
    }

}
