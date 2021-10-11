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

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.geotoolkit.citygml.xml.v100.AddressPropertyType;
import org.geotoolkit.gml.xml.v311.LengthType;
import org.geotoolkit.gml.xml.v311.MeasureOrNullListType;
import org.geotoolkit.gml.xml.v311.MultiCurvePropertyType;
import org.geotoolkit.gml.xml.v311.MultiSurfacePropertyType;
import org.geotoolkit.gml.xml.v311.SolidPropertyType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.citygml.building._1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _GenericApplicationPropertyOfInteriorWallSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfInteriorWallSurface");
    private static final QName _GroundSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "GroundSurface");
    private static final QName _Door_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "Door");
    private static final QName _GenericApplicationPropertyOfRoom_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfRoom");
    private static final QName _GenericApplicationPropertyOfRoofSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfRoofSurface");
    private static final QName _Building_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "Building");
    private static final QName _GenericApplicationPropertyOfBoundarySurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfBoundarySurface");
    private static final QName _CeilingSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "CeilingSurface");
    private static final QName _FloorSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "FloorSurface");
    private static final QName _GenericApplicationPropertyOfIntBuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfIntBuildingInstallation");
    private static final QName _BuildingPart_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "BuildingPart");
    private static final QName _Room_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "Room");
    private static final QName _Opening_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_Opening");
    private static final QName _WallSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "WallSurface");
    private static final QName _GenericApplicationPropertyOfWindow_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfWindow");
    private static final QName _ClosureSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "ClosureSurface");
    private static final QName _InteriorWallSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "InteriorWallSurface");
    private static final QName _GenericApplicationPropertyOfBuildingFurniture_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfBuildingFurniture");
    private static final QName _GenericApplicationPropertyOfCeilingSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfCeilingSurface");
    private static final QName _GenericApplicationPropertyOfGroundSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfGroundSurface");
    private static final QName _BuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "BuildingInstallation");
    private static final QName _GenericApplicationPropertyOfBuilding_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfBuilding");
    private static final QName _BoundarySurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_BoundarySurface");
    private static final QName _GenericApplicationPropertyOfFloorSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfFloorSurface");
    private static final QName _RoofSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "RoofSurface");
    private static final QName _GenericApplicationPropertyOfBuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfBuildingInstallation");
    private static final QName _GenericApplicationPropertyOfClosureSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfClosureSurface");
    private static final QName _BuildingFurniture_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "BuildingFurniture");
    private static final QName _GenericApplicationPropertyOfBuildingPart_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfBuildingPart");
    private static final QName _AbstractBuilding_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_AbstractBuilding");
    private static final QName _IntBuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "IntBuildingInstallation");
    private static final QName _GenericApplicationPropertyOfDoor_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfDoor");
    private static final QName _GenericApplicationPropertyOfWallSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfWallSurface");
    private static final QName _GenericApplicationPropertyOfOpening_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfOpening");
    private static final QName _Window_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "Window");
    private static final QName _GenericApplicationPropertyOfAbstractBuilding_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "_GenericApplicationPropertyOfAbstractBuilding");
    private static final QName _RoomTypeRoomInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "roomInstallation");
    private static final QName _RoomTypeLod4Solid_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod4Solid");
    private static final QName _RoomTypeUsage_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "usage");
    private static final QName _RoomTypeInteriorFurniture_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "interiorFurniture");
    private static final QName _RoomTypeClass_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "class");
    private static final QName _RoomTypeFunction_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "function");
    private static final QName _RoomTypeLod4MultiSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod4MultiSurface");
    private static final QName _RoomTypeBoundedBy_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "boundedBy");
    private static final QName _AbstractBuildingTypeLod4TerrainIntersection_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod4TerrainIntersection");
    private static final QName _AbstractBuildingTypeLod4MultiCurve_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod4MultiCurve");
    private static final QName _AbstractBuildingTypeStoreysAboveGround_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "storeysAboveGround");
    private static final QName _AbstractBuildingTypeLod1MultiSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod1MultiSurface");
    private static final QName _AbstractBuildingTypeLod2MultiCurve_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod2MultiCurve");
    private static final QName _AbstractBuildingTypeLod1TerrainIntersection_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod1TerrainIntersection");
    private static final QName _AbstractBuildingTypeInteriorBuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "interiorBuildingInstallation");
    private static final QName _AbstractBuildingTypeYearOfDemolition_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "yearOfDemolition");
    private static final QName _AbstractBuildingTypeLod3MultiSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod3MultiSurface");
    private static final QName _AbstractBuildingTypeConsistsOfBuildingPart_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "consistsOfBuildingPart");
    private static final QName _AbstractBuildingTypeLod3MultiCurve_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod3MultiCurve");
    private static final QName _AbstractBuildingTypeLod3Solid_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod3Solid");
    private static final QName _AbstractBuildingTypeLod2TerrainIntersection_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod2TerrainIntersection");
    private static final QName _AbstractBuildingTypeOuterBuildingInstallation_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "outerBuildingInstallation");
    private static final QName _AbstractBuildingTypeMeasuredHeight_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "measuredHeight");
    private static final QName _AbstractBuildingTypeLod2Solid_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod2Solid");
    private static final QName _AbstractBuildingTypeLod1Solid_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod1Solid");
    private static final QName _AbstractBuildingTypeYearOfConstruction_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "yearOfConstruction");
    private static final QName _AbstractBuildingTypeAddress_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "address");
    private static final QName _AbstractBuildingTypeStoreysBelowGround_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "storeysBelowGround");
    private static final QName _AbstractBuildingTypeRoofType_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "roofType");
    private static final QName _AbstractBuildingTypeStoreyHeightsAboveGround_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "storeyHeightsAboveGround");
    private static final QName _AbstractBuildingTypeInteriorRoom_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "interiorRoom");
    private static final QName _AbstractBuildingTypeLod2MultiSurface_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod2MultiSurface");
    private static final QName _AbstractBuildingTypeLod3TerrainIntersection_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "lod3TerrainIntersection");
    private static final QName _AbstractBuildingTypeStoreyHeightsBelowGround_QNAME = new QName("http://www.opengis.net/citygml/building/1.0", "storeyHeightsBelowGround");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.citygml.building._1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CeilingSurfaceType }
     *
     */
    public CeilingSurfaceType createCeilingSurfaceType() {
        return new CeilingSurfaceType();
    }

    /**
     * Create an instance of {@link GroundSurfaceType }
     *
     */
    public GroundSurfaceType createGroundSurfaceType() {
        return new GroundSurfaceType();
    }

    /**
     * Create an instance of {@link BuildingPartPropertyType }
     *
     */
    public BuildingPartPropertyType createBuildingPartPropertyType() {
        return new BuildingPartPropertyType();
    }

    /**
     * Create an instance of {@link BuildingPartType }
     *
     */
    public BuildingPartType createBuildingPartType() {
        return new BuildingPartType();
    }

    /**
     * Create an instance of {@link RoomType }
     *
     */
    public RoomType createRoomType() {
        return new RoomType();
    }

    /**
     * Create an instance of {@link BuildingInstallationType }
     *
     */
    public BuildingInstallationType createBuildingInstallationType() {
        return new BuildingInstallationType();
    }

    /**
     * Create an instance of {@link InteriorRoomPropertyType }
     *
     */
    public InteriorRoomPropertyType createInteriorRoomPropertyType() {
        return new InteriorRoomPropertyType();
    }

    /**
     * Create an instance of {@link BoundarySurfacePropertyType }
     *
     */
    public BoundarySurfacePropertyType createBoundarySurfacePropertyType() {
        return new BoundarySurfacePropertyType();
    }

    /**
     * Create an instance of {@link RoofSurfaceType }
     *
     */
    public RoofSurfaceType createRoofSurfaceType() {
        return new RoofSurfaceType();
    }

    /**
     * Create an instance of {@link IntBuildingInstallationType }
     *
     */
    public IntBuildingInstallationType createIntBuildingInstallationType() {
        return new IntBuildingInstallationType();
    }

    /**
     * Create an instance of {@link ClosureSurfaceType }
     *
     */
    public ClosureSurfaceType createClosureSurfaceType() {
        return new ClosureSurfaceType();
    }

    /**
     * Create an instance of {@link BuildingType }
     *
     */
    public BuildingType createBuildingType() {
        return new BuildingType();
    }

    /**
     * Create an instance of {@link DoorType }
     *
     */
    public DoorType createDoorType() {
        return new DoorType();
    }

    /**
     * Create an instance of {@link InteriorWallSurfaceType }
     *
     */
    public InteriorWallSurfaceType createInteriorWallSurfaceType() {
        return new InteriorWallSurfaceType();
    }

    /**
     * Create an instance of {@link WallSurfaceType }
     *
     */
    public WallSurfaceType createWallSurfaceType() {
        return new WallSurfaceType();
    }

    /**
     * Create an instance of {@link BuildingInstallationPropertyType }
     *
     */
    public BuildingInstallationPropertyType createBuildingInstallationPropertyType() {
        return new BuildingInstallationPropertyType();
    }

    /**
     * Create an instance of {@link IntBuildingInstallationPropertyType }
     *
     */
    public IntBuildingInstallationPropertyType createIntBuildingInstallationPropertyType() {
        return new IntBuildingInstallationPropertyType();
    }

    /**
     * Create an instance of {@link OpeningPropertyType }
     *
     */
    public OpeningPropertyType createOpeningPropertyType() {
        return new OpeningPropertyType();
    }

    /**
     * Create an instance of {@link WindowType }
     *
     */
    public WindowType createWindowType() {
        return new WindowType();
    }

    /**
     * Create an instance of {@link BuildingFurnitureType }
     *
     */
    public BuildingFurnitureType createBuildingFurnitureType() {
        return new BuildingFurnitureType();
    }

    /**
     * Create an instance of {@link InteriorFurniturePropertyType }
     *
     */
    public InteriorFurniturePropertyType createInteriorFurniturePropertyType() {
        return new InteriorFurniturePropertyType();
    }

    /**
     * Create an instance of {@link FloorSurfaceType }
     *
     */
    public FloorSurfaceType createFloorSurfaceType() {
        return new FloorSurfaceType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfInteriorWallSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfInteriorWallSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfInteriorWallSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroundSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "GroundSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<GroundSurfaceType> createGroundSurface(final GroundSurfaceType value) {
        return new JAXBElement<GroundSurfaceType>(_GroundSurface_QNAME, GroundSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "Door", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_Opening")
    public JAXBElement<DoorType> createDoor(final DoorType value) {
        return new JAXBElement<DoorType>(_Door_QNAME, DoorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfRoom")
    public JAXBElement<Object> createGenericApplicationPropertyOfRoom(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfRoom_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfRoofSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfRoofSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfRoofSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "Building", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_AbstractBuilding")
    public JAXBElement<BuildingType> createBuilding(final BuildingType value) {
        return new JAXBElement<BuildingType>(_Building_QNAME, BuildingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfBoundarySurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfBoundarySurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfBoundarySurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CeilingSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "CeilingSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<CeilingSurfaceType> createCeilingSurface(final CeilingSurfaceType value) {
        return new JAXBElement<CeilingSurfaceType>(_CeilingSurface_QNAME, CeilingSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FloorSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "FloorSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<FloorSurfaceType> createFloorSurface(final FloorSurfaceType value) {
        return new JAXBElement<FloorSurfaceType>(_FloorSurface_QNAME, FloorSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfIntBuildingInstallation")
    public JAXBElement<Object> createGenericApplicationPropertyOfIntBuildingInstallation(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfIntBuildingInstallation_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingPartType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "BuildingPart", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_AbstractBuilding")
    public JAXBElement<BuildingPartType> createBuildingPart(final BuildingPartType value) {
        return new JAXBElement<BuildingPartType>(_BuildingPart_QNAME, BuildingPartType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoomType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "Room", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<RoomType> createRoom(final RoomType value) {
        return new JAXBElement<RoomType>(_Room_QNAME, RoomType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractOpeningType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_Opening", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<AbstractOpeningType> createOpening(final AbstractOpeningType value) {
        return new JAXBElement<AbstractOpeningType>(_Opening_QNAME, AbstractOpeningType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WallSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "WallSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<WallSurfaceType> createWallSurface(final WallSurfaceType value) {
        return new JAXBElement<WallSurfaceType>(_WallSurface_QNAME, WallSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfWindow")
    public JAXBElement<Object> createGenericApplicationPropertyOfWindow(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfWindow_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClosureSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "ClosureSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<ClosureSurfaceType> createClosureSurface(final ClosureSurfaceType value) {
        return new JAXBElement<ClosureSurfaceType>(_ClosureSurface_QNAME, ClosureSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InteriorWallSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "InteriorWallSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<InteriorWallSurfaceType> createInteriorWallSurface(final InteriorWallSurfaceType value) {
        return new JAXBElement<InteriorWallSurfaceType>(_InteriorWallSurface_QNAME, InteriorWallSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfBuildingFurniture")
    public JAXBElement<Object> createGenericApplicationPropertyOfBuildingFurniture(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfBuildingFurniture_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfCeilingSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfCeilingSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfCeilingSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfGroundSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfGroundSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfGroundSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingInstallationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "BuildingInstallation", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<BuildingInstallationType> createBuildingInstallation(final BuildingInstallationType value) {
        return new JAXBElement<BuildingInstallationType>(_BuildingInstallation_QNAME, BuildingInstallationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfBuilding")
    public JAXBElement<Object> createGenericApplicationPropertyOfBuilding(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfBuilding_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractBoundarySurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_BoundarySurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<AbstractBoundarySurfaceType> createBoundarySurface(final AbstractBoundarySurfaceType value) {
        return new JAXBElement<AbstractBoundarySurfaceType>(_BoundarySurface_QNAME, AbstractBoundarySurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfFloorSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfFloorSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfFloorSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoofSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "RoofSurface", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_BoundarySurface")
    public JAXBElement<RoofSurfaceType> createRoofSurface(final RoofSurfaceType value) {
        return new JAXBElement<RoofSurfaceType>(_RoofSurface_QNAME, RoofSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfBuildingInstallation")
    public JAXBElement<Object> createGenericApplicationPropertyOfBuildingInstallation(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfBuildingInstallation_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfClosureSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfClosureSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfClosureSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingFurnitureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "BuildingFurniture", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<BuildingFurnitureType> createBuildingFurniture(final BuildingFurnitureType value) {
        return new JAXBElement<BuildingFurnitureType>(_BuildingFurniture_QNAME, BuildingFurnitureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfBuildingPart")
    public JAXBElement<Object> createGenericApplicationPropertyOfBuildingPart(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfBuildingPart_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractBuildingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_AbstractBuilding", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_Site")
    public JAXBElement<AbstractBuildingType> createAbstractBuilding(final AbstractBuildingType value) {
        return new JAXBElement<AbstractBuildingType>(_AbstractBuilding_QNAME, AbstractBuildingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntBuildingInstallationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "IntBuildingInstallation", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<IntBuildingInstallationType> createIntBuildingInstallation(final IntBuildingInstallationType value) {
        return new JAXBElement<IntBuildingInstallationType>(_IntBuildingInstallation_QNAME, IntBuildingInstallationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfDoor")
    public JAXBElement<Object> createGenericApplicationPropertyOfDoor(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfDoor_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfWallSurface")
    public JAXBElement<Object> createGenericApplicationPropertyOfWallSurface(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfWallSurface_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfOpening")
    public JAXBElement<Object> createGenericApplicationPropertyOfOpening(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfOpening_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WindowType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "Window", substitutionHeadNamespace = "http://www.opengis.net/citygml/building/1.0", substitutionHeadName = "_Opening")
    public JAXBElement<WindowType> createWindow(final WindowType value) {
        return new JAXBElement<WindowType>(_Window_QNAME, WindowType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "_GenericApplicationPropertyOfAbstractBuilding")
    public JAXBElement<Object> createGenericApplicationPropertyOfAbstractBuilding(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfAbstractBuilding_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntBuildingInstallationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "roomInstallation", scope = RoomType.class)
    public JAXBElement<IntBuildingInstallationPropertyType> createRoomTypeRoomInstallation(final IntBuildingInstallationPropertyType value) {
        return new JAXBElement<IntBuildingInstallationPropertyType>(_RoomTypeRoomInstallation_QNAME, IntBuildingInstallationPropertyType.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4Solid", scope = RoomType.class)
    public JAXBElement<SolidPropertyType> createRoomTypeLod4Solid(final SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_RoomTypeLod4Solid_QNAME, SolidPropertyType.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "usage", scope = RoomType.class)
    public JAXBElement<String> createRoomTypeUsage(final String value) {
        return new JAXBElement<String>(_RoomTypeUsage_QNAME, String.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InteriorFurniturePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "interiorFurniture", scope = RoomType.class)
    public JAXBElement<InteriorFurniturePropertyType> createRoomTypeInteriorFurniture(final InteriorFurniturePropertyType value) {
        return new JAXBElement<InteriorFurniturePropertyType>(_RoomTypeInteriorFurniture_QNAME, InteriorFurniturePropertyType.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "class", scope = RoomType.class)
    public JAXBElement<String> createRoomTypeClass(final String value) {
        return new JAXBElement<String>(_RoomTypeClass_QNAME, String.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "function", scope = RoomType.class)
    public JAXBElement<String> createRoomTypeFunction(final String value) {
        return new JAXBElement<String>(_RoomTypeFunction_QNAME, String.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4MultiSurface", scope = RoomType.class)
    public JAXBElement<MultiSurfacePropertyType> createRoomTypeLod4MultiSurface(final MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_RoomTypeLod4MultiSurface_QNAME, MultiSurfacePropertyType.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundarySurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "boundedBy", scope = RoomType.class)
    public JAXBElement<BoundarySurfacePropertyType> createRoomTypeBoundedBy(final BoundarySurfacePropertyType value) {
        return new JAXBElement<BoundarySurfacePropertyType>(_RoomTypeBoundedBy_QNAME, BoundarySurfacePropertyType.class, RoomType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4TerrainIntersection", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod4TerrainIntersection(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod4TerrainIntersection_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4MultiCurve", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod4MultiCurve(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod4MultiCurve_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "storeysAboveGround", scope = AbstractBuildingType.class)
    public JAXBElement<BigInteger> createAbstractBuildingTypeStoreysAboveGround(final BigInteger value) {
        return new JAXBElement<BigInteger>(_AbstractBuildingTypeStoreysAboveGround_QNAME, BigInteger.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod1MultiSurface", scope = AbstractBuildingType.class)
    public JAXBElement<MultiSurfacePropertyType> createAbstractBuildingTypeLod1MultiSurface(final MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_AbstractBuildingTypeLod1MultiSurface_QNAME, MultiSurfacePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod2MultiCurve", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod2MultiCurve(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod2MultiCurve_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod1TerrainIntersection", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod1TerrainIntersection(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod1TerrainIntersection_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntBuildingInstallationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "interiorBuildingInstallation", scope = AbstractBuildingType.class)
    public JAXBElement<IntBuildingInstallationPropertyType> createAbstractBuildingTypeInteriorBuildingInstallation(final IntBuildingInstallationPropertyType value) {
        return new JAXBElement<IntBuildingInstallationPropertyType>(_AbstractBuildingTypeInteriorBuildingInstallation_QNAME, IntBuildingInstallationPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "function", scope = AbstractBuildingType.class)
    public JAXBElement<String> createAbstractBuildingTypeFunction(final String value) {
        return new JAXBElement<String>(_RoomTypeFunction_QNAME, String.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "yearOfDemolition", scope = AbstractBuildingType.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractBuildingTypeYearOfDemolition(final XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AbstractBuildingTypeYearOfDemolition_QNAME, XMLGregorianCalendar.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod3MultiSurface", scope = AbstractBuildingType.class)
    public JAXBElement<MultiSurfacePropertyType> createAbstractBuildingTypeLod3MultiSurface(final MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_AbstractBuildingTypeLod3MultiSurface_QNAME, MultiSurfacePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingPartPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "consistsOfBuildingPart", scope = AbstractBuildingType.class)
    public JAXBElement<BuildingPartPropertyType> createAbstractBuildingTypeConsistsOfBuildingPart(final BuildingPartPropertyType value) {
        return new JAXBElement<BuildingPartPropertyType>(_AbstractBuildingTypeConsistsOfBuildingPart_QNAME, BuildingPartPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod3MultiCurve", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod3MultiCurve(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod3MultiCurve_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod3Solid", scope = AbstractBuildingType.class)
    public JAXBElement<SolidPropertyType> createAbstractBuildingTypeLod3Solid(final SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_AbstractBuildingTypeLod3Solid_QNAME, SolidPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "usage", scope = AbstractBuildingType.class)
    public JAXBElement<String> createAbstractBuildingTypeUsage(final String value) {
        return new JAXBElement<String>(_RoomTypeUsage_QNAME, String.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod2TerrainIntersection", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod2TerrainIntersection(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod2TerrainIntersection_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingInstallationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "outerBuildingInstallation", scope = AbstractBuildingType.class)
    public JAXBElement<BuildingInstallationPropertyType> createAbstractBuildingTypeOuterBuildingInstallation(final BuildingInstallationPropertyType value) {
        return new JAXBElement<BuildingInstallationPropertyType>(_AbstractBuildingTypeOuterBuildingInstallation_QNAME, BuildingInstallationPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4MultiSurface", scope = AbstractBuildingType.class)
    public JAXBElement<MultiSurfacePropertyType> createAbstractBuildingTypeLod4MultiSurface(final MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_RoomTypeLod4MultiSurface_QNAME, MultiSurfacePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LengthType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "measuredHeight", scope = AbstractBuildingType.class)
    public JAXBElement<LengthType> createAbstractBuildingTypeMeasuredHeight(final LengthType value) {
        return new JAXBElement<LengthType>(_AbstractBuildingTypeMeasuredHeight_QNAME, LengthType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod2Solid", scope = AbstractBuildingType.class)
    public JAXBElement<SolidPropertyType> createAbstractBuildingTypeLod2Solid(final SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_AbstractBuildingTypeLod2Solid_QNAME, SolidPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod1Solid", scope = AbstractBuildingType.class)
    public JAXBElement<SolidPropertyType> createAbstractBuildingTypeLod1Solid(final SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_AbstractBuildingTypeLod1Solid_QNAME, SolidPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundarySurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "boundedBy", scope = AbstractBuildingType.class)
    public JAXBElement<BoundarySurfacePropertyType> createAbstractBuildingTypeBoundedBy(final BoundarySurfacePropertyType value) {
        return new JAXBElement<BoundarySurfacePropertyType>(_RoomTypeBoundedBy_QNAME, BoundarySurfacePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "yearOfConstruction", scope = AbstractBuildingType.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractBuildingTypeYearOfConstruction(final XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AbstractBuildingTypeYearOfConstruction_QNAME, XMLGregorianCalendar.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "address", scope = AbstractBuildingType.class)
    public JAXBElement<AddressPropertyType> createAbstractBuildingTypeAddress(final AddressPropertyType value) {
        return new JAXBElement<AddressPropertyType>(_AbstractBuildingTypeAddress_QNAME, AddressPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod4Solid", scope = AbstractBuildingType.class)
    public JAXBElement<SolidPropertyType> createAbstractBuildingTypeLod4Solid(final SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_RoomTypeLod4Solid_QNAME, SolidPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "storeysBelowGround", scope = AbstractBuildingType.class)
    public JAXBElement<BigInteger> createAbstractBuildingTypeStoreysBelowGround(final BigInteger value) {
        return new JAXBElement<BigInteger>(_AbstractBuildingTypeStoreysBelowGround_QNAME, BigInteger.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "class", scope = AbstractBuildingType.class)
    public JAXBElement<String> createAbstractBuildingTypeClass(final String value) {
        return new JAXBElement<String>(_RoomTypeClass_QNAME, String.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "roofType", scope = AbstractBuildingType.class)
    public JAXBElement<String> createAbstractBuildingTypeRoofType(final String value) {
        return new JAXBElement<String>(_AbstractBuildingTypeRoofType_QNAME, String.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureOrNullListType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "storeyHeightsAboveGround", scope = AbstractBuildingType.class)
    public JAXBElement<MeasureOrNullListType> createAbstractBuildingTypeStoreyHeightsAboveGround(final MeasureOrNullListType value) {
        return new JAXBElement<MeasureOrNullListType>(_AbstractBuildingTypeStoreyHeightsAboveGround_QNAME, MeasureOrNullListType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InteriorRoomPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "interiorRoom", scope = AbstractBuildingType.class)
    public JAXBElement<InteriorRoomPropertyType> createAbstractBuildingTypeInteriorRoom(final InteriorRoomPropertyType value) {
        return new JAXBElement<InteriorRoomPropertyType>(_AbstractBuildingTypeInteriorRoom_QNAME, InteriorRoomPropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod2MultiSurface", scope = AbstractBuildingType.class)
    public JAXBElement<MultiSurfacePropertyType> createAbstractBuildingTypeLod2MultiSurface(final MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_AbstractBuildingTypeLod2MultiSurface_QNAME, MultiSurfacePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "lod3TerrainIntersection", scope = AbstractBuildingType.class)
    public JAXBElement<MultiCurvePropertyType> createAbstractBuildingTypeLod3TerrainIntersection(final MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_AbstractBuildingTypeLod3TerrainIntersection_QNAME, MultiCurvePropertyType.class, AbstractBuildingType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureOrNullListType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/building/1.0", name = "storeyHeightsBelowGround", scope = AbstractBuildingType.class)
    public JAXBElement<MeasureOrNullListType> createAbstractBuildingTypeStoreyHeightsBelowGround(final MeasureOrNullListType value) {
        return new JAXBElement<MeasureOrNullListType>(_AbstractBuildingTypeStoreyHeightsBelowGround_QNAME, MeasureOrNullListType.class, AbstractBuildingType.class, value);
    }

}
