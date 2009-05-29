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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
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
 * @author Guilhem legal
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AbstractGML_QNAME               = new QName("http://www.opengis.net/gml", "AbstractGML");
    private final static QName _DatumID_QNAME                   = new QName("http://www.opengis.net/gml", "datumID");
    private final static QName _SourceDimensions_QNAME          = new QName("http://www.opengis.net/gml", "sourceDimensions");
    private final static QName _SourceCRS_QNAME                 = new QName("http://www.opengis.net/gml", "sourceCRS");
    private final static QName _UsesCartesianCS_QNAME           = new QName("http://www.opengis.net/gml", "usesCartesianCS");
    private final static QName _DoubleOrNullTupleList_QNAME     = new QName("http://www.opengis.net/gml", "doubleOrNullTupleList");
    private final static QName _InnerBoundaryIs_QNAME           = new QName("http://www.opengis.net/gml", "innerBoundaryIs");
    private final static QName _TimePrimitive_QNAME             = new QName("http://www.opengis.net/gml", "AbstractTimePrimitive");
    private final static QName _ValidArea_QNAME                 = new QName("http://www.opengis.net/gml", "validArea");
    private final static QName _EllipsoidID_QNAME               = new QName("http://www.opengis.net/gml", "ellipsoidID");
    private final static QName _TimeInterval_QNAME              = new QName("http://www.opengis.net/gml", "timeInterval");
    private final static QName _Category_QNAME                  = new QName("http://www.opengis.net/gml", "Category");
    private final static QName _DataSource_QNAME                = new QName("http://www.opengis.net/gml", "dataSource");
    private final static QName _TimePeriod_QNAME                = new QName("http://www.opengis.net/gml", "TimePeriod");
    private final static QName _Null_QNAME                      = new QName("http://www.opengis.net/gml", "Null");
    private final static QName _Pos_QNAME                       = new QName("http://www.opengis.net/gml", "pos");
    private final static QName _CartesianCS_QNAME               = new QName("http://www.opengis.net/gml", "CartesianCS");
    private final static QName _Exterior_QNAME                  = new QName("http://www.opengis.net/gml", "exterior");
    private final static QName _MethodName_QNAME                = new QName("http://www.opengis.net/gml", "methodName");
    private final static QName _MinimumOccurs_QNAME             = new QName("http://www.opengis.net/gml", "minimumOccurs");
    private final static QName _AxisAbbrev_QNAME                = new QName("http://www.opengis.net/gml", "axisAbbrev");
    private final static QName _DecimalMinutes_QNAME            = new QName("http://www.opengis.net/gml", "decimalMinutes");
    private final static QName _Seconds_QNAME                   = new QName("http://www.opengis.net/gml", "seconds");
    private final static QName _TargetCRS_QNAME                 = new QName("http://www.opengis.net/gml", "targetCRS");
    private final static QName _UsesObliqueCartesianCS_QNAME    = new QName("http://www.opengis.net/gml", "usesObliqueCartesianCS");
    private final static QName _MethodFormula_QNAME             = new QName("http://www.opengis.net/gml", "methodFormula");
    private final static QName _Status_QNAME                    = new QName("http://www.opengis.net/gml", "status");
    private final static QName _ModifiedCoordinate_QNAME        = new QName("http://www.opengis.net/gml", "modifiedCoordinate");
    private final static QName _CoordinateOperationName_QNAME   = new QName("http://www.opengis.net/gml", "coordinateOperationName");
    private final static QName _CartesianCSRef_QNAME            = new QName("http://www.opengis.net/gml", "cartesianCSRef");
    private final static QName _Covariance_QNAME                = new QName("http://www.opengis.net/gml", "covariance");
    private final static QName _Origin_QNAME                    = new QName("http://www.opengis.net/gml", "origin");
    private final static QName _Minutes_QNAME                   = new QName("http://www.opengis.net/gml", "minutes");
    private final static QName _ImageDatumRef_QNAME             = new QName("http://www.opengis.net/gml", "imageDatumRef");
    private final static QName _EnvelopeWithTimePeriod_QNAME    = new QName("http://www.opengis.net/gml", "EnvelopeWithTimePeriod");
    private final static QName _MappingRule_QNAME               = new QName("http://www.opengis.net/gml", "MappingRule");
    private final static QName _CsName_QNAME                    = new QName("http://www.opengis.net/gml", "csName");
    private final static QName _Count_QNAME                     = new QName("http://www.opengis.net/gml", "Count");
    private final static QName _ReferenceSystem_QNAME           = new QName("http://www.opengis.net/gml", "AbstractReferenceSystem");
    private final static QName _AxisDirection_QNAME             = new QName("http://www.opengis.net/gml", "axisDirection");
    private final static QName _AxisID_QNAME                    = new QName("http://www.opengis.net/gml", "axisID");
    private final static QName _ParameterName_QNAME             = new QName("http://www.opengis.net/gml", "parameterName");
    private final static QName _DatumName_QNAME                 = new QName("http://www.opengis.net/gml", "datumName");
    private final static QName _PixelInCell_QNAME               = new QName("http://www.opengis.net/gml", "pixelInCell");
    private final static QName _ImageDatum_QNAME                = new QName("http://www.opengis.net/gml", "ImageDatum");
    private final static QName _MeridianID_QNAME                = new QName("http://www.opengis.net/gml", "meridianID");
    private final static QName _AbstractRing_QNAME              = new QName("http://www.opengis.net/gml", "AbstractRing");
    private final static QName _TimePosition_QNAME              = new QName("http://www.opengis.net/gml", "timePosition");
    private final static QName _OperationVersion_QNAME          = new QName("http://www.opengis.net/gml", "operationVersion");
    private final static QName _IntegerValue_QNAME              = new QName("http://www.opengis.net/gml", "integerValue");
    private final static QName _UsesImageDatum_QNAME            = new QName("http://www.opengis.net/gml", "usesImageDatum");
    private final static QName _AbstractSurface_QNAME           = new QName("http://www.opengis.net/gml", "AbstractSurface");
    private final static QName _ColumnIndex_QNAME               = new QName("http://www.opengis.net/gml", "columnIndex");
    private final static QName _MethodID_QNAME                  = new QName("http://www.opengis.net/gml", "methodID");
    private final static QName _TimeGeometricPrimitive_QNAME    = new QName("http://www.opengis.net/gml", "AbstractTimeGeometricPrimitive");
    private final static QName _Version_QNAME                   = new QName("http://www.opengis.net/gml", "version");
    private final static QName _Operation_QNAME                 = new QName("http://www.opengis.net/gml", "AbstractOperation");
    private final static QName _OuterBoundaryIs_QNAME           = new QName("http://www.opengis.net/gml", "outerBoundaryIs");
    private final static QName _CoordinateOperationID_QNAME     = new QName("http://www.opengis.net/gml", "coordinateOperationID");
    private final static QName _ValidTime_QNAME                 = new QName("http://www.opengis.net/gml", "validTime");
    private final static QName _CoordinateSystemAxisRef_QNAME   = new QName("http://www.opengis.net/gml", "coordinateSystemAxisRef");
    private final static QName _StringValue_QNAME               = new QName("http://www.opengis.net/gml", "stringValue");
    private final static QName _Datum_QNAME                     = new QName("http://www.opengis.net/gml", "AbstractDatum");
    private final static QName _TemporalExtent_QNAME            = new QName("http://www.opengis.net/gml", "temporalExtent");
    private final static QName _Description_QNAME               = new QName("http://www.opengis.net/gml", "description");
    private final static QName _Boolean_QNAME                   = new QName("http://www.opengis.net/gml", "Boolean");
    private final static QName _RealizationEpoch_QNAME          = new QName("http://www.opengis.net/gml", "realizationEpoch");
    private final static QName _CountList_QNAME                 = new QName("http://www.opengis.net/gml", "CountList");
    private final static QName _Remarks_QNAME                   = new QName("http://www.opengis.net/gml", "remarks");
    private final static QName _CoordinateOperation_QNAME       = new QName("http://www.opengis.net/gml", "AbstractCoordinateOperation");
    private final static QName _MeasureDescription_QNAME        = new QName("http://www.opengis.net/gml", "measureDescription");
    private final static QName _SingleOperation_QNAME           = new QName("http://www.opengis.net/gml", "AbstractSingleOperation");
    private final static QName _Name_QNAME                      = new QName("http://www.opengis.net/gml", "name");
    private final static QName _CRS_QNAME                       = new QName("http://www.opengis.net/gml", "AbstractCRS");
    private final static QName _ValueFile_QNAME                 = new QName("http://www.opengis.net/gml", "valueFile");
    private final static QName _GeometricPrimitive_QNAME        = new QName("http://www.opengis.net/gml", "AbstractGeometricPrimitive");
    private final static QName _RowIndex_QNAME                  = new QName("http://www.opengis.net/gml", "rowIndex");
    private final static QName _EllipsoidName_QNAME             = new QName("http://www.opengis.net/gml", "ellipsoidName");
    private final static QName _MaximumOccurs_QNAME             = new QName("http://www.opengis.net/gml", "maximumOccurs");
    private final static QName _Point_QNAME                     = new QName("http://www.opengis.net/gml", "Point");
    private final static QName _Object_QNAME                    = new QName("http://www.opengis.net/gml", "AbstractObject");
    private final static QName _BooleanList_QNAME               = new QName("http://www.opengis.net/gml", "BooleanList");
    private final static QName _ObliqueCartesianCS_QNAME        = new QName("http://www.opengis.net/gml", "ObliqueCartesianCS");
    private final static QName _Polygon_QNAME                   = new QName("http://www.opengis.net/gml", "Polygon");
    private final static QName _MeridianName_QNAME              = new QName("http://www.opengis.net/gml", "meridianName");
    private final static QName _CoordinateSystemAxis_QNAME      = new QName("http://www.opengis.net/gml", "CoordinateSystemAxis");
    private final static QName _GroupName_QNAME                 = new QName("http://www.opengis.net/gml", "groupName");
    private final static QName _CatalogSymbol_QNAME             = new QName("http://www.opengis.net/gml", "catalogSymbol");
    private final static QName _Envelope_QNAME                  = new QName("http://www.opengis.net/gml", "Envelope");
    private final static QName _CountExtent_QNAME               = new QName("http://www.opengis.net/gml", "CountExtent");
    private final static QName _CsID_QNAME                      = new QName("http://www.opengis.net/gml", "csID");
    private final static QName _TupleList_QNAME                 = new QName("http://www.opengis.net/gml", "tupleList");
    private final static QName _BoundingPolygon_QNAME           = new QName("http://www.opengis.net/gml", "boundingPolygon");
    private final static QName _CrsRef_QNAME                    = new QName("http://www.opengis.net/gml", "crsRef");
    private final static QName _IsSphere_QNAME                  = new QName("http://www.opengis.net/gml", "isSphere");
    private final static QName _BooleanValue_QNAME              = new QName("http://www.opengis.net/gml", "booleanValue");
    private final static QName _Interior_QNAME                  = new QName("http://www.opengis.net/gml", "interior");
    private final static QName _ObliqueCartesianCSRef_QNAME     = new QName("http://www.opengis.net/gml", "obliqueCartesianCSRef");
    private final static QName _TargetDimensions_QNAME          = new QName("http://www.opengis.net/gml", "targetDimensions");
    private final static QName _CoordinateSystem_QNAME          = new QName("http://www.opengis.net/gml", "AbstractCoordinateSystem");
    private final static QName _ParameterID_QNAME               = new QName("http://www.opengis.net/gml", "parameterID");
    private final static QName _Duration_QNAME                  = new QName("http://www.opengis.net/gml", "duration");
    private final static QName _TimeObject_QNAME                = new QName("http://www.opengis.net/gml", "AbstractTimeObject");
    private final static QName _Geometry_QNAME                  = new QName("http://www.opengis.net/gml", "AbstractGeometry");
    private final static QName _SrsName_QNAME                   = new QName("http://www.opengis.net/gml", "srsName");
    private final static QName _Scope_QNAME                     = new QName("http://www.opengis.net/gml", "scope");
    private final static QName _CoordinateReferenceSystem_QNAME = new QName("http://www.opengis.net/gml", "AbstractCoordinateReferenceSystem");
    private final static QName _Coordinates_QNAME               = new QName("http://www.opengis.net/gml", "coordinates");
    private final static QName _LocationKeyWord_QNAME           = new QName("http://www.opengis.net/gml", "LocationKeyWord");
    private final static QName _ImageCRS_QNAME                  = new QName("http://www.opengis.net/gml", "ImageCRS");
    private final static QName _SrsID_QNAME                     = new QName("http://www.opengis.net/gml", "srsID");
    private final static QName _ImplicitGeometry_QNAME          = new QName("http://www.opengis.net/gml", "AbstractImplicitGeometry");
    private final static QName _QuantityType_QNAME              = new QName("http://www.opengis.net/gml", "quantityType");
    private final static QName _LocationString_QNAME            = new QName("http://www.opengis.net/gml", "LocationString");
    private final static QName _UsesAxis_QNAME                  = new QName("http://www.opengis.net/gml", "usesAxis");
    private final static QName _AnchorPoint_QNAME               = new QName("http://www.opengis.net/gml", "anchorPoint");
    private final static QName _IntegerValueList_QNAME          = new QName("http://www.opengis.net/gml", "integerValueList");
    private final static QName _Definition_QNAME                = new QName("http://www.opengis.net/gml", "Definition");
    private final static QName _PositionalAccuracy_QNAME        = new QName("http://www.opengis.net/gml", "AbstractpositionalAccuracy");
    private final static QName _GroupID_QNAME                   = new QName("http://www.opengis.net/gml", "groupID");
    private final static QName _TimeInstant_QNAME               = new QName("http://www.opengis.net/gml", "TimeInstant");
    private final static QName _MetaDataProperty_QNAME          = new QName("http://www.opengis.net/gml", "metaDataProperty");
    private final static QName _RectifiedGrid_QNAME             = new QName("http://www.opengis.net/gml", "RectifiedGrid");
    private final static QName _Grid_QNAME                      = new QName("http://www.opengis.net/gml", "Grid");
    private final static QName _AbstractFeature_QNAME           = new QName("http://www.opengis.net/gml", "AbstractFeature");
    private final static QName _BaseUnit_QNAME                  = new QName("http://www.opengis.net/gml", "BaseUnit");
    private final static QName _BoundedBy_QNAME                 = new QName("http://www.opengis.net/gml", "boundedBy");
    private final static QName _Location_QNAME                  = new QName("http://www.opengis.net/gml", "location");
    private final static QName _UnitDefinition_QNAME            = new QName("http://www.opengis.net/gml", "UnitDefinition");
    private final static QName _UnitOfMeasure_QNAME             = new QName("http://www.opengis.net/gml", "unitOfMeasure");
    private final static QName _AbstractCurve_QNAME             = new QName("http://www.opengis.net/gml", "AbstractCurve");
    private final static QName _TimeComplex_QNAME               = new QName("http://www.opengis.net/gml", "AbstractTimeComplex"); 
    private final static QName _TemporalCRS_QNAME               = new QName("http://www.opengis.net/gml", "TemporalCRS");
    private final static QName _TemporalCSRef_QNAME             = new QName("http://www.opengis.net/gml", "temporalCSRef");
    private final static QName _UsesTemporalCS_QNAME            = new QName("http://www.opengis.net/gml", "usesTemporalCS");
    private final static QName _TemporalCS_QNAME                = new QName("http://www.opengis.net/gml", "TemporalCS");
    private final static QName _TemporalDatumRef_QNAME          = new QName("http://www.opengis.net/gml", "temporalDatumRef");
    private final static QName _UsesTemporalDatum_QNAME         = new QName("http://www.opengis.net/gml", "usesTemporalDatum");
    private final static QName _TemporalDatum_QNAME             = new QName("http://www.opengis.net/gml", "TemporalDatum");
    private final static QName _CoordinateSystemRef_QNAME       = new QName("http://www.opengis.net/gml", "coordinateSystemRef");
    private final static QName _UsesCS_QNAME                    = new QName("http://www.opengis.net/gml", "usesCS");
    private final static QName _EngineeringCRS_QNAME            = new QName("http://www.opengis.net/gml", "EngineeringCRS");
    private final static QName _EngineeringDatumRef_QNAME       = new QName("http://www.opengis.net/gml", "engineeringDatumRef");
    private final static QName _UsesEngineeringDatum_QNAME      = new QName("http://www.opengis.net/gml", "usesEngineeringDatum");
    private final static QName _EngineeringDatum_QNAME          = new QName("http://www.opengis.net/gml", "EngineeringDatum");
    private final static QName _AbstractCurveSegment_QNAME      = new QName("http://www.opengis.net/gml", "AbstractCurveSegment");
    private final static QName _PosList_QNAME                   = new QName("http://www.opengis.net/gml", "posList");
    private final static QName _LineStringProperty_QNAME        = new QName("http://www.opengis.net/gml", "lineStringProperty");
    private final static QName _LineStringMember_QNAME          = new QName("http://www.opengis.net/gml", "lineStringMember");
    private final static QName _LineStringSegment_QNAME         = new QName("http://www.opengis.net/gml", "LineStringSegment");
    private final static QName _LineString_QNAME                = new QName("http://www.opengis.net/gml", "LineString");
    private final static QName _PointRep_QNAME                  = new QName("http://www.opengis.net/gml", "pointRep");
    private final static QName _PointMember_QNAME               = new QName("http://www.opengis.net/gml", "pointMember");
    private final static QName _Position_QNAME                  = new QName("http://www.opengis.net/gml", "position");
    private final static QName _PointProperty_QNAME             = new QName("http://www.opengis.net/gml", "pointProperty");
    private final static QName _CenterOf_QNAME                  = new QName("http://www.opengis.net/gml", "centerOf");
    private final static QName _Coord_QNAME                     = new QName("http://www.opengis.net/gml", "coord");
    private final static QName _AbstractFeatureCollection_QNAME = new QName("http://www.opengis.net/gml", "AbstractFeatureCollection");
    private final static QName _FeatureCollection_QNAME         = new QName("http://www.opengis.net/gml", "FeatureCollection");
    private final static QName _FeatureMembers_QNAME            = new QName("http://www.opengis.net/gml", "featureMembers");
    private final static QName _History_QNAME                   = new QName("http://www.opengis.net/gml", "history");
    private final static QName _TimeSlice_QNAME                 = new QName("http://www.opengis.net/gml", "AbstractTimeSlice");
    private final static QName _MovingObjectStatus_QNAME        = new QName("http://www.opengis.net/gml", "MovingObjectStatus");
    private final static QName _Track_QNAME                     = new QName("http://www.opengis.net/gml", "track");
    private final static QName _Measure_QNAME                   = new QName("http://www.opengis.net/gml", "measure");
    private final static QName _Direction_QNAME                 = new QName("http://www.opengis.net/gml", "direction");
    private final static QName _Quantity_QNAME                  = new QName("http://www.opengis.net/gml", "Quantity");
    private final static QName _CompassPoint_QNAME              = new QName("http://www.opengis.net/gml", "CompassPoint");
    private final static QName _Angle_QNAME                     = new QName("http://www.opengis.net/gml", "angle");
    private final static QName _SemiMajorAxis_QNAME             = new QName("http://www.opengis.net/gml", "semiMajorAxis");
    private final static QName _Result_QNAME                    = new QName("http://www.opengis.net/gml", "result");
    private final static QName _Value_QNAME                     = new QName("http://www.opengis.net/gml", "value");
    private final static QName _SemiMinorAxis_QNAME             = new QName("http://www.opengis.net/gml", "semiMinorAxis");
    private final static QName _DirectionVector_QNAME           = new QName("http://www.opengis.net/gml", "DirectionVector");
    private final static QName _InverseFlattening_QNAME         = new QName("http://www.opengis.net/gml", "inverseFlattening");
    private final static QName _GeometricAggregate_QNAME        = new QName("http://www.opengis.net/gml", "_GeometricAggregate");
    private final static QName _Association_QNAME               = new QName("http://www.opengis.net/gml", "_association");
    private final static QName _ResultOf_QNAME                  = new QName("http://www.opengis.net/gml", "resultOf");
    private final static QName _Member_QNAME                    = new QName("http://www.opengis.net/gml", "member");
    private final static QName _StrictAssociation_QNAME         = new QName("http://www.opengis.net/gml", "_strictAssociation");
    private final static QName _GeometryMember_QNAME            = new QName("http://www.opengis.net/gml", "geometryMember");
    private final static QName _MultiPosition_QNAME             = new QName("http://www.opengis.net/gml", "multiPosition");
    private final static QName _MultiPoint_QNAME                = new QName("http://www.opengis.net/gml", "MultiPoint");
    private final static QName _MultiPointProperty_QNAME        = new QName("http://www.opengis.net/gml", "multiPointProperty");
    private final static QName _MultiCenterOf_QNAME             = new QName("http://www.opengis.net/gml", "multiCenterOf");
    private final static QName _MultiLocation_QNAME             = new QName("http://www.opengis.net/gml", "multiLocation");
    private final static QName _PointMembers_QNAME              = new QName("http://www.opengis.net/gml", "pointMembers");
    private final static QName _PointArrayProperty_QNAME        = new QName("http://www.opengis.net/gml", "pointArrayProperty");
    private final static QName _FeatureMember_QNAME             = new QName("http://www.opengis.net/gml", "featureMember");
    private final static QName _AbstractSolid_QNAME             = new QName("http://www.opengis.net/gml", "AbstractSolid");
    private final static QName _AbstractSurfacePatch_QNAME      = new QName("http://www.opengis.net/gml", "AbstractSurfacePatch");
    private final static QName _CompositeSolid_QNAME            = new QName("http://www.opengis.net/gml", "CompositeSolid");
    private final static QName _Cone_QNAME                      = new QName("http://www.opengis.net/gml", "Cone");
    private final static QName _CurveArrayProperty_QNAME        = new QName("http://www.opengis.net/gml", "curveArrayProperty");
    private final static QName _CurveMember_QNAME               = new QName("http://www.opengis.net/gml", "curveMember");
    private final static QName _Solid_QNAME                     = new QName("http://www.opengis.net/gml", "Solid");
    private final static QName _MultiCurveProperty_QNAME        = new QName("http://www.opengis.net/gml", "multiCurveProperty");
    private final static QName _PolygonPatches_QNAME            = new QName("http://www.opengis.net/gml", "polygonPatches");
    private final static QName _MultiEdgeOf_QNAME               = new QName("http://www.opengis.net/gml", "multiEdgeOf");
    private final static QName _CurveMembers_QNAME              = new QName("http://www.opengis.net/gml", "curveMembers");
    private final static QName _CenterLineOf_QNAME              = new QName("http://www.opengis.net/gml", "centerLineOf");
    private final static QName _MultiCenterLineOf_QNAME         = new QName("http://www.opengis.net/gml", "multiCenterLineOf");
    private final static QName _BaseCurve_QNAME                 = new QName("http://www.opengis.net/gml", "baseCurve");
    private final static QName _CurveProperty_QNAME             = new QName("http://www.opengis.net/gml", "curveProperty");
    private final static QName _PolygonPatch_QNAME              = new QName("http://www.opengis.net/gml", "PolygonPatch");
    private final static QName _GriddedSurface_QNAME            = new QName("http://www.opengis.net/gml", "_GriddedSurface");
    private final static QName _Rectangle_QNAME                 = new QName("http://www.opengis.net/gml", "Rectangle");
    private final static QName _EdgeOf_QNAME                    = new QName("http://www.opengis.net/gml", "edgeOf");
    private final static QName _Triangle_QNAME                  = new QName("http://www.opengis.net/gml", "Triangle");
    private final static QName _ParametricCurveSurface_QNAME    = new QName("http://www.opengis.net/gml", "_ParametricCurveSurface");
    private final static QName _Cylinder_QNAME                  = new QName("http://www.opengis.net/gml", "Cylinder");
    private final static QName _MultiCurve_QNAME                = new QName("http://www.opengis.net/gml", "MultiCurve");
    private final static QName _MultiSurface_QNAME              = new QName("http://www.opengis.net/gml", "MultiSurface");
    private final static QName _MultiSurfaceProperty_QNAME      = new QName("http://www.opengis.net/gml", "multiSurfaceProperty");
    private final static QName _MultiExtentOf_QNAME             = new QName("http://www.opengis.net/gml", "multiExtentOf");
    private final static QName _QuantityList_QNAME              = new QName("http://www.opengis.net/gml", "QuantityList");
    private final static QName _Segments_QNAME                  = new QName("http://www.opengis.net/gml", "segments");
    private final static QName _Curve_QNAME                     = new QName("http://www.opengis.net/gml", "Curve");
    private final static QName _MultiCoverage_QNAME             = new QName("http://www.opengis.net/gml", "multiCoverage");
    private final static QName _PolyhedralSurface_QNAME         = new QName("http://www.opengis.net/gml", "PolyhedralSurface");
    private final static QName _QuantityExtent_QNAME            = new QName("http://www.opengis.net/gml", "QuantityExtent");
    private final static QName _SolidProperty_QNAME             = new QName("http://www.opengis.net/gml", "solidProperty");
    private final static QName _Sphere_QNAME                    = new QName("http://www.opengis.net/gml", "Sphere");
    private final static QName _SolidMember_QNAME               = new QName("http://www.opengis.net/gml", "solidMember");
    private final static QName _SurfaceArrayProperty_QNAME      = new QName("http://www.opengis.net/gml", "surfaceArrayProperty");
    private final static QName _SurfaceMembers_QNAME            = new QName("http://www.opengis.net/gml", "surfaceMembers");
    private final static QName _Patches_QNAME                   = new QName("http://www.opengis.net/gml", "patches");
    private final static QName _BaseSurface_QNAME               = new QName("http://www.opengis.net/gml", "baseSurface");
    private final static QName _SurfaceProperty_QNAME           = new QName("http://www.opengis.net/gml", "surfaceProperty");
    private final static QName _ExtentOf_QNAME                  = new QName("http://www.opengis.net/gml", "extentOf");
    private final static QName _SurfaceMember_QNAME             = new QName("http://www.opengis.net/gml", "surfaceMember");
    private final static QName _TrianglePatches_QNAME           = new QName("http://www.opengis.net/gml", "trianglePatches");
    private final static QName _TriangulatedSurface_QNAME       = new QName("http://www.opengis.net/gml", "TriangulatedSurface");
    private final static QName _Tin_QNAME                       = new QName("http://www.opengis.net/gml", "Tin");
    private final static QName _Surface_QNAME                   = new QName("http://www.opengis.net/gml", "Surface");
    private final static QName _CompositeCurve_QNAME            = new QName("http://www.opengis.net/gml", "CompositeCurve");
    private final static QName _CompositeSurface_QNAME          = new QName("http://www.opengis.net/gml", "CompositeSurface");
    private final static QName _GeometricComplex_QNAME          = new QName("http://www.opengis.net/gml", "GeometricComplex");
    private final static QName _Ring_QNAME                      = new QName("http://www.opengis.net/gml", "Ring");
    private final static QName _LinearRing_QNAME                = new QName("http://www.opengis.net/gml", "LinearRing");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.gml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LinearRingPropertyType }
     *
     */
    public LinearRingPropertyType createLinearRingPropertyType() {
        return new LinearRingPropertyType();
    }

    /**
     * Create an instance of {@link LinearRingType }
     *
     */
    public LinearRingType createLinearRingType() {
        return new LinearRingType();
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
     * Create an instance of {@link GeometricComplexPropertyType }
     *
     */
    public GeometricComplexPropertyType createGeometricComplexPropertyType() {
        return new GeometricComplexPropertyType();
    }

    /**
     * Create an instance of {@link GeometricPrimitivePropertyType }
     *
     */
    public GeometricPrimitivePropertyType createGeometricPrimitivePropertyType() {
        return new GeometricPrimitivePropertyType();
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
     * Create an instance of {@link GeometricComplexType }
     *
     */
    public GeometricComplexType createGeometricComplexType() {
        return new GeometricComplexType();
    }
    
    /**
     * Create an instance of {@link TriangleType }
     *
     */
    public TriangleType createTriangleType() {
        return new TriangleType();
    }
    
    /**
     * Create an instance of {@link SurfaceType }
     *
     */
    public SurfaceType createSurfaceType() {
        return new SurfaceType();
    }
    
    /**
     * Create an instance of {@link SphereType }
     *
     */
    public SphereType createSphereType() {
        return new SphereType();
    }

    /**
     * Create an instance of {@link SurfaceArrayPropertyType }
     *
     */
    public SurfaceArrayPropertyType createSurfaceArrayPropertyType() {
        return new SurfaceArrayPropertyType();
    }
    
     /**
     * Create an instance of {@link RectangleType }
     *
     */
    public RectangleType createRectangleType() {
        return new RectangleType();
    }

    /**
     * Create an instance of {@link SolidPropertyType }
     *
     */
    public SolidPropertyType createSolidPropertyType() {
        return new SolidPropertyType();
    }
    
    /**
     * Create an instance of {@link QuantityExtentType }
     *
     */
    public QuantityExtentType createQuantityExtentType() {
        return new QuantityExtentType();
    }
    
    /**
     * Create an instance of {@link PolygonPatchArrayPropertyType }
     *
     */
    public PolygonPatchArrayPropertyType createPolygonPatchArrayPropertyType() {
        return new PolygonPatchArrayPropertyType();
    }

    /**
     * Create an instance of {@link PolyhedralSurfaceType }
     *
     */
    public PolyhedralSurfaceType createPolyhedralSurfaceType() {
        return new PolyhedralSurfaceType();
    }
    
    /**
     * Create an instance of {@link PolygonPatchType }
     *
     */
    public PolygonPatchType createPolygonPatchType() {
        return new PolygonPatchType();
    }

    /**
     * Create an instance of {@link MultiSurfacePropertyType }
     *
     */
    public MultiSurfacePropertyType createMultiSurfacePropertyType() {
        return new MultiSurfacePropertyType();
    }
    
    /**
     * Create an instance of {@link MeasureOrNullListType }
     *
     */
    public MeasureOrNullListType createMeasureOrNullListType() {
        return new MeasureOrNullListType();
    }
    
    /**
     * Create an instance of {@link MultiCurveType }
     *
     */
    public MultiCurveType createMultiCurveType() {
        return new MultiCurveType();
    }

    /**
     * Create an instance of {@link LineStringSegmentArrayPropertyType }
     *
     */
    public LineStringSegmentArrayPropertyType createLineStringSegmentArrayPropertyType() {
        return new LineStringSegmentArrayPropertyType();
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
     * Create an instance of {@link SurfacePatchArrayPropertyType }
     *
     */
    public SurfacePatchArrayPropertyType createSurfacePatchArrayPropertyType() {
        return new SurfacePatchArrayPropertyType();
    }


    /**
     * Create an instance of {@link SolidType }
     *
     */
    public SolidType createSolidType() {
        return new SolidType();
    }

    /**
     * Create an instance of {@link CurvePropertyType }
     *
     */
    public CurvePropertyType createCurvePropertyType() {
        return new CurvePropertyType();
    }

    /**
     * Create an instance of {@link CurveType }
     *
     */
    public CurveType createCurveType() {
        return new CurveType();
    }

    /**
     * Create an instance of {@link MultiCurvePropertyType }
     *
     */
    public MultiCurvePropertyType createMultiCurvePropertyType() {
        return new MultiCurvePropertyType();
    }
    
    /**
     * Create an instance of {@link MultiSurfaceType }
     *
     */
    public MultiSurfaceType createMultiSurfaceType() {
        return new MultiSurfaceType();
    }

    /**
     * Create an instance of {@link CurveArrayPropertyType }
     *
     */
    public CurveArrayPropertyType createCurveArrayPropertyType() {
        return new CurveArrayPropertyType();
    }

    /**
     * Create an instance of {@link TrianglePatchArrayPropertyType }
     *
     */
    public TrianglePatchArrayPropertyType createTrianglePatchArrayPropertyType() {
        return new TrianglePatchArrayPropertyType();
    }
    
    /**
     * Create an instance of {@link ConeType }
     *
     */
    public ConeType createConeType() {
        return new ConeType();
    }
    
    /**
     * Create an instance of {@link CompositeSolidType }
     *
     */
    public CompositeSolidType createCompositeSolidType() {
        return new CompositeSolidType();
    }
    
    /**
     * Create an instance of {@link PointArrayPropertyType }
     *
     */
    public PointArrayPropertyType createPointArrayPropertyType() {
        return new PointArrayPropertyType();
    }
    
    /**
     * Create an instance of {@link MultiPointType }
     *
     */
    public MultiPointType createMultiPointType() {
        return new MultiPointType();
    }
    
    /**
     * Create an instance of {@link MultiPointPropertyType }
     *
     */
    public MultiPointPropertyType createMultiPointPropertyType() {
        return new MultiPointPropertyType();
    }
    
    /**
     * Create an instance of {@link GeometryPropertyType }
     *
     */
    public GeometryPropertyType createGeometryPropertyType() {
        return new GeometryPropertyType();
    }
    
    /**
     * Create an instance of {@link AssociationType }
     *
     */
    public AssociationType createAssociationType() {
        return new AssociationType();
    }
    
    /**
     * Create an instance of {@link DynamicFeatureType }
     *
     */
    public DynamicFeatureType createDynamicFeatureType() {
        return new DynamicFeatureType();
    }

    /**
     * Create an instance of {@link AreaType }
     *
     */
    public AreaType createAreaType() {
        return new AreaType();
    }

    /**
     * Create an instance of {@link GridLengthType }
     *
     */
    public GridLengthType createGridLengthType() {
        return new GridLengthType();
    }

    /**
     * Create an instance of {@link DirectionPropertyType }
     *
     */
    public DirectionPropertyType createDirectionPropertyType() {
        return new DirectionPropertyType();
    }

    /**
     * Create an instance of {@link VolumeType }
     *
     */
    public VolumeType createVolumeType() {
        return new VolumeType();
    }

    /**
     * Create an instance of {@link ScaleType }
     *
     */
    public ScaleType createScaleType() {
        return new ScaleType();
    }

    /**
     * Create an instance of {@link MeasureType }
     *
     */
    public MeasureType createMeasureType() {
        return new MeasureType();
    }

    /**
     * Create an instance of {@link DirectionVectorType }
     *
     */
    public DirectionVectorType createDirectionVectorType() {
        return new DirectionVectorType();
    }

    /**
     * Create an instance of {@link SpeedType }
     *
     */
    public SpeedType createSpeedType() {
        return new SpeedType();
    }

    /**
     * Create an instance of {@link AngleType }
     *
     */
    public AngleType createAngleType() {
        return new AngleType();
    }

    /**
     * Create an instance of {@link TimeType }
     *
     */
    public TimeType createTimeType() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link LengthType }
     *
     */
    public LengthType createLengthType() {
        return new LengthType();
    }

    /**
     * Create an instance of {@link HistoryPropertyType }
     *
     */
    public HistoryPropertyType createHistoryPropertyType() {
        return new HistoryPropertyType();
    }

    /**
     * Create an instance of {@link TrackType }
     *
     */
    public TrackType createTrackType() {
        return new TrackType();
    }

    /**
     * Create an instance of {@link MovingObjectStatusType }
     *
     */
    public MovingObjectStatusType createMovingObjectStatusType() {
        return new MovingObjectStatusType();
    }

    /**
     * Create an instance of {@link DynamicFeatureCollectionType }
     *
     */
    public DynamicFeatureCollectionType createDynamicFeatureCollectionType() {
        return new DynamicFeatureCollectionType();
    }
    
    /**
     * Create an instance of {@link FeatureCollectionType }
     *
     */
    public FeatureCollectionType createFeatureCollectionType() {
        return new FeatureCollectionType();
    }

    /**
     * Create an instance of {@link FeatureArrayPropertyType }
     *
     */
    public FeatureArrayPropertyType createFeatureArrayPropertyType() {
        return new FeatureArrayPropertyType();
    }
    
    /**
     * Create an instance of {@link MetaDataPropertyType }
     * 
     */
    public MetaDataPropertyType createMetaDataPropertyType() {
        return new MetaDataPropertyType();
    }
    
    /**
     * Create an instance of {@link CRSRefType }
     * 
     */
    public CRSRefType createCRSRefType() {
        return new CRSRefType();
    }

    /**
     * Create an instance of {@link ExtentType }
     * 
     */
    public ExtentType createExtentType() {
        return new ExtentType();
    }

    /**
     * Create an instance of {@link ImageCRSType }
     * 
     */
    public ImageCRSType createImageCRSType() {
        return new ImageCRSType();
    }

   
    /**
     * Create an instance of {@link TimeIntervalLengthType }
     * 
     */
    public TimeIntervalLengthType createTimeIntervalLengthType() {
        return new TimeIntervalLengthType();
    }

    
    /**
     * Create an instance of {@link TimePositionType }
     * 
     */
    public TimePositionType createTimePositionType() {
        return new TimePositionType();
    }

    /**
     * Create an instance of {@link CartesianCSRefType }
     * 
     */
    public CartesianCSRefType createCartesianCSRefType() {
        return new CartesianCSRefType();
    }

    /**
     * Create an instance of {@link PolygonType }
     * 
     */
    public PolygonType createPolygonType() {
        return new PolygonType();
    }


    /**
     * Create an instance of {@link TimePeriodType }
     * 
     */
    public TimePeriodType createTimePeriodType() {
        return new TimePeriodType();
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
     * Create an instance of {@link IdentifierType }
     * 
     */
    public IdentifierType createIdentifierType() {
        return new IdentifierType();
    }

    /**
     * Create an instance of {@link CoordinatesType }
     * 
     */
    public CoordinatesType createCoordinatesType() {
        return new CoordinatesType();
    }

    /**
     * Create an instance of {@link RelatedTimeType }
     * 
     */
    public RelatedTimeType createRelatedTimeType() {
        return new RelatedTimeType();
    }


    /**
     * Create an instance of {@link DirectPositionType }
     * 
     */
    public DirectPositionType createDirectPositionType() {
        return new DirectPositionType();
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

    /*
     * Create an instance of {@link AbstractRingPropertyType }
     * 
     */
    public AbstractRingPropertyType createAbstractRingPropertyType() {
        return new AbstractRingPropertyType();
    }
    
    /**
     * Create an instance of {@link PointType }
     * 
     */
    public PointType createPointType() {
        return new PointType();
    }


    /**
     * Create an instance of {@link TimeInstantPropertyType }
     * 
     */
    public TimeInstantPropertyType createTimeInstantPropertyType() {
        return new TimeInstantPropertyType();
    }

    /**
     * Create an instance of {@link CoordinateSystemAxisType }
     * 
     */
    public CoordinateSystemAxisType createCoordinateSystemAxisType() {
        return new CoordinateSystemAxisType();
    }

    /**
     * Create an instance of {@link DefinitionType }
     * 
     */
    public DefinitionType createDefinitionType() {
        return new DefinitionType();
    }


    /**
     * Create an instance of {@link TimeInstantType }
     * 
     */
    public TimeInstantType createTimeInstantType() {
        return new TimeInstantType();
    }
    
    /**
     * Create an instance of {@link AbstractSurfaceType }
     * 
     */
    public AbstractSurfaceType createAbstractSurfaceType() {
        return new AbstractSurfaceType();
    }

   
    /**
     * Create an instance of {@link StringOrRefType }
     * 
     */
    public StringOrRefType createStringOrRefType() {
        return new StringOrRefType();
    }
    
    /**
     * Create an instance of {@link CartesianCSType }
     * 
     */
    public CartesianCSType createCartesianCSType() {
        return new CartesianCSType();
    }

    /**
     * Create an instance of {@link ObliqueCartesianCSRefType }
     * 
     */
    public ObliqueCartesianCSRefType createObliqueCartesianCSRefType() {
        return new ObliqueCartesianCSRefType();
    }

    
    /**
     * Create an instance of {@link PixelInCellType }
     * 
     */
    public PixelInCellType createPixelInCellType() {
        return new PixelInCellType();
    }

    
    /**
     * Create an instance of {@link EnvelopeWithTimePeriodType }
     * 
     */
    public EnvelopeWithTimePeriodType createEnvelopeWithTimePeriodType() {
        return new EnvelopeWithTimePeriodType();
    }


    /**
     * Create an instance of {@link CoordinateSystemAxisRefType }
     * 
     */
    public CoordinateSystemAxisRefType createCoordinateSystemAxisRefType() {
        return new CoordinateSystemAxisRefType();
    }

    /**
     * Create an instance of {@link ObliqueCartesianCSType }
     * 
     */
    public ObliqueCartesianCSType createObliqueCartesianCSType() {
        return new ObliqueCartesianCSType();
    }
    
    /**
     * Create an instance of {@link CodeListType }
     * 
     */
    public CodeListType createCodeListType() {
        return new CodeListType();
    }
    
    /**
     * Create an instance of {@link GridType }
     * 
     */
    public GridType createGridType() {
        return new GridType();
    }
    
    /**
     * Create an instance of {@link RectifiedGridType }
     * 
     */
    public RectifiedGridType createRectifiedGridType() {
        return new RectifiedGridType();
    }
    
     /**
     * Create an instance of {@link GridLimitsType }
     * 
     */
    public GridLimitsType createGridLimitsType() {
        return new GridLimitsType();
    }
    
    /**
     * Create an instance of {@link GridEnvelopeType }
     * 
     */
    public GridEnvelopeType createGridEnvelopeType() {
        return new GridEnvelopeType();
    }

    /**
     * Create an instance of {@link BaseUnitType }
     * 
     */
    public BaseUnitType createBaseUnitType() {
        return new BaseUnitType();
    }
    
    /**
     * Create an instance of {@link BoundingShapeType }
     * 
     */
    public BoundingShapeEntry createBoundingShapeType() {
        return new BoundingShapeEntry();
    }
    
    /**
     * Create an instance of {@link LocationPropertyType }
     * 
     */
    public LocationPropertyType createLocationPropertyType() {
        return new LocationPropertyType();
    }
    
    /**
     * Create an instance of {@link TimePeriodPropertyType }
     * 
     */
    public TimePeriodPropertyType createTimePeriodPropertyType() {
        return new TimePeriodPropertyType();
    }

    /**
     * Create an instance of {@link UnitDefinitionType }
     * 
     */
    public UnitDefinitionType createUnitDefinitionType() {
        return new UnitDefinitionType();
    }
    
    /**
     * Create an instance of {@link UnitOfMeasureType }
     * 
     */
    public UnitOfMeasureType createUnitOfMeasureType() {
        return new UnitOfMeasureType();
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
     * Create an instance of {@link CoordinateSystemRefType }
     * 
     */
    public CoordinateSystemRefType createCoordinateSystemRefType() {
        return new CoordinateSystemRefType();
    }
    
    /**
     * Create an instance of {@link EngineeringCRSType }
     * 
     */
    public EngineeringCRSType createEngineeringCRSType() {
        return new EngineeringCRSType();
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
     * Create an instance of {@link DirectPositionListType }
     * 
     */
    public DirectPositionListType createDirectPositionListType() {
        return new DirectPositionListType();
    }
    
    /**
     * Create an instance of {@link LineStringPropertyType }
     * 
     */
    public LineStringPropertyType createLineStringPropertyType() {
        return new LineStringPropertyType();
    }
    
    /**
     * Create an instance of {@link LineStringSegmentType }
     * 
     */
    public LineStringSegmentType createLineStringSegmentType() {
        return new LineStringSegmentType();
    }
    
    /**
     * Create an instance of {@link LineStringType }
     * 
     */
    public LineStringType createLineStringType() {
        return new LineStringType();
    }
    
    /**
     * Create an instance of {@link PointPropertyType }
     * 
     */
    public PointPropertyType createPointPropertyType() {
        return new PointPropertyType();
    }
    
    /**
     * Create an instance of {@link CoordType }
     * 
     */
    public CoordType createCoordType() {
        return new CoordType();
    }

    /**
     * Create an instance of {@link TinType.ControlPoint }
     *
     */
    public TinType.ControlPoint createTinTypeControlPoint() {
        return new TinType.ControlPoint();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TrianglePatchArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "trianglePatches", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "patches")
    public JAXBElement<TrianglePatchArrayPropertyType> createTrianglePatches(TrianglePatchArrayPropertyType value) {
        return new JAXBElement<TrianglePatchArrayPropertyType>(_TrianglePatches_QNAME, TrianglePatchArrayPropertyType.class, null, value);
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
     * Create an instance of {@link TriangulatedSurfaceType }
     *
     */
    public TriangulatedSurfaceType createTriangulatedSurfaceType() {
        return new TriangulatedSurfaceType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "baseSurface")
    public JAXBElement<SurfacePropertyType> createBaseSurface(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_BaseSurface_QNAME, SurfacePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "extentOf")
    public JAXBElement<SurfacePropertyType> createExtentOf(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_ExtentOf_QNAME, SurfacePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePatchArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "patches")
    public JAXBElement<SurfacePatchArrayPropertyType> createPatches(SurfacePatchArrayPropertyType value) {
        return new JAXBElement<SurfacePatchArrayPropertyType>(_Patches_QNAME, SurfacePatchArrayPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfaceArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "surfaceMembers")
    public JAXBElement<SurfaceArrayPropertyType> createSurfaceMembers(SurfaceArrayPropertyType value) {
        return new JAXBElement<SurfaceArrayPropertyType>(_SurfaceMembers_QNAME, SurfaceArrayPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link SphereType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Sphere", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GriddedSurface")
    public JAXBElement<SphereType> createSphere(SphereType value) {
        return new JAXBElement<SphereType>(_Sphere_QNAME, SphereType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link QuantityExtentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "QuantityExtent")
    public JAXBElement<QuantityExtentType> createQuantityExtent(QuantityExtentType value) {
        return new JAXBElement<QuantityExtentType>(_QuantityExtent_QNAME, QuantityExtentType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCoverage")
    public JAXBElement<MultiSurfacePropertyType> createMultiCoverage(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiCoverage_QNAME, MultiSurfacePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiExtentOf")
    public JAXBElement<MultiSurfacePropertyType> createMultiExtentOf(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiExtentOf_QNAME, MultiSurfacePropertyType.class, null, value);
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
     * Create an instance of {@link AbstractGriddedSurfaceType }
     *
     */
    public AbstractGriddedSurfaceType createAbstractGriddedSurfaceType() {
        return new AbstractGriddedSurfaceType();
    }

    /**
     * Create an instance of {@link SurfacePropertyType }
     *
     */
    public SurfacePropertyType createSurfacePropertyType() {
        return new SurfacePropertyType();
    }

    /**
     * Create an instance of {@link TinType }
     *
     */
    public TinType createTinType() {
        return new TinType();
    }

    /**
     * Create an instance of {@link AbstractSolidType }
     *
     */
    public AbstractSolidType createAbstractSolidType() {
        return new AbstractSolidType();
    }

    /**
     * Create an instance of {@link AbstractParametricCurveSurfaceType }
     *
     */
    public AbstractParametricCurveSurfaceType createAbstractParametricCurveSurfaceType() {
        return new AbstractParametricCurveSurfaceType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LinearRing", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractRing")
    public JAXBElement<LinearRingType> createLinearRing(LinearRingType value) {
        return new JAXBElement<LinearRingType>(_LinearRing_QNAME, LinearRingType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureOrNullListType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "QuantityList")
    public JAXBElement<MeasureOrNullListType> createQuantityList(MeasureOrNullListType value) {
        return new JAXBElement<MeasureOrNullListType>(_QuantityList_QNAME, MeasureOrNullListType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiCurveType> createMultiCurve(MultiCurveType value) {
        return new JAXBElement<MultiCurveType>(_MultiCurve_QNAME, MultiCurveType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiCurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCurveProperty")
    public JAXBElement<MultiCurvePropertyType> createMultiCurveProperty(MultiCurvePropertyType value) {
        return new JAXBElement<MultiCurvePropertyType>(_MultiCurveProperty_QNAME, MultiCurvePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveMember")
    public JAXBElement<CurvePropertyType> createCurveMember(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_CurveMember_QNAME, CurvePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "edgeOf")
    public JAXBElement<CurvePropertyType> createEdgeOf(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_EdgeOf_QNAME, CurvePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CurvePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "baseCurve")
    public JAXBElement<CurvePropertyType> createBaseCurve(CurvePropertyType value) {
        return new JAXBElement<CurvePropertyType>(_BaseCurve_QNAME, CurvePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPatchArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "polygonPatches", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "patches")
    public JAXBElement<PolygonPatchArrayPropertyType> createPolygonPatches(PolygonPatchArrayPropertyType value) {
        return new JAXBElement<PolygonPatchArrayPropertyType>(_PolygonPatches_QNAME, PolygonPatchArrayPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "curveArrayProperty")
    public JAXBElement<CurveArrayPropertyType> createCurveArrayProperty(CurveArrayPropertyType value) {
        return new JAXBElement<CurveArrayPropertyType>(_CurveArrayProperty_QNAME, CurveArrayPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TriangleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Triangle", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<TriangleType> createTriangle(TriangleType value) {
        return new JAXBElement<TriangleType>(_Triangle_QNAME, TriangleType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSurfacePatchType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSurfacePatch")
    public JAXBElement<AbstractSurfacePatchType> createAbstractSurfacePatch(AbstractSurfacePatchType value) {
        return new JAXBElement<AbstractSurfacePatchType>(_AbstractSurfacePatch_QNAME, AbstractSurfacePatchType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonPatchType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "PolygonPatch", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurfacePatch")
    public JAXBElement<PolygonPatchType> createPolygonPatch(PolygonPatchType value) {
        return new JAXBElement<PolygonPatchType>(_PolygonPatch_QNAME, PolygonPatchType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeSolidType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeSolid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSolid")
    public JAXBElement<CompositeSolidType> createCompositeSolid(CompositeSolidType value) {
        return new JAXBElement<CompositeSolidType>(_CompositeSolid_QNAME, CompositeSolidType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGriddedSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_GriddedSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_ParametricCurveSurface")
    public JAXBElement<AbstractGriddedSurfaceType> createGriddedSurface(AbstractGriddedSurfaceType value) {
        return new JAXBElement<AbstractGriddedSurfaceType>(_GriddedSurface_QNAME, AbstractGriddedSurfaceType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coord")
    public JAXBElement<CoordType> createCoord(CoordType value) {
        return new JAXBElement<CoordType>(_Coord_QNAME, CoordType.class, null, value);
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
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointMember")
    public JAXBElement<PointPropertyType> createPointMember(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_PointMember_QNAME, PointPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointProperty")
    public JAXBElement<PointPropertyType> createPointProperty(PointPropertyType value) {
        return new JAXBElement<PointPropertyType>(_PointProperty_QNAME, PointPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LineString", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCurve")
    public JAXBElement<LineStringType> createLineString(LineStringType value) {
        return new JAXBElement<LineStringType>(_LineString_QNAME, LineStringType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "lineStringProperty")
    public JAXBElement<LineStringPropertyType> createLineStringProperty(LineStringPropertyType value) {
        return new JAXBElement<LineStringPropertyType>(_LineStringProperty_QNAME, LineStringPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectPositionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "posList")
    public JAXBElement<DirectPositionListType> createPosList(DirectPositionListType value) {
        return new JAXBElement<DirectPositionListType>(_PosList_QNAME, DirectPositionListType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCurveSegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCurveSegment")
    public JAXBElement<AbstractCurveSegmentType> createAbstractCurveSegment(AbstractCurveSegmentType value) {
        return new JAXBElement<AbstractCurveSegmentType>(_AbstractCurveSegment_QNAME, AbstractCurveSegmentType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link EngineeringCRSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "EngineeringCRS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateReferenceSystem")
    public JAXBElement<EngineeringCRSType> createEngineeringCRS(EngineeringCRSType value) {
        return new JAXBElement<EngineeringCRSType>(_EngineeringCRS_QNAME, EngineeringCRSType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCurve", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<AbstractCurveType> createAbstractCurve(AbstractCurveType value) {
        return new JAXBElement<AbstractCurveType>(_AbstractCurve_QNAME, AbstractCurveType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositeSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<CompositeSurfaceType> createCompositeSurface(CompositeSurfaceType value) {
        return new JAXBElement<CompositeSurfaceType>(_CompositeSurface_QNAME, CompositeSurfaceType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeComplexType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeComplex", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeObject")
    public JAXBElement<AbstractTimeComplexType> createTimeComplex(AbstractTimeComplexType value) {
        return new JAXBElement<AbstractTimeComplexType>(_TimeComplex_QNAME, AbstractTimeComplexType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link UnitDefinitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "UnitDefinition", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<UnitDefinitionType> createUnitDefinition(UnitDefinitionType value) {
        return new JAXBElement<UnitDefinitionType>(_UnitDefinition_QNAME, UnitDefinitionType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingShapeEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "boundedBy")
    public JAXBElement<BoundingShapeEntry> createBoundedBy(BoundingShapeEntry value) {
        return new JAXBElement<BoundingShapeEntry>(_BoundedBy_QNAME, BoundingShapeEntry.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractFeatureEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractFeature", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractFeatureEntry> createAbstractFeature(AbstractFeatureEntry value) {
        return new JAXBElement<AbstractFeatureEntry>(_AbstractFeature_QNAME, AbstractFeatureEntry.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GridType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Grid", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<GridType> createGrid(GridType value) {
        return new JAXBElement<GridType>(_Grid_QNAME, GridType.class, null, value);
    }


   
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Envelope", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<EnvelopeEntry> createEnvelope(EnvelopeEntry value) {
        return new JAXBElement<EnvelopeEntry>(_Envelope_QNAME, EnvelopeEntry.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGMLType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGML", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<AbstractGMLEntry> createAbstractGML(AbstractGMLEntry value) {
        return new JAXBElement<AbstractGMLEntry>(_AbstractGML_QNAME, AbstractGMLEntry.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "sourceDimensions")
    public JAXBElement<BigInteger> createSourceDimensions(BigInteger value) {
        return new JAXBElement<BigInteger>(_SourceDimensions_QNAME, BigInteger.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CartesianCSRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesCartesianCS")
    public JAXBElement<CartesianCSRefType> createUsesCartesianCS(CartesianCSRefType value) {
        return new JAXBElement<CartesianCSRefType>(_UsesCartesianCS_QNAME, CartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "doubleOrNullTupleList")
    public JAXBElement<List<String>> createDoubleOrNullTupleList(List<String> value) {
        return new JAXBElement<List<String>>(_DoubleOrNullTupleList_QNAME, ((Class) List.class), null, ((List<String> ) value));
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
    public JAXBElement<AbstractTimePrimitiveType> createTimePrimitive(AbstractTimePrimitiveType value) {
        return new JAXBElement<AbstractTimePrimitiveType>(_TimePrimitive_QNAME, AbstractTimePrimitiveType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ellipsoidID")
    public JAXBElement<IdentifierType> createEllipsoidID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_EllipsoidID_QNAME, IdentifierType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Category")
    public JAXBElement<CodeType> createCategory(CodeType value) {
        return new JAXBElement<CodeType>(_Category_QNAME, CodeType.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "dataSource")
    public JAXBElement<StringOrRefType> createDataSource(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_DataSource_QNAME, StringOrRefType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Null")
    public JAXBElement<List<String>> createNull(List<String> value) {
        return new JAXBElement<List<String>>(_Null_QNAME, ((Class) List.class), null, ((List<String> ) value));
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CartesianCSType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CartesianCS", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCoordinateSystem")
    public JAXBElement<CartesianCSType> createCartesianCS(CartesianCSType value) {
        return new JAXBElement<CartesianCSType>(_CartesianCS_QNAME, CartesianCSType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "methodName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createMethodName(CodeType value) {
        return new JAXBElement<CodeType>(_MethodName_QNAME, CodeType.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "minimumOccurs")
    public JAXBElement<BigInteger> createMinimumOccurs(BigInteger value) {
        return new JAXBElement<BigInteger>(_MinimumOccurs_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "axisAbbrev")
    public JAXBElement<CodeType> createAxisAbbrev(CodeType value) {
        return new JAXBElement<CodeType>(_AxisAbbrev_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "seconds")
    public JAXBElement<BigDecimal> createSeconds(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Seconds_QNAME, BigDecimal.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ObliqueCartesianCSRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "usesObliqueCartesianCS")
    public JAXBElement<ObliqueCartesianCSRefType> createUsesObliqueCartesianCS(ObliqueCartesianCSRefType value) {
        return new JAXBElement<ObliqueCartesianCSRefType>(_UsesObliqueCartesianCS_QNAME, ObliqueCartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "methodFormula")
    public JAXBElement<CodeType> createMethodFormula(CodeType value) {
        return new JAXBElement<CodeType>(_MethodFormula_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "status")
    public JAXBElement<StringOrRefType> createStatus(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_Status_QNAME, StringOrRefType.class, null, value);
    }



    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "modifiedCoordinate")
    public JAXBElement<BigInteger> createModifiedCoordinate(BigInteger value) {
        return new JAXBElement<BigInteger>(_ModifiedCoordinate_QNAME, BigInteger.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "coordinateOperationName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createCoordinateOperationName(CodeType value) {
        return new JAXBElement<CodeType>(_CoordinateOperationName_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "covariance")
    public JAXBElement<Double> createCovariance(Double value) {
        return new JAXBElement<Double>(_Covariance_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "origin")
    public JAXBElement<XMLGregorianCalendar> createOrigin(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Origin_QNAME, XMLGregorianCalendar.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageDatumRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "imageDatumRef")
    public JAXBElement<ImageDatumRefType> createImageDatumRef(ImageDatumRefType value) {
        return new JAXBElement<ImageDatumRefType>(_ImageDatumRef_QNAME, ImageDatumRefType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MappingRule")
    public JAXBElement<StringOrRefType> createMappingRule(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_MappingRule_QNAME, StringOrRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "csName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createCsName(CodeType value) {
        return new JAXBElement<CodeType>(_CsName_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Count")
    public JAXBElement<BigInteger> createCount(BigInteger value) {
        return new JAXBElement<BigInteger>(_Count_QNAME, BigInteger.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "axisDirection")
    public JAXBElement<CodeType> createAxisDirection(CodeType value) {
        return new JAXBElement<CodeType>(_AxisDirection_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "parameterName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createParameterName(CodeType value) {
        return new JAXBElement<CodeType>(_ParameterName_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "datumName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createDatumName(CodeType value) {
        return new JAXBElement<CodeType>(_DatumName_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageDatumType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ImageDatum", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractDatum")
    public JAXBElement<ImageDatumType> createImageDatum(ImageDatumType value) {
        return new JAXBElement<ImageDatumType>(_ImageDatum_QNAME, ImageDatumType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "operationVersion")
    public JAXBElement<String> createOperationVersion(String value) {
        return new JAXBElement<String>(_OperationVersion_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "integerValue")
    public JAXBElement<BigInteger> createIntegerValue(BigInteger value) {
        return new JAXBElement<BigInteger>(_IntegerValue_QNAME, BigInteger.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractSurface", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometricPrimitive")
    public JAXBElement<AbstractSurfaceType> createAbstractSurface(AbstractSurfaceType value) {
        return new JAXBElement<AbstractSurfaceType>(_AbstractSurface_QNAME, AbstractSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "columnIndex")
    public JAXBElement<BigInteger> createColumnIndex(BigInteger value) {
        return new JAXBElement<BigInteger>(_ColumnIndex_QNAME, BigInteger.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeGeometricPrimitiveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeGeometricPrimitive", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimePrimitive")
    public JAXBElement<AbstractTimeGeometricPrimitiveType> createTimeGeometricPrimitive(AbstractTimeGeometricPrimitiveType value) {
        return new JAXBElement<AbstractTimeGeometricPrimitiveType>(_TimeGeometricPrimitive_QNAME, AbstractTimeGeometricPrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "version")
    public JAXBElement<String> createVersion(String value) {
        return new JAXBElement<String>(_Version_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "outerBoundaryIs", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "exterior")
    public JAXBElement<AbstractRingPropertyType> createOuterBoundaryIs(AbstractRingPropertyType value) {
        return new JAXBElement<AbstractRingPropertyType>(_OuterBoundaryIs_QNAME, AbstractRingPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePrimitivePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "validTime")
    public JAXBElement<TimePrimitivePropertyType> createValidTime(TimePrimitivePropertyType value) {
        return new JAXBElement<TimePrimitivePropertyType>(_ValidTime_QNAME, TimePrimitivePropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "stringValue")
    public JAXBElement<String> createStringValue(String value) {
        return new JAXBElement<String>(_StringValue_QNAME, String.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "temporalExtent")
    public JAXBElement<TimePeriodType> createTemporalExtent(TimePeriodType value) {
        return new JAXBElement<TimePeriodType>(_TemporalExtent_QNAME, TimePeriodType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "realizationEpoch")
    public JAXBElement<XMLGregorianCalendar> createRealizationEpoch(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RealizationEpoch_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CountList")
    public JAXBElement<List<String>> createCountList(List<String> value) {
        return new JAXBElement<List<String>>(_CountList_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "remarks")
    public JAXBElement<StringOrRefType> createRemarks(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_Remarks_QNAME, StringOrRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateOperationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateOperation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractCoordinateOperationType> createCoordinateOperation(AbstractCoordinateOperationType value) {
        return new JAXBElement<AbstractCoordinateOperationType>(_CoordinateOperation_QNAME, AbstractCoordinateOperationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "measureDescription")
    public JAXBElement<CodeType> createMeasureDescription(CodeType value) {
        return new JAXBElement<CodeType>(_MeasureDescription_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "name")
    public JAXBElement<CodeType> createName(CodeType value) {
        return new JAXBElement<CodeType>(_Name_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "valueFile")
    public JAXBElement<String> createValueFile(String value) {
        return new JAXBElement<String>(_ValueFile_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGeometricPrimitive", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<AbstractGeometricPrimitiveType> createGeometricPrimitive(AbstractGeometricPrimitiveType value) {
        return new JAXBElement<AbstractGeometricPrimitiveType>(_GeometricPrimitive_QNAME, AbstractGeometricPrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "rowIndex")
    public JAXBElement<BigInteger> createRowIndex(BigInteger value) {
        return new JAXBElement<BigInteger>(_RowIndex_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "ellipsoidName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createEllipsoidName(CodeType value) {
        return new JAXBElement<CodeType>(_EllipsoidName_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "maximumOccurs")
    public JAXBElement<BigInteger> createMaximumOccurs(BigInteger value) {
        return new JAXBElement<BigInteger>(_MaximumOccurs_QNAME, BigInteger.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractObject")
    public JAXBElement<Object> createObject(Object value) {
        return new JAXBElement<Object>(_Object_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "BooleanList")
    public JAXBElement<List<String>> createBooleanList(List<String> value) {
        return new JAXBElement<List<String>>(_BooleanList_QNAME, ((Class) List.class), null, ((List<String> ) value));
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Polygon", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractSurface")
    public JAXBElement<PolygonType> createPolygon(PolygonType value) {
        return new JAXBElement<PolygonType>(_Polygon_QNAME, PolygonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "meridianName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createMeridianName(CodeType value) {
        return new JAXBElement<CodeType>(_MeridianName_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "groupName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createGroupName(CodeType value) {
        return new JAXBElement<CodeType>(_GroupName_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CountExtent")
    public JAXBElement<List<String>> createCountExtent(List<String> value) {
        return new JAXBElement<List<String>>(_CountExtent_QNAME, ((Class) List.class), null, ((List<String> ) value));
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CoordinatesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "tupleList")
    public JAXBElement<CoordinatesType> createTupleList(CoordinatesType value) {
        return new JAXBElement<CoordinatesType>(_TupleList_QNAME, CoordinatesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "boundingPolygon")
    public JAXBElement<PolygonType> createBoundingPolygon(PolygonType value) {
        return new JAXBElement<PolygonType>(_BoundingPolygon_QNAME, PolygonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CRSRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "crsRef")
    public JAXBElement<CRSRefType> createCrsRef(CRSRefType value) {
        return new JAXBElement<CRSRefType>(_CrsRef_QNAME, CRSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "isSphere")
    public JAXBElement<String> createIsSphere(String value) {
        return new JAXBElement<String>(_IsSphere_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "booleanValue")
    public JAXBElement<Boolean> createBooleanValue(Boolean value) {
        return new JAXBElement<Boolean>(_BooleanValue_QNAME, Boolean.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ObliqueCartesianCSRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "obliqueCartesianCSRef")
    public JAXBElement<ObliqueCartesianCSRefType> createObliqueCartesianCSRef(ObliqueCartesianCSRefType value) {
        return new JAXBElement<ObliqueCartesianCSRefType>(_ObliqueCartesianCSRef_QNAME, ObliqueCartesianCSRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "targetDimensions")
    public JAXBElement<BigInteger> createTargetDimensions(BigInteger value) {
        return new JAXBElement<BigInteger>(_TargetDimensions_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoordinateSystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateSystem", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<AbstractCoordinateSystemType> createCoordinateSystem(AbstractCoordinateSystemType value) {
        return new JAXBElement<AbstractCoordinateSystemType>(_CoordinateSystem_QNAME, AbstractCoordinateSystemType.class, null, value);
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
    public JAXBElement<AbstractTimeObjectType> createTimeObject(AbstractTimeObjectType value) {
        return new JAXBElement<AbstractTimeObjectType>(_TimeObject_QNAME, AbstractTimeObjectType.class, null, value);
    }

   /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractGeometry", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractGeometryType> createGeometry(AbstractGeometryType value) {
        return new JAXBElement<AbstractGeometryType>(_Geometry_QNAME, AbstractGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "srsName", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "name")
    public JAXBElement<CodeType> createSrsName(CodeType value) {
        return new JAXBElement<CodeType>(_SrsName_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "scope")
    public JAXBElement<String> createScope(String value) {
        return new JAXBElement<String>(_Scope_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceSystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractCoordinateReferenceSystem", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractCRS")
    public JAXBElement<AbstractReferenceSystemType> createCoordinateReferenceSystem(AbstractReferenceSystemType value) {
        return new JAXBElement<AbstractReferenceSystemType>(_CoordinateReferenceSystem_QNAME, AbstractReferenceSystemType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LocationKeyWord")
    public JAXBElement<CodeType> createLocationKeyWord(CodeType value) {
        return new JAXBElement<CodeType>(_LocationKeyWord_QNAME, CodeType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "srsID")
    public JAXBElement<IdentifierType> createSrsID(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_SrsID_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractImplicitGeometry", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGeometry")
    public JAXBElement<AbstractGeometryType> createImplicitGeometry(AbstractGeometryType value) {
        return new JAXBElement<AbstractGeometryType>(_ImplicitGeometry_QNAME, AbstractGeometryType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link StringOrRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LocationString")
    public JAXBElement<StringOrRefType> createLocationString(StringOrRefType value) {
        return new JAXBElement<StringOrRefType>(_LocationString_QNAME, StringOrRefType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "anchorPoint")
    public JAXBElement<CodeType> createAnchorPoint(CodeType value) {
        return new JAXBElement<CodeType>(_AnchorPoint_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "integerValueList")
    public JAXBElement<List<BigInteger>> createIntegerValueList(List<BigInteger> value) {
        return new JAXBElement<List<BigInteger>>(_IntegerValueList_QNAME, ((Class) List.class), null, ((List<BigInteger> ) value));
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPositionalAccuracyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractpositionalAccuracy")
    public JAXBElement<AbstractPositionalAccuracyType> createPositionalAccuracy(AbstractPositionalAccuracyType value) {
        return new JAXBElement<AbstractPositionalAccuracyType>(_PositionalAccuracy_QNAME, AbstractPositionalAccuracyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeInstantType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "TimeInstant", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractTimeGeometricPrimitive")
    public JAXBElement<TimeInstantType> createTimeInstant(TimeInstantType value) {
        return new JAXBElement<TimeInstantType>(_TimeInstant_QNAME, TimeInstantType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "FeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<FeatureCollectionType> createFeatureCollection(FeatureCollectionType value) {
        return new JAXBElement<FeatureCollectionType>(_FeatureCollection_QNAME, FeatureCollectionType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "featureMembers")
    public JAXBElement<FeatureArrayPropertyType> createFeatureMembers(FeatureArrayPropertyType value) {
        return new JAXBElement<FeatureArrayPropertyType>(_FeatureMembers_QNAME, FeatureArrayPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimeSliceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "AbstractTimeSlice", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<AbstractTimeSliceType> createTimeSlice(AbstractTimeSliceType value) {
        return new JAXBElement<AbstractTimeSliceType>(_TimeSlice_QNAME, AbstractTimeSliceType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "inverseFlattening")
    public JAXBElement<MeasureType> createInverseFlattening(MeasureType value) {
        return new JAXBElement<MeasureType>(_InverseFlattening_QNAME, MeasureType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "value")
    public JAXBElement<MeasureType> createValue(MeasureType value) {
        return new JAXBElement<MeasureType>(_Value_QNAME, MeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "semiMinorAxis")
    public JAXBElement<MeasureType> createSemiMinorAxis(MeasureType value) {
        return new JAXBElement<MeasureType>(_SemiMinorAxis_QNAME, MeasureType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectionPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "direction")
    public JAXBElement<DirectionPropertyType> createDirection(DirectionPropertyType value) {
        return new JAXBElement<DirectionPropertyType>(_Direction_QNAME, DirectionPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CompassPointEnumeration }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompassPoint")
    public JAXBElement<CompassPointEnumeration> createCompassPoint(CompassPointEnumeration value) {
        return new JAXBElement<CompassPointEnumeration>(_CompassPoint_QNAME, CompassPointEnumeration.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "semiMajorAxis")
    public JAXBElement<MeasureType> createSemiMajorAxis(MeasureType value) {
        return new JAXBElement<MeasureType>(_SemiMajorAxis_QNAME, MeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "result")
    public JAXBElement<MeasureType> createResult(MeasureType value) {
        return new JAXBElement<MeasureType>(_Result_QNAME, MeasureType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_association")
    public JAXBElement<AssociationType> createAssociation(AssociationType value) {
        return new JAXBElement<AssociationType>(_Association_QNAME, AssociationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "resultOf")
    public JAXBElement<AssociationType> createResultOf(AssociationType value) {
        return new JAXBElement<AssociationType>(_ResultOf_QNAME, AssociationType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link AssociationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "_strictAssociation")
    public JAXBElement<AssociationType> createStrictAssociation(AssociationType value) {
        return new JAXBElement<AssociationType>(_StrictAssociation_QNAME, AssociationType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiPosition")
    public JAXBElement<MultiPointPropertyType> createMultiPosition(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiPosition_QNAME, MultiPointPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "multiCenterOf")
    public JAXBElement<MultiPointPropertyType> createMultiCenterOf(MultiPointPropertyType value) {
        return new JAXBElement<MultiPointPropertyType>(_MultiCenterOf_QNAME, MultiPointPropertyType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiPoint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_GeometricAggregate")
    public JAXBElement<MultiPointType> createMultiPoint(MultiPointType value) {
        return new JAXBElement<MultiPointType>(_MultiPoint_QNAME, MultiPointType.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link PointArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "pointArrayProperty")
    public JAXBElement<PointArrayPropertyType> createPointArrayProperty(PointArrayPropertyType value) {
        return new JAXBElement<PointArrayPropertyType>(_PointArrayProperty_QNAME, PointArrayPropertyType.class, null, value);
    }

        /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeaturePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "featureMember")
    public JAXBElement<FeaturePropertyType> createFeatureMember(FeaturePropertyType value) {
        return new JAXBElement<FeaturePropertyType>(_FeatureMember_QNAME, FeaturePropertyType.class, null, value);
    }

}
