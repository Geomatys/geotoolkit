/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter.binaryspatial;

import org.geotoolkit.feature.Feature;
import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;

import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.logging.Logging;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.coverage.Coverage;
import org.apache.sis.util.Utilities;

/**
 * Immutable abstract binary spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 * @module pending
 */
public abstract class AbstractBinarySpatialOperator<E extends Expression,F extends Expression>
                                                implements BinarySpatialOperator,Serializable {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter.binaryspatial");
    protected static final CoordinateReferenceSystem MERCATOR;

    static{
        try {
            MERCATOR = CRS.forCode("EPSG:3395");
        } catch (FactoryException ex) {
            throw new RuntimeException("Could not load EPSG:3395 mercator projection.",ex);
        }
    }


    protected final E left;
    protected final F right;

    protected AbstractBinarySpatialOperator(final E left, final F right){
        ensureNonNull("left", left);
        ensureNonNull("right", right);
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E getExpression1() {
        return left;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F getExpression2() {
        return right;
    }

    protected static Unit toUnit(final String str){
        return Units.valueOf(str);
    }

    protected static Geometry toGeometry(final Object object, Expression exp){
        Object value;
        if ((exp instanceof PropertyName) && object instanceof Feature && ((PropertyName)exp).getPropertyName().isEmpty()) {
            value = findFirstGeometry((Feature)object);
        } else {
            value = exp.evaluate(object);
        }

        Geometry candidate;
        if(value instanceof Coverage){
            //use the coverage envelope
            final Coverage coverage = (Coverage) value;
            candidate = JTS.toGeometry(coverage.getEnvelope());
        }else if(value instanceof org.opengis.geometry.Geometry){
            final org.opengis.geometry.Geometry geo = (org.opengis.geometry.Geometry) value;
            if(geo instanceof AbstractJTSGeometry) {
                candidate = ((AbstractJTSGeometry)geo).getJTSGeometry();
            }else{
                candidate = null;
            }
        }else{
            try{
                candidate = ObjectConverters.convert(value, Geometry.class);
            }catch(UnconvertibleObjectException ex){
                LOGGER.log(Level.INFO, "Could not convert expression : "+exp+" to geometry for object : "+object+"\n"+ex.getMessage(), ex);
                candidate = null;
            }
        }

        return candidate;
    }

    /**
     * Reproject geometries to the same CRS if needed and if possible.
     */
    protected static Geometry[] toSameCRS(final Geometry leftGeom, final Geometry rightGeom)
            throws NoSuchAuthorityCodeException, FactoryException, TransformException{

        final CoordinateReferenceSystem leftCRS = JTS.findCoordinateReferenceSystem(leftGeom);
        final CoordinateReferenceSystem rightCRS = JTS.findCoordinateReferenceSystem(rightGeom);

        if(leftCRS == null || rightCRS == null){
            //one or bother geometries doesn't have a defined SRID, we assume that both
            //are in the same CRS
            return new Geometry[]{leftGeom, rightGeom};
        } else if (Utilities.equalsIgnoreMetadata(leftCRS, rightCRS)) {
            //both are in the same CRS, nothing to reproject
            return new Geometry[]{leftGeom, rightGeom};
        }

        //we choose to reproject the right operand.
        //there is no special reason to make this choice but we must make one.
        //perhaps there could be a way to determine a the best crs ?
        final MathTransform trs = CRS.findOperation(rightCRS, leftCRS, null).getMathTransform();

        return new Geometry[]{leftGeom, JTS.transform(rightGeom, trs)};
    }

    /**
     * Reproject one or both geometries to the same crs, the matching crs
     * will be compatible with the requested unit.
     * return Array[leftGeometry, rightGeometry, matchingCRS];
     */
    protected static Object[] toSameCRS(final Geometry leftGeom, final Geometry rightGeom, final Unit unit)
            throws NoSuchAuthorityCodeException, FactoryException, TransformException{

        final CoordinateReferenceSystem leftCRS = JTS.findCoordinateReferenceSystem(leftGeom);
        final CoordinateReferenceSystem rightCRS = JTS.findCoordinateReferenceSystem(rightGeom);

        if(leftCRS == null && rightCRS == null){
            //bother geometries doesn't have a defined SRID, we assume that both
            //are in the same CRS
            return new Object[]{leftGeom, rightGeom, null};
        } else if (leftCRS == null || rightCRS == null || Utilities.equalsIgnoreMetadata(leftCRS, rightCRS)) {
            //both are in the same CRS

            final CoordinateReferenceSystem geomCRS = (leftCRS == null) ? rightCRS : leftCRS;

            if(geomCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                //the geometries crs is compatible with the requested unit, nothing to reproject
                return new Object[]{leftGeom,rightGeom,geomCRS};
            }else{
                //the crs unit is not compatible, we must reproject both geometries to a more appropriate crs
                if(Units.METRE.isCompatible(unit)){
                    //in that case we reproject to mercator EPSG:3395
                    final MathTransform trs = CRS.findOperation(geomCRS, MERCATOR, null).getMathTransform();

                    return new Object[]{
                        JTS.transform(leftGeom,trs),
                        JTS.transform(rightGeom,trs),
                        MERCATOR};

                }else{
                    //we can not find a matching projection in this case
                    throw new TransformException("Could not find a matching CRS for both geometries for unit :" + unit);
                }
            }

        }else{
            //both have different CRS, try to find the most appropriate crs amoung both

            final CoordinateReferenceSystem matchingCRS;
            final Geometry leftMatch;
            final Geometry rightMatch;

            if(leftCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                matchingCRS = leftCRS;
                final MathTransform trs = CRS.findOperation(rightCRS, matchingCRS, null).getMathTransform();
                rightMatch = JTS.transform(rightGeom, trs);
                leftMatch = leftGeom;
            }else if(rightCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                matchingCRS = rightCRS;
                final MathTransform trs = CRS.findOperation(leftCRS, matchingCRS, null).getMathTransform();
                leftMatch = JTS.transform(leftGeom, trs);
                rightMatch = rightGeom;
            }else{
                //the crs unit is not compatible, we must reproject both geometries to a more appropriate crs
                if(Units.METRE.isCompatible(unit)){
                    //in that case we reproject to mercator EPSG:3395
                    matchingCRS = MERCATOR;

                    MathTransform trs = CRS.findOperation(leftCRS, matchingCRS, null).getMathTransform();
                    leftMatch = JTS.transform(leftGeom, trs);
                    trs = CRS.findOperation(rightCRS, matchingCRS, null).getMathTransform();
                    rightMatch = JTS.transform(rightGeom, trs);

                }else{
                    //we can not find a matching projection in this case
                    throw new TransformException("Could not find a matching CRS for both geometries for unit :" + unit);
                }
            }

            return new Object[]{leftMatch,rightMatch,matchingCRS};
        }

    }

    private static Object findFirstGeometry(ComplexAttribute ca){
        //search for a default geometry
        if(ca instanceof Feature){
            final Feature feature = (Feature) ca;
            final GeometryDescriptor geomDesc = feature.getType().getGeometryDescriptor();
            if(geomDesc!=null){
                final GeometryAttribute geomAtt = feature.getDefaultGeometryProperty();
                return (geomAtt==null) ? null : geomAtt.getValue();
            }
        }

        //search normal properties
        for(Property p : ca.getProperties()){
            if(p instanceof GeometryAttribute){
                Object val = p.getValue();
                if(val!=null) return val;
            }
        }

        //search complex properties
        for(Property p : ca.getProperties()){
            if(p instanceof ComplexAttribute){
                Object val = findFirstGeometry((ComplexAttribute) p);
                if(val!=null) return val;
            }
        }

        return null;

    }

}
