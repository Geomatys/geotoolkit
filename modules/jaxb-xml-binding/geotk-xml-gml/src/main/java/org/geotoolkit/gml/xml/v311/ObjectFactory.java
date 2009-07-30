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

import java.math.BigDecimal;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.gml package. 
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

    private static final QName _AbstractGML_QNAME = new QName("http://www.opengis.net/gml", "AbstractGML");
    private static final QName _CoordinateReferenceSystem_QNAME = new QName("http://www.opengis.net/gml", "AbstractCoordinateReferenceSystem");
    private static final QName _DatumID_QNAME = new QName("http://www.opengis.net/gml", "datumID");
    private static final QName _PositionalAccuracy_QNAME = new QName("http://www.opengis.net/gml", "AbstractpositionalAccuracy");
    private static final QName _UnitDefinition_QNAME = new QName("http://www.opengis.net/gml", "UnitDefinition");
    private static final QName _ReferenceSystem_QNAME = new QName("http://www.opengis.net/gml", "AbstractReferenceSystem");
    private static final QName _AxisID_QNAME = new QName("http://www.opengis.net/gml", "axisID");
    private static final QName _ParameterID_QNAME = new QName("http://www.opengis.net/gml", "parameterID");
    private static final QName _UsesCartesianCS_QNAME = new QName("http://www.opengis.net/gml", "usesCartesianCS");
    private static final QName _CompositeCurve_QNAME = new QName("http://www.opengis.net/gml", "CompositeCurve");
    private static final QName _CompositeSolid_QNAME = new QName("http://www.opengis.net/gml", "CompositeSolid");
    private static final QName _CompositeSurface_QNAME = new QName("http://www.opengis.net/gml", "CompositeSurface");
    private static final QName _CoordinateSystemAxisRef_QNAME = new QName("http://www.opengis.net/gml", "coordinateSystemAxisRef");
    private static final QName _CoordinateSystemAxis_QNAME = new QName("http://www.opengis.net/gml", "CoordinateSystemAxis");
    private static final QName _Direction_QNAME = new QName("http://www.opengis.net/gml", "direction");
    private static final QName _ImageCRS_QNAME = new QName("http://www.opengis.net/gml", "ImageCRS");
    private static final QName _DirectionVector_QNAME = new QName("http://www.opengis.net/gml", "DirectionVector");
    private static final QName _UsesAxis_QNAME = new QName("http://www.opengis.net/gml", "usesAxis");
    private static final QName _EngineeringCRS_QNAME = new QName("http://www.opengis.net/gml", "EngineeringCRS");
    private static final QName _CountExtent_QNAME = new QName("http://www.opengis.net/gml", "CountExtent");
    private static final QName _EngineeringDatumRef_QNAME = new QName("http://www.opengis.net/gml", "engineeringDatumRef");
    private static final QName _UsesEngineeringDatum_QNAME = new QName("http://www.opengis.net/gml", "usesEngineeringDatum");
    private static final QName _EngineeringDatum_QNAME = new QName("http://www.opengis.net/gml", "EngineeringDatum");
    private static final QName _ValidArea_QNAME = new QName("http://www.opengis.net/gml", "validArea");
    private static final QName _GeometricComplex_QNAME = new QName("http://www.opengis.net/gml", "GeometricComplex");
    private static final QName _CoordinateSystemRef_QNAME = new QName("http://www.opengis.net/gml", "coordinateSystemRef");
    private static final QName _UsesCS_QNAME = new QName("http://www.opengis.net/gml", "usesCS");
    private static final QName _MethodID_QNAME = new QName("http://www.opengis.net/gml", "methodID");
    private static final QName _TemporalExtent_QNAME = new QName("http://www.opengis.net/gml", "temporalExtent");
    private static final QName _TemporalCRS_QNAME = new QName("http://www.opengis.net/gml", "TemporalCRS");
    private static final QName _TemporalCSRef_QNAME = new QName("http://www.opengis.net/gml", "temporalCSRef");
    private static final QName _UsesTemporalCS_QNAME = new QName("http://www.opengis.net/gml", "usesTemporalCS");
    private static final QName _TemporalCS_QNAME = new QName("http://www.opengis.net/gml", "TemporalCS");
    private static final QName _TemporalDatumRef_QNAME = new QName("http://www.opengis.net/gml", "temporalDatumRef");
    private static final QName _UsesTemporalDatum_QNAME = new QName("http://www.opengis.net/gml", "usesTemporalDatum");
    private static final QName _TemporalDatum_QNAME = new QName("http://www.opengis.net/gml", "TemporalDatum");
    private static final QName _ImageDatumRef_QNAME = new QName("http://www.opengis.net/gml", "imageDatumRef");
    private static final QName _ImageDatum_QNAME = new QName("http://www.opengis.net/gml", "ImageDatum");
    private static final QName _UsesImageDatum_QNAME = new QName("http://www.opengis.net/gml", "usesImageDatum");
    private static final QName _MovingObjectStatus_QNAME = new QName("http://www.opengis.net/gml", "MovingObjectStatus");
    private static final QName _UsesObliqueCartesianCS_QNAME = new QName("http://www.opengis.net/gml", "usesObliqueCartesianCS");
    private static final QName _ObliqueCartesianCS_QNAME = new QName("http://www.opengis.net/gml", "ObliqueCartesianCS");
    private static final QName _ObliqueCartesianCSRef_QNAME = new QName("http://www.opengis.net/gml", "obliqueCartesianCSRef");
    private static final QName _Quantity_QNAME = new QName("http://www.opengis.net/gml", "Quantity");
    private static final QName _QuantityList_QNAME = new QName("http://www.opengis.net/gml", "QuantityList");
    private static final QName _QuantityExtent_QNAME = new QName("http://www.opengis.net/gml", "QuantityExtent");
    private static final QName _RectifiedGrid_QNAME = new QName("http://www.opengis.net/gml", "RectifiedGrid");
    private static final QName _PixelInCell_QNAME = new QName("http://www.opengis.net/gml", "pixelInCell");
    private static final QName _Track_QNAME = new QName("http://www.opengis.net/gml", "track");
    private static final QName _CoordinateOperationID_QNAME = new QName("http://www.opengis.net/gml", "coordinateOperationID");
    private static final QName _OuterBoundaryIs_QNAME = new QName("http://www.opengis.net/gml", "outerBoundaryIs");
    private static final QName _MultiLineString_QNAME = new QName("http://www.opengis.net/gml", "MultiLineString");
    private static final QName _GeometricAggregate_QNAME = new QName("http://www.opengis.net/gml", "_GeometricAggregate");
    private static final QName _CRS_QNAME = new QName("http://www.opengis.net/gml", "AbstractCRS");
    private static final QName _SrsID_QNAME = new QName("http://www.opengis.net/gml", "srsID");
    private static final QName _GroupID_QNAME = new QName("http://www.opengis.net/gml", "groupID");
    private static final QName _SourceCRS_QNAME = new QName("http://www.opengis.net/gml", "sourceCRS");
    private static final QName _TargetCRS_QNAME = new QName("http://www.opengis.net/gml", "targetCRS");
    private static final QName _TimeSlice_QNAME = new QName("http://www.opengis.net/gml", "AbstractTimeSlice");
    private static final QName _CartesianCSRef_QNAME = new QName("http://www.opengis.net/gml", "cartesianCSRef");
    private static final QName _Geometry_QNAME = new QName("http://www.opengis.net/gml", "AbstractGeometry");
    private static final QName _BSpline_QNAME = new QName("http://www.opengis.net/gml", "BSpline");
    private static final QName _ValidTime_QNAME = new QName("http://www.opengis.net/gml", "validTime");
    private static final QName _SolidProperty_QNAME = new QName("http://www.opengis.net/gml", "solidProperty");
    private static final QName _TriangulatedSurface_QNAME = new QName("http://www.opengis.net/gml", "TriangulatedSurface");
    private static final QName _LinearRing_QNAME = new QName("http://www.opengis.net/gml", "LinearRing");
    private static final QName _LineStringSegment_QNAME = new QName("http://www.opengis.net/gml", "LineStringSegment");
    private static final QName _DefinitionCollection_QNAME = new QName("http://www.opengis.net/gml", "DefinitionCollection");
    private static final QName _InnerBoundaryIs_QNAME = new QName("http://www.opengis.net/gml", "innerBoundaryIs");
    private static final QName AbstractTimePrimitive_QNAME = new QName("http://www.opengis.net/gml", "AbstractTimePrimitive");
    private static final QName _Reference_QNAME = new QName("http://www.opengis.net/gml", "_reference");
    private static final QName _MultiSolidProperty_QNAME = new QName("http://www.opengis.net/gml", "multiSolidProperty");
    private static final QName _GriddedSurface_QNAME = new QName("http://www.opengis.net/gml", "_GriddedSurface");
    private static final QName _Grid_QNAME = new QName("http://www.opengis.net/gml", "Grid");
    private static final QName _EllipsoidID_QNAME = new QName("http://www.opengis.net/gml", "ellipsoidID");
    private static final QName _History_QNAME = new QName("http://www.opengis.net/gml", "history");
    private static final QName _SolidMembers_QNAME = new QName("http://www.opengis.net/gml", "solidMembers");
    private static final QName _UnitOfMeasure_QNAME = new QName("http://www.opengis.net/gml", "unitOfMeasure");
    private static final QName _Circle_QNAME = new QName("http://www.opengis.net/gml", "Circle");
    private static final QName _CurveProperty_QNAME = new QName("http://www.opengis.net/gml", "curveProperty");
    private static final QName _ConventionalUnit_QNAME = new QName("http://www.opengis.net/gml", "ConventionalUnit");
    private static final QName _CoordinateOperation_QNAME = new QName("http://www.opengis.net/gml", "AbstractCoordinateOperation");
    private static final QName _CsID_QNAME = new QName("http://www.opengis.net/gml", "csID");
    private static final QName _MeridianID_QNAME = new QName("http://www.opengis.net/gml", "meridianID");
    private static final QName _SingleOperation_QNAME = new QName("http://www.opengis.net/gml", "AbstractSingleOperation");
    private static final QName _Operation_QNAME = new QName("http://www.opengis.net/gml", "AbstractOperation");
    private static final QName _Datum_QNAME = new QName("http://www.opengis.net/gml", "AbstractDatum");
    private static final QName AbstractCoordinateSystem_QNAME = new QName("http://www.opengis.net/gml", "AbstractCoordinateSystem");
    private static final QName _Description_QNAME = new QName("http://www.opengis.net/gml", "description");
    private static final QName _SurfaceMembers_QNAME = new QName("http://www.opengis.net/gml", "surfaceMembers");
    private static final QName _PolygonPatch_QNAME = new QName("http://www.opengis.net/gml", "PolygonPatch");
    private static final QName _AbstractSolid_QNAME = new QName("http://www.opengis.net/gml", "AbstractSolid");
    private static final QName _AffinePlacement_QNAME = new QName("http://www.opengis.net/gml", "AffinePlacement");
    private static final QName _ArcByCenterPoint_QNAME = new QName("http://www.opengis.net/gml", "ArcByCenterPoint");
    private static final QName _PosList_QNAME = new QName("http://www.opengis.net/gml", "posList");
    private static final QName _MetaDataProperty_QNAME = new QName("http://www.opengis.net/gml", "metaDataProperty");
    private static final QName _TimeInterval_QNAME = new QName("http://www.opengis.net/gml", "timeInterval");
    private static final QName _SurfaceProperty_QNAME = new QName("http://www.opengis.net/gml", "surfaceProperty");
    private static final QName _MultiSolid_QNAME = new QName("http://www.opengis.net/gml", "MultiSolid");
    private static final QName _OrientableCurve_QNAME = new QName("http://www.opengis.net/gml", "OrientableCurve");
    private static final QName _MultiPosition_QNAME = new QName("http://www.opengis.net/gml", "multiPosition");
    private static final QName _SurfaceMember_QNAME = new QName("http://www.opengis.net/gml", "surfaceMember");
    private static final QName _DefinitionRef_QNAME = new QName("http://www.opengis.net/gml", "definitionRef");
    private static final QName _MultiPolygon_QNAME = new QName("http://www.opengis.net/gml", "MultiPolygon");
    private static final QName _Solid_QNAME = new QName("http://www.opengis.net/gml", "Solid");
    private static final QName _Measure_QNAME = new QName("http://www.opengis.net/gml", "measure");
    private static final QName _PolygonMember_QNAME = new QName("http://www.opengis.net/gml", "polygonMember");
    private static final QName _MultiGeometry_QNAME = new QName("http://www.opengis.net/gml", "MultiGeometry");
    private static final QName _Members_QNAME = new QName("http://www.opengis.net/gml", "members");
    private static final QName _Association_QNAME = new QName("http://www.opengis.net/gml", "_association");
    private static final QName _MultiCurveProperty_QNAME = new QName("http://www.opengis.net/gml", "multiCurveProperty");
    private static final QName _PolygonPatches_QNAME = new QName("http://www.opengis.net/gml", "polygonPatches");
    private static final QName _PolyhedralSurface_QNAME = new QName("http://www.opengis.net/gml", "PolyhedralSurface");
    private static final QName _Name_QNAME = new QName("http://www.opengis.net/gml", "name");
    private static final QName _TimePeriod_QNAME = new QName("http://www.opengis.net/gml", "TimePeriod");
    private static final QName _AbstractCurve_QNAME = new QName("http://www.opengis.net/gml", "AbstractCurve");
    private static final QName _RoughConversionToPreferredUnit_QNAME = new QName("http://www.opengis.net/gml", "roughConversionToPreferredUnit");
    private static final QName _Clothoid_QNAME = new QName("http://www.opengis.net/gml", "Clothoid");
    private static final QName _AbstractGeometricPrimitive_QNAME = new QName("http://www.opengis.net/gml", "AbstractGeometricPrimitive");
    private static final QName _Null_QNAME = new QName("http://www.opengis.net/gml", "Null");
    private static final QName _PointProperty_QNAME = new QName("http://www.opengis.net/gml", "pointProperty");
    private static final QName _GeometryMember_QNAME = new QName("http://www.opengis.net/gml", "geometryMember");
    private static final QName _MultiEdgeOf_QNAME = new QName("http://www.opengis.net/gml", "multiEdgeOf");
    private static final QName _Curve_QNAME = new QName("http://www.opengis.net/gml", "Curve");
    private static final QName _CurveMembers_QNAME = new QName("http://www.opengis.net/gml", "curveMembers");
    private static final QName _LineStringMember_QNAME = new QName("http://www.opengis.net/gml", "lineStringMember");
    private static final QName _Array_QNAME = new QName("http://www.opengis.net/gml", "Array");
    private static final QName _Pos_QNAME = new QName("http://www.opengis.net/gml", "pos");
    private static final QName _Arc_QNAME = new QName("http://www.opengis.net/gml", "Arc");
    private static final QName _Exterior_QNAME = new QName("http://www.opengis.net/gml", "exterior");
    private static final QName _GenericMetaData_QNAME = new QName("http://www.opengis.net/gml", "GenericMetaData");
    private static final QName _Surface_QNAME = new QName("http://www.opengis.net/gml", "Surface");
    private static final QName _Point_QNAME = new QName("http://www.opengis.net/gml", "Point");
    private static final QName _Coord_QNAME = new QName("http://www.opengis.net/gml", "coord");
    private static final QName _MultiCoverage_QNAME = new QName("http://www.opengis.net/gml", "multiCoverage");
    private static final QName _PolygonProperty_QNAME = new QName("http://www.opengis.net/gml", "polygonProperty");
    private static final QName _MultiSurface_QNAME = new QName("http://www.opengis.net/gml", "MultiSurface");
    private static final QName _Angle_QNAME = new QName("http://www.opengis.net/gml", "angle");
    private static final QName _SolidMember_QNAME = new QName("http://www.opengis.net/gml", "solidMember");
    private static final QName _Geodesic_QNAME = new QName("http://www.opengis.net/gml", "Geodesic");
    private static final QName _OrientableSurface_QNAME = new QName("http://www.opengis.net/gml", "OrientableSurface");
    private static final QName AbstractObject_QNAME = new QName("http://www.opengis.net/gml", "AbstractObject");
    private static final QName _Member_QNAME = new QName("http://www.opengis.net/gml", "member");
    private static final QName _CurveMember_QNAME = new QName("http://www.opengis.net/gml", "curveMember");
    private static final QName _DecimalMinutes_QNAME = new QName("http://www.opengis.net/gml", "decimalMinutes");
    private static final QName _Tin_QNAME = new QName("http://www.opengis.net/gml", "Tin");
    private static final QName _Rectangle_QNAME = new QName("http://www.opengis.net/gml", "Rectangle");
    private static final QName _DerivationUnitTerm_QNAME = new QName("http://www.opengis.net/gml", "derivationUnitTerm");
    private static final QName _Polygon_QNAME = new QName("http://www.opengis.net/gml", "Polygon");
    private static final QName _DefinitionProxy_QNAME = new QName("http://www.opengis.net/gml", "DefinitionProxy");
    private static final QName _Seconds_QNAME = new QName("http://www.opengis.net/gml", "seconds");
    private static final QName _CubicSpline_QNAME = new QName("http://www.opengis.net/gml", "CubicSpline");
    private static final QName _CenterLineOf_QNAME = new QName("http://www.opengis.net/gml", "centerLineOf");
    private static final QName _Ring_QNAME = new QName("http://www.opengis.net/gml", "Ring");
    private static final QName _CatalogSymbol_QNAME = new QName("http://www.opengis.net/gml", "catalogSymbol");
    private static final QName _ExtentOf_QNAME = new QName("http://www.opengis.net/gml", "extentOf");
    private static final QName _DerivedUnit_QNAME = new QName("http://www.opengis.net/gml", "DerivedUnit");
    private static final QName _ArcString_QNAME = new QName("http://www.opengis.net/gml", "ArcString");
    private static final QName AbstractSurfacePatch_QNAME = new QName("http://www.opengis.net/gml", "AbstractSurfacePatch");
    private static final QName _MultiExtentOf_QNAME = new QName("http://www.opengis.net/gml", "multiExtentOf");
    private static final QName _MultiGeometryProperty_QNAME = new QName("http://www.opengis.net/gml", "multiGeometryProperty");
    private static final QName _MultiCenterOf_QNAME = new QName("http://www.opengis.net/gml", "multiCenterOf");
    private static final QName _BoundedBy_QNAME = new QName("http://www.opengis.net/gml", "boundedBy");
    private static final QName AbstractCurveSegment_QNAME = new QName("http://www.opengis.net/gml", "AbstractCurveSegment");
    private static final QName _FeatureCollection_QNAME = new QName("http://www.opengis.net/gml", "FeatureCollection");
    private static final QName _Interior_QNAME = new QName("http://www.opengis.net/gml", "interior");
    private static final QName _Location_QNAME = new QName("http://www.opengis.net/gml", "location");
    private static final QName _CircleByCenterPoint_QNAME = new QName("http://www.opengis.net/gml", "CircleByCenterPoint");
    private static final QName _Sphere_QNAME = new QName("http://www.opengis.net/gml", "Sphere");
    private static final QName _SolidArrayProperty_QNAME = new QName("http://www.opengis.net/gml", "solidArrayProperty");
    private static final QName _PointMembers_QNAME = new QName("http://www.opengis.net/gml", "pointMembers");
    private static final QName _Degrees_QNAME = new QName("http://www.opengis.net/gml", "degrees");
    private static final QName _Minutes_QNAME = new QName("http://www.opengis.net/gml", "minutes");
    private static final QName _MetaData_QNAME = new QName("http://www.opengis.net/gml", "_MetaData");
    private static final QName _PriorityLocation_QNAME = new QName("http://www.opengis.net/gml", "priorityLocation");
    private static final QName _StrictAssociation_QNAME = new QName("http://www.opengis.net/gml", "_strictAssociation");
    private static final QName _EnvelopeWithTimePeriod_QNAME = new QName("http://www.opengis.net/gml", "EnvelopeWithTimePeriod");
    private static final QName _ArcByBulge_QNAME = new QName("http://www.opengis.net/gml", "ArcByBulge");
    private static final QName _CurveArrayProperty_QNAME = new QName("http://www.opengis.net/gml", "curveArrayProperty");
    private static final QName _Duration_QNAME = new QName("http://www.opengis.net/gml", "duration");
    private static final QName AbstractTimeObject_QNAME = new QName("http://www.opengis.net/gml", "AbstractTimeObject");
    private static final QName _PointRep_QNAME = new QName("http://www.opengis.net/gml", "pointRep");
    private static final QName _CenterOf_QNAME = new QName("http://www.opengis.net/gml", "centerOf");
    private static final QName _PointArrayProperty_QNAME = new QName("http://www.opengis.net/gml", "pointArrayProperty");
    private static final QName _AbstractFeatureCollection_QNAME = new QName("http://www.opengis.net/gml", "AbstractFeatureCollection");
    private static final QName _DmsAngle_QNAME = new QName("http://www.opengis.net/gml", "dmsAngle");
    private static final QName _Bag_QNAME = new QName("http://www.opengis.net/gml", "Bag");
    private static final QName _OffsetCurve_QNAME = new QName("http://www.opengis.net/gml", "OffsetCurve");
    private static final QName _ConversionToPreferredUnit_QNAME = new QName("http://www.opengis.net/gml", "conversionToPreferredUnit");
    private static final QName _Coordinates_QNAME = new QName("http://www.opengis.net/gml", "coordinates");
    private static final QName _MultiCurve_QNAME = new QName("http://www.opengis.net/gml", "MultiCurve");
    private static final QName _MultiPoint_QNAME = new QName("http://www.opengis.net/gml", "MultiPoint");
    private static final QName _Vector_QNAME = new QName("http://www.opengis.net/gml", "vector");
    private static final QName _EdgeOf_QNAME = new QName("http://www.opengis.net/gml", "edgeOf");
    private static final QName _MultiCenterLineOf_QNAME = new QName("http://www.opengis.net/gml", "multiCenterLineOf");
    private static final QName _GeometryMembers_QNAME = new QName("http://www.opengis.net/gml", "geometryMembers");
    private static final QName _LocationKeyWord_QNAME = new QName("http://www.opengis.net/gml", "LocationKeyWord");
    private static final QName _DictionaryEntry_QNAME = new QName("http://www.opengis.net/gml", "dictionaryEntry");
    private static final QName _IndirectEntry_QNAME = new QName("http://www.opengis.net/gml", "indirectEntry");
    private static final QName _Bezier_QNAME = new QName("http://www.opengis.net/gml", "Bezier");
    private static final QName _MultiPointProperty_QNAME = new QName("http://www.opengis.net/gml", "multiPointProperty");
    private static final QName _Patches_QNAME = new QName("http://www.opengis.net/gml", "patches");
    private static final QName _ArcStringByBulge_QNAME = new QName("http://www.opengis.net/gml", "ArcStringByBulge");
    private static final QName _AbstractFeature_QNAME = new QName("http://www.opengis.net/gml", "AbstractFeature");
    private static final QName _MultiLocation_QNAME = new QName("http://www.opengis.net/gml", "multiLocation");
    private static final QName _FeatureProperty_QNAME = new QName("http://www.opengis.net/gml", "featureProperty");
    private static final QName _LineStringProperty_QNAME = new QName("http://www.opengis.net/gml", "lineStringProperty");
    private static final QName _TrianglePatches_QNAME = new QName("http://www.opengis.net/gml", "trianglePatches");
    private static final QName _AbstractRing_QNAME = new QName("http://www.opengis.net/gml", "AbstractRing");
    private static final QName _TimePosition_QNAME = new QName("http://www.opengis.net/gml", "timePosition");
    private static final QName _MultiSurfaceProperty_QNAME = new QName("http://www.opengis.net/gml", "multiSurfaceProperty");
    private static final QName _DefinitionMember_QNAME = new QName("http://www.opengis.net/gml", "definitionMember");
    private static final QName _SurfaceArrayProperty_QNAME = new QName("http://www.opengis.net/gml", "surfaceArrayProperty");
    private static final QName _Triangle_QNAME = new QName("http://www.opengis.net/gml", "Triangle");
    private static final QName _Cone_QNAME = new QName("http://www.opengis.net/gml", "Cone");
    private static final QName _QuantityType_QNAME = new QName("http://www.opengis.net/gml", "quantityType");
    private static final QName _FeatureMember_QNAME = new QName("http://www.opengis.net/gml", "featureMember");
    private static final QName _LineString_QNAME = new QName("http://www.opengis.net/gml", "LineString");
    private static final QName _BaseUnit_QNAME = new QName("http://www.opengis.net/gml", "BaseUnit");
    private static final QName _LocationString_QNAME = new QName("http://www.opengis.net/gml", "LocationString");
    private static final QName _Envelope_QNAME = new QName("http://www.opengis.net/gml", "Envelope");
    private static final QName _AbstractSurface_QNAME = new QName("http://www.opengis.net/gml", "AbstractSurface");
    private static final QName _Definition_QNAME = new QName("http://www.opengis.net/gml", "Definition");
    private static final QName _FeatureMembers_QNAME = new QName("http://www.opengis.net/gml", "featureMembers");
    private static final QName _GeodesicString_QNAME = new QName("http://www.opengis.net/gml", "GeodesicString");
    private static final QName _BaseSurface_QNAME = new QName("http://www.opengis.net/gml", "baseSurface");
    private static final QName _PointMember_QNAME = new QName("http://www.opengis.net/gml", "pointMember");
    private static final QName AbstractTimeGeometricPrimitive_QNAME = new QName("http://www.opengis.net/gml", "AbstractTimeGeometricPrimitive");
    private static final QName _Cylinder_QNAME = new QName("http://www.opengis.net/gml", "Cylinder");
    private static final QName _Position_QNAME = new QName("http://www.opengis.net/gml", "position");
    private static final QName _Segments_QNAME = new QName("http://www.opengis.net/gml", "segments");
    private static final QName _ParametricCurveSurface_QNAME = new QName("http://www.opengis.net/gml", "_ParametricCurveSurface");
    private static final QName _TimeComplex_QNAME = new QName("http://www.opengis.net/gml", "_TimeComplex");
    private static final QName _Dictionary_QNAME = new QName("http://www.opengis.net/gml", "Dictionary");
    private static final QName _BaseCurve_QNAME = new QName("http://www.opengis.net/gml", "baseCurve");
    private static final QName _TimeInstant_QNAME = new QName("http://www.opengis.net/gml", "TimeInstant");
    private static final QName _AbstractGeometry_QNAME = new QName("http://www.opengis.net/gml", "AbstractGeometry");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.gml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RingType }
     * 
     */
    public RingType createRingType() {
        return new RingType();
    }

    /**
     * Create an instance of {@link RingPropertyType }
     * 
     */
    public RingPropertyType createRingPropertyType() {
        return new RingPropertyType();
    }

    /**
     * Create an instance of {@link LineStringSegmentArrayPropertyType }
     * 
     */
    public LineStringSegmentArrayPropertyType createLineStringSegmentArrayPropertyType() {
        return new LineStringSegmentArrayPropertyType();
    }

    /**
     * Create an instance of {@link GeometryArrayPropertyType }
     * 
     */
    public GeometryArrayPropertyType createGeometryArrayPropertyType() {
        return new GeometryArrayPropertyType();
    }

    /**
     * Create an instance of {@link MultiCurvePropertyType }
     * 
     */
    public MultiCurvePropertyType createMultiCurvePropertyType() {
        return new MultiCurvePropertyType();
    }

    /**
     * Create an instance of {@link BagType }
     * 
     */
    public BagType createBagType() {
        return new BagType();
    }

    /**
     * Create an instance of {@link SurfacePropertyType }
     * 
     */
    public SurfacePropertyType createSurfacePropertyType() {
        return new SurfacePropertyType();
    }

    /**
     * Create an instance of {@link StringOrRefType }
     * 
     */
    public StringOrRefType createStringOrRefType() {
        return new StringOrRefType();
    }

    /**
     * Create an instance of {@link AbstractGriddedSurfaceType }
     * 
     */
    public AbstractGriddedSurfaceType createAbstractGriddedSurfaceType() {
        return new AbstractGriddedSurfaceType();
    }

    /**
     * Create an instance of {@link PolygonType }
     * 
     */
    public PolygonType createPolygonType() {
        return new PolygonType();
    }

    /**
     * Create an instance of {@link CoordinatesType }
     * 
     */
    public CoordinatesType createCoordinatesType() {
        return new CoordinatesType();
    }

    /**
     * Create an instance of {@link SolidPropertyType }
     * 
     */
    public SolidPropertyType createSolidPropertyType() {
        return new SolidPropertyType();
    }

    /**
     * Create an instance of {@link TimePrimitivePropertyType }
     * 
     */
    public TimePrimitivePropertyType createTimePrimitivePropertyType() {
        return new TimePrimitivePropertyType();
    }

    /**
     * Create an instance of {@link CodeType }
     * 
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link TimeInstantType }
     * 
     */
    public TimeInstantType createTimeInstantType() {
        return new TimeInstantType();
    }

    /**
     * Create an instance of {@link VectorType }
     * 
     */
    public VectorType createVectorType() {
        return new VectorType();
    }

    /**
     * Create an instance of {@link MeasureOrNullListType }
     * 
     */
    public MeasureOrNullListType createMeasureOrNullListType() {
        return new MeasureOrNullListType();
    }

    /**
     * Create an instance of {@link BezierType }
     * 
     */
    public BezierType createBezierType() {
        return new BezierType();
    }

    /**
     * Create an instance of {@link PointArrayPropertyType }
     * 
     */
    public PointArrayPropertyType createPointArrayPropertyType() {
        return new PointArrayPropertyType();
    }

    /**
     * Create an instance of {@link GenericMetaDataType }
     * 
     */
    public GenericMetaDataType createGenericMetaDataType() {
        return new GenericMetaDataType();
    }

    /**
     * Create an instance of {@link MultiGeometryType }
     * 
     */
    public MultiGeometryType createMultiGeometryType() {
        return new MultiGeometryType();
    }

    /**
     * Create an instance of {@link CurveType }
     * 
     */
    public CurveType createCurveType() {
        return new CurveType();
    }

    /**
     * Create an instance of {@link LineStringType }
     * 
     */
    public LineStringType createLineStringType() {
        return new LineStringType();
    }

    /**
     * Create an instance of {@link MultiPolygonType }
     * 
     */
    public MultiPolygonType createMultiPolygonType() {
        return new MultiPolygonType();
    }

    /**
     * Create an instance of {@link CodeOrNullListType }
     * 
     */
    public CodeOrNullListType createCodeOrNullListType() {
        return new CodeOrNullListType();
    }

    /**
     * Create an instance of {@link PriorityLocationPropertyType }
     * 
     */
    public PriorityLocationPropertyType createPriorityLocationPropertyType() {
        return new PriorityLocationPropertyType();
    }

    /**
     * Create an instance of {@link TimePositionType }
     * 
     */
    public TimePositionType createTimePositionType() {
        return new TimePositionType();
    }

    /**
     * Create an instance of {@link OffsetCurveType }
     * 
     */
    public OffsetCurveType createOffsetCurveType() {
        return new OffsetCurveType();
    }

    /**
     * Create an instance of {@link EnvelopeWithTimePeriodType }
     * 
     */
    public EnvelopeWithTimePeriodType createEnvelopeWithTimePeriodType() {
        return new EnvelopeWithTimePeriodType();
    }

    /**
     * Create an instance of {@link TimePeriodPropertyType }
     * 
     */
    public TimePeriodPropertyType createTimePeriodPropertyType() {
        return new TimePeriodPropertyType();
    }

    /**
     * Create an instance of {@link FeatureCollectionType }
     * 
     */
    public FeatureCollectionType createFeatureCollectionType() {
        return new FeatureCollectionType();
    }

    /**
     * Create an instance of {@link GeodesicType }
     * 
     */
    public GeodesicType createGeodesicType() {
        return new GeodesicType();
    }

    /**
     * Create an instance of {@link TinType }
     * 
     */
    public TinType createTinType() {
        return new TinType();
    }

    /**
     * Create an instance of {@link BaseUnitType }
     * 
     */
    public BaseUnitType createBaseUnitType() {
        return new BaseUnitType();
    }

    /**
     * Create an instance of {@link SurfaceType }
     * 
     */
    public SurfaceType createSurfaceType() {
        return new SurfaceType();
    }

    /**
     * Create an instance of {@link AngleChoiceType }
     * 
     */
    public AngleChoiceType createAngleChoiceType() {
        return new AngleChoiceType();
    }

    /**
     * Create an instance of {@link DefinitionProxyType }
     * 
     */
    public DefinitionProxyType createDefinitionProxyType() {
        return new DefinitionProxyType();
    }

    /**
     * Create an instance of {@link AbstractGriddedSurfaceType.Row }
     * 
     */
    public AbstractGriddedSurfaceType.Row createAbstractGriddedSurfaceTypeRow() {
        return new AbstractGriddedSurfaceType.Row();
    }

    /**
     * Create an instance of {@link TimeGeometricPrimitivePropertyType }
     * 
     */
    public TimeGeometricPrimitivePropertyType createTimeGeometricPrimitivePropertyType() {
        return new TimeGeometricPrimitivePropertyType();
    }

    /**
     * Create an instance of {@link CircleType }
     * 
     */
    public CircleType createCircleType() {
        return new CircleType();
    }

    /**
     * Create an instance of {@link ArrayAssociationType }
     * 
     */
    public ArrayAssociationType createArrayAssociationType() {
        return new ArrayAssociationType();
    }

    /**
     * Create an instance of {@link BSplineType }
     * 
     */
    public BSplineType createBSplineType() {
        return new BSplineType();
    }

    /**
     * Create an instance of {@link TinType.ControlPoint }
     * 
     */
    public TinType.ControlPoint createTinTypeControlPoint() {
        return new TinType.ControlPoint();
    }

    /**
     * Create an instance of {@link AbstractRingPropertyType }
     * 
     */
    public AbstractRingPropertyType createAbstractRingPropertyType() {
        return new AbstractRingPropertyType();
    }

    /**
     * Create an instance of {@link AbstractSolidType }
     * 
     */
    public AbstractSolidType createAbstractSolidType() {
        return new AbstractSolidType();
    }

    /**
     * Create an instance of {@link DirectPositionListType }
     * 
     */
    public DirectPositionListType createDirectPositionListType() {
        return new DirectPositionListType();
    }

    /**
     * Create an instance of {@link PointType }
     * 
     */
    public PointType createPointType() {
        return new PointType();
    }

    /**
     * Create an instance of {@link ConventionalUnitType }
     * 
     */
    public ConventionalUnitType createConventionalUnitType() {
        return new ConventionalUnitType();
    }

    /**
     * Create an instance of {@link PointPropertyType }
     * 
     */
    public PointPropertyType createPointPropertyType() {
        return new PointPropertyType();
    }

    /**
     * Create an instance of {@link AssociationType }
     * 
     */
    public AssociationType createAssociationType() {
        return new AssociationType();
    }

    /**
     * Create an instance of {@link ConeType }
     * 
     */
    public ConeType createConeType() {
        return new ConeType();
    }

    /**
     * Create an instance of {@link LengthType }
     * 
     */
    public LengthType createLengthType() {
        return new LengthType();
    }

    /**
     * Create an instance of {@link TimeType }
     * 
     */
    public TimeType createTimeType() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link DegreesType }
     * 
     */
    public DegreesType createDegreesType() {
        return new DegreesType();
    }

    /**
     * Create an instance of {@link TriangleType }
     * 
     */
    public TriangleType createTriangleType() {
        return new TriangleType();
    }

    /**
     * Create an instance of {@link ConversionToPreferredUnitType }
     * 
     */
    public ConversionToPreferredUnitType createConversionToPreferredUnitType() {
        return new ConversionToPreferredUnitType();
    }

    /**
     * Create an instance of {@link TrianglePatchArrayPropertyType }
     * 
     */
    public TrianglePatchArrayPropertyType createTrianglePatchArrayPropertyType() {
        return new TrianglePatchArrayPropertyType();
    }

    /**
     * Create an instance of {@link BoundingShapeType }
     * 
     */
    public BoundingShapeEntry createBoundingShapeType() {
        return new BoundingShapeEntry();
    }

    /**
     * Create an instance of {@link TimeInstantPropertyType }
     * 
     */
    public TimeInstantPropertyType createTimeInstantPropertyType() {
        return new TimeInstantPropertyType();
    }

    /**
     * Create an instance of {@link MeasureType }
     * 
     */
    public MeasureType createMeasureType() {
        return new MeasureType();
    }

    /**
     * Create an instance of {@link KnotType }
     * 
     */
    public KnotType createKnotType() {
        return new KnotType();
    }

    /**
     * Create an instance of {@link PolygonPropertyType }
     * 
     */
    public PolygonPropertyType createPolygonPropertyType() {
        return new PolygonPropertyType();
    }

    /**
     * Create an instance of {@link MeasureListType }
     * 
     */
    public MeasureListType createMeasureListType() {
        return new MeasureListType();
    }

    /**
     * Create an instance of {@link LineStringPropertyType }
     * 
     */
    public LineStringPropertyType createLineStringPropertyType() {
        return new LineStringPropertyType();
    }

    /**
     * Create an instance of {@link FormulaType }
     * 
     */
    public FormulaType createFormulaType() {
        return new FormulaType();
    }

    /**
     * Create an instance of {@link SurfacePatchArrayPropertyType }
     * 
     */
    public SurfacePatchArrayPropertyType createSurfacePatchArrayPropertyType() {
        return new SurfacePatchArrayPropertyType();
    }

    /**
     * Create an instance of {@link SphereType }
     * 
     */
    public SphereType createSphereType() {
        return new SphereType();
    }

    /**
     * Create an instance of {@link DerivedUnitType }
     * 
     */
    public DerivedUnitType createDerivedUnitType() {
        return new DerivedUnitType();
    }

    /**
     * Create an instance of {@link DefinitionType }
     * 
     */
    public DefinitionType createDefinitionType() {
        return new DefinitionType();
    }

    /**
     * Create an instance of {@link MultiGeometryPropertyType }
     * 
     */
    public MultiGeometryPropertyType createMultiGeometryPropertyType() {
        return new MultiGeometryPropertyType();
    }

    /**
     * Create an instance of {@link ArcByBulgeType }
     * 
     */
    public ArcByBulgeType createArcByBulgeType() {
        return new ArcByBulgeType();
    }

    /**
     * Create an instance of {@link CurveArrayPropertyType }
     * 
     */
    public CurveArrayPropertyType createCurveArrayPropertyType() {
        return new CurveArrayPropertyType();
    }

    /**
     * Create an instance of {@link DictionaryType }
     * 
     */
    public DictionaryType createDictionaryType() {
        return new DictionaryType();
    }

    /**
     * Create an instance of {@link IndirectEntryType }
     * 
     */
    public IndirectEntryType createIndirectEntryType() {
        return new IndirectEntryType();
    }

    /**
     * Create an instance of {@link MultiSolidType }
     * 
     */
    public MultiSolidType createMultiSolidType() {
        return new MultiSolidType();
    }

    /**
     * Create an instance of {@link SolidType }
     * 
     */
    public SolidType createSolidType() {
        return new SolidType();
    }

    /**
     * Create an instance of {@link SurfaceArrayPropertyType }
     * 
     */
    public SurfaceArrayPropertyType createSurfaceArrayPropertyType() {
        return new SurfaceArrayPropertyType();
    }

    /**
     * Create an instance of {@link ArrayType }
     * 
     */
    public ArrayType createArrayType() {
        return new ArrayType();
    }

    /**
     * Create an instance of {@link TriangulatedSurfaceType }
     * 
     */
    public TriangulatedSurfaceType createTriangulatedSurfaceType() {
        return new TriangulatedSurfaceType();
    }

    /**
     * Create an instance of {@link CurveSegmentArrayPropertyType }
     * 
     */
    public CurveSegmentArrayPropertyType createCurveSegmentArrayPropertyType() {
        return new CurveSegmentArrayPropertyType();
    }

    /**
     * Create an instance of {@link CylinderType }
     * 
     */
    public CylinderType createCylinderType() {
        return new CylinderType();
    }

    /**
     * Create an instance of {@link KnotPropertyType }
     * 
     */
    public KnotPropertyType createKnotPropertyType() {
        return new KnotPropertyType();
    }

    /**
     * Create an instance of {@link DMSAngleType }
     * 
     */
    public DMSAngleType createDMSAngleType() {
        return new DMSAngleType();
    }

    /**
     * Create an instance of {@link MultiPointType }
     * 
     */
    public MultiPointType createMultiPointType() {
        return new MultiPointType();
    }

    /**
     * Create an instance of {@link EnvelopeType }
     * 
     */
    public EnvelopeEntry createEnvelopeType() {
        return new EnvelopeEntry();
    }

    /**
     * Create an instance of {@link AbstractSurfaceType }
     * 
     */
    public AbstractSurfaceType createAbstractSurfaceType() {
        return new AbstractSurfaceType();
    }

    /**
     * Create an instance of {@link FeaturePropertyType }
     * 
     */
    public FeaturePropertyType createFeaturePropertyType() {
        return new FeaturePropertyType();
    }

    /**
     * Create an instance of {@link DerivationUnitTermType }
     * 
     */
    public DerivationUnitTermType createDerivationUnitTermType() {
        return new DerivationUnitTermType();
    }

    /**
     * Create an instance of {@link LinearRingPropertyType }
     * 
     */
    public LinearRingPropertyType createLinearRingPropertyType() {
        return new LinearRingPropertyType();
    }

    /**
     * Create an instance of {@link UnitDefinitionType }
     * 
     */
    public UnitDefinitionType createUnitDefinitionType() {
        return new UnitDefinitionType();
    }

    /**
     * Create an instance of {@link GeometryPropertyType }
     * 
     */
    public GeometryPropertyType createGeometryPropertyType() {
        return new GeometryPropertyType();
    }

    /**
     * Create an instance of {@link MultiLineStringPropertyType }
     * 
     */
    public MultiLineStringPropertyType createMultiLineStringPropertyType() {
        return new MultiLineStringPropertyType();
    }

    /**
     * Create an instance of {@link SolidArrayPropertyType }
     * 
     */
    public SolidArrayPropertyType createSolidArrayPropertyType() {
        return new SolidArrayPropertyType();
    }

    /**
     * Create an instance of {@link RelatedTimeType }
     * 
     */
    public RelatedTimeType createRelatedTimeType() {
        return new RelatedTimeType();
    }

    /**
     * Create an instance of {@link ScaleType }
     * 
     */
    public ScaleType createScaleType() {
        return new ScaleType();
    }

    /**
     * Create an instance of {@link TimeIntervalLengthType }
     * 
     */
    public TimeIntervalLengthType createTimeIntervalLengthType() {
        return new TimeIntervalLengthType();
    }

    /**
     * Create an instance of {@link MultiCurveType }
     * 
     */
    public MultiCurveType createMultiCurveType() {
        return new MultiCurveType();
    }

    /**
     * Create an instance of {@link GridLengthType }
     * 
     */
    public GridLengthType createGridLengthType() {
        return new GridLengthType();
    }

    /**
     * Create an instance of {@link UnitOfMeasureType }
     * 
     */
    public UnitOfMeasureType createUnitOfMeasureType() {
        return new UnitOfMeasureType();
    }

    /**
     * Create an instance of {@link MultiLineStringType }
     * 
     */
    public MultiLineStringType createMultiLineStringType() {
        return new MultiLineStringType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceEntry createReferenceType() {
        return new ReferenceEntry();
    }

    /**
     * Create an instance of {@link PolygonPatchArrayPropertyType }
     * 
     */
    public PolygonPatchArrayPropertyType createPolygonPatchArrayPropertyType() {
        return new PolygonPatchArrayPropertyType();
    }

    /**
     * Create an instance of {@link AffinePlacementType }
     * 
     */
    public AffinePlacementType createAffinePlacementType() {
        return new AffinePlacementType();
    }

    /**
     * Create an instance of {@link CircleByCenterPointType }
     * 
     */
    public CircleByCenterPointType createCircleByCenterPointType() {
        return new CircleByCenterPointType();
    }

    /**
     * Create an instance of {@link ClothoidType }
     * 
     */
    public ClothoidType createClothoidType() {
        return new ClothoidType();
    }

    /**
     * Create an instance of {@link GeometricPrimitivePropertyType }
     * 
     */
    public GeometricPrimitivePropertyType createGeometricPrimitivePropertyType() {
        return new GeometricPrimitivePropertyType();
    }

    /**
     * Create an instance of {@link SpeedType }
     * 
     */
    public SpeedType createSpeedType() {
        return new SpeedType();
    }

    /**
     * Create an instance of {@link OrientableCurveType }
     * 
     */
    public OrientableCurveType createOrientableCurveType() {
        return new OrientableCurveType();
    }

    /**
     * Create an instance of {@link AreaType }
     * 
     */
    public AreaType createAreaType() {
        return new AreaType();
    }

    /**
     * Create an instance of {@link CRSRefType }
     *
     */
    public CRSRefType createCRSRefType() {
        return new CRSRefType();
    }

    /**
     * Create an instance of {@link CartesianCSRefType }
     *
     */
    public CartesianCSRefType createCartesianCSRefType() {
        return new CartesianCSRefType();
    }

    /**
     * Create an instance of {@link ImageDatumType }
     *
     */
    public ImageDatumType createImageDatumType() {
        return new ImageDatumType();
    }

    /**
     * Create an instance of {@link ImageDatumRefType }
     *
     */
    public ImageDatumRefType createImageDatumRefType() {
        return new ImageDatumRefType();
    }

    /**
     * Create an instance of {@link CompositeCurveType }
     *
     */
    public CompositeCurveType createCompositeCurveType() {
        return new CompositeCurveType();
    }

    /**
     * Create an instance of {@link CompositeSurfaceType }
     *
     */
    public CompositeSurfaceType createCompositeSurfaceType() {
        return new CompositeSurfaceType();
    }

    /**
     * Create an instance of {@link CoordinateSystemAxisType }
     *
     */
    public CoordinateSystemAxisType createCoordinateSystemAxisType() {
        return new CoordinateSystemAxisType();
    }

    /**
     * Create an instance of {@link CoordinateSystemAxisRefType }
     *
     */
    public CoordinateSystemAxisRefType createCoordinateSystemAxisRefType() {
        return new CoordinateSystemAxisRefType();
    }

    /**
     * Create an instance of {@link CoordinateSystemRefType }
     *
     */
    public CoordinateSystemRefType createCoordinateSystemRefType() {
        return new CoordinateSystemRefType();
    }

    /**
     * Create an instance of {@link DynamicFeatureCollectionType }
     *
     */
    public DynamicFeatureCollectionType createDynamicFeatureCollectionType() {
        return new DynamicFeatureCollectionType();
    }

    /**
     * Create an instance of {@link EngineeringCRSType }
     *
     */
    public EngineeringCRSType createEngineeringCRSType() {
        return new EngineeringCRSType();
    }

    /**
     * Create an instance of {@link DynamicFeatureType }
     *
     */
    public DynamicFeatureType createDynamicFeatureType() {
        return new DynamicFeatureType();
    }

    /**
     * Create an instance of {@link EngineeringDatumRefType }
     *
     */
    public EngineeringDatumRefType createEngineeringDatumRefType() {
        return new EngineeringDatumRefType();
    }

    /**
     * Create an instance of {@link EngineeringDatumType }
     *
     */
    public EngineeringDatumType createEngineeringDatumType() {
        return new EngineeringDatumType();
    }

    /**
     * Create an instance of {@link GeometricComplexPropertyType }
     *
     */
    public GeometricComplexPropertyType createGeometricComplexPropertyType() {
        return new GeometricComplexPropertyType();
    }

    /**
     * Create an instance of {@link GeometricComplexType }
     *
     */
    public GeometricComplexType createGeometricComplexType() {
        return new GeometricComplexType();
    }

    /**
     * Create an instance of {@link IdentifierType }
     *
     */
    public IdentifierType createIdentifierType() {
        return new IdentifierType();
    }

    /**
     * Create an instance of {@link MultiSolidPropertyType }
     * 
     */
    public MultiSolidPropertyType createMultiSolidPropertyType() {
        return new MultiSolidPropertyType();
    }

    /**
     * Create an instance of {@link GeodesicStringType }
     * 
     */
    public GeodesicStringType createGeodesicStringType() {
        return new GeodesicStringType();
    }

    /**
     * Create an instance of {@link MultiPointPropertyType }
     * 
     */
    public MultiPointPropertyType createMultiPointPropertyType() {
        return new MultiPointPropertyType();
    }

    /**
     * Create an instance of {@link TrackType }
     *
     */
    public TrackType createTrackType() {
        return new TrackType();
    }

    /**
     * Create an instance of {@link RectifiedGridType }
     *
     */
    public RectifiedGridType createRectifiedGridType() {
        return new RectifiedGridType();
    }

    /**
     * Create an instance of {@link CompositeSolidType }
     *
     */
    public CompositeSolidType createCompositeSolidType() {
        return new CompositeSolidType();
    }

    /**
     * Create an instance of {@link GridType }
     *
     */
    public GridType createGridType() {
        return new GridType();
    }

    /**
     * Create an instance of {@link GridLimitsType }
     *
     */
    public GridLimitsType createGridLimitsType() {
        return new GridLimitsType();
    }

    /**
     * Create an instance of {@link PixelInCellType }
     *
     */
    public PixelInCellType createPixelInCellType() {
        return new PixelInCellType();
    }

    /**
     * Create an instance of {@link ImageCRSType }
     *
     */
    public ImageCRSType createImageCRSType() {
        return new ImageCRSType();
    }

    /**
     * Create an instance of {@link HistoryPropertyType }
     *
     */
    public HistoryPropertyType createHistoryPropertyType() {
        return new HistoryPropertyType();
    }

    /**
     * Create an instance of {@link TemporalCRSType }
     *
     */
    public TemporalCRSType createTemporalCRSType() {
        return new TemporalCRSType();
    }

    /**
     * Create an instance of {@link TemporalCSRefType }
     *
     */
    public TemporalCSRefType createTemporalCSRefType() {
        return new TemporalCSRefType();
    }

    /**
     * Create an instance of {@link TemporalCSType }
     *
     */
    public TemporalCSType createTemporalCSType() {
        return new TemporalCSType();
    }

    /**
     * Create an instance of {@link TemporalDatumRefType }
     *
     */
    public TemporalDatumRefType createTemporalDatumRefType() {
        return new TemporalDatumRefType();
    }

    /**
     * Create an instance of {@link TemporalDatumType }
     *
     */
    public TemporalDatumType createTemporalDatumType() {
        return new TemporalDatumType();
    }

    /**
     * Create an instance of {@link GridEnvelopeType }
     *
     */
    public GridEnvelopeType createGridEnvelopeType() {
        return new GridEnvelopeType();
    }

    /**
     * Create an instance of {@link DirectionPropertyType }
     *
     */
    public DirectionPropertyType createDirectionPropertyType() {
        return new DirectionPropertyType();
    }

    /**
     * Create an instance of {@link DirectionVectorType }
     *
     */
    public DirectionVectorType createDirectionVectorType() {
        return new DirectionVectorType();
    }

    /**
     * Create an instance of {@link MovingObjectStatusType }
     *
     */
    public MovingObjectStatusType createMovingObjectStatusType() {
        return new MovingObjectStatusType();
    }

    /**
     * Create an instance of {@link ObliqueCartesianCSRefType }
     *
     */
    public ObliqueCartesianCSRefType createObliqueCartesianCSRefType() {
        return new ObliqueCartesianCSRefType();
    }

    /**
     * Create an instance of {@link ObliqueCartesianCSType }
     *
     */
    public ObliqueCartesianCSType createObliqueCartesianCSType() {
        return new ObliqueCartesianCSType();
    }

    /**
     * Create an instance of {@link LinearRingType }
     * 
     */
    public LinearRingType createLinearRingType() {
        return new LinearRingType();
    }

    /**
     * Create an instance of {@link PolyhedralSurfaceType }
     * 
     */
    public PolyhedralSurfaceType createPolyhedralSurfaceType() {
        return new PolyhedralSurfaceType();
    }

    /**
     * Create an instance of {@link ClothoidType.RefLocation }
     * 
     */
    public ClothoidType.RefLocation createClothoidTypeRefLocation() {
        return new ClothoidType.RefLocation();
    }

    /**
     * Create an instance of {@link ArcByCenterPointType }
     * 
     */
    public ArcByCenterPointType createArcByCenterPointType() {
        return new ArcByCenterPointType();
    }

    /**
     * Create an instance of {@link MetaDataPropertyType }
     * 
     */
    public MetaDataPropertyType createMetaDataPropertyType() {
        return new MetaDataPropertyType();
    }

    /**
     * Create an instance of {@link PolygonPatchType }
     * 
     */
    public PolygonPatchType createPolygonPatchType() {
        return new PolygonPatchType();
    }

    /**
     * Create an instance of {@link LineStringSegmentType }
     * 
     */
    public LineStringSegmentType createLineStringSegmentType() {
        return new LineStringSegmentType();
    }

    /**
     * Create an instance of {@link DirectPositionType }
     * 
     */
    public DirectPositionType createDirectPositionType() {
        return new DirectPositionType();
    }

    /**
     * Create an instance of {@link ArcStringType }
     * 
     */
    public ArcStringType createArcStringType() {
        return new ArcStringType();
    }

    /**
     * Create an instance of {@link LocationPropertyType }
     * 
     */
    public LocationPropertyType createLocationPropertyType() {
        return new LocationPropertyType();
    }

    /**
     * Create an instance of {@link TimePeriodType }
     * 
     */
    public TimePeriodType createTimePeriodType() {
        return new TimePeriodType();
    }

    /**
     * Create an instance of {@link CurvePropertyType }
     * 
     */
    public CurvePropertyType createCurvePropertyType() {
        return new CurvePropertyType();
    }

    /**
     * Create an instance of {@link ArcStringByBulgeType }
     * 
     */
    public ArcStringByBulgeType createArcStringByBulgeType() {
        return new ArcStringByBulgeType();
    }

    /**
     * Create an instance of {@link CodeListType }
     * 
     */
    public CodeListType createCodeListType() {
        return new CodeListType();
    }

    /**
     * Create an instance of {@link VolumeType }
     * 
     */
    public VolumeType createVolumeType() {
        return new VolumeType();
    }

    /**
     * Create an instance of {@link CubicSplineType }
     * 
     */
    public CubicSplineType createCubicSplineType() {
        return new CubicSplineType();
    }

    /**
     * Create an instance of {@link FeatureArrayPropertyType }
     * 
     */
    public FeatureArrayPropertyType createFeatureArrayPropertyType() {
        return new FeatureArrayPropertyType();
    }

    /**
     * Create an instance of {@link AbstractParametricCurveSurfaceType }
     * 
     */
    public AbstractParametricCurveSurfaceType createAbstractParametricCurveSurfaceType() {
        return new AbstractParametricCurveSurfaceType();
    }

    /**
     * Create an instance of {@link OrientableSurfaceType }
     * 
     */
    public OrientableSurfaceType createOrientableSurfaceType() {
        return new OrientableSurfaceType();
    }

    /**
     * Create an instance of {@link ArcType }
     * 
     */
    public ArcType createArcType() {
        return new ArcType();
    }

    /**
     * Create an instance of {@link CoordType }
     * 
     */
    public CoordType createCoordType() {
        return new CoordType();
    }

    /**
     * Create an instance of {@link MultiSurfacePropertyType }
     * 
     */
    public MultiSurfacePropertyType createMultiSurfacePropertyType() {
        return new MultiSurfacePropertyType();
    }

    /**
     * Create an instance of {@link MultiPolygonPropertyType }
     * 
     */
    public MultiPolygonPropertyType createMultiPolygonPropertyType() {
        return new MultiPolygonPropertyType();
    }

    /**
     * Create an instance of {@link RectangleType }
     * 
     */
    public RectangleType createRectangleType() {
        return new RectangleType();
    }

    /**
     * Create an instance of {@link MultiSurfaceType }
     * 
     */
    public MultiSurfaceType createMultiSurfaceType() {
        return new MultiSurfaceType();
    }

    /**
     * Create an instance of {@link DictionaryEntryType }
     * 
     */
    public DictionaryEntryType createDictionaryEntryType() {
        return new DictionaryEntryType();
    }

    /**
     * Create an instance of {@link AngleType }
     * 
     */
    public AngleType createAngleType() {
        return new AngleType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnitDefinitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "UnitDefinition", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<UnitDefinitionType> createUnitDefinition(UnitDefinitionType value) {
        return new JAXBElement<UnitDefinitionType>(_UnitDefinition_QNAME, UnitDefinitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "outerBoundaryIs", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "exterior")
    public JAXBElement<AbstractRingPropertyType> createOuterBoundaryIs(AbstractRingPropertyType value) {
        return new JAXBElement<AbstractRingPropertyType>(_OuterBoundaryIs_QNAME, AbstractRingPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiLineString", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiLineStringType> createMultiLineString(MultiLineStringType value) {
        return new JAXBElement<MultiLineStringType>(_MultiLineString_QNAME, MultiLineStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_GeometricAggregate", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<AbstractGeometricAggregateType> createGeometricAggregate(AbstractGeometricAggregateType value) {
        return new JAXBElement<AbstractGeometricAggregateType>(_GeometricAggregate_QNAME, AbstractGeometricAggregateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BSplineType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "BSpline", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<BSplineType> createBSpline(BSplineType value) {
        return new JAXBElement<BSplineType>(_BSpline_QNAME, BSplineType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePrimitivePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "validTime")
    public JAXBElement<TimePrimitivePropertyType> createValidTime(TimePrimitivePropertyType value) {
        return new JAXBElement<TimePrimitivePropertyType>(_ValidTime_QNAME, TimePrimitivePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "solidProperty")
    public JAXBElement<SolidPropertyType> createSolidProperty(SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_SolidProperty_QNAME, SolidPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TriangulatedSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Surface")
    public JAXBElement<TriangulatedSurfaceType> createTriangulatedSurface(TriangulatedSurfaceType value) {
        return new JAXBElement<TriangulatedSurfaceType>(_TriangulatedSurface_QNAME, TriangulatedSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LinearRing", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractRing")
    public JAXBElement<LinearRingType> createLinearRing(LinearRingType value) {
        return new JAXBElement<LinearRingType>(_LinearRing_QNAME, LinearRingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringSegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LineStringSegment", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<LineStringSegmentType> createLineStringSegment(LineStringSegmentType value) {
        return new JAXBElement<LineStringSegmentType>(_LineStringSegment_QNAME, LineStringSegmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DictionaryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DefinitionCollection", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<DictionaryType> createDefinitionCollection(DictionaryType value) {
        return new JAXBElement<DictionaryType>(_DefinitionCollection_QNAME, DictionaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "innerBoundaryIs", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "interior")
    public JAXBElement<AbstractRingPropertyType> createInnerBoundaryIs(AbstractRingPropertyType value) {
        return new JAXBElement<AbstractRingPropertyType>(_InnerBoundaryIs_QNAME, AbstractRingPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimePrimitiveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimePrimitive", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeObject")
    public JAXBElement<AbstractTimePrimitiveType> createAbstractTimePrimitive(AbstractTimePrimitiveType value) {
        return new JAXBElement<AbstractTimePrimitiveType>(AbstractTimePrimitive_QNAME, AbstractTimePrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_reference")
    public JAXBElement<ReferenceEntry> createReference(ReferenceEntry value) {
        return new JAXBElement<ReferenceEntry>(_Reference_QNAME, ReferenceEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSolidPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiSolidProperty")
    public JAXBElement<MultiSolidPropertyType> createMultiSolidProperty(MultiSolidPropertyType value) {
        return new JAXBElement<MultiSolidPropertyType>(_MultiSolidProperty_QNAME, MultiSolidPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGriddedSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_GriddedSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_ParametricCurveSurface")
    public JAXBElement<AbstractGriddedSurfaceType> createGriddedSurface(AbstractGriddedSurfaceType value) {
        return new JAXBElement<AbstractGriddedSurfaceType>(_GriddedSurface_QNAME, AbstractGriddedSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "solidMembers")
    public JAXBElement<SolidArrayPropertyType> createSolidMembers(SolidArrayPropertyType value) {
        return new JAXBElement<SolidArrayPropertyType>(_SolidMembers_QNAME, SolidArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnitOfMeasureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "unitOfMeasure")
    public JAXBElement<UnitOfMeasureType> createUnitOfMeasure(UnitOfMeasureType value) {
        return new JAXBElement<UnitOfMeasureType>(_UnitOfMeasure_QNAME, UnitOfMeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CircleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Circle", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Arc")
    public JAXBElement<CircleType> createCircle(CircleType value) {
        return new JAXBElement<CircleType>(_Circle_QNAME, CircleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveProperty")
    public JAXBElement<CurvePropertyType> createCurveProperty(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_CurveProperty_QNAME, CurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConventionalUnitType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ConventionalUnit", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "UnitDefinition")
    public JAXBElement<ConventionalUnitType> createConventionalUnit(ConventionalUnitType value) {
        return new JAXBElement<ConventionalUnitType>(_ConventionalUnit_QNAME, ConventionalUnitType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "description")
    public JAXBElement<StringOrRefType> createDescription(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_Description_QNAME, StringOrRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfaceArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "surfaceMembers")
    public JAXBElement<SurfaceArrayPropertyType> createSurfaceMembers(SurfaceArrayPropertyType value) {
        return new JAXBElement<SurfaceArrayPropertyType>(_SurfaceMembers_QNAME, SurfaceArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPatchType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "PolygonPatch", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<PolygonPatchType> createPolygonPatch(PolygonPatchType value) {
        return new JAXBElement<PolygonPatchType>(_PolygonPatch_QNAME, PolygonPatchType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSolid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<AbstractSolidType> createAbstractSolid(AbstractSolidType value) {
        return new JAXBElement<AbstractSolidType>(_AbstractSolid_QNAME, AbstractSolidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AffinePlacementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AffinePlacement")
    public JAXBElement<AffinePlacementType> createAffinePlacement(AffinePlacementType value) {
        return new JAXBElement<AffinePlacementType>(_AffinePlacement_QNAME, AffinePlacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcByCenterPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ArcByCenterPoint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<ArcByCenterPointType> createArcByCenterPoint(ArcByCenterPointType value) {
        return new JAXBElement<ArcByCenterPointType>(_ArcByCenterPoint_QNAME, ArcByCenterPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectPositionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "posList")
    public JAXBElement<DirectPositionListType> createPosList(DirectPositionListType value) {
        return new JAXBElement<DirectPositionListType>(_PosList_QNAME, DirectPositionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetaDataPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "metaDataProperty")
    public JAXBElement<MetaDataPropertyType> createMetaDataProperty(MetaDataPropertyType value) {
        return new JAXBElement<MetaDataPropertyType>(_MetaDataProperty_QNAME, MetaDataPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeIntervalLengthType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "timeInterval")
    public JAXBElement<TimeIntervalLengthType> createTimeInterval(TimeIntervalLengthType value) {
        return new JAXBElement<TimeIntervalLengthType>(_TimeInterval_QNAME, TimeIntervalLengthType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "surfaceProperty")
    public JAXBElement<SurfacePropertyType> createSurfaceProperty(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_SurfaceProperty_QNAME, SurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiSolid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiSolidType> createMultiSolid(MultiSolidType value) {
        return new JAXBElement<MultiSolidType>(_MultiSolid_QNAME, MultiSolidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "OrientableCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurve")
    public JAXBElement<OrientableCurveType> createOrientableCurve(OrientableCurveType value) {
        return new JAXBElement<OrientableCurveType>(_OrientableCurve_QNAME, OrientableCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiPosition")
    public JAXBElement<MultiPointPropertyType> createMultiPosition(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiPosition_QNAME, MultiPointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "surfaceMember")
    public JAXBElement<SurfacePropertyType> createSurfaceMember(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_SurfaceMember_QNAME, SurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "definitionRef")
    public JAXBElement<ReferenceEntry> createDefinitionRef(ReferenceEntry value) {
        return new JAXBElement<ReferenceEntry>(_DefinitionRef_QNAME, ReferenceEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiPolygon", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiPolygonType> createMultiPolygon(MultiPolygonType value) {
        return new JAXBElement<MultiPolygonType>(_MultiPolygon_QNAME, MultiPolygonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Solid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSolid")
    public JAXBElement<SolidType> createSolid(SolidType value) {
        return new JAXBElement<SolidType>(_Solid_QNAME, SolidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "measure")
    public JAXBElement<MeasureType> createMeasure(MeasureType value) {
        return new JAXBElement<MeasureType>(_Measure_QNAME, MeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "polygonMember")
    public JAXBElement<PolygonPropertyType> createPolygonMember(PolygonPropertyType value) {
        return new JAXBElement<PolygonPropertyType>(_PolygonMember_QNAME, PolygonPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiGeometry", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiGeometryType> createMultiGeometry(MultiGeometryType value) {
        return new JAXBElement<MultiGeometryType>(_MultiGeometry_QNAME, MultiGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayAssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "members")
    public JAXBElement<ArrayAssociationType> createMembers(ArrayAssociationType value) {
        return new JAXBElement<ArrayAssociationType>(_Members_QNAME, ArrayAssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_association")
    public JAXBElement<AssociationType> createAssociation(AssociationType value) {
        return new JAXBElement<AssociationType>(_Association_QNAME, AssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCurveProperty")
    public JAXBElement<MultiCurvePropertyType> createMultiCurveProperty(MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_MultiCurveProperty_QNAME, MultiCurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPatchArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "polygonPatches", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "patches")
    public JAXBElement<PolygonPatchArrayPropertyType> createPolygonPatches(PolygonPatchArrayPropertyType value) {
        return new JAXBElement<PolygonPatchArrayPropertyType>(_PolygonPatches_QNAME, PolygonPatchArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "PolyhedralSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Surface")
    public JAXBElement<PolyhedralSurfaceType> createPolyhedralSurface(PolyhedralSurfaceType value) {
        return new JAXBElement<PolyhedralSurfaceType>(_PolyhedralSurface_QNAME, PolyhedralSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "name")
    public JAXBElement<CodeType> createName(CodeType value) {
        return new JAXBElement<CodeType>(_Name_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TimePeriod", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeGeometricPrimitive")
    public JAXBElement<TimePeriodType> createTimePeriod(TimePeriodType value) {
        return new JAXBElement<TimePeriodType>(_TimePeriod_QNAME, TimePeriodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<AbstractCurveType> createAbstractCurve(AbstractCurveType value) {
        return new JAXBElement<AbstractCurveType>(_AbstractCurve_QNAME, AbstractCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConversionToPreferredUnitType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "roughConversionToPreferredUnit")
    public JAXBElement<ConversionToPreferredUnitType> createRoughConversionToPreferredUnit(ConversionToPreferredUnitType value) {
        return new JAXBElement<ConversionToPreferredUnitType>(_RoughConversionToPreferredUnit_QNAME, ConversionToPreferredUnitType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClothoidType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Clothoid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<ClothoidType> createClothoid(ClothoidType value) {
        return new JAXBElement<ClothoidType>(_Clothoid_QNAME, ClothoidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGeometricPrimitive", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<AbstractGeometricPrimitiveType> createAbstractGeometricPrimitive(AbstractGeometricPrimitiveType value) {
        return new JAXBElement<AbstractGeometricPrimitiveType>(_AbstractGeometricPrimitive_QNAME, AbstractGeometricPrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Null")
    public JAXBElement<List<String>> createNull(List<String> value) {
        return new JAXBElement<List<String>>(_Null_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointProperty")
    public JAXBElement<PointPropertyType> createPointProperty(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_PointProperty_QNAME, PointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "geometryMember")
    public JAXBElement<GeometryPropertyType> createGeometryMember(GeometryPropertyType value) {
        return new JAXBElement<GeometryPropertyType>(_GeometryMember_QNAME, GeometryPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiEdgeOf")
    public JAXBElement<MultiCurvePropertyType> createMultiEdgeOf(MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_MultiEdgeOf_QNAME, MultiCurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Curve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurve")
    public JAXBElement<CurveType> createCurve(CurveType value) {
        return new JAXBElement<CurveType>(_Curve_QNAME, CurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveMembers")
    public JAXBElement<CurveArrayPropertyType> createCurveMembers(CurveArrayPropertyType value) {
        return new JAXBElement<CurveArrayPropertyType>(_CurveMembers_QNAME, CurveArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "lineStringMember")
    public JAXBElement<LineStringPropertyType> createLineStringMember(LineStringPropertyType value) {
        return new JAXBElement<LineStringPropertyType>(_LineStringMember_QNAME, LineStringPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Array", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<ArrayType> createArray(ArrayType value) {
        return new JAXBElement<ArrayType>(_Array_QNAME, ArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectPositionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pos")
    public JAXBElement<DirectPositionType> createPos(DirectPositionType value) {
        return new JAXBElement<DirectPositionType>(_Pos_QNAME, DirectPositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Arc", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "ArcString")
    public JAXBElement<ArcType> createArc(ArcType value) {
        return new JAXBElement<ArcType>(_Arc_QNAME, ArcType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "exterior")
    public JAXBElement<AbstractRingPropertyType> createExterior(AbstractRingPropertyType value) {
        return new JAXBElement<AbstractRingPropertyType>(_Exterior_QNAME, AbstractRingPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericMetaDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "GenericMetaData", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_MetaData")
    public JAXBElement<GenericMetaDataType> createGenericMetaData(GenericMetaDataType value) {
        return new JAXBElement<GenericMetaDataType>(_GenericMetaData_QNAME, GenericMetaDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Surface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<SurfaceType> createSurface(SurfaceType value) {
        return new JAXBElement<SurfaceType>(_Surface_QNAME, SurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Point", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<PointType> createPoint(PointType value) {
        return new JAXBElement<PointType>(_Point_QNAME, PointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coord")
    public JAXBElement<CoordType> createCoord(CoordType value) {
        return new JAXBElement<CoordType>(_Coord_QNAME, CoordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCoverage")
    public JAXBElement<MultiSurfacePropertyType> createMultiCoverage(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiCoverage_QNAME, MultiSurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "polygonProperty")
    public JAXBElement<PolygonPropertyType> createPolygonProperty(PolygonPropertyType value) {
        return new JAXBElement<PolygonPropertyType>(_PolygonProperty_QNAME, PolygonPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiSurfaceType> createMultiSurface(MultiSurfaceType value) {
        return new JAXBElement<MultiSurfaceType>(_MultiSurface_QNAME, MultiSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "angle")
    public JAXBElement<MeasureType> createAngle(MeasureType value) {
        return new JAXBElement<MeasureType>(_Angle_QNAME, MeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "solidMember")
    public JAXBElement<SolidPropertyType> createSolidMember(SolidPropertyType value) {
        return new JAXBElement<SolidPropertyType>(_SolidMember_QNAME, SolidPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeodesicType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Geodesic", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "GeodesicString")
    public JAXBElement<GeodesicType> createGeodesic(GeodesicType value) {
        return new JAXBElement<GeodesicType>(_Geodesic_QNAME, GeodesicType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "OrientableSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<OrientableSurfaceType> createOrientableSurface(OrientableSurfaceType value) {
        return new JAXBElement<OrientableSurfaceType>(_OrientableSurface_QNAME, OrientableSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractObject")
    public JAXBElement<Object> createObject(Object value) {
        return new JAXBElement<Object>(AbstractObject_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "member")
    public JAXBElement<AssociationType> createMember(AssociationType value) {
        return new JAXBElement<AssociationType>(_Member_QNAME, AssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveMember")
    public JAXBElement<CurvePropertyType> createCurveMember(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_CurveMember_QNAME, CurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "decimalMinutes")
    public JAXBElement<BigDecimal> createDecimalMinutes(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_DecimalMinutes_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TinType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Tin", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "TriangulatedSurface")
    public JAXBElement<TinType> createTin(TinType value) {
        return new JAXBElement<TinType>(_Tin_QNAME, TinType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RectangleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Rectangle", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<RectangleType> createRectangle(RectangleType value) {
        return new JAXBElement<RectangleType>(_Rectangle_QNAME, RectangleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DerivationUnitTermType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "derivationUnitTerm")
    public JAXBElement<DerivationUnitTermType> createDerivationUnitTerm(DerivationUnitTermType value) {
        return new JAXBElement<DerivationUnitTermType>(_DerivationUnitTerm_QNAME, DerivationUnitTermType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Polygon", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<PolygonType> createPolygon(PolygonType value) {
        return new JAXBElement<PolygonType>(_Polygon_QNAME, PolygonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DefinitionProxyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DefinitionProxy", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<DefinitionProxyType> createDefinitionProxy(DefinitionProxyType value) {
        return new JAXBElement<DefinitionProxyType>(_DefinitionProxy_QNAME, DefinitionProxyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "seconds")
    public JAXBElement<BigDecimal> createSeconds(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Seconds_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CubicSplineType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CubicSpline", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<CubicSplineType> createCubicSpline(CubicSplineType value) {
        return new JAXBElement<CubicSplineType>(_CubicSpline_QNAME, CubicSplineType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "centerLineOf")
    public JAXBElement<CurvePropertyType> createCenterLineOf(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_CenterLineOf_QNAME, CurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Ring", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractRing")
    public JAXBElement<RingType> createRing(RingType value) {
        return new JAXBElement<RingType>(_Ring_QNAME, RingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "catalogSymbol")
    public JAXBElement<CodeType> createCatalogSymbol(CodeType value) {
        return new JAXBElement<CodeType>(_CatalogSymbol_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "extentOf")
    public JAXBElement<SurfacePropertyType> createExtentOf(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_ExtentOf_QNAME, SurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DerivedUnitType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DerivedUnit", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "UnitDefinition")
    public JAXBElement<DerivedUnitType> createDerivedUnit(DerivedUnitType value) {
        return new JAXBElement<DerivedUnitType>(_DerivedUnit_QNAME, DerivedUnitType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ArcString", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<ArcStringType> createArcString(ArcStringType value) {
        return new JAXBElement<ArcStringType>(_ArcString_QNAME, ArcStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSurfacePatchType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSurfacePatch")
    public JAXBElement<AbstractSurfacePatchType> createAbstractSurfacePatch(AbstractSurfacePatchType value) {
        return new JAXBElement<AbstractSurfacePatchType>(AbstractSurfacePatch_QNAME, AbstractSurfacePatchType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiExtentOf")
    public JAXBElement<MultiSurfacePropertyType> createMultiExtentOf(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiExtentOf_QNAME, MultiSurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiGeometryPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiGeometryProperty")
    public JAXBElement<MultiGeometryPropertyType> createMultiGeometryProperty(MultiGeometryPropertyType value) {
        return new JAXBElement<MultiGeometryPropertyType>(_MultiGeometryProperty_QNAME, MultiGeometryPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCenterOf")
    public JAXBElement<MultiPointPropertyType> createMultiCenterOf(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiCenterOf_QNAME, MultiPointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingShapeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "boundedBy")
    public JAXBElement<BoundingShapeEntry> createBoundedBy(BoundingShapeEntry value) {
        return new JAXBElement<BoundingShapeEntry>(_BoundedBy_QNAME, BoundingShapeEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCurveSegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCurveSegment")
    public JAXBElement<AbstractCurveSegmentType> createAbstractCurveSegment(AbstractCurveSegmentType value) {
        return new JAXBElement<AbstractCurveSegmentType>(AbstractCurveSegment_QNAME, AbstractCurveSegmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "FeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<FeatureCollectionType> createFeatureCollection(FeatureCollectionType value) {
        return new JAXBElement<FeatureCollectionType>(_FeatureCollection_QNAME, FeatureCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "interior")
    public JAXBElement<AbstractRingPropertyType> createInterior(AbstractRingPropertyType value) {
        return new JAXBElement<AbstractRingPropertyType>(_Interior_QNAME, AbstractRingPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocationPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "location")
    public JAXBElement<LocationPropertyType> createLocation(LocationPropertyType value) {
        return new JAXBElement<LocationPropertyType>(_Location_QNAME, LocationPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CircleByCenterPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CircleByCenterPoint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "ArcByCenterPoint")
    public JAXBElement<CircleByCenterPointType> createCircleByCenterPoint(CircleByCenterPointType value) {
        return new JAXBElement<CircleByCenterPointType>(_CircleByCenterPoint_QNAME, CircleByCenterPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SphereType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Sphere", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GriddedSurface")
    public JAXBElement<SphereType> createSphere(SphereType value) {
        return new JAXBElement<SphereType>(_Sphere_QNAME, SphereType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SolidArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "solidArrayProperty")
    public JAXBElement<SolidArrayPropertyType> createSolidArrayProperty(SolidArrayPropertyType value) {
        return new JAXBElement<SolidArrayPropertyType>(_SolidArrayProperty_QNAME, SolidArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointMembers")
    public JAXBElement<PointArrayPropertyType> createPointMembers(PointArrayPropertyType value) {
        return new JAXBElement<PointArrayPropertyType>(_PointMembers_QNAME, PointArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DegreesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "degrees")
    public JAXBElement<DegreesType> createDegrees(DegreesType value) {
        return new JAXBElement<DegreesType>(_Degrees_QNAME, DegreesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "minutes")
    public JAXBElement<Integer> createMinutes(Integer value) {
        return new JAXBElement<Integer>(_Minutes_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractMetaDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_MetaData", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<AbstractMetaDataType> createMetaData(AbstractMetaDataType value) {
        return new JAXBElement<AbstractMetaDataType>(_MetaData_QNAME, AbstractMetaDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriorityLocationPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "priorityLocation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "location")
    public JAXBElement<PriorityLocationPropertyType> createPriorityLocation(PriorityLocationPropertyType value) {
        return new JAXBElement<PriorityLocationPropertyType>(_PriorityLocation_QNAME, PriorityLocationPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_strictAssociation")
    public JAXBElement<AssociationType> createStrictAssociation(AssociationType value) {
        return new JAXBElement<AssociationType>(_StrictAssociation_QNAME, AssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeWithTimePeriodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "EnvelopeWithTimePeriod", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Envelope")
    public JAXBElement<EnvelopeWithTimePeriodType> createEnvelopeWithTimePeriod(EnvelopeWithTimePeriodType value) {
        return new JAXBElement<EnvelopeWithTimePeriodType>(_EnvelopeWithTimePeriod_QNAME, EnvelopeWithTimePeriodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcByBulgeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ArcByBulge", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "ArcStringByBulge")
    public JAXBElement<ArcByBulgeType> createArcByBulge(ArcByBulgeType value) {
        return new JAXBElement<ArcByBulgeType>(_ArcByBulge_QNAME, ArcByBulgeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveArrayProperty")
    public JAXBElement<CurveArrayPropertyType> createCurveArrayProperty(CurveArrayPropertyType value) {
        return new JAXBElement<CurveArrayPropertyType>(_CurveArrayProperty_QNAME, CurveArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeObject", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractTimeObjectType> createAbstractTimeObject(AbstractTimeObjectType value) {
        return new JAXBElement<AbstractTimeObjectType>(AbstractTimeObject_QNAME, AbstractTimeObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointRep")
    public JAXBElement<PointPropertyType> createPointRep(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_PointRep_QNAME, PointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "centerOf")
    public JAXBElement<PointPropertyType> createCenterOf(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_CenterOf_QNAME, PointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointArrayProperty")
    public JAXBElement<PointArrayPropertyType> createPointArrayProperty(PointArrayPropertyType value) {
        return new JAXBElement<PointArrayPropertyType>(_PointArrayProperty_QNAME, PointArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractFeatureCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractFeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<AbstractFeatureCollectionType> createAbstractFeatureCollection(AbstractFeatureCollectionType value) {
        return new JAXBElement<AbstractFeatureCollectionType>(_AbstractFeatureCollection_QNAME, AbstractFeatureCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DMSAngleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "dmsAngle")
    public JAXBElement<DMSAngleType> createDmsAngle(DMSAngleType value) {
        return new JAXBElement<DMSAngleType>(_DmsAngle_QNAME, DMSAngleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BagType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Bag", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<BagType> createBag(BagType value) {
        return new JAXBElement<BagType>(_Bag_QNAME, BagType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OffsetCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "OffsetCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<OffsetCurveType> createOffsetCurve(OffsetCurveType value) {
        return new JAXBElement<OffsetCurveType>(_OffsetCurve_QNAME, OffsetCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConversionToPreferredUnitType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "conversionToPreferredUnit")
    public JAXBElement<ConversionToPreferredUnitType> createConversionToPreferredUnit(ConversionToPreferredUnitType value) {
        return new JAXBElement<ConversionToPreferredUnitType>(_ConversionToPreferredUnit_QNAME, ConversionToPreferredUnitType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinatesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coordinates")
    public JAXBElement<CoordinatesType> createCoordinates(CoordinatesType value) {
        return new JAXBElement<CoordinatesType>(_Coordinates_QNAME, CoordinatesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiCurveType> createMultiCurve(MultiCurveType value) {
        return new JAXBElement<MultiCurveType>(_MultiCurve_QNAME, MultiCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiPoint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiPointType> createMultiPoint(MultiPointType value) {
        return new JAXBElement<MultiPointType>(_MultiPoint_QNAME, MultiPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "vector")
    public JAXBElement<VectorType> createVector(VectorType value) {
        return new JAXBElement<VectorType>(_Vector_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "edgeOf")
    public JAXBElement<CurvePropertyType> createEdgeOf(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_EdgeOf_QNAME, CurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCenterLineOf")
    public JAXBElement<MultiCurvePropertyType> createMultiCenterLineOf(MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_MultiCenterLineOf_QNAME, MultiCurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "geometryMembers")
    public JAXBElement<GeometryArrayPropertyType> createGeometryMembers(GeometryArrayPropertyType value) {
        return new JAXBElement<GeometryArrayPropertyType>(_GeometryMembers_QNAME, GeometryArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LocationKeyWord")
    public JAXBElement<CodeType> createLocationKeyWord(CodeType value) {
        return new JAXBElement<CodeType>(_LocationKeyWord_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DictionaryEntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "dictionaryEntry")
    public JAXBElement<DictionaryEntryType> createDictionaryEntry(DictionaryEntryType value) {
        return new JAXBElement<DictionaryEntryType>(_DictionaryEntry_QNAME, DictionaryEntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IndirectEntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "indirectEntry")
    public JAXBElement<IndirectEntryType> createIndirectEntry(IndirectEntryType value) {
        return new JAXBElement<IndirectEntryType>(_IndirectEntry_QNAME, IndirectEntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BezierType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Bezier", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "BSpline")
    public JAXBElement<BezierType> createBezier(BezierType value) {
        return new JAXBElement<BezierType>(_Bezier_QNAME, BezierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiPointProperty")
    public JAXBElement<MultiPointPropertyType> createMultiPointProperty(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiPointProperty_QNAME, MultiPointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePatchArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "patches")
    public JAXBElement<SurfacePatchArrayPropertyType> createPatches(SurfacePatchArrayPropertyType value) {
        return new JAXBElement<SurfacePatchArrayPropertyType>(_Patches_QNAME, SurfacePatchArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArcStringByBulgeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ArcStringByBulge", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<ArcStringByBulgeType> createArcStringByBulge(ArcStringByBulgeType value) {
        return new JAXBElement<ArcStringByBulgeType>(_ArcStringByBulge_QNAME, ArcStringByBulgeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractFeature", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractFeatureEntry> createAbstractFeature(AbstractFeatureEntry value) {
        return new JAXBElement<AbstractFeatureEntry>(_AbstractFeature_QNAME, AbstractFeatureEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiLocation")
    public JAXBElement<MultiPointPropertyType> createMultiLocation(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiLocation_QNAME, MultiPointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeaturePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "featureProperty")
    public JAXBElement<FeaturePropertyType> createFeatureProperty(FeaturePropertyType value) {
        return new JAXBElement<FeaturePropertyType>(_FeatureProperty_QNAME, FeaturePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "lineStringProperty")
    public JAXBElement<LineStringPropertyType> createLineStringProperty(LineStringPropertyType value) {
        return new JAXBElement<LineStringPropertyType>(_LineStringProperty_QNAME, LineStringPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrianglePatchArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "trianglePatches", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "patches")
    public JAXBElement<TrianglePatchArrayPropertyType> createTrianglePatches(TrianglePatchArrayPropertyType value) {
        return new JAXBElement<TrianglePatchArrayPropertyType>(_TrianglePatches_QNAME, TrianglePatchArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractRing", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<AbstractRingType> createAbstractRing(AbstractRingType value) {
        return new JAXBElement<AbstractRingType>(_AbstractRing_QNAME, AbstractRingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePositionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "timePosition")
    public JAXBElement<TimePositionType> createTimePosition(TimePositionType value) {
        return new JAXBElement<TimePositionType>(_TimePosition_QNAME, TimePositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiSurfaceProperty")
    public JAXBElement<MultiSurfacePropertyType> createMultiSurfaceProperty(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiSurfaceProperty_QNAME, MultiSurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DictionaryEntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "definitionMember", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "dictionaryEntry")
    public JAXBElement<DictionaryEntryType> createDefinitionMember(DictionaryEntryType value) {
        return new JAXBElement<DictionaryEntryType>(_DefinitionMember_QNAME, DictionaryEntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfaceArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "surfaceArrayProperty")
    public JAXBElement<SurfaceArrayPropertyType> createSurfaceArrayProperty(SurfaceArrayPropertyType value) {
        return new JAXBElement<SurfaceArrayPropertyType>(_SurfaceArrayProperty_QNAME, SurfaceArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriangleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Triangle", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<TriangleType> createTriangle(TriangleType value) {
        return new JAXBElement<TriangleType>(_Triangle_QNAME, TriangleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Cone", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GriddedSurface")
    public JAXBElement<ConeType> createCone(ConeType value) {
        return new JAXBElement<ConeType>(_Cone_QNAME, ConeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "quantityType")
    public JAXBElement<StringOrRefType> createQuantityType(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_QuantityType_QNAME, StringOrRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeaturePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "featureMember")
    public JAXBElement<FeaturePropertyType> createFeatureMember(FeaturePropertyType value) {
        return new JAXBElement<FeaturePropertyType>(_FeatureMember_QNAME, FeaturePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LineString", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurve")
    public JAXBElement<LineStringType> createLineString(LineStringType value) {
        return new JAXBElement<LineStringType>(_LineString_QNAME, LineStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseUnitType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "BaseUnit", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "UnitDefinition")
    public JAXBElement<BaseUnitType> createBaseUnit(BaseUnitType value) {
        return new JAXBElement<BaseUnitType>(_BaseUnit_QNAME, BaseUnitType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LocationString")
    public JAXBElement<StringOrRefType> createLocationString(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_LocationString_QNAME, StringOrRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Envelope")
    public JAXBElement<EnvelopeEntry> createEnvelope(EnvelopeEntry value) {
        return new JAXBElement<EnvelopeEntry>(_Envelope_QNAME, EnvelopeEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<AbstractSurfaceType> createAbstractSurface(AbstractSurfaceType value) {
        return new JAXBElement<AbstractSurfaceType>(_AbstractSurface_QNAME, AbstractSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DefinitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Definition", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<DefinitionType> createDefinition(DefinitionType value) {
        return new JAXBElement<DefinitionType>(_Definition_QNAME, DefinitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "featureMembers")
    public JAXBElement<FeatureArrayPropertyType> createFeatureMembers(FeatureArrayPropertyType value) {
        return new JAXBElement<FeatureArrayPropertyType>(_FeatureMembers_QNAME, FeatureArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeodesicStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "GeodesicString", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurveSegment")
    public JAXBElement<GeodesicStringType> createGeodesicString(GeodesicStringType value) {
        return new JAXBElement<GeodesicStringType>(_GeodesicString_QNAME, GeodesicStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "baseSurface")
    public JAXBElement<SurfacePropertyType> createBaseSurface(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_BaseSurface_QNAME, SurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointMember")
    public JAXBElement<PointPropertyType> createPointMember(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_PointMember_QNAME, PointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeGeometricPrimitiveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeGeometricPrimitive", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimePrimitive")
    public JAXBElement<AbstractTimeGeometricPrimitiveType> createAbstractTimeGeometricPrimitive(AbstractTimeGeometricPrimitiveType value) {
        return new JAXBElement<AbstractTimeGeometricPrimitiveType>(AbstractTimeGeometricPrimitive_QNAME, AbstractTimeGeometricPrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CylinderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Cylinder", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GriddedSurface")
    public JAXBElement<CylinderType> createCylinder(CylinderType value) {
        return new JAXBElement<CylinderType>(_Cylinder_QNAME, CylinderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "position")
    public JAXBElement<PointPropertyType> createPosition(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_Position_QNAME, PointPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveSegmentArrayPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "segments")
    public JAXBElement<CurveSegmentArrayPropertyType> createSegments(CurveSegmentArrayPropertyType value) {
        return new JAXBElement<CurveSegmentArrayPropertyType>(_Segments_QNAME, CurveSegmentArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractParametricCurveSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_ParametricCurveSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<AbstractParametricCurveSurfaceType> createParametricCurveSurface(AbstractParametricCurveSurfaceType value) {
        return new JAXBElement<AbstractParametricCurveSurfaceType>(_ParametricCurveSurface_QNAME, AbstractParametricCurveSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeComplexType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_TimeComplex", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeObject")
    public JAXBElement<AbstractTimeComplexType> createTimeComplex(AbstractTimeComplexType value) {
        return new JAXBElement<AbstractTimeComplexType>(_TimeComplex_QNAME, AbstractTimeComplexType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DictionaryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Dictionary", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<DictionaryType> createDictionary(DictionaryType value) {
        return new JAXBElement<DictionaryType>(_Dictionary_QNAME, DictionaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "baseCurve")
    public JAXBElement<CurvePropertyType> createBaseCurve(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_BaseCurve_QNAME, CurvePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeInstantType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TimeInstant", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeGeometricPrimitive")
    public JAXBElement<TimeInstantType> createTimeInstant(TimeInstantType value) {
        return new JAXBElement<TimeInstantType>(_TimeInstant_QNAME, TimeInstantType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGeometry", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractGeometryType> createAbstractGeometry(AbstractGeometryType value) {
        return new JAXBElement<AbstractGeometryType>(_AbstractGeometry_QNAME, AbstractGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateOperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateOperation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractCoordinateOperationType> createAbstractCoordinateOperation(AbstractCoordinateOperationType value) {
        return new JAXBElement<AbstractCoordinateOperationType>(_CoordinateOperation_QNAME, AbstractCoordinateOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateSystemType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateSystem", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractCoordinateSystemType> createAbstractCoordinateSystem(AbstractCoordinateSystemType value) {
        return new JAXBElement<AbstractCoordinateSystemType>(AbstractCoordinateSystem_QNAME, AbstractCoordinateSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "imageDatumRef")
    public JAXBElement<ImageDatumRefType> createImageDatumRef(ImageDatumRefType value) {
        return new JAXBElement<ImageDatumRefType>(_ImageDatumRef_QNAME, ImageDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageDatumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ImageDatum", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractDatum")
    public JAXBElement<ImageDatumType> createImageDatum(ImageDatumType value) {
        return new JAXBElement<ImageDatumType>(_ImageDatum_QNAME, ImageDatumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesImageDatum")
    public JAXBElement<ImageDatumRefType> createUsesImageDatum(ImageDatumRefType value) {
        return new JAXBElement<ImageDatumRefType>(_UsesImageDatum_QNAME, ImageDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDatumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractDatum", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractDatumType> createDatum(AbstractDatumType value) {
        return new JAXBElement<AbstractDatumType>(_Datum_QNAME, AbstractDatumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGMLType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGML", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<AbstractGMLEntry> createAbstractGML(AbstractGMLEntry value) {
        return new JAXBElement<AbstractGMLEntry>(_AbstractGML_QNAME, AbstractGMLEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPositionalAccuracyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractpositionalAccuracy")
    public JAXBElement<AbstractPositionalAccuracyType> createPositionalAccuracy(AbstractPositionalAccuracyType value) {
        return new JAXBElement<AbstractPositionalAccuracyType>(_PositionalAccuracy_QNAME, AbstractPositionalAccuracyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceSystemType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractReferenceSystem", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractReferenceSystemType> createReferenceSystem(AbstractReferenceSystemType value) {
        return new JAXBElement<AbstractReferenceSystemType>(_ReferenceSystem_QNAME, AbstractReferenceSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateOperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSingleOperation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateOperation")
    public JAXBElement<AbstractCoordinateOperationType> createSingleOperation(AbstractCoordinateOperationType value) {
        return new JAXBElement<AbstractCoordinateOperationType>(_SingleOperation_QNAME, AbstractCoordinateOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateOperationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractOperation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSingleOperation")
    public JAXBElement<AbstractCoordinateOperationType> createOperation(AbstractCoordinateOperationType value) {
        return new JAXBElement<AbstractCoordinateOperationType>(_Operation_QNAME, AbstractCoordinateOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceSystemType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractReferenceSystem")
    public JAXBElement<AbstractReferenceSystemType> createCRS(AbstractReferenceSystemType value) {
        return new JAXBElement<AbstractReferenceSystemType>(_CRS_QNAME, AbstractReferenceSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeSliceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeSlice", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractTimeSliceType> createTimeSlice(AbstractTimeSliceType value) {
        return new JAXBElement<AbstractTimeSliceType>(_TimeSlice_QNAME, AbstractTimeSliceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CRSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "sourceCRS")
    public JAXBElement<CRSRefType> createSourceCRS(CRSRefType value) {
        return new JAXBElement<CRSRefType>(_SourceCRS_QNAME, CRSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CRSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "targetCRS")
    public JAXBElement<CRSRefType> createTargetCRS(CRSRefType value) {
        return new JAXBElement<CRSRefType>(_TargetCRS_QNAME, CRSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CartesianCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "cartesianCSRef")
    public JAXBElement<CartesianCSRefType> createCartesianCSRef(CartesianCSRefType value) {
        return new JAXBElement<CartesianCSRefType>(_CartesianCSRef_QNAME, CartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CartesianCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesCartesianCS")
    public JAXBElement<CartesianCSRefType> createUsesCartesianCS(CartesianCSRefType value) {
        return new JAXBElement<CartesianCSRefType>(_UsesCartesianCS_QNAME, CartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeCurveType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurve")
    public JAXBElement<CompositeCurveType> createCompositeCurve(CompositeCurveType value) {
        return new JAXBElement<CompositeCurveType>(_CompositeCurve_QNAME, CompositeCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeSolid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSolid")
    public JAXBElement<CompositeSolidType> createCompositeSolid(CompositeSolidType value) {
        return new JAXBElement<CompositeSolidType>(_CompositeSolid_QNAME, CompositeSolidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<CompositeSurfaceType> createCompositeSurface(CompositeSurfaceType value) {
        return new JAXBElement<CompositeSurfaceType>(_CompositeSurface_QNAME, CompositeSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinateSystemAxisRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coordinateSystemAxisRef")
    public JAXBElement<CoordinateSystemAxisRefType> createCoordinateSystemAxisRef(CoordinateSystemAxisRefType value) {
        return new JAXBElement<CoordinateSystemAxisRefType>(_CoordinateSystemAxisRef_QNAME, CoordinateSystemAxisRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinateSystemAxisType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CoordinateSystemAxis", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<CoordinateSystemAxisType> createCoordinateSystemAxis(CoordinateSystemAxisType value) {
        return new JAXBElement<CoordinateSystemAxisType>(_CoordinateSystemAxis_QNAME, CoordinateSystemAxisType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinateSystemAxisRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesAxis")
    public JAXBElement<CoordinateSystemAxisRefType> createUsesAxis(CoordinateSystemAxisRefType value) {
        return new JAXBElement<CoordinateSystemAxisRefType>(_UsesAxis_QNAME, CoordinateSystemAxisRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinateSystemRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coordinateSystemRef")
    public JAXBElement<CoordinateSystemRefType> createCoordinateSystemRef(CoordinateSystemRefType value) {
        return new JAXBElement<CoordinateSystemRefType>(_CoordinateSystemRef_QNAME, CoordinateSystemRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinateSystemRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesCS")
    public JAXBElement<CoordinateSystemRefType> createUsesCS(CoordinateSystemRefType value) {
        return new JAXBElement<CoordinateSystemRefType>(_UsesCS_QNAME, CoordinateSystemRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectionPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "direction")
    public JAXBElement<DirectionPropertyType> createDirection(DirectionPropertyType value) {
        return new JAXBElement<DirectionPropertyType>(_Direction_QNAME, DirectionPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectionVectorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DirectionVector")
    public JAXBElement<DirectionVectorType> createDirectionVector(DirectionVectorType value) {
        return new JAXBElement<DirectionVectorType>(_DirectionVector_QNAME, DirectionVectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageCRSType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ImageCRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateReferenceSystem")
    public JAXBElement<ImageCRSType> createImageCRS(ImageCRSType value) {
        return new JAXBElement<ImageCRSType>(_ImageCRS_QNAME, ImageCRSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EngineeringCRSType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "EngineeringCRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateReferenceSystem")
    public JAXBElement<EngineeringCRSType> createEngineeringCRS(EngineeringCRSType value) {
        return new JAXBElement<EngineeringCRSType>(_EngineeringCRS_QNAME, EngineeringCRSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EngineeringDatumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "EngineeringDatum", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractDatum")
    public JAXBElement<EngineeringDatumType> createEngineeringDatum(EngineeringDatumType value) {
        return new JAXBElement<EngineeringDatumType>(_EngineeringDatum_QNAME, EngineeringDatumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EngineeringDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "engineeringDatumRef")
    public JAXBElement<EngineeringDatumRefType> createEngineeringDatumRef(EngineeringDatumRefType value) {
        return new JAXBElement<EngineeringDatumRefType>(_EngineeringDatumRef_QNAME, EngineeringDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EngineeringDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesEngineeringDatum")
    public JAXBElement<EngineeringDatumRefType> createUsesEngineeringDatum(EngineeringDatumRefType value) {
        return new JAXBElement<EngineeringDatumRefType>(_UsesEngineeringDatum_QNAME, EngineeringDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "validArea")
    public JAXBElement<ExtentType> createValidArea(ExtentType value) {
        return new JAXBElement<ExtentType>(_ValidArea_QNAME, ExtentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CountExtent")
    public JAXBElement<List<String>> createCountExtent(List<String> value) {
        return new JAXBElement<List<String>>(_CountExtent_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometricComplexType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "GeometricComplex", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<GeometricComplexType> createGeometricComplex(GeometricComplexType value) {
        return new JAXBElement<GeometricComplexType>(_GeometricComplex_QNAME, GeometricComplexType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GridType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Grid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<GridType> createGrid(GridType value) {
        return new JAXBElement<GridType>(_Grid_QNAME, GridType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistoryPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "history")
    public JAXBElement<HistoryPropertyType> createHistory(HistoryPropertyType value) {
        return new JAXBElement<HistoryPropertyType>(_History_QNAME, HistoryPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "datumID")
    public JAXBElement<IdentifierType> createDatumID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_DatumID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ellipsoidID")
    public JAXBElement<IdentifierType> createEllipsoidID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_EllipsoidID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "axisID")
    public JAXBElement<IdentifierType> createAxisID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_AxisID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "meridianID")
    public JAXBElement<IdentifierType> createMeridianID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_MeridianID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "methodID")
    public JAXBElement<IdentifierType> createMethodID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_MethodID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coordinateOperationID")
    public JAXBElement<IdentifierType> createCoordinateOperationID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_CoordinateOperationID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "csID")
    public JAXBElement<IdentifierType> createCsID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_CsID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "parameterID")
    public JAXBElement<IdentifierType> createParameterID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_ParameterID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "srsID")
    public JAXBElement<IdentifierType> createSrsID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_SrsID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "groupID")
    public JAXBElement<IdentifierType> createGroupID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_GroupID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MovingObjectStatusType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MovingObjectStatus", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeSlice")
    public JAXBElement<MovingObjectStatusType> createMovingObjectStatus(MovingObjectStatusType value) {
        return new JAXBElement<MovingObjectStatusType>(_MovingObjectStatus_QNAME, MovingObjectStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObliqueCartesianCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesObliqueCartesianCS")
    public JAXBElement<ObliqueCartesianCSRefType> createUsesObliqueCartesianCS(ObliqueCartesianCSRefType value) {
        return new JAXBElement<ObliqueCartesianCSRefType>(_UsesObliqueCartesianCS_QNAME, ObliqueCartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObliqueCartesianCSType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ObliqueCartesianCS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateSystem")
    public JAXBElement<ObliqueCartesianCSType> createObliqueCartesianCS(ObliqueCartesianCSType value) {
        return new JAXBElement<ObliqueCartesianCSType>(_ObliqueCartesianCS_QNAME, ObliqueCartesianCSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObliqueCartesianCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "obliqueCartesianCSRef")
    public JAXBElement<ObliqueCartesianCSRefType> createObliqueCartesianCSRef(ObliqueCartesianCSRefType value) {
        return new JAXBElement<ObliqueCartesianCSRefType>(_ObliqueCartesianCSRef_QNAME, ObliqueCartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PixelInCellType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pixelInCell")
    public JAXBElement<PixelInCellType> createPixelInCell(PixelInCellType value) {
        return new JAXBElement<PixelInCellType>(_PixelInCell_QNAME, PixelInCellType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QuantityExtentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "QuantityExtent")
    public JAXBElement<QuantityExtentType> createQuantityExtent(QuantityExtentType value) {
        return new JAXBElement<QuantityExtentType>(_QuantityExtent_QNAME, QuantityExtentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureOrNullListType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "QuantityList")
    public JAXBElement<MeasureOrNullListType> createQuantityList(MeasureOrNullListType value) {
        return new JAXBElement<MeasureOrNullListType>(_QuantityList_QNAME, MeasureOrNullListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Quantity")
    public JAXBElement<MeasureType> createQuantity(MeasureType value) {
        return new JAXBElement<MeasureType>(_Quantity_QNAME, MeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RectifiedGridType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "RectifiedGrid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Grid")
    public JAXBElement<RectifiedGridType> createRectifiedGrid(RectifiedGridType value) {
        return new JAXBElement<RectifiedGridType>(_RectifiedGrid_QNAME, RectifiedGridType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalDatumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TemporalDatum", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractDatum")
    public JAXBElement<TemporalDatumType> createTemporalDatum(TemporalDatumType value) {
        return new JAXBElement<TemporalDatumType>(_TemporalDatum_QNAME, TemporalDatumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesTemporalDatum")
    public JAXBElement<TemporalDatumRefType> createUsesTemporalDatum(TemporalDatumRefType value) {
        return new JAXBElement<TemporalDatumRefType>(_UsesTemporalDatum_QNAME, TemporalDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalDatumRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "temporalDatumRef")
    public JAXBElement<TemporalDatumRefType> createTemporalDatumRef(TemporalDatumRefType value) {
        return new JAXBElement<TemporalDatumRefType>(_TemporalDatumRef_QNAME, TemporalDatumRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalCSType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TemporalCS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateSystem")
    public JAXBElement<TemporalCSType> createTemporalCS(TemporalCSType value) {
        return new JAXBElement<TemporalCSType>(_TemporalCS_QNAME, TemporalCSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesTemporalCS")
    public JAXBElement<TemporalCSRefType> createUsesTemporalCS(TemporalCSRefType value) {
        return new JAXBElement<TemporalCSRefType>(_UsesTemporalCS_QNAME, TemporalCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalCSRefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "temporalCSRef")
    public JAXBElement<TemporalCSRefType> createTemporalCSRef(TemporalCSRefType value) {
        return new JAXBElement<TemporalCSRefType>(_TemporalCSRef_QNAME, TemporalCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalCRSType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TemporalCRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateReferenceSystem")
    public JAXBElement<TemporalCRSType> createTemporalCRS(TemporalCRSType value) {
        return new JAXBElement<TemporalCRSType>(_TemporalCRS_QNAME, TemporalCRSType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "temporalExtent")
    public JAXBElement<TimePeriodType> createTemporalExtent(TimePeriodType value) {
        return new JAXBElement<TimePeriodType>(_TemporalExtent_QNAME, TimePeriodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrackType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "track", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "history")
    public JAXBElement<TrackType> createTrack(TrackType value) {
        return new JAXBElement<TrackType>(_Track_QNAME, TrackType.class, null, value);
    }

 

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceSystemType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateReferenceSystem", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCRS")
    public JAXBElement<AbstractReferenceSystemType> createCoordinateReferenceSystem(AbstractReferenceSystemType value) {
        return new JAXBElement<AbstractReferenceSystemType>(_CoordinateReferenceSystem_QNAME, AbstractReferenceSystemType.class, null, value);
    }
}
