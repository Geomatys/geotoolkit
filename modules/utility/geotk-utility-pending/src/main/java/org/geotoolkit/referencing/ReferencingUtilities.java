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
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.operation.projection.Mercator;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.LinearTransform1D;
import org.geotoolkit.referencing.operation.transform.PassThroughTransform;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
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
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

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

    private static boolean isWrapAroundCompatible(MathTransform trs){
        if(trs instanceof ConcatenatedTransform){
            final ConcatenatedTransform ct = (ConcatenatedTransform) trs;
            for(MathTransform step : ct.getSteps()){
                if(!isWrapAroundCompatible(step)) return false;
            }
            return true;
        }
        return trs instanceof LinearTransform || trs instanceof Mercator;
    }

    public static Envelope wrapNormalize(Envelope env, DirectPosition[] warp) {
        if(warp==null){
            return env;
        }

        //TODO we assume the warp is on a along an axis of the coordinate system.
        final DirectPosition p0 = warp[0];
        final DirectPosition p1 = warp[1];
        for(int i=0,n=p0.getDimension();i<n;i++){
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
            //we tryed...
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
                    //we tryed...
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

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = DefaultTemporalCRS.JAVA;
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
                final CoordinateReferenceSystem realElevation = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
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
            crs = new DefaultCompoundCRS(crs2D.getName().getCode()+"/"+temporalDim.getName().getCode(),
                    crs2D,  temporalDim);
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realTemporal = DefaultTemporalCRS.JAVA;
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
            crs = new DefaultCompoundCRS(crs2D.getName().getCode()+"/"+verticalDim.getName().getCode(),
                    crs2D, verticalDim);
            env = new GeneralEnvelope(crs);

            int[] indexes = findDimensionIndexes(crs);
            env.setRange(indexes[0], bounds.getMinX(), bounds.getMaxX());
            env.setRange(indexes[1], bounds.getMinY(), bounds.getMaxY());

            try {
                final CoordinateReferenceSystem realElevation = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
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

    public static MathTransform toTransform(final MathTransform base, double[] ... values){

        MathTransform result = PassThroughTransform.create(0, base, values.length);
        final int baseDim = base.getSourceDimensions();
        for(int i=0; i<values.length; i++){
            final double[] array = values[i];
            final MathTransform1D axistrs;
            if(array.length == 0){
                axistrs = LinearTransform1D.create(1, 0);
            }else if(array.length == 1){
                axistrs = LinearTransform1D.create(1, array[0]);
            }else{
                axistrs = SequenceValueTransform1D.create(array);
            }
            final MathTransform mask = PassThroughTransform.create(baseDim+i, axistrs, values.length-i-1);
            result = ConcatenatedTransform.create(result, mask);
        }

        return result;
    }

    /**
     * Recursively explor given crs, and return a list of distinct unary CRS.
     * @param crs
     * @return List<CoordinateReferenceSystem>
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
}
