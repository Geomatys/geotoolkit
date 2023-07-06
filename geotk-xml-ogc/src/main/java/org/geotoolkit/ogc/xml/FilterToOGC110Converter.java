/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.ogc.xml;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBElement;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.gml.GMLUtilities;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.MultiCurveType;
import org.geotoolkit.gml.xml.v311.MultiGeometryType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.ogc.xml.v110.AbstractIdType;
import org.geotoolkit.ogc.xml.v110.AndType;
import org.geotoolkit.ogc.xml.v110.BBOXType;
import org.geotoolkit.ogc.xml.v110.BeyondType;
import org.geotoolkit.ogc.xml.v110.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.ContainsType;
import org.geotoolkit.ogc.xml.v110.CrossesType;
import org.geotoolkit.ogc.xml.v110.DWithinType;
import org.geotoolkit.ogc.xml.v110.DisjointType;
import org.geotoolkit.ogc.xml.v110.EqualsType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.IntersectsType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v110.NotType;
import org.geotoolkit.ogc.xml.v110.ObjectFactory;
import org.geotoolkit.ogc.xml.v110.OrType;
import org.geotoolkit.ogc.xml.v110.OverlapsType;
import org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
import org.geotoolkit.ogc.xml.v110.TouchesType;
import org.geotoolkit.ogc.xml.v110.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v110.WithinType;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.NullOperator;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.CodeList;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterToOGC110Converter implements FilterToOGCConverter<FilterType> {

    private static final FilterFactory FF = org.geotoolkit.filter.FilterUtilities.FF;

    protected final ObjectFactory ogc_factory;
    private final org.geotoolkit.gml.xml.v311.ObjectFactory gml_factory;

    public FilterToOGC110Converter() {
        ogc_factory = new ObjectFactory();
        this.gml_factory = new org.geotoolkit.gml.xml.v311.ObjectFactory();
    }

    public JAXBElement<?> extract(final Expression exp) {
        final JAXBElement<?> jax;
        final List<Expression<? super Object, ?>> parameters = exp.getParameters();
        switch (exp.getFunctionName().tip().toString()) {
            case "Multiply": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createMul(bot);
                break;
            }
            case "Literal": {
                final LiteralType literal = ogc_factory.createLiteralType();
                Object val = ((Literal) exp).getValue();
                if (val instanceof Color) {
                    val = FilterUtilities.toString((Color)val);
                }
                literal.setContent(val == null? null : val.toString());
                jax = ogc_factory.createLiteral(literal);
                break;
            }
            case "Add": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createAdd(bot);
                break;
            }
            case "Divide": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createDiv(bot);
                break;
            }
            case "Subtract": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createSub(bot);
                break;
            }
            case "PropertyName":
            case "ValueReference": {
                final PropertyNameType literal = ogc_factory.createPropertyNameType();
                literal.setContent(((ValueReference) exp).getXPath());
                jax = ogc_factory.createPropertyName(literal);
                break;
            }
            default: {
                final FunctionType ft = ogc_factory.createFunctionType();
                ft.setName(exp.getFunctionName().tip().toString());
                for (final Expression ex : parameters) {
                    ft.getExpression().add(extract(ex));
                }
                jax = ogc_factory.createFunction(ft);
                break;
            }
        }
        return jax;
    }

    public JAXBElement<?> visit(final Filter filter) {
        if (filter.equals(Filter.include())) {
            return null;
        }
        if (filter.equals(Filter.exclude())) {
            return null;
        }
        final CodeList<?> type = filter.getOperatorType();
        if (filter instanceof BetweenComparisonOperator) {
            final BetweenComparisonOperator pib = (BetweenComparisonOperator) filter;
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));
            final PropertyIsBetweenType bot = new PropertyIsBetweenType(extract(pib.getExpression()), lbt, ubt);
            return ogc_factory.createPropertyIsBetween(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsEqualToType bot = ogc_factory.createPropertyIsEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsEqualTo(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanType bot = ogc_factory.createPropertyIsGreaterThanType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsGreaterThan(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanOrEqualToType bot = ogc_factory.createPropertyIsGreaterThanOrEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanType bot = ogc_factory.createPropertyIsLessThanType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsLessThan(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanOrEqualToType bot = ogc_factory.createPropertyIsLessThanOrEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(bot);
        } else if (filter instanceof LikeOperator) {
            final LikeOperator pis = (LikeOperator) filter;
            final List<Expression> expressions = filter.getExpressions();
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscapeChar(String.valueOf(pis.getEscapeChar()));
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.setContent(((Literal) expressions.get(1)).getValue());
            bot.setLiteral(lt.getStringValue());
            final Expression expression = expressions.get(0);
            if (!(expression instanceof ValueReference)) {
                throw new IllegalArgumentException("LikeOperator can support ValueReference only, but was a " + expression);
            }
            final PropertyNameType pnt = (PropertyNameType) extract(expression).getValue();
            bot.setPropertyName(pnt);
            bot.setSingleChar(String.valueOf(pis.getSingleChar()));
            bot.setWildCard(String.valueOf(pis.getWildCard()));
            return ogc_factory.createPropertyIsLike(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsNotEqualToType bot = ogc_factory.createPropertyIsNotEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsNotEqualTo(bot);
        } else if (filter instanceof NullOperator) {
            final NullOperator pis = (NullOperator) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            final Object obj = extract((Expression) pis.getExpressions().get(0)).getValue();
            bot.setPropertyName((PropertyNameType) obj);
            return ogc_factory.createPropertyIsNull(bot);
        } else if (type == LogicalOperatorName.AND) {
            final LogicalOperator and = (LogicalOperator) filter;
            final List<JAXBElement> children = new ArrayList<>();
            for (final Filter f : (List<Filter>) and.getOperands()) {
                final JAXBElement<? extends LogicOpsType> ele = (JAXBElement<? extends LogicOpsType>) visit(f);
                if (ele != null) {
                    children.add(ele);
                }
            }
            return ogc_factory.createAnd(new AndType(children.toArray()));
        } else if (type == LogicalOperatorName.OR) {
            final LogicalOperator or = (LogicalOperator) filter;
            final List<JAXBElement> children = new ArrayList<>();
            for (final Filter f : (List<Filter>) or.getOperands()) {
                final JAXBElement<? extends LogicOpsType> ele = (JAXBElement<? extends LogicOpsType>) visit(f);
                if (ele != null) {
                    children.add(ele);
                }
            }
            return ogc_factory.createOr(new OrType(children.toArray()));
        } else if (type == LogicalOperatorName.NOT) {
            final LogicalOperator not = (LogicalOperator) filter;
            JAXBElement<?> sf = visit((Filter) not.getOperands().get(0));
            return ogc_factory.createNot(new NotType(sf.getValue()));
        } else if (filter instanceof ResourceId) {
            throw new IllegalArgumentException("Not parsed yet : " + filter);
        } else if (type == SpatialOperatorName.BBOX) {
            final BBOX bbox = BBOX.wrap((BinarySpatialOperator) filter);
            final Expression left = bbox.getOperand1();
            final Expression right = bbox.getOperand2();
            final String property;
            final double minx;
            final double maxx;
            final double miny;
            final double maxy;
            String srs;
            if (left instanceof ValueReference) {
                property = ((ValueReference) left).getXPath();
                final Object objGeom = ((Literal) right).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
                        if (srs == null) {
                            srs = ReferencingUtilities.lookupIdentifier(env.getCoordinateReferenceSystem(), true);
                        }
                    } catch (FactoryException ex) {
                        throw new IllegalArgumentException("invalid bbox element : " + filter + " " + ex.getMessage(), ex);
                    }
                } else if (objGeom instanceof Geometry) {
                    final Geometry geom = (Geometry) objGeom;
                    final Envelope env = geom.getEnvelopeInternal();
                    minx = env.getMinX();
                    maxx = env.getMaxX();
                    miny = env.getMinY();
                    maxy = env.getMaxY();
                    srs = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                } else {
                    throw new IllegalArgumentException("invalide bbox element : " + filter);
                }
            } else if (right instanceof ValueReference) {
                property = ((ValueReference) right).getXPath();
                final Object objGeom = ((Literal) left).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
                    } catch (FactoryException ex) {
                        throw new IllegalArgumentException("invalide bbox element : " + filter + " " + ex.getMessage(), ex);
                    }
                } else if (objGeom instanceof Geometry) {
                    final Geometry geom = (Geometry) objGeom;
                    final Envelope env = geom.getEnvelopeInternal();
                    minx = env.getMinX();
                    maxx = env.getMaxX();
                    miny = env.getMinY();
                    maxy = env.getMaxY();
                    srs = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                } else {
                    throw new IllegalArgumentException("invalide bbox element : " + filter);
                }
            } else {
                throw new IllegalArgumentException("invalide bbox element : " + filter);
            }
            final BBOXType bbtype = new BBOXType(property, minx, miny, maxx, maxy, srs);
            return ogc_factory.createBBOX(bbtype);
        } else if (filter instanceof ResourceId) {
            //todo OGC filter can not handle ID when we are inside another filter type
            //so here we make a small tric to change an id filter in a serie of propertyequal filter
            //this is not really legal but we dont have the choice here
            //we should propose an evolution of ogc filter do consider id filter as a comparison filter
            final ValueReference n = FF.property(AttributeConvention.IDENTIFIER);
            String ident = ((ResourceId) filter).getIdentifier();
            return visit(FF.equal(n, FF.literal(ident)));
        } else if (filter instanceof SpatialOperator) {
            final BinarySpatialOperator spatialOp = (BinarySpatialOperator) filter;
            Expression exp1 = spatialOp.getOperand1();
            Expression exp2 = spatialOp.getOperand2();
            if (!(exp1 instanceof ValueReference)) {
                //flip order
                final Expression ex = exp1;
                exp1 = exp2;
                exp2 = ex;
            }
            if (!(exp1 instanceof ValueReference)) {
                throw new IllegalArgumentException("Filter can not be transformed in wml filter, "
                        + "expression are not of the requiered type ");
            }
            final JAXBElement<PropertyNameType> pnt = (JAXBElement<PropertyNameType>) extract(exp1);
            final JAXBElement<EnvelopeType> jaxEnv;
            final JAXBElement<? extends AbstractGeometryType> jaxGeom;
            final Object geom = ((Literal) exp2).getValue();
            if (geom instanceof Geometry) {
                final Geometry jts = (Geometry) geom;
                final String srid = SRIDGenerator.toSRS(jts.getSRID(), SRIDGenerator.Version.V1);
                CoordinateReferenceSystem crs;
                try {
                    crs = CRS.forCode(srid);
                } catch (Exception ex) {
                    Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                    crs = null;
                }
                final AbstractGeometryType gt = GMLUtilities.getGMLFromISO(JTSUtils.toISO(jts, crs));
                // TODO use gml method to return any JAXBElement
                if (gt instanceof PointType) {
                    jaxGeom = gml_factory.createPoint((PointType) gt);
                } else if (gt instanceof CurveType) {
                    jaxGeom = gml_factory.createCurve((CurveType) gt);
                } else if (gt instanceof LineStringType) {
                    jaxGeom = gml_factory.createLineString((LineStringType) gt);
                } else if (gt instanceof PolygonType) {
                    jaxGeom = gml_factory.createPolygon((PolygonType) gt);
                } else if (gt instanceof MultiPolygonType) {
                    jaxGeom = gml_factory.createMultiPolygon((MultiPolygonType) gt);
                } else if (gt instanceof MultiLineStringType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiCurveType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiPointType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiGeometryType) {
                    jaxGeom = gml_factory.createMultiGeometry((MultiGeometryType) gt);
                } else if (gt instanceof PolyhedralSurfaceType) {
                    jaxGeom = gml_factory.createPolyhedralSurface((PolyhedralSurfaceType) gt);
                } else if (gt != null) {
                    throw new IllegalArgumentException("unexpected Geometry type:" + gt.getClass().getName());
                } else {
                    jaxGeom = null;
                }
                jaxEnv = null;
            } else if (geom instanceof org.opengis.geometry.Geometry) {
                final AbstractGeometryType gt = GMLUtilities.getGMLFromISO((org.opengis.geometry.Geometry) geom);
                // TODO use gml method to return any JAXBElement
                if (gt instanceof PointType) {
                    jaxGeom = gml_factory.createPoint((PointType) gt);
                } else if (gt instanceof CurveType) {
                    jaxGeom = gml_factory.createCurve((CurveType) gt);
                } else if (gt instanceof LineStringType) {
                    jaxGeom = gml_factory.createLineString((LineStringType) gt);
                } else if (gt instanceof PolygonType) {
                    jaxGeom = gml_factory.createPolygon((PolygonType) gt);
                } else if (gt instanceof MultiPolygonType) {
                    jaxGeom = gml_factory.createMultiPolygon((MultiPolygonType) gt);
                } else if (gt instanceof MultiLineStringType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiCurveType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiPointType) {
                    jaxGeom = gml_factory.createMultiLineString((MultiLineStringType) gt);
                } else if (gt instanceof MultiGeometryType) {
                    jaxGeom = gml_factory.createMultiGeometry((MultiGeometryType) gt);
                } else if (gt instanceof PolyhedralSurfaceType) {
                    jaxGeom = gml_factory.createPolyhedralSurface((PolyhedralSurfaceType) gt);
                } else if (gt != null) {
                    throw new IllegalArgumentException("unexpected Geometry type:" + gt.getClass().getName());
                } else {
                    jaxGeom = null;
                }
                jaxEnv = null;
            } else if (geom instanceof org.opengis.geometry.Envelope) {
                final org.opengis.geometry.Envelope genv = (org.opengis.geometry.Envelope) geom;
                EnvelopeType ee = gml_factory.createEnvelopeType();
                try {
                    ee.setSrsName(IdentifiedObjects.lookupURN(genv.getCoordinateReferenceSystem(), null));
                } catch (FactoryException ex) {
                    Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                }
                ee.setLowerCorner(new DirectPositionType(genv.getLowerCorner()));
                ee.setUpperCorner(new DirectPositionType(genv.getUpperCorner()));
                jaxGeom = null;
                jaxEnv = gml_factory.createEnvelope(ee);
            } else {
                throw new IllegalArgumentException("Type is not geometric or envelope.");
            }
            if (type == DistanceOperatorName.BEYOND) {
                final BeyondType jaxelement = ogc_factory.createBeyondType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setPropertyName(pnt.getValue());
                return ogc_factory.createBeyond(jaxelement);
            } else if (type == SpatialOperatorName.CONTAINS) {
                final ContainsType jaxelement = ogc_factory.createContainsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createContains(jaxelement);
            } else if (type == SpatialOperatorName.CROSSES) {
                final CrossesType jaxelement = ogc_factory.createCrossesType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createCrosses(jaxelement);
            } else if (type == DistanceOperatorName.WITHIN) {
                final DWithinType jaxelement = ogc_factory.createDWithinType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setPropertyName(pnt.getValue());
                return ogc_factory.createDWithin(jaxelement);
            } else if (type == SpatialOperatorName.DISJOINT) {
                final DisjointType jaxelement = ogc_factory.createDisjointType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createDisjoint(jaxelement);
            } else if (type == SpatialOperatorName.EQUALS) {
                final EqualsType jaxelement = ogc_factory.createEqualsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createEquals(jaxelement);
            } else if (type == SpatialOperatorName.INTERSECTS) {
                final IntersectsType jaxelement = ogc_factory.createIntersectsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createIntersects(jaxelement);
            } else if (type == SpatialOperatorName.OVERLAPS) {
                final OverlapsType jaxelement = new OverlapsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createOverlaps(jaxelement);
            } else if (type == SpatialOperatorName.TOUCHES) {
                final TouchesType jaxelement = ogc_factory.createTouchesType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createTouches(jaxelement);
            } else if (type == SpatialOperatorName.WITHIN) {
                final WithinType jaxelement = ogc_factory.createWithinType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createWithin(jaxelement);
            } else {
                throw new IllegalArgumentException("Unknowed filter element : " + filter + " class :" + filter.getClass());
            }
        } else {
            throw new IllegalArgumentException("Unknowed filter element : " + filter + " class :" + filter.getClass());
        }
    }

    public JAXBElement<AbstractIdType> visitFilter(final ResourceId filter) {
        final String ident = filter.getIdentifier();
        final FeatureIdType fit = ogc_factory.createFeatureIdType();
        fit.setFid(ident);
        final JAXBElement jax = ogc_factory.createFeatureId(fit);
        return jax;
    }

    @Override
    public FilterType apply(final Filter filter) {
        final FilterType ft = ogc_factory.createFilterType();
        if (filter instanceof ResourceId) {
            ft.getId().add(visitFilter((ResourceId) filter));
        } else {
            final JAXBElement<?> sf = visit(filter);
            if (sf == null) {
                return null;
            } else if (sf.getValue() instanceof ComparisonOpsType) {
                ft.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
            } else if (sf.getValue() instanceof LogicOpsType) {
                ft.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
            } else if (sf.getValue() instanceof SpatialOpsType) {
                ft.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
            } else {
                //should not happen
                throw new IllegalArgumentException("invalide filter element : " + sf);
            }
        }
        return ft;
    }
}
