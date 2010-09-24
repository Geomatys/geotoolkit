/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.Collections;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.DefiningConversion;


/**
 * Applies heuristic rules for converting (if possible) a grid geometry with irregular axes
 * to a grid geometry with regular axes. This class handles only two-dimensional grids.
 * <p>
 * <b>Use case:</b> NetCDF <cite>Coriolis</cite> data were computed on a regular grid in the
 * Mercator projection. However when the file is saved, the Mercator coordinates are converted
 * to geographic coordinates by the data provider, resulting in an irregular grid. This
 * {@code IrregularAxesConverter} class checks if it can revert the coordinates back to Mercator.
 * <p>
 * Current implementation checks only Mercator projection. Future version may add more checks.
 * <p>
 * Current implementation assumes that only the <var>y</var> axis is irregular, and that its
 * value does not depend on <var>x</var> values. This assumption is valid only for the Mercator
 * projection.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public final class IrregularAxesConverter {
    /**
     * The operation methods to try.
     */
    private static final String[] METHODS = {"Mercator_1SP"};

    /**
     * A tolerance factor, relative to the increment.
     */
    private final double tolerance;

    /**
     * The factory to use for creating math transform.
     */
    private final MathTransformFactory mtFactory;

    /**
     * The factory to use for creating CRS objects.
     */
    private final CRSFactory crsFactory;

    /**
     * The new <var>x</var> and <var>y</var> axis ordinate values.
     * Those fields are valid only if {@link #canConvert} returned a non-null value.
     */
    public double[] nx, ny;

    /**
     * Creates a new instance.
     *
     * @param tolerance A tolerance factor, relative to the increment.
     * @param hints An optional set of hints for determining the factories to use.
     */
    public IrregularAxesConverter(final double tolerance, final Hints hints) {
        this.tolerance  = tolerance;
        this.mtFactory  = FactoryFinder.getMathTransformFactory(hints);
        this.crsFactory = FactoryFinder.getCRSFactory(hints);
    }

    /**
     * Creates a projected CRS for the given geographic CRS and operation method.
     * Current implementation creates a projection using the default parameters only.
     *
     * @param  baseCRS The base geographic CRS.
     * @param  method  The operation method.
     * @return The projected CRS.
     * @throws FactoryException If an error occurred while creating the projected CRS.
     */
    private ProjectedCRS createProjectedCRS(final GeographicCRS baseCRS, final String method)
            throws FactoryException
    {
        final Ellipsoid ellipsoid = baseCRS.getDatum().getEllipsoid();
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters(method);
        parameters.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
        parameters.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
        final MathTransform projection = mtFactory.createParameterizedTransform(parameters);
        return crsFactory.createProjectedCRS(Collections.singletonMap(ProjectedCRS.NAME_KEY,
                method + " (" + baseCRS.getName().getCode() + ')'), baseCRS,
                new DefiningConversion(Collections.singletonMap(ProjectedCRS.NAME_KEY, method),
                mtFactory.getLastMethodUsed(), projection), DefaultCartesianCS.PROJECTED);
    }

    /**
     * Tests if the given axes can be made regular.
     *
     * @param  sourceCRS The two-dimensional CRS of the given <var>x</var> and <var>y</var> axes.
     * @param  x The <var>x</var> axis.
     * @param  y The <var>y</var> axis.
     * @return {@code null} if the axis can not be made regular, or the target CRS otherwise.
     *         In the later case, ({@link #nx},{@link #ny}) contain the new ordinate values.
     */
    public ProjectedCRS canConvert(final GeographicCRS sourceCRS,
                                   final DiscreteCoordinateSystemAxis x,
                                   final DiscreteCoordinateSystemAxis y)
    {
        for (final String method : METHODS) {
            try {
                final ProjectedCRS targetCRS = createProjectedCRS(sourceCRS, method);
                final MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
                if ((ny = canConvert(tr, 1, y, x)) != null &&
                    (nx = canConvert(tr, 0, x, y)) != null)
                {
                    return targetCRS;
                }
            } catch (FactoryException e) { // TODO: multi-catch with Java 7.
                Logging.recoverableException(IrregularAxesConverter.class, "canConvert", e);
            } catch (TransformException e) {
                Logging.recoverableException(IrregularAxesConverter.class, "canConvert", e);
            }
        }
        return null;
    }

    /**
     * Transforms the given ordinate using the given transform, and check if the result
     * is regular. If the result is regular, returns the new axis ordinate values.
     *
     * {@note In this method, the term <cite>y axis</cite> designates the axis for which
     * we will transform the ordinates, and the term <cite>x axis</cite> designates the
     * independent axis.}
     *
     * @param  tr The transform to use.
     * @param  yOrdinate 0 for transforming the <var>x</var> axis,
     *         or 1 for transforming the <var>y</var> axis.
     * @param  yAxis The axis having the ordinate values to transform.
     * @param  xAxis The axis having the independent ordinate values.
     * @return The new axis ordinate values if the result is regular,
     *         or {@code null} if the result is irregular.
     * @throws TransformException If an error occurred while transforming the coordinates.
     */
    private double[] canConvert(final MathTransform tr, final int yOrdinate,
            final DiscreteCoordinateSystemAxis yAxis,
            final DiscreteCoordinateSystemAxis xAxis)
            throws TransformException
    {
        /*
         * Fill a temporary array with the ordinate values of the irregular axis. The
         * ordinate of the other (assumed independent) axis is fixed to the center of
         * the axis domain range.
         */
        final int      numPts  = yAxis.length();
        final double[] coords  = new double[numPts * 2];
        final double   centerX = valueOf(xAxis.getOrdinateAt(xAxis.length() / 2));
        for (int i=0; i<numPts; i++) {
            final int i2 = (i << 1) | yOrdinate;
            coords[i2] = valueOf(yAxis.getOrdinateAt(i));
            coords[i2 ^ 1] = centerX;
        }
        tr.transform(coords, 0, coords, 0, numPts);
        /*
         * Check if the ordinates values are regularly spaced among both dimensions.
         * The ordinate values can be in increasing or decreasing order.
         */
        final double tx = ((coords[coords.length - 2] - coords[0]) / (numPts-1)) * tolerance;
        final double ty = ((coords[coords.length - 1] - coords[1]) / (numPts-1)) * tolerance;
        for (int i=4; i<coords.length; i+=2) {
            if (!(Math.abs(coords[i  ] - 2*coords[i-2] + coords[i-4]) <= tx) ||
                !(Math.abs(coords[i+1] - 2*coords[i-1] + coords[i-3]) <= ty))
            {
                // Ordinate values are irregular.
                return null;
            }
        }
        /*
         * Axis ordinate values are regular. Copy them in a new array.
         */
        final double[] ordinates = new double[numPts];
        for (int i=0; i<numPts; i++) {
            ordinates[i] = coords[(i << 1) | yOrdinate];
        }
        return ordinates;
    }

    /**
     * Returns the given ordinate value as a primitive type {@code double} if the conversion
     * is allowed, or returns {@code NaN} otherwise. The NaN value will cause the axis to be
     * considered irregular.
     */
    private static double valueOf(final Comparable<?> ordinate) {
        return (ordinate instanceof Number) ? ((Number) ordinate).doubleValue() : Double.NaN;
    }
}
