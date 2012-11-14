/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Complementary utility methods for CRS manipulation.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class ReferencingUtilities {
    
    private ReferencingUtilities(){}
    
    /**
     * Transform the given envelope to the given crs.
     * Unlike CRS.transform this method handle growing number of dimensions by filling
     * other axes with default values.
     * 
     * @param env source Envelope
     * @param targetCRS target CoordinateReferenceSystem
     * @return transformed envelope
     */
    public static Envelope transform(Envelope env, CoordinateReferenceSystem targetCRS) throws TransformException{
        try {
            return CRS.transform(env, targetCRS);
        } catch (TransformException ex) {
            //we tryed...
        }
        
        //lazy transform
        final CoordinateReferenceSystem sourceCRS = env.getCoordinateReferenceSystem();
        final GeneralEnvelope result = new GeneralEnvelope(targetCRS);
        
        //decompose crs
        final List<CoordinateReferenceSystem> sourceParts;
        final List<CoordinateReferenceSystem> targetParts;
        if(sourceCRS instanceof CompoundCRS){
            sourceParts = ((CompoundCRS)sourceCRS).getComponents();
        }else{
            sourceParts = UnmodifiableArrayList.wrap(sourceCRS);
        }
        if(targetCRS instanceof CompoundCRS){
            targetParts = ((CompoundCRS)targetCRS).getComponents();
        }else{
            targetParts = UnmodifiableArrayList.wrap(targetCRS);
        }
        
        int sourceAxeIndex=0;
        loop:
        for(CoordinateReferenceSystem sourcePart : sourceParts){
            final int sourcePartDimension = sourcePart.getCoordinateSystem().getDimension();
            int targetAxeIndex=0;
            for(CoordinateReferenceSystem targetPart : targetParts){
                final int targetPartDimension = targetPart.getCoordinateSystem().getDimension();
                
                //try conversion
                try {
                    final MathTransform trs = CRS.findMathTransform(sourcePart, targetPart, true);
                    //we could transform by using two coordinate, but envelope conversion allows to handle
                    //crs singularities more efficiently
                    final GeneralEnvelope partSource = new GeneralEnvelope(sourcePart);
                    for(int i=0;i<sourcePartDimension;i++){
                        partSource.setRange(i, env.getMinimum(sourceAxeIndex+i), env.getMaximum(sourceAxeIndex+i));
                    }
                    final Envelope partResult = CRS.transform(trs, partSource);
                    for(int i=0;i<targetPartDimension;i++){
                        result.setRange(targetAxeIndex+i, partResult.getMinimum(i), partResult.getMaximum(i));
                    }
                    continue loop;
                } catch (FactoryException ex) {
                    //we tryed...
                }
                
                targetAxeIndex += targetPartDimension;
            }
            sourceAxeIndex += sourcePartDimension;
        }
        
        
        
        return result;
    }
    
    /**
     * Make a new envelope with vertical and temporal dimensions.
     */
    public static GeneralEnvelope combine(final Envelope bounds, final Date[] temporal, final Double[] elevation) throws TransformException{
        CoordinateReferenceSystem crs = bounds.getCoordinateReferenceSystem();
        Rectangle2D rect = new Rectangle2D.Double(
                bounds.getMinimum(0),
                bounds.getMinimum(1),
                bounds.getSpan(0),
                bounds.getSpan(1));
        return combine(crs, rect, temporal, elevation);
    }

    /**
     * Make a new envelope with vertical and temporal dimensions.
     */
    public static GeneralEnvelope combine(CoordinateReferenceSystem crs, final Rectangle2D bounds,
            final Date[] temporal, final Double[] elevation) throws TransformException{
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);
        TemporalCRS temporalDim = null;
        VerticalCRS verticalDim = null;

        if(temporal != null && (temporal[0] != null || temporal[1] != null)){
            temporalDim = CRS.getTemporalCRS(crs);

            if(temporalDim == null){
                temporalDim = DefaultTemporalCRS.JAVA;
            }
        }

        if(elevation != null && (elevation[0] != null || elevation[1] != null)){
            verticalDim = CRS.getVerticalCRS(crs);

            if(verticalDim == null){
                verticalDim = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
            }
        }

        final GeneralEnvelope env;
        if(temporalDim != null && verticalDim != null){
            crs = new DefaultCompoundCRS(crs2D.getName().getCode()+"/"+verticalDim.getName().getCode()+"/"+temporalDim.getName().getCode(),
                    crs2D, verticalDim, temporalDim);
            env = new GeneralEnvelope(crs);
            env.setRange(0, bounds.getMinX(), bounds.getMaxX());
            env.setRange(1, bounds.getMinY(), bounds.getMaxY());
            env.setRange(2,
                    (elevation[0] != null) ? elevation[0] : Double.NEGATIVE_INFINITY,
                    (elevation[1] != null) ? elevation[1] : Double.POSITIVE_INFINITY);
            env.setRange(3,
                    (temporal[0] != null) ? temporal[0].getTime() : Double.NEGATIVE_INFINITY,
                    (temporal[1] != null) ? temporal[1].getTime() : Double.POSITIVE_INFINITY);
        }else if(temporalDim != null){
            crs = new DefaultCompoundCRS(crs2D.getName().getCode()+"/"+temporalDim.getName().getCode(),
                    crs2D,  temporalDim);
            env = new GeneralEnvelope(crs);
            env.setRange(0, bounds.getMinX(), bounds.getMaxX());
            env.setRange(1, bounds.getMinY(), bounds.getMaxY());
            env.setRange(2,
                    (temporal[0] != null) ? temporal[0].getTime() : Double.NEGATIVE_INFINITY,
                    (temporal[1] != null) ? temporal[1].getTime() : Double.POSITIVE_INFINITY);
        }else if(verticalDim != null){
            crs = new DefaultCompoundCRS(crs2D.getName().getCode()+"/"+verticalDim.getName().getCode(),
                    crs2D, verticalDim);
            env = new GeneralEnvelope(crs);
            env.setRange(0, bounds.getMinX(), bounds.getMaxX());
            env.setRange(1, bounds.getMinY(), bounds.getMaxY());
            env.setRange(2,
                    (elevation[0] != null) ? elevation[0] : Double.NEGATIVE_INFINITY,
                    (elevation[1] != null) ? elevation[1] : Double.POSITIVE_INFINITY);
        }else{
            crs = crs2D;
            env = new GeneralEnvelope(crs);
            env.setRange(0, bounds.getMinX(), bounds.getMaxX());
            env.setRange(1, bounds.getMinY(), bounds.getMaxY());
        }

        return env;
    }

    /**
     * Change the 2D CRS part of the CRS.
     * 
     * @param originalCRS : base CRS, possible multi-dimension
     * @param crs2D : replacement 2D crs
     * @return CoordinateReferenceSystem
     * @throws TransformException 
     */
    public static CoordinateReferenceSystem change2DComponent( final CoordinateReferenceSystem originalCRS,
            final CoordinateReferenceSystem crs2D) throws TransformException {
        if(crs2D.getCoordinateSystem().getDimension() != 2){
            throw new IllegalArgumentException("Expected a 2D CRS");
        }

        final CoordinateReferenceSystem targetCRS;

        if(originalCRS instanceof CompoundCRS){
            final CompoundCRS ccrs = (CompoundCRS) originalCRS;
            final CoordinateReferenceSystem part2D = CRSUtilities.getCRS2D(originalCRS);
            final List<CoordinateReferenceSystem> lst = new ArrayList<CoordinateReferenceSystem>();
            final StringBuilder sb = new StringBuilder();
            for(CoordinateReferenceSystem c : ccrs.getComponents()){
                if(c.equals(part2D)){
                    //replace the 2D part
                    lst.add(crs2D);
                    sb.append(crs2D.getName().toString()).append(' ');
                }else{
                    //preserve other axis
                    lst.add(c);
                    sb.append(c.getName().toString()).append(' ');
                }
            }
            targetCRS = new DefaultCompoundCRS(sb.toString(), lst.toArray(new CoordinateReferenceSystem[lst.size()]));

        }else if(originalCRS.getCoordinateSystem().getDimension() == 2){
            //no other axis, just reproject normally
            targetCRS = crs2D;
        }else{
            throw new UnsupportedOperationException("How do we change the 2D component of a CRS if it's not a CompoundCRS ?");
        }

        return targetCRS;
    }

    /**
     * Transform the CRS 2D component of this envelope.
     * This preserve temporal/elevation or other axis.
     */
    public static Envelope transform2DCRS(final Envelope env, final CoordinateReferenceSystem crs2D) throws TransformException{
        final CoordinateReferenceSystem originalCRS = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem targetCRS = change2DComponent(originalCRS, crs2D);
        return CRS.transform(env, targetCRS);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     * Reproject the envelope.
     */
    public static Envelope setLongitudeFirst(final Envelope env) throws TransformException, FactoryException{
        if(env == null) return env;

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem flipped = setLongitudeFirst(crs);
        return CRS.transform(env, flipped);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     */
    public static CoordinateReferenceSystem setLongitudeFirst(final CoordinateReferenceSystem crs) throws FactoryException{
        if(crs instanceof SingleCRS){
            final SingleCRS singlecrs = (SingleCRS) crs;
            final CoordinateSystem cs = singlecrs.getCoordinateSystem();
            final int dimension = cs.getDimension();

            if(dimension <=1){
                //can't change anything if it's only one axis
                return crs;
            }


            //find the east axis
            int eastAxis = -1;
            for(int i=0; i<dimension; i++){
                final AxisDirection firstAxis = cs.getAxis(i).getDirection();
                if(firstAxis == AxisDirection.EAST || firstAxis == AxisDirection.WEST){
                    eastAxis = i;
                    break;
                }
            }

            if(eastAxis == 0){
                //the crs is already in the correct order or does not have any east axis
                return singlecrs;
            }

            //try to change the crs axis
            final String id = IdentifiedObjects.lookupIdentifier(singlecrs, true);
            if(id != null){
                return CRS.decode(id, true);
            }else{
                //TODO how to manage custom crs ? might be a derivedCRS.
                throw new FactoryException("Failed to create flipped axis for crs : " + singlecrs);
            }

        }else if(crs instanceof CompoundCRS){
            final CompoundCRS compoundcrs = (CompoundCRS) crs;

            final List<CoordinateReferenceSystem> components = compoundcrs.getComponents();
            final int size = components.size();
            final CoordinateReferenceSystem[] parts = new CoordinateReferenceSystem[size];

            //only recreate the crs if one element changed.
            boolean changed = false;
            for(int i=0; i<size; i++){
                final CoordinateReferenceSystem orig = components.get(i);
                parts[i] = setLongitudeFirst(orig);
                if(!parts[i].equals(orig)) changed = true;
            }

            if(changed){
                return new DefaultCompoundCRS(compoundcrs.getName().getCode(), parts);
            }else{
                return crs;
            }
        }
        return crs;
    }

    /**
     * Create an affine transform object where (0,0) in the dimension
     * match the top left corner of the envelope.
     * This method assume that the Y axis of the rectangle is going down.
     * This return the display to objective transform (rect to env).
     */
    public static AffineTransform toAffine(final Dimension rect, final Envelope env){
        final double minx = env.getMinimum(0);
        final double maxy = env.getMaximum(1);
        final double scaleX = env.getSpan(0)/rect.width;
        final double scaleY = - env.getSpan(1)/rect.height;
        return new AffineTransform(scaleX, 0, 0, scaleY, minx, maxy);
    }
    
}
