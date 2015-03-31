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
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;

import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.operation.projection.Mercator;
import org.geotoolkit.referencing.operation.transform.LinearInterpolator1D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.apache.sis.referencing.CRS.getHorizontalComponent;
import static org.apache.sis.referencing.CRS.getVerticalComponent;
import static org.apache.sis.referencing.CRS.getTemporalComponent;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.util.FileUtilities;
import org.opengis.geometry.MismatchedDimensionException;


/**
 * Complementary utility methods for CRS manipulation.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReferencingUtilities {

    private ReferencingUtilities(){}

    /**
     * Test if the Coordinate Reference System has a wrap around axis.
     * This method checks if the CRS is geographic or projected with a compatible
     * transformation (only linear or Mercator).
     *
     * Example, for EPSG:4326 the result is :
     * [0] = Position(0,-180)
     * [1] = Position(0,+180)
     *
     * Example, for EPSG:3395 (mercator) the result is :
     * [0] = Position(-2M,0)
     * [1] = Position(+2M,0)
     *
     * Example, for EPSG:27582 (conic) the result is :
     * null since there is no possible wrap around.
     *
     * @param crs to test
     * @return DirectPosition[] size 2 :
     *     [0] : start wrap around position in given CRS.
     *     [1] : end wrap around position in given CRS.
     *    null if crs does not have a wrap around axis
     *
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static DirectPosition[] findWrapAround(CoordinateReferenceSystem crs) throws TransformException{
        if(crs instanceof GeographicCRS){
            final EllipsoidalCS cs = ((GeographicCRS)crs).getCoordinateSystem();
            for(int i=0,n=cs.getDimension();i<n;i++){
                final CoordinateSystemAxis axis = cs.getAxis(i);
                if(RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())){
                    final DirectPosition start = new GeneralDirectPosition(crs);
                    start.setOrdinate(i, axis.getMinimumValue());
                    final DirectPosition end = new DirectPosition2D(crs);
                    end.setOrdinate(i, axis.getMaximumValue());
                    return new DirectPosition[]{start,end};
                }
            }
        }else if(crs instanceof ProjectedCRS){
            final ProjectedCRS projectedCRS = (ProjectedCRS) crs;
            final MathTransform trs = projectedCRS.getConversionFromBase().getMathTransform();

            //test if the transform can contain a wrap axis
            if(isWrapAroundCompatible(trs)){
                final CoordinateReferenceSystem baseCrs = projectedCRS.getBaseCRS();
                final DirectPosition[] info = findWrapAround(baseCrs);
                info[0] = trs.transform(info[0], null);
                info[1] = trs.transform(info[1], null);
                return info;
            }
        }

        return null;
    }

    private static boolean isWrapAroundCompatible(MathTransform trs) {
        for (final MathTransform step : MathTransforms.getSteps(trs)) {
            if (!(trs instanceof LinearTransform || trs instanceof Mercator)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Convert resolution from old resolution and {@linkplain Envelope#getCoordinateReferenceSystem() source CRS} 
     * within srcEnvelope, and store result into destination array newResolution.<br><br>
     * 
     * <strong>
     * Note 1 : newResolution array may be {@code null}, in this case a new array result will be created.
     * Where its length will be equals to targetCRS dimension number.<br>
     * Note 2 : the resolution convertion will be compute from 
     * {@linkplain CRSUtilities#getCRS2D(org.opengis.referencing.crs.CoordinateReferenceSystem) 2D CRS horizontal part}
     * of source CRS from {@link Envelope} and 2D targetCRS horizontal part.<br>
     * Note 3 : if destination resolution array is not {@code null} the resolution values about 
     * other dimension than 2D horizontal CRS part are unchanged, else (if new resolution array is {@code null}) 
     * the resolution values on other dimensions are setted to {@code 1}.
     * </strong>
     * 
     * @param srcEnvelope source envelope in relation with the source resolution.
     * @param oldResolution the old resolution which will be convert.
     * @param targetCrs destination {@link CoordinateReferenceSystem} where the new resolution will be exprimate.
     * @param newResolution the result array of the transformed resolution. 
     * You may pass the same array than oldResolution if you want to store result in the same array.
     * 
     * @return a new resolution array compute from oldResolution exprimate into targetCRS. 
     * @throws org.opengis.referencing.operation.TransformException if problem during Envelope transformation into targetCrs.
     * @throws NullArgumentException if one of these parameter is {@code null} : srcEnvelope, oldResolution or targetCRS.
     * @throws IllegalArgumentException if oldResolution and newResolution array haven't got same length.
     * @throws IllegalArgumentException if Resolution array length and source CRS dimension are different.
     */
    public static double[] convertResolution(final Envelope srcEnvelope, final double[] oldResolution, 
                                             final CoordinateReferenceSystem targetCrs, double... newResolution) throws TransformException {
        ArgumentChecks.ensureNonNull("srcEnvelope",   srcEnvelope);
        ArgumentChecks.ensureNonNull("oldResolution", oldResolution);
        ArgumentChecks.ensureNonNull("targetCRS",     targetCrs);
        
        //-- initialize destination array if it is null.
        if (newResolution == null || newResolution.length == 0) {
            newResolution = new double[targetCrs.getCoordinateSystem().getDimension()];
            Arrays.fill(newResolution, 1);
        } else {
            if (targetCrs.getCoordinateSystem().getDimension() != newResolution.length) 
            throw new IllegalArgumentException("Destination resolution array lenght should be equals than target CRS dimension number."
                    + "Destination resolution array length = "+newResolution.length+", CRS dimension number = "+targetCrs.getCoordinateSystem().getDimension());
        }
        
        final CoordinateReferenceSystem srcCRS = srcEnvelope.getCoordinateReferenceSystem();
        
        if (srcCRS.getCoordinateSystem().getDimension() != oldResolution.length) 
            throw new IllegalArgumentException("Resolution array lenght should be equals than source CRS dimension number."
                    + "Resolution array length = "+oldResolution.length+", CRS dimension number = "+srcCRS.getCoordinateSystem().getDimension());
        
        if (CRS.equalsIgnoreMetadata(srcCRS, targetCrs)) {
            System.arraycopy(oldResolution, 0, newResolution, 0, newResolution.length);
        } else {
            final int srcMinOrdi = CRSUtilities.firstHorizontalAxis(srcCRS);
            
            //-- grid envelope
            final int displayWidth  = (int) StrictMath.ceil(srcEnvelope.getSpan(srcMinOrdi)     / oldResolution[srcMinOrdi]);
            final int displayHeight = (int) StrictMath.ceil(srcEnvelope.getSpan(srcMinOrdi + 1) / oldResolution[srcMinOrdi + 1]);
            
            //-- resolution working is only available on 2D horizontal CRS part
            //-- also avoid mismatch dimension problem
            final CoordinateReferenceSystem srcCRS2D    = CRSUtilities.getCRS2D(srcCRS);
            final CoordinateReferenceSystem targetCRS2D = CRSUtilities.getCRS2D(targetCrs);
            
            final GeneralEnvelope srcEnvelope2D = new GeneralEnvelope(srcCRS2D);
            srcEnvelope2D.setRange(0, srcEnvelope.getMinimum(srcMinOrdi),     srcEnvelope.getMaximum(srcMinOrdi));
            srcEnvelope2D.setRange(1, srcEnvelope.getMinimum(srcMinOrdi + 1), srcEnvelope.getMaximum(srcMinOrdi + 1));
            
            //-- target image into target CRS 2D
            final Envelope targetEnvelope2D = Envelopes.transform(srcEnvelope2D, targetCRS2D);
            
            final int targetMinOrdi = CRSUtilities.firstHorizontalAxis(targetCrs);
            
            newResolution[targetMinOrdi]     = targetEnvelope2D.getSpan(0) / displayWidth;
            newResolution[targetMinOrdi + 1] = targetEnvelope2D.getSpan(1) / displayHeight;
        }
        return newResolution;
    }

    public static Envelope wrapNormalize(Envelope env, DirectPosition[] warp) {
        if (warp == null) return env;
        //TODO we assume the warp is on a along an axis of the coordinate system.
        final DirectPosition p0 = warp[0];
        final DirectPosition p1 = warp[1];
        for (int i = 0, n = p0.getDimension(); i < n; i++) {
            final double minimum = p0.getOrdinate(i);
            final double maximum = p1.getOrdinate(i);
            if(minimum == maximum){
                //not the wrap axis
                continue;
            }

            final double csSpan = maximum-minimum;
            double o1 = env.getMinimum(i);
            double o2 = env.getMaximum(i);

            final GeneralEnvelope res = new GeneralEnvelope(env);

            if (Math.abs(o2-o1) >= csSpan) {
                /*
                 * If the range exceed the CS span, then we have to replace it by the
                 * full span, otherwise the range computed by the "else" block is too
                 * small. The full range will typically be [-180 … 180]°.  However we
                 * make a special case if the two bounds are multiple of the CS span,
                 * typically [0 … 360]°. In this case the [0 … -0]° range matches the
                 * original values and is understood by GeneralEnvelope as a range
                 * spanning all the world.
                 */
                if (o1 != minimum || o2 != maximum) {
                    if ((o1 % csSpan) == 0 && (o2 % csSpan) == 0) {
                        res.setRange(i, +0.0, -0.0);
                    } else {
                        res.setRange(i, minimum, maximum);
                    }
                }
            } else {
                if(o1<minimum || o2>maximum){
                    //envelope cross the anti-méridian
                    res.setRange(i, minimum, maximum);
                }

//                o1 = Math.floor((o1 - minimum) / csSpan) * csSpan;
//                o2 = Math.floor((o2 - minimum) / csSpan) * csSpan;
//                if (o1 != 0){
//                    res.setRange(i, res.getMinimum(i)-o1, res.getMaximum(i));
//                }
//                if (o2 != 0){
//                    res.setRange(i, res.getMinimum(i), res.getMaximum(i)-o2);
//                }
            }

            return res;
        }

        return env;
    }

    /**
     * Transform the given envelope to the given crs.
     * Unlike CRS.transform this method handle growing number of dimensions by filling
     * other axes with default values.
     *
     * @param env source Envelope
     * @param targetCRS target CoordinateReferenceSystem
     * @return transformed envelope
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static Envelope transform(Envelope env, CoordinateReferenceSystem targetCRS) throws TransformException{
        try {
            return CRS.transform(env, targetCRS);
        } catch (TransformException ex) {
            //we tried...
        }

        //lazy transform
        final CoordinateReferenceSystem sourceCRS = env.getCoordinateReferenceSystem();
        final GeneralEnvelope result = new GeneralEnvelope(targetCRS);

        //decompose crs
        final List<CoordinateReferenceSystem> sourceParts = ReferencingUtilities.decompose(sourceCRS);
        final List<CoordinateReferenceSystem> targetParts = ReferencingUtilities.decompose(targetCRS);

        int sourceAxeIndex=0;
        sourceLoop:
        for(CoordinateReferenceSystem sourcePart : sourceParts){
            final int sourcePartDimension = sourcePart.getCoordinateSystem().getDimension();
            int targetAxeIndex=0;

            targetLoop:
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
                    break targetLoop;
                } catch (FactoryException ex) {
                    //we tried...
                }

                targetAxeIndex += targetPartDimension;
            }
            sourceAxeIndex += sourcePartDimension;
        }

        return result;
    }

    /**
     * Return array of dimensions indexes with
     * [0, 1] : geographic
     * [2] : elevation
     * [3] : temporal
     * @param crs
     * @return
     */
    private static int[] findDimensionIndexes(CoordinateReferenceSystem crs) {

        final CoordinateSystem cs = crs.getCoordinateSystem();
        assert (cs.getDimension() <= 4);
        int[] indexes = new int[4];

        int d =0;
        for(CoordinateReferenceSystem crs2 : ReferencingUtilities.decompose(crs)) {

            final CoordinateSystem cs2 = crs2.getCoordinateSystem();
            if (CRS.isHorizontalCRS(crs2)) {
                assert cs2.getDimension() == 2;
                indexes[0] = d;
                indexes[1] = d+1;
            } else {
                assert cs2.getDimension() == 1;
                final AxisDirection direction = cs2.getAxis(0).getDirection();
                final Unit unit = cs2.getAxis(0).getUnit();

                //Elevation
                if (direction == AxisDirection.UP || direction == AxisDirection.DOWN && (unit != null && unit.isCompatible(SI.METRE))) {
                    assert crs2 instanceof VerticalCRS;
                    indexes[2] = d;
                }

                //temporal
                if (direction == AxisDirection.FUTURE || direction == AxisDirection.PAST) {
                    assert crs2 instanceof TemporalCRS;
                    indexes[3] = d;
                }
            }
            d += cs2.getDimension();
        }

        return indexes;
    }

    /**
     * Make a new envelope with vertical and temporal dimensions.
     * @param bounds
     * @param temporal
     * @param elevation
     * @return
     * @throws org.opengis.referencing.operation.TransformException
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
     * @param crs
     * @param bounds
     * @param temporal
     * @param elevation
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static GeneralEnvelope combine(CoordinateReferenceSystem crs, final Rectangle2D bounds,
            final Date[] temporal, final Double[] elevation) throws TransformException{
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);
        TemporalCRS temporalDim = null;
        VerticalCRS verticalDim = null;

        if(temporal != null && (temporal[0] != null || temporal[1] != null)){
            temporalDim = getTemporalComponent(crs);

            if(temporalDim == null){
                temporalDim = CommonCRS.Temporal.JAVA.crs();
            }
        }

        if(elevation != null && (elevation[0] != null || elevation[1] != null)){
            verticalDim = getVerticalComponent(crs, true);

            if(verticalDim == null){
                verticalDim = CommonCRS.Vertical.ELLIPSOIDAL.crs();
            }
        }

        final GeneralEnvelope env;
        if(temporalDim != null && verticalDim != null){
            crs = new DefaultCompoundCRS(name(crs2D.getName().getCode() + "/" + verticalDim.getName().getCode() + "/" + temporalDim.getName().getCode()),
                    crs2D, verticalDim, temporalDim);
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = CommonCRS.Temporal.JAVA.crs();
                final MathTransform trs = CRS.findMathTransform(realTemporal, temporalDim);
                final double[] coords = new double[2];
                coords[0] = (temporal[0] != null) ? temporal[0].getTime() : Double.NEGATIVE_INFINITY;
                coords[1] = (temporal[1] != null) ? temporal[1].getTime() : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[3],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }
            try {
                final CoordinateReferenceSystem realElevation = CommonCRS.Vertical.ELLIPSOIDAL.crs();
                final MathTransform trs = CRS.findMathTransform(realElevation, verticalDim);
                final double[] coords = new double[2];
                coords[0] = (elevation[0] != null) ? elevation[0] : Double.NEGATIVE_INFINITY;
                coords[1] = (elevation[1] != null) ? elevation[1] : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[2],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }
        }else if(temporalDim != null){
            crs = new DefaultCompoundCRS(name(crs2D.getName().getCode() + "/" + temporalDim.getName().getCode()),
                    crs2D,  temporalDim);
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = CommonCRS.Temporal.JAVA.crs();
                final MathTransform trs = CRS.findMathTransform(realTemporal, temporalDim);
                final double[] coords = new double[2];
                coords[0] = (temporal[0] != null) ? temporal[0].getTime() : Double.NEGATIVE_INFINITY;
                coords[1] = (temporal[1] != null) ? temporal[1].getTime() : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[3],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }


        }else if(verticalDim != null){
            crs = new DefaultCompoundCRS(name(crs2D.getName().getCode() + "/" + verticalDim.getName().getCode()),
                    crs2D, verticalDim);
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realElevation = CommonCRS.Vertical.ELLIPSOIDAL.crs();
                final MathTransform trs = CRS.findMathTransform(realElevation, verticalDim);
                final double[] coords = new double[2];
                coords[0] = (elevation[0] != null) ? elevation[0] : Double.NEGATIVE_INFINITY;
                coords[1] = (elevation[1] != null) ? elevation[1] : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[2],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }

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
            targetCRS = new DefaultCompoundCRS(name(sb.toString()), lst.toArray(new CoordinateReferenceSystem[lst.size()]));

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
     * @param env
     * @param crs2D
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static Envelope transform2DCRS(final Envelope env, final CoordinateReferenceSystem crs2D) throws TransformException{
        final CoordinateReferenceSystem originalCRS = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem targetCRS = change2DComponent(originalCRS, crs2D);
        return CRS.transform(env, targetCRS);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     * Reproject the envelope.
     * @param env
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     * @throws org.opengis.util.FactoryException
     */
    public static Envelope setLongitudeFirst(final Envelope env) throws TransformException, FactoryException{
        if(env == null) return env;

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem flipped = setLongitudeFirst(crs);
        return CRS.transform(env, flipped);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     * @param crs
     * @return
     * @throws org.opengis.util.FactoryException
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
                return new DefaultCompoundCRS(name(compoundcrs.getName().getCode()), parts);
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
     * @param rect
     * @param env
     * @return
     */
    public static AffineTransform toAffine(final Dimension rect, final Envelope env){
        final double minx = env.getMinimum(0);
        final double maxy = env.getMaximum(1);
        final double scaleX = env.getSpan(0)/rect.width;
        final double scaleY = - env.getSpan(1)/rect.height;
        return new AffineTransform(scaleX, 0, 0, scaleY, minx, maxy);
    }

    /**
     * 
     * @param base
     * @param values
     * @return
     * @deprecated replaced by {@link #toTransform(int, org.opengis.referencing.operation.MathTransform, java.util.Map, int) 
     */
    @Deprecated
    public static MathTransform toTransform(final MathTransform base, double[] ... values){

        MathTransform result = PassThroughTransform.create(0, base, values.length);
        final int baseDim = base.getSourceDimensions();
        for(int i=0; i<values.length; i++){
            final double[] array = values[i];
            final MathTransform1D axistrs;
            if(array.length == 0){
                axistrs = (MathTransform1D) MathTransforms.linear(1, 0);
            }else if(array.length == 1){
                axistrs = (MathTransform1D) MathTransforms.linear(1, array[0]);
            }else{
                axistrs = LinearInterpolator1D.create(array);
            }
            final MathTransform mask = PassThroughTransform.create(baseDim+i, axistrs, values.length-i-1);
            result = MathTransforms.concatenate(result, mask);
        }

        return result;
    }
    
    /**
     * Returns a {@link PassThroughTransform} with <strong>expectedTargetDimension</strong> number, 
     * from <strong>subtransform</strong> at dimension index <strong>firstBaseOrdinate</strong>
     * and with <strong>axisValues</strong> infomation on each other dimensions.
     * 
     * @param firstBaseOrdinate the first minimum ordinate of subtransform into the expected target dimension.
     * @param subTransform the sub transformation which will be wrapped by other mathematical functions.
     * @param axisValues the list of mathmatical function for each other dimensions than already present subtransform dimensions.
     * @param expectedTargetDimension the expected target {@link PassThroughTransform} dimension.
     * @return expected {@link PassThroughTransform}, created from given parameters.
     * @see #checkMTToTransform(int, org.opengis.referencing.operation.MathTransform, java.util.Map, int) 
     */
    public static MathTransform toTransform(final int firstBaseOrdinate, final MathTransform subTransform, 
                                            final Map<Integer, double[]> axisValues, final int expectedTargetDimension) {
        checkMTToTransform(firstBaseOrdinate, subTransform, axisValues, expectedTargetDimension);
        
        MathTransform result = PassThroughTransform.create(firstBaseOrdinate, subTransform, expectedTargetDimension - subTransform.getTargetDimensions() - firstBaseOrdinate);
        for (Integer dim : axisValues.keySet()) {
            final double[] currentAxisValues = axisValues.get(dim);
            final MathTransform1D axistrs;
            if(currentAxisValues.length <= 1) {
                axistrs = (MathTransform1D) MathTransforms.linear(1, (currentAxisValues.length == 0) 
                                                                     ? 0 : currentAxisValues[0]);
            } else {
                axistrs = LinearInterpolator1D.create(currentAxisValues);
            }
            final MathTransform mask = PassThroughTransform.create(dim, axistrs, expectedTargetDimension - dim - 1);
            result                   = MathTransforms.concatenate(result, mask);
        }
        return result;
    }

    /**
     * Check than all needed parameters to build appropriate {@link PassThroughTransform} are conform. 
     * 
     * @param firstBaseOrdinate the first minimum ordinate of subtransform into the expected target dimension.
     * @param subTransform the sub transformation which will be wrapped by other mathematical functions.
     * @param axisValues the list of mathmatical function for each other dimensions than already present subtransform dimensions.
     * @param expectedTargetDimension the expected target {@link PassThroughTransform} dimension.
     * @return {@code true} if all needed dimension are informed else {@code false}.
     * @throws NullArgumentException if subtransform or axisValues are {@code null}.
     * @throws MismatchedDimensionException if expected targetDimension is lesser than subtransform dimension.
     * @throws IllegalArgumentException if firstBaseOrdinate is out of target dimension boundary [0 ---> targetDim - subtransform target dim] 
     */
    private static void checkMTToTransform(final int firstBaseOrdinate,             final MathTransform subTransform, 
                                           final Map<Integer, double[]> axisValues, final int expectedTargetDimension) {
        ArgumentChecks.ensureNonNull("subTransform", subTransform);
        ArgumentChecks.ensureNonNull("axisValues", axisValues);
        final int subTransformDimensionNumber = subTransform.getTargetDimensions();
        if (expectedTargetDimension < subTransformDimensionNumber)
            throw new MismatchedDimensionException("expectedTargetDimension should be "
                    + "upper than subtransform target dimension. Expected upper than : "
                    +subTransformDimensionNumber+" found : "+expectedTargetDimension);
        ArgumentChecks.ensureBetween("firstBaseOrdinate", 0, expectedTargetDimension - subTransformDimensionNumber, firstBaseOrdinate);
        if (expectedTargetDimension > 64) 
            throw new IllegalArgumentException("targetDimension > 64 not supported");
        //-- create a long where the bits ordinate are at 1 in relation with subtransform ordinate. 
        long isOrdinateChecked = ((1 << (subTransformDimensionNumber)) -1) << (expectedTargetDimension - firstBaseOrdinate - subTransformDimensionNumber);
        for (final Integer dim : axisValues.keySet()) {
            isOrdinateChecked = isOrdinateChecked | (1 << (expectedTargetDimension - dim - 1));
        }
        if (isOrdinateChecked != ((1 << expectedTargetDimension) - 1)) {
            final StringBuilder strB = new StringBuilder("The following dimension must be informed : ");
            for (int d = 0; d < expectedTargetDimension; d++) {
                long currentDim = 1 << d;
                if ((isOrdinateChecked & currentDim) != currentDim){
                    strB.append(d);
                    if (d != (expectedTargetDimension - 1)) strB.append(",");
                }
            }
            throw new IllegalArgumentException(strB.toString());
        }
    }
    
    /**
     * Recursively explor given crs, and return a list of distinct unary CRS.
     * @param crs
     * @return List<CoordinateReferenceSystem>
     * @deprecated moved to {@link org.geotoolkit.internal.referencing.CRSUtilities#decompose(org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static List<CoordinateReferenceSystem> decompose(CoordinateReferenceSystem crs){
        final List<CoordinateReferenceSystem> lst = new ArrayList<CoordinateReferenceSystem>();
        decompose(crs, lst);
        return lst;
    }

    private static void decompose(final CoordinateReferenceSystem crs,
            final List<CoordinateReferenceSystem> lst){
        if(crs instanceof CompoundCRS){
            final List<CoordinateReferenceSystem> parts = ((CompoundCRS)crs).getComponents();
            for(CoordinateReferenceSystem part : parts){
                decompose(part, lst);
            }
        }else{
            lst.add(crs);
        }
    }

    /**
     * Decompose CRS and return each sub-crs along with their dimension index.
     * @param crs
     * @return Map of index and sub-crs
     */
    public static Map<Integer, CoordinateReferenceSystem> indexedDecompose(CoordinateReferenceSystem crs) {
        final TreeMap<Integer, CoordinateReferenceSystem> result = new TreeMap<>();
        int index = 0;
        CoordinateReferenceSystem crsPart;
        CoordinateSystem csPart;
        final List<CoordinateReferenceSystem> crsParts = ReferencingUtilities.decompose(crs);
        for (int j = 0; j < crsParts.size(); j++) {
            crsPart = crsParts.get(j);
            csPart = crsPart.getCoordinateSystem();
            result.put(index, crsPart);
            index += csPart.getDimension();
        }
        return result;
    }

    /**
     * For each axis value of the source envelope, we'll set corresponding one
     * in destination envelope, if we find a valid transform. For all the components
     * of the destination that can't be filled, they're left as is.
     * @param source The envelope to take values from.
     * @param destination The envelope to set values into. Will be modified.
     * @return The destination envelope that have been modified.
     */
    public static GeneralEnvelope transposeEnvelope(final GeneralEnvelope source, GeneralEnvelope destination) {
        ArgumentChecks.ensureNonNull("source envelope", source);
        ArgumentChecks.ensureNonNull("destination envelope", destination);

        final CoordinateReferenceSystem sourceCRS = source.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem destCRS = destination.getCoordinateReferenceSystem();

        // If they're the same, we can return the source envelope.
        if (CRS.equalsApproximatively(sourceCRS, destCRS)) {
            destination = new GeneralEnvelope(source);
        }

        // Decompose source and destination CRSs
        final List<CoordinateReferenceSystem> sourceComponents = decompose(sourceCRS);
        final List<CoordinateReferenceSystem> destComponents = decompose(destCRS);
        // Store sub-CRSs of destination which have already been used for range transfer.
        final List<CoordinateReferenceSystem> usedCRS = new ArrayList<CoordinateReferenceSystem>(destComponents.size());

        boolean searchHorizontal;
        boolean searchTemporal;
        boolean searchVertical;
        boolean compatible;
        int srcLowerAxis = 0;
        int srcAxisCount = 0;
        int destLowerAxis;
        int destAxisCount;
        browseSource:
        for (int srcCptCounter = 0; srcCptCounter < sourceComponents.size(); srcCptCounter++) {
            // Prepare iteration.
            searchHorizontal = false;
            searchVertical = false;
            searchTemporal = false;
            srcLowerAxis += srcAxisCount;
            destLowerAxis = 0;
            destAxisCount = 0;

            final CoordinateReferenceSystem srcCurrent = sourceComponents.get(srcCptCounter);
            srcAxisCount = srcCurrent.getCoordinateSystem().getDimension();

             // First, we must browse destination to check if we can find an equal CRS.
            for (int destCptCounter = 0; destCptCounter < destComponents.size(); destCptCounter++) {
                destLowerAxis += destAxisCount;
                final CoordinateReferenceSystem destCurrent = destComponents.get(destCptCounter);
                destAxisCount = destCurrent.getCoordinateSystem().getDimension();

                // We already bind a dimension of the source envelope for this CRS, just keep going.
                if (usedCRS.contains(destCurrent)) {
                    continue;
                }

                if (CRS.equalsApproximatively(srcCurrent, destCurrent)) {
                    final GeneralEnvelope srcSubEnvelope = source.subEnvelope(srcLowerAxis, srcLowerAxis + srcAxisCount);
                    destination.subEnvelope(destLowerAxis, destLowerAxis+ srcSubEnvelope.getDimension()).setEnvelope(srcSubEnvelope);
                    usedCRS.add(destCurrent);
                    continue browseSource;
                }
            }
            destLowerAxis = 0;
            destAxisCount = 0;

            /*
             * If equality failed, we'll try to get CRS whose meaning is
             * compatible. To do so, we try to identify the CRS type (horizontal,
             * vertical or temporal). If it's a fail, our last chance is to
             * compare both CRS axis.
             */
            if (getHorizontalComponent(srcCurrent) != null) {
                searchHorizontal = true;
            } else if (getVerticalComponent(srcCurrent, true) != null) {
                searchVertical = true;
            } else if (getTemporalComponent(srcCurrent) != null) {
                searchTemporal = true;
            }

            for (int destCptCounter = 0; destCptCounter < destComponents.size(); destCptCounter++) {
                compatible = false;
                destLowerAxis += destAxisCount;

                final CoordinateReferenceSystem destCurrent = destComponents.get(destCptCounter);
                destAxisCount = destCurrent.getCoordinateSystem().getDimension();

                // We already bind a dimension of the source envelope for this CRS, just keep going.
                if (usedCRS.contains(destCurrent)) {
                    continue;
                }

                if (searchHorizontal) {
                    if (getHorizontalComponent(destCurrent) != null) {
                        compatible = true;
                    }
                } else if (searchVertical) {
                    if (getVerticalComponent(destCurrent, true) != null) {
                        compatible = true;
                    }
                } else if (searchTemporal) {
                    if (getTemporalComponent(destCurrent) != null) {
                        compatible = true;
                    }
                } else {
                    // Check both CRS axis.
                    final CoordinateSystem sourceCS = srcCurrent.getCoordinateSystem();
                    final CoordinateSystem destCS = destCurrent.getCoordinateSystem();

                    if (sourceCS.getDimension() == destCS.getDimension()) {
                        boolean sameAxis = true;
                        for (int axisCount = 0; axisCount < sourceCS.getDimension(); axisCount++) {
                            if (AxisDirections.indexOfColinear(destCS, sourceCS.getAxis(axisCount).getDirection()) < 0) {
                                sameAxis = false;
                                break;
                            }
                        }
                        compatible = sameAxis;
                    }
                }

                // We found matching CRS, Now we can transfer ordinates.
                if (compatible) {
                    final GeneralEnvelope srcSubEnvelope = source.subEnvelope(srcLowerAxis, srcLowerAxis + srcAxisCount);
                    srcSubEnvelope.setCoordinateReferenceSystem(srcCurrent);
                    try {
                        final Envelope tmp = CRS.transform(srcSubEnvelope, destCurrent);
                        destination.subEnvelope(destLowerAxis, destLowerAxis + tmp.getDimension()).setEnvelope(tmp);
                    } catch (TransformException e) {
                        // If we can't transform it, we just have to go to the next iteration.
                        continue;
                    }
                    usedCRS.add(destCurrent);
                    break;
                }
            }
        }

        return destination;
    }


    /**
     * Build an envelope which is the intersection between both of the parameters.
     * The CRS of the two input envelopes can be different, with different number
     * of dimension.
     * @param layerEnvelope The envelope of the source layer. Result envelope will
     * keep this object CRS.
     * @param filterEnvelope the envelope used as filter.
     * @return An envelope which is the found intersection between the two inputs.
     * The CRS of the result will be the same as the first input.
     * @throws TransformException If an incompatibility between CRSs is found.
     */
    public static GeneralEnvelope intersectEnvelopes(final Envelope layerEnvelope, final Envelope filterEnvelope)
            throws TransformException {
        ArgumentChecks.ensureNonNull("source envelope", layerEnvelope);
        GeneralEnvelope resultEnvelope = new GeneralEnvelope(layerEnvelope);

        if (filterEnvelope == null) {
            return resultEnvelope;
        }

        final CoordinateReferenceSystem inputCRS = layerEnvelope.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem filterCRS = filterEnvelope.getCoordinateReferenceSystem();

        if (resultEnvelope.getDimension() <= filterEnvelope.getDimension()) {
            resultEnvelope.intersect(CRS.transform(filterEnvelope, inputCRS));
        } else {
            /* If source CRS got more dimensions than the one given as filter,
             * we need to isolate each component of the filter to insert them
             * into an envelope of the source CRS.
             */
            if (inputCRS instanceof CompoundCRS) {
                final ArrayList<SingleCRS> toFind = new ArrayList<>(3);
                final TemporalCRS inputTemporal = getTemporalComponent(inputCRS);
                final TemporalCRS filterTemporal = getTemporalComponent(filterCRS);
                final SingleCRS inputHorizontal = getHorizontalComponent(inputCRS);
                final SingleCRS filterHorizontal = getHorizontalComponent(filterCRS);
                final VerticalCRS inputVertical = getVerticalComponent(inputCRS, true);
                final VerticalCRS filterVertical = getVerticalComponent(filterCRS, true);
                if (inputHorizontal != null && filterHorizontal != null) {
                    toFind.add(inputHorizontal);
                }
                if (inputTemporal != null && filterTemporal != null) {
                    toFind.add(inputTemporal);
                }
                if (inputVertical != null && filterVertical != null) {
                    toFind.add(inputVertical);
                }
                /* build a sub-CRS, containing all the input components which can
                 * be compared with current filter.
                 */
                final CoordinateReferenceSystem tmpCRS;
                if (toFind.size() <= 0) {
                    throw new TransformException("An error occured while applying filter : input layer CRS and filter CRS aren't compatible.");
                } else if (toFind.size() == 1) {
                    tmpCRS = toFind.get(0);
                } else {
                    tmpCRS = CRS.getCompoundCRS((CompoundCRS) inputCRS, toFind.toArray(new SingleCRS[toFind.size()]));
                }
                final GeneralEnvelope tmpFilter = new GeneralEnvelope(CRS.transform(filterEnvelope, tmpCRS));
                final GeneralEnvelope tmpResult = new GeneralEnvelope(CRS.transform(resultEnvelope, tmpCRS));
                tmpResult.intersect(tmpFilter);

                /* Re-injection pass. For each component crs of the above computed
                 * intersection, we try to find the same in input envelope, to
                 * replace its values.
                 */
                int tmpOffset = 0;
                for (CoordinateReferenceSystem tmpSubCRS : toFind) {
                    int srcOffset = 0;
                    int tmpDimNumber = tmpSubCRS.getCoordinateSystem().getDimension();

                    for (CoordinateReferenceSystem subCRS : ((CompoundCRS) inputCRS).getComponents()) {
                        if (subCRS.equals(tmpSubCRS)) {
                            final GeneralEnvelope subTmp = tmpResult.subEnvelope(tmpOffset, tmpOffset + tmpDimNumber);
                            resultEnvelope.subEnvelope(srcOffset, srcOffset+tmpResult.getDimension()).setEnvelope(subTmp);
                            break;
                        }
                        srcOffset += subCRS.getCoordinateSystem().getDimension();
                    }

                    tmpOffset += tmpDimNumber;
                }

            } else {
                throw new TransformException("An error occured while applying filter : input layer CRS and filter CRS aren't compatible.");
            }
        }
        return resultEnvelope;
    }

    /**
     * Read TFW file and return the content affine transform.
     *
     * @param f
     * @return
     * @throws IOException
     * @throws NumberFormatException
     */
    public static AffineTransform readTransform(File f) throws IOException, NumberFormatException {
        final String str = FileUtilities.getStringFromFile(f);
        final String[] parts = str.split("\n");
        final double[] vals = new double[6];
        int idx=0;
        for(String line : parts){
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;
            vals[idx] = Double.parseDouble(line);
            idx++;
        }
        return new AffineTransform(vals);
    }

    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }
}
