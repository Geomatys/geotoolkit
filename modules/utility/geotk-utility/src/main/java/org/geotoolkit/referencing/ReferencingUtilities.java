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
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.CRS;

import static org.apache.sis.referencing.CRS.getHorizontalComponent;
import static org.apache.sis.referencing.CRS.getSingleComponents;
import static org.apache.sis.referencing.CRS.getTemporalComponent;
import static org.apache.sis.referencing.CRS.getVerticalComponent;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.Mercator;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.referencing.operation.transform.LinearInterpolator1D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.extent.GeographicBoundingBox;
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
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static java.lang.Math.*;


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
            if (!(step instanceof LinearTransform || step instanceof Mercator)) {
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
     * Note 1 : newResolution array may be {@code null}, in this case a new array result will be created,
     * where its length will be equals to targetCRS dimensions number.<br>
     * Note 2 : the resolution convertion will be compute from
     * {@linkplain CRSUtilities#getCRS2D(org.opengis.referencing.crs.CoordinateReferenceSystem) 2D CRS horizontal part}
     * of source CRS from {@link Envelope} and 2D targetCRS horizontal part.<br>
     * Note 3 : if destination resolution array is not {@code null} the resolution values about
     * other dimensions than 2D horizontal targetCRS part are unchanged, else (if new resolution array is {@code null})
     * the resolution values on other dimensions are setted to {@code 1}.<br>
     * Note 4 : oldResolution array length may not be mandatory equal to {@linkplain Envelope#getDimension() source Envelope dimension number}.
     * The resolution convertion will be computed on horizontal CRS 2D part.
     * </strong>
     *
     * @param srcEnvelope source envelope in relation with the source resolution.
     * @param oldResolution the old resolution which will be convert.
     * @param targetCrs destination {@link CoordinateReferenceSystem} where the new resolution will be exprimate.
     * @param newResolution the result array of the transformed resolution.
     * You may pass the same array than oldResolution if you want to store result in the same array.
     *
     * @return a new resolution array compute from oldResolution exprimate into targetCRS.
     * @throws TransformException if problem during Envelope transformation into targetCrs.
     * @throws NullArgumentException if one of these parameter is {@code null} : srcEnvelope, oldResolution or targetCRS.
     * @throws MismatchedDimensionException if oldResolution array have length different than 2.
     * @throws MismatchedDimensionException if newResolution array length and target CRS dimension are differents.
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
            throw new MismatchedDimensionException("Destination resolution array length should be equals than target CRS dimension number."
                    + "Destination resolution array length = "+newResolution.length+", CRS dimension number = "+targetCrs.getCoordinateSystem().getDimension());
        }

        final CoordinateReferenceSystem srcCRS = srcEnvelope.getCoordinateReferenceSystem();
        if (oldResolution.length != 2)
            throw new IllegalArgumentException("Resolution array length should be equals to 2. Founded array length : " + oldResolution.length);

        final int targetMinOrdi = CRSUtilities.firstHorizontalAxis(targetCrs);

        if (Utilities.equalsIgnoreMetadata(srcCRS, targetCrs)) {
            assert targetMinOrdi + oldResolution.length <= newResolution.length : "First horizontal index from target CRS + old resolution array length " +
                    "should be lesser than new resolution array length.";
            System.arraycopy(oldResolution, 0, newResolution, targetMinOrdi, oldResolution.length);
        } else {
            final int srcMinOrdi = CRSUtilities.firstHorizontalAxis(srcCRS);
            final CoordinateReferenceSystem srcCRS2D    = CRSUtilities.getCRS2D(srcCRS);
            final CoordinateReferenceSystem targetCRS2D = CRSUtilities.getCRS2D(targetCrs);

            final DirectPosition center = new GeneralDirectPosition(srcCRS2D);
            center.setOrdinate(0, srcEnvelope.getMedian(srcMinOrdi));
            center.setOrdinate(1, srcEnvelope.getMedian(srcMinOrdi+1));

            try {
                final MathTransform trs = CRS.findOperation(srcCRS2D, targetCRS2D, null).getMathTransform();
                final Matrix derivative = trs.derivative(center);
                double[] res2d = MatrixSIS.castOrCopy(derivative).multiply(oldResolution);
                //array sizes may differ, copy only the 2D resolution.
                newResolution[0] = res2d[0];
                newResolution[1] = res2d[1];

            } catch (FactoryException | ProjectionException ex) {
                //uncorrect fallback solution, better then nothing
                //-- grid envelope
                final int displayWidth  = (int) StrictMath.ceil(srcEnvelope.getSpan(srcMinOrdi)     / oldResolution[0]);
                final int displayHeight = (int) StrictMath.ceil(srcEnvelope.getSpan(srcMinOrdi + 1) / oldResolution[1]);

                final GeneralEnvelope srcEnvelope2D = new GeneralEnvelope(srcCRS2D);
                srcEnvelope2D.setRange(0, srcEnvelope.getMinimum(srcMinOrdi),     srcEnvelope.getMaximum(srcMinOrdi));
                srcEnvelope2D.setRange(1, srcEnvelope.getMinimum(srcMinOrdi + 1), srcEnvelope.getMaximum(srcMinOrdi + 1));

                //-- target image into target CRS 2D
                final Envelope targetEnvelope2D = Envelopes.transform(srcEnvelope2D, targetCRS2D);

                newResolution[targetMinOrdi]     = targetEnvelope2D.getSpan(0) / displayWidth;
                newResolution[targetMinOrdi + 1] = targetEnvelope2D.getSpan(1) / displayHeight;
            }

        }

        //at current step, resolution may have negative values du to derivate.
        for(int i=0; i<newResolution.length; i++) {
            newResolution[i] = abs(newResolution[i]);
            if (Double.isNaN(newResolution[i]) && (i == targetMinOrdi || i == targetMinOrdi+1)) {
                //if a value is NaN, peek resolution of the other horizontal axis
                newResolution[i] = newResolution[i == targetMinOrdi ? i+1 : i-1];
            }
            if (Double.isNaN(newResolution[i])) {
                //if a value is still NaN, use original resolution
                newResolution[i] = oldResolution[i];
            }
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

            if (abs(o2-o1) >= csSpan) {
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

//                o1 = floor((o1 - minimum) / csSpan) * csSpan;
//                o2 = floor((o2 - minimum) / csSpan) * csSpan;
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
     * Unlike Envelopes.transform this method handle growing number of dimensions by filling
     * other axes with default values.
     *
     * @param env source Envelope
     * @param targetCRS target CoordinateReferenceSystem
     * @return transformed envelope
     */
    public static Envelope transform(Envelope env, CoordinateReferenceSystem targetCRS) throws TransformException{
        try {
            return Envelopes.transform(env, targetCRS);
        } catch (TransformException ex) {
            //we tried...
        }

        //lazy transform
        final CoordinateReferenceSystem sourceCRS = env.getCoordinateReferenceSystem();
        final GeneralEnvelope result = new GeneralEnvelope(targetCRS);

        //decompose crs
        final List<SingleCRS> sourceParts = getSingleComponents(sourceCRS);
        final List<SingleCRS> targetParts = getSingleComponents(targetCRS);

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
                    final MathTransform trs = CRS.findOperation(sourcePart, targetPart, null).getMathTransform();
                    //we could transform by using two coordinate, but envelope conversion allows to handle
                    //crs singularities more efficiently
                    final GeneralEnvelope partSource = new GeneralEnvelope(sourcePart);
                    for(int i=0;i<sourcePartDimension;i++){
                        partSource.setRange(i, env.getMinimum(sourceAxeIndex+i), env.getMaximum(sourceAxeIndex+i));
                    }
                    final Envelope partResult = Envelopes.transform(trs, partSource);
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
     *
     * @deprecated dimension not found have their index left to 0, which is probably not appropriate
     *             (they will be taken as longitude or easting).
     */
    @Deprecated
    private static int[] findDimensionIndexes(CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assert (cs.getDimension() <= 4);
        int[] indexes = new int[4];
        int d = 0;
        for (SingleCRS crs2 : getSingleComponents(crs)) {
            final int dim = crs2.getCoordinateSystem().getDimension();
            if (crs2 instanceof GeographicCRS || crs2 instanceof ProjectedCRS) {
                for (int i=0; i<dim; i++) {      // May be 2 or 3 dimensional.
                    indexes[i] = d++;
                }
            } else if (crs2 instanceof VerticalCRS) {
                assert dim == 1;
                indexes[2] = d++;
            } else if (crs2 instanceof TemporalCRS) {
                assert dim == 1;
                indexes[3] = d++;
            } else {
                throw new IllegalArgumentException("Unsupported CRS: " + crs2);
            }
        }
        return indexes;
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
        if (temporalDim != null && verticalDim != null) {
            try {
                crs = CRS.compound(crs2D, verticalDim, temporalDim);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);      // TODO: not appropriate.
            }
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = CommonCRS.Temporal.JAVA.crs();
                final MathTransform trs = CRS.findOperation(realTemporal, temporalDim, null).getMathTransform();
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
                final MathTransform trs = CRS.findOperation(realElevation, verticalDim, null).getMathTransform();
                final double[] coords = new double[2];
                coords[0] = (elevation[0] != null) ? elevation[0] : Double.NEGATIVE_INFINITY;
                coords[1] = (elevation[1] != null) ? elevation[1] : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[2],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }
        } else if (temporalDim != null) {
            try {
                crs = CRS.compound(crs2D,  temporalDim);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);      // TODO: not appropriate.
            }
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = CommonCRS.Temporal.JAVA.crs();
                final MathTransform trs = CRS.findOperation(realTemporal, temporalDim, null).getMathTransform();
                final double[] coords = new double[2];
                coords[0] = (temporal[0] != null) ? temporal[0].getTime() : Double.NEGATIVE_INFINITY;
                coords[1] = (temporal[1] != null) ? temporal[1].getTime() : Double.POSITIVE_INFINITY;
                trs.transform(coords, 0, coords, 0, 2);
                env.setRange(indexes[3],coords[0],coords[1]);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(),ex);
            }


        } else if (verticalDim != null) {
            try {
                crs = CRS.compound(crs2D, verticalDim);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);      // TODO: not appropriate.
            }
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realElevation = CommonCRS.Vertical.ELLIPSOIDAL.crs();
                final MathTransform trs = CRS.findOperation(realElevation, verticalDim, null).getMathTransform();
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
     * @param originalCRS  base CRS, possible multi-dimension
     * @param crs2D  replacement 2D crs
     */
    public static CoordinateReferenceSystem change2DComponent( final CoordinateReferenceSystem originalCRS,
            final CoordinateReferenceSystem crs2D) throws TransformException {
        if (crs2D.getCoordinateSystem().getDimension() != 2) {
            throw new IllegalArgumentException("Expected a 2D CRS");
        }

        final CoordinateReferenceSystem targetCRS;

        if (originalCRS instanceof CompoundCRS) {
            final CompoundCRS ccrs = (CompoundCRS) originalCRS;
            final CoordinateReferenceSystem part2D = CRS.getHorizontalComponent(originalCRS);
            final List<CoordinateReferenceSystem> lst = new ArrayList<>();
            for (CoordinateReferenceSystem c : ccrs.getComponents()) {
                if (c.equals(part2D)) {
                    //replace the 2D part
                    lst.add(crs2D);
                } else {
                    //preserve other axis
                    lst.add(c);
                }
            }
            try {
                targetCRS = CRS.compound(lst.toArray(new CoordinateReferenceSystem[lst.size()]));
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);      // TODO: not appropriate.
            }
        } else if (originalCRS.getCoordinateSystem().getDimension() == 2) {
            //no other axis, just reproject normally
            targetCRS = crs2D;
        } else {
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
        return Envelopes.transform(env, targetCRS);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     * Reproject the envelope.
     */
    public static Envelope setLongitudeFirst(final Envelope env) throws TransformException, FactoryException{
        if(env == null) return env;

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem flipped = setLongitudeFirst(crs);
        return Envelopes.transform(env, flipped);
    }

    /**
     * Try to change a coordinate reference system axis order to place the east axis first.
     *
     * @deprecated Use {@link org.apache.sis.referencing.crs.AbstractCRS#forConvention} instead.
     */
    @Deprecated
    public static CoordinateReferenceSystem setLongitudeFirst(final CoordinateReferenceSystem crs) throws FactoryException{
        if (crs instanceof SingleCRS) {
            final SingleCRS singlecrs = (SingleCRS) crs;
            final CoordinateSystem cs = singlecrs.getCoordinateSystem();
            final int dimension = cs.getDimension();

            if (dimension <= 1) {
                //can't change anything if it's only one axis
                return crs;
            }

            //find the east axis
            int eastAxis = -1;
            for (int i=0; i<dimension; i++) {
                final AxisDirection firstAxis = cs.getAxis(i).getDirection();
                if (firstAxis == AxisDirection.EAST || firstAxis == AxisDirection.WEST) {
                    eastAxis = i;
                    break;
                }
            }
            if (eastAxis == 0) {
                //the crs is already in the correct order or does not have any east axis
                return singlecrs;
            }

            //try to change the crs axis
            final String id = lookupIdentifier(singlecrs, true);
            if (id != null) {
                return AbstractCRS.castOrCopy(CRS.forCode(id)).forConvention(AxesConvention.RIGHT_HANDED);
            } else {
                //TODO how to manage custom crs ? might be a derivedCRS.
                throw new FactoryException("Failed to create flipped axis for crs : " + singlecrs);
            }

        } else if (crs instanceof CompoundCRS) {
            final CompoundCRS compoundcrs = (CompoundCRS) crs;

            final List<CoordinateReferenceSystem> components = compoundcrs.getComponents();
            final int size = components.size();
            final CoordinateReferenceSystem[] parts = new CoordinateReferenceSystem[size];

            //only recreate the crs if one element changed.
            boolean changed = false;
            for (int i=0; i<size; i++) {
                final CoordinateReferenceSystem orig = components.get(i);
                parts[i] = setLongitudeFirst(orig);
                if (!parts[i].equals(orig)) changed = true;
            }
            if (changed) {
                return CRS.compound(parts);
            } else {
                return crs;
            }
        }
        return crs;
    }

    /**
     * Create an affine transform object where (0,0) in the dimension
     * match the top left corner of the envelope.
     * This method assumes that the Y axis of the rectangle is going down.
     * This returns the display to objective transform (rect to env).
     */
    public static AffineTransform toAffine(final Dimension rect, final Envelope env){
        final double minx = env.getMinimum(0);
        final double maxy = env.getMaximum(1);
        final double scaleX = env.getSpan(0)/rect.width;
        final double scaleY = - env.getSpan(1)/rect.height;
        return new AffineTransform(scaleX, 0, 0, scaleY, minx, maxy);
    }

    /**
     * @deprecated replaced by {@link #toTransform(int, org.opengis.referencing.operation.MathTransform, java.util.Map, int)
     */
    @Deprecated
    public static MathTransform toTransform(final MathTransform base, double[] ... values){

        MathTransform result = MathTransforms.passThrough(0, base, values.length);
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
            final MathTransform mask = MathTransforms.passThrough(baseDim+i, axistrs, values.length-i-1);
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

        MathTransform result = MathTransforms.passThrough(firstBaseOrdinate, subTransform, expectedTargetDimension - subTransform.getTargetDimensions() - firstBaseOrdinate);
        for (Integer dim : axisValues.keySet()) {
            final double[] currentAxisValues = axisValues.get(dim);
            final MathTransform1D axistrs;
            if(currentAxisValues.length <= 1) {
                axistrs = (MathTransform1D) MathTransforms.linear(1, (currentAxisValues.length == 0)
                                                                     ? 0 : currentAxisValues[0]);
            } else {
                axistrs = LinearInterpolator1D.create(currentAxisValues);
            }
            final MathTransform mask = MathTransforms.passThrough(dim, axistrs, expectedTargetDimension - dim - 1);
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
     * Decompose CRS and return each sub-crs along with their dimension index.
     *
     * @return Map of index and sub-crs
     */
    public static Map<Integer, CoordinateReferenceSystem> indexedDecompose(CoordinateReferenceSystem crs) {
        final TreeMap<Integer, CoordinateReferenceSystem> result = new TreeMap<>();
        int index = 0;
        CoordinateReferenceSystem crsPart;
        CoordinateSystem csPart;
        final List<SingleCRS> crsParts = getSingleComponents(crs);
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
     *
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
        if (Utilities.equalsApproximately(sourceCRS, destCRS)) {
            destination = new GeneralEnvelope(source);
        }

        // Decompose source and destination CRSs
        final List<SingleCRS> sourceComponents = getSingleComponents(sourceCRS);
        final List<SingleCRS> destComponents = getSingleComponents(destCRS);
        // Store sub-CRSs of destination which have already been used for range transfer.
        final List<CoordinateReferenceSystem> usedCRS = new ArrayList<>(destComponents.size());

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

                if (Utilities.equalsApproximately(srcCurrent, destCurrent)) {
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

                // We found matching CRS, Now we can transfer coordinates.
                if (compatible) {
                    final GeneralEnvelope srcSubEnvelope = source.subEnvelope(srcLowerAxis, srcLowerAxis + srcAxisCount);
                    srcSubEnvelope.setCoordinateReferenceSystem(srcCurrent);
                    try {
                        final Envelope tmp = Envelopes.transform(srcSubEnvelope, destCurrent);
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
     *
     * @param  layerEnvelope The envelope of the source layer. Result envelope will keep this object CRS.
     * @param  filterEnvelope the envelope used as filter.
     * @return An envelope which is the found intersection between the two inputs.
     *         The CRS of the result will be the same as the first input.
     * @throws TransformException if an incompatibility between CRSs is found.
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
            resultEnvelope.intersect(Envelopes.transform(filterEnvelope, inputCRS));
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
                    tmpCRS = org.geotoolkit.referencing.CRS.getCompoundCRS((CompoundCRS) inputCRS, toFind.toArray(new SingleCRS[toFind.size()]));
                }
                final GeneralEnvelope tmpFilter = new GeneralEnvelope(Envelopes.transform(filterEnvelope, tmpCRS));
                final GeneralEnvelope tmpResult = new GeneralEnvelope(Envelopes.transform(resultEnvelope, tmpCRS));
                tmpResult.intersect(tmpFilter);

                /* Re-injection pass. For each component crs of the above computed
                 * intersection, we try to find the same in input envelope, to
                 * replace its values.
                 */
                int tmpOffset = 0;
                for (CoordinateReferenceSystem tmpSubCRS : toFind) {
                    final int srcOffset = org.apache.sis.internal.referencing.AxisDirections.indexOfColinear(
                            inputCRS.getCoordinateSystem(), tmpSubCRS.getCoordinateSystem());
                    if(srcOffset>=0){
                        int tmpDimNumber = tmpSubCRS.getCoordinateSystem().getDimension();
                        final GeneralEnvelope subTmp = tmpResult.subEnvelope(tmpOffset, tmpOffset + tmpDimNumber);
                        resultEnvelope.subEnvelope(srcOffset, srcOffset+tmpResult.getDimension()).setEnvelope(subTmp);
                        break;
                    }
                }

            } else {
                throw new TransformException("An error occured while applying filter : input layer CRS and filter CRS aren't compatible.");
            }
        }
        return resultEnvelope;
    }

    /**
     * Read TFW file and return the content affine transform.
     */
    public static AffineTransform readTransform(Path f) throws IOException, NumberFormatException {
        final String str = IOUtilities.toString(f);
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

    /**
     * Looks up an {@linkplain Identifier identifier}, such as {@code "EPSG:4326"},
     * of the specified object. This method searches in registered factories for an object
     * {@linkplain ComparisonMode#APPROXIMATE approximately equals} to the specified
     * object. If such an object is found, then its first identifier is returned. Otherwise
     * this method returns {@code null}.
     * <p>
     * <strong>Note that this method checks the identifier validity</strong>. If the given object
     * declares explicitly an identifier, then this method will instantiate an object from the
     * authority factory using that identifier and compare it with the given object. If the
     * comparison fails, then this method returns {@code null}. Consequently this method may
     * returns {@code null} even if the given object declares explicitly its identifier. If
     * the declared identifier is wanted unconditionally, use
     * {@link #getIdentifier(IdentifiedObject)} instead.
     *
     * @param  object The object (usually a {@linkplain CoordinateReferenceSystem coordinate
     *         reference system}) whose identifier is to be found, or {@code null}.
     * @param  fullScan If {@code true}, an exhaustive full scan against all registered objects
     *         should be performed (may be slow). Otherwise only a fast lookup based on embedded
     *         identifiers and names will be performed.
     * @return The identifier, or {@code null} if none was found or if the given object was null.
     * @throws FactoryException If an unexpected failure occurred during the search.
     *
     * @see AbstractAuthorityFactory#getIdentifiedObjectFinder(Class)
     * @see IdentifiedObjectFinder#findIdentifier(IdentifiedObject)
     */
    public static String lookupIdentifier(final IdentifiedObject object, final boolean fullScan)
            throws FactoryException {
        if (object == null) {
            return null;
        }
        final IdentifiedObjectFinder f = org.apache.sis.referencing.IdentifiedObjects.newFinder(null);
        f.setSearchDomain(fullScan ? IdentifiedObjectFinder.Domain.ALL_DATASET : IdentifiedObjectFinder.Domain.DECLARATION);
        for (final IdentifiedObject o : f.find(object)) {
            final String i = org.apache.sis.referencing.IdentifiedObjects.toString(
                    org.apache.sis.referencing.IdentifiedObjects.getIdentifier(o, null));
            if (i != null) return i;
        }
        return null;
    }

    /**
     * Test if two envelopes intersects.
     * This method will transform envelopes if needed.
     *
     * @return true if envelopes intersect
     */
    public static boolean intersects(Envelope env1, Envelope env2) {
        final CoordinateReferenceSystem crs1 = env1.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2 = env2.getCoordinateReferenceSystem();

        //try to find a compatible crs
        final CoordinateReferenceSystem crs = CRS.suggestCommonTarget(null, crs1,crs2);
        if (crs!=null) {
            try {
                final Envelope penv1 = Envelopes.transform(env1, crs);
                final Envelope penv2 = Envelopes.transform(env2, crs);
                return GeneralEnvelope.castOrCopy(penv1).intersects(penv2);
            } catch(Exception ex) {/*do nothing*/}
        }

        //try both way intersection
        cas1:
        try {
            final Envelope penv2 = Envelopes.transform(env2, crs1);
            if (GeneralEnvelope.castOrCopy(penv2).isEmpty()) break cas1;
            return GeneralEnvelope.castOrCopy(env1).intersects(penv2);
        } catch(Exception ex) {/*do nothing*/}

        cas2:
        try {
            final Envelope penv1 = Envelopes.transform(env1, crs2);
            if (GeneralEnvelope.castOrCopy(penv1).isEmpty()) break cas2;
            return GeneralEnvelope.castOrCopy(penv1).intersects(env2);
        } catch(Exception ex) {/*do nothing*/}

        /*
         * TODO: an exception means that a value exist, but can not be computed because the calculation
         *       do not converge. The value that we have not been able to calculate could be inside the
         *       other envelope. Concequently returning 'false' is not okay - we should propagate the
         *       exception instead.
         */
        return false;
    }

    /**
     * Try to extract a geographic bounding box from given resource.
     * @implNote : We'll try to acquire metadata from the resource, and making
     * an union of all available geographic boxes found in it.
     * @param r The resource to analyze.
     * @return A geographic envelope if we've found any in the resource metadata.
     * Nothing otherwise.
     *
     * @throws DataStoreException If accessing the resource metadata failed.
     */
    public static Optional<? extends GeographicBoundingBox> findGeographicBBox(final Resource r) throws DataStoreException {
        return r.getMetadata().getIdentificationInfo().stream()
                .flatMap(i -> i.getExtents().stream())
                .map(Extents::getGeographicBoundingBox)
                .map(DefaultGeographicBoundingBox::new )
                .reduce((b1, b2) -> {b1.add(b2); return b1;});
    }

    /**
     * Returns the orthodromic distance between two geographic coordinates.
     * The orthodromic distance is the shortest distance between two points
     * on a sphere's surface. The orthodromic path is always on a great circle.
     *
     * @param  λ1  longitude of first point (in decimal degrees).
     * @param  φ1  latitude of first point (in decimal degrees).
     * @param  λ2  longitude of second point (in decimal degrees).
     * @param  φ2  latitude of second point (in decimal degrees).
     * @return the orthodromic distance (in the units of this ellipsoid's axis).
     */
    public static double orthodromicDistance(final double radius, double λ1, double φ1, double λ2, double φ2) {
        φ1 = toRadians(φ1);
        φ2 = toRadians(φ2);
        final double dx = toRadians(abs(λ2-λ1) % 360);
        double rho = sin(φ1)*sin(φ2) + cos(φ1)*cos(φ2)*cos(dx);
        assert abs(rho) < 1.0000001 : rho;
        if (rho > +1) rho = +1;                         // Catch rounding error.
        if (rho < -1) rho = -1;                         // Catch rounding error.
        return acos(rho) * radius;
    }

    /** Authalic sphere radius in metres. */
    public static final double EARTH_RADIUS = 6371007;
}
