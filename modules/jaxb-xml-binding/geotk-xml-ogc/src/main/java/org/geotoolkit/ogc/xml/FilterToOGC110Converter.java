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
import javax.xml.bind.JAXBElement;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.logging.Logging;
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
import org.geotoolkit.ogc.xml.v110.BinaryLogicOpType;
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
import org.geotoolkit.ogc.xml.v110.UnaryLogicOpType;
import org.geotoolkit.ogc.xml.v110.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v110.WithinType;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterToOGC110Converter implements FilterToOGCConverter<FilterType> {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    protected final ObjectFactory ogc_factory;
    private final org.geotoolkit.gml.xml.v311.ObjectFactory gml_factory;

    public FilterToOGC110Converter() {
        ogc_factory = new ObjectFactory();
        this.gml_factory = new org.geotoolkit.gml.xml.v311.ObjectFactory();
    }

    public JAXBElement<?> extract(final Expression exp) {
        JAXBElement<?> jax = null;

        if (exp instanceof Function) {
            final Function function = (Function) exp;
            final FunctionType ft = ogc_factory.createFunctionType();
            ft.setName(function.getName());
            for (final Expression ex : function.getParameters()) {
                ft.getExpression().add(extract(ex));
            }
            jax = ogc_factory.createFunction(ft);
        } else if (exp instanceof Multiply) {
            final Multiply multiply = (Multiply) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(multiply.getExpression1()));
            bot.getExpression().add(extract(multiply.getExpression2()));
            jax = ogc_factory.createMul(bot);
        } else if (exp instanceof Literal) {
            final LiteralType literal = ogc_factory.createLiteralType();
            Object val = ((Literal) exp).getValue();
            if (val instanceof Color) {
                val = FilterUtilities.toString((Color)val);
            }
            literal.setContent(val == null? null : val.toString());
            jax = ogc_factory.createLiteral(literal);
        } else if (exp instanceof Add) {
            final Add add = (Add) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(add.getExpression1()));
            bot.getExpression().add(extract(add.getExpression2()));
            jax = ogc_factory.createAdd(bot);
        } else if (exp instanceof Divide) {
            final Divide divide = (Divide) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(divide.getExpression1()));
            bot.getExpression().add(extract(divide.getExpression2()));
            jax = ogc_factory.createDiv(bot);
        } else if (exp instanceof Subtract) {
            final Subtract substract = (Subtract) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(substract.getExpression1()));
            bot.getExpression().add(extract(substract.getExpression2()));
            jax = ogc_factory.createSub(bot);
        } else if (exp instanceof PropertyName) {
            final PropertyNameType literal = ogc_factory.createPropertyNameType();
            literal.setContent(((PropertyName) exp).getPropertyName());
            jax = ogc_factory.createPropertyName(literal);
        } else if (exp instanceof NilExpression) {
            //DO nothing on NILL expression
        } else {
            throw new IllegalArgumentException("Unknowed expression element :" + exp);
        }

        return jax;
    }

    public JAXBElement<?> visit(final Filter filter) {
        if (filter.equals(Filter.INCLUDE)) {
            return null;
        }
        if (filter.equals(Filter.EXCLUDE)) {
            return null;
        }

        if (filter instanceof PropertyIsBetween) {
            final PropertyIsBetween pib = (PropertyIsBetween) filter;
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));
            final PropertyIsBetweenType bot = new PropertyIsBetweenType(extract(pib.getExpression()), lbt, ubt);
            return ogc_factory.createPropertyIsBetween(bot);
        } else if (filter instanceof PropertyIsEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsEqualToType bot = ogc_factory.createPropertyIsEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsEqualTo(bot);
        } else if (filter instanceof PropertyIsGreaterThan) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanType bot = ogc_factory.createPropertyIsGreaterThanType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThan(bot);
        } else if (filter instanceof PropertyIsGreaterThanOrEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanOrEqualToType bot = ogc_factory.createPropertyIsGreaterThanOrEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(bot);
        } else if (filter instanceof PropertyIsLessThan) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanType bot = ogc_factory.createPropertyIsLessThanType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThan(bot);
        } else if (filter instanceof PropertyIsLessThanOrEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanOrEqualToType bot = ogc_factory.createPropertyIsLessThanOrEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(bot);
        } else if (filter instanceof PropertyIsLike) {
            final PropertyIsLike pis = (PropertyIsLike) filter;
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscapeChar(pis.getEscape());
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.setContent(pis.getLiteral());
            bot.setLiteral(lt.getStringValue());
            if (!(pis.getExpression() instanceof PropertyName)) {
                throw new IllegalArgumentException("PropertyIsLike can support PropertyName only, but was a " + pis.getExpression());
            }
            final PropertyNameType pnt = (PropertyNameType) extract(pis.getExpression()).getValue();
            bot.setPropertyName(pnt);
            bot.setSingleChar(pis.getSingleChar());
            bot.setWildCard(pis.getWildCard());
            return ogc_factory.createPropertyIsLike(bot);
        } else if (filter instanceof PropertyIsNotEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsNotEqualToType bot = ogc_factory.createPropertyIsNotEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsNotEqualTo(bot);
        } else if (filter instanceof PropertyIsNull) {
            final PropertyIsNull pis = (PropertyIsNull) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            final Object obj = extract(pis.getExpression()).getValue();
            bot.setPropertyName((PropertyNameType) obj);

            return ogc_factory.createPropertyIsNull(bot);
        } else if (filter instanceof And) {
            final And and = (And) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for (final Filter f : and.getChildren()) {
                final JAXBElement<? extends LogicOpsType> ele = (JAXBElement<? extends LogicOpsType>) visit(f);
                if (ele != null) {
                    lot.getLogicOps().add(ele);
                }
            }

            return ogc_factory.createAnd(new AndType(lot.getLogicOps().toArray()));
        } else if (filter instanceof Or) {
            final Or or = (Or) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for (final Filter f : or.getChildren()) {
                final JAXBElement<? extends LogicOpsType> ele = (JAXBElement<? extends LogicOpsType>) visit(f);
                if (ele != null) {
                    lot.getLogicOps().add(ele);
                }
            }
            return ogc_factory.createOr(new OrType(lot.getLogicOps().toArray()));
        } else if (filter instanceof Not) {
            final Not not = (Not) filter;
            final UnaryLogicOpType lot = ogc_factory.createUnaryLogicOpType();
            final JAXBElement<?> sf = visit(not.getFilter());

            if (sf.getValue() instanceof ComparisonOpsType) {
                lot.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getComparisonOps().getValue()));
            }
            if (sf.getValue() instanceof LogicOpsType) {
                lot.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getLogicOps().getValue()));
            }
            if (sf.getValue() instanceof SpatialOpsType) {
                lot.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getSpatialOps().getValue()));
            }
            //should not happen
            throw new IllegalArgumentException("invalide filter element : " + sf);
        } else if (filter instanceof FeatureId) {
            throw new IllegalArgumentException("Not parsed yet : " + filter);
        } else if (filter instanceof BBOX) {
            final BBOX bbox = (BBOX) filter;

            final Expression left = bbox.getExpression1();
            final Expression right = bbox.getExpression2();

            final String property;
            final double minx;
            final double maxx;
            final double miny;
            final double maxy;
            String srs;

            if (left instanceof PropertyName) {
                property = ((PropertyName) left).getPropertyName();

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

            } else if (right instanceof PropertyName) {
                property = ((PropertyName) right).getPropertyName();

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
        } else if (filter instanceof Id) {
            //todo OGC filter can not handle ID when we are inside another filter type
            //so here we make a small tric to change an id filter in a serie of propertyequal filter
            //this is not really legal but we dont have the choice here
            //we should propose an evolution of ogc filter do consider id filter as a comparison filter
            final PropertyName n = FF.property(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            final List<Filter> lst = new ArrayList<Filter>();

            for (Identifier ident : ((Id) filter).getIdentifiers()) {
                lst.add(FF.equals(n, FF.literal(ident.getID().toString())));
            }

            if (lst.isEmpty()) {
                return null;
            } else if (lst.size() == 1) {
                return visit(lst.get(0));
            } else {
                return visit(FF.and(lst));
            }

        } else if (filter instanceof BinarySpatialOperator) {
            final BinarySpatialOperator spatialOp = (BinarySpatialOperator) filter;

            Expression exp1 = spatialOp.getExpression1();
            Expression exp2 = spatialOp.getExpression2();

            if (!(exp1 instanceof PropertyName)) {
                //flip order
                final Expression ex = exp1;
                exp1 = exp2;
                exp2 = ex;
            }

            if (!(exp1 instanceof PropertyName)) {
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
                    Logging.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
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
                    Logging.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                }

                ee.setLowerCorner(new DirectPositionType(genv.getLowerCorner()));
                ee.setUpperCorner(new DirectPositionType(genv.getUpperCorner()));

                jaxGeom = null;
                jaxEnv = gml_factory.createEnvelope(ee);
            } else {
                throw new IllegalArgumentException("Type is not geometric or envelope.");
            }

            if (filter instanceof Beyond) {
                final BeyondType jaxelement = ogc_factory.createBeyondType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setPropertyName(pnt.getValue());
                return ogc_factory.createBeyond(jaxelement);
            } else if (filter instanceof Contains) {
                final ContainsType jaxelement = ogc_factory.createContainsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createContains(jaxelement);
            } else if (filter instanceof Crosses) {
                final CrossesType jaxelement = ogc_factory.createCrossesType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createCrosses(jaxelement);
            } else if (filter instanceof DWithin) {
                final DWithinType jaxelement = ogc_factory.createDWithinType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setPropertyName(pnt.getValue());
                return ogc_factory.createDWithin(jaxelement);
            } else if (filter instanceof Disjoint) {
                final DisjointType jaxelement = ogc_factory.createDisjointType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createDisjoint(jaxelement);
            } else if (filter instanceof Equals) {
                final EqualsType jaxelement = ogc_factory.createEqualsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createEquals(jaxelement);
            } else if (filter instanceof Intersects) {
                final IntersectsType jaxelement = ogc_factory.createIntersectsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createIntersects(jaxelement);
            } else if (filter instanceof Overlaps) {
                final OverlapsType jaxelement = new OverlapsType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createOverlaps(jaxelement);
            } else if (filter instanceof Touches) {
                final TouchesType jaxelement = ogc_factory.createTouchesType();
                jaxelement.setAbstractGeometry(jaxGeom);
                jaxelement.setEnvelope(jaxEnv);
                jaxelement.setPropertyName(pnt);
                return ogc_factory.createTouches(jaxelement);
            } else if (filter instanceof Within) {
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

    public List<JAXBElement<AbstractIdType>> visitFilter(final Id filter) {

        final List<JAXBElement<AbstractIdType>> lst = new ArrayList<JAXBElement<AbstractIdType>>();

        for (Identifier ident : filter.getIdentifiers()) {
            final FeatureIdType fit = ogc_factory.createFeatureIdType();
            fit.setFid(ident.getID().toString());
            final JAXBElement jax = ogc_factory.createFeatureId(fit);
            lst.add(jax);
        }

        return lst;
    }

    @Override
    public FilterType apply(final Filter filter) {
        final FilterType ft = ogc_factory.createFilterType();

        if (filter instanceof Id) {
            ft.getId().addAll(visitFilter((Id) filter));
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
