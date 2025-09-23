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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Immutable abstract binary spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 */
public abstract class AbstractBinarySpatialOperator<E extends Expression,F extends Expression> implements Serializable {
    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.filter.binaryspatial");
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

    public List getExpressions() {
        return Arrays.asList(left, right);
    }

    public E getOperand1() {
        return left;
    }

    public F getOperand2() {
        return right;
    }

    public Class<Object> getResourceClass() {
        return null;        // Actually undetermined.
    }

    protected static Unit toUnit(final String str){
        return Units.valueOf(str);
    }

    protected static Geometry toGeometry(final Object object, Expression exp){
        Object value;
        if ((exp instanceof ValueReference) && object instanceof Feature && ((ValueReference) exp).getXPath().isEmpty()) {
            value = findFirstGeometry((Feature) object);
        } else {
            value = exp.apply(object);
        }
        Geometry candidate;
        if(value instanceof GridCoverage){
            //use the coverage envelope
            final GridCoverage coverage = (GridCoverage) value;
            candidate = JTS.toGeometry(coverage.getGridGeometry().getEnvelope());
        }else if(value instanceof GridCoverageResource){
            //use the coverage envelope
            final GridCoverageResource resource = (GridCoverageResource) value;
            try{
                GridGeometry gg = resource.getGridGeometry();
                candidate = JTS.toGeometry(gg.getEnvelope());
                candidate.setUserData(gg.getCoordinateReferenceSystem());
            }catch(DataStoreException ex){
                LOGGER.log(Level.INFO, "Could not convert expression : "+exp+" to geometry for object : "+object+"\n"+ex.getMessage(), ex);
                candidate = null;
            }
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
        } else if (CRS.equivalent(leftCRS, rightCRS)) {
            //both are in the same CRS, nothing to reproject
            return new Geometry[]{leftGeom, rightGeom};
        }

        //we choose to reproject the right operand.
        //there is no special reason to make this choice but we must make one.
        //perhaps there could be a way to determine a the best crs ?
        final MathTransform trs = CRS.findOperation(rightCRS, leftCRS, null).getMathTransform();

        return new Geometry[]{leftGeom, org.apache.sis.geometry.wrapper.jts.JTS.transform(rightGeom, trs)};
    }

    /**
     * Reproject one or both geometries to the same crs, the matching crs
     * will be compatible with the requested unit.
     * return Array[leftGeometry, rightGeometry, matchingCRS];
     */
    protected static Object[] toSameCRS(final Geometry leftGeom, final Geometry rightGeom, final Unit unit)
            throws NoSuchAuthorityCodeException, FactoryException, TransformException
    {
        final CoordinateReferenceSystem leftCRS = JTS.findCoordinateReferenceSystem(leftGeom);
        final CoordinateReferenceSystem rightCRS = JTS.findCoordinateReferenceSystem(rightGeom);

        if(leftCRS == null && rightCRS == null){
            //bother geometries doesn't have a defined SRID, we assume that both
            //are in the same CRS
            return new Object[]{leftGeom, rightGeom, null};
        } else if (leftCRS == null || rightCRS == null || CRS.equivalent(leftCRS, rightCRS)) {
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
                        org.apache.sis.geometry.wrapper.jts.JTS.transform(leftGeom,trs),
                        org.apache.sis.geometry.wrapper.jts.JTS.transform(rightGeom,trs),
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
                rightMatch = org.apache.sis.geometry.wrapper.jts.JTS.transform(rightGeom, trs);
                leftMatch = leftGeom;
            }else if(rightCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                matchingCRS = rightCRS;
                final MathTransform trs = CRS.findOperation(leftCRS, matchingCRS, null).getMathTransform();
                leftMatch = org.apache.sis.geometry.wrapper.jts.JTS.transform(leftGeom, trs);
                rightMatch = rightGeom;
            }else{
                //the crs unit is not compatible, we must reproject both geometries to a more appropriate crs
                if(Units.METRE.isCompatible(unit)){
                    //in that case we reproject to mercator EPSG:3395
                    matchingCRS = MERCATOR;

                    MathTransform trs = CRS.findOperation(leftCRS, matchingCRS, null).getMathTransform();
                    leftMatch = org.apache.sis.geometry.wrapper.jts.JTS.transform(leftGeom, trs);
                    trs = CRS.findOperation(rightCRS, matchingCRS, null).getMathTransform();
                    rightMatch = org.apache.sis.geometry.wrapper.jts.JTS.transform(rightGeom, trs);
                }else{
                    //we can not find a matching projection in this case
                    throw new TransformException("Could not find a matching CRS for both geometries for unit :" + unit);
                }
            }
            return new Object[]{leftMatch,rightMatch,matchingCRS};
        }
    }

    private static Object findFirstGeometry(Feature ca){
        //search for a default geometry
        try{
            return ca.getPropertyValue(AttributeConvention.GEOMETRY);
        }catch(PropertyNotFoundException ex){}

        //search normal properties
        for(PropertyType pt : ca.getType().getProperties(true)){
            if(AttributeConvention.isGeometryAttribute(pt)){
                Object val = ca.getPropertyValue(pt.getName().toString());
                if(val!=null) return val;
            }
        }

        //search complex properties
        for(PropertyType pt : ca.getType().getProperties(true)){
            if(pt instanceof FeatureAssociationRole){
                Feature f = (Feature) ca.getPropertyValue(pt.getName().toString());
                if(f!=null){
                    Object val = findFirstGeometry(f);
                    if(val!=null) return val;
                }
            }
        }
        return null;
    }
}
