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
package org.geotoolkit.citygml.xml.v100.transportation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.citygml.transportation._1 package.
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

    private static final QName _TrafficArea_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "TrafficArea");
    private static final QName _TransportationComplex_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "TransportationComplex");
    private static final QName _GenericApplicationPropertyOfSquare_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfSquare");
    private static final QName _GenericApplicationPropertyOfRoad_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfRoad");
    private static final QName _GenericApplicationPropertyOfTransportationComplex_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfTransportationComplex");
    private static final QName _GenericApplicationPropertyOfTrack_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfTrack");
    private static final QName _Railway_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "Railway");
    private static final QName _Road_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "Road");
    private static final QName _Track_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "Track");
    private static final QName _GenericApplicationPropertyOfTransportationObject_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfTransportationObject");
    private static final QName _GenericApplicationPropertyOfRailway_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfRailway");
    private static final QName _TransportationObject_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_TransportationObject");
    private static final QName _Square_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "Square");
    private static final QName _AuxiliaryTrafficArea_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "AuxiliaryTrafficArea");
    private static final QName _GenericApplicationPropertyOfTrafficArea_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfTrafficArea");
    private static final QName _GenericApplicationPropertyOfAuxiliaryTrafficArea_QNAME = new QName("http://www.opengis.net/citygml/transportation/1.0", "_GenericApplicationPropertyOfAuxiliaryTrafficArea");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.citygml.transportation._1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TrafficAreaType }
     *
     */
    public TrafficAreaType createTrafficAreaType() {
        return new TrafficAreaType();
    }

    /**
     * Create an instance of {@link RoadType }
     *
     */
    public RoadType createRoadType() {
        return new RoadType();
    }

    /**
     * Create an instance of {@link TrackType }
     *
     */
    public TrackType createTrackType() {
        return new TrackType();
    }

    /**
     * Create an instance of {@link AuxiliaryTrafficAreaPropertyType }
     *
     */
    public AuxiliaryTrafficAreaPropertyType createAuxiliaryTrafficAreaPropertyType() {
        return new AuxiliaryTrafficAreaPropertyType();
    }

    /**
     * Create an instance of {@link SquareType }
     *
     */
    public SquareType createSquareType() {
        return new SquareType();
    }

    /**
     * Create an instance of {@link TrafficAreaPropertyType }
     *
     */
    public TrafficAreaPropertyType createTrafficAreaPropertyType() {
        return new TrafficAreaPropertyType();
    }

    /**
     * Create an instance of {@link AuxiliaryTrafficAreaType }
     *
     */
    public AuxiliaryTrafficAreaType createAuxiliaryTrafficAreaType() {
        return new AuxiliaryTrafficAreaType();
    }

    /**
     * Create an instance of {@link RailwayType }
     *
     */
    public RailwayType createRailwayType() {
        return new RailwayType();
    }

    /**
     * Create an instance of {@link TransportationComplexType }
     *
     */
    public TransportationComplexType createTransportationComplexType() {
        return new TransportationComplexType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrafficAreaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "TrafficArea", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "_TransportationObject")
    public JAXBElement<TrafficAreaType> createTrafficArea(final TrafficAreaType value) {
        return new JAXBElement<TrafficAreaType>(_TrafficArea_QNAME, TrafficAreaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransportationComplexType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "TransportationComplex", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "_TransportationObject")
    public JAXBElement<TransportationComplexType> createTransportationComplex(final TransportationComplexType value) {
        return new JAXBElement<TransportationComplexType>(_TransportationComplex_QNAME, TransportationComplexType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfSquare")
    public JAXBElement<Object> createGenericApplicationPropertyOfSquare(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfSquare_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfRoad")
    public JAXBElement<Object> createGenericApplicationPropertyOfRoad(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfRoad_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfTransportationComplex")
    public JAXBElement<Object> createGenericApplicationPropertyOfTransportationComplex(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfTransportationComplex_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfTrack")
    public JAXBElement<Object> createGenericApplicationPropertyOfTrack(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfTrack_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RailwayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "Railway", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "TransportationComplex")
    public JAXBElement<RailwayType> createRailway(final RailwayType value) {
        return new JAXBElement<RailwayType>(_Railway_QNAME, RailwayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoadType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "Road", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "TransportationComplex")
    public JAXBElement<RoadType> createRoad(final RoadType value) {
        return new JAXBElement<RoadType>(_Road_QNAME, RoadType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrackType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "Track", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "TransportationComplex")
    public JAXBElement<TrackType> createTrack(final TrackType value) {
        return new JAXBElement<TrackType>(_Track_QNAME, TrackType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfTransportationObject")
    public JAXBElement<Object> createGenericApplicationPropertyOfTransportationObject(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfTransportationObject_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfRailway")
    public JAXBElement<Object> createGenericApplicationPropertyOfRailway(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfRailway_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTransportationObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_TransportationObject", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<AbstractTransportationObjectType> createTransportationObject(final AbstractTransportationObjectType value) {
        return new JAXBElement<AbstractTransportationObjectType>(_TransportationObject_QNAME, AbstractTransportationObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SquareType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "Square", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "TransportationComplex")
    public JAXBElement<SquareType> createSquare(final SquareType value) {
        return new JAXBElement<SquareType>(_Square_QNAME, SquareType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuxiliaryTrafficAreaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "AuxiliaryTrafficArea", substitutionHeadNamespace = "http://www.opengis.net/citygml/transportation/1.0", substitutionHeadName = "_TransportationObject")
    public JAXBElement<AuxiliaryTrafficAreaType> createAuxiliaryTrafficArea(final AuxiliaryTrafficAreaType value) {
        return new JAXBElement<AuxiliaryTrafficAreaType>(_AuxiliaryTrafficArea_QNAME, AuxiliaryTrafficAreaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfTrafficArea")
    public JAXBElement<Object> createGenericApplicationPropertyOfTrafficArea(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfTrafficArea_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/transportation/1.0", name = "_GenericApplicationPropertyOfAuxiliaryTrafficArea")
    public JAXBElement<Object> createGenericApplicationPropertyOfAuxiliaryTrafficArea(final Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfAuxiliaryTrafficArea_QNAME, Object.class, null, value);
    }

}
