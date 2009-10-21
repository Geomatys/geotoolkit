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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import org.geotoolkit.ogc.xml.v100.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v100.FunctionType;
import org.geotoolkit.ogc.xml.v100.LiteralType;
import org.geotoolkit.ogc.xml.v100.PropertyNameType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.Identifier;

/**
 * Transform OGC v1.0 jaxb xml in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OGC100toGTTransformer {

    protected final FilterFactory2 filterFactory;

    public OGC100toGTTransformer(FilterFactory2 factory){
        this.filterFactory = factory;
    }

    /**
     * Transform a SLD filter v1.0 in GT filter.
     */
    public Filter visitFilter(org.geotoolkit.ogc.xml.v100.FilterType ft){
        if(ft == null)return null;        
        
        if(ft.getComparisonOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.ComparisonOpsType> jax = ft.getComparisonOps();
            return visitComparisonOp(jax);     
        }else if(ft.getLogicOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.LogicOpsType> jax = ft.getLogicOps();
            return visitLogicOp(jax);
        }else if(ft.getSpatialOps() != null){
            final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.SpatialOpsType> jax = ft.getSpatialOps();
            return visitSpatialOp(jax);
        }else if(ft.getFeatureId() != null && !ft.getFeatureId().isEmpty()){
            return visitIds(ft.getFeatureId());
        }else{
            //this case should not happen but if so, we consider it's an ALL features filter
            return Filter.INCLUDE;
        }
        
    }
    
    /**
     * Transform a SLD spatial Filter v1.0 in GT filter.
     */
    public Filter visitSpatialOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.SpatialOpsType> jax) {
        final org.geotoolkit.ogc.xml.v100.SpatialOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v100.BinarySpatialOpType) {
            final org.geotoolkit.ogc.xml.v100.BinarySpatialOpType binary = (org.geotoolkit.ogc.xml.v100.BinarySpatialOpType) ops;
            final JAXBElement<? extends org.geotoolkit.gml.xml.v212.AbstractGeometryType> geom = binary.getGeometry();
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pnt = binary.getPropertyName();
                        
            final Expression left = filterFactory.property(pnt.getContent());
            final Expression right = visit(geom);

            if (OGCJAXBStatics.FILTER_SPATIAL_CONTAINS.equalsIgnoreCase(OpName)) {
                return filterFactory.contains(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_CROSSES.equalsIgnoreCase(OpName)) {
                return filterFactory.crosses(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_DISJOINT.equalsIgnoreCase(OpName)) {
                return filterFactory.disjoint(left,right);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_EQUALS.equalsIgnoreCase(OpName)) {
                return filterFactory.equal(left,right);
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
            
        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.DistanceBufferType) {
            final org.geotoolkit.ogc.xml.v100.DistanceBufferType dstOp = (org.geotoolkit.ogc.xml.v100.DistanceBufferType) ops;
            final org.geotoolkit.ogc.xml.v100.DistanceType dt = dstOp.getDistance();
            final JAXBElement<? extends org.geotoolkit.gml.xml.v212.AbstractGeometryType> geom = dstOp.getGeometry();
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pnt = dstOp.getPropertyName();

            final Expression geom1 = filterFactory.property(pnt.getContent());
            final Expression geom2 = visit(geom);
            final double distance = Double.valueOf(dt.getContent());
            final String units = dt.getUnits();
            
            
            if (OGCJAXBStatics.FILTER_SPATIAL_DWITHIN.equalsIgnoreCase(OpName)) {
                return filterFactory.dwithin(geom1, geom2, distance, units);
            } else if (OGCJAXBStatics.FILTER_SPATIAL_BEYOND.equalsIgnoreCase(OpName)) {
                return filterFactory.beyond(geom1, geom2, distance, units);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.BBOXType) {
            final org.geotoolkit.ogc.xml.v100.BBOXType binary = (org.geotoolkit.ogc.xml.v100.BBOXType) ops;
            final org.geotoolkit.gml.xml.v212.BoxType box = binary.getBox();
            final org.geotoolkit.ogc.xml.v100.PropertyNameType pnt = binary.getPropertyName();
            
            final Expression geom = filterFactory.property(pnt.getContent());
            final double minx = box.getCoord().get(0).getX().doubleValue();
            final double maxx = box.getCoord().get(1).getX().doubleValue();
            final double miny = box.getCoord().get(0).getY().doubleValue();
            final double maxy = box.getCoord().get(1).getY().doubleValue();
            final String srs =  box.getSrsName();
            
            if (OGCJAXBStatics.FILTER_SPATIAL_BBOX.equalsIgnoreCase(OpName)) {
                return filterFactory.bbox(geom, minx, miny, maxx, maxy, srs);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        }

        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD logic Filter v1.0 in GT filter.
     */
    public Filter visitLogicOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.LogicOpsType> jax) {
        final org.geotoolkit.ogc.xml.v100.LogicOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v100.UnaryLogicOpType) {
            final org.geotoolkit.ogc.xml.v100.UnaryLogicOpType unary = (org.geotoolkit.ogc.xml.v100.UnaryLogicOpType) ops;

            if (OGCJAXBStatics.FILTER_LOGIC_NOT.equalsIgnoreCase(OpName)) {
                Filter filter = null;
                
                if(unary.getComparisonOps() != null) filter = visitComparisonOp(unary.getComparisonOps());
                else if(unary.getLogicOps() != null) filter = visitLogicOp(unary.getLogicOps());
                else if(unary.getSpatialOps() != null) filter = visitSpatialOp(unary.getSpatialOps());
                
                if(filter == null){
                    throw new IllegalArgumentException("Invalide filter element" + unary);
                }
                
                return filterFactory.not(filter);
            }

        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.BinaryLogicOpType) {
            final org.geotoolkit.ogc.xml.v100.BinaryLogicOpType binary = (org.geotoolkit.ogc.xml.v100.BinaryLogicOpType) ops;
            
            if (OGCJAXBStatics.FILTER_LOGIC_AND.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();
                
                for(final JAXBElement<?> ele : binary.getComparisonOpsOrSpatialOpsOrLogicOps()){
                    final Object obj = ele.getValue();
                    if(obj instanceof org.geotoolkit.ogc.xml.v100.ComparisonOpsType){
                        filters.add(visitComparisonOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.ComparisonOpsType>) ele ));
                    }else if(obj instanceof org.geotoolkit.ogc.xml.v100.SpatialOpsType){
                        filters.add(visitSpatialOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.SpatialOpsType>) ele ));
                    }else if(obj instanceof org.geotoolkit.ogc.xml.v100.LogicOpsType){
                        filters.add(visitLogicOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.LogicOpsType>) ele ));
                    }
                }
                
                return filterFactory.and(filters);
            } else if (OGCJAXBStatics.FILTER_LOGIC_OR.equalsIgnoreCase(OpName)) {
                final List<Filter> filters = new ArrayList<Filter>();
                
                for(final JAXBElement<?> ele : binary.getComparisonOpsOrSpatialOpsOrLogicOps()){
                    final Object obj = ele.getValue();
                    if(obj instanceof org.geotoolkit.ogc.xml.v100.ComparisonOpsType){
                        filters.add(visitComparisonOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.ComparisonOpsType>) ele ));
                    }else if(obj instanceof org.geotoolkit.ogc.xml.v100.SpatialOpsType){
                        filters.add(visitSpatialOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.SpatialOpsType>) ele ));
                    }else if(obj instanceof org.geotoolkit.ogc.xml.v100.LogicOpsType){
                        filters.add(visitLogicOp( (JAXBElement<? extends org.geotoolkit.ogc.xml.v100.LogicOpsType>) ele ));
                    }
                }
                
                return filterFactory.or(filters);
            }

        }

        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD comparison Filter v1.0 in GT filter.
     */
    public Filter visitComparisonOp(final JAXBElement<? extends org.geotoolkit.ogc.xml.v100.ComparisonOpsType> jax){
        final org.geotoolkit.ogc.xml.v100.ComparisonOpsType ops = jax.getValue();
        final String OpName = jax.getName().getLocalPart();

        if (ops instanceof org.geotoolkit.ogc.xml.v100.BinaryComparisonOpType) {
            final org.geotoolkit.ogc.xml.v100.BinaryComparisonOpType binary = (org.geotoolkit.ogc.xml.v100.BinaryComparisonOpType) ops;

            final Expression left = visitExpression(binary.getExpression().get(0));
            final Expression right = visitExpression(binary.getExpression().get(1));
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.equals(left,right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISNOTEQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.notEqual(left, right, false);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESS.equalsIgnoreCase(OpName)) {
                return filterFactory.less(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATER.equalsIgnoreCase(OpName)) {
                return filterFactory.greater(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISLESSOREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.lessOrEqual(left, right);
            } else if (OGCJAXBStatics.FILTER_COMPARISON_ISGREATEROREQUAL.equalsIgnoreCase(OpName)) {
                return filterFactory.greaterOrEqual(left, right);
            }

            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.PropertyIsLikeType) {
            final org.geotoolkit.ogc.xml.v100.PropertyIsLikeType property = (org.geotoolkit.ogc.xml.v100.PropertyIsLikeType) ops;

            final Expression expr = filterFactory.property(property.getPropertyName().getContent());
            final String pattern = visitExpression(property.getLiteral()).toString();
            final String wild = property.getWildCard();
            final String single = property.getSingleChar();
            final String escape = property.getEscape();
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISLIKE.equalsIgnoreCase(OpName)) {
                return filterFactory.like(expr, pattern, wild, single, escape);
            }

            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.PropertyIsBetweenType) {
            final org.geotoolkit.ogc.xml.v100.PropertyIsBetweenType property = (org.geotoolkit.ogc.xml.v100.PropertyIsBetweenType) ops;

            final Expression lower = visitExpression( property.getLowerBoundary().getExpression() );
            final Expression upper = visitExpression( property.getUpperBoundary().getExpression() );
            final Expression expr = visitExpression( property.getExpression() );
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISBETWEEN.equalsIgnoreCase(OpName)) {
                return filterFactory.between(expr, lower, upper);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        } else if (ops instanceof org.geotoolkit.ogc.xml.v100.PropertyIsNullType) {
            final org.geotoolkit.ogc.xml.v100.PropertyIsNullType property = (org.geotoolkit.ogc.xml.v100.PropertyIsNullType) ops;

            Expression expr = null;
            if(property.getPropertyName() != null){
                expr = filterFactory.property(property.getPropertyName().getContent());
            }else if(property.getLiteral() != null){
                expr = visitExpression(property.getLiteral());
            }
            
            if (OGCJAXBStatics.FILTER_COMPARISON_ISNULL.equalsIgnoreCase(OpName)) {
                return filterFactory.isNull(expr);
            }
            
            throw new IllegalArgumentException("Illegal filter element" + OpName + " : " + ops);

        }
        
        throw new IllegalArgumentException("Unknowed filter element" + jax);
    }
    
    /**
     * Transform a SLD IDS Filter v1.0 in GT filter.
     */
    public Filter visitIds(List<org.geotoolkit.ogc.xml.v100.FeatureIdType> lst){
        final Set<Identifier> ids = new HashSet<Identifier>();
        
        for(final org.geotoolkit.ogc.xml.v100.FeatureIdType id : lst){
            ids.add( filterFactory.gmlObjectId(id.getFid()) );
        }
        
        return filterFactory.id(ids);
    }
    
    public Expression visit(JAXBElement<? extends org.geotoolkit.gml.xml.v212.AbstractGeometryType> ele){
        throw new UnsupportedOperationException("not supported yet, need GML");
    }
    
    /**
     * Transform a JaxBelement in Expression.
     */
    public Expression visitExpression(JAXBElement<?> jax){
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
            final PropertyNameType pnt = (PropertyNameType) obj;
            return filterFactory.property(pnt.getContent());
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
        
        throw new IllegalArgumentException("Unknowed expression element : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
    }
    
    /**
     * Transform a literalType in Expression.
     */
    public Expression visitExpression(LiteralType type){
        final List<Object> content = type.getContent();

        for(final Object obj : content){
            if(obj != null && !obj.toString().trim().isEmpty()){
                return filterFactory.literal(obj);
            }
        }
        return filterFactory.literal("");
    }

}
