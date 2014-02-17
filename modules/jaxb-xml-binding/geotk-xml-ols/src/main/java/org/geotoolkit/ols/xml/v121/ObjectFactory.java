/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.EnvelopeType;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.xls package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RouteMapRequest_QNAME = new QName("http://www.opengis.net/xls", "RouteMapRequest");
    private final static QName _Altitude_QNAME = new QName("http://www.opengis.net/xls", "Altitude");
    private final static QName _StreetLocation_QNAME = new QName("http://www.opengis.net/xls", "_StreetLocation");
    private final static QName _RoutePlan_QNAME = new QName("http://www.opengis.net/xls", "RoutePlan");
    private final static QName _InputMSIDs_QNAME = new QName("http://www.opengis.net/xls", "InputMSIDs");
    private final static QName _ReverseGeocodeResponse_QNAME = new QName("http://www.opengis.net/xls", "ReverseGeocodeResponse");
    private final static QName _NamedReferenceSystem_QNAME = new QName("http://www.opengis.net/xls", "_NamedReferenceSystem");
    private final static QName _ReverseGeocodePreference_QNAME = new QName("http://www.opengis.net/xls", "ReverseGeocodePreference");
    private final static QName _BoundingBox_QNAME = new QName("http://www.opengis.net/xls", "BoundingBox");
    private final static QName _DetermineRouteRequest_QNAME = new QName("http://www.opengis.net/xls", "DetermineRouteRequest");
    private final static QName _POIInfo_QNAME = new QName("http://www.opengis.net/xls", "POIInfo");
    private final static QName _RouteGeometryRequest_QNAME = new QName("http://www.opengis.net/xls", "RouteGeometryRequest");
    private final static QName _NAICS_QNAME = new QName("http://www.opengis.net/xls", "NAICS");
    private final static QName _Map_QNAME = new QName("http://www.opengis.net/xls", "Map");
    private final static QName _Distance_QNAME = new QName("http://www.opengis.net/xls", "Distance");
    private final static QName _GatewayParameters_QNAME = new QName("http://www.opengis.net/xls", "_GatewayParameters");
    private final static QName _SLIA_QNAME = new QName("http://www.opengis.net/xls", "SLIA");
    private final static QName _AvoidList_QNAME = new QName("http://www.opengis.net/xls", "AvoidList");
    private final static QName _ADT_QNAME = new QName("http://www.opengis.net/xls", "_ADT");
    private final static QName _Angle_QNAME = new QName("http://www.opengis.net/xls", "Angle");
    private final static QName _SLIR_QNAME = new QName("http://www.opengis.net/xls", "SLIR");
    private final static QName _SearchCentreDistance_QNAME = new QName("http://www.opengis.net/xls", "SearchCentreDistance");
    private final static QName _PortrayMapRequest_QNAME = new QName("http://www.opengis.net/xls", "PortrayMapRequest");
    private final static QName _NACE_QNAME = new QName("http://www.opengis.net/xls", "NACE");
    private final static QName _GeocodeMatchCode_QNAME = new QName("http://www.opengis.net/xls", "GeocodeMatchCode");
    private final static QName _RequestHeader_QNAME = new QName("http://www.opengis.net/xls", "RequestHeader");
    private final static QName _InputGatewayParameters_QNAME = new QName("http://www.opengis.net/xls", "InputGatewayParameters");
    private final static QName _StartPoint_QNAME = new QName("http://www.opengis.net/xls", "StartPoint");
    private final static QName _RouteInstructionsRequest_QNAME = new QName("http://www.opengis.net/xls", "RouteInstructionsRequest");
    private final static QName _PostalCode_QNAME = new QName("http://www.opengis.net/xls", "PostalCode");
    private final static QName _AOI_QNAME = new QName("http://www.opengis.net/xls", "AOI");
    private final static QName _Location_QNAME = new QName("http://www.opengis.net/xls", "_Location");
    private final static QName _GeocodeResponse_QNAME = new QName("http://www.opengis.net/xls", "GeocodeResponse");
    private final static QName _Position_QNAME = new QName("http://www.opengis.net/xls", "Position");
    private final static QName _RouteGeometry_QNAME = new QName("http://www.opengis.net/xls", "RouteGeometry");
    private final static QName _POILocation_QNAME = new QName("http://www.opengis.net/xls", "POILocation");
    private final static QName _RoutePreference_QNAME = new QName("http://www.opengis.net/xls", "RoutePreference");
    private final static QName _GetPortrayMapCapabilitiesRequest_QNAME = new QName("http://www.opengis.net/xls", "GetPortrayMapCapabilitiesRequest");
    private final static QName _MSIDs_QNAME = new QName("http://www.opengis.net/xls", "_MSIDs");
    private final static QName _AvoidFeature_QNAME = new QName("http://www.opengis.net/xls", "AvoidFeature");
    private final static QName _Error_QNAME = new QName("http://www.opengis.net/xls", "Error");
    private final static QName _POIProperties_QNAME = new QName("http://www.opengis.net/xls", "POIProperties");
    private final static QName _AbstractPOI_QNAME = new QName("http://www.opengis.net/xls", "AbstractPOI");
    private final static QName _ResponseParameters_QNAME = new QName("http://www.opengis.net/xls", "_ResponseParameters");
    private final static QName _POIInfoList_QNAME = new QName("http://www.opengis.net/xls", "POIInfoList");
    private final static QName _MSInformation_QNAME = new QName("http://www.opengis.net/xls", "_MSInformation");
    private final static QName _DirectoryResponse_QNAME = new QName("http://www.opengis.net/xls", "DirectoryResponse");
    private final static QName _POISelectionCriteria_QNAME = new QName("http://www.opengis.net/xls", "_POISelectionCriteria");
    private final static QName _AbstractPosition_QNAME = new QName("http://www.opengis.net/xls", "AbstractPosition");
    private final static QName _ViaPoint_QNAME = new QName("http://www.opengis.net/xls", "ViaPoint");
    private final static QName _RouteInstructionsList_QNAME = new QName("http://www.opengis.net/xls", "RouteInstructionsList");
    private final static QName _Building_QNAME = new QName("http://www.opengis.net/xls", "Building");
    private final static QName _GetPortrayMapCapabilitiesResponse_QNAME = new QName("http://www.opengis.net/xls", "GetPortrayMapCapabilitiesResponse");
    private final static QName _Body_QNAME = new QName("http://www.opengis.net/xls", "_Body");
    private final static QName _RouteInstruction_QNAME = new QName("http://www.opengis.net/xls", "RouteInstruction");
    private final static QName _AbstractPOIProperty_QNAME = new QName("http://www.opengis.net/xls", "AbstractPOIProperty");
    private final static QName _RouteHandle_QNAME = new QName("http://www.opengis.net/xls", "RouteHandle");
    private final static QName _WayPoint_QNAME = new QName("http://www.opengis.net/xls", "_WayPoint");
    private final static QName _ReverseGeocodeRequest_QNAME = new QName("http://www.opengis.net/xls", "ReverseGeocodeRequest");
    private final static QName _NextSegment_QNAME = new QName("http://www.opengis.net/xls", "NextSegment");
    private final static QName _Header_QNAME = new QName("http://www.opengis.net/xls", "_Header");
    private final static QName _GeocodeRequest_QNAME = new QName("http://www.opengis.net/xls", "GeocodeRequest");
    private final static QName _InputMSInformation_QNAME = new QName("http://www.opengis.net/xls", "InputMSInformation");
    private final static QName _POI_QNAME = new QName("http://www.opengis.net/xls", "POI");
    private final static QName _SIC_QNAME = new QName("http://www.opengis.net/xls", "SIC");
    private final static QName _POIAttributeList_QNAME = new QName("http://www.opengis.net/xls", "POIAttributeList");
    private final static QName _POIProperty_QNAME = new QName("http://www.opengis.net/xls", "POIProperty");
    private final static QName _GeocodeResponseList_QNAME = new QName("http://www.opengis.net/xls", "GeocodeResponseList");
    private final static QName _OutputGatewayParameters_QNAME = new QName("http://www.opengis.net/xls", "OutputGatewayParameters");
    private final static QName _RequestParameters_QNAME = new QName("http://www.opengis.net/xls", "_RequestParameters");
    private final static QName _ResponseHeader_QNAME = new QName("http://www.opengis.net/xls", "ResponseHeader");
    private final static QName _RouteMap_QNAME = new QName("http://www.opengis.net/xls", "RouteMap");
    private final static QName _CircularArc_QNAME = new QName("http://www.opengis.net/xls", "CircularArc");
    private final static QName _OutputMSInformation_QNAME = new QName("http://www.opengis.net/xls", "OutputMSInformation");
    private final static QName _OutputMSIDs_QNAME = new QName("http://www.opengis.net/xls", "OutputMSIDs");
    private final static QName _XLS_QNAME = new QName("http://www.opengis.net/xls", "XLS");
    private final static QName _DetermineRouteResponse_QNAME = new QName("http://www.opengis.net/xls", "DetermineRouteResponse");
    private final static QName _Time_QNAME = new QName("http://www.opengis.net/xls", "Time");
    private final static QName _Ellipse_QNAME = new QName("http://www.opengis.net/xls", "Ellipse");
    private final static QName _ErrorList_QNAME = new QName("http://www.opengis.net/xls", "ErrorList");
    private final static QName _EndPoint_QNAME = new QName("http://www.opengis.net/xls", "EndPoint");
    private final static QName _ReferenceSystem_QNAME = new QName("http://www.opengis.net/xls", "ReferenceSystem");
    private final static QName _Measure_QNAME = new QName("http://www.opengis.net/xls", "_Measure");
    private final static QName _Street_QNAME = new QName("http://www.opengis.net/xls", "Street");
    private final static QName _AbstractNextSegment_QNAME = new QName("http://www.opengis.net/xls", "AbstractNextSegment");
    private final static QName _Request_QNAME = new QName("http://www.opengis.net/xls", "Request");
    private final static QName _PortrayMapResponse_QNAME = new QName("http://www.opengis.net/xls", "PortrayMapResponse");
    private final static QName _Address_QNAME = new QName("http://www.opengis.net/xls", "Address");
    private final static QName _TimeStamp_QNAME = new QName("http://www.opengis.net/xls", "TimeStamp");
    private final static QName _Speed_QNAME = new QName("http://www.opengis.net/xls", "Speed");
    private final static QName _StreetAddress_QNAME = new QName("http://www.opengis.net/xls", "StreetAddress");
    private final static QName _RouteSummary_QNAME = new QName("http://www.opengis.net/xls", "RouteSummary");
    private final static QName _Place_QNAME = new QName("http://www.opengis.net/xls", "Place");
    private final static QName _DirectoryRequest_QNAME = new QName("http://www.opengis.net/xls", "DirectoryRequest");
    private final static QName _WayPointList_QNAME = new QName("http://www.opengis.net/xls", "WayPointList");
    private final static QName _Response_QNAME = new QName("http://www.opengis.net/xls", "Response");
    private final static QName _AbstractRouteSummary_QNAME = new QName("http://www.opengis.net/xls", "AbstractRouteSummary");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.xls
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RouteInstructionsRequestType }
     * 
     */
    public RouteInstructionsRequestType createRouteInstructionsRequestType() {
        return new RouteInstructionsRequestType();
    }

    /**
     * Create an instance of {@link HorAccType }
     * 
     */
    public HorAccType createHorAccType() {
        return new HorAccType();
    }

    /**
     * Create an instance of {@link DetermineRouteRequestType }
     * 
     */
    public DetermineRouteRequestType createDetermineRouteRequestType() {
        return new DetermineRouteRequestType();
    }

    /**
     * Create an instance of {@link AbstractMSIDsType }
     * 
     */
    public AbstractMSIDsType createAbstractMSIDsType() {
        return new AbstractMSIDsType();
    }

    /**
     * Create an instance of {@link SICType }
     * 
     */
    public SICType createSICType() {
        return new SICType();
    }

    /**
     * Create an instance of {@link AvailableFormatsType }
     * 
     */
    public AvailableFormatsType createAvailableFormatsType() {
        return new AvailableFormatsType();
    }

    /**
     * Create an instance of {@link ReferenceSystemType }
     * 
     */
    public ReferenceSystemType createReferenceSystemType() {
        return new ReferenceSystemType();
    }

    /**
     * Create an instance of {@link StyleType }
     * 
     */
    public StyleType createStyleType() {
        return new StyleType();
    }

    /**
     * Create an instance of {@link BuildingLocatorType }
     * 
     */
    public BuildingLocatorType createBuildingLocatorType() {
        return new BuildingLocatorType();
    }

    /**
     * Create an instance of {@link QualityOfPositionType }
     * 
     */
    public QualityOfPositionType createQualityOfPositionType() {
        return new QualityOfPositionType();
    }

    /**
     * Create an instance of {@link GeocodeResponseListType }
     * 
     */
    public GeocodeResponseListType createGeocodeResponseListType() {
        return new GeocodeResponseListType();
    }

    /**
     * Create an instance of {@link POIWithDistanceType }
     * 
     */
    public POIWithDistanceType createPOIWithDistanceType() {
        return new POIWithDistanceType();
    }

    /**
     * Create an instance of {@link PointOfInterestType }
     * 
     */
    public PointOfInterestType createPointOfInterestType() {
        return new PointOfInterestType();
    }

    /**
     * Create an instance of {@link CircularArcType }
     * 
     */
    public CircularArcType createCircularArcType() {
        return new CircularArcType();
    }

    /**
     * Create an instance of {@link RequestType }
     * 
     */
    public RequestType createRequestType() {
        return new RequestType();
    }

    /**
     * Create an instance of {@link StreetAddressType }
     * 
     */
    public StreetAddressType createStreetAddressType() {
        return new StreetAddressType();
    }

    /**
     * Create an instance of {@link XLSType }
     * 
     */
    public XLSType createXLSType() {
        return new XLSType();
    }

    /**
     * Create an instance of {@link NACEType }
     * 
     */
    public NACEType createNACEType() {
        return new NACEType();
    }

    /**
     * Create an instance of {@link GetPortrayMapCapabilitiesResponseType }
     * 
     */
    public GetPortrayMapCapabilitiesResponseType createGetPortrayMapCapabilitiesResponseType() {
        return new GetPortrayMapCapabilitiesResponseType();
    }

    /**
     * Create an instance of {@link LayerType.Layer }
     * 
     */
    public LayerType.Layer createLayerTypeLayer() {
        return new LayerType.Layer();
    }

    /**
     * Create an instance of {@link OutputType }
     * 
     */
    public OutputType createOutputType() {
        return new OutputType();
    }

    /**
     * Create an instance of {@link OutputMSIDsType }
     * 
     */
    public OutputMSIDsType createOutputMSIDsType() {
        return new OutputMSIDsType();
    }

    /**
     * Create an instance of {@link RouteGeometryType }
     * 
     */
    public RouteGeometryType createRouteGeometryType() {
        return new RouteGeometryType();
    }

    /**
     * Create an instance of {@link GeocodeResponseType }
     * 
     */
    public GeocodeResponseType createGeocodeResponseType() {
        return new GeocodeResponseType();
    }

    /**
     * Create an instance of {@link PositionType }
     * 
     */
    public PositionType createPositionType() {
        return new PositionType();
    }

    /**
     * Create an instance of {@link InputMSIDsType }
     * 
     */
    public InputMSIDsType createInputMSIDsType() {
        return new InputMSIDsType();
    }

    /**
     * Create an instance of {@link AbstractDataType }
     * 
     */
    public AbstractDataType createAbstractDataType() {
        return new AbstractDataType();
    }

    /**
     * Create an instance of {@link POIInfoListType }
     * 
     */
    public POIInfoListType createPOIInfoListType() {
        return new POIInfoListType();
    }

    /**
     * Create an instance of {@link CenterContextType }
     * 
     */
    public CenterContextType createCenterContextType() {
        return new CenterContextType();
    }

    /**
     * Create an instance of {@link DetermineRouteResponseType }
     * 
     */
    public DetermineRouteResponseType createDetermineRouteResponseType() {
        return new DetermineRouteResponseType();
    }

    /**
     * Create an instance of {@link ClipType }
     * 
     */
    public ClipType createClipType() {
        return new ClipType();
    }

    /**
     * Create an instance of {@link RouteMapRequestType }
     * 
     */
    public RouteMapRequestType createRouteMapRequestType() {
        return new RouteMapRequestType();
    }

    /**
     * Create an instance of {@link RouteInstructionType }
     * 
     */
    public RouteInstructionType createRouteInstructionType() {
        return new RouteInstructionType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link SpeedType }
     * 
     */
    public SpeedType createSpeedType() {
        return new SpeedType();
    }

    /**
     * Create an instance of {@link ContentType }
     * 
     */
    public ContentType createContentType() {
        return new ContentType();
    }

    /**
     * Create an instance of {@link RouteMapOutputType }
     * 
     */
    public RouteMapOutputType createRouteMapOutputType() {
        return new RouteMapOutputType();
    }

    /**
     * Create an instance of {@link DirectoryResponseType }
     * 
     */
    public DirectoryResponseType createDirectoryResponseType() {
        return new DirectoryResponseType();
    }

    /**
     * Create an instance of {@link GetPortrayMapCapabilitiesRequestType }
     * 
     */
    public GetPortrayMapCapabilitiesRequestType createGetPortrayMapCapabilitiesRequestType() {
        return new GetPortrayMapCapabilitiesRequestType();
    }

    /**
     * Create an instance of {@link RouteSummaryType }
     * 
     */
    public RouteSummaryType createRouteSummaryType() {
        return new RouteSummaryType();
    }

    /**
     * Create an instance of {@link EllipseType }
     * 
     */
    public EllipseType createEllipseType() {
        return new EllipseType();
    }

    /**
     * Create an instance of {@link AbstractMSInformationType }
     * 
     */
    public AbstractMSInformationType createAbstractMSInformationType() {
        return new AbstractMSInformationType();
    }

    /**
     * Create an instance of {@link StreetNameType }
     * 
     */
    public StreetNameType createStreetNameType() {
        return new StreetNameType();
    }

    /**
     * Create an instance of {@link ReverseGeocodeResponseType }
     * 
     */
    public ReverseGeocodeResponseType createReverseGeocodeResponseType() {
        return new ReverseGeocodeResponseType();
    }

    /**
     * Create an instance of {@link AltitudeType }
     * 
     */
    public AltitudeType createAltitudeType() {
        return new AltitudeType();
    }

    /**
     * Create an instance of {@link ResponseType }
     * 
     */
    public ResponseType createResponseType() {
        return new ResponseType();
    }

    /**
     * Create an instance of {@link SLIRType }
     * 
     */
    public SLIRType createSLIRType() {
        return new SLIRType();
    }

    /**
     * Create an instance of {@link POILocationType }
     * 
     */
    public POILocationType createPOILocationType() {
        return new POILocationType();
    }

    /**
     * Create an instance of {@link InputGatewayParametersType }
     * 
     */
    public InputGatewayParametersType createInputGatewayParametersType() {
        return new InputGatewayParametersType();
    }

    /**
     * Create an instance of {@link RouteHandleType }
     * 
     */
    public RouteHandleType createRouteHandleType() {
        return new RouteHandleType();
    }

    /**
     * Create an instance of {@link ErrorListType }
     * 
     */
    public ErrorListType createErrorListType() {
        return new ErrorListType();
    }

    /**
     * Create an instance of {@link WayPointListType }
     * 
     */
    public WayPointListType createWayPointListType() {
        return new WayPointListType();
    }

    /**
     * Create an instance of {@link AvailableSRSType }
     * 
     */
    public AvailableSRSType createAvailableSRSType() {
        return new AvailableSRSType();
    }

    /**
     * Create an instance of {@link WithinDistanceType }
     * 
     */
    public WithinDistanceType createWithinDistanceType() {
        return new WithinDistanceType();
    }

    /**
     * Create an instance of {@link WayPointType }
     * 
     */
    public WayPointType createWayPointType() {
        return new WayPointType();
    }

    /**
     * Create an instance of {@link POIAttributeListType }
     * 
     */
    public POIAttributeListType createPOIAttributeListType() {
        return new POIAttributeListType();
    }

    /**
     * Create an instance of {@link NAICSType }
     * 
     */
    public NAICSType createNAICSType() {
        return new NAICSType();
    }

    /**
     * Create an instance of {@link OverlayType }
     * 
     */
    public OverlayType createOverlayType() {
        return new OverlayType();
    }

    /**
     * Create an instance of {@link RouteSegmentType }
     * 
     */
    public RouteSegmentType createRouteSegmentType() {
        return new RouteSegmentType();
    }

    /**
     * Create an instance of {@link AngleType }
     * 
     */
    public AngleType createAngleType() {
        return new AngleType();
    }

    /**
     * Create an instance of {@link VerAccType }
     * 
     */
    public VerAccType createVerAccType() {
        return new VerAccType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link POIProperties }
     * 
     */
    public POIProperties createPOIProperties() {
        return new POIProperties();
    }

    /**
     * Create an instance of {@link PortrayMapResponseType }
     * 
     */
    public PortrayMapResponseType createPortrayMapResponseType() {
        return new PortrayMapResponseType();
    }

    /**
     * Create an instance of {@link OutputGatewayParametersType }
     * 
     */
    public OutputGatewayParametersType createOutputGatewayParametersType() {
        return new OutputGatewayParametersType();
    }

    /**
     * Create an instance of {@link TimeType }
     * 
     */
    public TimeType createTimeType() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link RouteMapType }
     * 
     */
    public RouteMapType createRouteMapType() {
        return new RouteMapType();
    }

    /**
     * Create an instance of {@link LineCorridorType }
     * 
     */
    public LineCorridorType createLineCorridorType() {
        return new LineCorridorType();
    }

    /**
     * Create an instance of {@link MapType }
     * 
     */
    public MapType createMapType() {
        return new MapType();
    }

    /**
     * Create an instance of {@link AreaOfInterestType }
     * 
     */
    public AreaOfInterestType createAreaOfInterestType() {
        return new AreaOfInterestType();
    }

    /**
     * Create an instance of {@link WithinBoundaryType }
     * 
     */
    public WithinBoundaryType createWithinBoundaryType() {
        return new WithinBoundaryType();
    }

    /**
     * Create an instance of {@link RouteInstructionsListType }
     * 
     */
    public RouteInstructionsListType createRouteInstructionsListType() {
        return new RouteInstructionsListType();
    }

    /**
     * Create an instance of {@link AvoidListType }
     * 
     */
    public AvoidListType createAvoidListType() {
        return new AvoidListType();
    }

    /**
     * Create an instance of {@link InputMSInformationType }
     * 
     */
    public InputMSInformationType createInputMSInformationType() {
        return new InputMSInformationType();
    }

    /**
     * Create an instance of {@link AvailableStylesType }
     * 
     */
    public AvailableStylesType createAvailableStylesType() {
        return new AvailableStylesType();
    }

    /**
     * Create an instance of {@link NamedPlaceType }
     * 
     */
    public NamedPlaceType createNamedPlaceType() {
        return new NamedPlaceType();
    }

    /**
     * Create an instance of {@link DirectoryRequestType }
     * 
     */
    public DirectoryRequestType createDirectoryRequestType() {
        return new DirectoryRequestType();
    }

    /**
     * Create an instance of {@link GeocodeRequestType }
     * 
     */
    public GeocodeRequestType createGeocodeRequestType() {
        return new GeocodeRequestType();
    }

    /**
     * Create an instance of {@link TimeStampType }
     * 
     */
    public TimeStampType createTimeStampType() {
        return new TimeStampType();
    }

    /**
     * Create an instance of {@link DistanceType }
     * 
     */
    public DistanceType createDistanceType() {
        return new DistanceType();
    }

    /**
     * Create an instance of {@link ReverseGeocodedLocationType }
     * 
     */
    public ReverseGeocodedLocationType createReverseGeocodedLocationType() {
        return new ReverseGeocodedLocationType();
    }

    /**
     * Create an instance of {@link SLIAType }
     * 
     */
    public SLIAType createSLIAType() {
        return new SLIAType();
    }

    /**
     * Create an instance of {@link NearestType }
     * 
     */
    public NearestType createNearestType() {
        return new NearestType();
    }

    /**
     * Create an instance of {@link RequestHeaderType }
     * 
     */
    public RequestHeaderType createRequestHeaderType() {
        return new RequestHeaderType();
    }

    /**
     * Create an instance of {@link ResponseHeaderType }
     * 
     */
    public ResponseHeaderType createResponseHeaderType() {
        return new ResponseHeaderType();
    }

    /**
     * Create an instance of {@link GeocodingQOSType }
     * 
     */
    public GeocodingQOSType createGeocodingQOSType() {
        return new GeocodingQOSType();
    }

    /**
     * Create an instance of {@link POIInfoType }
     * 
     */
    public POIInfoType createPOIInfoType() {
        return new POIInfoType();
    }

    /**
     * Create an instance of {@link OutputMSInformationType }
     * 
     */
    public OutputMSInformationType createOutputMSInformationType() {
        return new OutputMSInformationType();
    }

    /**
     * Create an instance of {@link ReverseGeocodeRequestType }
     * 
     */
    public ReverseGeocodeRequestType createReverseGeocodeRequestType() {
        return new ReverseGeocodeRequestType();
    }

    /**
     * Create an instance of {@link PortrayMapRequestType }
     * 
     */
    public PortrayMapRequestType createPortrayMapRequestType() {
        return new PortrayMapRequestType();
    }

    /**
     * Create an instance of {@link GeocodedAddressType }
     * 
     */
    public GeocodedAddressType createGeocodedAddressType() {
        return new GeocodedAddressType();
    }

    /**
     * Create an instance of {@link RoutePlanType }
     * 
     */
    public RoutePlanType createRoutePlanType() {
        return new RoutePlanType();
    }

    /**
     * Create an instance of {@link AvailableLayersType }
     * 
     */
    public AvailableLayersType createAvailableLayersType() {
        return new AvailableLayersType();
    }

    /**
     * Create an instance of {@link LayerType }
     * 
     */
    public LayerType createLayerType() {
        return new LayerType();
    }

    /**
     * Create an instance of {@link POIPropertyType }
     * 
     */
    public POIPropertyType createPOIPropertyType() {
        return new POIPropertyType();
    }

    /**
     * Create an instance of {@link RadiusType }
     * 
     */
    public RadiusType createRadiusType() {
        return new RadiusType();
    }

    /**
     * Create an instance of {@link POIPropertiesType }
     * 
     */
    public POIPropertiesType createPOIPropertiesType() {
        return new POIPropertiesType();
    }

    /**
     * Create an instance of {@link RouteGeometryRequestType }
     * 
     */
    public RouteGeometryRequestType createRouteGeometryRequestType() {
        return new RouteGeometryRequestType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteMapRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteMapRequest")
    public JAXBElement<RouteMapRequestType> createRouteMapRequest(RouteMapRequestType value) {
        return new JAXBElement<RouteMapRequestType>(_RouteMapRequest_QNAME, RouteMapRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltitudeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Altitude")
    public JAXBElement<AltitudeType> createAltitude(AltitudeType value) {
        return new JAXBElement<AltitudeType>(_Altitude_QNAME, AltitudeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractStreetLocatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_StreetLocation")
    public JAXBElement<AbstractStreetLocatorType> createStreetLocation(AbstractStreetLocatorType value) {
        return new JAXBElement<AbstractStreetLocatorType>(_StreetLocation_QNAME, AbstractStreetLocatorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoutePlanType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RoutePlan")
    public JAXBElement<RoutePlanType> createRoutePlan(RoutePlanType value) {
        return new JAXBElement<RoutePlanType>(_RoutePlan_QNAME, RoutePlanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InputMSIDsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "InputMSIDs", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_MSIDs")
    public JAXBElement<InputMSIDsType> createInputMSIDs(InputMSIDsType value) {
        return new JAXBElement<InputMSIDsType>(_InputMSIDs_QNAME, InputMSIDsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseGeocodeResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ReverseGeocodeResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<ReverseGeocodeResponseType> createReverseGeocodeResponse(ReverseGeocodeResponseType value) {
        return new JAXBElement<ReverseGeocodeResponseType>(_ReverseGeocodeResponse_QNAME, ReverseGeocodeResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractNamedReferenceSystem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_NamedReferenceSystem")
    public JAXBElement<AbstractNamedReferenceSystem> createNamedReferenceSystem(AbstractNamedReferenceSystem value) {
        return new JAXBElement<AbstractNamedReferenceSystem>(_NamedReferenceSystem_QNAME, AbstractNamedReferenceSystem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseGeocodePreferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ReverseGeocodePreference")
    public JAXBElement<ReverseGeocodePreferenceType> createReverseGeocodePreference(ReverseGeocodePreferenceType value) {
        return new JAXBElement<ReverseGeocodePreferenceType>(_ReverseGeocodePreference_QNAME, ReverseGeocodePreferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "BoundingBox")
    public JAXBElement<EnvelopeType> createBoundingBox(EnvelopeType value) {
        return new JAXBElement<EnvelopeType>(_BoundingBox_QNAME, EnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetermineRouteRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "DetermineRouteRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<DetermineRouteRequestType> createDetermineRouteRequest(DetermineRouteRequestType value) {
        return new JAXBElement<DetermineRouteRequestType>(_DetermineRouteRequest_QNAME, DetermineRouteRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POIInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POIInfo")
    public JAXBElement<POIInfoType> createPOIInfo(POIInfoType value) {
        return new JAXBElement<POIInfoType>(_POIInfo_QNAME, POIInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteGeometryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteGeometryRequest")
    public JAXBElement<RouteGeometryRequestType> createRouteGeometryRequest(RouteGeometryRequestType value) {
        return new JAXBElement<RouteGeometryRequestType>(_RouteGeometryRequest_QNAME, RouteGeometryRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NAICSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "NAICS", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_NamedReferenceSystem")
    public JAXBElement<NAICSType> createNAICS(NAICSType value) {
        return new JAXBElement<NAICSType>(_NAICS_QNAME, NAICSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MapType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Map", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<MapType> createMap(MapType value) {
        return new JAXBElement<MapType>(_Map_QNAME, MapType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Distance", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Measure")
    public JAXBElement<DistanceType> createDistance(DistanceType value) {
        return new JAXBElement<DistanceType>(_Distance_QNAME, DistanceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGatewayParametersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_GatewayParameters")
    public JAXBElement<AbstractGatewayParametersType> createGatewayParameters(AbstractGatewayParametersType value) {
        return new JAXBElement<AbstractGatewayParametersType>(_GatewayParameters_QNAME, AbstractGatewayParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SLIAType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "SLIA", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<SLIAType> createSLIA(SLIAType value) {
        return new JAXBElement<SLIAType>(_SLIA_QNAME, SLIAType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AvoidListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AvoidList")
    public JAXBElement<AvoidListType> createAvoidList(AvoidListType value) {
        return new JAXBElement<AvoidListType>(_AvoidList_QNAME, AvoidListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_ADT")
    public JAXBElement<AbstractDataType> createADT(AbstractDataType value) {
        return new JAXBElement<AbstractDataType>(_ADT_QNAME, AbstractDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AngleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Angle", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Measure")
    public JAXBElement<AngleType> createAngle(AngleType value) {
        return new JAXBElement<AngleType>(_Angle_QNAME, AngleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SLIRType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "SLIR", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<SLIRType> createSLIR(SLIRType value) {
        return new JAXBElement<SLIRType>(_SLIR_QNAME, SLIRType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "SearchCentreDistance")
    public JAXBElement<DistanceType> createSearchCentreDistance(DistanceType value) {
        return new JAXBElement<DistanceType>(_SearchCentreDistance_QNAME, DistanceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortrayMapRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "PortrayMapRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<PortrayMapRequestType> createPortrayMapRequest(PortrayMapRequestType value) {
        return new JAXBElement<PortrayMapRequestType>(_PortrayMapRequest_QNAME, PortrayMapRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NACEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "NACE", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_NamedReferenceSystem")
    public JAXBElement<NACEType> createNACE(NACEType value) {
        return new JAXBElement<NACEType>(_NACE_QNAME, NACEType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeocodingQOSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GeocodeMatchCode")
    public JAXBElement<GeocodingQOSType> createGeocodeMatchCode(GeocodingQOSType value) {
        return new JAXBElement<GeocodingQOSType>(_GeocodeMatchCode_QNAME, GeocodingQOSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RequestHeader", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Header")
    public JAXBElement<RequestHeaderType> createRequestHeader(RequestHeaderType value) {
        return new JAXBElement<RequestHeaderType>(_RequestHeader_QNAME, RequestHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InputGatewayParametersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "InputGatewayParameters", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_GatewayParameters")
    public JAXBElement<InputGatewayParametersType> createInputGatewayParameters(InputGatewayParametersType value) {
        return new JAXBElement<InputGatewayParametersType>(_InputGatewayParameters_QNAME, InputGatewayParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WayPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "StartPoint", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_WayPoint")
    public JAXBElement<WayPointType> createStartPoint(WayPointType value) {
        return new JAXBElement<WayPointType>(_StartPoint_QNAME, WayPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteInstructionsRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteInstructionsRequest")
    public JAXBElement<RouteInstructionsRequestType> createRouteInstructionsRequest(RouteInstructionsRequestType value) {
        return new JAXBElement<RouteInstructionsRequestType>(_RouteInstructionsRequest_QNAME, RouteInstructionsRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "PostalCode")
    public JAXBElement<String> createPostalCode(String value) {
        return new JAXBElement<String>(_PostalCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AreaOfInterestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AOI", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<AreaOfInterestType> createAOI(AreaOfInterestType value) {
        return new JAXBElement<AreaOfInterestType>(_AOI_QNAME, AreaOfInterestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractLocationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_Location", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<AbstractLocationType> createLocation(AbstractLocationType value) {
        return new JAXBElement<AbstractLocationType>(_Location_QNAME, AbstractLocationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeocodeResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GeocodeResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<GeocodeResponseType> createGeocodeResponse(GeocodeResponseType value) {
        return new JAXBElement<GeocodeResponseType>(_GeocodeResponse_QNAME, GeocodeResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PositionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Position", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "AbstractPosition")
    public JAXBElement<PositionType> createPosition(PositionType value) {
        return new JAXBElement<PositionType>(_Position_QNAME, PositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteGeometry", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<RouteGeometryType> createRouteGeometry(RouteGeometryType value) {
        return new JAXBElement<RouteGeometryType>(_RouteGeometry_QNAME, RouteGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POILocationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POILocation")
    public JAXBElement<POILocationType> createPOILocation(POILocationType value) {
        return new JAXBElement<POILocationType>(_POILocation_QNAME, POILocationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoutePreferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RoutePreference")
    public JAXBElement<RoutePreferenceType> createRoutePreference(RoutePreferenceType value) {
        return new JAXBElement<RoutePreferenceType>(_RoutePreference_QNAME, RoutePreferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GetPortrayMapCapabilitiesRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<GetPortrayMapCapabilitiesRequestType> createGetPortrayMapCapabilitiesRequest(GetPortrayMapCapabilitiesRequestType value) {
        return new JAXBElement<GetPortrayMapCapabilitiesRequestType>(_GetPortrayMapCapabilitiesRequest_QNAME, GetPortrayMapCapabilitiesRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractMSIDsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_MSIDs")
    public JAXBElement<AbstractMSIDsType> createMSIDs(AbstractMSIDsType value) {
        return new JAXBElement<AbstractMSIDsType>(_MSIDs_QNAME, AbstractMSIDsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AvoidFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AvoidFeature")
    public JAXBElement<AvoidFeatureType> createAvoidFeature(AvoidFeatureType value) {
        return new JAXBElement<AvoidFeatureType>(_AvoidFeature_QNAME, AvoidFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Error")
    public JAXBElement<ErrorType> createError(ErrorType value) {
        return new JAXBElement<ErrorType>(_Error_QNAME, ErrorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POIProperties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POIProperties", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_POISelectionCriteria")
    public JAXBElement<POIProperties> createPOIProperties(POIProperties value) {
        return new JAXBElement<POIProperties>(_POIProperties_QNAME, POIProperties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPOIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AbstractPOI", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Location")
    public JAXBElement<AbstractPOIType> createAbstractPOI(AbstractPOIType value) {
        return new JAXBElement<AbstractPOIType>(_AbstractPOI_QNAME, AbstractPOIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractResponseParametersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_ResponseParameters")
    public JAXBElement<AbstractResponseParametersType> createResponseParameters(AbstractResponseParametersType value) {
        return new JAXBElement<AbstractResponseParametersType>(_ResponseParameters_QNAME, AbstractResponseParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POIInfoListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POIInfoList")
    public JAXBElement<POIInfoListType> createPOIInfoList(POIInfoListType value) {
        return new JAXBElement<POIInfoListType>(_POIInfoList_QNAME, POIInfoListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractMSInformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_MSInformation")
    public JAXBElement<AbstractMSInformationType> createMSInformation(AbstractMSInformationType value) {
        return new JAXBElement<AbstractMSInformationType>(_MSInformation_QNAME, AbstractMSInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectoryResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "DirectoryResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<DirectoryResponseType> createDirectoryResponse(DirectoryResponseType value) {
        return new JAXBElement<DirectoryResponseType>(_DirectoryResponse_QNAME, DirectoryResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPOISelectionCriteriaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_POISelectionCriteria")
    public JAXBElement<AbstractPOISelectionCriteriaType> createPOISelectionCriteria(AbstractPOISelectionCriteriaType value) {
        return new JAXBElement<AbstractPOISelectionCriteriaType>(_POISelectionCriteria_QNAME, AbstractPOISelectionCriteriaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPositionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AbstractPosition", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Location")
    public JAXBElement<AbstractPositionType> createAbstractPosition(AbstractPositionType value) {
        return new JAXBElement<AbstractPositionType>(_AbstractPosition_QNAME, AbstractPositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WayPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ViaPoint", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_WayPoint")
    public JAXBElement<WayPointType> createViaPoint(WayPointType value) {
        return new JAXBElement<WayPointType>(_ViaPoint_QNAME, WayPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteInstructionsListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteInstructionsList", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<RouteInstructionsListType> createRouteInstructionsList(RouteInstructionsListType value) {
        return new JAXBElement<RouteInstructionsListType>(_RouteInstructionsList_QNAME, RouteInstructionsListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuildingLocatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Building", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_StreetLocation")
    public JAXBElement<BuildingLocatorType> createBuilding(BuildingLocatorType value) {
        return new JAXBElement<BuildingLocatorType>(_Building_QNAME, BuildingLocatorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPortrayMapCapabilitiesResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GetPortrayMapCapabilitiesResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<GetPortrayMapCapabilitiesResponseType> createGetPortrayMapCapabilitiesResponse(GetPortrayMapCapabilitiesResponseType value) {
        return new JAXBElement<GetPortrayMapCapabilitiesResponseType>(_GetPortrayMapCapabilitiesResponse_QNAME, GetPortrayMapCapabilitiesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractBodyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_Body")
    public JAXBElement<AbstractBodyType> createBody(AbstractBodyType value) {
        return new JAXBElement<AbstractBodyType>(_Body_QNAME, AbstractBodyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteInstructionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteInstruction")
    public JAXBElement<RouteInstructionType> createRouteInstruction(RouteInstructionType value) {
        return new JAXBElement<RouteInstructionType>(_RouteInstruction_QNAME, RouteInstructionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AbstractPOIProperty")
    public JAXBElement<Object> createAbstractPOIProperty(Object value) {
        return new JAXBElement<Object>(_AbstractPOIProperty_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteHandleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteHandle")
    public JAXBElement<RouteHandleType> createRouteHandle(RouteHandleType value) {
        return new JAXBElement<RouteHandleType>(_RouteHandle_QNAME, RouteHandleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractWayPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_WayPoint")
    public JAXBElement<AbstractWayPointType> createWayPoint(AbstractWayPointType value) {
        return new JAXBElement<AbstractWayPointType>(_WayPoint_QNAME, AbstractWayPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseGeocodeRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ReverseGeocodeRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<ReverseGeocodeRequestType> createReverseGeocodeRequest(ReverseGeocodeRequestType value) {
        return new JAXBElement<ReverseGeocodeRequestType>(_ReverseGeocodeRequest_QNAME, ReverseGeocodeRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteSegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "NextSegment", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "AbstractNextSegment")
    public JAXBElement<RouteSegmentType> createNextSegment(RouteSegmentType value) {
        return new JAXBElement<RouteSegmentType>(_NextSegment_QNAME, RouteSegmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_Header")
    public JAXBElement<AbstractHeaderType> createHeader(AbstractHeaderType value) {
        return new JAXBElement<AbstractHeaderType>(_Header_QNAME, AbstractHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeocodeRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GeocodeRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<GeocodeRequestType> createGeocodeRequest(GeocodeRequestType value) {
        return new JAXBElement<GeocodeRequestType>(_GeocodeRequest_QNAME, GeocodeRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InputMSInformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "InputMSInformation", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_MSInformation")
    public JAXBElement<InputMSInformationType> createInputMSInformation(InputMSInformationType value) {
        return new JAXBElement<InputMSInformationType>(_InputMSInformation_QNAME, InputMSInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointOfInterestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POI", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "AbstractPOI")
    public JAXBElement<PointOfInterestType> createPOI(PointOfInterestType value) {
        return new JAXBElement<PointOfInterestType>(_POI_QNAME, PointOfInterestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SICType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "SIC", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_NamedReferenceSystem")
    public JAXBElement<SICType> createSIC(SICType value) {
        return new JAXBElement<SICType>(_SIC_QNAME, SICType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POIAttributeListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POIAttributeList")
    public JAXBElement<POIAttributeListType> createPOIAttributeList(POIAttributeListType value) {
        return new JAXBElement<POIAttributeListType>(_POIAttributeList_QNAME, POIAttributeListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link POIPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "POIProperty", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "AbstractPOIProperty")
    public JAXBElement<POIPropertyType> createPOIProperty(POIPropertyType value) {
        return new JAXBElement<POIPropertyType>(_POIProperty_QNAME, POIPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeocodeResponseListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "GeocodeResponseList")
    public JAXBElement<GeocodeResponseListType> createGeocodeResponseList(GeocodeResponseListType value) {
        return new JAXBElement<GeocodeResponseListType>(_GeocodeResponseList_QNAME, GeocodeResponseListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutputGatewayParametersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "OutputGatewayParameters", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_GatewayParameters")
    public JAXBElement<OutputGatewayParametersType> createOutputGatewayParameters(OutputGatewayParametersType value) {
        return new JAXBElement<OutputGatewayParametersType>(_OutputGatewayParameters_QNAME, OutputGatewayParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRequestParametersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_RequestParameters")
    public JAXBElement<AbstractRequestParametersType> createRequestParameters(AbstractRequestParametersType value) {
        return new JAXBElement<AbstractRequestParametersType>(_RequestParameters_QNAME, AbstractRequestParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ResponseHeader", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Header")
    public JAXBElement<ResponseHeaderType> createResponseHeader(ResponseHeaderType value) {
        return new JAXBElement<ResponseHeaderType>(_ResponseHeader_QNAME, ResponseHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteMapType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteMap")
    public JAXBElement<RouteMapType> createRouteMap(RouteMapType value) {
        return new JAXBElement<RouteMapType>(_RouteMap_QNAME, RouteMapType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CircularArcType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "CircularArc")
    public JAXBElement<CircularArcType> createCircularArc(CircularArcType value) {
        return new JAXBElement<CircularArcType>(_CircularArc_QNAME, CircularArcType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutputMSInformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "OutputMSInformation", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_MSInformation")
    public JAXBElement<OutputMSInformationType> createOutputMSInformation(OutputMSInformationType value) {
        return new JAXBElement<OutputMSInformationType>(_OutputMSInformation_QNAME, OutputMSInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutputMSIDsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "OutputMSIDs", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_MSIDs")
    public JAXBElement<OutputMSIDsType> createOutputMSIDs(OutputMSIDsType value) {
        return new JAXBElement<OutputMSIDsType>(_OutputMSIDs_QNAME, OutputMSIDsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XLSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "XLS")
    public JAXBElement<XLSType> createXLS(XLSType value) {
        return new JAXBElement<XLSType>(_XLS_QNAME, XLSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetermineRouteResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "DetermineRouteResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<DetermineRouteResponseType> createDetermineRouteResponse(DetermineRouteResponseType value) {
        return new JAXBElement<DetermineRouteResponseType>(_DetermineRouteResponse_QNAME, DetermineRouteResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Time")
    public JAXBElement<TimeType> createTime(TimeType value) {
        return new JAXBElement<TimeType>(_Time_QNAME, TimeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EllipseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Ellipse")
    public JAXBElement<EllipseType> createEllipse(EllipseType value) {
        return new JAXBElement<EllipseType>(_Ellipse_QNAME, EllipseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ErrorList")
    public JAXBElement<ErrorListType> createErrorList(ErrorListType value) {
        return new JAXBElement<ErrorListType>(_ErrorList_QNAME, ErrorListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WayPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "EndPoint", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_WayPoint")
    public JAXBElement<WayPointType> createEndPoint(WayPointType value) {
        return new JAXBElement<WayPointType>(_EndPoint_QNAME, WayPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceSystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "ReferenceSystem")
    public JAXBElement<ReferenceSystemType> createReferenceSystem(ReferenceSystemType value) {
        return new JAXBElement<ReferenceSystemType>(_ReferenceSystem_QNAME, ReferenceSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractMeasureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "_Measure")
    public JAXBElement<AbstractMeasureType> createMeasure(AbstractMeasureType value) {
        return new JAXBElement<AbstractMeasureType>(_Measure_QNAME, AbstractMeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StreetNameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Street")
    public JAXBElement<StreetNameType> createStreet(StreetNameType value) {
        return new JAXBElement<StreetNameType>(_Street_QNAME, StreetNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRouteSegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AbstractNextSegment")
    public JAXBElement<AbstractRouteSegmentType> createAbstractNextSegment(AbstractRouteSegmentType value) {
        return new JAXBElement<AbstractRouteSegmentType>(_AbstractNextSegment_QNAME, AbstractRouteSegmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Request", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Body")
    public JAXBElement<RequestType> createRequest(RequestType value) {
        return new JAXBElement<RequestType>(_Request_QNAME, RequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PortrayMapResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "PortrayMapResponse", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ResponseParameters")
    public JAXBElement<PortrayMapResponseType> createPortrayMapResponse(PortrayMapResponseType value) {
        return new JAXBElement<PortrayMapResponseType>(_PortrayMapResponse_QNAME, PortrayMapResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Address", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Location")
    public JAXBElement<AddressType> createAddress(AddressType value) {
        return new JAXBElement<AddressType>(_Address_QNAME, AddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "TimeStamp")
    public JAXBElement<TimeStampType> createTimeStamp(TimeStampType value) {
        return new JAXBElement<TimeStampType>(_TimeStamp_QNAME, TimeStampType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpeedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Speed", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Measure")
    public JAXBElement<SpeedType> createSpeed(SpeedType value) {
        return new JAXBElement<SpeedType>(_Speed_QNAME, SpeedType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StreetAddressType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "StreetAddress")
    public JAXBElement<StreetAddressType> createStreetAddress(StreetAddressType value) {
        return new JAXBElement<StreetAddressType>(_StreetAddress_QNAME, StreetAddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteSummaryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "RouteSummary", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "AbstractRouteSummary")
    public JAXBElement<RouteSummaryType> createRouteSummary(RouteSummaryType value) {
        return new JAXBElement<RouteSummaryType>(_RouteSummary_QNAME, RouteSummaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NamedPlaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Place")
    public JAXBElement<NamedPlaceType> createPlace(NamedPlaceType value) {
        return new JAXBElement<NamedPlaceType>(_Place_QNAME, NamedPlaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectoryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "DirectoryRequest", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_RequestParameters")
    public JAXBElement<DirectoryRequestType> createDirectoryRequest(DirectoryRequestType value) {
        return new JAXBElement<DirectoryRequestType>(_DirectoryRequest_QNAME, DirectoryRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WayPointListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "WayPointList")
    public JAXBElement<WayPointListType> createWayPointList(WayPointListType value) {
        return new JAXBElement<WayPointListType>(_WayPointList_QNAME, WayPointListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "Response", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_Body")
    public JAXBElement<ResponseType> createResponse(ResponseType value) {
        return new JAXBElement<ResponseType>(_Response_QNAME, ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRouteSummaryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/xls", name = "AbstractRouteSummary", substitutionHeadNamespace = "http://www.opengis.net/xls", substitutionHeadName = "_ADT")
    public JAXBElement<AbstractRouteSummaryType> createAbstractRouteSummary(AbstractRouteSummaryType value) {
        return new JAXBElement<AbstractRouteSummaryType>(_AbstractRouteSummary_QNAME, AbstractRouteSummaryType.class, null, value);
    }

}
