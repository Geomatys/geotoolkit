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
package org.geotoolkit.ogc.xml;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.geotoolkit.util.NamesExt;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.ogc.xml.v110.AbstractIdType;
import org.geotoolkit.ogc.xml.v110.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.FeatureIdType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.GmlObjectIdType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SortByType;
import org.geotoolkit.ogc.xml.v110.SortPropertyType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ObjectConverters;

import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.geotoolkit.filter.FilterFactory2;
import org.opengis.filter.MatchAction;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.Literal;
import org.opengis.filter.SortProperty;

/**
 * Transform OGC jaxb xml in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OGC110toGTTransformer {

    protected final FilterFactory2 filterFactory;

    private final Map<String, String> namespaceMapping;

    public OGC110toGTTransformer(final FilterFactory2 factory) {
        this.filterFactory   = factory;
        this.namespaceMapping = null;
    }

    public OGC110toGTTransformer(final FilterFactory2 factory, final Map<String, String> namespaceMapping) {
        this.filterFactory = factory;
        this.namespaceMapping = namespaceMapping;
    }

    /**
     * Transform a SLD filter v1.1 in GT filter.
     */
    public Filter visitFilter(final org.geotoolkit.ogc.xml.v110.FilterType ft)
            throws FactoryException{
        if (ft == null) {return null;}

        if(ft.getComparisonOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.ComparisonOpsType> jax = ft.getComparisonOps();
            return visitComparisonOp(jax);
        }else if(ft.getLogicOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.LogicOpsType> jax = ft.getLogicOps();
            return visitLogicOp(jax);
        }else if(ft.getSpatialOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.SpatialOpsType> jax = ft.getSpatialOps();
            return visitSpatialOp(jax);
        }else if(ft.getId() != null && !ft.getId().isEmpty()){
            return visitIds(ft.getId());
        }else{
            //this case should not happen but if so, we consider it's an ALL features filter
            return Filter.include();
        }
    }

    /**
     * Transform a SLD spatial Filter v1.1 in GT filter.
     */
    public Filter visitSpatialOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.SpatialOpsType> jax)
            throws NoSuchAuthorityCodeException, FactoryException {
        final org.geotoolkit.ogc.xml.v110.SpatialOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.BinarySpatialOpType) {
            final org.geotoolkit.ogc.xml.v110.BinarySpatialOpType binary = (org.geotoolkit.ogc.xml.v110.BinarySpatialOpType) ops;
            final JAXBElement<? extends AbstractGeometryType> geom = binary.getAbstractGeometry();
            final JAXBElement<EnvelopeType> env = binary.getEnvelope();
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pnt = binary.getPropertyName().getValue();

            final Expression left = visitPropertyName(pnt);
            final Expression right;
            if(env != null && env.getValue() != null){
                try {
                    right = visitEnv(env);
                } catch (FactoryException ex) {
                    throw new IllegalArgumentException("SRS name is unknowned : " + ex.getLocalizedMessage(),ex);
                }
            }else{
                right = visit(geom);
            }

            if (OGCJAXBStatics.FILTER_SPATIAL_CONTAINS.equalsIgnoreCase(OpName)) {
                return filterFactory.contains(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_CROSSES.equalsIgnoreCase(OpName)) {
                return filterFactory.crosses(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_DISJOINT.equalsIgnoreCase(OpName)) {
                return filterFactory.disjoint(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_EQUALS.equalsIgnoreCase(OpName)) {
                return filterFactory.equals(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_INTERSECTS.equalsIgnoreCase(OpName)) {
                return filterFactory.intersects(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_OVERLAPS.equalsIgnoreCase(OpName)) {
                return filterFactory.overlaps(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_TOUCHES.equalsIgnoreCase(OpName)) {
                return filterFactory.touches(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_WITHIN.equalsIgnoreCase(OpName)) {
                return filterFactory.within(left,right);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.DistanceBufferType) {
            final org.geotoolkit.ogc.xml.v110.DistanceBufferType dstOp = (org.geotoolkit.ogc.xml.v110.DistanceBufferType) ops;
            final org.geotoolkit.ogc.xml.v110.DistanceType dt = dstOp.getDistanceType();
            final JAXBElement<? extends AbstractGeometryType> geom = dstOp.getAbstractGeometry();
            final org.geotoolkit.ogc.xml.v110.PropertyNameType pnt = dstOp.getPropertyName();

            final Expression geom1 = visitPropertyName(pnt);
            final Expression geom2 = visit(geom);
            //TODO marche pas ? ou est la distance ? Double.valueOf(dt.getContent());
            final double distance = 0;
            final String units = dt.getUnits();

            if (OGCJAXBStatics.FILTER_SPATIAL_DWITHIN.equalsIgnoreCase(OpName)) {
                return filterFactory.dwithin(geom1, geom2, distance, units);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_BEYOND.equalsIgnoreCase(OpName)) {
                return filterFactory.beyond(geom1, geom2, distance, units);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.BBOXType) {
            final org.geotoolkit.ogc.xml.v110.BBOXType binary = (org.geotoolkit.ogc.xml.v110.BBOXType) ops;
            final EnvelopeType box = binary.getEnvelope();
            final String pnt = binary.getPropertyName();

            final Expression geom;
            if (pnt != null) {
                geom = visitPropertyName(pnt);
            } else {
                geom = null;
            }
            final double minx = box.getLowerCorner().getOrdinate(0);
            final double maxx = box.getUpperCorner().getOrdinate(0);
            final double miny = box.getLowerCorner().getOrdinate(1);
            final double maxy = box.getUpperCorner().getOrdinate(1);

//            final double minx = box.getPos().get(0).getOrdinate(0);
//            final double maxx = box.getPos().get(0).getOrdinate(1);
//            final double miny = box.getPos().get(1).getOrdinate(0);
//            final double maxy = box.getPos().get(1).getOrdinate(1);
            final String srs =  box.getSrsName();

            if (OGCJAXBStatics.FILTER_SPATIAL_BBOX.equalsIgnoreCase(OpName)) {
                return filterFactory.bbox(geom, minx, miny, maxx, maxy, srs);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);
        }
        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }

    /**
     * Transform a SLD logic Filter v1.1 in GT filter.
     */
    public Filter visitLogicOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.LogicOpsType> jax)
            throws NoSuchAuthorityCodeException, FactoryException {
        final org.geotoolkit.ogc.xml.v110.LogicOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.UnaryLogicOpType) {
            final org.geotoolkit.ogc.xml.v110.UnaryLogicOpType unary = (org.geotoolkit.ogc.xml.v110.UnaryLogicOpType) ops;

            if (OGCJAXBStatics.FILTER_LOGIC_NOT.equalsIgnoreCase(OpName)) {
                Filter filter = null;

                if(unary.getComparisonOps() != null) {filter = visitComparisonOp(unary.getComparisonOps());}
                else if(unary.getLogicOps() != null) {filter = visitLogicOp(unary.getLogicOps());}
                else if(unary.getSpatialOps() != null) {filter = visitSpatialOp(unary.getSpatialOps());}

                if(filter == null){
                    throw new IllegalArgumentException("Invalide filter element" + unary);
                }
                return filterFactory.not(filter);
            }
        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.BinaryLogicOpType) {
            final org.geotoolkit.ogc.xml.v110.BinaryLogicOpType binary = (org.geotoolkit.ogc.xml.v110.BinaryLogicOpType) ops;

            if (OGCJAXBStatics.FILTER_LOGIC_AND.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();

                for (final JAXBElement<? extends ComparisonOpsType> ele : binary.getComparisonOps()) {
                    if (ele.getValue() instanceof ComparisonOpsType) {
                        filters.add(visitComparisonOp(ele));
                    }
                }
                for (final JAXBElement<? extends LogicOpsType> ele : binary.getLogicOps()) {
                    if (ele.getValue() instanceof LogicOpsType) {
                        filters.add(visitLogicOp(ele));
                    }
                }
                for (final JAXBElement<? extends SpatialOpsType> ele : binary.getSpatialOps()) {
                    if (ele.getValue() instanceof SpatialOpsType) {
                        filters.add(visitSpatialOp(ele));
                    }
                }
                if(filters.isEmpty()){
                    return Filter.include();
                }else if(filters.size() == 1){
                    return filters.get(0);
                }else{
                    return filterFactory.and((List) filters);
                }
            } else if (OGCJAXBStatics.FILTER_LOGIC_OR.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();

                for (final JAXBElement<? extends ComparisonOpsType> ele : binary.getComparisonOps()) {
                    if (ele.getValue() instanceof ComparisonOpsType) {
                        filters.add(visitComparisonOp(ele));
                    }
                }
                for (final JAXBElement<? extends LogicOpsType> ele : binary.getLogicOps()) {
                    if (ele.getValue() instanceof LogicOpsType) {
                        filters.add(visitLogicOp(ele));
                    }
                }
                for (final JAXBElement<? extends SpatialOpsType> ele : binary.getSpatialOps()) {
                    if (ele.getValue() instanceof SpatialOpsType) {
                        filters.add(visitSpatialOp(ele));
                    }
                }
                if(filters.isEmpty()){
                    return Filter.include();
                }else if(filters.size() == 1){
                    return filters.get(0);
                }else{
                    return filterFactory.or((List) filters);
                }
            }
        }
        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }

    /**
     * Transform a SLD comparison Filter v1.1 in GT filter.
     */
    public Filter visitComparisonOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.ComparisonOpsType> jax){
        final org.geotoolkit.ogc.xml.v110.ComparisonOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType) {
            final org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType binary = (org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType) ops;

            final Expression left = visitExpression(binary.getExpression().get(0));
            final Expression right = visitExpression(binary.getExpression().get(1));
            Boolean match = binary.getMatchCase();
            if(match == null) {match = Boolean.TRUE;}
            final MatchAction action = binary.getMatchAction();

            if (OGCJAXBStatics.FILTER_COMPARISON_ISEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.equal(left,right,match, action);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISNOTEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.notEqual(left, right, match, action);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESS.equalsIgnoreCase(OpName)) {
                return filterFactory.less(left, right, match, action);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATER.equalsIgnoreCase(OpName)) {
                return filterFactory.greater(left, right, match, action);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESSOREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.lessOrEqual(left, right, match, action);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATEROREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.greaterOrEqual(left, right, match, action);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsLikeType) {
            final org.geotoolkit.ogc.xml.v110.PropertyIsLikeType property = (org.geotoolkit.ogc.xml.v110.PropertyIsLikeType) ops;

            final Expression expr = visitPropertyName(property.getPropertyName());
            final String pattern = (String) visitExpression(property.getLiteralType()).getValue();
            final char wild = property.getWildCard();
            final char single = property.getSingleChar();
            final char escape = property.getEscapeChar();

            if (OGCJAXBStatics.FILTER_COMPARISON_ISLIKE.equalsIgnoreCase(OpName)) {
                return filterFactory.like(expr, pattern, wild, single, escape, true);
            }

            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType) {
            final org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType property = (org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType) ops;

            final Expression lower = visitExpression( property.getLowerBoundary().getExpression() );
            final Expression upper = visitExpression( property.getUpperBoundary().getExpression() );
            final Expression expr = visitExpression( property.getExpressionType() );

            if (OGCJAXBStatics.FILTER_COMPARISON_ISBETWEEN.equalsIgnoreCase(OpName)) {
                return filterFactory.between(expr, lower, upper);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v110.PropertyIsNullType) {
            final org.geotoolkit.ogc.xml.v110.PropertyIsNullType property = (org.geotoolkit.ogc.xml.v110.PropertyIsNullType) ops;

            final Expression expr = visitPropertyName(property.getPropertyName());

            if (OGCJAXBStatics.FILTER_COMPARISON_ISNULL.equalsIgnoreCase(OpName)) {
                return filterFactory.isNull(expr);
            }
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);
        }
        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }

    /**
     * Transform a SLD IDS Filter v1.1 in GT filter.
     */
    public Filter visitIds(final List<JAXBElement<? extends AbstractIdType>> lst){
        final Set<Filter<Object>> ids = new HashSet<>();
        for(final JAXBElement<? extends org.geotoolkit.ogc.xml.v110.AbstractIdType> id : lst){
            final AbstractIdType idd = id.getValue();
            if(idd instanceof FeatureIdType){
                ids.add( filterFactory.resourceId(( (FeatureIdType)idd).getFid()) );
            }else if(idd instanceof GmlObjectIdType){
                ids.add( filterFactory.resourceId(( (GmlObjectIdType)idd).getID()) );
            }
        }
        switch (ids.size()) {
            case 0:  return Filter.exclude();
            case 1:  return ids.iterator().next();
            default: return filterFactory.or(ids);
        }
    }

    public List<SortProperty> visitSortBy(final SortByType type){
        final List<SortProperty> sorts = new ArrayList<>();
        for(final SortPropertyType spt : type.getSortProperty()){
            final ValueReference pn = visitPropertyName(spt.getValueReference());
            sorts.add(filterFactory.sort(pn, spt.getSortOrder()));
        }
        return sorts;
    }

    public Expression visit(final JAXBElement<? extends AbstractGeometryType> ele)
            throws NoSuchAuthorityCodeException, FactoryException{
        return filterFactory.literal(GeometrytoJTS.toJTS(ele.getValue()));
    }

    public Expression visitEnv(final JAXBElement<EnvelopeType> env) throws FactoryException{
        final EnvelopeType entry = env.getValue();
        String srs = entry.getSrsName();
        DirectPositionType lower = entry.getLowerCorner();
        DirectPositionType upper = entry.getUpperCorner();

        GeneralEnvelope genv = new GeneralEnvelope(CRS.forCode(srs));
        genv.setRange(0, lower.getOrdinate(0), upper.getOrdinate(0));
        genv.setRange(1, lower.getOrdinate(1), upper.getOrdinate(1));

        return filterFactory.literal(genv);
    }

    public ValueReference visitPropertyName(final PropertyNameType pnt){
        if (pnt != null) {
            return visitPropertyName(pnt.getContent());
        }
        return null;
    }
    public ValueReference visitPropertyName(final String pnt){
        String brutPname = pnt;
        if (brutPname.indexOf(':') == -1) {
            return filterFactory.property(brutPname);
        }
        String[] pnames = brutPname.split("/");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String pname : pnames) {
            if (pnames.length > 1 && i != 0) {
                sb.append("/");
            }
            int pos = pname.indexOf(':');
            if (pos != -1 && namespaceMapping != null) {
                String prefix = pname.substring(0, pos);
                String namespace = namespaceMapping.get(prefix);
                if (namespace == null) {
                    throw new IllegalArgumentException("Prefix " + prefix + " is nor bounded.");
                } else {
                    sb.append('{').append(namespace).append('}').append(pname.substring(pos +1));
                }
            } else {
                sb.append(pname);
            }
            i++;
        }
        return filterFactory.property(sb.toString());
    }

    /**
     * Transform a JaxBelement in Expression.
     */
    public Expression visitExpression(final JAXBElement<?> jax){
//        JAXBElementFunctionType>  ---NS
//        JAXBElementExpressionType> ---k
//        JAXBElementLiteralType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementPropertyNameType>  ---k
//        JAXBElementBinaryOperatorType> ---k

        final String expName = jax.getName().getLocalPart();
        final Object obj = jax.getValue();

        if(obj instanceof LiteralType){
            return visitExpression( (LiteralType)obj );
        }else if(obj instanceof BinaryOperatorType){
            final BinaryOperatorType bot = (BinaryOperatorType) obj;
            final Expression left = visitExpression(bot.getExpression().get(0));
            final Expression right = visitExpression(bot.getExpression().get(1));

            if(OGCJAXBStatics.EXPRESSION_ADD.equalsIgnoreCase(expName)){
                return filterFactory.add(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_DIV.equalsIgnoreCase(expName)){
                return filterFactory.divide(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_MUL.equalsIgnoreCase(expName)){
                return filterFactory.multiply(left, right);
            }else if(OGCJAXBStatics.EXPRESSION_SUB.equalsIgnoreCase(expName)){
                return filterFactory.subtract(left, right);
            }
            throw new IllegalArgumentException("Unknowed expression element : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);

        }else if(obj instanceof PropertyNameType){
            return visitPropertyName((PropertyNameType) obj);
        }else if(obj instanceof FunctionType){
            final FunctionType ft = (FunctionType) obj;
            final Expression[] exps = new Expression[ft.getExpression().size()];

            int i=0;
            for(final JAXBElement<?> ele : ft.getExpression()){
                exps[i] = visitExpression(ele);
                i++;
            }
            return filterFactory.function(ft.getName(), exps);
        }
        throw new IllegalArgumentException("Unknowed expression element" + jax);
    }

    /**
     * Transform a literalType in Expression.
     */
    public Literal visitExpression(final LiteralType type){
        final List<Object> content = type.getContent();
        for(Object obj : content){
            if(obj != null){
                final String str = obj.toString().trim();
                if(!str.isEmpty()){
                    if(str.charAt(0) == '#'){
                        //try to convert it to a color
                        try {
                            Color c = ObjectConverters.convert(str, Color.class);
                            if(c!=null) obj = c;
                        } catch (UnconvertibleObjectException e) {
                            // TODO - do we really want to ignore?
                            try {
                                obj = Color.decode(str);
                            } catch (Exception bis) {
                                bis.addSuppressed(e);
                                Logging.recoverableException(null, OGC110toGTTransformer.class, "visitExpression", e);
                            }
                        }
                    }else{
                        //try to convert it to a number
                        try {
                            obj = Integer.valueOf(str);
                        } catch (NumberFormatException ex) {
                            try {
                                obj = Double.valueOf(str);
                            } catch (NumberFormatException ex2) {
                            }
                        }
                    }
                    return filterFactory.literal(obj);
                }
            }
        }
        return filterFactory.literal("");
    }

    /**
     * Change a QName in Name.
     */
    public GenericName visitQName(final QName qname){
        if(qname == null) {return null;}
        return NamesExt.create(qname);
    }
}
