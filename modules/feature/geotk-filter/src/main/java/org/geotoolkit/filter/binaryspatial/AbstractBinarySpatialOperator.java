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

import com.vividsolutions.jts.geom.Geometry;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Immutable abstract binary spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 * @module pending
 */
public abstract class AbstractBinarySpatialOperator<E extends Expression,F extends Expression> implements BinarySpatialOperator {

    protected static final CoordinateReferenceSystem MERCATOR;

    static{
        try {
            MERCATOR = CRS.decode("EPSG:3395");
        } catch (FactoryException ex) {
            throw new RuntimeException("Could not load EPSG:3395 mercator projection.");
        }
    }


    protected final E left;
    protected final F right;

    protected AbstractBinarySpatialOperator(E left, F right){
        if(left == null || right == null){
            throw new NullPointerException("Left and right expressions can not be null");
        }
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

    protected static Unit toUnit(String str){
        if(str.equalsIgnoreCase("kilometers")){
            return Unit.valueOf("km");
        }else if(str.equalsIgnoreCase("meters")){
            return Unit.valueOf("m");
        }
        return Unit.valueOf(str);
    }

    /**
     * Reproject geometries to the same CRS if needed and if possible.
     */
    protected static Geometry[] toSameCRS(Geometry leftGeom, Geometry rightGeom)
            throws NoSuchAuthorityCodeException, FactoryException, TransformException{

        final int srid1 = leftGeom.getSRID();
        final int srid2 = rightGeom.getSRID();

        if(srid1 == 0 || srid2 == 0){
            //one or bother geometries doesn't have a defined SRID, we assume that both
            //are in the same CRS
            return new Geometry[]{leftGeom, rightGeom};
        }else if(srid1 == srid2){
            //both are in the same CRS, nothing to reproject
            return new Geometry[]{leftGeom, rightGeom};
        }

        final CoordinateReferenceSystem leftCRS = CRS.decode(SRIDGenerator.toSRS(srid1, Version.V1));
        final CoordinateReferenceSystem rightCRS = CRS.decode(SRIDGenerator.toSRS(srid2, Version.V1));

        //we choose to reproject the right operand.
        //there is no special reason to make this choice but we must make one.
        //perhaps there could be a way to determine a the best crs ?
        final MathTransform trs = CRS.findMathTransform(rightCRS, leftCRS);

        return new Geometry[]{leftGeom, JTS.transform(rightGeom, trs)};
    }

    /**
     * Reproject one or both geometries to the same crs, the matching crs
     * will be compatible with the requested unit.
     * return Array[leftGeometry, rightGeometry, matchingCRS];
     */
    protected static Object[] toSameCRS(Geometry leftGeom, Geometry rightGeom, Unit unit)
            throws NoSuchAuthorityCodeException, FactoryException, TransformException{

        final int srid1 = leftGeom.getSRID();
        final int srid2 = rightGeom.getSRID();

        if(srid1 == 0 && srid2 == 0){
            //bother geometries doesn't have a defined SRID, we assume that both
            //are in the same CRS
            return new Object[]{leftGeom, rightGeom, null};
        }else if(srid1 == srid2 || srid1 == 0 || srid2 == 0){
            //both are in the same CRS

            final CoordinateReferenceSystem geomCRS = CRS.decode(
                    SRIDGenerator.toSRS((srid1 == 0)? srid2 : srid1, Version.V1));

            if(geomCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                //the geometries crs is compatible with the requested unit, nothing to reproject
                return new Object[]{leftGeom,rightGeom,geomCRS};
            }else{
                //the crs unit is not compatible, we must reproject both geometries to a more appropriate crs
                if(SI.METER.isCompatible(unit)){
                    //in that case we reproject to mercator EPSG:3395
                    final MathTransform trs = CRS.findMathTransform(geomCRS, MERCATOR);

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
            final CoordinateReferenceSystem leftCRS = CRS.decode(SRIDGenerator.toSRS(srid1, Version.V1));
            final CoordinateReferenceSystem rightCRS = CRS.decode(SRIDGenerator.toSRS(srid2, Version.V1));

            final CoordinateReferenceSystem matchingCRS;
            final Geometry leftMatch;
            final Geometry rightMatch;

            if(leftCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                matchingCRS = leftCRS;
                final MathTransform trs = CRS.findMathTransform(rightCRS, matchingCRS);
                rightMatch = JTS.transform(rightGeom, trs);
                leftMatch = leftGeom;
            }else if(rightCRS.getCoordinateSystem().getAxis(0).getUnit().isCompatible(unit)){
                matchingCRS = rightCRS;
                final MathTransform trs = CRS.findMathTransform(leftCRS, matchingCRS);
                leftMatch = JTS.transform(leftGeom, trs);
                rightMatch = rightGeom;
            }else{
                //the crs unit is not compatible, we must reproject both geometries to a more appropriate crs
                if(SI.METER.isCompatible(unit)){
                    //in that case we reproject to mercator EPSG:3395
                    matchingCRS = MERCATOR;

                    MathTransform trs = CRS.findMathTransform(leftCRS, matchingCRS);
                    leftMatch = JTS.transform(leftGeom, trs);
                    trs = CRS.findMathTransform(rightCRS, matchingCRS);
                    rightMatch = JTS.transform(rightGeom, trs);

                }else{
                    //we can not find a matching projection in this case
                    throw new TransformException("Could not find a matching CRS for both geometries for unit :" + unit);
                }
            }

            return new Object[]{leftMatch,rightMatch,matchingCRS};
        }

    }

}
